import java.util.ArrayList;
import java.util.Arrays;
import java.sql.*;
import javax.swing.*;

public class Lab10Phan {
    private int memberID = -1;
    private String isbn = "";
    private Connection con = null;
    private Statement stmt;

    // Initialize connection with database
    private void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://faure/tonyphan", "tonyphan", "832030270");
            this.stmt = con.createStatement();
            System.out.println("Successfully connected to db");
        } catch (Exception e) {
            System.out.println("Error establishing connection to db" + e);
            exitProgram();
        }
    }

    private void reset() {
        this.memberID = -1;
        this.isbn = "";
    }

    private boolean validateName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if (Character.isLetter(name.charAt(i)))
                continue;
            System.out.println(
                    "Name values should only contain alphabetical letters (No numbers or special characters).");
            JOptionPane.showMessageDialog(null,
                    "Name values should only contain alphabetical letters (No numbers or special characters).");
            return false;
        }
        return true;
    }

    private boolean validateGender(String gender) {
        gender = gender.toUpperCase();
        char c = gender.charAt(0);
        if (gender.length() == 1 && Character.isLetter(c)
                && (c == 'M' || c == 'F'))
            return true;

        System.out.println("Gender values should be 'M' or 'F'");
        JOptionPane.showMessageDialog(null, "Gender values should be 'M' or 'F'");
        return false;
    }

    private boolean validateDate(String date) {
        String[] dateArr = date.split("-");
        // Checks lengths
        if ((dateArr.length == 3) && (dateArr[0].length() == 4) && (dateArr[1].length() == 2)
                && (dateArr[2].length() == 2)) {
            try {
                int year = Integer.parseInt(dateArr[0]);
                int month = Integer.parseInt(dateArr[1]);
                int day = Integer.parseInt(dateArr[2]);
                // Setting parameter bounds-- NOTE: does not check number of days in a month
                // (ex: february does not have 31 days) This can be changed if needed.
                if (year <= 9999 && month > 0 && month <= 12 && day > 0 && day <= 31)
                    return true;

            } catch (Exception e) {
                System.out.println("Date values should be integers");
                JOptionPane.showMessageDialog(null, "Date values should be integers");
                return false;
            }
        }
        System.out.println("Date values should have the format (YYYY-MM-DD)");
        JOptionPane.showMessageDialog(null, "Date values should have the format (YYYY-MM-DD)");
        return false;
    }

    private void exitProgram() {
        System.out.println("Exiting Program...");
        JOptionPane.showMessageDialog(null, "Exiting Program...");
        if (this.con != null) { // Close connection to db
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    private boolean validMember(int memberID) {
        // Returns true if member exists
        try {
            ResultSet rs = this.stmt.executeQuery(String.format("SELECT * FROM member WHERE member_id=%d", memberID));
            if (!rs.isBeforeFirst() ) {    
                return false;
            } 
            
            rs.next();
            System.out.printf("Welcome %s, %s!\n", rs.getString("last_name"), rs.getString("first_name"));
            JOptionPane.showMessageDialog(null, String.format("Welcome %s, %s!", rs.getString("last_name"), rs.getString("first_name")));
            
            return true;

        } catch (Exception e) {
            System.out.println(e + "\nShutting down.");
            JOptionPane.showMessageDialog(null, "Error while validating members.");
            exitProgram();
        } 
        return false;
    }

    private String[] getMemberInfo() {
        JTextField firstName = new JTextField();
        JTextField lastName = new JTextField();
        JTextField dob = new JTextField();
        JTextField gender = new JTextField();

        Object[] memberData = {
                "First Name", firstName,
                "Last Name", lastName,
                "Date of Birth (YYYY-MM-DD)", dob,
                "Gender (M/F)", gender
        };

        boolean isValid = false;
        String firstString = "";
        String lastString = "";
        String dobString = "";
        String genString = "";

        while (!isValid) {
            int option = JOptionPane.showConfirmDialog(null, memberData, "Member Info",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.CANCEL_OPTION)
                return null;

            firstString = firstName.getText();
            lastString = lastName.getText();
            dobString = dob.getText();
            genString = gender.getText();

            // Makes sure all input fields are given
            if (firstString.equals("") || lastString.equals("") || dobString.equals("") || genString.equals("")) {
                JOptionPane.showMessageDialog(null, "Make sure to fill out all of the fields.");
                continue;
            }
            // Ensure proper format of input
            if (!validateDate(dobString) || !validateName(firstString) || !validateName(lastString)
                    || !validateGender(genString))
                continue;
            isValid = true;
        }
        return new String[] { firstString, lastString, dobString, genString };
    }

    private boolean addMember(int memberID) {
        String[] memberData = getMemberInfo(); // Gets member Information
        if (memberData==null) return false;
        try {
            //member_id, first_name, last_name, dob, gender
            this.stmt.executeUpdate(String.format("INSERT into member values(%s, '%s', '%s', '%s', '%s')", memberID, memberData[0], memberData[1],
                memberData[2], memberData[3]));
            System.out.println("Successfully added member.");
            JOptionPane.showMessageDialog(null, String.format("Successfully added member.\nWelcome %s, %s!", memberData[1], memberData[0]));
            return true;
        } catch (Exception e) {
            System.out.println("Error creating new member.\n" + e);
            JOptionPane.showMessageDialog(null, "Error creating new member.");
            return false;
        }
    }

    private void checkMember() {
        String value = "";
        while (true) {
            try {
                System.out.println("Enter a member id: ");
                value = JOptionPane.showInputDialog(null, "Enter member id: ", value);
                if (value == null)
                    exitProgram();

                int memberID = Integer.parseInt(value);
                // IF NOT valid then ask to add
                if (!validMember(memberID)) {
                    int confirmation = JOptionPane.showConfirmDialog(null,
                            "The member id entered does not exist.\nDo you want to create a new member with the following ID?",
                            "Notification",
                            JOptionPane.YES_NO_OPTION);
                    if (confirmation == JOptionPane.YES_OPTION){
                        if (!addMember(memberID)) {
                            continue;
                        }
                    }
                    else {
                        continue;
                    }
                }
                this.memberID = memberID;
                return;

            } catch (Exception e) {
                System.out.println("Member ID's should only contain numeric values.");
                JOptionPane.showMessageDialog(null, "Error: Member ID's should only contain numeric values");
                continue;
            }
        }
    }

    // Method checks if author exist in system.
    private boolean checkAuthor(int authorID) {
        try {
            ResultSet rs = this.stmt.executeQuery(String.format("SELECT * FROM author WHERE author_id=%d", authorID));
            if (!rs.isBeforeFirst() ) {    
                return false;
            } 
            return true;
            
        } catch (Exception e) {
            System.out.println(e + "\nShutting down.");
            JOptionPane.showMessageDialog(null, "Error while verifying author. Shutting down.");
            exitProgram();
        } 
        return false;
    }

    private String authorBooks(int authorID) {
        // SQL QUERY HERE- GET ALL BOOKS -- RETURN ISBN OF CHOSEN BOOK
        ArrayList<String> isbnList = new ArrayList<String>();
        ArrayList<String> titleList = new ArrayList<String>();
        try {
            ResultSet rs = this.stmt.executeQuery(String.format("SELECT b.isbn, b.title FROM author_book ab INNER JOIN book b on ab.ISBN=b.ISBN WHERE ab.author_id=%s", authorID));
            if (!rs.isBeforeFirst() ) {
                System.out.println("This author does not have any books registered in the system.");
                JOptionPane.showMessageDialog(null, "This author does not have any books registered in the system.");
                return null;
            } 

            while (rs.next()) {
                isbnList.add(rs.getString("isbn"));
                titleList.add(rs.getString("title"));
            }

        } catch (Exception e) {
            System.out.println(e + "\nShutting down.");
            JOptionPane.showMessageDialog(null, "Error while querying author_book. Shutting down.");
            exitProgram();
        } 

        Object[] bookOptions= titleList.toArray();

        String input = (String) JOptionPane.showInputDialog(null, String.format("Books written by %s", authorID), "Choose a book",
                JOptionPane.QUESTION_MESSAGE, null, bookOptions, bookOptions[0]);

        if (input == null) // Cancel was pressed
            return null;

        return isbnList.get(titleList.indexOf(input)); // Return isbn of book chosen
    }

    private boolean searchByAuthor() {
        String response = "";
        while (true) {
            System.out.println("Enter a author id: ");
            response = JOptionPane.showInputDialog(null, "Enter author id: ", response);
            if (response == null) // Cancel button was pressed
                return false;

            try {
                int authorID = Integer.parseInt(response); // throw error if not int
                if (!checkAuthor(authorID)) {
                    System.out.println("This author does not exist in the system.");
                    JOptionPane.showMessageDialog(null, "This author does not exist in the system.");
                    continue;
                }

                String isbn = authorBooks(authorID);
                if (isbn == null)
                    continue;
                else if (response.equals("")) {
                    System.out.println("Author field must contain content.");
                    JOptionPane.showMessageDialog(null, "Author field must contain content.");
                    continue;
                }
                this.isbn = isbn;
                return true;

            } catch (Exception e) {
                System.out.println("Author ID's should only contain numeric values.");
                JOptionPane.showMessageDialog(null, "Error: Author ID's should only contain numeric values.");
                continue;
            }
        }
    }

    private String titleBooks(String title) {
        // SQL QUERY HERE- GET all books containing title -- RETURN ISBN OF CHOSEN BOOK
        ArrayList<String> isbnList = new ArrayList<String>();
        ArrayList<String> titleList = new ArrayList<String>();
        try {
            ResultSet rs = this.stmt.executeQuery(String.format("SELECT isbn, title FROM book WHERE title LIKE '%%%s%%'", title));
            if (!rs.isBeforeFirst() ) {
                System.out.println("There are no books matching that title in the system.");
                JOptionPane.showMessageDialog(null, "There are no books matching that title in the system.");
                return null;
            } 

            while (rs.next()) {
                isbnList.add(rs.getString("isbn"));
                titleList.add(rs.getString("title"));
            }

        } catch (Exception e) {
            System.out.println(e + "\nShutting down.");
            JOptionPane.showMessageDialog(null, "Error while querying by book title. Shutting down.");
            exitProgram();
        } 

        Object[] bookOptions= titleList.toArray();

        String input = (String) JOptionPane.showInputDialog(null, "Choose a book", "Choose a book",
                JOptionPane.QUESTION_MESSAGE, null, bookOptions, bookOptions[0]);

        if (input == null) // Cancel was pressed
            return null;

        return isbnList.get(titleList.indexOf(input)); // Returns ISBN of book
    }

    private boolean searchByTitle() {
        String response = "";
        while (true) {
            System.out.println("Search by Book Title");
            response = JOptionPane.showInputDialog(null, "Search by Book Title", response);
            if (response == null) // Cancel button was pressed
                return false;
            else if (response.equals("")) {
                System.out.println("Title field must contain content.");
                JOptionPane.showMessageDialog(null, "Title field must contain content.");
                continue;
            }

            String isbn = titleBooks(response);
            // System.out.println(isbn);
            if (isbn == null)
                continue;

            this.isbn = isbn;
            return true;
        }
    }


    private boolean checkISBN(String isbn) {
        String[] isbnArr = isbn.split("-");
        if (isbnArr.length != 3) {
            System.out.println("Invalid ISBN format. It should be (XX-XXXXX-XXXXX)");
            JOptionPane.showMessageDialog(null, "Invalid ISBN format. It should be (XX-XXXXX-XXXXX)");
            return false;
        } else if (isbnArr[0].length() != 2 || isbnArr[1].length() != 5 || isbnArr[2].length() != 5) {
            System.out.println("Invalid ISBN format. It should be (XX-XXXXX-XXXXX)");
            JOptionPane.showMessageDialog(null, "Invalid ISBN format. It should be (XX-XXXXX-XXXXX)");
            return false;
        }

        String dashless = isbn.replaceAll("-", "");
        for (int i = 0; i < dashless.length(); i++) {
            if (!Character.isDigit(dashless.charAt(i))) {
                System.out.println("ISBN values should only contain numeric values(0-9) and dashes (-)");
                JOptionPane.showMessageDialog(null,
                        "ISBN values should only contain numeric values(0-9) and dashes (-)");
                return false;
            }
        }

        try {
            ResultSet rs = this.stmt.executeQuery(String.format("SELECT ISBN, title FROM book WHERE ISBN='%s'", dashless));
            if (!rs.isBeforeFirst() ) {    
                System.out.println("This ISBN value does not exist in the system.");
                JOptionPane.showMessageDialog(null, "This ISBN value does not exist in the system.");
                return false;
            } 
            rs.next();
            System.out.printf("You chose: %s\n", rs.getString("title"));
            int confirmation = JOptionPane.showConfirmDialog(null, String.format("Is the following correct?\n\n%s\n", rs.getString("title")), "Confirmation",
                JOptionPane.YES_NO_OPTION);
                    if (confirmation == JOptionPane.YES_OPTION)
                        return true;
                    else
                        return false;
            
        } catch (Exception e) {
            System.out.println(e + "\nShutting down.");
            JOptionPane.showMessageDialog(null, "Error while finding book. Shutting down.");
            exitProgram();
        } 
        return false;
    }

    private boolean searchByISBN() {
        String response = "";
        while (true) {
            System.out.println("Search by ISBN (XX-XXXXX-XXXXX)");
            response = JOptionPane.showInputDialog(null, "Search by ISBN (XX-XXXXX-XXXXX)", response);
            if (response == null) // Cancel button was pressed
                return false;
            else if (response.equals("")) {
                System.out.println("ISBN field must contain content.");
                JOptionPane.showMessageDialog(null, "ISBN field must contain content.");
                continue;
            }
            if (!checkISBN(response))
                continue;

            this.isbn = response.replace("-", "");
            return true;
        }
    }

    private void searchBooks() {
        Object[] options = {
                "Search by Author", "Search by Title", "Search by ISBN", "Exit"
        };
        boolean success = false;

        while (!success) {
            int response = JOptionPane.showOptionDialog(null, "How would you like to search for a book?",
                    "Search for a book", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
                    null);

            // Switch statement should initialize this.isbn
            switch (response) {
                case 0: // Author
                    success = searchByAuthor();
                    if (!success)
                        continue;
                    break;
                case 1: // Title
                    success = searchByTitle();
                    if (!success)
                        continue;
                    break;
                case 2: // ISBN
                    success = searchByISBN();
                    if (!success)
                        continue;
                    break;
                case 3: // Exit
                    exitProgram();
            }
        }
    }

    private void checkLibraries() {
        // SQL QUERY USING this.isbn on library_book table
        try {
            ResultSet rs = this.stmt.executeQuery(String.format("SELECT library_name, floor, shelf, copies_available FROM library_book WHERE ISBN='%s'", this.isbn));
            if (!rs.isBeforeFirst() ) {    
                System.out.println("This book is currently not in stock at any library location.");
                JOptionPane.showMessageDialog(null, "This book is currently not in stock at any library location.");
                return;
            } 
            String results = "This book is available at: \n\n";
            boolean available = false;
            while (rs.next()) {
                if (rs.getInt("copies_available") <= 0) {
                    continue;
                }
                results += String.format("\t%s Library on Floor %s, Shelf %s (Copies Available: %s)\n\n", rs.getString("library_name"), rs.getString("floor"), rs.getString("shelf"), rs.getString("copies_available"));
                available = true;
            }

            if (available) {
                System.out.println(results);
                JOptionPane.showMessageDialog(null, results);
                return;
            } else {
                System.out.println("All copies of this book is already checked out.");
                JOptionPane.showMessageDialog(null, "All copies of this book is already checked out.");
                return;
            }
            
        } catch (Exception e) {
            System.out.println(e + "\nShutting down.");
            JOptionPane.showMessageDialog(null, "Error while checking libraries. Shutting down.");
            exitProgram();
        } 

    }

    public static void main(String[] args) {
        Lab10Phan lab10 = new Lab10Phan();
        lab10.createConnection(); // Used to initialize connection

        while (true) {
            lab10.checkMember();
            lab10.searchBooks();
            lab10.checkLibraries();
            lab10.reset();
        }
    }
}
