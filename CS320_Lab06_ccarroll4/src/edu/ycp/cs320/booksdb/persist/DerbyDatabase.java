package edu.ycp.cs320.booksdb.persist;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.ycp.cs320.booksdb.model.Author;
import edu.ycp.cs320.booksdb.model.Book;
import edu.ycp.cs320.booksdb.model.Pair;
import edu.ycp.cs320.sqldemo.DBUtil;

public class DerbyDatabase implements IDatabase {
	static {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			throw new IllegalStateException("Could not load Derby driver");
		}
	}
	
	private interface Transaction<ResultType> {
		public ResultType execute(Connection conn) throws SQLException;
	}

	private static final int MAX_ATTEMPTS = 10;

	@Override
	public List<Pair<Author, Book>> findAuthorAndBookByTitle(final String title) {
		return executeTransaction(new Transaction<List<Pair<Author,Book>>>() {
			@Override
			public List<Pair<Author, Book>> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					stmt = conn.prepareStatement(
							"select authors.*, books.* " +
							"  from authors, books " +
							" where authors.author_id = books.author_id " +
							"   and books.title = ?"
					);
					stmt.setString(1, title);
					
					List<Pair<Author, Book>> result = new ArrayList<Pair<Author,Book>>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						Author author = new Author();
						loadAuthor(author, resultSet, 1);
						
						// create new Book object
						// retrieve attributes from resultSet starting at index 4
						Book book = new Book();
						loadBook(book, resultSet, 4);
						
						result.add(new Pair<Author, Book>(author, book));
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("<" + title + "> was not found in the books table");
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	
	public List<Pair<Author, Book>> findAuthorAndBookByAuthorLastName(final String lastname) {
		return executeTransaction(new Transaction<List<Pair<Author,Book>>>() {
			@Override
			public List<Pair<Author, Book>> execute(Connection conn) throws SQLException {
				PreparedStatement stmt = null;
				ResultSet resultSet = null;
				
				try {
					// retreive all attributes from both Books and Authors tables
					stmt = conn.prepareStatement(
							"select authors.*, books.* "
									+ "  from authors, books "
									+ "  where authors.author_id = books.author_id "
									+ "  and authors.lastname = ? "	
									+ "  order by books.title ASC"
							);
					stmt.setString(1, lastname);
					
					List<Pair<Author, Book>> result = new ArrayList<Pair<Author,Book>>();
					
					resultSet = stmt.executeQuery();
					
					// for testing that a result was returned
					Boolean found = false;
					
					while (resultSet.next()) {
						found = true;
						
						// create new Author object
						// retrieve attributes from resultSet starting with index 1
						Author author = new Author();
						loadAuthor(author, resultSet, 1);
						
						// create new Book object
						// retrieve attributes from resultSet starting at index 4
						Book book = new Book();
						loadBook(book, resultSet, 4);
						
						result.add(new Pair<Author, Book>(author, book));
					}
					
					// check if the title was found
					if (!found) {
						System.out.println("<" + lastname + "> was not found in the books table");
					}
					
					return result;
				} finally {
					DBUtil.closeQuietly(resultSet);
					DBUtil.closeQuietly(stmt);
				}
			}
		});
	}
	public List<Pair<Author, Book>>insertNewBookWithAuthor(final String firstname,final String lastname,final String title,final String isbn,final int published) {
		return executeTransaction(new Transaction<List<Pair<Author, Book>>>() {
			@Override
			public List<Pair<Author, Book>> execute(Connection conn) throws SQLException {
				PreparedStatement stmtAuthorID1    = null;
				PreparedStatement stmtAuthorID2    = null;		
				PreparedStatement stmtInsertAuthor = null;
				PreparedStatement stmtInsertBook   = null;
				PreparedStatement stmtCheckResults = null;
				
				// ResultSet references - one for each query
				ResultSet resultSetAuthorID1    = null;
				ResultSet resultSetAuthorID2    = null;
				ResultSet resultSetCheckResults = null;
				
				try {
					stmtAuthorID1 = conn.prepareStatement(
							"select authors.author_id "
							+ "  from authors "
							+ "  where authors.firstname = ? and authors.lastname = ? "
					);

					stmtAuthorID1.setString(1, firstname);

					stmtAuthorID1.setString(2, lastname);
			
					resultSetAuthorID1 = stmtAuthorID1.executeQuery();
					ResultSetMetaData resultSchema = stmtAuthorID1.getMetaData();
					
					int authorID = -1;
					if(resultSetAuthorID1.next()) {
						authorID = resultSetAuthorID1.getInt(1);
					}
					else {
						stmtInsertAuthor = conn.prepareStatement(
								"insert into authors (lastname, firstname) "
										+ "values (?, ?)"
								);
						stmtInsertAuthor.setString(1, lastname);
						stmtInsertAuthor.setString(2, firstname);
						
						stmtInsertAuthor.executeUpdate();
					}
						
						stmtAuthorID2 = conn.prepareStatement(
								"select authors.author_id "
										+ "  from authors "
										+ "  where authors.firstname = ? and authors.lastname = ? "
								);
						stmtAuthorID2.setString(1, firstname);
						stmtAuthorID2.setString(2, lastname);
						
						resultSetAuthorID2 = stmtAuthorID2.executeQuery();
						resultSchema = stmtAuthorID2.getMetaData();
						
						if(resultSetAuthorID2.next()) {
							authorID= resultSetAuthorID2.getInt(1);
							
						}
					
					stmtInsertBook = conn.prepareStatement(
							"insert into books (author_id, title, ISBN, published) "
									+ "  values (?, ?, ?, ?)"
							);
					stmtInsertBook.setInt(1, authorID);
					
					stmtInsertBook.setString(2,title);
					
					stmtInsertBook.setString(3, isbn);
					
					stmtInsertBook.setInt(4, published);
					
					stmtInsertBook.executeUpdate();		
					
					System.out.println("New Book Inserted");
					
					List<Pair<Author, Book>> result = new ArrayList<Pair<Author, Book>>();
							
					stmtCheckResults = conn.prepareStatement(
							"select *"
							+ " from authors, books"
							+ " where authors.author_id = books.author_id"
					);
					
					resultSetCheckResults = stmtCheckResults.executeQuery();
					resultSchema = stmtCheckResults.getMetaData();	
					
					int rowsReturned = 0;
					while (resultSetCheckResults.next()) {
						for (int i = 1; i <= resultSchema.getColumnCount(); i++) {				
							Object obj = resultSetCheckResults.getObject(i);
							if (i > 1) {
								System.out.print(",");
							}
							System.out.print(obj.toString());
						}
						System.out.println();
						rowsReturned++;
	                }
					if (rowsReturned == 0) {
						System.out.println("No books were found in the DB - that's really bad...!!");
					}		

	                return result;

	            } finally {
	                DBUtil.closeQuietly(resultSetAuthorID1);
	                DBUtil.closeQuietly(resultSetAuthorID2);
	                DBUtil.closeQuietly(resultSetCheckResults);
	                DBUtil.closeQuietly(stmtAuthorID1);
	                DBUtil.closeQuietly(stmtAuthorID2);
	                DBUtil.closeQuietly(stmtInsertAuthor);
	                DBUtil.closeQuietly(stmtInsertBook);
	                DBUtil.closeQuietly(stmtCheckResults);
						
				}
			}
		});
	}
	
	
	public<ResultType> ResultType executeTransaction(Transaction<ResultType> txn) {
		try {
			return doExecuteTransaction(txn);
		} catch (SQLException e) {
			throw new PersistenceException("Transaction failed", e);
		}
	}
	
	public<ResultType> ResultType doExecuteTransaction(Transaction<ResultType> txn) throws SQLException {
		Connection conn = connect();
		
		try {
			int numAttempts = 0;
			boolean success = false;
			ResultType result = null;
			
			while (!success && numAttempts < MAX_ATTEMPTS) {
				try {
					result = txn.execute(conn);
					conn.commit();
					success = true;
				} catch (SQLException e) {
					if (e.getSQLState() != null && e.getSQLState().equals("41000")) {
						// Deadlock: retry (unless max retry count has been reached)
						numAttempts++;
					} else {
						// Some other kind of SQLException
						throw e;
					}
				}
			}
			
			if (!success) {
				throw new SQLException("Transaction failed (too many retries)");
			}
			
			// Success!
			return result;
		} finally {
			DBUtil.closeQuietly(conn);
		}
	}

	private Connection connect() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
		// Set autocommit to false to allow execution of
		// multiple queries/statements as part of the same transaction.
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	private void loadAuthor(Author author, ResultSet resultSet, int index) throws SQLException {
		author.setAuthorId(resultSet.getInt(index++));
		author.setLastname(resultSet.getString(index++));
		author.setFirstname(resultSet.getString(index++));
	}
	
	private void loadBook(Book book, ResultSet resultSet, int index) throws SQLException {
		book.setBookId(resultSet.getInt(index++));
		book.setAuthorId(resultSet.getInt(index++));
		book.setTitle(resultSet.getString(index++));
		book.setIsbn(resultSet.getString(index++));
		book.setPublished(resultSet.getInt(index++));		
	}
	
	public void createTables() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				PreparedStatement stmt1 = null;
				PreparedStatement stmt2 = null;
				
				try {
					stmt1 = conn.prepareStatement(
						"create table authors (" +
						"	author_id integer primary key " +
						"		generated always as identity (start with 1, increment by 1), " +									
						"	lastname varchar(40)," +
						"	firstname varchar(40)" +
						")"
					);	
					stmt1.executeUpdate();
					
					stmt2 = conn.prepareStatement(
							"create table books (" +
							"	book_id integer primary key " +
							"		generated always as identity (start with 1, increment by 1), " +
							"	author_id integer constraint author_id references authors, " +
							"	title varchar(70)," +
							"	isbn varchar(15)," +
							"   published integer " +
							")"
					);
					stmt2.executeUpdate();
					
					return true;
				} finally {
					DBUtil.closeQuietly(stmt1);
					DBUtil.closeQuietly(stmt2);
				}
			}
		});
	}
	
	public void loadInitialData() {
		executeTransaction(new Transaction<Boolean>() {
			@Override
			public Boolean execute(Connection conn) throws SQLException {
				List<Author> authorList;
				List<Book> bookList;
				
				try {
					authorList = InitialData.getAuthors();
					bookList = InitialData.getBooks();
				} catch (IOException e) {
					throw new SQLException("Couldn't read initial data", e);
				}

				PreparedStatement insertAuthor = null;
				PreparedStatement insertBook   = null;

				try {
					// populate authors table (do authors first, since author_id is foreign key in books table)
					insertAuthor = conn.prepareStatement("insert into authors (lastname, firstname) values (?, ?)");
					for (Author author : authorList) {
//						insertAuthor.setInt(1, author.getAuthorId());	// auto-generated primary key, don't insert this
						insertAuthor.setString(1, author.getLastname());
						insertAuthor.setString(2, author.getFirstname());
						insertAuthor.addBatch();
					}
					insertAuthor.executeBatch();
					
					// populate books table (do this after authors table,
					// since author_id must exist in authors table before inserting book)
					insertBook = conn.prepareStatement("insert into books (author_id, title, isbn, published) values (?, ?, ?, ?)");
					for (Book book : bookList) {
//						insertBook.setInt(1, book.getBookId());		// auto-generated primary key, don't insert this
						insertBook.setInt(1, book.getAuthorId());
						insertBook.setString(2, book.getTitle());
						insertBook.setString(3, book.getIsbn());
						insertBook.setInt(4,  book.getPublished());
						insertBook.addBatch();
					}
					insertBook.executeBatch();
					
					return true;
				} finally {
					DBUtil.closeQuietly(insertBook);
					DBUtil.closeQuietly(insertAuthor);
				}
			}
		});
	}
	
	// The main method creates the database tables and loads the initial data.
	public static void main(String[] args) throws IOException {
		System.out.println("Creating tables...");
		DerbyDatabase db = new DerbyDatabase();
		db.createTables();
		
		System.out.println("Loading initial data...");
		db.loadInitialData();
		
		System.out.println("Success!");
	}
}
