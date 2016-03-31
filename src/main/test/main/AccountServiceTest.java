package main;

import accountService.AccountServiceImpl;
import accountService.dao.UserDataSetDAO;
import base.dataSets.UserDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Sasha on 29.03.16.
 */
@SuppressWarnings("DefaultFileTemplate")
@FixMethodOrder(MethodSorters.JVM)
public class AccountServiceTest {
    private AccountServiceImpl accountService;
    private SessionFactory sessionFactory;
    private UserDataSet admin;
    private UserDataSet guest;

    @Before
    public void initialize(){
        accountService = new AccountServiceImpl();

        final Configuration configuration = Config.getHibernateConfiguration();
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        sessionFactory = createSessionFactory(configuration);

        try(Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            admin = new UserDataSet();
            admin.setLogin("admin");
            admin.setEmail("admin@admin");
            admin.setPassword("admin");
            guest = new UserDataSet();
            guest.setLogin("guest");
            guest.setEmail("guest@guest");
            guest.setPassword("12345");
            dao.addUser(admin);
            dao.addUser(guest);

            dao.addUser(admin);
            dao.addUser(guest);
        }
    }

    @Test
    public void testGetAllUsers() {
        final List<UserDataSet> actualUsers= accountService.getAllUsers();
        final UserDataSet first = actualUsers.get(0);
        final UserDataSet second = actualUsers.get(1);
        assertEquals(admin, first);
        assertEquals(guest, second);
    }

    @Test
    public void testGetUser() {
        final UserDataSet user = accountService.getUser(1);
        assertEquals(admin, user);
    }

    @Test
    public void testGetUserByLogin() {
        final UserDataSet user = accountService.getUserByLogin("admin");
        assertEquals(admin, user);
    }

    @Test
    public void testGetUserByEmail() {
        final UserDataSet user = accountService.getUserByEmail("admin@admin");
        assertEquals(admin, user);
    }

    @Test
    public void testAddUserExistsFail() {
        final UserDataSet newUser = new UserDataSet();
        newUser.setLogin("guest");
        newUser.setEmail("guest@guest");
        newUser.setPassword("testtest");
        final long newUserId = accountService.addUser(newUser);

        assertEquals(-1, newUserId);
    }

    @Test
    public void testAddUser() {
        final UserDataSet newUser = new UserDataSet();
        newUser.setLogin("test");
        newUser.setEmail("test@test");
        newUser.setPassword("testtest");
        final long newUserId = accountService.addUser(newUser);

        try (Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet user = dao.getUser(newUserId);
            assertEquals(newUser, user);
        }
    }

    @Test
    public void testUpdateUser() {
        final UserDataSet updatedUser = new UserDataSet();
        updatedUser.setId(2);
        updatedUser.setLogin("guestee");
        updatedUser.setEmail("guest@guestee");
        updatedUser.setPassword("1234567");
        final long updatedUserId = accountService.updateUser(updatedUser, 2);

        try (Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet user = dao.getUser(updatedUserId);
            assertEquals(updatedUser, user);
        }
    }

    @Test
    public void testDeleteUser() {
        accountService.deleteUser(2);

        try (Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet user = dao.getUser(2);
            assertEquals(null, user);
        }
    }

    @Test
    public void testAddSession() {
        accountService.addSession("session", guest);
        final Map<String, UserDataSet> sessions = accountService.getSessions();
        assertTrue(sessions.containsValue(guest));
    }

    @Test
    public void testIsAuthenticatedNonAuthorized() {
        assertFalse(accountService.isAuthenticated("session"));
    }

    @Test
    public void testIsAuthenticatedAuthorized() {
        accountService.addSession("session", guest);
        assertTrue(accountService.isAuthenticated("session"));
    }

    @Test
    public void testGetUserBySession() {
        accountService.addSession("session", guest);
        final UserDataSet user = accountService.getUserBySession("session");
        assertEquals(guest, user);
    }

    @Test
    public void testIsValidUserInvalidLogin() {
        final UserDataSet invalidUser = new UserDataSet();
        invalidUser.setLogin("gue");
        invalidUser.setPassword("12345");
        assertFalse(accountService.isValidUser(invalidUser));
    }

    @Test
    public void testIsValidUserInvalidPassword() {
        final UserDataSet invalidUser = new UserDataSet();
        invalidUser.setLogin("guest");
        invalidUser.setPassword("1234567");
        assertFalse(accountService.isValidUser(invalidUser));
    }

    @Test
    public void testIsValidUserValid() {
        final UserDataSet validUser = new UserDataSet();
        validUser.setLogin("guest");
        validUser.setPassword("12345");
        assertTrue(accountService.isValidUser(validUser));
    }

    @Test
    public void testDeleteSession() {
        accountService.addSession("session", guest);
        accountService.deleteSession("session");
        final Map<String, UserDataSet> sessions = accountService.getSessions();
        assertFalse(sessions.containsValue(guest));
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}
