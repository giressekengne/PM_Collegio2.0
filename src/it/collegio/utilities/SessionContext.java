package it.collegio.utilities;

public class SessionContext {

    public static String userCounter   = null;
    public static String email         = null;
    public static String nome          = null;
    public static String cognome       = null;
    public static int    logId         = -1;
    public static int    sessionId     = -1;
    public static int    roleId        = -1;
    public static String roleNome      = null;
    public static String roleType      = null;
    public static int    committenteId = -1;
    public static String sessionToken  = null;

    private SessionContext() {
    }

    public static void clear() {
        userCounter   = null;
        email         = null;
        nome          = null;
        cognome       = null;
        logId         = -1;
        sessionId     = -1;
        roleId        = -1;
        roleNome      = null;
        roleType      = null;
        committenteId = -1;
        sessionToken  = null;
    }

    public static boolean isLoggedIn() {
        return userCounter != null && sessionToken != null;
    }
}
