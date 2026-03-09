package com.mycompany.oopmotorph.hr.service;

import com.mycompany.oopmotorph.employee.model.EmployeeFactory;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class HrEmployeeManagementService {

    private final EmployeeRepository repo;

    public HrEmployeeManagementService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public List<EmployeeRecord> listAll() throws IOException {
        return repo.findAll();
    }

    public List<EmployeeRecord> search(String keyword) throws IOException {
        String q = (keyword == null) ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return repo.findAll();

        return repo.findAll().stream()
                .filter(e -> contains(e.getEmployeeNo(), q)
                        || contains(e.getLastName(), q)
                        || contains(e.getFirstName(), q)
                        || contains(e.getEmail(), q)
                        || contains(e.getPosition(), q)
                        || contains(e.getStatus(), q))
                .collect(Collectors.toList());
    }

    public void add(EmployeeRecord incoming) throws IOException {
        EmployeeRecord cleaned = validateAndNormalizeHr(incoming);
        Optional<EmployeeRecord> existing = repo.findByEmployeeNo(cleaned.getEmployeeNo());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Employee # already exists: " + cleaned.getEmployeeNo());
        }
        repo.add(cleaned);
    }

    public void updateHr(EmployeeRecord incoming) throws IOException {
        EmployeeRecord cleaned = validateAndNormalizeHr(incoming);
        EmployeeRecord existing = repo.findByEmployeeNo(cleaned.getEmployeeNo())
                .orElseThrow(() -> new IllegalArgumentException("Employee # not found: " + cleaned.getEmployeeNo()));

        existing.setLastName(cleaned.getLastName());
        existing.setFirstName(cleaned.getFirstName());
        existing.setEmail(cleaned.getEmail());
        existing.setBirthday(cleaned.getBirthday());
        existing.setAddress(cleaned.getAddress());
        existing.setPhoneNumber(cleaned.getPhoneNumber());
        existing.setSssNo(cleaned.getSssNo());
        existing.setPhilhealthNo(cleaned.getPhilhealthNo());
        existing.setTinNo(cleaned.getTinNo());
        existing.setPagibigNo(cleaned.getPagibigNo());
        existing.setStatus(cleaned.getStatus());
        existing.setPosition(cleaned.getPosition());
        existing.setImmediateSupervisor(cleaned.getImmediateSupervisor());
        existing.setBasicSalary(cleaned.getBasicSalary());
        existing.setRiceSubsidy(cleaned.getRiceSubsidy());
        existing.setPhoneAllowance(cleaned.getPhoneAllowance());
        existing.setClothingAllowance(cleaned.getClothingAllowance());
        existing.setGrossSemiMonthlyRate(cleaned.getGrossSemiMonthlyRate());
        existing.setHourlyRate(cleaned.getHourlyRate());

        repo.update(existing);
    }

    public void delete(String employeeNo) throws IOException {
        if (isBlank(employeeNo)) throw new IllegalArgumentException("Employee # is required.");
        repo.delete(employeeNo.trim());
    }

    public String nextEmployeeNo() throws IOException {
        int max = 0;
        for (EmployeeRecord e : repo.findAll()) {
            String raw = (e == null) ? null : e.getEmployeeNo();
            if (raw == null || !raw.trim().matches("\\d+")) continue;
            try {
                int n = Integer.parseInt(raw.trim());
                if (n > max) max = n;
            } catch (NumberFormatException ignore) {
            }
        }
        return String.valueOf(max + 1);
    }

    private EmployeeRecord validateAndNormalizeHr(EmployeeRecord e) {
        if (e == null) throw new IllegalArgumentException("Employee record is required.");

        String empNo = reqDigits(e.getEmployeeNo(), "Employee #");
        String last = reqName(e.getLastName(), "Last Name");
        String first = reqName(e.getFirstName(), "First Name");
        String email = reqEmail(e.getEmail(), "Email");
        if (e.getBirthday() == null) throw new IllegalArgumentException("Birthday is required.");
        if (e.getBirthday().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthday cannot be a future date.");
        }

        String statusNorm = normalizeStatus(req(e.getStatus(), "Status"));
        String position = reqText(e.getPosition(), "Position");
        String sup = optName(e.getImmediateSupervisor(), "Immediate Supervisor");

        String phone = reqPattern(e.getPhoneNumber(), "Phone Number", "\\d{3}-\\d{3}-\\d{3}",
                "Must follow ###-###-### (e.g., 786-868-477).");
        String sss = optPattern(e.getSssNo(), "SSS #", "(?:\\d{2}-\\d{7}-\\d{1}|\\d{10})",
                "Use ##-#######-# or plain digits.");
        String phil = reqPattern(e.getPhilhealthNo(), "PhilHealth #", "(?:\\d{2}-\\d{9}-\\d{1}|\\d{12})",
                "Must follow ##-#########-# or plain digits.");
        String tin = reqPattern(e.getTinNo(), "TIN #", "(?:\\d{3}-\\d{3}-\\d{3}-\\d{3}|\\d{12})",
                "Must follow ###-###-###-### or plain digits.");
        String pagibig = optPattern(e.getPagibigNo(), "Pag-IBIG #", "(?:\\d{4}-\\d{4}-\\d{4}|\\d{12})",
                "Use ####-####-#### or plain digits.");

        nonNegative(e.getBasicSalary(), "Basic Salary");
        nonNegative(e.getRiceSubsidy(), "Rice Subsidy");
        nonNegative(e.getPhoneAllowance(), "Phone Allowance");
        nonNegative(e.getClothingAllowance(), "Clothing Allowance");
        nonNegative(e.getGrossSemiMonthlyRate(), "Gross Semi-monthly Rate");
        nonNegative(e.getHourlyRate(), "Hourly Rate");

        double expectedSemiMonthly = round2((e.getBasicSalary() / 2.0) + ((e.getRiceSubsidy() + e.getPhoneAllowance() + e.getClothingAllowance()) / 2.0));
        if (e.getBasicSalary() > 0 && e.getGrossSemiMonthlyRate() > 0 && Math.abs(expectedSemiMonthly - e.getGrossSemiMonthlyRate()) > 0.05) {
            throw new IllegalArgumentException("Gross Semi-monthly Rate must match Basic Salary/2 + total allowances/2.");
        }

        EmployeeRecord cleaned = EmployeeFactory.create(statusNorm);
        cleaned.setEmployeeNo(empNo);
        cleaned.setLastName(last);
        cleaned.setFirstName(first);
        cleaned.setEmail(email);
        cleaned.setBirthday(e.getBirthday());
        cleaned.setAddress(safeTrim(e.getAddress()));
        cleaned.setPhoneNumber(phone);
        cleaned.setSssNo(sss);
        cleaned.setPhilhealthNo(phil);
        cleaned.setTinNo(tin);
        cleaned.setPagibigNo(pagibig);
        cleaned.setStatus(statusNorm);
        cleaned.setPosition(position);
        cleaned.setImmediateSupervisor(sup);
        cleaned.setBasicSalary(e.getBasicSalary());
        cleaned.setRiceSubsidy(e.getRiceSubsidy());
        cleaned.setPhoneAllowance(e.getPhoneAllowance());
        cleaned.setClothingAllowance(e.getClothingAllowance());
        cleaned.setGrossSemiMonthlyRate(e.getGrossSemiMonthlyRate() > 0 ? e.getGrossSemiMonthlyRate() : expectedSemiMonthly);
        cleaned.setHourlyRate(e.getHourlyRate());
        return cleaned;
    }

    private String normalizeStatus(String raw) {
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.equals("regular")) return "Regular";
        if (s.equals("probation") || s.equals("probationary")) return "Probationary";
        throw new IllegalArgumentException("Status must be Regular or Probationary only.");
    }

    private void nonNegative(double v, String field) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            throw new IllegalArgumentException(field + " is invalid.");
        }
        if (v < 0) throw new IllegalArgumentException(field + " cannot be negative.");
    }

    private boolean contains(String field, String q) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(q);
    }

    private String req(String s, String field) {
        if (isBlank(s)) throw new IllegalArgumentException(field + " is required.");
        return s.trim();
    }

    private String reqDigits(String s, String field) {
        String v = req(s, field);
        if (!v.matches("\\d+")) throw new IllegalArgumentException(field + " must contain numbers only.");
        return v;
    }

    private String reqName(String s, String field) {
        String v = req(s, field);
        if (!v.matches("[A-Za-z][A-Za-z .'-]*")) {
            throw new IllegalArgumentException(field + " must contain letters only (no numbers/special chars).");
        }
        return v;
    }

    private String optName(String s, String field) {
        String v = safeTrim(s);
        if (v.isEmpty()) return "";
        if (!v.matches("[A-Za-z][A-Za-z .'-]*")) {
            throw new IllegalArgumentException(field + " must contain letters only (no numbers/special chars).");
        }
        return v;
    }

    private String reqText(String s, String field) {
        String v = req(s, field);
        if (v.matches(".*\\d.*")) {
            throw new IllegalArgumentException(field + " must not contain numbers.");
        }
        return v;
    }

    private String reqEmail(String s, String field) {
        String v = req(s, field);
        if (!v.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(field + " format is invalid.");
        }
        return v;
    }

    private String reqPattern(String s, String field, String regex, String hint) {
        String v = req(s, field);
        if (!v.matches(regex)) {
            throw new IllegalArgumentException(field + " format is invalid. " + hint);
        }
        return v;
    }

    private String optPattern(String s, String field, String regex, String hint) {
        String v = safeTrim(s);
        if (v.isEmpty()) return "";
        if (!v.matches(regex)) {
            throw new IllegalArgumentException(field + " format is invalid. " + hint);
        }
        return v;
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
