import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

enum Role {
    LIBRARIAN,
    MEMBER
}

class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
    private String borrowedByUserId;

    public Book(String isbn, String title, String author, String borrowedByUserId) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.borrowedByUserId = borrowedByUserId;
    }

    // Getters and setters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getBorrowedByUserId() { return borrowedByUserId; }
    public void setBorrowedByUserId(String userId) { this.borrowedByUserId = userId; }
}

class User implements Serializable {
    private String userId;
    private String name;
    private Role role;

    public User(String userId, String name, Role role) {
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public Role getRole() { return role; }
}

class DataHandler {
    private static final String BOOKS_FILE = "books.dat";
    private static final String USERS_FILE = "users.dat";

    public static List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                books = (List<Book>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading books: " + e.getMessage());
            }
        }
        return books;
    }

    public static void saveBooks(List<Book> books) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKS_FILE))) {
            oos.writeObject(books);
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading users: " + e.getMessage());
            }
        }

        if (users.isEmpty()) {
            User admin = new User("admin", "Administrator", Role.LIBRARIAN);
            users.add(admin);
            saveUsers(users);
        }
        return users;
    }

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}

public class LibraryManagementSystem {
    private static List<Book> books;
    private static List<User> users;
    private static User currentUser;

    public static void main(String[] args) {
        books = DataHandler.loadBooks();
        users = DataHandler.loadUsers();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter user ID: ");
            String userId = scanner.nextLine();
            currentUser = users.stream()
                    .filter(u -> u.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);

            if (currentUser != null) {
                System.out.println("Welcome, " + currentUser.getName() + "!");
                break;
            }
            System.out.println("User not found. Try again.");
        }

        if (currentUser.getRole() == Role.LIBRARIAN) {
            showLibrarianMenu(scanner);
        } else {
            showMemberMenu(scanner);
        }

        DataHandler.saveBooks(books);
        DataHandler.saveUsers(users);
    }

    private static void showLibrarianMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nLibrarian Menu:");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. List All Books");
            System.out.println("4. List Available Books");
            System.out.println("5. Add User");
            System.out.println("6. List Users");
            System.out.println("7. Search Books");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addBook(scanner); break;
                case 2: removeBook(scanner); break;
                case 3: listAllBooks(); break;
                case 4: listAvailableBooks(); break;
                case 5: addUser(scanner); break;
                case 6: listUsers(); break;
                case 7: searchBooks(scanner); break;
                case 8: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void showMemberMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nMember Menu:");
            System.out.println("1. Search Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. List Available Books");
            System.out.println("5. My Borrowed Books");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: searchBooks(scanner); break;
                case 2: borrowBook(scanner); break;
                case 3: returnBook(scanner); break;
                case 4: listAvailableBooks(); break;
                case 5: listMyBorrowedBooks(); break;
                case 6: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void addBook(Scanner scanner) {
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        if (books.stream().anyMatch(b -> b.getIsbn().equals(isbn))) {
            System.out.println("Book with this ISBN already exists!");
            return;
        }

        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();

        books.add(new Book(isbn, title, author, null));
        System.out.println("Book added successfully!");
    }

    private static void removeBook(Scanner scanner) {
        System.out.print("Enter ISBN to remove: ");
        String isbn = scanner.nextLine();
        boolean removed = books.removeIf(b -> b.getIsbn().equals(isbn));
        System.out.println(removed ? "Book removed!" : "Book not found!");
    }

    private static void listAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        books.forEach(b -> System.out.println(
                b.getTitle() + " by " + b.getAuthor() + " (ISBN: " + b.getIsbn() + ") - " +
                        (b.getBorrowedByUserId() == null ? "Available" : "Checked Out")
        ));
    }

    private static void listAvailableBooks() {
        List<Book> available = books.stream()
                .filter(b -> b.getBorrowedByUserId() == null)
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("No available books.");
            return;
        }
        available.forEach(b -> System.out.println(
                b.getTitle() + " by " + b.getAuthor() + " (ISBN: " + b.getIsbn() + ")"
        ));
    }

    private static void addUser(Scanner scanner) {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        if (users.stream().anyMatch(u -> u.getUserId().equals(userId))) {
            System.out.println("User ID already exists!");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter role (LIBRARIAN/MEMBER): ");
        try {
            Role role = Role.valueOf(scanner.nextLine().toUpperCase());
            users.add(new User(userId, name, role));
            System.out.println("User added successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role. User not added.");
        }
    }

    private static void listUsers() {
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        users.forEach(u -> System.out.println(
                u.getUserId() + ": " + u.getName() + " (" + u.getRole() + ")"
        ));
    }

    private static void searchBooks(Scanner scanner) {
        System.out.print("Enter search term: ");
        String term = scanner.nextLine().toLowerCase();

        List<Book> results = books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(term) ||
                        b.getAuthor().toLowerCase().contains(term) ||
                        b.getIsbn().contains(term))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        results.forEach(b -> System.out.println(
                b.getTitle() + " by " + b.getAuthor() + " (ISBN: " + b.getIsbn() + ") - " +
                        (b.getBorrowedByUserId() == null ? "Available" : "Checked Out")
        ));
    }

    private static void borrowBook(Scanner scanner) {
        System.out.print("Enter ISBN to borrow: ");
        String isbn = scanner.nextLine();

        Book book = books.stream()
                .filter(b -> b.getIsbn().equals(isbn) && b.getBorrowedByUserId() == null)
                .findFirst()
                .orElse(null);

        if (book == null) {
            System.out.println("Book not available or invalid ISBN.");
            return;
        }

        book.setBorrowedByUserId(currentUser.getUserId());
        System.out.println("Book borrowed successfully!");
    }

    private static void returnBook(Scanner scanner) {
        System.out.print("Enter ISBN to return: ");
        String isbn = scanner.nextLine();

        Book book = books.stream()
                .filter(b -> b.getIsbn().equals(isbn) &&
                        currentUser.getUserId().equals(b.getBorrowedByUserId()))
                .findFirst()
                .orElse(null);

        if (book == null) {
            System.out.println("Book not found or not checked out by you.");
            return;
        }

        book.setBorrowedByUserId(null);
        System.out.println("Book returned successfully!");
    }

    private static void listMyBorrowedBooks() {
        List<Book> borrowed = books.stream()
                .filter(b -> currentUser.getUserId().equals(b.getBorrowedByUserId()))
                .collect(Collectors.toList());

        if (borrowed.isEmpty()) {
            System.out.println("You have no borrowed books.");
            return;
        }
        borrowed.forEach(b -> System.out.println(
                b.getTitle() + " by " + b.getAuthor() + " (ISBN: " + b.getIsbn() + ")"
        ));
    }
}