package com.bwp.utils.sql;


import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * Represents a SQL database
 */
public class SqlDatabase {

    private Connection connection;
    private final Properties properties;
    private final String url;
    private final SqlUtils.SQLDriver driver;

    /**
     * Creates a new SQL database
     *
     * @param driver   The driver of the database
     * @param host     The host of the database
     * @param port     The port of the database
     * @param database The name of the database
     * @param username The username of the database
     * @param password The password of the database
     */
    public SqlDatabase(SqlUtils.SQLDriver driver, String host, Integer port, String database, String username, String password) {
        this.properties = new Properties();
        this.properties.setProperty("user", username);
        this.properties.setProperty("password", password);
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.driver = driver;
    }

    /**
     * initializes a new SQL database
     *
     * @return Boolean if the database was initialized
     */
    public Boolean init() {
        try {
            if (driver.equals(SqlUtils.SQLDriver.MYSQL)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection( url + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", properties);
            }
            if (driver.equals(SqlUtils.SQLDriver.SQLITE)) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(url);
            }

            if (connection != null) {
                return true;
            }

        } catch (SQLException | ClassNotFoundException ex) {
            LoggerFactory.getLogger("Core").info("An error occurred while connecting databse ({})", url);
            return false;
        }
        return false;
    }

    /**
     * Sends a query to the database
     *
     * @param query SQL String
     * @param values Values to replace in the query
     * @return ResultSet from the query
     */
    public ResultSet query(String query, Object... values) {
        try {
            PreparedStatement statement = prepare(query, values);
            if (statement == null) {
                return null;
            }
            return statement.executeQuery();
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * Updates the database
     *
     * @param query SQL String
     * @param values Values to replace in the query
     * @return Integer of the update
     */
    public Integer update(String query, Object... values) {
        try {
            PreparedStatement statement = prepare(query, values);
            if (statement == null) {
                return -1;
            }
            return statement.executeUpdate();
        } catch (Exception exception) {
            return -1;
        }

    }

    /**
     * Inserts data into the database
     *
     * @param query SQL String
     * @param values Values to replace in the query
     * @return Boolean if the data was inserted
     */
    public boolean input(String query, Object... values) {
        try {
            PreparedStatement statement = prepare(query, values);
            if (statement == null) {
                return false;
            }
            return statement.execute();
        } catch (Exception exception) {
            return false;
        }

    }


    private PreparedStatement prepare(String query, Object... values) {
        try {
            if (!init()) {
                return null;
            }
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement;
        } catch (Exception exception) {
            return null;
        }
    }
}
