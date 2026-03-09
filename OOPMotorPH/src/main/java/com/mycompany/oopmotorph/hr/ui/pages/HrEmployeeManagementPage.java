package com.mycompany.oopmotorph.hr.ui.pages;

import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.mycompany.oopmotorph.hr.service.HrEmployeeManagementService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HrEmployeeManagementPage extends JPanel {

    private final HrEmployeeManagementService hrService;
    private final JTextField txtSearch = new JTextField(22);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnRefresh = new JButton("Refresh");
    private final JButton btnAdd = new JButton("Add");
    private final JButton btnEdit = new JButton("Edit");
    private final JButton btnDelete = new JButton("Delete");
    private final JTable tbl = new JTable();
    private final DefaultTableModel model;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public HrEmployeeManagementPage(HrEmployeeManagementService hrService) {
        setLayout(new BorderLayout(10, 10));
        this.hrService = hrService;

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(txtSearch);
        top.add(btnSearch);
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{
                "Employee #", "Last Name", "First Name", "Email", "Birthday",
                "Phone", "Status", "Position", "Supervisor",
                "Basic Salary", "Allowances", "Gross Semi-monthly", "Hourly Rate"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tbl.setModel(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnAdd);
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadAll());
        btnSearch.addActionListener(e -> search());
        txtSearch.addActionListener(e -> search());
        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        loadAll();
    }

    public HrEmployeeManagementPage() {
        this(null);
    }

    private void loadAll() {
        try {
            render(hrService.listAll());
        } catch (Exception ex) {
            showError("Failed to load employees", ex);
        }
    }

    private void search() {
        try {
            render(hrService.search(txtSearch.getText()));
        } catch (Exception ex) {
            showError("Failed to search employees", ex);
        }
    }

    private void render(List<EmployeeRecord> rows) {
        model.setRowCount(0);
        for (EmployeeRecord e : rows) {
            model.addRow(new Object[]{
                    e.getEmployeeNo(),
                    e.getLastName(),
                    e.getFirstName(),
                    e.getEmail(),
                    formatDate(e.getBirthday()),
                    e.getPhoneNumber(),
                    e.getStatus(),
                    e.getPosition(),
                    e.getImmediateSupervisor(),
                    e.getBasicSalary(),
                    e.getTotalAllowances(),
                    e.getGrossSemiMonthlyRate(),
                    e.getHourlyRate()
            });
        }
    }

    private void onAdd() {
        String nextNo;
        try {
            nextNo = hrService.nextEmployeeNo();
        } catch (Exception ex) {
            showError("Failed to generate next Employee #", ex);
            return;
        }

        EmployeeFormDialog dlg = new EmployeeFormDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add Employee",
                null,
                nextNo,
                employee -> hrService.add(employee)
        );
        dlg.setVisible(true);
        EmployeeRecord created = dlg.getResult();
        if (created == null) return;

        loadAll();
        JOptionPane.showMessageDialog(this, "Employee added!");
    }

    private void onEdit() {
        EmployeeRecord selected = getSelectedEmployeeFromRepo();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        EmployeeFormDialog dlg = new EmployeeFormDialog(
                SwingUtilities.getWindowAncestor(this),
                "Edit Employee",
                selected,
                employee -> hrService.updateHr(employee)
        );
        dlg.setVisible(true);
        EmployeeRecord updated = dlg.getResult();
        if (updated == null) return;

        loadAll();
        JOptionPane.showMessageDialog(this, "Employee updated!");
    }

    private void onDelete() {
        int row = tbl.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }

        String empNo = String.valueOf(model.getValueAt(row, 0));
        int ok = JOptionPane.showConfirmDialog(this, "Delete Employee #" + empNo + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            hrService.delete(empNo);
            loadAll();
            JOptionPane.showMessageDialog(this, "Employee deleted!");
        } catch (Exception ex) {
            showError("Failed to delete employee", ex);
        }
    }

    private EmployeeRecord getSelectedEmployeeFromRepo() {
        try {
            int row = tbl.getSelectedRow();
            if (row < 0) return null;
            String empNo = String.valueOf(model.getValueAt(row, 0));
            for (EmployeeRecord e : hrService.listAll()) {
                if (e.getEmployeeNo() != null && e.getEmployeeNo().equals(empNo)) return e;
            }
            return null;
        } catch (Exception ex) {
            showError("Failed to read selected employee", ex);
            return null;
        }
    }

    private String formatDate(LocalDate d) {
        return d == null ? "" : dateFmt.format(d);
    }

    private void showError(String title, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, title + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
