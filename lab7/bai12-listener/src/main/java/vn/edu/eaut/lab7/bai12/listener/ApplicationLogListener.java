package vn.edu.eaut.lab7.bai12.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;
import java.time.LocalDateTime;

@WebListener
public class ApplicationLogListener implements ServletContextListener {
  public void contextInitialized(ServletContextEvent e) {
    e.getServletContext().log("[LAB7] Ứng dụng khởi động lúc " + LocalDateTime.now());
  }

  public void contextDestroyed(ServletContextEvent e) {
    e.getServletContext().log("[LAB7] Ứng dụng dừng lúc " + LocalDateTime.now());
  }
}
