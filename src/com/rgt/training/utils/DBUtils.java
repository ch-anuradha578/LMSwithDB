package com.rgt.training.utils;

import java.sql.*;

public class DBUtils {
	private static final String DB_NAME = "library-management-system";
	private static final String URL = "jdbc:mysql://localhost:3306/";
	private static final String USER = "root";
	private static final String PASSWORD = "ADMIN123";

	static {
		createDatabaseIfNotExists();
		createTablesIfNotExists();
	}

	private static void createDatabaseIfNotExists() {
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement st = connection.createStatement()) {
			String sql = "CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`";
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.err.println("Error while creating database: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void createTablesIfNotExists() {
		try (Connection connection = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
				Statement st = connection.createStatement()) {

			// Create books table
			String createBooksTableSql = "CREATE TABLE IF NOT EXISTS books (" 
			        + "id INT AUTO_INCREMENT PRIMARY KEY, "
					+ "bookId INT UNIQUE," 
			        + "title VARCHAR(255), " 
					+ "author VARCHAR(255), "
					+ "isBorrowed BOOLEAN DEFAULT FALSE, " 
					+ "borrowedBy VARCHAR(255) DEFAULT 'None'" 
					+ ");";
			st.executeUpdate(createBooksTableSql);

			// Create patrons table
			String createPatronsTableSql = "CREATE TABLE IF NOT EXISTS patrons ("
					+ "id INT AUTO_INCREMENT PRIMARY KEY, " 
					+ "patronId INT UNIQUE," 
					+ "name VARCHAR(255)" 
					+ ");";
			st.executeUpdate(createPatronsTableSql);

			// Create borrowed_books table
			String createBorrowedBooksTableSql = "CREATE TABLE IF NOT EXISTS borrowed_books (" 
			        + "bookId INT, "
					+ "title VARCHAR(255), " 
			        + "author VARCHAR(255), " 
					+ "patronId INT, " 
			        + "patronName VARCHAR(255), "
					+ "PRIMARY KEY (bookId, patronId), " 
			        + "FOREIGN KEY (bookId) REFERENCES books(bookId), "
					+ "FOREIGN KEY (patronId) REFERENCES patrons(patronId)" 
			        + ");";
			st.executeUpdate(createBorrowedBooksTableSql);

			// Create returned_books table
			String createReturnedBooksTableSql = "CREATE TABLE IF NOT EXISTS returned_books (" 
			        + "bookId INT, "
					+ "title VARCHAR(255), " 
			        + "author VARCHAR(255), " 
					+ "patronId INT, "
					+ "patronName VARCHAR(255), "
					+ "PRIMARY KEY (bookId, patronId), " 
					+ "FOREIGN KEY (bookId) REFERENCES books(bookId), "
					+ "FOREIGN KEY (patronId) REFERENCES patrons(patronId)" 
					+ ");";
			st.executeUpdate(createReturnedBooksTableSql);

		} catch (SQLException e) {
			System.err.println("Error while creating tables: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
	}
}
