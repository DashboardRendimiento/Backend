package com.rrhh.dashboard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreateDb {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "villa13");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE dashboard");
            System.out.println("===========================================");
            System.out.println("DATABASE 'dashboard' CREATED SUCCESSFULLY!");
            System.out.println("===========================================");
            conn.close();
        } catch (Exception e) {
            System.out.println("===========================================");
            System.out.println("DATABASE ERROR: " + e.getMessage());
            System.out.println("===========================================");
        }
    }
}
