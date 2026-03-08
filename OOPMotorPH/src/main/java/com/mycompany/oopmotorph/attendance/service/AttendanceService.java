/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.attendance.service;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.model.AttendanceStatus;
import com.mycompany.oopmotorph.attendance.repository.AttendanceRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class AttendanceService {

    private final AttendanceRepository attendanceRepo;

    // Late rule: after 9:00 AM
    private static final LocalTime LATE_CUTOFF = LocalTime.of(9, 0);

    public AttendanceService(AttendanceRepository attendanceRepo) {
        this.attendanceRepo = Objects.requireNonNull(attendanceRepo, "attendanceRepo");
    }

    public List<AttendanceRecord> getAttendance(String search, LocalDate date, AttendanceStatus statusOrNull) {
        String q = (search == null) ? "" : search.trim();

        return attendanceRepo.findAll().stream()
                // date filter (if chosen)
                .filter(r -> date == null || (r != null && date.equals(r.getDate())))
                // compute status/remarks based on time-in/out
                .map(this::applyRulesSafe)
                // search filter
                .filter(r -> matchesSearch(r, q))
                // status filter
                .filter(r -> matchesStatus(r, statusOrNull))
                // stable sorting (null-safe)
                .sorted(Comparator
                        .comparing(AttendanceRecord::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(AttendanceRecord::getEmployeeId, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());
    }

    public List<AttendanceRecord> getAttendance(String search, LocalDate date, String statusTextOrAll) {
        AttendanceStatus st = parseStatus(statusTextOrAll);
        return getAttendance(search, date, st);
    }

    private AttendanceStatus parseStatus(String statusTextOrAll) {
        if (statusTextOrAll == null) return null;
        String s = statusTextOrAll.trim().toUpperCase(Locale.ROOT);
        if (s.isEmpty() || s.equals("ALL")) return null;

        try {
            return AttendanceStatus.valueOf(s);
        } catch (Exception ignored) {
            return null; // invalid status text -> treat as All (safe)
        }
    }

    private AttendanceRecord applyRulesSafe(AttendanceRecord r) {
        if (r == null) return null;

        AttendanceStatus status;
        String remarks;

        if (r.getTimeIn() == null || r.getTimeOut() == null) {
            status = AttendanceStatus.ABSENT;
            remarks = "No time in/out";
        } else if (r.getTimeIn().isAfter(LATE_CUTOFF)) {
            status = AttendanceStatus.LATE;
            remarks = "Late time-in";
        } else {
            status = AttendanceStatus.PRESENT;
            remarks = "On time";
        }

        return new AttendanceRecord(
                r.getEmployeeId(),
                r.getEmployeeName(),
                r.getDate(),
                r.getPosition(),
                r.getTimeIn(),
                r.getTimeOut(),
                status,
                remarks
        );
    }

    private boolean matchesSearch(AttendanceRecord r, String search) {
        if (r == null) return false;
        if (search == null || search.isBlank()) return true;

        String q = search.trim().toLowerCase(Locale.ROOT);
        return contains(r.getEmployeeId(), q)
                || contains(r.getEmployeeName(), q)
                || contains(r.getPosition(), q);
    }

    private boolean matchesStatus(AttendanceRecord r, AttendanceStatus statusOrNull) {
        if (r == null) return false;
        if (statusOrNull == null) return true;
        return statusOrNull == r.getStatus();
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(q);
    }
}