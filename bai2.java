import java.util.Scanner;

public class bai2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập số nguyên dương n: ");
        int n = scanner.nextInt();
        
        double s = 0;
        for (int i = 1; i <= n; i++) {
            s += 1.0 / i;
        }
        
        System.out.println("Tổng nghịch đảo s = " + s);
        scanner.close();
    }
}