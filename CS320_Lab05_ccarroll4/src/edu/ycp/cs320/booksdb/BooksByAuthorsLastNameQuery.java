package edu.ycp.cs320.booksdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

import edu.ycp.cs320.sqldemo.DBUtil;

public class BooksByAuthorsLastNameQuery {
	public static void main(String[] args) throws Exception{
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) { 
			System.err.println("Could not load Derby JDBC driver");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultset = null;
		
		conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");

		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);

		try {
			conn.setAutoCommit(true);
			
			System.out.print("Author's last name: ");
			String lastname = keyboard.nextLine();
			
			stmt = conn.prepareStatement(
					"select authors.lastname, authors.firstname, books.title, books.isbn, books.published "
							+ "  from authors, books "
							+ "  where authors.author_id = books.author_id "
							+ "  and authors.lastname = ? "	
							+ "  order by books.title ASC"
					);
			stmt.setString(1, lastname);
			resultset = stmt.executeQuery();
			
			ResultSetMetaData resultSchema = stmt.getMetaData();
			
			int rowsReturned = 0;
			
			while (resultset.next()) {
				for (int i = 1; i <= resultSchema.getColumnCount(); i++) {
					Object obj = resultset.getObject(i);
					if (i > 1) {
						System.out.print(",");
					}
					System.out.print(obj.toString());
				}
				System.out.println();
				
				rowsReturned++;
			}
			if (rowsReturned == 0) {
				System.out.println("No rows returned that matched the query");
			}
		} finally {
			// close result set, statement, connection
			DBUtil.closeQuietly(resultset);
			DBUtil.closeQuietly(stmt);
			DBUtil.closeQuietly(conn);
		}
	}
}