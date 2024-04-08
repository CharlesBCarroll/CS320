package edu.ycp.cs320.booksdb.persist;

import java.io.IOException;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import edu.ycp.cs320.booksdb.model.Author;
import edu.ycp.cs320.booksdb.model.Book;
import edu.ycp.cs320.booksdb.model.Pair;

public class FakeDatabase implements IDatabase {
	
	private List<Author> authorList;
	private List<Book> bookList;
	
	public FakeDatabase() {
		authorList = new ArrayList<Author>();
		bookList = new ArrayList<Book>();
		
		// Add initial data
		readInitialData();
		
		System.out.println(authorList.size() + " authors");
		System.out.println(bookList.size() + " books");
	}

	public void readInitialData() {
		try {
			authorList.addAll(InitialData.getAuthors());
			bookList.addAll(InitialData.getBooks());
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't read initial data", e);
		}
	}
	
	@Override
	public List<Pair<Author, Book>> findAuthorAndBookByTitle(String title) {
		List<Pair<Author, Book>> result = new ArrayList<Pair<Author,Book>>();
		for (Book book : bookList) {
			if (book.getTitle().equals(title)) {
				Author author = findAuthorByAuthorId(book.getAuthorId());
				result.add(new Pair<Author, Book>(author, book));
			}
		}
		return result;
	}
	
	public List<Pair<Author, Book>> findAuthorAndBookByAuthorLastName(String lastname){
		List<Pair<Author, Book>> result = new ArrayList<Pair<Author, Book>>();
		for(Author author : authorList) {
			if(author.getLastname().equals(lastname)) {
				for(Book book : bookList) {
					if(book.getAuthorId() == author.getAuthorId()) {
						result.add(new Pair<Author, Book>(author, book));
					}
				}
			}
		}
		Collections.sort(result, new Comparator<Pair<Author, Book>>() {
            @Override
            public int compare(Pair<Author, Book> pair1, Pair<Author, Book> pair2) {
                String title1 = pair1.getRight().getTitle();
                String title2 = pair2.getRight().getTitle();
                return title1.compareTo(title2);
            }
        });

        return result;
    
	}
	
	public List<Pair<Author, Book>> insertNewBookWithAuthor(String firstname, String lastname, String title, String isbn, int published ) {
		List<Pair<Author, Book>> result = new ArrayList<Pair<Author,Book>>();
		Author authorWithLastName = null;
		for(Author author : authorList) {
			if (author.getLastname().equals(lastname) && author.getFirstname().equals(firstname)) {
				authorWithLastName = author;
				break;
			}
		}
		try {
			int newAuthorId;
			if (authorWithLastName == null) {
				newAuthorId = authorList.size()+ 1;
				Author newAuthor = new Author();
				newAuthor.setAuthorId(newAuthorId);
				newAuthor.setLastname(lastname);
				newAuthor.setFirstname(firstname);
				authorList.add(newAuthor);
				authorWithLastName = newAuthor;
				System.out.println("Added New Author");
				System.out.println();
			}
			else {
				Author author2 = null;
				for (Author author : authorList) {
					if(author.getLastname().equals(lastname) && author.getFirstname().equals(firstname)) {
						author2 = author;
						break;
					}
				}
				newAuthorId = author2.getAuthorId();
			}
			Book newBook = new Book();
			newBook.setTitle(title);
			newBook.setIsbn(isbn);
			newBook.setPublished(published);
			newBook.setAuthorId(newAuthorId);
			newBook.setBookId(bookList.size()+1);
			bookList.add(newBook);
			result.add(new Pair<Author,Book>(authorWithLastName, newBook));
			System.out.println("Added New Book");
			System.out.println();
			System.out.println("Updated database:");
	        for (Author author : authorList) {
	            System.out.println("Author: " + author.getFirstname() + " " + author.getLastname() + " Author ID: " + author.getAuthorId());
	            for (Book book : bookList) {
	                if (book.getAuthorId() == author.getAuthorId()) {
	                    System.out.println("  Book: " + book.getTitle() + " ISBN: " + book.getIsbn() + " Published: " + book.getPublished() + " Book ID: "+ book.getBookId());
	                }
	            }
	        }
		} catch(IllegalArgumentException e) {
			System.out.print(e.getMessage());
		}
		return result;
		
	}
	
	

	private Author findAuthorByAuthorId(int authorId) {
		for (Author author : authorList) {
			if (author.getAuthorId() == authorId) {
				return author;
			}
		}
		return null;
	}
	

}

