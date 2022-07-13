package bookReserve.datamodel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordUtils {

    private static String verifyLoginQuery = "SELECT * from user where username = ? and password_sha1 = ?";

    public static String passwordToSHA1Hash(String password) {
        return DigestUtils.sha1Hex(password);
    }

    public static User login(Connection sqlConnection, String username, String password) throws SQLException {
        PreparedStatement statement = sqlConnection.prepareStatement(verifyLoginQuery);
        statement.setString(1, username);
        String passwordSha1 = passwordToSHA1Hash(password);
        statement.setString(2, passwordSha1);
        ResultSet results = statement.executeQuery();
        if (results.next()) {
            return User.userFromResult(results);
        }
        return null;
    }

}
