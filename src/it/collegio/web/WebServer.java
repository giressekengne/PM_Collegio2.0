package it.collegio.web;

import com.sun.net.httpserver.HttpServer;
import it.collegio.web.handlers.CheckInHandler;
import it.collegio.web.handlers.CheckOutHandler;
import it.collegio.web.handlers.FatturaHandler;
import it.collegio.web.handlers.GestioneFattureHandler;
import it.collegio.web.handlers.HomeHandler;
import it.collegio.web.handlers.Loading1Handler;
import it.collegio.web.handlers.LoadingHandler;
import it.collegio.web.handlers.LoginHandler;
import it.collegio.web.handlers.LogoutHandler;
import it.collegio.web.handlers.LogsHandler;
import it.collegio.web.handlers.ManageRiservationHandler;
import it.collegio.web.handlers.ManageTenantHandler;
import it.collegio.web.handlers.ManageUserHandler;
import it.collegio.web.handlers.PasswordResetHandler;
import it.collegio.web.handlers.RegistrationHandler;
import it.collegio.web.handlers.RoomsHandler;
import it.collegio.web.handlers.StaticHandler;

import java.awt.Desktop;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

/**
 * Mini server HTTP che riusa Controller e DAO di v2 per servire l'interfaccia HTML.
 *
 * Punto didattico: nessuna riga di logica applicativa o di accesso al DB e' duplicata.
 * Gli handler chiamano gli stessi Controller usati dalle JFrame Swing. Questa e' la
 * vera dimostrazione del valore di MVC + DAO: un secondo "front-end" sopra lo stesso core.
 */
public class WebServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Splash / benvenuto
        server.createContext("/",          new LoadingHandler());
        server.createContext("/loading1",  new Loading1Handler());

        // Auth
        server.createContext("/login",        new LoginHandler());
        server.createContext("/registration", new RegistrationHandler());
        server.createContext("/password",     new PasswordResetHandler());
        server.createContext("/logout",       new LogoutHandler());

        // Area autenticata
        server.createContext("/home",              new HomeHandler());
        server.createContext("/rooms",             new RoomsHandler());
        server.createContext("/users",             new ManageUserHandler());
        server.createContext("/tenants",           new ManageTenantHandler());
        server.createContext("/reservation",       new ManageRiservationHandler());
        server.createContext("/checkin",           new CheckInHandler());
        server.createContext("/checkout",          new CheckOutHandler());
        server.createContext("/fattura",           new FatturaHandler());
        server.createContext("/gestione-fatture",  new GestioneFattureHandler());
        server.createContext("/logs",              new LogsHandler());

        // Asset statici
        server.createContext("/static/", new StaticHandler());

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        String url = "http://localhost:" + PORT + "/";
        System.out.println("PM_Collegio v2 HTML view in ascolto su " + url);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ignore) {
            // il server resta su anche se non riesce ad aprire il browser
        }
    }
}
