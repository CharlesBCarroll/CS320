package edu.ycp.cs320.booksdb;

import java.sql.Connection;



import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		PreparedStatement stmt4 = null;
		ResultSet resultSet = null;
		ResultSet resultSet2 = null;
		
		
		conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);

		try {
			conn.setAutoCommit(true);
			
			System.out.print("Author's first name: ");
			String firstName = keyboard.nextLine();
			
			System.out.print("Author's last name: ");
			String lastName = keyboard.nextLine();
			
			System.out.print("Title: ");
			String title = keyboard.nextLine();
			
			System.out.print("ISBN: ");
			String isbn = keyboard.nextLine();
			
			System.out.print("Year Published: ");
			String yearPublished = keyboard.nextLine();
			
			stmt = conn.prepareStatement(
					"select author_id "
						+ "  from authors "
						+ "  where firstname = ? "
						+ "  and lastname = ?"
					);
			
			stmt.setString(1, firstName);
			stmt.setString(2, lastName);
			resultSet = stmt.executeQuery();
			
			
			int authorID = -1;
			if(resultSet.next()) {
				authorID = resultSet.getInt("author_id");
			}
			else {
				stmt2 = conn.prepareStatement(
						"Insert into authors (firstname, lastname)"
						+"values (?, ?)"
						);
				stmt2.setString(1, firstName);
				stmt2.setString(2,lastName);
				stmt2.executeUpdate();
				
				stmt3 = conn.prepareStatement(
						"select author_id "
								+ "  from authors "
								+ "  where firstname = ? "
								+ "  and lastname = ?"
						);
				stmt3.setString(1, firstName);
				stmt3.setString(2,lastName);
				resultSet2 = stmt3.executeQuery();
				if(resultSet2.next()) {
					authorID = resultSet2.getInt("author_id");
				}else {
					throw new RuntimeException("Failed to retrieve author ID for newly inserted author");
				}
		
			}			
			stmt4 = conn.prepareStatement(
					"insert into books(author_id, title, isbn, published)"
					+"values (?,?,?,?)"
					);
			stmt4.setInt(1, authorID);
			stmt4.setString(2, title);
			stmt4.setString(3, isbn);
			stmt4.setString(4, yearPublished);
			stmt4.executeUpdate();
			
			System.out.print("Book was inserted into database");
		}catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
		}finally {
			// close result set, statement, connection
			DBUtil.closeQuietly(resultSet);
			DBUtil.closeQuietly(resultSet2);
			DBUtil.closeQuietly(stmt);
			DBUtil.closeQuietly(stmt2);
			DBUtil.closeQuietly(stmt3);
			DBUtil.closeQuietly(stmt4);
			DBUtil.closeQuietly(conn);
			
		}
	}
}