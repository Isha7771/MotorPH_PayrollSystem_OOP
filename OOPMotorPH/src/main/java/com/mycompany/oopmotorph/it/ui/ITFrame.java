package com.mycompany.oopmotorph.it.ui;

import com.mycompany.oopmotorph.auth.UserAccountAdminService;
import com.mycompany.oopmotorph.it.service.ITSupportService;
import com.mycompany.oopmotorph.it.ui.pages.ITAccountManagementPage;
import com.mycompany.oopmotorph.it.ui.pages.ITTicketManagementPage;
import com.mycompany.oopmotorph.timesheet.repository.TimeLogCsvRepository;
import com.mycompany.oopmotorph.timesheet.service.TimeLogService;
import com.mycompany.oopmotorph.ui.RoleSelectionFrame;
import com.mycompany.oopmotorph.ui.UserHeaderSupport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ITFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JLabel lblTitle = new JLabel("IT Dashboard");
    private final String employeeNo;
    private final String lastName;
    private final String firstName;
    private final Path timeLogCsvPath = Paths.get(System.getProperty("user.dir")).resolve("Data").resolve("DataTimeLogs.csv");
    private final TimeLogService timeLogService = new TimeLogService(new TimeLogCsvRepository(timeLogCsvPath));

    private static final String PAGE_ACCOUNTS = "accounts";
    private static final String PAGE_TICKETS = "tickets";

    public ITFrame(String employeeNo, String lastName, String firstName,
                   UserAccountAdminService accountAdminService,
                   ITSupportService itSupportService) {
        super("MotorPH - IT");
        this.employeeNo = employeeNo;
        this.lastName = lastName;
        this.firstName = firstName;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildRoot());
        contentPanel.add(new ITAccountManagementPage(accountAdminService), PAGE_ACCOUNTS);
        contentPanel.add(new ITTicketManagementPage(itSupportService, firstName + " " + lastName), PAGE_TICKETS);
        showPage(PAGE_ACCOUNTS, "Account Management");
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(13, 40, 100));
        top.setBorder(new EmptyBorder(12, 16, 12, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(lblTitle, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JButton btnUser = new JButton(firstName + " " + lastName + " (" + employeeNo + ")");
        UserHeaderSupport.makeClickable(btnUser, this, employeeNo);
        JButton btnIn = new JButton("Time In");
        JButton btnOut = new JButton("Time Out");
        btnIn.addActionListener(e -> handleTimeIn());
        btnOut.addActionListener(e -> handleTimeOut());
        right.add(btnUser);
        right.add(btnIn);
        right.add(btnOut);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(new Color(245, 247, 252));
        side.setPreferredSize(new Dimension(230, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(16, 12, 16, 12));
        side.add(sideTitle("IT Support"));
        side.add(Box.createVerticalStrut(14));
        side.add(navButton("Account Management", PAGE_ACCOUNTS));
        side.add(Box.createVerticalStrut(10));
        side.add(navButton("Support Tickets", PAGE_TICKETS));
        side.add(Box.createVerticalGlue());
        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogout.addActionListener(e -> { new RoleSelectionFrame().setVisible(true); dispose(); });
        side.add(btnLogout);
        return side;
    }

    private JLabel sideTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JButton navButton(String text, String pageKey) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.addActionListener(e -> showPage(pageKey, text));
        return btn;
    }

    private void showPage(String pageKey, String title) {
        lblTitle.setText(title);
        cardLayout.show(contentPanel, pageKey);
    }

    private void handleTimeIn() {
        try { timeLogService.timeIn(employeeNo, lastName, firstName); JOptionPane.showMessageDialog(this, "Time In recorded!"); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "IT", JOptionPane.ERROR_MESSAGE); }
    }
    private void handleTimeOut() {
        try { timeLogService.timeOut(employeeNo); JOptionPane.showMessageDialog(this, "Time Out recorded!"); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "IT", JOptionPane.ERROR_MESSAGE); }
    }
}
