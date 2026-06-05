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

La versione 3 rappresenta il refactor della versione 1 (monolitica) verso una architettura a strati MVC + DAO + DTO, 
e aggiunge un secondo front-end oltre alle 15 JFrame Swing del package views. 
Si tratta di un'interfaccia web servita da un mini server HTTP embedded, pensata per dimostrare empiricamente i vantaggi dell'architettura MVC + DAO introdotta in v2.
L'idea di fondo è semplice: se la separazione tra View, Controller, DAO e DTO è ben fatta, deve essere possibile scrivere un secondo front-end senza toccare il dominio applicativo. Il package web è la prova pratica di questa affermazione.

Obiettivi sono: Dimostrare la riusabilità dei Controller 
mostrare che lo stesso Controller può servire sia la view Swing che la view HTML, senza modifiche.
Dimostrare la riusabilità dei DAO : nessuna query SQL nuova, tutti gli accessi al DB passano dagli stessi DAO della v2 Swing.
Validare il refactor v1 → v2 : in v1 (architettura monolitica) questo lavoro avrebbe richiesto la riscrittura di tutta la logica applicativa, per ogni nuovo front-end. In v3 il front-end è sostituibile.
Offrire un'esperienza moderna : interfaccia responsive Bootstrap 5, accessibile da qualunque browser sulla stessa macchina.

