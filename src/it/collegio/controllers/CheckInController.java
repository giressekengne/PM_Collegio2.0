package it.collegio.controllers;

import it.collegio.dao.DatabaseConnection;
import it.collegio.dao.FatturaDao;
import it.collegio.dao.ReservationDao;
import it.collegio.dao.RoomDao;
import it.collegio.dao.UserDao;
import it.collegio.enums.FatturaStatus;
import it.collegio.enums.ReservationStatus;
import it.collegio.enums.RoomStatus;
import it.collegio.models.Fattura;
import it.collegio.models.Reservation;
import it.collegio.models.Room;
import it.collegio.models.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckInController {

    private final RoomDao roomDao;
    private final ReservationDao reservationDao;
    private final FatturaDao fatturaDao;
    private final UserDao userDao;

    public CheckInController() {
        this.roomDao = new RoomDao();
        this.reservationDao = new ReservationDao();
        this.fatturaDao = new FatturaDao();
        this.userDao = new UserDao();
    }

    public List<Room> getRoomDisponibili() {
        return roomDao.getAvailableRooms();
    }

    public Room getRoomById(int roomId) {
        return roomDao.getById(roomId);
    }

    public List<Room> getRoomPerPrezzo(double prezzo) {
        List<Room> filtrate = new ArrayList<>();
        for (Room r : roomDao.getAvailableRooms()) {
            if (r.getPrezzo() == prezzo) {
                filtrate.add(r);
            }
        }
        return filtrate;
    }

    public User cercaUserPerEmail(String email) {
        return userDao.getUser(email);
    }

    /**
     * Esegue la prenotazione in transazione: inserisce Reservation,
     * crea Fattura con importo=0 stato IN_ATTESA, marca la Room come OCCUPATA.
     * Tutto o niente: se uno step fallisce, rollback.
     *
     * @return l'id della Reservation creata, oppure -1 in caso di errore
     */
    public int prenota(String userCounter, int roomId, int committenteId,
                       String checkInDateStr, String note) {
        Connection conn = DatabaseConnection.getDatabaseConnection().getConnection();

        try {
            conn.setAutoCommit(false);

            Date checkInDate = parseDate(checkInDateStr);
            if (checkInDate == null) {
                conn.rollback();
                return -1;
            }

            Reservation reservation = new Reservation();
            reservation.setUser(userCounter);
            reservation.setCommittente(committenteId);
            reservation.setRoom(roomId);
            reservation.setCheckIn(checkInDate);
            reservation.setStato(ReservationStatus.ATTIVA);
            reservation.setNote(note);

            int reservationId = reservationDao.insertReservation(reservation);
            if (reservationId < 0) {
                conn.rollback();
                return -1;
            }

            Fattura fattura = new Fattura(reservationId, 0.0, LocalDateTime.now(), FatturaStatus.IN_ATTESA);
            int fatturaId = fatturaDao.insertFattura(fattura);
            if (fatturaId < 0) {
                conn.rollback();
                return -1;
            }

            if (!roomDao.updateStato(roomId, RoomStatus.OCCUPATA)) {
                conn.rollback();
                return -1;
            }

            conn.commit();
            return reservationId;

        } catch (SQLException ex) {
            Logger.getLogger(CheckInController.class.getName()).log(Level.SEVERE, null, ex);
            try {
                conn.rollback();
            } catch (SQLException rbEx) {
                Logger.getLogger(CheckInController.class.getName()).log(Level.SEVERE, null, rbEx);
            }
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(CheckInController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr.trim());
        } catch (ParseException ex) {
            return null;
        }
    }
}
