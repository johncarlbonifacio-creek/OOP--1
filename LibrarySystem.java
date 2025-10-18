import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Person {
    String id;
    String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void displayInfo() {
        System.out.println("ID: " + id + ", Name: " + name);
    }
}

class User extends Person {
    String password;
    String role;
    ArrayList<String> borrowedBooks = new ArrayList<>();

    public User(String id, String name, String password, String role) {
        super(id, name);
        this.password = password;
        this.role = role;
    }

    @Override
    public void displayInfo() {
        System.out.println(id + " | " + name + " | " + role);
    }
}

class Book {
    String bookId;
    String title;
    String author;
    boolean available;

    public Book(String bookId, String title, String author, boolean available) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.available = available;
    }

    public void displayBookDetails() {
        System.out.println(bookId + " | " + title + " | " + author + " | " + (available ? "Available" : "Borrowed"));
    }
}

class Transaction {
    String transactionId;
    String userId;
    String bookId;
    String dateBorrowed;
    String dateReturned;

    public Transaction(String transactionId, String userId, String bookId, String dateBorrowed, String dateReturned) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
    }

    public void displayTransaction() {
        System.out.println(transactionId + " | " + userId + " | " + bookId + " | " + dateBorrowed + " | " + dateReturned);
    }
}

public class LibrarySystem {
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Book> books = new ArrayList<>();
    ArrayList<Transaction> transactions = new ArrayList<>();
    User loggedInUser = null;
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new LibrarySystem().run();
    }

    public void run() {
        System.out.println("Welcome to the Library Management System");
        System.out.println("-----------------------------------------");
        System.out.println("Please log in to continue.");

        try {
            loadFiles();
            login();
            if (loggedInUser != null) {
                if (loggedInUser.role.equals("admin")) adminMenu();
                else userMenu();
                saveFiles();
                System.out.println("All changes saved. Goodbye!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Missing data file.");
        } catch (IOException e) {
            System.out.println("Error reading file.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    void loadFiles() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("users.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");
            users.add(new User(p[0], p[1], p[2], p[3]));
        }
        br.close();

        br = new BufferedReader(new FileReader("books.txt"));
        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");
            books.add(new Book(p[0], p[1], p[2], Boolean.parseBoolean(p[3])));
        }
        br.close();

        br = new BufferedReader(new FileReader("transactions.txt"));
        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");
            transactions.add(new Transaction(p[0], p[1], p[2], p[3], p[4]));
        }
        br.close();
    }

    void saveFiles() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"));
        for (Book b : books) {
            bw.write(b.bookId + "," + b.title + "," + b.author + "," + b.available);
            bw.newLine();
        }
        bw.close();

        bw = new BufferedWriter(new FileWriter("users.txt"));
        for (User u : users) {
            bw.write(u.id + "," + u.name + "," + u.password + "," + u.role);
            bw.newLine();
        }
        bw.close();

        bw = new BufferedWriter(new FileWriter("transactions.txt"));
        for (Transaction t : transactions) {
            bw.write(t.transactionId + "," + t.userId + "," + t.bookId + "," + t.dateBorrowed + "," + t.dateReturned);
            bw.newLine();
        }
        bw.close();
    }

    void login() {
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("\nUsername: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();

            for (User u : users) {
                if (u.name.equals(username) && u.password.equals(password)) {
                    loggedInUser = u;
                    System.out.println("\nLogin successful! Welcome, " + u.name + ".");
                    return;
                }
            }

            attempts--;
            if (attempts == 0) {
                System.out.println("Too many failed attempts. Exiting...");
                System.exit(0);
            } else {
                System.out.println("Invalid username or password. Try again.");
                System.out.println("(Attempts left: " + attempts + ")");
            }
        }
    }

    void userMenu() {
        int choice = 0;
        while (choice != 4) {
            System.out.println("\n1. View All Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> viewBooks();
                case 2 -> borrowBook();
                case 3 -> returnBook();
                case 4 -> {}
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    void adminMenu() {
        int choice = 0;
        while (choice != 5) {
            System.out.println("\n1. View All Books");
            System.out.println("2. Users (Add, Update, Delete, Display, Exit)");
            System.out.println("3. Catalogue (Add, Update, Delete, Display, Exit)");
            System.out.println("4. Transactions (View All, View by User, View by Book, Exit)");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> viewBooks();
                case 2 -> manageUsers();
                case 3 -> manageBooks();
                case 4 -> displayTransactions();
                case 5 -> {}
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // --- USER MANAGEMENT (Admin) ---
    void manageUsers() {
        int choice = 0;
        while (choice != 5) {
            System.out.println("\n1. Add User");
            System.out.println("2. Update User");
            System.out.println("3. Delete User");
            System.out.println("4. Display Users");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> addUser();
                case 2 -> updateUser();
                case 3 -> deleteUser();
                case 4 -> displayUsers();
                case 5 -> {}
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    void addUser() {
        try {
            System.out.print("Enter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine();
            System.out.print("Enter Role (admin/user): ");
            String role = sc.nextLine();

            users.add(new User(id, name, pass, role));
            saveFiles();
            System.out.println("User added successfully!");
        } catch (IOException e) {
            System.out.println("Error saving new user.");
        }
    }

    void updateUser() {
        System.out.print("Enter User ID to update: ");
        String id = sc.nextLine();
        for (User u : users) {
            if (u.id.equals(id)) {
                System.out.print("New Name: ");
                u.name = sc.nextLine();
                System.out.print("New Password: ");
                u.password = sc.nextLine();
                System.out.print("New Role: ");
                u.role = sc.nextLine();
                try { saveFiles(); } catch (IOException e) {}
                System.out.println("User updated!");
                return;
            }
        }
        System.out.println("User not found.");
    }

    void deleteUser() {
        System.out.print("Enter User ID to delete: ");
        String id = sc.nextLine();
        users.removeIf(u -> u.id.equals(id));
        try { saveFiles(); } catch (IOException e) {}
        System.out.println("User deleted if found.");
    }

    void displayUsers() {
        System.out.println("\nUsers:");
        for (User u : users) {
            u.displayInfo();
        }
    }

    // --- BOOK MANAGEMENT (Admin) ---
    void manageBooks() {
        int choice = 0;
        while (choice != 5) {
            System.out.println("\n1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Display Catalogue");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> addBook();
                case 2 -> updateBook();
                case 3 -> deleteBook();
                case 4 -> viewBooks();
                case 5 -> {}
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    void addBook() {
        try {
            System.out.print("Enter Book ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();

            books.add(new Book(id, title, author, true));
            saveFiles();
            System.out.println("Book added successfully!");
        } catch (IOException e) {
            System.out.println("Error saving book.");
        }
    }

    void updateBook() {
        System.out.print("Enter Book ID to update: ");
        String id = sc.nextLine();
        for (Book b : books) {
            if (b.bookId.equals(id)) {
                System.out.print("New Title: ");
                b.title = sc.nextLine();
                System.out.print("New Author: ");
                b.author = sc.nextLine();
                try { saveFiles(); } catch (IOException e) {}
                System.out.println("Book updated!");
                return;
            }
        }
        System.out.println("Book not found.");
    }

    void deleteBook() {
        System.out.print("Enter Book ID to delete: ");
        String id = sc.nextLine();
        books.removeIf(b -> b.bookId.equals(id));
        try { saveFiles(); } catch (IOException e) {}
        System.out.println("Book deleted if found.");
    }

    // --- COMMON FEATURES ---
    void viewBooks() {
        System.out.println("\nBook List:");
        for (Book b : books) {
            b.displayBookDetails();
        }
    }

    void borrowBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine();
        for (Book b : books) {
            if (b.bookId.equals(id) && b.available) {
                b.available = false;
                System.out.println("Book borrowed successfully!");
                try { saveFiles(); } catch (IOException e) {}
                return;
            }
        }
        System.out.println("Book not available or not found.");
    }

    void returnBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine();
        for (Book b : books) {
            if (b.bookId.equals(id) && !b.available) {
                b.available = true;
                System.out.println("Book returned successfully!");
                try { saveFiles(); } catch (IOException e) {}
                return;
            }
        }
        System.out.println("Book not found or already available.");
    }

    void displayTransactions() {
        System.out.println("\nTransactions:");
        for (Transaction t : transactions) {
            t.displayTransaction();
        }
    }
}
