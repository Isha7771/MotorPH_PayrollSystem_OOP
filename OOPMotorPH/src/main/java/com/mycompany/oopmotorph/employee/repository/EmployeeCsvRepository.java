package com.mycompany.oopmotorph.employee.repository;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.employee.model.EmployeeFactory;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeCsvRepository implements EmployeeRepository {

    private final Path csvPath;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    private static final String HEADER =
            "Employee #,Last Name,First Name,Email,Birthday,Address,Phone Number,SSS #,Philhealth #,TIN #,Pag-ibig #,"
                    + "Status,Position,Immediate Supervisor,Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,"
                    + "Gross Semi-monthly Rate,Hourly Rate";

    public EmployeeCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<EmployeeRecord> findAll() throws IOException {
        if (!Files.exists(csvPath)) return Collections.emptyList();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String headerLine = br.readLine();
            if (headerLine == null) return Collections.emptyList();

            Map<String, Integer> idx = indexMap(CsvUtils.splitCsvLine(headerLine));
            List<EmployeeRecord> out = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] cells = CsvUtils.splitCsvLine(line);
                EmployeeRecord r = EmployeeFactory.create(get(cells, idx, "Status"));
                r.setEmployeeNo(get(cells, idx, "Employee #"));
                r.setLastName(get(cells, idx, "Last Name"));
                r.setFirstName(get(cells, idx, "First Name"));
                r.setEmail(firstNonBlank(
                        get(cells, idx, "Email"),
                        buildFallbackEmail(r.getEmployeeNo())
                ));
                r.setBirthday(parseDate(get(cells, idx, "Birthday")));
                r.setAddress(get(cells, idx, "Address"));
                r.setPhoneNumber(get(cells, idx, "Phone Number"));
                r.setSssNo(normalizeNumericId(get(cells, idx, "SSS #")));
                r.setPhilhealthNo(normalizeNumericId(get(cells, idx, "Philhealth #")));
                r.setTinNo(normalizeNumericId(get(cells, idx, "TIN #")));
                r.setPagibigNo(normalizeNumericId(get(cells, idx, "Pag-ibig #")));
                r.setStatus(get(cells, idx, "Status"));
                r.setPosition(get(cells, idx, "Position"));
                r.setImmediateSupervisor(get(cells, idx, "Immediate Supervisor"));
                r.setBasicSalary(parseMoney(get(cells, idx, "Basic Salary")));
                r.setRiceSubsidy(parseMoney(get(cells, idx, "Rice Subsidy")));
                r.setPhoneAllowance(parseMoney(get(cells, idx, "Phone Allowance")));
                r.setClothingAllowance(parseMoney(get(cells, idx, "Clothing Allowance")));
                r.setGrossSemiMonthlyRate(parseMoney(get(cells, idx, "Gross Semi-monthly Rate")));
                r.setHourlyRate(parseMoney(get(cells, idx, "Hourly Rate")));
                out.add(r);
            }
            return out;
        }
    }

    @Override
    public Optional<EmployeeRecord> findByEmployeeNo(String employeeNo) throws IOException {
        if (employeeNo == null) return Optional.empty();
        String key = employeeNo.trim();
        for (EmployeeRecord r : findAll()) {
            if (safe(r.getEmployeeNo()).equalsIgnoreCase(key)) return Optional.of(r);
        }
        return Optional.empty();
    }

    @Override
    public void add(EmployeeRecord employee) throws IOException {
        ensureFileExists();
        String empNo = safe(employee.getEmployeeNo());
        if (empNo.isEmpty()) throw new IllegalArgumentException("Employee # is required.");
        for (EmployeeRecord r : findAll()) {
            if (safe(r.getEmployeeNo()).equalsIgnoreCase(empNo)) {
                throw new IllegalArgumentException("Employee # already exists: " + empNo);
            }
        }
        List<EmployeeRecord> all = findAll();
        all.add(employee);
        writeAll(all);
    }

    @Override
    public void update(EmployeeRecord employee) throws IOException {
        ensureFileExists();
        String empNo = safe(employee.getEmployeeNo());
        if (empNo.isEmpty()) throw new IllegalArgumentException("Employee # is required.");

        List<EmployeeRecord> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (safe(all.get(i).getEmployeeNo()).equalsIgnoreCase(empNo)) {
                all.set(i, employee);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Employee # not found: " + empNo);
        writeAll(all);
    }

    @Override
    public void delete(String employeeNo) throws IOException {
        ensureFileExists();
        String key = safe(employeeNo);
        if (key.isEmpty()) throw new IllegalArgumentException("Employee # is required.");

        List<EmployeeRecord> all = findAll();
        boolean removed = all.removeIf(e -> safe(e.getEmployeeNo()).equalsIgnoreCase(key));
        if (!removed) throw new IllegalArgumentException("Employee # not found: " + key);
        writeAll(all);
    }

    private void writeAll(List<EmployeeRecord> rows) throws IOException {
        Files.createDirectories(csvPath.getParent());
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write(HEADER);
            bw.newLine();
            for (EmployeeRecord r : rows) {
                bw.write(String.join(",",
                        CsvUtils.escapeCsv(safe(r.getEmployeeNo())),
                        CsvUtils.escapeCsv(safe(r.getLastName())),
                        CsvUtils.escapeCsv(safe(r.getFirstName())),
                        CsvUtils.escapeCsv(safe(r.getEmail())),
                        CsvUtils.escapeCsv(formatDate(r.getBirthday())),
                        CsvUtils.escapeCsv(safe(r.getAddress())),
                        CsvUtils.escapeCsv(safe(r.getPhoneNumber())),
                        CsvUtils.escapeCsv(safe(r.getSssNo())),
                        CsvUtils.escapeCsv(safe(r.getPhilhealthNo())),
                        CsvUtils.escapeCsv(safe(r.getTinNo())),
                        CsvUtils.escapeCsv(safe(r.getPagibigNo())),
                        CsvUtils.escapeCsv(safe(r.getStatus())),
                        CsvUtils.escapeCsv(safe(r.getPosition())),
                        CsvUtils.escapeCsv(safe(r.getImmediateSupervisor())),
                        formatMoney(r.getBasicSalary()),
                        formatMoney(r.getRiceSubsidy()),
                        formatMoney(r.getPhoneAllowance()),
                        formatMoney(r.getClothingAllowance()),
                        formatMoney(r.getGrossSemiMonthlyRate()),
                        formatMoney(r.getHourlyRate())
                ));
                bw.newLine();
            }
        }
    }

    private void ensureFileExists() throws IOException {
        if (!Files.exists(csvPath)) {
            Files.createDirectories(csvPath.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
                bw.write(HEADER);
                bw.newLine();
            }
        }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim(), dateFmt);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatDate(LocalDate d) {
        return (d == null) ? "" : dateFmt.format(d);
    }

    private double parseMoney(String s) {
        if (s == null || s.isBlank()) return 0.0;
        String cleaned = s.trim().replace(",", "").replace("\"", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String formatMoney(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        return String.valueOf(v);
    }

    private Map<String, Integer> indexMap(String[] headers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            map.put(headers[i].trim(), i);
        }
        return map;
    }

    private String get(String[] cells, Map<String, Integer> idx, String col) {
        Integer i = idx.get(col);
        if (i == null || i < 0 || i >= cells.length) return "";
        return CsvUtils.unquote(cells[i]);
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private String firstNonBlank(String first, String fallback) {
        return safe(first).isEmpty() ? safe(fallback) : safe(first);
    }

    private String buildFallbackEmail(String employeeNo) {
        String empNo = safe(employeeNo);
        return empNo.isEmpty() ? "" : (empNo + "@motorph.local");
    }

    private String normalizeNumericId(String raw) {
        String value = safe(raw);
        if (value.isEmpty()) return "";
        if (value.matches("[0-9.E+-]+")) {
            try {
                java.math.BigDecimal bd = new java.math.BigDecimal(value);
                return bd.stripTrailingZeros().toPlainString();
            } catch (Exception ignored) {
            }
        }
        return value;
    }
}
