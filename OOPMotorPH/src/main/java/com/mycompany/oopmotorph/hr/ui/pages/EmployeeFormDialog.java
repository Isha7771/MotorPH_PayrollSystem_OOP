package com.mycompany.oopmotorph.hr.ui.pages;

import com.mycompany.oopmotorph.employee.model.EmployeeFactory;
import com.mycompany.oopmotorph.employee.model.EmployeeRecord;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class EmployeeFormDialog extends JDialog {

    @FunctionalInterface
    public interface SaveHandler {
        void save(EmployeeRecord employee) throws Exception;
    }

    private final JTextField txtEmpNo = new JTextField(15);
    private final JTextField txtLast = new JTextField(15);
    private final JTextField txtFirst = new JTextField(15);
    private final JTextField txtEmail = new JTextField(20);
    private final JDateChooser dateChooser = new JDateChooser();
    private final JTextField txtAddress = new JTextField(20);
    private final JFormattedTextField txtPhone = new JFormattedTextField(createMask("###-###-###"));
    private final JFormattedTextField txtSss = new JFormattedTextField(createMask("##-#######-#"));
    private final JFormattedTextField txtPhil = new JFormattedTextField(createMask("##-#########-#"));
    private final JFormattedTextField txtTin = new JFormattedTextField(createMask("###-###-###-###"));
    private final JFormattedTextField txtPagibig = new JFormattedTextField(createMask("####-####-####"));

    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Regular", "Probationary"});
    private final JTextField txtPosition = new JTextField(15);
    private final JTextField txtSupervisor = new JTextField(15);

    private final JTextField txtBasicSalary = new JTextField(12);
    private final JTextField txtRice = new JTextField(12);
    private final JTextField txtPhoneAllowance = new JTextField(12);
    private final JTextField txtClothing = new JTextField(12);
    private final JTextField txtGross = new JTextField(12);
    private final JTextField txtHourly = new JTextField(12);

    private final JButton btnSave = new JButton("Save");
    private final JButton btnCancel = new JButton("Cancel");

    private EmployeeRecord result;
    private final boolean editMode;
    private final SaveHandler saveHandler;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("M/d/yyyy");

    public EmployeeFormDialog(Window owner, String title, EmployeeRecord existing, SaveHandler saveHandler) {
        this(owner, title, existing, null, saveHandler);
    }

    public EmployeeFormDialog(Window owner, String title, EmployeeRecord existing, String autoEmployeeNo, SaveHandler saveHandler) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.editMode = existing != null;
        this.saveHandler = saveHandler;
        buildUI();

        if (existing != null) {
            fill(existing);
            txtEmpNo.setEditable(false);
        } else {
            txtEmpNo.setEditable(false);
            if (autoEmployeeNo != null && !autoEmployeeNo.isBlank()) {
                txtEmpNo.setText(autoEmployeeNo.trim());
            }
        }

        pack();
        setLocationRelativeTo(owner);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> {
            result = null;
            dispose();
        });
    }

    public EmployeeRecord getResult() {
        return result;
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        configureDateChooser();

        int r = 0;
        addRow(form, gc, r++, "Employee #", txtEmpNo);
        addRow(form, gc, r++, "Last Name", txtLast);
        addRow(form, gc, r++, "First Name", txtFirst);
        addRow(form, gc, r++, "Email", txtEmail);
        addRow(form, gc, r++, "Birthday", dateChooser);
        addRow(form, gc, r++, "Address", txtAddress);
        addRow(form, gc, r++, "Phone Number", txtPhone);

        initMaskedField(txtSss);
        initMaskedField(txtPhil);
        initMaskedField(txtTin);
        initMaskedField(txtPagibig);

        addRow(form, gc, r++, "SSS #", txtSss);
        addRow(form, gc, r++, "Philhealth #", txtPhil);
        addRow(form, gc, r++, "TIN #", txtTin);
        addRow(form, gc, r++, "Pag-ibig #", txtPagibig);
        addRow(form, gc, r++, "Status", cmbStatus);
        addRow(form, gc, r++, "Position", txtPosition);
        addRow(form, gc, r++, "Immediate Supervisor", txtSupervisor);
        addRow(form, gc, r++, "Basic Salary", txtBasicSalary);
        addRow(form, gc, r++, "Rice Subsidy", txtRice);
        addRow(form, gc, r++, "Phone Allowance", txtPhoneAllowance);
        addRow(form, gc, r++, "Clothing Allowance", txtClothing);
        addRow(form, gc, r++, "Gross Semi-monthly Rate", txtGross);
        addRow(form, gc, r++, "Hourly Rate", txtHourly);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(new JScrollPane(form), BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void configureDateChooser() {
        dateChooser.setDateFormatString("M/d/yyyy");
        dateChooser.setMaxSelectableDate(new Date());
        dateChooser.setPreferredSize(new Dimension(160, 26));
        JComponent dateEditor = dateChooser.getDateEditor().getUiComponent() instanceof JComponent
                ? (JComponent) dateChooser.getDateEditor().getUiComponent()
                : null;
        if (dateEditor instanceof JTextField textField) {
            textField.setEditable(false);
            textField.setColumns(12);
        }
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, Component field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        panel.add(new JLabel(label), gc);
        gc.gridx = 1;
        gc.weightx = 1;
        panel.add(field, gc);
    }

    private void fill(EmployeeRecord e) {
        txtEmpNo.setText(nz(e.getEmployeeNo()));
        txtLast.setText(nz(e.getLastName()));
        txtFirst.setText(nz(e.getFirstName()));
        txtEmail.setText(nz(e.getEmail()));
        dateChooser.setDate(toDate(e.getBirthday()));
        txtAddress.setText(nz(e.getAddress()));
        txtPhone.setText(nz(e.getPhoneNumber()));
        txtSss.setText(nz(e.getSssNo()));
        txtPhil.setText(nz(e.getPhilhealthNo()));
        txtTin.setText(nz(e.getTinNo()));
        txtPagibig.setText(nz(e.getPagibigNo()));
        cmbStatus.setSelectedItem((e.getStatus() != null && e.getStatus().toLowerCase().contains("prob")) ? "Probationary" : "Regular");
        txtPosition.setText(nz(e.getPosition()));
        txtSupervisor.setText(nz(e.getImmediateSupervisor()));
        txtBasicSalary.setText(String.valueOf(e.getBasicSalary()));
        txtRice.setText(String.valueOf(e.getRiceSubsidy()));
        txtPhoneAllowance.setText(String.valueOf(e.getPhoneAllowance()));
        txtClothing.setText(String.valueOf(e.getClothingAllowance()));
        txtGross.setText(String.valueOf(e.getGrossSemiMonthlyRate()));
        txtHourly.setText(String.valueOf(e.getHourlyRate()));
    }

    private void onSave() {
        try {
            String status = String.valueOf(cmbStatus.getSelectedItem());
            EmployeeRecord e = EmployeeFactory.create(status);
            e.setEmployeeNo(req(txtEmpNo.getText(), "Employee #"));
            e.setLastName(reqNoDigits(txtLast.getText(), "Last Name"));
            e.setFirstName(reqNoDigits(txtFirst.getText(), "First Name"));
            e.setEmail(req(txtEmail.getText(), "Email"));
            e.setBirthday(requireBirthday());
            e.setAddress(nz(txtAddress.getText()).trim());
            e.setPhoneNumber(req(txtPhone.getText(), "Phone Number"));
            e.setSssNo(maskedOrEmpty(txtSss));
            e.setPhilhealthNo(maskedOrEmpty(txtPhil));
            e.setTinNo(maskedOrEmpty(txtTin));
            e.setPagibigNo(maskedOrEmpty(txtPagibig));
            e.setStatus(status);
            e.setPosition(req(txtPosition.getText(), "Position"));
            e.setImmediateSupervisor(nz(txtSupervisor.getText()).trim());
            e.setBasicSalary(parseMoney(txtBasicSalary.getText()));
            e.setRiceSubsidy(parseMoney(txtRice.getText()));
            e.setPhoneAllowance(parseMoney(txtPhoneAllowance.getText()));
            e.setClothingAllowance(parseMoney(txtClothing.getText()));
            e.setGrossSemiMonthlyRate(parseMoney(txtGross.getText()));
            e.setHourlyRate(parseMoney(txtHourly.getText()));

            if (saveHandler != null) {
                saveHandler.save(e);
            }

            result = e;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate requireBirthday() {
        Date selectedDate = dateChooser.getDate();
        if (selectedDate == null) {
            throw new IllegalArgumentException("Birthday is required.");
        }

        LocalDate birthday = Instant.ofEpochMilli(selectedDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (birthday.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthday cannot be a future date.");
        }
        return birthday;
    }

    private static MaskFormatter createMask(String mask) {
        try {
            MaskFormatter mf = new MaskFormatter(mask);
            mf.setPlaceholderCharacter('_');
            return mf;
        } catch (ParseException e) {
            throw new IllegalStateException("Invalid mask: " + mask, e);
        }
    }

    private static void initMaskedField(JFormattedTextField f) {
        f.setColumns(15);
        f.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    }

    private static String maskedOrEmpty(JFormattedTextField f) {
        if (f == null) return "";
        String t = String.valueOf(f.getText()).trim();
        return t.contains("_") ? "" : t;
    }

    private double parseMoney(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        return Double.parseDouble(s.trim().replace(",", "").replace("\"", ""));
    }

    private String req(String s, String field) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(field + " is required.");
        return s.trim();
    }

    private String reqNoDigits(String s, String field) {
        String v = req(s, field);
        if (!v.matches("[A-Za-z][A-Za-z .'-]*")) {
            throw new IllegalArgumentException(field + " must contain letters only (no numbers/special chars).");
        }
        return v;
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
