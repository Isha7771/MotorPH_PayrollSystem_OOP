package com.mycompany.oopmotorph.auth;

import java.util.Locale;

public class RoleMapper {

    public Role fromPosition(String positionRaw) {
        String p = safe(positionRaw).toLowerCase(Locale.ROOT);

        if (p.equals("chief executive officer")
                || p.equals("chief operating officer")
                || p.equals("chief marketing officer")) {
            return Role.ADMIN;
        }

        if (p.equals("chief finance officer")
                || p.contains("payroll")
                || p.contains("accounting")
                || p.contains("finance")) {
            return Role.FINANCE;
        }

        if (p.contains("human resource") || p.startsWith("hr ") || p.equals("hr")) {
            return Role.HR;
        }

        if (p.contains("information technology") || p.startsWith("it ") || p.equals("it") || p.contains("system admin")) {
            return Role.IT;
        }

        if (p.contains("supervisor") || p.contains("manager")) {
            return Role.SUPERVISOR;
        }

        return Role.EMPLOYEE;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
