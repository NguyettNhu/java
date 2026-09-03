package vn.edu.eaut.lab6.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import vn.edu.eaut.lab6.model.Student;
import vn.edu.eaut.lab6.store.StudentStore;

import java.util.List;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        List<Student> samples = List.of(
                new Student("SV001", "Nguyen Van An", "DCCNTT12", "an@example.com"),
                new Student("SV002", "Tran Thi Binh", "DCCNTT12", "binh@example.com"),
                new Student("SV003", "Le Quang Huy", "DCCNTT13", "huy@example.com"),
                new Student("SV004", "Pham Thu Ha", "DCCNTT13", "ha@example.com"),
                new Student("SV005", "Do Minh Duc", "DCCNTT14", "duc@example.com")
        );

        StudentStore.replaceAll(samples);

        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("students", StudentStore.findAll());

        System.out.println("Ung dung Lab 6 da khoi dong");
        System.out.println("Da khoi tao " + StudentStore.countAll() + " sinh vien mau trong ServletContext");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Ung dung Lab 6 da dung");
        System.out.println("Tong so sinh vien truoc khi dung: " + StudentStore.countAll());
    }
}
