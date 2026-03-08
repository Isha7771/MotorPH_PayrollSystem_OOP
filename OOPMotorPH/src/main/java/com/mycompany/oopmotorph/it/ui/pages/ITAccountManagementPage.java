package com.mycompany.oopmotorph.it.ui.pages;

import com.mycompany.oopmotorph.auth.User;
import com.mycompany.oopmotorph.auth.UserAccountAdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ITAccountManagementPage extends JPanel {
    private final UserAccountAdminService service;
    private final JTextField txtSearch = new JTextField(20);
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Employee #", "Name", "Role", "Username", "Position"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public ITAccountManagementPage(UserAccountAdminService service) {
        this.service = service;
        setLayout(new BorderLayout(10, 10));
        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSearch = new JButton("Search");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnEdit = new JButton("Edit Selected Account");
        btnSearch.addActionListener(e -> refresh());
        btnRefresh.addActionListener(e -> refresh());
        btnEdit.addActionListener(e -> editSelected());
        top.add(new JLabel("Search"));
        top.add(txtSearch);
        top.add(btnSearch);
        top.add(btnRefresh);
        top.add(btnEdit);
        return top;
    }

    private void refresh() {
        try {
            List<User> users = service.search(txtSearch.getText());
            model.setRowCount(0);
            for (User u : users) {
                model.addRow(new Object[]{u.getEmployeeNo(), u.getFullName(), u.getRole(), u.getUsername(), u.getPosition()});
            }
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select an account first.");
            return;
        }
        String empNo = String.valueOf(model.getValueAt(row, 0));
        String currentUsername = String.valueOf(model.getValueAt(row, 3));
        JTextField txtUser = new JTextField(currentUsername, 20);
        JPasswordField txtPass = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Username"));
        panel.add(txtUser);
        panel.add(new JLabel("New Password"));
        panel.add(txtPass);
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            service.updateCredentials(empNo, txtUser.getText(), new String(txtPass.getPassword()));
            refresh();
            JOptionPane.showMessageDialog(this, "Account updated successfully.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "IT Account Management", JOptionPane.ERROR_MESSAGE);
    }
}
