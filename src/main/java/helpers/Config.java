package helpers;

import base.dataSets.UserDataSet;
import org.hibernate.cfg.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Sasha on 31.03.16.
 */
public class Config {
    public static Configuration getHibernateConfiguration(String dbName, boolean autoCreate) {
        final Properties dbProperties = new Properties();
        //noinspection OverlyBroadCatchBlock
        try {
            final FileInputStream fis = new FileInputStream("src/main/java/cfg/db.properties");
            dbProperties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", dbProperties.getProperty("db.dialect"));
        configuration.setProperty("hibernate.connection.driver_class", dbProperties.getProperty("db.driver_class"));
        configuration.setProperty("hibernate.connection.url", dbProperties.getProperty("db.url") + dbName);
        configuration.setProperty("hibernate.connection.username", dbProperties.getProperty("db.username"));
        configuration.setProperty("hibernate.connection.password",dbProperties.getProperty(("db.password")));
        configuration.setProperty("hibernate.show_sql", dbProperties.getProperty("db.show_sql"));
        if (autoCreate) {
            configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        } else {
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        }

        return configuration;
    }
}
