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
 * Created by a.serebrennikova
 */
@FixMethodOrder(MethodSorters.JVM)
public class NonAuthorizedServletTest extends JerseyTest {
    private SessionFactory sessionFactory;

    @Override
    protected Application configure() {
        final Context context = new Context();
        context.put(AccountService.class, new AccountServiceImpl());

        final ResourceConfig config = new ResourceConfig(Users.class, Sessions.class);
        final HttpSession session = mock(HttpSession.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);

        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(context);
                bind(session).to(HttpSession.class);
                bind(request).to(HttpServletRequest.class);
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

        try (Session testSession = sessionFactory.openSession()) {
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
    public void testSignUpUserExistsFail() {
        final UserDataSet newUser = new UserDataSet();
        newUser.setEmail("admin@admin");
        newUser.setLogin("admin");
        newUser.setPassword("123");
        final Response actualResponse = target("user").request().put(Entity.json(newUser));
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testSignUp() {
        final UserDataSet newUser = new UserDataSet();
        newUser.setEmail("test@test");
        newUser.setLogin("test");
        newUser.setPassword("testtest");
        long newUserId;
        try (Session testSession = sessionFactory.openSession()) {
            Criteria criteria = testSession
                    .createCriteria(UserDataSet.class)
                    .setProjection(Projections.max("id"));
            newUserId = (Long)criteria.uniqueResult() + 1;

            final String actualJson = target("user").request().put(Entity.json(newUser), String.class);
            final String expectedJson = "{ \"id\": \"" + newUserId + "\" }";
            assertEquals(expectedJson, actualJson);
        }

        try (Session testSession = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(testSession);
            UserDataSet user = dao.getUser(newUserId);
            assertEquals("test@test", user.getEmail());
            assertEquals("test", user.getLogin());
            assertEquals("testtest", user.getPassword());
        }
    }

    @Test
    public void testAuthorized() {
        final Response actualResponse = target("session").request().get();
        assertEquals(401, actualResponse.getStatus());
    }

    @Test
    public void testSignInWrongUserFail() {
        final UserDataSet user = new UserDataSet();
        user.setLogin("admin");
        user.setPassword("12345");
        user.setEmail("");
        final Response actualResponse = target("session").request().put(Entity.json(user));
        assertEquals(400, actualResponse.getStatus());
    }

    @Test
    public void testSignIn() {
        final UserDataSet user = new UserDataSet();
        user.setLogin("admin");
        user.setPassword("admin");
        user.setEmail("");
        final String actualJson = target("session").request().put(Entity.json(user), String.class);
        final String expectedJson = "{ \"id\": \"1\" }";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testUpdateForeignUserFail() {
        final UserDataSet updatedUser = new UserDataSet();
        updatedUser.setLogin("agmin");
        updatedUser.setPassword("12345");
        updatedUser.setEmail("adm@adm");
        final Response actualResponse = target("user").path("1").request().post(Entity.json(updatedUser));
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testDeleteForeignUserFail() {
        final Response actualResponse = target("user").path("1").request().delete();
        assertEquals(403, actualResponse.getStatus());
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
