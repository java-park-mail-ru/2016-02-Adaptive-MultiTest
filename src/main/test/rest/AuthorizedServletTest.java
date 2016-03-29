package rest;

import accountService.AccountServiceImpl;
import accountService.dao.UserDataSetDAO;
import base.AccountService;
import base.dataSets.UserDataSet;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Projections;
import org.hibernate.service.ServiceRegistry;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import static org.mockito.Mockito.*;

import javax.servlet.http.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import main.Context;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
/**
 * Created by Sasha on 28.03.16.
 */
@FixMethodOrder(MethodSorters.JVM)
public class AuthorizedServletTest extends JerseyTest {
    SessionFactory sessionFactory;

    @Override
    protected Application configure() {
        final Context context = new Context();
        context.put(AccountService.class, new AccountServiceImpl());

        final ResourceConfig config = new ResourceConfig(Users.class, Sessions.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpSession session = mock(HttpSession.class);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(context);
                bind(request).to(HttpServletRequest.class);
                bind(session).to(HttpSession.class);
                when(request.getSession()).thenReturn(session);
                when(session.getId()).thenReturn("session");
            }
        });

        return config;
    }

    @Before
    public void initialize() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/MultiTest");
        configuration.setProperty("hibernate.connection.username", "mtestuser");
        configuration.setProperty("hibernate.connection.password", "secret");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        sessionFactory = createSessionFactory(configuration);

        try(Session testSession = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(testSession);
            UserDataSet admin = new UserDataSet();
            admin.setLogin("admin");
            admin.setEmail("admin@admin");
            admin.setPassword("admin");
            UserDataSet guest = new UserDataSet();
            guest.setLogin("guest");
            guest.setEmail("guest@guest");
            guest.setPassword("12345");
            dao.addUser(admin);
            dao.addUser(guest);

            target("session").request().put(Entity.json(guest));
        }
    }

    @Test
    public void testGetAllUsers() {
        final String actualJson = target("user").request().get(String.class);
        final String expectedJson = "[{\"email\":\"admin@admin\",\"id\":1,\"login\":\"admin\",\"password\":\"admin\"}," +
                "{\"email\":\"guest@guest\",\"id\":2,\"login\":\"guest\",\"password\":\"12345\"}]";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetNonExistentUserFail() {
        final Response actualResponse = target("user").path("-1").request().get();
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testGetAdminUser() {
        final String actualJson = target("user").path("1").request().get(String.class);
        final String expectedJson = "{ \"id\": \"1\",\"login\": \"admin\",\"email\": \"admin@admin\" }";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testAuthorized() {
        final String actualJson = target("session").request().get(String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testUpdateForeignUserFail() {
        UserDataSet updatedUser = new UserDataSet();
        updatedUser.setEmail("adm@adm");
        updatedUser.setLogin("adm");
        updatedUser.setPassword("123");
        final Response actualResponse = target("user").path("1").request().post(Entity.json(updatedUser));
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testUpdateUserEmailLoginExistsFail() {
        UserDataSet updatedUser = new UserDataSet();
        updatedUser.setEmail("admin@admin");
        updatedUser.setLogin("admin");
        updatedUser.setPassword("1234");
        final String actualJson = target("user").path("2").request().post(Entity.json(updatedUser), String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);

        try(Session testSession = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(testSession);
            UserDataSet user = dao.getUser(2);
            assertEquals("guest@guest", user.getEmail());
            assertEquals("guest", user.getLogin());
            assertEquals("1234", user.getPassword());
        }
    }

    @Test
    public void testUpdateUser() {
        UserDataSet updatedUser = new UserDataSet();
        updatedUser.setEmail("g@g");
        updatedUser.setLogin("gue");
        updatedUser.setPassword("123");

        final String actualJson = target("user").path("2").request().post(Entity.json(updatedUser), String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);

        try (Session testSession = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(testSession);
            UserDataSet user = dao.getUser(2);
            assertEquals("g@g", user.getEmail());
            assertEquals("gue", user.getLogin());
            assertEquals("123", user.getPassword());
        }
    }

    @Test
    public void testLogOut() {
        final String actualJson = target("session").request().delete(String.class);
        final String expectedJson = "{}";
        assertEquals(expectedJson, actualJson);

        final Response actualResponse = target("session").request().get();
        assertEquals(401, actualResponse.getStatus());
    }

    @Test
    public void testDeleteForeignUserFail() {
        final Response actualResponse = target("user").path("1").request().delete();
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testDeleteUser() {
        final String actualJson = target("user").path("2").request().delete(String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);

        try(Session testSession = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(testSession);
            UserDataSet user = dao.getUser(2);
            assertEquals(null, user);
        }
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
