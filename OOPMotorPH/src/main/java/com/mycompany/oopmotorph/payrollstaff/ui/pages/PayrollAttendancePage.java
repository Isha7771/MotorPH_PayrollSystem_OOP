package com.mycompany.oopmotorph.payrollstaff.ui.pages;

import com.mycompany.oopmotorph.payrollstaff.model.PayrollAttendanceSummary;
import com.mycompany.oopmotorph.payrollstaff.service.PayrollAttendanceService;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class PayrollAttendancePage extends JPanel {
    private final PayrollAttendanceService payrollAttendanceService;
    private final JTextField txtSearch = new JTextField(18);
    private final JDateChooser fromChooser = new JDateChooser();
    private final JDateChooser toChooser = new JDateChooser();
    private final JButton btnGenerate = new JButton("Generate");
    private final JTable tbl = new JTable();
    private final DefaultTableModel model;

    public PayrollAttendancePage(PayrollAttendanceService payrollAttendanceService) {
        this.payrollAttendanceService = payrollAttendanceService;
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:")); top.add(txtSearch);
        top.add(new JLabel("From:")); fromChooser.setDateFormatString("M/d/yyyy"); fromChooser.setDate(null); fromChooser.setPreferredSize(new Dimension(130, 25)); top.add(fromChooser);
        top.add(new JLabel("To:")); toChooser.setDateFormatString("M/d/yyyy"); toChooser.setDate(null); toChooser.setPreferredSize(new Dimension(130, 25)); top.add(toChooser);
        top.add(btnGenerate); add(top, BorderLayout.NORTH);
        model = new DefaultTableModel(new Object[]{"Employee #", "Employee Name", "Present", "Late", "Absent", "Total Hours"}, 0) { @Override public boolean isCellEditable(int row, int col) { return false; } };
        tbl.setModel(model); add(new JScrollPane(tbl), BorderLayout.CENTER);
        btnGenerate.addActionListener(e -> loadTable()); txtSearch.addActionListener(e -> loadTable()); loadTable();
    }
    public PayrollAttendancePage() { this(null); }
    private void loadTable() {
        try {
            if (payrollAttendanceService == null) return;
            LocalDate from = toLocalDate(fromChooser.getDate()); LocalDate to = toLocalDate(toChooser.getDate());
            List<PayrollAttendanceSummary> rows = payrollAttendanceService.summarize(from, to, txtSearch.getText());
            model.setRowCount(0);
            for (PayrollAttendanceSummary r : rows) model.addRow(new Object[]{r.getEmployeeId(), r.getEmployeeName(), r.getPresentCount(), r.getLateCount(), r.getAbsentCount(), r.getTotalHours()});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate attendance summary.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private LocalDate toLocalDate(java.util.Date date) { return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); }
}
