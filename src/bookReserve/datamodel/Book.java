package bookReserve.datamodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Book {

	private final static String UPDATE_STOCK = "update book set number_available = ? where book_id =?";
	private final static String GET_STOCK = "select number_available from book where book_id =?";
	private final static String SEARCH = "select * from book where title like ? and author like ? LIMIT 100";
	private final static String CREATE = "insert into book (title, author, year, number_available) values (?,?,? ,?)";
	private final static String RESERVE = "insert into book_reservation (user_id, book_id, start_date, due_date) values (?,?,? ,?)";

	public Book(int id, String title, String author, int year, int numberAvailable) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.year = year;
		this.numberAvailable = numberAvailable;
	}

	public Book(String title, String author, int year, int numberAvailable) {
		this.title = title;
		this.author = author;
		this.year = year;
		this.numberAvailable = numberAvailable;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumberAvailable() {
		return numberAvailable;
	}

	public void setNumberAvailable(int numberAvailable) {
		this.numberAvailable = numberAvailable;
	}

	public int getId() {
		return id;
	}

	private int id = -1;
	private String title;
	private String author;
	private int year;
	private int numberAvailable;

	public void create(Connection sqlConnection) throws SQLException {
		PreparedStatement statement = sqlConnection.prepareStatement(CREATE);
		statement.setString(1, this.title);
		statement.setString(2, this.author);
		statement.setInt(3, this.year);
		statement.setInt(4, this.numberAvailable);
		statement.executeUpdate();
	}

	public void addStock(Connection sqlConnection, int numberNewBooks) throws SQLException {
		PreparedStatement getStockStatement = sqlConnection.prepareStatement(GET_STOCK);
		getStockStatement.setInt(1, this.id);
		ResultSet rs = getStockStatement.executeQuery();
		rs.next();
		this.numberAvailable = numberNewBooks + rs.getInt("number_available");
		PreparedStatement statement = sqlConnection.prepareStatement(UPDATE_STOCK);
		statement.setInt(1, this.numberAvailable);
		statement.setInt(2, this.id);
		statement.executeUpdate();
	}

	public Instant createReservation(Connection sqlConnection, int userId) throws SQLException, ModelException {
		PreparedStatement getStockStatement = sqlConnection.prepareStatement(GET_STOCK);
		getStockStatement.setInt(1, this.id);
		ResultSet rs = getStockStatement.executeQuery();
		rs.next();
		this.numberAvailable = rs.getInt("number_available");
		if (numberAvailable <= 0)
			throw new ModelException("Cannot reserve a book when there are no available copies");
		this.numberAvailable--;
		PreparedStatement statement = sqlConnection.prepareStatement(UPDATE_STOCK);
		statement.setInt(1, this.numberAvailable);
		statement.setInt(2, this.id);
		statement.executeUpdate();

		statement = sqlConnection.prepareStatement(RESERVE);
		Instant start = Instant.now();
		Instant due = start.plus(Duration.ofDays(7));

		statement.setInt(1, userId);
		statement.setInt(2, this.id);
		statement.setLong(3, start.getEpochSecond());
		statement.setLong(4, due.getEpochSecond());
		statement.executeUpdate();
		return due;
	}

	public static List<Book> search(Connection sqlConnection, String title, String author) throws SQLException {
		title = String.format("%%%s%%", title);
		author = String.format("%%%s%%", author);
		PreparedStatement statement = sqlConnection.prepareStatement(SEARCH);
		statement.setString(1, title);
		statement.setString(2, author);
		ArrayList<Book> list = new ArrayList<>();
		ResultSet results = statement.executeQuery();

		while (results.next()) {
			list.add(bookFromResults(results));
		}
		return list;
	}

	static Book bookFromResults(ResultSet results) throws SQLException {
		return new Book(results.getInt("book_id"), results.getString("title"),
				results.getString("author"), results.getInt("year"),
				results.getInt("number_available"));
	}
}
