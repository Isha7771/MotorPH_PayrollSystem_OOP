package com.mycompany.oopmotorph.employee.model;

import java.util.Locale;

public final class EmployeeFactory {
    private EmployeeFactory() {}

    public static EmployeeRecord create(String status) {
        String s = status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
        if (s.contains("probation")) {
            return new ProbationaryEmployee();
        }
        return new RegularEmployee();
    }
}
