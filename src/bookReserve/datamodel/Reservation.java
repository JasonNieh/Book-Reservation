package bookReserve.datamodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Reservation {

    private static final String RETURN_BOOK = "delete from book_reservation where book_id = ? and user_id = ?";
    private static final String GET_ALL_RESERVATIONS = "select * from book_reservation join user on book_reservation.user_id = user.user_id join book on book_reservation.book_id = book.book_id";
    private Instant start;
    private Instant due;
    private Book book;
    private User user;
    public Reservation(Instant start, Instant due, Book book, User user) {
        this.start = start;
        this.due = due;
        this.book = book;
        this.user = user;
    }
    public Instant getStart() {
        return start;
    }
    public User getUser() {
        return user;
    }
    public Book getBook() {
        return book;
    }
    public Instant getDue() {
        return due;
    }

    public void returnBook(Connection sqlConnection) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(RETURN_BOOK);
        statement.setInt(1, book.getId());
        statement.setInt(2, user.getId());
        statement.executeUpdate();
        book.addStock(sqlConnection, 1);
    }

    public static Reservation reservationFromUserResults(ResultSet rs, User user) throws SQLException {
        Instant start = Instant.ofEpochSecond(rs.getLong("start_date"));
        Instant due = Instant.ofEpochSecond(rs.getLong("due_date"));
        Book book = Book.bookFromResults(rs);
        return new Reservation(start, due, book, user);
    }

    public static Reservation reservationFromResults(ResultSet rs) throws SQLException {
        User user = User.userFromResult(rs);
        return reservationFromUserResults(rs, user);
    }

    public static List<Reservation> getAllReservations(Connection sqlConnection) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(GET_ALL_RESERVATIONS);
        ResultSet rs = statement.executeQuery();
        ArrayList<Reservation> list = new ArrayList<>();
        while (rs.next()) {
            list.add(reservationFromResults(rs));
        }
        return list;
    }

    
}
