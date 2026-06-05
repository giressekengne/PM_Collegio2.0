package it.collegio.utilities;


public final class QueryContainer {
    
  public static String queryLogin = "select * from User where email=? AND access=?"; 
   
  public static String queryInsCommittente = " INSERT INTO Committente (ragione_Sociale, gestore, email, telefono, indirizzo_id)\n" +
                                       "VALUES (?, ?, ?, ?, ?);";  
  public static String queryUpdCommittente = "UPDATE Committente SET ragione_Sociale = ?, gestore = ?, email = ?, telefono = ?, indirizzo_id = ?\n" +
                                       "WHERE codCommittente = ?;";
  public static String queryInsUser = "INSERT INTO User(user_counter, nome, cognome, email, access, ruolo, committente_id, stato, telefono, indirizzo_id, recupero, response, genere)\n" +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
  public static String queryUpdUser = "UPDATE User SET nome = ?, cognome = ?, email = ?, access = ?, ruolo = ?, committente_id = ?, stato = ?, telefono = ?, indirizzo_id = ?, recupero = ?, response = ?, genere = ?\n" +
                                "WHERE user_counter = ?;";
  public static String queryInsRoom = "INSERT INTO Room (committente_id, numero_stanza, tipo, prezzo, letto_tipo, stato)\n" +
                                "VALUES (?, ?, ?, ?, ?, ?);";
  public static String queryUpdRoom = "UPDATE Room SET numero_stanza = ?, tipo = ?, prezzo = ?, letto_tipo = ?, stato = ?\n" +
                                "WHERE room_id = ?;";
  public static String queryInsReservation = "INSERT INTO Reservation (user_id, committente_id, room_id, check_in, check_out, giorni, status, note)\n" +
                                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?);" ;
  public static String queryUpdReservation = "UPDATE Reservation SET room_id = ?, check_in = ?, check_out = ?, giorni = ?, status = ?, note = ?\n" +
                                       "WHERE reservation_id = ?;" ;
  public static String queryInsFattura = "INSERT INTO Fattura (reservation_id, importo, data_emissione, stato)\n" +
                                       "VALUES (?, ?, ?, ?);";
  public static String queryUpdFattura = "UPDATE Fattura SET importo = ?, data_emissione = ?, stato = ?\n" +
                                   "WHERE fattura_id = ?;";
  public static String queryInsPagamento = "INSERT INTO Pagamento (fattura_id, metodo_id, importo)\n" +
                                     "VALUES (?, ?, ?);";
  public static String queryUpdPagamento = "UPDATE Pagamento SET metodo_id = ?, importo = ?\n" +
                                      "WHERE pagamento_id = ?;"; 
  public static String queryInsLog = "INSERT INTO AccessLog (user_id, ip_address, note)\n" +
                               "VALUES (?, ?, ?);";
  public static String queryUpdLog = "UPDATE AccessLog SET logout_time = NOW(), note = ?\n" +
                               "WHERE log_id = ?;";
  public static String queryInsSession = "INSERT INTO sessions (user_id, committente_id, role_id, token, expires)\n" +
                                   "VALUES (?, ?, ?, ?, ?);";
  public static String queryUpdSession = "UPDATE sessions SET token = ?, expires = ?\n" +
                                   "WHERE session_id = ?;";
  
  public static String queryIndirizzo = "SELECT * FROM Indirizzo;";

  public static String queryMansione = "SELECT * FROM Mansione;";

  public static String queryGetUserByMail = "SELECT * FROM User WHERE email =? ";

  public static String queryGetRoleById = "SELECT * FROM Mansione WHERE role_id =? ";

  public static String queryUsers = "SELECT * FROM User";

  public static String queryUsersByCom = "SELECT * FROM User WHERE committente_id = ?";

  public static String queryRoomId = "SELECT Max(room_id) FROM Room";

  public static String queryInsIndirizzo = "INSERT INTO Indirizzo (paese, provincia, citta, via, cap)\n" +
                                     "VALUES (?, ?, ?, ?, ?);";

  public static String queryUpdIndirizzo = "UPDATE Indirizzo SET paese = ?, provincia = ?, citta = ?, via = ?, cap = ?\n" +
                                     "WHERE indirizzo_id = ?;";

  public static String queryGetIndirizzoById = "SELECT * FROM Indirizzo WHERE indirizzo_id = ?;";

  public static String queryGetMansioneByType = "SELECT * FROM Mansione WHERE role_type = ?;";

  public static String queryGetCommittenti = "SELECT * FROM Committente;";

  public static String queryGetCommittenteById = "SELECT * FROM Committente WHERE codCommittente = ?;";

  public static String queryGetUserByCounter = "SELECT * FROM User WHERE user_counter = ?;";

  public static String queryGetLogs = "SELECT * FROM AccessLog ORDER BY login_time DESC;";

  public static String queryGetLogsByUser = "SELECT * FROM AccessLog WHERE user_id = ? ORDER BY login_time DESC;";

  public static String queryGetSessionByToken = "SELECT * FROM sessions WHERE token = ?;";

  public static String queryInvalidateSession = "UPDATE sessions SET expires = NOW() WHERE session_id = ?;";

  public static String queryGetFatture = "SELECT * FROM Fattura ORDER BY data_emissione DESC;";

  public static String queryGetFatturaById = "SELECT * FROM Fattura WHERE fattura_id = ?;";

  public static String queryGetFatturaByReservation = "SELECT * FROM Fattura WHERE reservation_id = ?;";

  public static String queryUpdFatturaStato = "UPDATE Fattura SET stato = ? WHERE fattura_id = ?;";

  public static String queryGetMetodiPagamento = "SELECT * FROM MetodoPagamento;";

  public static String queryGetMetodoPagamentoById = "SELECT * FROM MetodoPagamento WHERE metodo_id = ?;";

  public static String queryGetPagamentiByFattura = "SELECT * FROM Pagamento WHERE fattura_id = ? ORDER BY data_pagamento DESC;";

  public static String queryGetReservations = "SELECT * FROM Reservation ORDER BY check_in DESC;";

  public static String queryGetReservationById = "SELECT * FROM Reservation WHERE reservation_id = ?;";

  public static String queryGetReservationsByUser = "SELECT * FROM Reservation WHERE user_id = ? ORDER BY check_in DESC;";

  public static String queryGetReservationsByCommittente = "SELECT * FROM Reservation WHERE committente_id = ? ORDER BY check_in DESC;";

  public static String queryGetRooms = "SELECT * FROM Room ORDER BY numero_stanza;";

  public static String queryGetRoomById = "SELECT * FROM Room WHERE room_id = ?;";

  public static String queryGetRoomsByCommittente = "SELECT * FROM Room WHERE committente_id = ? ORDER BY numero_stanza;";

  public static String queryGetAvailableRooms = "SELECT * FROM Room WHERE stato = 'disponibile' ORDER BY numero_stanza;";

  public static String queryGetAvailableRoomsByCommittente = "SELECT * FROM Room WHERE committente_id = ? AND stato = 'disponibile' ORDER BY numero_stanza;";

  public static String queryUpdRoomStato = "UPDATE Room SET stato = ? WHERE room_id = ?;";

  public static String queryDelRoom = "DELETE FROM Room WHERE room_id = ?;";

  public static String queryInsRoomFull =
      "INSERT INTO Room (room_id, committente_id, numero_stanza, tipo, prezzo, letto_tipo, stato) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?);";

  public static String queryGetFatturaDettaglio =
      "SELECT F.fattura_id, F.reservation_id, U.nome, R.numero_stanza, " +
      "F.importo, F.data_emissione, F.stato, Res.status " +
      "FROM Fattura F " +
      "JOIN Reservation Res ON Res.reservation_id = F.reservation_id " +
      "JOIN User U ON U.user_counter = Res.user_id " +
      "JOIN Room R ON R.room_id = Res.room_id " +
      "WHERE F.fattura_id = ?;";

  public static String queryGetFattureDettagliate =
      "SELECT F.fattura_id, F.reservation_id, U.nome, R.numero_stanza, " +
      "F.importo, F.data_emissione, F.stato, Res.status " +
      "FROM Fattura F " +
      "JOIN Reservation Res ON Res.reservation_id = F.reservation_id " +
      "JOIN User U ON U.user_counter = Res.user_id " +
      "JOIN Room R ON R.room_id = Res.room_id " +
      "ORDER BY F.fattura_id DESC;";

  public static String queryCountUsers = "SELECT COUNT(*) FROM User;";

  public static String queryGetIndirizzoByVia = "SELECT * FROM Indirizzo WHERE via = ?;";

  public static String queryDelUser = "DELETE FROM User WHERE user_counter = ?;";

  public static String queryUpdPasswordByEmail = "UPDATE User SET access = ? WHERE email = ?;";

  public static String queryGetAdminEmails =
      "SELECT email FROM User WHERE ruolo != ? AND stato = 'attivo' ORDER BY email;";

  public static String queryMaxCommittenteId = "SELECT MAX(codCommittente) FROM Committente;";

  public static String queryInsCommittenteFull =
      "INSERT INTO Committente (codCommittente, ragione_Sociale, gestore, email, telefono, indirizzo_id) " +
      "VALUES (?, ?, ?, ?, ?, ?);";

  public static String queryGetCommittentiDettagli =
      "SELECT C.codCommittente, C.ragione_Sociale, U.email AS gestore_email, C.email, C.telefono, I.via " +
      "FROM Committente C " +
      "LEFT JOIN User U ON U.user_counter = C.gestore " +
      "LEFT JOIN Indirizzo I ON I.indirizzo_id = C.indirizzo_id " +
      "ORDER BY C.codCommittente;";

  public static String queryGetCommittenteDettaglio =
      "SELECT C.codCommittente, C.ragione_Sociale, U.email AS gestore_email, C.email, C.telefono, I.via " +
      "FROM Committente C " +
      "LEFT JOIN User U ON U.user_counter = C.gestore " +
      "LEFT JOIN Indirizzo I ON I.indirizzo_id = C.indirizzo_id " +
      "WHERE C.codCommittente = ?;";

  public static String queryGetLogsDettagliati =
      "SELECT U.nome AS user_nome, AL.login_time, AL.logout_time, AL.ip_address, M.role_nome " +
      "FROM AccessLog AL " +
      "INNER JOIN User U ON U.user_counter = AL.user_id " +
      "INNER JOIN Mansione M ON M.role_id = U.ruolo " +
      "ORDER BY AL.login_time DESC;";

  public static String queryInsStorico =
      "INSERT INTO StoricoPrenotazioni(reservation_id, user_id, check_in_precedente, check_out_precedente, nuovo_check_in, nuovo_check_out, data_modifica) " +
      "VALUES (?, ?, ?, ?, ?, ?, ?);";

  public static String queryGetStoricoByReservation =
      "SELECT * FROM StoricoPrenotazioni WHERE reservation_id = ? ORDER BY data_modifica DESC;";

  public static String queryGetReservationDettaglio =
      "SELECT Re.reservation_id, U.nome AS user_nome, Re.committente_id, Re.room_id, R.prezzo, " +
      "Re.check_in, Re.check_out, Re.status, Re.note, Re.giorni, " +
      "(R.prezzo * COALESCE(Re.giorni, 1)) AS totale " +
      "FROM Reservation Re " +
      "INNER JOIN User U ON U.user_counter = Re.user_id " +
      "INNER JOIN Room R ON R.room_id = Re.room_id " +
      "WHERE Re.reservation_id = ?;";

  public static String queryGetReservationDettaglioByUser =
      "SELECT Re.reservation_id, U.nome AS user_nome, Re.committente_id, Re.room_id, R.prezzo, " +
      "Re.check_in, Re.check_out, Re.status, Re.note, Re.giorni, " +
      "(R.prezzo * COALESCE(Re.giorni, 1)) AS totale " +
      "FROM Reservation Re " +
      "INNER JOIN User U ON U.user_counter = Re.user_id " +
      "INNER JOIN Room R ON R.room_id = Re.room_id " +
      "WHERE Re.reservation_id = ? AND Re.user_id = ?;";

  public static String queryGetReservationDettagli =
      "SELECT Re.reservation_id, U.nome AS user_nome, Re.committente_id, Re.room_id, R.prezzo, " +
      "Re.check_in, Re.check_out, Re.status, Re.note, Re.giorni, " +
      "(R.prezzo * COALESCE(Re.giorni, 1)) AS totale " +
      "FROM Reservation Re " +
      "INNER JOIN User U ON U.user_counter = Re.user_id " +
      "INNER JOIN Room R ON R.room_id = Re.room_id " +
      "ORDER BY Re.reservation_id DESC;";

  public static String queryGetReservationDettagliByUser =
      "SELECT Re.reservation_id, U.nome AS user_nome, Re.committente_id, Re.room_id, R.prezzo, " +
      "Re.check_in, Re.check_out, Re.status, Re.note, Re.giorni, " +
      "(R.prezzo * COALESCE(Re.giorni, 1)) AS totale " +
      "FROM Reservation Re " +
      "INNER JOIN User U ON U.user_counter = Re.user_id " +
      "INNER JOIN Room R ON R.room_id = Re.room_id " +
      "WHERE Re.user_id = ? " +
      "ORDER BY Re.reservation_id DESC;";

  public static String queryGetFattureDettagliateByUser =
      "SELECT F.fattura_id, F.reservation_id, U.nome, R.numero_stanza, " +
      "F.importo, F.data_emissione, F.stato, Res.status " +
      "FROM Fattura F " +
      "JOIN Reservation Res ON Res.reservation_id = F.reservation_id " +
      "JOIN User U ON U.user_counter = Res.user_id " +
      "JOIN Room R ON R.room_id = Res.room_id " +
      "WHERE Res.user_id = ? " +
      "ORDER BY F.fattura_id DESC;";
}
