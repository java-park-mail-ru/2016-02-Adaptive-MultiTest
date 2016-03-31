package main;

import base.dataSets.UserDataSet;
import org.hibernate.cfg.Configuration;

/**
 * Created by Sasha on 31.03.16.
 */
public class Config {
    public static Configuration getHibernateConfiguration() {
        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/MultiTest");
        configuration.setProperty("hibernate.connection.username", "mtestuser");
        configuration.setProperty("hibernate.connection.password", "secret");
        configuration.setProperty("hibernate.show_sql", "true");

        return configuration;
    }
}
