package shop.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import shop.controllers.ProductController;
import shop.controllers.CartController;
import java.util.concurrent.atomic.AtomicReference;
import shop.controllers.OrderController;

public class MainFrame extends JFrame {
    private javax.swing.Timer searchTimer;
    private boolean isCategoryPanelVisible = true;
    private boolean isGridViewActive = true;

    public MainFrame(ProductController productController, CartController cartController, OrderController orderController ) {
        super("ElectroShop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(0, 0, 0, 0));
        JPanel topContainer = new JPanel(new BorderLayout());
        NavbarPanel navbarPanel = new NavbarPanel();
        topContainer.add(navbarPanel, BorderLayout.NORTH);

        // --- Product panels (grid and list views) ---
        ProductCardPanel productPanel = new ProductCardPanel(productController, cartController, navbarPanel);
        ProductListPanel listPanel = new ProductListPanel(productController, cartController, navbarPanel);
        AtomicReference<JComponent> currentCenter = new AtomicReference<>(productPanel);

        // --- Category panel (togglable) ---
        CategoryPanel categoryPanel = new CategoryPanel(category -> {
            if ("All".equals(category)) {
                productPanel.loadAllProducts();
                listPanel.loadAllProducts();
            } else {
                productPanel.loadProductsByCategory(category);
                listPanel.loadProductsByCategory(category);
            }
        });
        container.add(categoryPanel, BorderLayout.WEST);

        // Search bar with controls
        SearchBarPanel searchBarPanel = new SearchBarPanel();
        topContainer.add(searchBarPanel, BorderLayout.SOUTH);

        // Category panel toggle (X/≡)
        searchBarPanel.getCategoryToggleButton().addActionListener(e -> {
            isCategoryPanelVisible = !isCategoryPanelVisible;
            categoryPanel.setVisible(isCategoryPanelVisible);
            searchBarPanel.updateCategoryToggleButton(isCategoryPanelVisible);
            container.revalidate();
            container.repaint();
            // Refresh product layout to adjust to new available space
            productPanel.refreshLayout();
            listPanel.refreshLayout();
        });

        // View toggle (Grid ⇄ List)
        searchBarPanel.getListViewButton().addActionListener(e -> {
            isGridViewActive = !isGridViewActive;
            if (isGridViewActive) {
                container.remove(listPanel);
                container.add(productPanel, BorderLayout.CENTER);
                currentCenter.set(productPanel);
            } else {
                container.remove(productPanel);
                container.add(listPanel, BorderLayout.CENTER);
                currentCenter.set(listPanel);
            }
            searchBarPanel.updateViewToggleButton(isGridViewActive);
            container.revalidate();
            container.repaint();
        });
        
        // Search field with debounce
        final MainFrame mainFrame = this;
        searchBarPanel.getSearchField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                scheduleSearch();
            }
            private void scheduleSearch() {
                if (mainFrame.searchTimer != null && mainFrame.searchTimer.isRunning()) {
                    mainFrame.searchTimer.stop();
                }
                mainFrame.searchTimer = new javax.swing.Timer(300, e -> performSearch());
                mainFrame.searchTimer.setRepeats(false);
                mainFrame.searchTimer.start();
            }
            private void performSearch() {
                String query = searchBarPanel.getSearchField().getText().trim();
                if (query.isEmpty()) {
                    productPanel.loadAllProducts();
                    listPanel.loadAllProducts();
                } else {
                    productPanel.filterProducts(query);
                    listPanel.filterProducts(query);
                }
            }
        });
        
        container.add(currentCenter.get(), BorderLayout.CENTER);
        container.add(topContainer, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);

        // Footer panel (fixed at bottom)
        add(new FooterPanel(), BorderLayout.SOUTH);

        // Initialize cart counter
        navbarPanel.updateCartCount(cartController.getItemCount());
        
        // Wire cart button to show cart window
        navbarPanel.getCartButton().addActionListener(e -> {
            JFrame cartWindow = new JFrame("Shopping Cart");
            cartWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            CartPanel cartPanel = new CartPanel(cartController, orderController, cartWindow);
            cartPanel.setNavbarPanel(navbarPanel);
            cartWindow.add(cartPanel);
            cartWindow.setSize(900, 600);
            cartWindow.setLocationRelativeTo(null);
            cartWindow.setVisible(true);
        });
    }
}