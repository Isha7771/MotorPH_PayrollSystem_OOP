package com.mycompany.oopmotorph.supervisor.ui.pages;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.model.AttendanceStatus;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupervisorAttendancePanel extends JPanel {
    private final AttendanceService attendanceService;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtSearch = new JTextField();
    private final JDateChooser dateChooser = new JDateChooser();
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"All", "Present", "Late", "Absent"});
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnClearDate = new JButton("Clear Date");
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public SupervisorAttendancePanel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));
        add(buildFilterPanel(), BorderLayout.NORTH);
        model = new DefaultTableModel(new Object[]{"Employee #", "Employee Name", "Date", "Department/Position", "Time In", "Time Out", "Total Hours", "Status", "Remarks"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); dateChooser.setDate(null); cmbStatus.setSelectedIndex(0); loadTable(); });
        btnClearDate.addActionListener(e -> { dateChooser.setDate(null); loadTable(); });
        txtSearch.addActionListener(e -> loadTable());
        cmbStatus.addActionListener(e -> loadTable());
        dateChooser.getDateEditor().addPropertyChangeListener(evt -> { if ("date".equals(evt.getPropertyName())) loadTable(); });
        loadTable();
    }

    public SupervisorAttendancePanel() { this(null); }

    private JComponent buildFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));
        txtSearch.setPreferredSize(new Dimension(180, 28));
        dateChooser.setDateFormatString("M/d/yyyy");
        dateChooser.setDate(null);
        dateChooser.setPreferredSize(new Dimension(140, 28));
        cmbStatus.setPreferredSize(new Dimension(110, 28));
        panel.add(new JLabel("Search:")); panel.add(txtSearch);
        panel.add(new JLabel("Date:")); panel.add(dateChooser);
        panel.add(new JLabel("Status:")); panel.add(cmbStatus);
        panel.add(btnClearDate); panel.add(btnRefresh);
        return panel;
    }

    private void loadTable() {
        try {
            if (attendanceService == null) return;
            String q = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
            LocalDate selected = toLocalDate(dateChooser.getDate());
            AttendanceStatus status = parseStatusOrNull((String) cmbStatus.getSelectedItem());
            List<AttendanceRecord> rows = attendanceService.getAttendance(q, selected, status);
            model.setRowCount(0);
            for (AttendanceRecord r : rows) {
                model.addRow(new Object[]{r.getEmployeeId(), r.getEmployeeName(), r.getDate() == null ? "" : r.getDate().format(dateFmt), r.getPosition(), r.getTimeIn() == null ? "" : r.getTimeIn().toString(), r.getTimeOut() == null ? "" : r.getTimeOut().toString(), String.format("%.2f", r.getTotalHours()), r.getStatus().name(), r.getRemarks()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate toLocalDate(java.util.Date d) { return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); }
    private AttendanceStatus parseStatusOrNull(String s) {
        if (s == null) return null;
        return switch (s) { case "Present" -> AttendanceStatus.PRESENT; case "Late" -> AttendanceStatus.LATE; case "Absent" -> AttendanceStatus.ABSENT; default -> null; };
    }
}
