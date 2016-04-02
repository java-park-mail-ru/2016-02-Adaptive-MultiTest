package testHelpers;

import accountService.dao.UserDataSetDAO;
import base.dataSets.UserDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by Sasha on 01.04.16.
 */
public class DBFiller {
    public static void fillDB(SessionFactory sessionFactory) {
        try(Session testSession = sessionFactory.openSession()) {
            final UserDataSetDAO dao = new UserDataSetDAO(testSession);
            final UserDataSet admin = new UserDataSet();
            admin.setLogin("admin");
            admin.setEmail("admin@admin");
            admin.setPassword("admin");
            final UserDataSet guest = new UserDataSet();
            guest.setLogin("guest");
            guest.setEmail("guest@guest");
            guest.setPassword("12345");
            dao.addUser(admin);
            dao.addUser(guest);
        }
    }
}
