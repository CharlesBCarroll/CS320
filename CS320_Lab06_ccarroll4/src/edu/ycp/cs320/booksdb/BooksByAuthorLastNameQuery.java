package edu.ycp.cs320.booksdb;

import java.util.List;

import java.util.Scanner;

import edu.ycp.cs320.booksdb.InitDatabase;
import edu.ycp.cs320.booksdb.model.Author;
import edu.ycp.cs320.booksdb.model.Book;
import edu.ycp.cs320.booksdb.model.Pair;
import edu.ycp.cs320.booksdb.persist.DatabaseProvider;
import edu.ycp.cs320.booksdb.persist.IDatabase;

public class BooksByAuthorLastNameQuery{
	public static void main(String[] args) throws Exception{
		Scanner keyboard = new Scanner(System.in);
		
		InitDatabase.init(keyboard);
		
		System.out.print("Enter an Authors Last Name: ");
		String lastname = keyboard.nextLine();
		
		// get the DB instance and execute transaction
		IDatabase db = DatabaseProvider.getInstance();
		List<Pair<Author, Book>> authorBookList = db.findAuthorAndBookByAuthorLastName(lastname);
		
		// check if anything was returned and output the list
		if (authorBookList.isEmpty()) {
			System.out.println("No books found with Author Last Name <" + lastname + ">");
		}
		else {
			for (Pair<Author, Book> authorBook : authorBookList) {
				Author author = authorBook.getLeft();
				Book book = authorBook.getRight();
				System.out.println(author.getLastname() + "," + author.getFirstname() + "," + book.getTitle() + "," + book.getIsbn() + "," + book.getPublished());
			}
		}
	}
	
}