package bookReserve;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import bookReserve.datamodel.Book;

public class BookSearchTableModel extends AbstractTableModel {


    List<Book> books= new ArrayList<>();


    public void setBookResults(List<Book> books) {
        this.books = books;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch(columnIndex) {
            case 0: return book.getTitle();
            case 1: return book.getAuthor();
            case 2: return book.getYear();
            case 3: return book.getNumberAvailable();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0: return "Title";
            case 1: return "Author";
            case 2: return "Year";
            case 3: return "Available copies";
            default: return null;
        }
    }
    
    public Book getBook(int index) {
        return books.get(index);
    }
}
