package it.collegio.enums;

public enum FatturaStatus {

    PAGATA("pagato"),
    NON_PAGATA("non pagato"),
    IN_ATTESA("in attesa");

    private final String dbValue;

    FatturaStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static FatturaStatus fromDb(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (FatturaStatus s : values()) {
            if (s.dbValue.equalsIgnoreCase(dbValue)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Stato fattura sconosciuto: " + dbValue);
    }
}
