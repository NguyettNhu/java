import java.util.Scanner;

public class bai4 {
    private static final double EPSILON = 1e-9;

    private static boolean nearlyEqual(double x, double y) {
        return Math.abs(x - y) < EPSILON;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập cạnh a: ");
        double a = scanner.nextDouble();
        System.out.print("Nhập cạnh b: ");
        double b = scanner.nextDouble();
        System.out.print("Nhập cạnh c: ");
        double c = scanner.nextDouble();
        
        double x = a;
        double y = b;
        double z = c;

        if (x > y) {
            double temp = x;
            x = y;
            y = temp;
        }
        if (y > z) {
            double temp = y;
            y = z;
            z = temp;
        }
        if (x > y) {
            double temp = x;
            x = y;
            y = temp;
        }

        // Điều kiện để 3 cạnh tạo thành một tam giác
        if (x + y > z) {
            
            // Kiểm tra phân loại
            if (nearlyEqual(x, y) && nearlyEqual(y, z)) {
                System.out.println("Đây là tam giác đều.");
            } else if (nearlyEqual(x * x + y * y, z * z)) {
                if (nearlyEqual(x, y) || nearlyEqual(y, z)) {
                    System.out.println("Đây là tam giác vuông cân.");
                } else {
                    System.out.println("Đây là tam giác vuông.");
                }
            } else if (nearlyEqual(x, y) || nearlyEqual(y, z)) {
                System.out.println("Đây là tam giác cân.");
            } else {
                System.out.println("Đây là tam giác thường.");
            }
            
        } else {
            System.out.println("Ba số này không tạo thành một tam giác.");
        }
        scanner.close();
    }
}