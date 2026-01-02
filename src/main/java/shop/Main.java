package shop;

import javax.swing.SwingUtilities;

import shop.config.DbConfig;
import shop.config.DbInit;
import shop.controllers.ProductController;
import shop.controllers.CartController;
import shop.controllers.OrderController;
import shop.repositories.ProductRepository;
import shop.repositories.CartRepository;
import shop.repositories.OrderRepository;

import java.sql.Connection;
import shop.ui.MainFrame;

public class Main {
  public static void main(String[] args) {
    System.out.println("Welcome to the Shop Application!");
    try {
      DbInit.migrate();

      Connection conn = DbConfig.getConnection();
      ProductRepository productRepository = new ProductRepository(conn);
      CartRepository cartRepository = new CartRepository(conn, productRepository);
      ProductController productController = new ProductController(productRepository);
      CartController cartController = new CartController(cartRepository, productRepository);
      OrderRepository orderRepository = new OrderRepository(conn, productRepository);
OrderController orderController = new OrderController(orderRepository, productRepository);

     

      SwingUtilities.invokeLater(() -> {
        new MainFrame(productController, cartController,orderController).setVisible(true);
      });
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}