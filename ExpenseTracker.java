package com.tap;

import java.sql.*;
import java.util.Scanner;

public class ExpenseTracker {
	static final String DB_URL = "jdbc:mysql://localhost:3306/jdbc_project";
	static final String USER = "root";
	static final String PASS = "Tiger";

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
			while (true) {
				System.out.println("\n--- Personal Expense Tracker ---");
				System.out.println("1. Add Expense");
				System.out.println("2. View All Expenses");
				System.out.println("3. View Total Expense by Category");
				System.out.println("4. Exit");
				System.out.print("Choose an option: ");
				int choice = sc.nextInt();
				sc.nextLine();  // consume newline

				switch (choice) {
				case 1:
					addExpense(conn, sc);
					break;
				case 2:
					viewExpenses(conn);
					break;
				case 3:
					viewTotalByCategory(conn, sc);
					break;
				case 4:
					System.out.println("Exiting...");
					return;
				default:
					System.out.println("Invalid option. Try again.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static void addExpense(Connection conn, Scanner sc) throws SQLException {
		
		sc.nextLine();
		System.out.print("Enter date (YYYY-MM-DD): ");
		String date = sc.nextLine();

		System.out.print("Enter category (Food/Travel/Shopping/etc.): ");
		String category = sc.nextLine();

		System.out.print("Enter amount: ");
		double amount = sc.nextDouble();
		sc.nextLine();  // consume newline

		System.out.print("Enter description: ");
		String description = sc.nextLine();

		String sql = "INSERT INTO expenses (date, category, amount, description) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, date);
			stmt.setString(2, category);
			stmt.setDouble(3, amount);
			stmt.setString(4, description);
			stmt.executeUpdate();
			System.out.println("Expense added successfully.");
		}
	}

	static void viewExpenses(Connection conn) throws SQLException {
		String sql = "SELECT * FROM expenses";
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			System.out.printf("\n| %-5s | %-12s | %-15s | %-10s | %-30s |\n", "ID", "Date", "Category", "Amount", "Description");
			System.out.println("----------------------------------------------------------------------------------------");

			while (rs.next()) {
				System.out.printf("| %-5d | %-12s | %-15s | %-10.2f | %-30s |\n",
						rs.getInt("id"),
						rs.getDate("date"),
						rs.getString("category"),
						rs.getDouble("amount"),
						rs.getString("description"));
			}
		}
	}

	static void viewTotalByCategory(Connection conn, Scanner sc) throws SQLException {
		System.out.print("Enter category to view total expense: ");
		String category = sc.nextLine();

		String sql = "SELECT SUM(amount) AS total FROM expenses WHERE category = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, category);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				System.out.println("Total expense for " + category + ": ₹" + rs.getDouble("total"));
			} else {
				System.out.println("No expenses found for this category.");
			}
		}
	}
}


