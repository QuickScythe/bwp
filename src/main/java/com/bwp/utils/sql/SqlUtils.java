package com.bwp.utils.sql;

import com.bwp.Main;
import com.bwp.registries.Registries;
import com.bwp.registries.Registry;

/**
 * Utility class for SQL operations
 */
public class SqlUtils {

    /**
     * Utility class
     */
    private SqlUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final DatabaseManager DATABASE_MANAGER = new DatabaseManager();


    /**
     * Escapes a string for SQL
     *
     * @param str The string to escape
     * @return The escaped string
     */
    public static String escape(String str) {

        String data = null;
        if (str != null && !str.isEmpty()) {
            str = str.replace("\\", "\\\\");
            str = str.replace("'", "\\'");
            str = str.replace("\0", "\\0");
            str = str.replace("\n", "\\n");
            str = str.replace("\r", "\\r");
            str = str.replace("\"", "\\\"");
            str = str.replace("\\x1a", "\\Z");
            data = str;
        }
        return data;

    }

    public static class DatabaseManager {

        private final Registry<SqlDatabase> databases;

        private DatabaseManager() {
            databases = Registries.register("databases", ()-> null);
        }

        /**
         * @param name The name of the database
         * @param db   The database to create
         */
        public void create(String name, SqlDatabase db) {
            if (!db.init()) {
                Main.LOGGER.info("There was an error registering database: {}", name);
                return;
            }
            Main.LOGGER.info("Registered database: {}", name);
            databases.register(name, db);
        }

        /**
         * Gets a database
         *
         * @param name The name of the database
         * @return The {@link SqlDatabase} or null if it doesn't exist
         */
        public SqlDatabase get(String name) {
            return databases.getOrDefault(name, null);
        }
    }

    /**
     * Represents a SQL driver
     */
    public enum SQLDriver {

        /**
         * Represents a SQLite driver
         */
        SQLITE("sqlite"),

        /**
         * Represents a MySQL driver
         */
        MYSQL("mysql");

        final String name;

        /**
         * Creates a new SQL driver
         *
         * @param name The name of the driver
         */
        SQLDriver(String name) {
            this.name = name;
        }

    }

}