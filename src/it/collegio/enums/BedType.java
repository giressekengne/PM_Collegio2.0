package it.collegio.enums;

public enum BedType {

    MATRIMONIALE("matrimoniale"),
    SINGOLO("singolo"),
    KING_SIZE("king-size");

    private final String dbValue;

    BedType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static BedType fromDb(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (BedType b : values()) {
            if (b.dbValue.equalsIgnoreCase(dbValue)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Tipo letto sconosciuto: " + dbValue);
    }
}
