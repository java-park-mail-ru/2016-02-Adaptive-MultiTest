package frontend.rest;

import accountService.AccountServiceImpl;
import accountService.dao.UserDataSetDAO;
import base.AccountService;
import base.dataSets.UserDataSet;
import helpers.Config;
import helpers.Status;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.*;

import static org.mockito.Mockito.*;

import javax.servlet.http.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import helpers.Context;
import org.junit.runners.MethodSorters;
import testHelpers.DBFiller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
/**
 * Created by Sasha on 28.03.16.
 */
@SuppressWarnings("DefaultFileTemplate")
@FixMethodOrder(MethodSorters.JVM)
public class AuthorizedServletTest extends JerseyTest {
    private static SessionFactory sessionFactory;
    private static UserDataSet guest;
    private static String dbName;

    @BeforeClass
    public static void fillDB() throws IOException {
        final String cfgPath = new File("").getAbsolutePath() + "/cfg/";
        final Properties dbProperties = new Properties();
        try {
            final FileInputStream fis = new FileInputStream(cfgPath + "db.properties");
            dbProperties.load(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        dbName = dbProperties.getProperty("test_db.name");
        final Configuration configuration = Config.getHibernateConfiguration(dbName, true);
        sessionFactory = createSessionFactory(configuration);
        DBFiller.fillDB(sessionFactory);

        guest = new UserDataSet();
        guest.setLogin("guest");
        guest.setEmail("guest@guest");
        guest.setPassword("12345");
    }

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    @Override
    protected Application configure() {
        final Context context = new Context();
        context.put(AccountService.class, new AccountServiceImpl(dbName));

        final ResourceConfig config = new ResourceConfig(Users.class, Sessions.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpSession session = mock(HttpSession.class);

        //noinspection AnonymousInnerClassMayBeStatic
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
    public void guestSignIn() {
        target("session").request().put(Entity.json(guest));
    }

    @Test
    public void testGetAllUsers() {
        final String actualJson = target("user").request().get(String.class);
        final String expectedJson = "[{\"email\":\"admin@admin\",\"id\":1,\"login\":\"admin\",\"password\":\"admin\",\"score\":0}," +
                "{\"email\":\"guest@guest\",\"id\":2,\"login\":\"guest\",\"password\":\"12345\",\"score\":0}]";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetNonExistentUserFail() {
        final Response actualResponse = target("user").path("-1").request().get();
        assertEquals(Status.FORBIDDEN, actualResponse.getStatus());
    }

    @Test
    public void testGetGuestUser() {
        final String actualJson = target("user").path("2").request().get(String.class);
        final String expectedJson = "{\"email\":\"guest@guest\",\"id\":2,\"login\":\"guest\",\"password\":\"12345\",\"score\":0}";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testAuthorized() {
        final String actualJson = target("session").request().get(String.class);
        final String expectedJson = "{ \"id\": \"2\", \"login\": \"guest\" }";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testUpdateForeignUserFail() {
        final UserDataSet updatedUser = new UserDataSet();
        updatedUser.setEmail("adm@adm");
        updatedUser.setLogin("adm");
        updatedUser.setPassword("123");
        final Response actualResponse = target("user").path("1").request().post(Entity.json(updatedUser));
        assertEquals(Status.FORBIDDEN, actualResponse.getStatus());
    }

    @Test
    public void testUpdateEmailLoginExistsFail() {
        final UserDataSet updatedUser = new UserDataSet();
        updatedUser.setEmail("admin@admin");
        updatedUser.setLogin("admin");
        updatedUser.setPassword("1234");
        final String actualJson = target("user").path("2").request().post(Entity.json(updatedUser), String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);

        try(Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet user = dao.getUser(2);
            assertEquals("guest@guest", user.getEmail());
            assertEquals("guest", user.getLogin());
            assertEquals("1234", user.getPassword());
        }

        guest.setPassword("1234");
    }

    @Test
    public void testUpdateUser() {
        final UserDataSet updatedUser = new UserDataSet();
        updatedUser.setEmail("g@g");
        updatedUser.setLogin("gue");
        updatedUser.setPassword("123");

        final String actualJson = target("user").path("2").request().post(Entity.json(updatedUser), String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);

        try (Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet user = dao.getUser(2);
            assertEquals("g@g", user.getEmail());
            assertEquals("gue", user.getLogin());
            assertEquals("123", user.getPassword());
        }

        guest.setEmail("g@g");
        guest.setLogin("gue");
        guest.setPassword("123");
    }

    @Test
    public void testLogOut() {
        final String actualJson = target("session").request().delete(String.class);
        final String expectedJson = "{}";
        assertEquals(expectedJson, actualJson);

        final Response actualResponse = target("session").request().get();
        assertEquals(Status.UNAUTHORIZED, actualResponse.getStatus());
    }

    @Test
    public void testDeleteForeignUserFail() {
        final Response actualResponse = target("user").path("1").request().delete();
        assertEquals(Status.FORBIDDEN, actualResponse.getStatus());
    }

    @Test
    public void testDeleteUser() {
        final String actualJson = target("user").path("2").request().delete(String.class);
        final String expectedJson = "{ \"id\": \"2\" }";
        assertEquals(expectedJson, actualJson);

        try(Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet user = dao.getUser(2);
            assertEquals(null, user);
        }
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}