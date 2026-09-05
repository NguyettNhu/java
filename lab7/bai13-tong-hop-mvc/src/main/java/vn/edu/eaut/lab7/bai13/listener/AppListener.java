package vn.edu.eaut.lab7.bai13.listener;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppListener implements ServletContextListener {
  public void contextInitialized(ServletContextEvent e) {
    e.getServletContext().log("[Bai 13] Ứng dụng MVC đã khởi động");
  }

  public void contextDestroyed(ServletContextEvent e) {
    e.getServletContext().log("[Bai 13] Ứng dụng MVC đã dừng");
  }
}
