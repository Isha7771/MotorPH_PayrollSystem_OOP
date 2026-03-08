package com.mycompany.oopmotorph.it.ui.pages;

import com.mycompany.oopmotorph.it.model.ITSupportTicket;
import com.mycompany.oopmotorph.it.model.ITTicketStatus;
import com.mycompany.oopmotorph.it.service.ITSupportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ITTicketManagementPage extends JPanel {
    private final ITSupportService service;
    private final String itStaffName;
    private final JTextField txtSearch = new JTextField(18);
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"All", "OPEN", "IN_PROGRESS", "RESOLVED"});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Ticket ID", "Employee #", "Employee Name", "Category", "Status", "Created", "Assigned To"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private List<ITSupportTicket> currentTickets = java.util.Collections.emptyList();

    public ITTicketManagementPage(ITSupportService service, String itStaffName) {
        this.service = service;
        this.itStaffName = itStaffName;
        setLayout(new BorderLayout(10, 10));
        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnUpdate = new JButton("Update Selected Ticket");
        btnRefresh.addActionListener(e -> refresh());
        btnUpdate.addActionListener(e -> updateSelected());
        top.add(new JLabel("Search"));
        top.add(txtSearch);
        top.add(new JLabel("Status"));
        top.add(cmbStatus);
        top.add(btnRefresh);
        top.add(btnUpdate);
        return top;
    }

    private void refresh() {
        try {
            ITTicketStatus status = "All".equals(String.valueOf(cmbStatus.getSelectedItem())) ? null : ITTicketStatus.fromString(String.valueOf(cmbStatus.getSelectedItem()));
            currentTickets = service.search(txtSearch.getText(), status);
            model.setRowCount(0);
            for (ITSupportTicket t : currentTickets) {
                model.addRow(new Object[]{t.getTicketId(), t.getEmployeeNo(), t.getEmployeeName(), t.getCategory(), t.getStatus(), t.getCreatedDate(), t.getAssignedTo()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "IT Tickets", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentTickets.size()) {
            JOptionPane.showMessageDialog(this, "Select a ticket first.", "IT Tickets", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ITSupportTicket ticket = currentTickets.get(row);
        JComboBox<ITTicketStatus> statusBox = new JComboBox<>(ITTicketStatus.values());
        statusBox.setSelectedItem(ticket.getStatus());
        JTextField assignedField = new JTextField(ticket.getAssignedTo().isBlank() ? itStaffName : ticket.getAssignedTo(), 20);
        JTextArea notesArea = new JTextArea(ticket.getResolvedNotes(), 5, 20);
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Status"));
        form.add(statusBox);
        form.add(new JLabel("Assigned To"));
        form.add(assignedField);
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(notesArea), BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Ticket " + ticket.getTicketId(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            service.updateTicket(ticket.getTicketId(), (ITTicketStatus) statusBox.getSelectedItem(), assignedField.getText(), notesArea.getText());
            refresh();
            JOptionPane.showMessageDialog(this, "Ticket updated.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "IT Tickets", JOptionPane.ERROR_MESSAGE);
        }
    }
}
