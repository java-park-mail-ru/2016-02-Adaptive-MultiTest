package accountService;

import accountService.dao.UserDataSetDAO;
import base.AccountService;
import base.dataSets.UserDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author a.serebrennikova
 */
public class AccountServiceImpl implements AccountService{
    private final Map<String, UserDataSet> sessions = new HashMap<>();

    private SessionFactory sessionFactory;

    public AccountServiceImpl() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/MultiTest");
        configuration.setProperty("hibernate.connection.username", "mtestuser");
        configuration.setProperty("hibernate.connection.password", "secret");
        configuration.setProperty("hibernate.show_sql", "true");
        //configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        sessionFactory = createSessionFactory(configuration);
    }

    @Override
    public List<UserDataSet> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getAllUsers();
        }
    }

    @Override
    public long addUser(UserDataSet user) {
        try ( Session session = sessionFactory.openSession() ) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            if (dao.getUserByLogin(user.getLogin()) != null || dao.getUserByEmail(user.getEmail()) != null) {
                return -1;
            } else {
                dao.addUser(user);
                return dao.getUserByLogin(user.getLogin()).getId();
            }
        }
    }

    @Override
    public UserDataSet getUser(long userId) {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getUser(userId);
        }
    }

    @Override
    public UserDataSet getUserByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getUserByLogin(login);
        }
    }

    @Override
    public UserDataSet getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getUserByEmail(email);
        }
    }

    @Override
    public long updateUser(UserDataSet updatedUser, long userId) {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            dao.updateUser(updatedUser, userId);
            return userId;
        }
    }

    @Override
    public void deleteUser(long userId) {
        try (Session session = sessionFactory.openSession()) {
            UserDataSetDAO dao = new UserDataSetDAO(session);
            dao.deleteUser(userId);
        }
    }

    @Override
    public void addSession(String sessionId, UserDataSet user) { sessions.put(sessionId, user); }

    @Override
    public boolean isAuthenticated(String sessionId) { return sessions.containsKey(sessionId); }

    @Override
    public UserDataSet getUserBySession(String sessionId) { return sessions.get(sessionId); }

    @Override
    public boolean isValidUser(UserDataSet user) {
        UserDataSet actualUser = getUserByLogin(user.getLogin());
        return (actualUser != null && actualUser.getPassword().equals(user.getPassword()));
    }

    @Override
    public void deleteSession(String sessionId) { sessions.remove(sessionId); }

    public void shutdown() {
        sessionFactory.close();
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}
