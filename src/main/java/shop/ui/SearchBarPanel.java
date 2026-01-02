package shop.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern search bar panel with category toggle, search field, clear button, and view toggle.
 */
public class SearchBarPanel extends JPanel {
    private final JTextField searchField;
    private final JButton clearButton;
    private final JButton listViewButton;
    private final JButton categoryToggleButton;

    public SearchBarPanel() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setBorder(new EmptyBorder(16, 32, 16, 32));

        JPanel inner = new RoundedShadowPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.X_AXIS));
        inner.setOpaque(false);
        inner.setPreferredSize(new Dimension(800, 56));
        inner.setMaximumSize(new Dimension(800, 56));
        inner.setBorder(new EmptyBorder(0, 12, 0, 12));

        // Category toggle button (left-most)
        categoryToggleButton = new JButton("\u2716"); // Unicode X (default when panel is visible)
        categoryToggleButton.setToolTipText("Hide Categories");
        categoryToggleButton.setFocusable(false);
        categoryToggleButton.setPreferredSize(new Dimension(40, 40));
        categoryToggleButton.setMaximumSize(new Dimension(40, 40));
        categoryToggleButton.setContentAreaFilled(false);
        categoryToggleButton.setBorderPainted(false);
        categoryToggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        categoryToggleButton.setOpaque(false);
        categoryToggleButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        categoryToggleButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                categoryToggleButton.setBackground(new Color(230, 230, 230));
                categoryToggleButton.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                categoryToggleButton.setOpaque(false);
            }
        });
        inner.add(categoryToggleButton);
        inner.add(Box.createHorizontalStrut(8));

        // Search icon
        JLabel searchIcon = new JLabel("\uD83D\uDD0D"); // Unicode magnifier
        searchIcon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchIcon.setForeground(new Color(120, 130, 140));
        inner.add(searchIcon);
        inner.add(Box.createHorizontalStrut(6));

        // Search field
        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchField.setOpaque(false);
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        searchField.setForeground(Color.DARK_GRAY);
        searchField.setCaretColor(new Color(21, 59, 122));
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, brand, or model...");
        inner.add(searchField);
        inner.add(Box.createHorizontalStrut(12));

        // Clear button
        clearButton = new JButton("\u2716"); // Unicode X
        clearButton.setToolTipText("Clear");
        clearButton.setFocusable(false);
        clearButton.setPreferredSize(new Dimension(36, 36));
        clearButton.setMaximumSize(new Dimension(36, 36));
        clearButton.setContentAreaFilled(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearButton.setOpaque(false);
        clearButton.addActionListener(e -> searchField.setText(""));
        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(new Color(230, 230, 230));
                clearButton.setOpaque(true);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearButton.setOpaque(false);
            }
        });
        inner.add(Box.createHorizontalStrut(4));
        inner.add(clearButton);
        inner.add(Box.createHorizontalStrut(8));

        // List/Grid View toggle button
        listViewButton = new JButton("List View");
        listViewButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        listViewButton.setForeground(Color.WHITE);
        listViewButton.setBackground(new Color(21, 59, 122));
        listViewButton.setFocusPainted(false);
        listViewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        listViewButton.setOpaque(true);
        listViewButton.setBorder(new RoundedBorder(18));
        listViewButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                listViewButton.setBackground(new Color(10, 28, 55));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                listViewButton.setBackground(new Color(21, 59, 122));
            }
        });
        inner.add(listViewButton);
        inner.add(Box.createHorizontalStrut(8));

        add(inner);
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getListViewButton() {
        return listViewButton;
    }

    public JButton getCategoryToggleButton() {
        return categoryToggleButton;
    }

    /**
     * Updates the category toggle button text based on visibility state.
     * @param isVisible true if category panel is currently visible
     */
    public void updateCategoryToggleButton(boolean isVisible) {
        categoryToggleButton.setText(isVisible ? "\u2716" : "\u2630");
        categoryToggleButton.setToolTipText(isVisible ? "Hide Categories" : "Show Categories");
    }

    /**
     * Updates the view toggle button text based on current view.
     * @param isGridView true if currently in grid view
     */
    public void updateViewToggleButton(boolean isGridView) {
        listViewButton.setText(isGridView ? "List View" : "Grid View");
    }

    /**
     * Rounded panel with subtle shadow effect.
     */
    static class RoundedShadowPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int arc = 32;
            int shadow = 8;
            int w = getWidth();
            int h = getHeight();

            // Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(shadow, shadow, w - shadow * 2, h - shadow * 2, arc, arc);

            // Background
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - shadow, h - shadow, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public boolean isOpaque() {
            return false;
        }
    }

    /**
     * Custom rounded border for buttons.
     */
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(21, 59, 122));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 22, 8, 22);
        }
    }
}
