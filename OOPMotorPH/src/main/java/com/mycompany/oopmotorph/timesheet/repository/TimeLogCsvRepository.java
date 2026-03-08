/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.timesheet.repository;

import com.mycompany.oopmotorph.timesheet.model.TimeLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeLogCsvRepository implements TimeLogRepository {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    private static final String HEADER = "Employee #,Last Name,First Name,Date,Log In,Log Out";

    private final Path csvPath;

    public TimeLogCsvRepository(Path csvPath) {
        this.csvPath = Objects.requireNonNull(csvPath, "csvPath");
    }

    @Override
    public List<TimeLog> findAll() {
        if (!Files.exists(csvPath)) return List.of();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String header = br.readLine();
            if (header == null) return List.of();

            List<TimeLog> result = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] c = line.split(",", -1);

                String empNo = get(c, 0);
                String last = get(c, 1);
                String first = get(c, 2);
                LocalDate date = parseDate(get(c, 3));
                LocalTime in = parseTime(get(c, 4));
                LocalTime out = parseTime(get(c, 5));

                // ✅ strict: ignore invalid/malformed rows
                if (!isDigits(empNo) || date == null) continue;

                // ✅ if both exist, ignore invalid ordering rows
                if (in != null && out != null && out.isBefore(in)) continue;

                result.add(new TimeLog(empNo, last, first, date, in, out));
            }
            return result;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read: " + csvPath, e);
        }
    }

    @Override
    public void upsertTimeIn(String employeeNo, String lastName, String firstName, LocalDate date, LocalTime timeIn) {
        ensureFileWithHeader();

        String empNo = reqDigits(employeeNo, "Employee #");
        if (date == null) throw new IllegalArgumentException("Date is required.");
        if (timeIn == null) throw new IllegalArgumentException("Time In is required.");

        List<String> lines = readAllLinesSafe();
        String header = lines.get(0);
        List<String> body = new ArrayList<>(lines.subList(1, lines.size()));

        boolean updated = false;
        for (int i = 0; i < body.size(); i++) {
            String[] c = body.get(i).split(",", -1);
            if (matchRow(c, empNo, date)) {
                c = ensureLen(c, 6);

                if (isBlank(c[4])) {
                    c[4] = TIME_FMT.format(timeIn);
                }

                if (isBlank(c[1])) c[1] = safe(lastName);
                if (isBlank(c[2])) c[2] = safe(firstName);

                body.set(i, String.join(",", c));
                updated = true;
                break;
            }
        }

        if (!updated) {
            String row = String.join(",",
                    empNo,
                    safe(lastName),
                    safe(firstName),
                    DATE_FMT.format(date),
                    TIME_FMT.format(timeIn),
                    ""
            );
            body.add(row);
        }

        writeAll(header, body);
    }

    @Override
    public void upsertTimeOut(String employeeNo, LocalDate date, LocalTime timeOut) {
        ensureFileWithHeader();

        String empNo = reqDigits(employeeNo, "Employee #");
        if (date == null) throw new IllegalArgumentException("Date is required.");
        if (timeOut == null) throw new IllegalArgumentException("Time Out is required.");

        List<String> lines = readAllLinesSafe();
        String header = lines.get(0);
        List<String> body = new ArrayList<>(lines.subList(1, lines.size()));

        int idx = findRowIndex(body, empNo, date);
        if (idx < 0) {
            throw new IllegalArgumentException("Cannot Time Out: no Time In record found for today.");
        }

        String[] c = body.get(idx).split(",", -1);
        c = ensureLen(c, 6);

        LocalTime existingIn = parseTime(get(c, 4));
        if (existingIn == null) {
            throw new IllegalArgumentException("Cannot Time Out: Time In is missing for today.");
        }

        if (!isBlank(c[5])) {
            throw new IllegalArgumentException("Time Out already recorded for today.");
        }

        if (timeOut.isBefore(existingIn)) {
            throw new IllegalArgumentException("Time Out cannot be earlier than Time In.");
        }

        c[5] = TIME_FMT.format(timeOut);
        body.set(idx, String.join(",", c));

        writeAll(header, body);
    }


    private int findRowIndex(List<String> body, String empNo, LocalDate date) {
        for (int i = 0; i < body.size(); i++) {
            String[] c = body.get(i).split(",", -1);
            if (matchRow(c, empNo, date)) return i;
        }
        return -1;
    }

    private boolean matchRow(String[] c, String empNo, LocalDate date) {
        String rowEmp = get(c, 0);
        LocalDate rowDate = parseDate(get(c, 3));
        return rowEmp.equals(empNo) && Objects.equals(rowDate, date);
    }

    private void ensureFileWithHeader() {
        try {
            if (!Files.exists(csvPath)) {
                Files.createDirectories(csvPath.getParent());
                Files.writeString(csvPath, HEADER + System.lineSeparator());
                return;
            }
            if (Files.size(csvPath) == 0) {
                Files.writeString(csvPath, HEADER + System.lineSeparator(), StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to init file: " + csvPath, e);
        }
    }

    private List<String> readAllLinesSafe() {
        try {
            return Files.readAllLines(csvPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read: " + csvPath, e);
        }
    }

    private void writeAll(String header, List<String> body) {
        try {
            List<String> out = new ArrayList<>();
            out.add(header);
            out.addAll(body);
            Files.write(csvPath, out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write: " + csvPath, e);
        }
    }

    private String[] ensureLen(String[] c, int len) {
        if (c.length >= len) return c;
        String[] n = new String[len];
        System.arraycopy(c, 0, n, 0, c.length);
        for (int i = c.length; i < len; i++) n[i] = "";
        return n;
    }

    private String get(String[] c, int idx) {
        if (c == null || idx < 0 || idx >= c.length) return "";
        return c[idx] == null ? "" : c[idx].trim();
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim(), DATE_FMT); } catch (Exception e) { return null; }
    }

    private LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalTime.parse(s.trim(), TIME_FMT); } catch (Exception e) { return null; }
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private static boolean isDigits(String s) {
        return s != null && s.trim().matches("\\d+");
    }

    private static String reqDigits(String s, String field) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        String v = s.trim();
        if (!v.matches("\\d+")) throw new IllegalArgumentException(field + " must be numbers only.");
        return v;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}