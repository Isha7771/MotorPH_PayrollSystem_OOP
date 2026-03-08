/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.service;

import com.mycompany.oopmotorph.timesheet.model.TimeLog;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class TimeLogService {

    private final TimeLogRepository repo;

    public TimeLogService(TimeLogRepository repo) {
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    public List<TimeLog> getAll() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(TimeLog::getDate).reversed()
                        .thenComparing(TimeLog::getEmployeeNumber, Comparator.nullsLast(String::compareToIgnoreCase)))
                .toList();
    }

    public void timeIn(String empNo, String last, String first) {
        String emp = reqDigits(empNo, "Employee #");
        String ln = reqName(last, "Last Name");
        String fn = reqName(first, "First Name");

        LocalDate today = LocalDate.now();

        // Encapsulation tightening: service blocks invalid workflow
        TimeLog todays = findToday(emp, today);
        if (todays != null && todays.getTimeIn() != null) {
            throw new IllegalStateException("You already have a Time In record today.");
        }

        repo.upsertTimeIn(emp, ln, fn, today, nowHHMM());
    }

    public void timeOut(String empNo) {
        String emp = reqDigits(empNo, "Employee #");

        LocalDate today = LocalDate.now();

        TimeLog todays = findToday(emp, today);
        if (todays == null || todays.getTimeIn() == null) {
            throw new IllegalStateException("Cannot Time Out without a Time In record today.");
        }
        if (todays.getTimeOut() != null) {
            throw new IllegalStateException("You already have a Time Out record today.");
        }

        repo.upsertTimeOut(emp, today, nowHHMM());
    }


    private TimeLog findToday(String empNo, LocalDate date) {
        for (TimeLog t : repo.findAll()) {
            if (t == null) continue;
            if (date.equals(t.getDate()) && empNo.equalsIgnoreCase(safe(t.getEmployeeNumber()))) {
                return t;
            }
        }
        return null;
    }

    private LocalTime nowHHMM() {
        return LocalTime.now().withSecond(0).withNano(0);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String reqDigits(String s, String field) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        String v = s.trim();
        if (!v.matches("\\d+")) throw new IllegalArgumentException(field + " must contain numbers only.");
        return v;
    }

    private String reqName(String s, String field) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        String v = s.trim();
        if (!v.matches("[A-Za-z][A-Za-z .'-]*")) {
            throw new IllegalArgumentException(field + " must contain letters only (no digits/special chars).");
        }
        return v;
    }
}