Specifica Tecnica - Sistema di Prenotazione Multi-Tenant (ma per ora mi limito a svillupare solo un committente)

Il sistema gestisce la prenotazione di camere per più strutture Collegi universitari (committenti), 
con utenti autenticati e ruoli distinti (admin sistema, admin committente, receptionist, cliente). 
Gli utenti possono prenotare camere, visualizzare le proprie prenotazioni e fatture, e gestire il cambio password. 
Gli admin gestiscono strutture, utenti e disponibilità camere. Ogni prenotazione genera una fattura (checkin + checkout), con pagamenti finto supportati (carta, PayPal, bonifico, contanti). 
Il sistema garantisce autenticazione "sicura"(controlla userid e password), ripristino password e monitoraggio degli accessi. 
gestione della sessione(simile perche la session esiste solo per le app web), gli admin possono anche consultare lo storico delle prenotazione.
 
 
PM_Collegio è un sistema desktop sviluppato in Java (Swing/JFrame) per la gestione delle prenotazioni di camere in collegi universitari. 
L'applicazione supporta uno scenario multi-committente ma nella versione attuale è ottimizzata per un singolo committente. 
Il database di supporto è MySQL (schema pm_collegio).
L'applicazione offre un'interfaccia grafica completa che guida l'operatore attraverso tutte le fasi operative: 
registrazione degli ospiti, assegnazione delle camere, check-in e check-out, generazione e gestione delle fatture, 
e supervisione amministrativa con controllo degli accessi basato su ruolo.

La versione 2 rappresenta il refactor della versione 1 (monolitica) verso una architettura a strati MVC + DAO + DTO.
Obiettivi sono: Separare nettamente UI, logica applicativa e accesso ai dati
Centralizzare le query SQL in un unico punto (QueryContainer )
Eliminare la duplicazione di codice di connessione DB sparso in ogni view
Introdurre transazioni esplicite per operazioni atomiche (CheckIn, CheckOut, audit)
Applicare validazioni e calcoli server-side (controller) invece che inline nelle view

 Regole architetturali
1. La View non parla mai al DB. Chiede al Controller, che chiede al DAO.
2. Il DAO non conosce il dominio applicativo. Riceve e ritorna oggetti, esegue query, basta.
3. Il Controller orchestra. Conosce sia il DAO che gli oggetti applicativi (SessionContext, ecc.), può
gestire transazioni cross-DAO.
4. Le query SQL stanno in 
QueryContainer 
, non sparse nei DAO.
5. I DTO sono read-only, usati quando un'operazione restituisce dati joinati da più tabelle.

