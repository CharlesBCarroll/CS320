package edu.ycp.cs320.booksdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

import edu.ycp.cs320.sqldemo.DBUtil;

public class InsertNewBookWithAuthor {
	public static void main(String[] args) throws Exception {
		// load Derby JDBC driver
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			System.err.println("Could not load Derby JDBC driver");
			System.err.println(e.getMessage());
			System.exit(1);
		}

		// define the references for accessing the DB and the query results
		// DB connection
		Connection conn = null;
		
		// PreparedStatement references - one for each of the queries / inserts 
		PreparedStatement stmtAuthorID1    = null;
		PreparedStatement stmtAuthorID2    = null;		
		PreparedStatement stmtInsertAuthor = null;
		PreparedStatement stmtInsertBook   = null;
		PreparedStatement stmtCheckResults = null;
		
		// ResultSet references - one for each query
		ResultSet resultSetAuthorID1    = null;
		ResultSet resultSetAuthorID2    = null;
		ResultSet resultSetCheckResults = null;
		
		// define variables for accepting user input
		String  authorFirstName = null;
		String  authorLastName  = null;
		String  bookTitle       = null;		
		String  bookISBN        = null;
		Integer bookPublished   = null;				

		// now connect to the database
		// the DB name is "test.db"
		// in this case, it is expected to be located at the same level as the project sub-folders
		// create the DB, if it doesn't already exist - but that won't create the schema, or populate the tables
		//you can do that by running DerbyDatabase.java as an Application
		conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");

		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);

		// prompt user for all information for new book
		// this could include a new author
		try {
			System.out.println("Enter new book information:");
			System.out.print("   Author first name: ");			
			authorFirstName = keyboard.nextLine();

			System.out.print("   Author last name: ");
			authorLastName = keyboard.nextLine();
			
			System.out.print("   Book title: ");
			bookTitle = keyboard.nextLine();			

			System.out.print("   Book ISBN: ");
			bookISBN = keyboard.nextLine();
			
			System.out.print("   Book year published: ");
			bookPublished = keyboard.nextInt();			
			
			// to insert the new book, we have to have an author ID
			// first check if the author exists, and if so, retrieve their author_ID
			// if this is a book by a new author, insert the author into AUTHORS table first
			// then, retrieve their author ID (auto-generated for AUTHORS table by the DB)
			// finally, now that we have the author ID (for an existing or new author),
			// insert the new book, with that author ID (it is a foreign key in the BOOKS table)
			stmtAuthorID1 = conn.prepareStatement(
					"select authors.author_id "
					+ "  from authors "
					+ "  where authors.firstname = ? and authors.lastname = ? "
			);

			// substitute the author's first name entered by the user for the 1st placeholder
			stmtAuthorID1.setString(1, authorFirstName);

			// substitute the author's last name entered by the user for the 2nd placeholder
			stmtAuthorID1.setString(2, authorLastName);
			
			// execute the query
			resultSetAuthorID1 = stmtAuthorID1.executeQuery();

			// get the precise schema of the tuples returned as the result of the query
			// there should only be a single row (assuming author names are unique in this DB)
			// with a single attribute (author_id)
			ResultSetMetaData resultSchema = stmtAuthorID1.getMetaData();
			
			// we'll use this to save the retrieved author ID (initialize to an out-of-range value)
			int authorID = -1;
			
			// check if an author ID was returned from the query
			if (resultSetAuthorID1.next()) {
				
				// found the author
				// get their author ID from the 1st (and only) attribute, index 1
				// authorID is an integer, retrieve it as an Integer from the result set, first index
				authorID = resultSetAuthorID1.getInt(1);
				
				System.out.println("Existing author found in AUTHORS table\n");
			}
			else
			{
				// new author, need to insert into the AUTHORS table
				System.out.println("Inserting new author into AUTHORS table\n");
				
				// otherwise, insert new author into the AUTHORS table
				stmtInsertAuthor = conn.prepareStatement(
						"insert into authors (lastname, firstname) "
						+ "values (?, ?)"
				);
				
				// substitute the author's last name entered by the user for the 1st placeholder
				stmtInsertAuthor.setString(1, authorLastName);
				
				// substitute the author's last name entered by the user for the 2nd placeholder
				stmtInsertAuthor.setString(2, authorFirstName);				
				
				// execute the DB update (this is not a query, but a change to the AUTHORS table in the DB)
				stmtInsertAuthor.executeUpdate();

				// the author should now exist, retrieve author_ID
				// it was auto-generated by the DB as a primary key in the AUTHORS table
				stmtAuthorID2 = conn.prepareStatement(
						"select authors.author_id "
								+ "  from authors "
								+ "  where authors.firstname = ? and authors.lastname = ? "
				);

				// substitute the author's first name entered by the user for the 1st placeholder
				stmtAuthorID2.setString(1, authorFirstName);

				// substitute the author's last name entered by the user for the 2nd placeholder
				stmtAuthorID2.setString(2, authorLastName);
				
				// execute the query
				resultSetAuthorID2 = stmtAuthorID2.executeQuery();

				// get the precise schema of the tuples returned as the result of the query
				// there should only be a single row (assuming author names are unique in this DB)
				// with a single attribute (author_id)
				resultSchema = stmtAuthorID2.getMetaData();
				
				// check if an author was returned from the query
				if (resultSetAuthorID2.next()) {
					
					// get new author's ID (as an Integer) from the first (and only) attribute, index 1
					authorID = resultSetAuthorID2.getInt(1);
					
					System.out.println("New author inserted into AUTHORS table with author_ID: " + authorID + "\n");
				}
				else {
					
					// we really shouldn't ever hit this, and it could be handled as an exception
					System.out.println("Something very bad has happened - the new author was not found in the AUTHORS table");
				}
			}
			
			// now we can insert the new book into the BOOKS table
			stmtInsertBook = conn.prepareStatement(
					"insert into books (author_id, title, ISBN, published) "
					+ "  values (?, ?, ?, ?)"
			);
			
			// substitute the author's author ID for the 1st placeholder (as in Integer)
			stmtInsertBook.setInt(1, authorID);
			
			// substitute the book title entered by the user for the 2nd placeholder (as a String)
			stmtInsertBook.setString(2, bookTitle);
			
			// substitute the book's ISBN entered by the user for the 3rd placeholder (as a String)
			stmtInsertBook.setString(3, bookISBN);
			
			// substitute the book's publish year entered by the use for the 4th placeholder (as an Integer)
			stmtInsertBook.setInt(4, bookPublished);
			
			// execute the DB update (this is not a query, but a change to the BOOKS table in the DB)
			stmtInsertBook.executeUpdate();
			
			System.out.println("New book inserted into BOOKS table with title <" + bookTitle + 
					           "> for author " + authorFirstName + " " + authorLastName + "\n");			
						
			// finally, check to see that the book was inserted into the BOOKS table
			// by retrieving all of the author and book attributes for all authors
			stmtCheckResults = conn.prepareStatement(
					"select * "
					+ "  from authors, books "
					+ "  where authors.author_id = books.author_id"
			);

			// execute the query
			resultSetCheckResults = stmtCheckResults.executeQuery();
			
			// get the precise schema of the tuples returned as the result of the query
			// that will be (author_ID, lastname, firstname, book_id, author_id, title, ISBN, published)
			resultSchema = stmtCheckResults.getMetaData();

			// count # of rows returned
			int rowsReturned = 0;
			
			// iterate through the returned tuples, printing each one
			// the while loop runs through the rows
			while (resultSetCheckResults.next()) {
				
				// the for loop runs through the attributes in the row
				// notice that the loop is '1' indexed, not '0' indexed
				for (int i = 1; i <= resultSchema.getColumnCount(); i++) {

					// each attribute is requested as an Object,
					// but you could also request by type: getInt(), getString(), if you know the type					
					Object obj = resultSetCheckResults.getObject(i);
					
					// this just inserts commas in between the attributes (if there are multiple attributes)
					if (i > 1) {
						System.out.print(",");
					}

					// this assumes that each Object has a toString() method defined
					System.out.print(obj.toString());
				}
				
				// terminate the line that 
				System.out.println();
				
				// count # of rows returned
				rowsReturned++;				
			}
			
			// indicate if the query returned nothing
			if (rowsReturned == 0) {
				System.out.println("No books were found in the DB - that's really bad...!!");
			}						
		} finally {
			
			// close ResultSets
			DBUtil.closeQuietly(resultSetAuthorID1);
			DBUtil.closeQuietly(resultSetAuthorID2);
			DBUtil.closeQuietly(resultSetCheckResults);
			
			// close PreparedStatements
			DBUtil.closeQuietly(stmtAuthorID1);
			DBUtil.closeQuietly(stmtAuthorID2);		
			DBUtil.closeQuietly(stmtInsertAuthor);
			DBUtil.closeQuietly(stmtInsertBook);
			DBUtil.closeQuietly(stmtCheckResults);
			
			// close DB Connection
			DBUtil.closeQuietly(conn);

			// close Keyboard
			keyboard.close();
		}
	}
}
