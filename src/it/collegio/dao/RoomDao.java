package it.collegio.dao;

import it.collegio.enums.BedType;
import it.collegio.enums.RoomStatus;
import it.collegio.enums.RoomType;
import it.collegio.models.Room;
import it.collegio.utilities.QueryContainer;
import it.collegio.utilities.utility;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomDao {

    private DatabaseConnection dbConnection;

    public RoomDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public String getRoomId() {

        String counter = "R0001";
        try {
            String query = QueryContainer.queryRoomId;
            PreparedStatement pst = this.dbConnection.getConnection().prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Object raw = rs.getObject(1);
                if (raw != null) {
                    int id = ((Number) raw).intValue();
                    id++;
                    counter = utility.convAlfaR(id);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return counter;
    }

    public int insertRoom(Room room) {
        String query = QueryContainer.queryInsRoom;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, room.getCommittente());
            pst.setInt(2, room.getNumeroStanza());
            pst.setString(3, room.getrTipo().name().toLowerCase());
            pst.setDouble(4, room.getPrezzo());
            pst.setString(5, room.getLettoTipo() != null ? room.getLettoTipo().getDbValue() : BedType.SINGOLO.getDbValue());
            pst.setString(6, room.getStato() != null ? room.getStato().name().toLowerCase() : RoomStatus.DISPONIBILE.name().toLowerCase());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        room.setId(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public int getMaxId() {
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryRoomId);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                Object raw = rs.getObject(1);
                if (raw != null) {
                    return ((Number) raw).intValue();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean insertRoomWithId(Room room) {
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryInsRoomFull)) {
            pst.setInt(1, room.getId());
            pst.setInt(2, room.getCommittente() > 0 ? room.getCommittente() : 1);
            pst.setInt(3, room.getNumeroStanza());
            pst.setString(4, room.getrTipo().name().toLowerCase());
            pst.setDouble(5, room.getPrezzo());
            pst.setString(6, room.getLettoTipo() != null ? room.getLettoTipo().getDbValue() : BedType.SINGOLO.getDbValue());
            pst.setString(7, room.getStato() != null ? room.getStato().name().toLowerCase() : RoomStatus.DISPONIBILE.name().toLowerCase());
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteRoom(int roomId) {
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryDelRoom)) {
            pst.setInt(1, roomId);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateRoom(Room room) {
        String query = QueryContainer.queryUpdRoom;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setInt(1, room.getNumeroStanza());
            pst.setString(2, room.getrTipo().name().toLowerCase());
            pst.setDouble(3, room.getPrezzo());
            pst.setString(4, room.getLettoTipo() != null ? room.getLettoTipo().getDbValue() : BedType.SINGOLO.getDbValue());
            pst.setString(5, room.getStato() != null ? room.getStato().name().toLowerCase() : RoomStatus.DISPONIBILE.name().toLowerCase());
            pst.setInt(6, room.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateStato(int roomId, RoomStatus stato) {
        String query = QueryContainer.queryUpdRoomStato;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, stato.name().toLowerCase());
            pst.setInt(2, roomId);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Room getById(int id) {
        Room room = null;
        String query = QueryContainer.queryGetRoomById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    room = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return room;
    }

    public List<Room> getByCommittente(int committenteId) {
        List<Room> rooms = new ArrayList<>();
        String query = QueryContainer.queryGetRoomsByCommittente;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, committenteId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = QueryContainer.queryGetAvailableRooms;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rooms;
    }

    public List<Room> getAvailableRoomsByCommittente(int committenteId) {
        List<Room> rooms = new ArrayList<>();
        String query = QueryContainer.queryGetAvailableRoomsByCommittente;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, committenteId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rooms;
    }

    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String query = QueryContainer.queryGetRooms;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rooms;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setId(rs.getInt("room_id"));
        r.setCommittente(rs.getInt("committente_id"));
        r.setNumeroStanza(rs.getInt("numero_stanza"));
        r.setPrezzo(rs.getDouble("prezzo"));
        r.setrTipo(RoomType.valueOf(rs.getString("tipo").toUpperCase()));
        r.setLettoTipo(BedType.fromDb(rs.getString("letto_tipo")));
        r.setStato(RoomStatus.valueOf(rs.getString("stato").toUpperCase()));
        return r;
    }
}
