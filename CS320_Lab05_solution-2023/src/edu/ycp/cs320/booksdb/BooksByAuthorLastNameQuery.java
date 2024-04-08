package edu.ycp.cs320.booksdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

import edu.ycp.cs320.sqldemo.DBUtil;

public class BooksByAuthorLastNameQuery {
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
		Connection conn        = null;
		PreparedStatement stmt = null;
		ResultSet resultSet    = null;
		
		// define the variables for accepting user input
		String authorLastName  = null;

		// connect to the database
		// the DB name is "test.db"
		// in this case, it is expected to be located at the same level as the project sub-folders
		// create the DB, if it doesn't already exist - but that won't create the schema, or populate the tables
		// you can do that by running DerbyDatabase.java as an Application
		conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");

		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);

		// Modified from TitleQuery to issue BooksByAuthor query
		// prompt user for author's last name for the query
		try {
			System.out.print("Find books by author (last name): ");
			authorLastName = keyboard.nextLine();
			
			// a canned query to find book information (including author name) from author's last name
			// return results in ascending order by title
			stmt = conn.prepareStatement(
					"select authors.lastname, authors.firstname, books.title, books.isbn, books.published "
					+ "  from authors, books "
					+ "  where authors.author_id = books.author_id and "
					+ "        authors.lastname = ? "
					+ "  order by title ASC"
			);

			// substitute the author's last name entered by the user for the 1st placeholder
			stmt.setString(1, authorLastName);

			// execute the query
			resultSet = stmt.executeQuery();

			// get the precise schema of the tuples returned as the result of the query
			// that will be (lastname, firstname, title, ISBN, published)
			ResultSetMetaData resultSchema = stmt.getMetaData();
			
			// count # of rows returned
			int rowsReturned = 0;
			
			// iterate through the returned tuples, printing each one
			// the while loop runs through the rows
			while (resultSet.next()) {
				
				// the for loop runs through the attributes in the row
				// notice that the loop is '1' indexed, not '0' indexed
				for (int i = 1; i <= resultSchema.getColumnCount(); i++) {

					// each attribute is requested as an Object,
					// but you could also request by type: getInt(), getString(), if you know the type					
					Object obj = resultSet.getObject(i);
					
					// this just inserts commas in between the attributes (if there are multiple attributes)
					if (i > 1) {
						System.out.print(",");
					}

					// this assumes that each Object has a toString() method defined
					System.out.print(obj.toString());
				}
				
				// terminate the line for this row
				System.out.println();
				
				// count # of rows returned
				rowsReturned++;				
			}
			
			// indicate if the query returned nothing
			if (rowsReturned == 0) {
				System.out.println("No books were found for author " + authorLastName);
			}			
			
		} finally {
			// close result set, statement, connection, keyboard
			DBUtil.closeQuietly(resultSet);
			DBUtil.closeQuietly(stmt);
			DBUtil.closeQuietly(conn);
			keyboard.close();
		}
	}
}
