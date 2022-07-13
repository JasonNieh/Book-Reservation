package bookReserve;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import bookReserve.datamodel.Reservation;

public class ReservationTableModel extends AbstractTableModel {


    List<Reservation> reservations= new ArrayList<>();
    final boolean showsUserData;
    
    public ReservationTableModel() {
        showsUserData = false;
    }
    
    public ReservationTableModel(boolean showsUserData) {
        this.showsUserData = showsUserData;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        
        return reservations.size();
    }

    @Override
    public int getColumnCount() {
        return showsUserData?8:4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reservation reservation = reservations.get(rowIndex);

        if (! showsUserData ) {
            columnIndex +=4;
        }
        switch(columnIndex) {
            case 0: return reservation.getUser().getName();
            case 1: return reservation.getUser().getUsername();
            case 2: return reservation.getUser().getEmail();
            case 3: return reservation.getUser().getPhone();
            case 4: return reservation.getBook().getTitle();
            case 5: return reservation.getBook().getAuthor();
            case 6: return reservation.getBook().getYear();
            case 7: return reservation.getDue();
            default: return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (! showsUserData ) {
            columnIndex +=4;
        }
        switch(columnIndex) {
            case 0: return "Name";
            case 1: return "Username";
            case 2: return "Email";
            case 3: return "Phone";
            case 4: return "Title";
            case 5: return "Author";
            case 6: return "Year";
            case 7: return "Due date";
            default: return null;
        }
    }
    
    public Reservation getReservation(int index) {
        return reservations.get(index);
    }
}
