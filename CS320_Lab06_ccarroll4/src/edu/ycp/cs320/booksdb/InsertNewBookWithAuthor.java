package edu.ycp.cs320.booksdb;

import java.util.Scanner;

import edu.ycp.cs320.booksdb.persist.DatabaseProvider;
import edu.ycp.cs320.booksdb.persist.IDatabase;

public class InsertNewBookWithAuthor {
    public static void main(String[] args) throws Exception {
        Scanner keyboard = new Scanner(System.in);

        InitDatabase.init(keyboard);

        System.out.print("Enter the author's first name: ");
        String firstname = keyboard.nextLine();

        System.out.print("Enter the author's last name: ");
        String lastname = keyboard.nextLine();

        System.out.print("Enter the title of the book: ");
        String title = keyboard.nextLine();

        System.out.print("Enter the ISBN of the book: ");
        String isbn = keyboard.nextLine();

        System.out.print("Enter the publish year of the book: ");
        int published = keyboard.nextInt();

        // Get the DB instance and execute transaction
        IDatabase db = DatabaseProvider.getInstance();
        db.insertNewBookWithAuthor(firstname, lastname, title, isbn, published );

        System.out.println("Book inserted successfully.");
    }
}