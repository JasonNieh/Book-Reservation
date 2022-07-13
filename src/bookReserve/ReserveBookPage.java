package bookReserve;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import bookReserve.datamodel.Book;
import bookReserve.datamodel.ModelException;
import bookReserve.datamodel.Reservation;
import bookReserve.datamodel.User;

public class ReserveBookPage extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7244331372528002239L;
	/**
	 * 
	 */
	private JPanel userArea;
	private JPanel bookReservationArea;
	private JPanel searchArea;
	private JScrollPane searchResultScroll;
	private JPanel submitArea;

	private JLabel bookNameLabel;
	private JLabel authorLabel;

	private JTextField bookNameText;
	private JTextField authorNameText;
	private JButton searchButton;

	private JButton submitButton;
	private JTable bookTable;

	private User user;
	private Connection sqlConnection;
	private BookSearchTableModel bookSearchTableModel = new BookSearchTableModel();
	private ReservationTableModel reservationTableModel;

	private JPanel bookReturnArea;
	private JTable reservationTable;
	private JPanel returnButtonArea;
	private JButton returnButton;
	private JScrollPane reservationScroll;
	private JButton addCopiesButton;
	private JButton addNewBook;

	// test area

	// this is the ReaderPage constructor
	public ReserveBookPage(Connection connection, User user) {
		this.user = user;
		this.sqlConnection = connection;
		reservationTableModel = new ReservationTableModel(user.isAdmin());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(1000,800);

		initBookReservationArea();
		initBookReturnArea();

		initUserArea();
		executeSearch();
	}

	private void initUserArea() {
		userArea = new JPanel();
		userArea.setLayout(new BorderLayout());
		userArea.add(new JLabel(String.format("Welcome, %s!", user.getName())), BorderLayout.NORTH);
		userArea.add(new JLabel(String.format("%s",  (user.isAdmin()?"You are an admin, you can return other people's books and you can add copies of books": "You are not an admin"))), BorderLayout.CENTER);

		this.add(userArea, BorderLayout.NORTH);
	}

	private void initBookReservationArea() {
		initSearchArea();
		initSearchResultArea();
		initSubmitArea();
		bookReservationArea = new JPanel();
		bookReservationArea.setLayout(new BorderLayout());

		bookReservationArea.add(searchArea, BorderLayout.NORTH);
		bookReservationArea.add(searchResultScroll, BorderLayout.CENTER);
		bookReservationArea.add(submitArea, BorderLayout.SOUTH);
		this.add(bookReservationArea, BorderLayout.CENTER);

	}

	// this is used in the ReaderPage constructor
	// to initialize the top panel which is AKA the
	// search panel that includes the search buttons
	public void initSearchArea() {
		bookNameLabel = new JLabel("Book");
		authorLabel = new JLabel("Author");
		bookNameText = new JTextField();
		authorNameText = new JTextField();
		searchButton = new JButton("Search/Update");
		searchButton.addActionListener((e) -> {
			executeSearch();
		});
		bookNameText.setColumns(20);
		authorNameText.setColumns(20);

		searchArea = new JPanel();
		searchArea.add(bookNameLabel);
		searchArea.add(bookNameText);
		searchArea.add(authorLabel);
		searchArea.add(authorNameText);
		searchArea.add(searchButton);
		if (user.isAdmin()) {
			addNewBook = new JButton("Add New Book");
			addNewBook.setFont(new Font("Times", Font.BOLD, 13));
			addNewBook.addActionListener((ev)-> {
					AddNewBookPage newBookPage = new AddNewBookPage(this, sqlConnection);
					newBookPage.setVisible(true);
					executeSearch();
			});
			searchArea.add(addNewBook);
		}

	}

	private void executeSearch() {
		try {
			bookSearchTableModel.setBookResults(Book.search(sqlConnection, bookNameText.getText(), authorNameText.getText()));
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage(), "Search error", JOptionPane.ERROR_MESSAGE);
			
		}
	}

	public void initSearchResultArea() {
		bookTable = new JTable(bookSearchTableModel);

		bookTable.setFillsViewportHeight(true);
		bookTable.setRowSelectionAllowed(true);
		bookTable.setColumnSelectionAllowed(false);
		bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		searchResultScroll = new JScrollPane(bookTable);
		searchResultScroll.setPreferredSize(new Dimension(400,300));
	}

	public void initSubmitArea() {
		submitButton = new JButton("Reserve");
		submitButton.addActionListener((e) -> {
			if (bookTable.getSelectedRow() != -1) {
				Book book = bookSearchTableModel.getBook(bookTable.getSelectedRow());
				try {
					switch(JOptionPane.showConfirmDialog(this, String.format("Do you want to reserve %s by %s", book.getTitle(), book.getAuthor()), "Reservation", JOptionPane.YES_NO_OPTION)) {
						case JOptionPane.YES_OPTION: 
							Instant dueTime =book.createReservation(sqlConnection, user.getId());
							JOptionPane.showMessageDialog(this, String.format("Your copy of %s by %s is due on %s", book.getTitle(), book.getAuthor(), dueTime.toString()), "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);
							updateReservationList();

					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(this,
							String.format("There was a SQL Error trying to reserve %s by %s: %s", book.getTitle(),
									book.getAuthor(), e1.getMessage()),
							"Couldn't reserve the book", JOptionPane.ERROR_MESSAGE);
				} catch (ModelException e1) {
					JOptionPane.showMessageDialog(this, String.format("Couldn't reserve %s by %s: %s", book.getTitle(),
							book.getAuthor(), e1.getMessage()), "Couldn't reserve the book", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		submitArea = new JPanel();
		submitArea.add(submitButton);
		if (user.isAdmin()) {
			addCopiesButton = new JButton("Update Inventory");
			addCopiesButton.setFont(new Font("Times", Font.BOLD, 13));
			addCopiesButton.addActionListener((ev)-> {
				if (bookTable.getSelectedRow() != -1) {

				Book book = bookSearchTableModel.getBook(bookTable.getSelectedRow());
				try {
					int newCopies = Integer.parseInt(JOptionPane.showInputDialog(this, String.format("How many copies of %s by %s are you adding?",book.getTitle(),book.getAuthor())));
					if (newCopies<0) throw new NumberFormatException();
					book.addStock(sqlConnection, newCopies);
				} catch (NumberFormatException exception) {
					JOptionPane.showMessageDialog(this, "please enter a valid number of copies to add that is greater than zero", "Couldn't add copies", JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(this, e1.getMessage(), "Couldn't add copies", JOptionPane.ERROR_MESSAGE);
				}
			}});
			submitArea.add(addCopiesButton);
		}
	}


	private void initBookReturnArea() {
		initReservationList();
		initReturnButtonArea();
		bookReturnArea = new JPanel();
		bookReturnArea.setLayout(new BorderLayout());

		
		bookReturnArea.add(reservationScroll, BorderLayout.CENTER);
		bookReturnArea.add(returnButtonArea, BorderLayout.SOUTH);
		this.add(bookReturnArea, BorderLayout.SOUTH);

	}

	private void initReturnButtonArea() {
		returnButton = new JButton("Return");
		returnButton.addActionListener((e) -> {
			if (reservationTable.getSelectedRow() != -1) {
				Reservation reservation = reservationTableModel.getReservation(reservationTable.getSelectedRow());
				try {
					switch(JOptionPane.showConfirmDialog(this, String.format("Do you want to return %s by %s", reservation.getBook().getTitle(), reservation.getBook().getAuthor()), "Return book", JOptionPane.YES_NO_OPTION)) {
						case JOptionPane.YES_OPTION: 
							reservation.returnBook(sqlConnection);
							JOptionPane.showMessageDialog(this, String.format("You have returned %s by %s", reservation.getBook().getTitle(),  reservation.getBook().getAuthor()), "Book Returned", JOptionPane.INFORMATION_MESSAGE);
							updateReservationList();

					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(this,
							String.format("There was a SQL Error trying to return %s by %s: %s", reservation.getBook().getTitle(),
							reservation.getBook().getAuthor(), e1.getMessage()),
							"Couldn't return the book", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		returnButtonArea = new JPanel();
		JButton refreshButton = new JButton("Refresh Reservations");
		refreshButton.addActionListener((ev)-> {
			updateReservationList();
		});
		returnButtonArea.add(refreshButton);

		returnButtonArea.add(returnButton);
	}

	private void initReservationList() {
		reservationTable = new JTable(reservationTableModel);

		updateReservationList();
		reservationTable.setFillsViewportHeight(true);
		reservationTable.setRowSelectionAllowed(true);
		reservationTable.setColumnSelectionAllowed(false);
		reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reservationScroll = new JScrollPane(reservationTable);
		reservationScroll.setPreferredSize(new Dimension(400,300));
	}

	private void updateReservationList() {
		try {
			reservationTableModel.setReservations(user.isAdmin()? Reservation.getAllReservations(sqlConnection): user.getReservations(sqlConnection));
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),"Could not load your reservations, please try again", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
