package bookReserve;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class ReservationSystemBootstrap {

	private static Connection sqlConnection;
	
	private static String createBook = "CREATE TABLE \"book\" (\n"
			+ "	\"book_id\"	INTEGER NOT NULL,\n"
			+ "	\"title\"	TEXT NOT NULL,\n"
			+ "	\"author\"	TEXT NOT NULL,\n"
			+ "	\"year\"	INTEGER NOT NULL,\n"
			+ "	\"number_available\"	INTEGER NOT NULL,\n"
			+ "	PRIMARY KEY(\"book_id\")\n"
			+ ")";
	
	private static String createUser ="CREATE TABLE \"user\" (\n"
			+ "	\"user_id\"	INTEGER NOT NULL,\n"
			+ "	\"username\"	TEXT NOT NULL UNIQUE,\n"
			+ "	\"password_sha1\"	TEXT NOT NULL,\n"
			+ "	\"name\"	TEXT NOT NULL,\n"
			+ "	\"address\"	TEXT NOT NULL,\n"
			+ "	\"phone\"	TEXT NOT NULL,\n"
			+ "	\"email\"	TEXT NOT NULL,\n"
			+ "	\"is_admin\"	INTEGER NOT NULL DEFAULT 0,\n"
			+ "	UNIQUE(\"username\"),\n"
			+ "	PRIMARY KEY(\"user_id\")\n"
			+ ")";
	
	private static String createBookReservation= "CREATE TABLE \"book_reservation\" (\n"
			+ "	\"user_id\"	INTEGER NOT NULL,\n"
			+ "	\"book_id\"	INTEGER NOT NULL,\n"
			+ "	\"start_date\"	INTEGER NOT NULL,\n"
			+ "	\"due_date\"	INTEGER NOT NULL,\n"
			+ "	PRIMARY KEY(\"user_id\",\"book_id\"),\n"
			+ "	FOREIGN KEY(\"user_id\") REFERENCES \"user\"(\"user_id\"),\n"
			+ "	FOREIGN KEY(\"book_id\") REFERENCES \"book\"(\"book_id\")\n"
			+ ")";

	private static String insertAdmin = "INSERT INTO user (username, password_sha1, name, address, phone, email, is_admin) VALUES ( 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'admin', 'admin', '12346567', 'admin@admin.edu', 1);";
	private static boolean tableExists(String tableName) throws SQLException {
		DatabaseMetaData md = sqlConnection.getMetaData();
		ResultSet rs = md.getTables(null, null, tableName, null);
		return rs.next();
	}

	private static void initializeConnection() throws SQLException {
		// create a database connection
		sqlConnection = DriverManager.getConnection("jdbc:sqlite:library.db");
		Statement statement = sqlConnection.createStatement();

		if (!tableExists("user")) {
			statement.executeUpdate(createUser);
			statement.executeUpdate(insertAdmin);
		}
		
		if (!tableExists("book")) {
			statement.executeUpdate(createBook);
		}
		
		if (!tableExists("book_reservation")) {
			statement.executeUpdate(createBookReservation);
		}
	}


	public static void main(String[] args) {
		LoginPage login = new LoginPage();
		login.setVisible(true);
		try {
			initializeConnection();
			login.setConnection(sqlConnection);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(login, e.getMessage(), "Could not open database", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

}
