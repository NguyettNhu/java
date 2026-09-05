package vn.edu.eaut.lab7.bai12.listener;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.*;
import java.time.LocalDateTime;

@WebListener
public class SessionLogListener implements HttpSessionListener {
  public void sessionCreated(HttpSessionEvent e) {
    e.getSession()
        .getServletContext()
        .log("[LAB7] Tạo session " + e.getSession().getId() + " lúc " + LocalDateTime.now());
  }

  public void sessionDestroyed(HttpSessionEvent e) {
    e.getSession()
        .getServletContext()
        .log("[LAB7] Hủy session " + e.getSession().getId() + " lúc " + LocalDateTime.now());
  }
}
