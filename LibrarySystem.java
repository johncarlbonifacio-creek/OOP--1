import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibrarySystem {
    private List<Book> books = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private User loggedInUser;

    public static void main(String[] args) {
        new LibrarySystem().displayMenu();
    }

    public LibrarySystem() {
        loadUsers();
        loadBooks();
        loadTransactions();
    }

    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                users.add(new User(data[0], data[1], data[2], data[3]));
            }
        } catch (IOException e) {
            System.out.println("Error loading users.txt");
        }
    }

    private void loadBooks() {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                books.add(new Book(data[0], data[1], data[2], Boolean.parseBoolean(data[3])));
            }
        } catch (IOException e) {
            System.out.println("Error loading books.txt");
        }
    }

    private void loadTransactions() {
        try (BufferedReader br = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                transactions.add(new Transaction(data[0], data[1], data[2], data[3], data[4]));
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions.txt");
        }
    }

    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the Library Management System");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        loggedInUser = authenticate(username, password);

        if (loggedInUser == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        System.out.println("Login successful! Welcome, " + loggedInUser.name);

        int choice;
        do {
            System.out.println("\n1. View All Books\n2. Borrow Book\n3. Return Book\n4. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewAllBooks();
                case 2 -> borrowBook(sc);
                case 3 -> returnBook(sc);
                case 4 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 4);
    }

    private User authenticate(String username, String password) {
        for (User u : users) {
            if (u.name.equals(username) && u.getPassword().equals(password))
                return u;
        }
        return null;
    }

    private void viewAllBooks() {
        for (Book b : books) {
            b.displayBookDetails();
        }
    }

    private void borrowBook(Scanner sc) {
        System.out.print("Enter Book ID: ");
        String bookId = sc.nextLine();
        for (Book b : books) {
            if (b.getBookId().equals(bookId) && b.isAvailable()) {
                b.setAvailable(false);
                System.out.println("Book borrowed successfully!");
                return;
            }
        }
        System.out.println("Book not available.");
    }

    private void returnBook(Scanner sc) {
        System.out.print("Enter Book ID: ");
        String bookId = sc.nextLine();
        for (Book b : books) {
            if (b.getBookId().equals(bookId) && !b.isAvailable()) {
                b.setAvailable(true);
                System.out.println("Book returned successfully!");
                return;
            }
        }
        System.out.println("Book not found or already returned.");
    }
                  }
