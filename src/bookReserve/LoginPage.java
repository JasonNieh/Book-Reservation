package bookReserve;
import bookReserve.datamodel.User;
import bookReserve.datamodel.PasswordUtils;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginPage extends JFrame {

	/**
	 * generated serial version ID
	 */
	private static final long serialVersionUID = -8381737415679078655L;

	private JPanel mainPanel;
	private JPanel buttonPanel;
	private JTextField usernameText;
	private JPasswordField passwordText;
	private JButton loginButton;
	private JButton signUpButton;

	private Connection sqlConnection;
	public LoginPage() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout(5,5);
		setLayout(layout);
		initializeMainPanel();
		
		initializeButtonPanel();
		this.setSize(250,180);
	}

	private void initializeButtonPanel() {
		buttonPanel = new JPanel();
		loginButton = new JButton("Log in");
		signUpButton = new JButton("Create new User");
		buttonPanel.add(loginButton);
		buttonPanel.add(signUpButton);

		loginButton.addActionListener((e)-> { 
			try {
				User user = PasswordUtils.login(sqlConnection,usernameText.getText(), new String(passwordText.getPassword()));
				if (user == null) { 
					JOptionPane.showMessageDialog(this, "Wrong password??", "Can't log in", JOptionPane.WARNING_MESSAGE);
				} else {

					ReserveBookPage rp = new ReserveBookPage(sqlConnection, user);
					rp.setSize(1000,800);
					rp.setVisible(true);
					this.setVisible(false);
				}
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(), "Couldn't login, SQL error", JOptionPane.ERROR_MESSAGE);
			}
		});
		signUpButton.addActionListener((ev)-> {
			new SignUpPage(this, sqlConnection).setVisible(true);
		}
		);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	private void initializeMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
		mainPanel.add(new JLabel("Username"));
		usernameText = new JTextField();
		mainPanel.add(usernameText);
		mainPanel.add(new JLabel("Password"));
		passwordText = new JPasswordField();
		mainPanel.add(passwordText);
		mainPanel.setSize(mainPanel.getMinimumSize());
		this.add(mainPanel, BorderLayout.CENTER);
	}

	public void setConnection(Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}

}
