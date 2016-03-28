package accountService.dao;

import base.dataSets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Sasha on 27.03.16.
 */
public class UserDataSetDAO {
    private Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public List<UserDataSet> getAllUsers() {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (List<UserDataSet>) criteria.list();
    }

    public UserDataSet getUser(long id) {
        return session.get(UserDataSet.class, id);
    }

    public void addUser(UserDataSet user) {
        Transaction trx = session.beginTransaction();
        session.save(user);
        trx.commit();
    }

    public void updateUser(UserDataSet updatedUser, long userId) {
        Transaction trx = session.beginTransaction();
        UserDataSet user = session.load(UserDataSet.class, userId);
        if (getUserByLogin(updatedUser.getLogin()) == null)
            user.setLogin(updatedUser.getLogin());
        if (getUserByEmail(updatedUser.getEmail()) == null)
            user.setEmail(updatedUser.getEmail());
        user.setPassword(updatedUser.getPassword());
        session.save(user);
        trx.commit();
    }

    public void deleteUser(long userId) {
        Transaction trx = session.beginTransaction();
        UserDataSet user = session.load(UserDataSet.class, userId);
        session.delete(user);
        trx.commit();
    }

    public UserDataSet getUserByLogin(String login) {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("login", login))
                .uniqueResult();
    }

    public UserDataSet getUserByEmail(String email) {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }
}
