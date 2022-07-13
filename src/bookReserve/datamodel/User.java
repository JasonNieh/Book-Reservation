package bookReserve.datamodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User {

    private static final String UPDATE_USER_PASSWORD = "update user set password_sha1 = ? where user_id = ? and password_sha1 = ?";
    private static final String UPDATE_USER_ADMIN_BIT = "update user set is_admin=? where user_id=?";
    private static final String UPDATE_USER_DETAILS = "update user set name = ?, username = ?, address =?, email=?, phone=? where user_id=?";
    private static final String CREATE = "insert into user (name, username, password_sha1, address, email, phone, is_admin) values (?,?,?,?,?,?,?)";

    private static final String GET_RESERVATIONS = "Select * from book_reservation inner join book on book_reservation.book_id = book.book_id where book_reservation.user_id = ?";

    public User(int id, String name, String username, String address, String email, String phone,
            boolean isAdmin) {
        this.id = id;
        this.setName(name);
        this.setUsername(username);
        this.setAddress(address);
        this.setEmail(email);
        this.setPhone(phone);
        this.setAdmin(isAdmin);
    }

    public User(String name, String username, String address, String email, String phone,
            boolean isAdmin) {
        this.setName(name);
        this.setUsername(username);
        this.setAddress(address);
        this.setEmail(email);
        this.setPhone(phone);
        this.setAdmin(isAdmin);
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    private int id = -1;
    private String name;
    private String username;
    private String address;
    private String email;
    private String phone;
    private boolean isAdmin;

    public void updateInDb(Connection sqlConnection) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(UPDATE_USER_DETAILS);
        statement.setString(1, this.name);
        statement.setString(2, this.username);
        statement.setString(3, this.address);
        statement.setString(4, this.email);
        statement.setString(5, this.phone);
        statement.setInt(6, this.id);
        statement.executeUpdate();
    }

    public void create(Connection sqlConnection, String password) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(CREATE);
        statement.setString(1, this.name);
        statement.setString(2, this.username);
        statement.setString(3, PasswordUtils.passwordToSHA1Hash(password));
        statement.setString(4, this.address);
        statement.setString(5, this.email);
        statement.setString(6, this.phone);
        statement.setInt(7, this.id);
        statement.executeUpdate();
    }

    /**
     * Changes the user's password. May throw a SQLException in the case that the
     * old password provided was incorrect, not only at connection or logic errors.
     */
    public void updatePassword(Connection sqlConnection, String oldPassword, String newPassword) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(UPDATE_USER_PASSWORD);
        statement.setString(1, PasswordUtils.passwordToSHA1Hash(newPassword));
        statement.setInt(2, this.id);
        statement.setString(3, PasswordUtils.passwordToSHA1Hash(oldPassword));
        statement.executeUpdate();
    }

    public void updateAdminBit(Connection sqlConnection) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(UPDATE_USER_ADMIN_BIT);
        statement.setInt(1, isAdmin ? 1 : 0);
        statement.setInt(2, this.id);
        statement.executeUpdate();
    }

    public List<Reservation> getReservations(Connection sqlConnection) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(GET_RESERVATIONS);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        ArrayList<Reservation> list = new ArrayList<>();
        while (rs.next()) {
            list.add(Reservation.reservationFromUserResults(rs, this));
        }
        return list;
    }

    static User userFromResult(ResultSet results) throws SQLException {
        return new User(results.getInt("user_id"), results.getString("name"),
                results.getString("username"),
                results.getString("address"), results.getString("phone"),
                results.getString("email"),
                results.getInt("is_admin") == 1);
    }

}
