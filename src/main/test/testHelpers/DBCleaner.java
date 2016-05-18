package testHelpers;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by Sasha on 17.05.16.
 */
public class DBCleaner {
    public static void clearDB(SessionFactory sessionFactory) {
        try(Session testSession = sessionFactory.openSession()) {
            final Query query = testSession.createSQLQuery("truncate table User");
            query.executeUpdate();
        }
    }
}
