package bookReserve;

import java.awt.GridLayout;
import java.lang.ProcessHandle.Info;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bookReserve.datamodel.Book;

public class AddNewBookPage extends JDialog {
    private Book book;
    private JPanel infoPanel = new JPanel();
   

    private JPanel submitPanel = new JPanel();
    private JTextField titleText = new JTextField(20);
    private JTextField authorText = new JTextField(20);
    private JTextField yearText = new JTextField(20);
    private JTextField numberText = new JTextField(20);

    private JButton submitButton = new JButton("Submit");
    public AddNewBookPage(JFrame parent, Connection sqlConnection) {
        super(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400,200);
        
        
        infoPanel.setLayout(new GridLayout(4,2));
        infoPanel.add(new JLabel("Book Title:"));
        infoPanel.add(titleText);
        infoPanel.add(new JLabel("Author:"));
        infoPanel.add(authorText);
        infoPanel.add(new JLabel("Year:"));
        infoPanel.add(yearText);
        infoPanel.add(new JLabel("Available Copies:"));
        infoPanel.add(numberText);
        submitButton.addActionListener((e)->{
            book = new Book(titleText.getText(), authorText.getText(), Integer.parseInt(yearText.getText()), Integer.parseInt(numberText.getText()));
            try {
               switch(JOptionPane.showConfirmDialog(this, String.format("Are you sure to add book %s by %s of year %d with %d copies?",book.getTitle(),book.getAuthor(),book.getYear(),book.getNumberAvailable()), "Reservation", JOptionPane.YES_NO_OPTION)){
                case JOptionPane.YES_OPTION: 
                    book.create(sqlConnection); 		
                    JOptionPane.showMessageDialog(this, "Added successfully", "New Book Confirmed", JOptionPane.INFORMATION_MESSAGE);
                    setVisible(false);
                }  
            } catch (SQLException e1) {
                JOptionPane.showMessageDialog(this, "Error adding books", "Couldn't add new book", JOptionPane.ERROR_MESSAGE);
            }
        });
        submitPanel.add(submitButton);
        add(infoPanel,BorderLayout.NORTH);
        add(submitPanel,BorderLayout.CENTER);
    }
}
