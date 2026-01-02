package shop.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Footer panel with branding and navigation links.
 */
public class FooterPanel extends JPanel {

    public FooterPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(21, 59, 122));
        setPreferredSize(new Dimension(0, 45));
        setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // Left: App title
        JLabel leftLabel = new JLabel("Electronics Store Manager");
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        add(leftLabel, BorderLayout.WEST);

        // Right: Navigation buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));

        JButton helpBtn = createFooterButton("Help");
        JButton githubBtn = createFooterButton("GitHub");
        githubBtn.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://github.com/"));
            } catch (Exception ignored) {
            }
        });

        rightPanel.add(helpBtn);
        rightPanel.add(Box.createHorizontalStrut(12));
        rightPanel.add(githubBtn);

        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createFooterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(21, 59, 122));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(200, 220, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }
}
