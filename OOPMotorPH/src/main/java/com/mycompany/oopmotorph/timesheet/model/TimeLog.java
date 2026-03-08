/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class TimeLog {

    private final String employeeNumber;
    private final String lastName;
    private final String firstName;
    private final LocalDate date;
    private final LocalTime timeIn;
    private final LocalTime timeOut;

    public TimeLog(String employeeNumber, String lastName, String firstName,
                   LocalDate date, LocalTime timeIn, LocalTime timeOut) {

        this.employeeNumber = reqDigits(employeeNumber, "Employee #");
        this.lastName = safe(lastName);
        this.firstName = safe(firstName);

        if (date == null) throw new IllegalArgumentException("Date is required.");
        this.date = date;

        this.timeIn = timeIn;
        this.timeOut = timeOut;
        
        if (this.timeIn != null && this.timeOut != null && this.timeOut.isBefore(this.timeIn)) {
            throw new IllegalArgumentException("Time Out cannot be earlier than Time In.");
        }
    }

    public String getEmployeeNumber() { return employeeNumber; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }

    public String getFullName() {
        String ln = safe(lastName);
        String fn = safe(firstName);
        if (ln.isBlank() && fn.isBlank()) return "";
        if (ln.isBlank()) return fn;
        if (fn.isBlank()) return ln;
        return ln + ", " + fn;
    }

    public double getTotalHours() {
        if (timeIn == null || timeOut == null) return 0.0;
        long mins = Duration.between(timeIn, timeOut).toMinutes();
        if (mins < 0) return 0.0;
        return mins / 60.0;
    }

    public String getTotalHoursText() {
        if (timeIn == null || timeOut == null) return "";
        long mins = Duration.between(timeIn, timeOut).toMinutes();
        if (mins < 0) return "";
        return String.format("%d:%02d", mins / 60, mins % 60);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static String reqDigits(String s, String field) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        String v = s.trim();
        if (!v.matches("\\d+")) throw new IllegalArgumentException(field + " must be numbers only.");
        return v;
    }
}