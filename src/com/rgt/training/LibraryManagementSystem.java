package com.rgt.training;

import java.sql.SQLException;

import com.rgt.training.controllers.*;
import com.rgt.training.database.*;
import com.rgt.training.services.*;
import com.rgt.training.utils.*;

public class LibraryManagementSystem {
	public static void main(String[] args) throws SQLException {
		
		DBUtils.getConnection();
		
		BookDatabase bookDatabase = new BookDatabase();
		PatronDatabase patronDatabase = new PatronDatabase();
		LibraryService libraryService = new LibraryService(bookDatabase, patronDatabase);
		LibraryController libraryController = new LibraryController(libraryService);

		libraryController.start();
	}
}
