package com.mycompany.oopmotorph.auth;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.employee.repository.EmployeeCsvRepository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UserCsvBootstrapper {

    private static final String HEADER =
            "Username,Password,Role,EmployeeNo,LastName,FirstName,Position,Category";

    public static void ensureUsersCsv(Path usersCsvPath, Path employeeDataCsv) throws IOException {
        if (usersCsvPath.getParent() != null) Files.createDirectories(usersCsvPath.getParent());
        if (Files.notExists(usersCsvPath) || Files.size(usersCsvPath) == 0) {
            Files.writeString(usersCsvPath, HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
        }

        Set<String> existing = new HashSet<>();
        CsvUserRepository repo = new CsvUserRepository(usersCsvPath);
        for (User u : repo.findAll()) {
            existing.add(u.getUsername().toLowerCase(Locale.ROOT));
        }

        EmployeeCsvRepository empRepo = new EmployeeCsvRepository(employeeDataCsv);
        List<EmployeeRecord> employees = empRepo.findAll();
        RoleMapper roleMapper = new RoleMapper();

        try (BufferedWriter bw = Files.newBufferedWriter(usersCsvPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            for (EmployeeRecord e : employees) {
                String empNo = safe(e.getEmployeeNo());
                if (empNo.isEmpty()) continue;

                String username = empNo;
                if (existing.contains(username.toLowerCase(Locale.ROOT))) continue;

                bw.write(String.join(",",
                        CsvUtils.escapeCsv(username),
                        CsvUtils.escapeCsv("MotorPH@" + empNo),
                        CsvUtils.escapeCsv(roleMapper.fromPosition(e.getPosition()).name()),
                        CsvUtils.escapeCsv(empNo),
                        CsvUtils.escapeCsv(safe(e.getLastName())),
                        CsvUtils.escapeCsv(safe(e.getFirstName())),
                        CsvUtils.escapeCsv(safe(e.getPosition())),
                        CsvUtils.escapeCsv(normalizeCategory(e.getStatus()))
                ));
                bw.newLine();
                existing.add(username.toLowerCase(Locale.ROOT));
            }
        }
    }

    private static String normalizeCategory(String statusRaw) {
        String s = safe(statusRaw).toLowerCase(Locale.ROOT);
        if (s.contains("regular")) return "Regular";
        return "Probationary";
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
