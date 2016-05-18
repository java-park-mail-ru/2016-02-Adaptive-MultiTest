package accountService.dao;

import base.dataSets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Sasha on 27.03.16.
 */
@SuppressWarnings("DefaultFileTemplate")
public class UserDataSetDAO {
    private final Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public List<UserDataSet> getAllUsers() {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        //noinspection unchecked
        return (List<UserDataSet>) criteria.list();
    }

    public UserDataSet getUser(long id) throws HibernateException {
        return session.get(UserDataSet.class, id);
    }

    public void addUser(UserDataSet user) throws HibernateException {
        session.save(user);
    }

    public void updateUser(UserDataSet updatedUser, long userId) throws HibernateException {
        final UserDataSet user = session.load(UserDataSet.class, userId);
        if (getUserByLogin(updatedUser.getLogin()) == null)
            user.setLogin(updatedUser.getLogin());
        if (getUserByEmail(updatedUser.getEmail()) == null)
            user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        session.save(user);
    }

    public  void setUserScore(UserDataSet actualUser, int score) throws HibernateException {
        final UserDataSet user = session.load(UserDataSet.class, actualUser.getId());
        user.setScore(score);
        session.save(user);
    }

    public void deleteUser(long userId) throws HibernateException {
        final UserDataSet user = session.load(UserDataSet.class, userId);
        session.delete(user);
    }

    public UserDataSet getUserByLogin(String login) throws HibernateException {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("login", login))
                .uniqueResult();
    }

    public UserDataSet getUserByEmail(String email) throws HibernateException {
        final Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    public List<UserDataSet> getTopPlayers() throws HibernateException {
        final Criteria criteria = session.createCriteria(UserDataSet.class)
                .addOrder(Order.desc("score"))
                .setFirstResult(0)
                .setMaxResults(10);
        //noinspection unchecked
        return (List<UserDataSet>) criteria.list();
    }
}
