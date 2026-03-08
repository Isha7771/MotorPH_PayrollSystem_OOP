package com.mycompany.oopmotorph.employee.ui.pages;

import com.mycompany.oopmotorph.it.model.ITSupportTicket;
import com.mycompany.oopmotorph.it.service.ITSupportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EmployeeITSupportPage extends JPanel {
    private final String employeeNo;
    private final String employeeName;
    private final ITSupportService service;
    private final JComboBox<String> cmbCategory = new JComboBox<>(new String[]{"Password Reset", "Username Update", "System Access", "Bug Report", "Hardware / Device", "Other"});
    private final JTextArea txtDescription = new JTextArea(5, 30);
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Ticket ID", "Category", "Status", "Created", "Assigned To"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public EmployeeITSupportPage(String employeeNo, String employeeName, ITSupportService service) {
        this.employeeNo = employeeNo;
        this.employeeName = employeeName;
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        refresh();
    }

    private JComponent buildForm() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSubmit = new JButton("Submit Ticket");
        JButton btnRefresh = new JButton("Refresh My Tickets");
        btnSubmit.addActionListener(e -> submit());
        btnRefresh.addActionListener(e -> refresh());
        fields.add(new JLabel("Category"));
        fields.add(cmbCategory);
        fields.add(btnSubmit);
        fields.add(btnRefresh);
        panel.add(fields, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtDescription), BorderLayout.CENTER);
        return panel;
    }

    private void submit() {
        try {
            service.submitTicket(employeeNo, employeeName, String.valueOf(cmbCategory.getSelectedItem()), txtDescription.getText());
            txtDescription.setText("");
            refresh();
            JOptionPane.showMessageDialog(this, "IT ticket submitted.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "IT Support", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        try {
            List<ITSupportTicket> tickets = service.findByEmployee(employeeNo);
            model.setRowCount(0);
            for (ITSupportTicket t : tickets) {
                model.addRow(new Object[]{t.getTicketId(), t.getCategory(), t.getStatus(), t.getCreatedDate(), t.getAssignedTo()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "IT Support", JOptionPane.ERROR_MESSAGE);
        }
    }
}
