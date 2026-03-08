package com.mycompany.oopmotorph.auth;

import java.util.Locale;

public enum Role {
    ADMIN,
    SUPERVISOR,
    FINANCE,
    HR,
    IT,
    EMPLOYEE;

    public static Role fromString(String text) {
        if (text == null) return EMPLOYEE;

        String s = text.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return switch (s) {
            case "ADMIN" -> ADMIN;
            case "SUPERVISOR" -> SUPERVISOR;
            case "FINANCE", "PAYROLL", "PAYROLL_STAFF" -> FINANCE;
            case "HR", "HR_STAFF" -> HR;
            case "IT", "IT_SUPPORT", "IT_STAFF" -> IT;
            case "EMPLOYEE" -> EMPLOYEE;
            default -> EMPLOYEE;
        };
    }
}
