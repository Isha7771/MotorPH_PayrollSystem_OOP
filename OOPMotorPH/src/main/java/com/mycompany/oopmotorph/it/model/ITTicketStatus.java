package com.mycompany.oopmotorph.it.model;

public enum ITTicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED;

    public static ITTicketStatus fromString(String raw) {
        if (raw == null) return OPEN;
        String s = raw.trim().toUpperCase().replace(' ', '_');
        for (ITTicketStatus value : values()) {
            if (value.name().equals(s)) return value;
        }
        return OPEN;
    }
}
