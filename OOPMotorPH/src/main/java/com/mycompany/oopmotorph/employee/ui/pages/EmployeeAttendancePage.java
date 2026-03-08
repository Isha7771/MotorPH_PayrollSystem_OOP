package com.mycompany.oopmotorph.employee.ui.pages;

import com.mycompany.oopmotorph.attendance.model.AttendanceRecord;
import com.mycompany.oopmotorph.attendance.service.AttendanceService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EmployeeAttendancePage extends JPanel {
    private final String employeeNo;
    private final AttendanceService attendanceService;
    private final JTextField txtSearch = new JTextField(18);
    private final JDateChooser dateChooser = new JDateChooser();
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"All", "Present", "Late", "Absent"});
    private final JButton btnRefresh = new JButton("Refresh");
    private final JTable tbl = new JTable();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Date", "Time In", "Time Out", "Total Hours", "Status", "Remarks"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public EmployeeAttendancePage(String employeeNo, AttendanceService attendanceService) {
        this.employeeNo = employeeNo == null ? "" : employeeNo.trim();
        this.attendanceService = attendanceService;
        buildUI();
        dateChooser.setDate(null);
        wire();
        loadTable();
    }
    public EmployeeAttendancePage(String employeeNo) { this(employeeNo, null); }
    public EmployeeAttendancePage() { this("", null); }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Employee - Attendance");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.add(title, BorderLayout.NORTH);
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.add(new JLabel("Search:")); filters.add(txtSearch);
        filters.add(new JLabel("Date:")); dateChooser.setPreferredSize(new Dimension(140, 26)); filters.add(dateChooser);
        filters.add(new JLabel("Status:")); filters.add(cmbStatus); filters.add(btnRefresh);
        header.add(filters, BorderLayout.SOUTH); add(header, BorderLayout.NORTH);
        tbl.setModel(model); tbl.setRowHeight(24); add(new JScrollPane(tbl), BorderLayout.CENTER);
    }
    private void wire() {
        btnRefresh.addActionListener(e -> loadTable()); txtSearch.addActionListener(e -> loadTable()); cmbStatus.addActionListener(e -> loadTable()); dateChooser.addPropertyChangeListener("date", evt -> loadTable());
    }
    private void loadTable() {
        try {
            if (attendanceService == null) return;
            String search = txtSearch.getText() == null ? "" : txtSearch.getText().trim();
            LocalDate selectedDate = dateChooser.getDate() == null ? null : dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String statusSelected = (String) cmbStatus.getSelectedItem();
            List<AttendanceRecord> filtered = attendanceService.getAttendance(search, selectedDate, statusSelected).stream().filter(r -> sameEmployeeId(r, employeeNo)).collect(Collectors.toList());
            model.setRowCount(0);
            for (AttendanceRecord r : filtered) model.addRow(new Object[]{fmtDate(r.getDate()), fmtTime(r.getTimeIn()), fmtTime(r.getTimeOut()), String.format(Locale.ROOT, "%.2f", r.getTotalHours()), r.getStatus() == null ? "" : r.getStatus().name(), nz(r.getRemarks())});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load attendance:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean sameEmployeeId(AttendanceRecord r, String id) { if (r == null) return false; String a = r.getEmployeeId() == null ? "" : r.getEmployeeId().trim(); return id != null && !id.trim().isEmpty() && a.equalsIgnoreCase(id.trim()); }
    private String fmtDate(LocalDate d) { return d == null ? "" : dateFmt.format(d); }
    private String fmtTime(LocalTime t) { return t == null ? "" : timeFmt.format(t); }
    private static String nz(String s) { return s == null ? "" : s.trim(); }
}
