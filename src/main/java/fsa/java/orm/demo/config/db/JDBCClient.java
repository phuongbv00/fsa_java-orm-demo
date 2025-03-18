package fsa.java.orm.demo.config.db;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class JDBCClient {
    private final Properties properties = new Properties();

    public JDBCClient() {
        try (InputStream input = JDBCClient.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find jdbc.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
