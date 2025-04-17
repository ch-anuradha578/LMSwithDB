package com.rgt.training.database;

import com.rgt.training.models.Books;
import com.rgt.training.utils.DBUtils;

import java.sql.*;
import java.util.*;

public class BookDatabase {

	// Check if a book exists in the database
	public boolean isBookExists(int bookId) {
		String sql = "SELECT COUNT(*) FROM books WHERE bookId = ?";
		try (Connection connection = DBUtils.getConnection(); PreparedStatement st = connection.prepareStatement(sql)) {
			st.setInt(1, bookId);
			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Add a new book to the database

	public void addBook(Books book) {
		String query = "INSERT INTO books(bookId, title, author) VALUES(?, ?, ?)";
		try (Connection connection = DBUtils.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, book.getBookId());
			ps.setString(2, book.getTitle());
			ps.setString(3, book.getAuthor());
			ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error while adding the book: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Get book by ID

	public Books getBookById(int bookId) {
		String query = "SELECT * FROM books WHERE bookId = ?";
		try (Connection connection = DBUtils.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {

			ps.setInt(1, bookId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new Books(rs.getInt("bookId"), rs.getString("title"), rs.getString("author"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Get book by title

	public Books getBookByTitle(String title) {
		String query = "SELECT * FROM books WHERE LOWER(title) = LOWER(?)";
		try (Connection connection = DBUtils.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {

			ps.setString(1, title);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Books book = new Books();
				book.setBookId(rs.getInt("bookId"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setBorrowed(rs.getBoolean("isBorrowed"));
				book.setBorrowedBy(rs.getString("borrowedBy")); // ✅ DB loaded
				return book;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Borrow a book

	public boolean borrowBook(int patronId, String title) {
		String sql = "SELECT * FROM books WHERE LOWER(title) = LOWER(?)";
		try (Connection connection = DBUtils.getConnection(); PreparedStatement st = connection.prepareStatement(sql)) {
			st.setString(1, title);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				int bookId = rs.getInt("bookId");
				String author = rs.getString("author");
				boolean isBorrowed = rs.getBoolean("isBorrowed");

				if (isBorrowed) {
					System.out.println("This book is already borrowed.");
					return false;
				}

				// Get Patron Name
				String patronName = "Unknown";
				String getPatronNameSql = "SELECT name FROM patrons WHERE patronId = ?";
				try (PreparedStatement patronStmt = connection.prepareStatement(getPatronNameSql)) {
					patronStmt.setInt(1, patronId);
					ResultSet patronRs = patronStmt.executeQuery();
					if (patronRs.next()) {
						patronName = patronRs.getString("name");
					} else {
						System.out.println("Patron not found.");
						return false;
					}
				}

				// Update books table to mark as borrowed and set borrowedBy to patronName
				String updateSql = "UPDATE books SET isBorrowed = TRUE, borrowedBy = ? WHERE bookId = ?";
				try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
					updateStmt.setString(1, patronName);
					updateStmt.setInt(2, bookId);
					updateStmt.executeUpdate();
				}

				// Insert into borrowed_books
				String insertSql = "INSERT INTO borrowed_books (bookId, title, author, patronId, patronName) VALUES (?, ?, ?, ?, ?)";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
					insertStmt.setInt(1, bookId);
					insertStmt.setString(2, title);
					insertStmt.setString(3, author);
					insertStmt.setInt(4, patronId);
					insertStmt.setString(5, patronName);
					insertStmt.executeUpdate();
				}
				
				// Delete from returned_books if borrowed again
				String deleteFromReturnedSql = "DELETE FROM returned_books WHERE bookId = ? AND patronId = ?";
				try (PreparedStatement deleteReturnedStmt = connection.prepareStatement(deleteFromReturnedSql)) {
					deleteReturnedStmt.setInt(1, bookId);
					deleteReturnedStmt.setInt(2, patronId);
					deleteReturnedStmt.executeUpdate();
				}

				return true;
			} else {
				System.out.println("Book not found with title: " + title);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Return a book

	public boolean returnBook(int patronId, String title, String patronName) {
		String sql = "SELECT * FROM books WHERE LOWER(title) = LOWER(?)";
		try (Connection connection = DBUtils.getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, title);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int bookId = rs.getInt("bookId");

				// Check if this book was borrowed by the given patron
				String checkBorrowSql = "SELECT * FROM borrowed_books WHERE bookId = ? AND patronId = ?";
				try (PreparedStatement checkStmt = connection.prepareStatement(checkBorrowSql)) {
					checkStmt.setInt(1, bookId);
					checkStmt.setInt(2, patronId);
					ResultSet checkRs = checkStmt.executeQuery();

					if (!checkRs.next()) {
						System.out.println("This book was not borrowed by Patron ID: " + patronId);
						return false;
					}
				}

				// Delete from borrowed_books
				String deleteSql = "DELETE FROM borrowed_books WHERE bookId = ? AND patronId = ?";
				try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
					deleteStmt.setInt(1, bookId);
					deleteStmt.setInt(2, patronId);
					deleteStmt.executeUpdate();
				}

				// Insert into returned_books (ignore duplicates if needed)
				String insertSql = "INSERT IGNORE INTO returned_books (bookId, title, author, patronId, patronName) "
						+ "VALUES (?, ?, ?, ?, ?)";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
					insertStmt.setInt(1, bookId);
					insertStmt.setString(2, title);
					insertStmt.setString(3, rs.getString("author"));
					insertStmt.setInt(4, patronId);
					insertStmt.setString(5, patronName);
					insertStmt.executeUpdate();
				}

				// Update books table
				String updateSql = "UPDATE books SET isBorrowed = FALSE, borrowedBy = 'None' WHERE bookId = ?";
				try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
					updateStmt.setInt(1, bookId);
					updateStmt.executeUpdate();
				}
				return true;
			} else {
				System.out.println("Book not found with title: " + title);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Get all books from the database

	public List<Books> getAllBooks() {
		List<Books> books = new ArrayList<>();
		String query = "SELECT * FROM books";
		try (Connection connection = DBUtils.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Books book = new Books(rs.getInt("bookId"), rs.getString("title"), rs.getString("author"));
				books.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;
	}

	// Get borrowed books by a particular patron

	public List<Books> getBorrowedBooksByPatron(int patronId) {
		List<Books> books = new ArrayList<>();
		String sql = "SELECT bookId, title, author FROM borrowed_books WHERE patronId = ?";

		try (Connection connection = DBUtils.getConnection(); PreparedStatement st = connection.prepareStatement(sql)) {

			st.setInt(1, patronId);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Books book = new Books(rs.getInt("bookId"), rs.getString("title"), rs.getString("author"));
				books.add(book);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return books;
	}

	// Get all borrowed books with patron details

	public List<String> getAllBorrowedBooks() {
		List<String> borrowedBooksDetails = new ArrayList<>();
		String sql = "SELECT b.title AS bookTitle, bb.patronId AS borrowedByPatronId, p.name AS patronName "
				+ "FROM borrowed_books bb " + "JOIN books b ON bb.bookId = b.bookId "
				+ "JOIN patrons p ON bb.patronId = p.patronId";

		try (Connection connection = DBUtils.getConnection();
				PreparedStatement st = connection.prepareStatement(sql);
				ResultSet rs = st.executeQuery()) {

			while (rs.next()) {
				String bookTitle = rs.getString("bookTitle");
				int patronId = rs.getInt("borrowedByPatronId");
				String patronName = rs.getString("patronName");

				// Store the details in the list
				String details = "Book: " + bookTitle + " | Borrowed by Patron ID: " + patronId + " | Patron Name: "
						+ patronName;
				borrowedBooksDetails.add(details);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return borrowedBooksDetails;
	}

}
