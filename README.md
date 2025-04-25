# Library Management System 📚
A console-based Library Management System implementing core Java concepts for managing book circulation, user roles, and persistent data storage.

## Features ✨
- **User Roles**: Librarian (Admin) and Member access levels
- **Book Management**: Add/remove books, search, and view availability
- **Transactions**: Book issuing and returning system
- **Persistence**: Data saved between sessions using serialization
- **Search System**: Find books by title, author, or ISBN
- **Modular Design**: OOP principles with separated concerns

## Installation ⚙️
1. **Requirements**:
   - JDK 17 or higher
   - Git (optional)

2. **Clone & Run**:

git clone https://github.com/your-username/Library-Management-System.git
cd Library-Management-System
javac LibraryManagementSystem.java
java LibraryManagementSystem


Usage 🖥️
Default Admin Credentials:
User ID: admin

Librarian Privileges:
Add/remove books and users
View all books and users
Search complete inventory

Member Privileges:
Borrow/return books
Search available books
View personal borrowing history

File Structure 📂 :

├── LibraryManagementSystem.java  # Main application
├── Book.java                     # Book entity class
├── User.java                     # User entity class
├── DataHandler.java              # Data persistence logic
├── books.dat                     # Book database (auto-generated)
├── users.dat                     # User database (auto-generated)
└── README.md                     # This documentation

Tech Stack 💻 :
Core Java (JDK 17)
OOP Concepts: Encapsulation, Inheritance, Polymorphism
File Handling: Object Serialization/Deserialization
Collections Framework: ArrayList, Stream API
