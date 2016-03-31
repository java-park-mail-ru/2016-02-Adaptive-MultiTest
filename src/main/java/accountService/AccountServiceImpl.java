package accountService;

import accountService.dao.UserDataSetDAO;
import base.AccountService;
import base.dataSets.UserDataSet;
import main.Config;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author a.serebrennikova
 */
public class AccountServiceImpl implements AccountService{
    private final Map<String, UserDataSet> sessions = new HashMap<>();

    private final SessionFactory sessionFactory;

    public AccountServiceImpl() {
        final Configuration configuration = Config.getHibernateConfiguration();

        sessionFactory = createSessionFactory(configuration);
    }

    @Override
    public List<UserDataSet> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getAllUsers();
        }
    }

    @Override
    public long addUser(UserDataSet user) {
        try ( Session session = sessionFactory.openSession() ) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
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
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getUser(userId);
        }
    }

    @Override
    public UserDataSet getUserByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getUserByLogin(login);
        }
    }

    @Override
    public UserDataSet getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            return dao.getUserByEmail(email);
        }
    }

    @Override
    public long updateUser(UserDataSet updatedUser, long userId) {
        try (Session session = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
            dao.updateUser(updatedUser, userId);
            return userId;
        }
    }

    @Override
    public void deleteUser(long userId) {
        try (Session session = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(session);
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
        final UserDataSet actualUser = getUserByLogin(user.getLogin());
        return (actualUser != null && actualUser.getPassword().equals(user.getPassword()));
    }

    @Override
    public void deleteSession(String sessionId) { sessions.remove(sessionId); }

    public Map<String, UserDataSet> getSessions() { return sessions; }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}
