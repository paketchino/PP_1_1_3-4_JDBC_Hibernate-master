package jm.task.core.jdbc.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Util {

    public Util() {
    }

    public static Connection getConnection() {
        Connection connection = null;
        try (InputStream in = Util.class.getClassLoader()
                .getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            return connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return connection;
    }

}
