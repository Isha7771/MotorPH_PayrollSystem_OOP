package com.mycompany.oopmotorph.it.service;

import com.mycompany.oopmotorph.it.model.ITSupportTicket;
import com.mycompany.oopmotorph.it.model.ITTicketStatus;
import com.mycompany.oopmotorph.it.repository.ITTicketRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ITSupportService {
    private final ITTicketRepository repository;

    public ITSupportService(ITTicketRepository repository) {
        this.repository = repository;
    }

    public List<ITSupportTicket> listAll() throws IOException {
        List<ITSupportTicket> all = new ArrayList<>(repository.findAll());
        all.sort(Comparator.comparing(ITSupportTicket::getTicketId).reversed());
        return all;
    }

    public List<ITSupportTicket> search(String keyword, ITTicketStatus status) throws IOException {
        String q = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return listAll().stream()
                .filter(t -> q.isEmpty()
                        || contains(t.getTicketId(), q)
                        || contains(t.getEmployeeNo(), q)
                        || contains(t.getEmployeeName(), q)
                        || contains(t.getCategory(), q)
                        || contains(t.getDescription(), q))
                .filter(t -> status == null || t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<ITSupportTicket> findByEmployee(String employeeNo) throws IOException {
        return listAll().stream()
                .filter(t -> t.matchesRequester(employeeNo))
                .collect(Collectors.toList());
    }

    public void submitTicket(String employeeNo, String employeeName, String category, String description) throws IOException {
        if (employeeNo == null || employeeNo.isBlank()) throw new IllegalArgumentException("Employee # is required.");
        if (employeeName == null || employeeName.isBlank()) throw new IllegalArgumentException("Employee name is required.");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category is required.");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description is required.");

        ITSupportTicket ticket = new ITSupportTicket();
        ticket.setTicketId(repository.nextTicketId());
        ticket.setEmployeeNo(employeeNo);
        ticket.setEmployeeName(employeeName);
        ticket.setCategory(category);
        ticket.setDescription(description);
        ticket.setStatus(ITTicketStatus.OPEN);
        ticket.setCreatedDate(LocalDate.now().toString());
        repository.append(ticket);
    }

    public void updateTicket(String ticketId, ITTicketStatus status, String assignedTo, String notes) throws IOException {
        List<ITSupportTicket> all = new ArrayList<>(repository.findAll());
        boolean found = false;
        for (ITSupportTicket t : all) {
            if (t.getTicketId().equalsIgnoreCase(ticketId)) {
                t.setStatus(status);
                t.setAssignedTo(assignedTo);
                t.setResolvedNotes(notes);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Ticket not found: " + ticketId);
        repository.saveAll(all);
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }
}
