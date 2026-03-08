package com.mycompany.oopmotorph.supervisor.ui;

import com.mycompany.oopmotorph.app.AppContext;
import com.mycompany.oopmotorph.supervisor.ui.pages.*;
import com.mycompany.oopmotorph.timesheet.service.TimeLogService;
import com.mycompany.oopmotorph.ui.RoleSelectionFrame;
import com.mycompany.oopmotorph.ui.UserHeaderSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SupervisorFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout(); private final JPanel contentPanel = new JPanel(cardLayout); private final JLabel lblTitle = new JLabel("Supervisor Dashboard"); private final JButton btnUser = new JButton();
    private final String employeeNo, lastName, firstName; private final AppContext appContext; private final TimeLogService timeLogService;
    private final JButton btnTimeIn = new JButton("Time In"), btnTimeOut = new JButton("Time Out");
    private static final String PAGE_ATTENDANCE = "attendance", PAGE_TIMESHEET = "timesheet", PAGE_OVERTIME = "overtime", PAGE_LEAVE = "leave", PAGE_PAYSLIP = "payslip";
    public SupervisorFrame(String employeeNo, String lastName, String firstName) { this(employeeNo, lastName, firstName, AppContext.createDefault()); }
    public SupervisorFrame(String employeeNo, String lastName, String firstName, AppContext appContext) {
        super("MotorPH - Supervisor"); this.employeeNo=employeeNo; this.lastName=lastName; this.firstName=firstName; this.appContext=appContext; this.timeLogService=appContext.getTimeLogService();
        btnUser.setText(firstName + " " + lastName + " (" + employeeNo + ")"); UserHeaderSupport.makeClickable(btnUser, this, employeeNo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setSize(1100,650); setLocationRelativeTo(null); setResizable(false); setContentPane(buildRoot()); registerPages(); showPage(PAGE_ATTENDANCE, "Attendance"); }
    private JPanel buildRoot() { JPanel root = new JPanel(new BorderLayout()); root.add(buildTopBar(), BorderLayout.NORTH); root.add(buildSidebar(), BorderLayout.WEST); root.add(contentPanel, BorderLayout.CENTER); return root; }
    private JComponent buildTopBar() { JPanel top = new JPanel(new BorderLayout()); top.setBackground(new Color(13,40,100)); top.setBorder(new EmptyBorder(12,16,12,16)); lblTitle.setForeground(Color.WHITE); lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18)); top.add(lblTitle, BorderLayout.WEST); JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); right.setOpaque(false); btnTimeIn.setFocusPainted(false); btnTimeOut.setFocusPainted(false); btnTimeIn.addActionListener(e -> handleTimeIn()); btnTimeOut.addActionListener(e -> handleTimeOut()); right.add(btnUser); right.add(Box.createHorizontalStrut(10)); right.add(btnTimeIn); right.add(btnTimeOut); top.add(right, BorderLayout.EAST); return top; }
    private JComponent buildSidebar() { JPanel side = new JPanel(); side.setBackground(new Color(245,247,252)); side.setPreferredSize(new Dimension(230,0)); side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS)); side.setBorder(new EmptyBorder(16,12,16,12)); side.add(sideTitle("Supervisor")); side.add(Box.createVerticalStrut(14)); side.add(navButton("Attendance", PAGE_ATTENDANCE)); side.add(Box.createVerticalStrut(10)); side.add(navButton("Timesheet", PAGE_TIMESHEET)); side.add(Box.createVerticalStrut(10)); side.add(navButton("Overtime Approval", PAGE_OVERTIME)); side.add(Box.createVerticalStrut(10)); side.add(navButton("Leave Approval", PAGE_LEAVE)); side.add(Box.createVerticalStrut(10)); side.add(navButton("Payslips", PAGE_PAYSLIP)); side.add(Box.createVerticalGlue()); JButton btnLogout = new JButton("Logout"); btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT); btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE,40)); btnLogout.addActionListener(e -> logout()); side.add(btnLogout); return side; }
    private JLabel sideTitle(String text) { JLabel l = new JLabel(text); l.setFont(new Font("SansSerif", Font.BOLD, 16)); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
    private JButton navButton(String text, String pageKey) { JButton btn = new JButton(text); btn.setAlignmentX(Component.LEFT_ALIGNMENT); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,44)); btn.setFocusPainted(false); btn.addActionListener(e -> showPage(pageKey, text)); return btn; }
    private void registerPages() { contentPanel.add(new SupervisorAttendancePanel(appContext.getAttendanceService()), PAGE_ATTENDANCE); contentPanel.add(new SupervisorTimesheetPanel(appContext.getTimeLogService()), PAGE_TIMESHEET); contentPanel.add(new SupervisorOvertimePanel(), PAGE_OVERTIME); contentPanel.add(new SupervisorLeavePanel(), PAGE_LEAVE); SupervisorPayslipPanel payslipPanel = new SupervisorPayslipPanel(); payslipPanel.setSupervisorName(lastName + ", " + firstName); contentPanel.add(payslipPanel, PAGE_PAYSLIP); }
    private void showPage(String pageKey, String title) { lblTitle.setText(title); cardLayout.show(contentPanel, pageKey); }
    private void logout() { if (JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { SwingUtilities.invokeLater(() -> { new RoleSelectionFrame().setVisible(true); dispose(); }); } }
    private void handleTimeIn() { try { timeLogService.timeIn(employeeNo, lastName, firstName); JOptionPane.showMessageDialog(this, "Time In recorded!"); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Unable to Time In:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } }
    private void handleTimeOut() { try { timeLogService.timeOut(employeeNo); JOptionPane.showMessageDialog(this, "Time Out recorded!"); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Unable to Time Out:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); } }
}
