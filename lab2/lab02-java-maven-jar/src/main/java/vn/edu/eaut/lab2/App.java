package vn.edu.eaut.lab2;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== LAB 2 - MAVEN PROJECT VA DONG GOI JAR =====");
        System.out.print("Nhap ma sinh vien: ");
        String studentId = scanner.nextLine().trim();
        while (studentId.isEmpty()) {
            System.out.print("Ma sinh vien khong duoc de trong! Nhap lai: ");
            studentId = scanner.nextLine().trim();
        }

        System.out.print("Nhap ho ten sinh vien: ");
        String fullName = scanner.nextLine().trim();
        while (fullName.isEmpty()) {
            System.out.print("Ho ten khong duoc de trong! Nhap lai: ");
            fullName = scanner.nextLine().trim();
        }

        double attendanceScore = inputScore(scanner, "diem chuyen can");
        double midtermScore = inputScore(scanner, "diem giua ky");
        double finalScore = inputScore(scanner, "diem cuoi ky");

        Student student = new Student(studentId, fullName,
                attendanceScore, midtermScore, finalScore);

        double totalScore = GradeCalculator.calculateFinalScore(student);
        String grade = GradeCalculator.classify(totalScore);

        System.out.println("\n----- KET QUA HOC PHAN -----");
        System.out.println("Ma SV: " + student.getStudentId());
        System.out.println("Ho ten: " + student.getFullName());
        System.out.printf("Diem tong ket: %.2f%n", totalScore);
        System.out.println("Xep loai: " + grade);

        scanner.close();
    }

    private static double inputScore(Scanner scanner, String label) {
        while (true) {
            try {
                System.out.print("Nhap " + label + ": ");
                double score = Double.parseDouble(scanner.nextLine().trim());
                GradeCalculator.validateScore(score, label);
                return score;
            } catch (NumberFormatException ex) {
                System.out.println("Loi: Vui long nhap so!");
            } catch (IllegalArgumentException ex) {
                System.out.println("Loi: " + ex.getMessage());
            }
        }
    }
}
