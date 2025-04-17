package com.rgt.training.controllers;

import java.util.Scanner;
import com.rgt.training.services.LibraryService;

public class LibraryController {
	private LibraryService libraryService;

	public LibraryController(LibraryService libraryService) {
		this.libraryService = libraryService;
	}

	public void start() {
		Scanner sc = new Scanner(System.in);
		int choice = -1;

		while (choice != 9) {
			System.out.println("\n=== Library Management System ===");
			System.out.println("1. Add Book");
			System.out.println("2. Add Patron");
			System.out.println("3. Borrow Book");
			System.out.println("4. Return Book");
			System.out.println("5. List of all Books");
			System.out.println("6. List of all Patrons");
			System.out.println("7. List of Books borrowed by specific patron");
			System.out.println("8. List of All Borrowed Books");
			System.out.println("9. Exit");
			System.out.print("Enter your choice: ");

			if (sc.hasNextInt()) {
				choice = sc.nextInt();
				sc.nextLine();
			} else {
				System.out.println("Invalid input! Please enter a number.");
				sc.nextLine();
				continue;
			}

			switch (choice) {

			case 1: // Add Book

				System.out.print("Enter Book Id: ");
				String bookIdStr = sc.nextLine().trim();

				if (bookIdStr.isEmpty() || !bookIdStr.matches("\\d+")) {
					System.out.println("Book ID can't be empty and must be a number.");
					break;
				}

				int bookId = Integer.parseInt(bookIdStr);

				if (libraryService.isBookExists(bookId)) {
					System.out.println("This book already exists.");
					break;
				}

				System.out.print("Enter Book Title: ");
				String title = sc.nextLine().trim();
				if (title.isEmpty()) {
					System.out.println("Book title cannot be empty.");
					break;
				}

				System.out.print("Enter Book Author: ");
				String author = sc.nextLine().trim();
				if (author.isEmpty()) {
					System.out.println("Book author cannot be empty.");
					break;
				}

				libraryService.addBook(bookId, title, author);
				System.out.println("Book added successfully.");
				break;

			case 2: // Add Patron

				System.out.print("Enter Patron Id: ");
				String patronIdStr = sc.nextLine().trim();

				if (patronIdStr.isEmpty() || !patronIdStr.matches("\\d+")) {
					System.out.println("Patron ID can't be empty and must be a number.");
					break;
				}

				int patronId = Integer.parseInt(patronIdStr);

				if (libraryService.isPatronExists(patronId)) {
					System.out.println("This patron already exists.");
					break;
				}

				System.out.print("Enter Patron Name: ");
				String name = sc.nextLine().trim();
				if (name.isEmpty()) {
					System.out.println("Patron name cannot be empty.");
					break;
				}

				libraryService.addPatron(patronId, name);
				System.out.println("Patron added successfully.");
				break;

			case 3: // Borrow Books

				System.out.print("Enter Patron Id: ");
				String borrowPatronIdStr = sc.nextLine().trim();

				if (borrowPatronIdStr.isEmpty() || !borrowPatronIdStr.matches("\\d+")) {
					System.out.println("Patron ID can't be empty and must be a number.");
					break;
				}

				int borrowPatronId = Integer.parseInt(borrowPatronIdStr);

				if (!libraryService.isPatronExists(borrowPatronId)) {
					System.out.println("Patron not found.");
					break;
				}

				System.out.print("Enter Book Title to Borrow: ");
				String borrowTitle = sc.nextLine().trim();
				if (borrowTitle.isEmpty()) {
					System.out.println("Book title cannot be empty.");
					break;
				}

				if (!libraryService.isBookTitleExists(borrowTitle)) {
					System.out.println("Book not found with the given title.");
				} else if (libraryService.borrowBook(borrowPatronId, borrowTitle)) {
					System.out.println("Book borrowed successfully!");
				} else {
					System.out.println("Book is already borrowed.");
				}
				break;

			case 4: // Return Books
				System.out.print("Enter Patron Id: ");
				String returnPatronIdStr = sc.nextLine().trim();

				if (returnPatronIdStr.isEmpty() || !returnPatronIdStr.matches("\\d+")) {
					System.out.println("Patron ID can't be empty and must be a number.");
					break;
				}

				int returnPatronId = Integer.parseInt(returnPatronIdStr);

				if (!libraryService.isPatronExists(returnPatronId)) {
					System.out.println("Patron not found.");
					break;
				}

				System.out.print("Enter Book Title to Return: ");
				String returnTitle = sc.nextLine().trim();
				if (returnTitle.isEmpty()) {
					System.out.println("Book title cannot be empty.");
					break;
				}

				boolean returned = libraryService.returnBook(returnPatronId, returnTitle);
				if (returned) {
					System.out.println("Book returned successfully!");
				} else {
					System.out.println("Book not found or not borrowed by this patron.");
				}
				break;


			case 5: // List of all books
				
				libraryService.listAllBooks();
				break;

			case 6: // List of all patrons
				libraryService.listAllPatrons();
				break;

			case 7: // List of all books borrowed by particular patron
				
				System.out.print("Enter Patron Id: ");
				String pidStr = sc.nextLine().trim();

				if (pidStr.isEmpty() || !pidStr.matches("\\d+")) {
					System.out.println("Patron ID can't be empty and must be a number.");
					break;
				}

				int pid = Integer.parseInt(pidStr);

				if (!libraryService.isPatronExists(pid)) {
					System.out.println("Patron not found.");
					break;
				}

				libraryService.listBorrowedBooksByPatron(pid);
				break;

			case 8: // List of all borrowed books
				
				libraryService.listBorrowedBooks();
				break;

			case 9: // Exit
				
				System.out.println("Exiting the Library Management System. Goodbye!");
				break;

			default:
				System.out.println("Invalid choice. Please select a valid option (1-9).");
			}
		}

		sc.close();
	}
}
