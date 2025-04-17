package com.rgt.training.services;

import com.rgt.training.models.*;
import com.rgt.training.database.*;

import java.util.*;

public class LibraryService {
	private BookDatabase bookDb;
	private PatronDatabase patronDb;

	public LibraryService(BookDatabase bookDb, PatronDatabase patronDb) {
		this.bookDb = bookDb;
		this.patronDb = patronDb;
	}

	// Check if a book exists in the database
	public boolean isBookExists(int bookId) {
		return bookDb.isBookExists(bookId);
	}

	// Check if a patron exists in the database
	public boolean isPatronExists(int patronId) {
		return patronDb.getPatronById(patronId) != null;
	}

	// Add a new book to the library
	public void addBook(int bookId, String title, String author) {
		Books book = new Books(bookId, title, author);
		bookDb.addBook(book);
	}

	// Add a new patron to the library
	public void addPatron(int patronId, String name) {
		Patrons patron = new Patrons(patronId, name);
		patronDb.addPatron(patron);
	}

	// Borrow a book for a specific patron
	public boolean borrowBook(int patronId, String title) {
		Books book = bookDb.getBookByTitle(title);
		if (book == null) {
			System.out.println("Book not found.");
			return false;
		}
		if (book.isBorrowed()) {
			System.out.println("This book is already borrowed by Patron: " + book.getBorrowedBy());
			return false;
		}
		book.borrow(patronId);
		return bookDb.borrowBook(patronId, book.getTitle());
	}

	// Check if a book title exists in the database
	public boolean isBookTitleExists(String title) {
		return bookDb.getBookByTitle(title) != null;
	}
	
	// Return a book from a particular patron
	public boolean returnBook(int patronId, String title) {
		Patrons patron = patronDb.getPatronById(patronId);
		if (patron == null) {
			System.out.println("Patron not found.");
			return false;
		}
		String patronName = patron.getName();
		return bookDb.returnBook(patronId, title, patronName);
	}


	// List all the books in the library
	public void listAllBooks() {
		List<Books> books = bookDb.getAllBooks();
		if (books.isEmpty()) {
			System.out.println("No Book is Available in Library!");
		} else {
			books.forEach(book -> System.out.println(
					"BookId: " + book.getBookId() + " | Book: " + book.getTitle() + " | Author: " + book.getAuthor()));
		}
	}

	// List all the patrons in the library
	public void listAllPatrons() {
		List<Patrons> patrons = patronDb.getAllPatrons();
		if (patrons.isEmpty()) {
			System.out.println("No Patron is Available in Library!");
		} else {
			patrons.forEach(patron -> System.out
					.println("PatronId: " + patron.getPatronId() + " | PatronName: " + patron.getName()));
		}
	}

	// List all borrowed books by a specific patron
	public void listBorrowedBooksByPatron(int patronId) {
		List<Books> borrowed = bookDb.getBorrowedBooksByPatron(patronId);
		if (borrowed.isEmpty()) {
			System.out.println("No books borrowed by Patron ID: " + patronId);
		} else {
			borrowed.forEach(book -> System.out.println("BorrowedBook: " + book.getTitle()));
		}
	}

	// List all borrowed books in the library
	public void listBorrowedBooks() {
		List<String> borrowedBooks = bookDb.getAllBorrowedBooks();

		if (borrowedBooks.isEmpty()) {
			System.out.println("No borrowed books at the moment.");
		} else {
			borrowedBooks.forEach(System.out::println);
		}
	}

}

