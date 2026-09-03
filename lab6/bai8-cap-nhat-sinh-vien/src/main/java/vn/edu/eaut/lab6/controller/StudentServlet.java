package vn.edu.eaut.lab6.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.eaut.lab6.model.Student;
import vn.edu.eaut.lab6.store.StudentStore;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = trim(request.getParameter("action"));
        if ("form".equals(action)) {
            showForm(request, response, new Student(), "create");
            return;
        }
        if ("edit".equals(action)) {
            Student student = StudentStore.findById(request.getParameter("id"));
            if (student == null) {
                redirectToListWithMessage(request, response, "Khong tim thay sinh vien can cap nhat.");
                return;
            }
            showForm(request, response, student, "update");
            return;
        }
        showList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String action = trim(request.getParameter("action"));

        if ("update".equals(action)) {
            updateStudent(request, response);
            return;
        }
        if ("delete".equals(action)) {
            deleteStudent(request, response);
            return;
        }
        createStudent(request, response);
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = trim(request.getParameter("keyword"));
        List<Student> students = StudentStore.searchByName(keyword);

        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);
        request.setAttribute("totalStudents", StudentStore.countAll());

        if (!keyword.isEmpty() && students.isEmpty()) {
            request.setAttribute("emptyMessage", "Khong tim thay sinh vien phu hop voi tu khoa \"" + keyword + "\".");
        }

        request.getRequestDispatcher("/student-list.jsp").forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Student student, String mode)
            throws ServletException, IOException {
        request.setAttribute("student", student);
        request.setAttribute("formMode", mode);
        request.getRequestDispatcher("/student-form.jsp").forward(request, response);
    }

    private void createStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Student student = readStudentFromRequest(request);
        String validationError = validateStudent(student, true);
        if (validationError != null) {
            request.setAttribute("error", validationError);
            showForm(request, response, student, "create");
            return;
        }

        boolean added = StudentStore.add(student);
        if (!added) {
            request.setAttribute("error", "Ma sinh vien da ton tai.");
            showForm(request, response, student, "create");
            return;
        }

        syncContext();
        redirectToListWithMessage(request, response, "Da them sinh vien moi.");
    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Student student = readStudentFromRequest(request);
        String validationError = validateStudent(student, false);
        if (validationError != null) {
            request.setAttribute("error", validationError);
            showForm(request, response, student, "update");
            return;
        }

        boolean updated = StudentStore.update(student);
        if (!updated) {
            request.setAttribute("error", "Khong tim thay sinh vien de cap nhat.");
            showForm(request, response, student, "update");
            return;
        }

        syncContext();
        redirectToListWithMessage(request, response, "Da cap nhat thong tin sinh vien.");
    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = trim(request.getParameter("id"));
        StudentStore.deleteById(id);
        syncContext();
        redirectToListWithMessage(request, response, "Da xoa sinh vien.");
    }

    private Student readStudentFromRequest(HttpServletRequest request) {
        return new Student(
                trim(request.getParameter("id")),
                trim(request.getParameter("name")),
                trim(request.getParameter("className")),
                trim(request.getParameter("email"))
        );
    }

    private String validateStudent(Student student, boolean creating) {
        if (student.getId().isEmpty()) {
            return "Ma sinh vien khong duoc de trong.";
        }
        if (student.getName().isEmpty()) {
            return "Ho ten khong duoc de trong.";
        }
        if (student.getClassName().isEmpty()) {
            return "Lop khong duoc de trong.";
        }
        if (student.getEmail().isEmpty() || !student.getEmail().contains("@")) {
            return "Email khong hop le.";
        }
        if (!creating && StudentStore.findById(student.getId()) == null) {
            return "Khong tim thay sinh vien can sua.";
        }
        return null;
    }

    private void redirectToListWithMessage(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/students?message=" + encodedMessage);
    }

    private void syncContext() {
        getServletContext().setAttribute("students", StudentStore.findAll());
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
