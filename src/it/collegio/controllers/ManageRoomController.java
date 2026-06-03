package it.collegio.controllers;

import it.collegio.dao.RoomDao;
import it.collegio.enums.BedType;
import it.collegio.enums.RoomStatus;
import it.collegio.enums.RoomType;
import it.collegio.models.Room;
import it.collegio.utilities.SessionContext;
import java.util.List;

public class ManageRoomController {

    private static final int DEFAULT_COMMITTENTE = 1;

    private final RoomDao roomDao;

    public ManageRoomController() {
        this.roomDao = new RoomDao();
    }

    public List<Room> getRooms() {
        return roomDao.getAll();
    }

    public Room getById(int id) {
        return roomDao.getById(id);
    }

    public int getNextId() {
        return roomDao.getMaxId() + 1;
    }

    public boolean addRoom(int roomId, String tipo, double prezzo, String lettoTipo) {
        if (roomDao.getById(roomId) != null) {
            return false;
        }

        Room room = new Room();
        room.setId(roomId);
        room.setCommittente(SessionContext.committenteId > 0 ? SessionContext.committenteId : DEFAULT_COMMITTENTE);
        room.setNumeroStanza(roomId);
        room.setrTipo(parseRoomType(tipo));
        room.setPrezzo(prezzo);
        room.setLettoTipo(parseBedType(lettoTipo));
        room.setStato(RoomStatus.DISPONIBILE);

        return roomDao.insertRoomWithId(room);
    }

    public boolean updateRoom(int roomId, String tipo, double prezzo, String lettoTipo) {
        Room existing = roomDao.getById(roomId);
        if (existing == null) {
            return false;
        }
        if (existing.getStato() == RoomStatus.OCCUPATA) {
            return false;
        }
        existing.setrTipo(parseRoomType(tipo));
        existing.setPrezzo(prezzo);
        existing.setLettoTipo(parseBedType(lettoTipo));
        return roomDao.updateRoom(existing);
    }

    public boolean deleteRoom(int roomId) {
        Room existing = roomDao.getById(roomId);
        if (existing == null) {
            return false;
        }
        if (existing.getStato() == RoomStatus.OCCUPATA) {
            return false;
        }
        return roomDao.deleteRoom(roomId);
    }

    public boolean isOccupata(String statoStr) {
        if (statoStr == null) return false;
        return RoomStatus.OCCUPATA.name().equalsIgnoreCase(statoStr);
    }

    private RoomType parseRoomType(String value) {
        if (value == null) return RoomType.SINGOLA;
        try {
            return RoomType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return RoomType.SINGOLA;
        }
    }

    private BedType parseBedType(String value) {
        BedType b = BedType.fromDb(value);
        return b != null ? b : BedType.SINGOLO;
    }
}
