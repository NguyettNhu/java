import java.util.Scanner;

public class bai5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập số nguyên dương n: ");
        int n = scanner.nextInt();
        
        int f0 = 0;
        int f1 = 1;
        
        System.out.print("Dãy " + n + " số Fibonacci đầu tiên: ");
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                System.out.print(f0 + " ");
            } else if (i == 1) {
                System.out.print(f1 + " ");
            } else {
                int fn = f0 + f1;
                System.out.print(fn + " ");
                f0 = f1;
                f1 = fn;
            }
        }
        System.out.println();
        scanner.close();
    }
}