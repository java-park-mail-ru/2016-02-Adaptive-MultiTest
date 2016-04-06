package accountService;

import accountService.dao.UserDataSetDAO;
import base.AccountService;
import base.dataSets.UserDataSet;
import main.Config;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author a.serebrennikova
 */
public class AccountServiceImpl implements AccountService{
    private final Map<String, UserDataSet> sessions = new HashMap<>();

    private final SessionFactory sessionFactory;

    public AccountServiceImpl(String dbName) {
        final Configuration configuration = Config.getHibernateConfiguration(dbName, false);

        sessionFactory = createSessionFactory(configuration);
    }

    @Nullable
    @Override
    public List<UserDataSet> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            try{
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                return dao.getAllUsers();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public long addUser(UserDataSet user) {
        try ( Session session = sessionFactory.openSession() ) {
            if (getUserByEmail(user.getEmail()) != null || getUserByLogin(user.getLogin()) != null) return -1;
            final Transaction trx = session.beginTransaction();
            try {
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                dao.addUser(user);
                trx.commit();
                return user.getId();
            } catch (HibernateException e) {
                e.printStackTrace();
                trx.rollback();
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public UserDataSet getUser(long userId) {
        final UserDataSet idUser;
        try (Session session = sessionFactory.openSession()) {
            try {
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                idUser = dao.getUser(userId);
            } catch (HibernateException e) {
                e.printStackTrace();
                return null;
            }
        }
        return idUser;
    }

    @Nullable
    @Override
    public UserDataSet getUserByLogin(String login) {
        final UserDataSet loginUser;
        try (Session session = sessionFactory.openSession()) {
            try {
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                loginUser = dao.getUserByLogin(login);
            } catch (HibernateException e) {
                e.printStackTrace();
                return null;
            }
        }
        return loginUser;
    }

    @Nullable
    @Override
    public UserDataSet getUserByEmail(String email) {
        final UserDataSet emailUser;
        try (Session session = sessionFactory.openSession()) {
            try {
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                emailUser = dao.getUserByEmail(email);
            } catch (HibernateException e) {
                e.printStackTrace();
                return null;
            }
        }
        return emailUser;
    }

    @Override
    public void updateUser(UserDataSet updatedUser, long userId) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction trx = session.beginTransaction();
            try {
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                dao.updateUser(updatedUser, userId);
                trx.commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                trx.rollback();
            }
        }
    }

    @Override
    public void deleteUser(long userId) {
        try (Session session = sessionFactory.openSession()) {
            final Transaction trx = session.beginTransaction();
            try {
                final UserDataSetDAO dao = new UserDataSetDAO(session);
                dao.deleteUser(userId);
                trx.commit();
            } catch (HibernateException e) {
                e.printStackTrace();
                trx.rollback();
            }
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

    @SuppressWarnings("unused")
    public Map<String, UserDataSet> getSessions() { return sessions; }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        final ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

}
