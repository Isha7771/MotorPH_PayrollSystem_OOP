package com.mycompany.oopmotorph.it.repository;

import com.mycompany.oopmotorph.common.CsvUtils;
import com.mycompany.oopmotorph.it.model.ITSupportTicket;
import com.mycompany.oopmotorph.it.model.ITTicketStatus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ITTicketCsvRepository implements ITTicketRepository {
    private static final String HEADER = "ticketId,employeeNo,employeeName,category,description,status,assignedTo,createdDate,resolvedNotes";
    private final Path csvPath;

    public ITTicketCsvRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<ITSupportTicket> findAll() throws IOException {
        ensureFile();
        List<ITSupportTicket> out = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] c = CsvUtils.splitCsvLine(line);
                ITSupportTicket t = new ITSupportTicket();
                t.setTicketId(get(c,0));
                t.setEmployeeNo(get(c,1));
                t.setEmployeeName(get(c,2));
                t.setCategory(get(c,3));
                t.setDescription(get(c,4));
                t.setStatus(ITTicketStatus.fromString(get(c,5)));
                t.setAssignedTo(get(c,6));
                t.setCreatedDate(get(c,7));
                t.setResolvedNotes(get(c,8));
                out.add(t);
            }
        }
        return out;
    }

    @Override
    public void saveAll(List<ITSupportTicket> tickets) throws IOException {
        ensureFile();
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write(HEADER);
            bw.newLine();
            for (ITSupportTicket t : tickets) {
                bw.write(String.join(",",
                        CsvUtils.escapeCsv(t.getTicketId()),
                        CsvUtils.escapeCsv(t.getEmployeeNo()),
                        CsvUtils.escapeCsv(t.getEmployeeName()),
                        CsvUtils.escapeCsv(t.getCategory()),
                        CsvUtils.escapeCsv(t.getDescription()),
                        CsvUtils.escapeCsv(t.getStatus().name()),
                        CsvUtils.escapeCsv(t.getAssignedTo()),
                        CsvUtils.escapeCsv(t.getCreatedDate()),
                        CsvUtils.escapeCsv(t.getResolvedNotes())
                ));
                bw.newLine();
            }
        }
    }

    @Override
    public void append(ITSupportTicket ticket) throws IOException {
        List<ITSupportTicket> all = findAll();
        all.add(ticket);
        saveAll(all);
    }

    @Override
    public String nextTicketId() throws IOException {
        int max = 0;
        for (ITSupportTicket t : findAll()) {
            String raw = t.getTicketId();
            if (raw != null && raw.startsWith("IT-")) {
                try {
                    max = Math.max(max, Integer.parseInt(raw.substring(3)));
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("IT-%04d", max + 1);
    }

    private void ensureFile() throws IOException {
        if (!Files.exists(csvPath)) {
            Files.createDirectories(csvPath.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
                bw.write(HEADER);
                bw.newLine();
            }
        }
    }

    private String get(String[] c, int i) {
        if (i < 0 || i >= c.length) return "";
        return CsvUtils.unquote(c[i]);
    }
}
