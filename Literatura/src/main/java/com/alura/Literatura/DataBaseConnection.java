package com.alura.Literatura;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

    public class DataBaseConnection {

        private static final String DB_URL = "jdbc:postgresql://localhost:5432/Literatura";
        private static final String DB_USER = "postgres";
        private static final String DB_PASSWORD = "123456";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);


        }
    }
