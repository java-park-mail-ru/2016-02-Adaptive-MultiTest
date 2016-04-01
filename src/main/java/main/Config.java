package main;

import base.dataSets.UserDataSet;
import org.hibernate.cfg.Configuration;

/**
 * Created by Sasha on 31.03.16.
 */
public class Config {
    public static Configuration getHibernateConfiguration(String dbName, boolean autoCreate) {
        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/" + dbName);
        configuration.setProperty("hibernate.connection.username", "mtestuser");
        configuration.setProperty("hibernate.connection.password", "secret");
        configuration.setProperty("hibernate.show_sql", "true");
        if (autoCreate) {
            configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        } else {
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        }

        return configuration;
    }
}
