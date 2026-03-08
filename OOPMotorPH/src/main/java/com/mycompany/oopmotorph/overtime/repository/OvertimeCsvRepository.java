/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopmotorph.overtime.repository;

import com.mycompany.oopmotorph.overtime.model.OvertimeRequest;
import com.mycompany.oopmotorph.overtime.model.OvertimeStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class OvertimeCsvRepository implements OvertimeRepository {

    private final Path csvPath;

    private static final String HEADER =
            "Request ID,Employee Name,Date,No. of Requests,Hours,Status,Actions,Reasons";

    public OvertimeCsvRepository(Path csvPath) {
        this.csvPath = Objects.requireNonNull(csvPath, "csvPath");
        ensureFile();
    }

    @Override
    public List<OvertimeRequest> findAll() {
        ensureFile();
        List<OvertimeRequest> out = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;

            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);
                if (p.length < 6) continue;

                String requestId = safe(p, 0);
                String employeeName = safe(p, 1);
                String date = safe(p, 2);
                String hours = safe(p, 4);
                OvertimeStatus status = OvertimeStatus.fromString(safe(p, 5));
                String reason = (p.length >= 8) ? safe(p, 7) : "";

                if (requestId.isBlank() || employeeName.isBlank()) continue;
                if (status == null) status = OvertimeStatus.PENDING;

                out.add(new OvertimeRequest(requestId, employeeName, date, hours, status, reason));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading Overtime.csv: " + e.getMessage(), e);
        }

        return out;
    }

    @Override
    public Optional<OvertimeRequest> findById(String requestId) {
        if (requestId == null) return Optional.empty();
        String key = requestId.trim();
        return findAll().stream()
                .filter(r -> r.getRequestId().equalsIgnoreCase(key))
                .findFirst();
    }

    @Override
    public void saveAll(List<OvertimeRequest> requests) {
        ensureFile();

        try (BufferedWriter bw = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8)) {
            bw.write(HEADER);
            bw.newLine();

            if (requests != null) {
                for (OvertimeRequest r : requests) {
                    validateRow(r);

                    String row = String.join(",",
                            csv(r.getRequestId()),
                            csv(r.getEmployeeName()),
                            csv(r.getDate()),
                            "1",
                            csv(r.getHours()),
                            csv(defaultStatus(r.getStatus()).name()),
                            "",
                            csv(r.getReason())
                    );
                    bw.write(row);
                    bw.newLine();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error writing Overtime.csv: " + e.getMessage(), e);
        }
    }

    @Override
    public void add(OvertimeRequest request) {
        validateRow(request);

        List<OvertimeRequest> all = findAll();
        boolean exists = all.stream().anyMatch(r -> r.getRequestId().equalsIgnoreCase(request.getRequestId()));
        if (exists) throw new IllegalArgumentException("Overtime Request ID already exists: " + request.getRequestId());

        all.add(request);
        saveAll(all);
    }

    @Override
    public List<OvertimeRequest> findByEmployeeNo(String employeeKey) {
        String key = employeeKey == null ? "" : employeeKey.trim().toLowerCase();
        return findAll().stream()
                .filter(r -> {
                    String name = r.getEmployeeName() == null ? "" : r.getEmployeeName().trim().toLowerCase();
                    return key.isEmpty() || name.contains(key);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void append(OvertimeRequest request) {
        add(request);
    }

    private void ensureFile() {
        try {
            if (Files.notExists(csvPath)) {
                Files.createDirectories(csvPath.getParent());
                Files.writeString(csvPath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot create Overtime.csv: " + e.getMessage(), e);
        }
    }

    private static void validateRow(OvertimeRequest r) {
        if (r == null) throw new IllegalArgumentException("Overtime request is required.");
        if (r.getRequestId() == null || r.getRequestId().trim().isEmpty())
            throw new IllegalArgumentException("Request ID is required.");
        if (r.getEmployeeName() == null || r.getEmployeeName().trim().isEmpty())
            throw new IllegalArgumentException("Employee name is required.");
    }

    private static OvertimeStatus defaultStatus(OvertimeStatus st) {
        return st == null ? OvertimeStatus.PENDING : st;
    }

    private static String safe(String[] p, int idx) {
        if (idx < 0 || idx >= p.length) return "";
        return p[idx] == null ? "" : p[idx].trim();
    }

    private static String csv(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").trim();
    }
}