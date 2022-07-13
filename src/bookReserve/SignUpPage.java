package bookReserve;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import bookReserve.datamodel.User;

public class SignUpPage extends JDialog  {
    JTextField usernameText = new JTextField();
    JPasswordField passwordText = new JPasswordField();
    JTextField nameText = new JTextField();
    JTextField addressText = new JTextField();
    JTextField phoneText = new JTextField();
    JTextField emailText = new JTextField();
    
    JButton submitButton= new JButton("Sign Up");

    JPanel fieldsArea= new JPanel();

    public SignUpPage(JFrame parent,Connection sqlConnection) {
        super(parent, true);
        setSize(320,200);
        fieldsArea.setLayout(new GridLayout(7,2));
        fieldsArea.add(new JLabel("Username"));
        fieldsArea.add(usernameText);
        fieldsArea.add(new JLabel("Password"));
        fieldsArea.add(passwordText);
        fieldsArea.add(new JLabel("Name"));
        fieldsArea.add(nameText);
        fieldsArea.add(new JLabel("Address"));
        fieldsArea.add(addressText);
        fieldsArea.add(new JLabel("Phone"));
        fieldsArea.add(phoneText);
        fieldsArea.add(new JLabel("Email"));
        fieldsArea.add(emailText);
        this.setLayout(new BorderLayout());
        this.add(fieldsArea, BorderLayout.CENTER);
        this.add(submitButton, BorderLayout.SOUTH);
        submitButton.addActionListener((ev)-> {

            User user = new User(nameText.getText(), usernameText.getText(), addressText.getText(), emailText.getText(), phoneText.getText(), false);
            try {
                user.create(sqlConnection, new String(passwordText.getPassword()));
                this.setVisible(false);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Couldn't create user", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
