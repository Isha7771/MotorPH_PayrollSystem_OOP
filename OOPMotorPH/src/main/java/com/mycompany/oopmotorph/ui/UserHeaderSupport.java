package com.mycompany.oopmotorph.ui;

import com.mycompany.oopmotorph.employee.ui.dialogs.EmployeeProfileDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UserHeaderSupport {
    private UserHeaderSupport() {}

    public static void makeClickable(AbstractButton btnUser, Window owner, String employeeNo) {
        if (btnUser == null || employeeNo == null || employeeNo.isBlank()) return;
        styleHeaderButton(btnUser);
        btnUser.setToolTipText("View your employee record");
        btnUser.addActionListener(e -> openProfile(owner, employeeNo));
    }

    private static void styleHeaderButton(AbstractButton btnUser) {
        btnUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnUser.setFocusPainted(false);
        btnUser.setBorderPainted(true);
        btnUser.setContentAreaFilled(true);
        btnUser.setOpaque(true);
        btnUser.setForeground(Color.WHITE);
        btnUser.setBackground(new Color(18, 56, 132));
        btnUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnUser.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 220, 242), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        btnUser.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private static void openProfile(Window owner, String employeeNo) {
        EmployeeProfileDialog dlg = new EmployeeProfileDialog(owner, employeeNo);
        dlg.setVisible(true);
    }
}
