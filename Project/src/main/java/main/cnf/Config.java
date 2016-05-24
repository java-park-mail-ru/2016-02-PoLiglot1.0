package main.cnf;

import account.UserProfile;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.*;

/**
 * Created by Snach on 15.04.16.
 */
public class Config {
    private static final String SERVER_CONFIG_FILE = "configuration/cfg/server.properties";
    private static final String DB_CONFIG_FILE = "configuration/cfg/db.properties";

    final boolean test;


    private static final Logger LOGGER = LogManager.getLogger(Config.class);

    private int port;
    //private String host;

    private final Configuration configuration = new Configuration();

    public Config(boolean test) {
        this.test = test;
    }

    public void loadServerParam() {
        final Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(SERVER_CONFIG_FILE)) {
            properties.load(fileInputStream);
            fileInputStream.close();
            port = Integer.valueOf(properties.getProperty("port"));
            /* host = properties.getProperty("host"); */
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found: " + SERVER_CONFIG_FILE);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void connectToDB() {

        configuration.addAnnotatedClass(UserProfile.class);

        final Properties properties = new Properties();

        try (FileInputStream fileInputStream = new FileInputStream(DB_CONFIG_FILE)) {
            properties.load(fileInputStream);
            fileInputStream.close();

            configuration.setProperty("hibernate.dialect", properties.getProperty("dialect"));
            configuration.setProperty("hibernate.connection.driver_class", properties.getProperty("connection.driver_class"));
            configuration.setProperty("hibernate.connection.username", properties.getProperty("connection.username"));
            configuration.setProperty("hibernate.connection.password", properties.getProperty("connection.password"));
            configuration.setProperty("hibernate.show_sql", properties.getProperty("show_sql"));

            if (test) {
                configuration.setProperty("hibernate.connection.url", properties.getProperty("connection.url.test"));
                configuration.setProperty("hibernate.hbm2ddl.auto", properties.getProperty("hbm2ddl.auto.test"));
            } else {
                configuration.setProperty("hibernate.connection.url", properties.getProperty("connection.url"));
                configuration.setProperty("hibernate.hbm2ddl.auto", properties.getProperty("hbm2ddl.auto"));
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found: " + DB_CONFIG_FILE);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void loadConfig() {

        loadServerParam();
        connectToDB();
    }


    public int getPort() {
        return this.port;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }


}
