import java.util.Scanner;

public class bai1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập số nguyên dương n: ");
        int n = scanner.nextInt();
        int s = 0;
        for (int i = 2; i <= n; i += 2) {
            s += i;
        }
        
        System.out.println("Tổng các số chẵn s = " + s);
        scanner.close();
    }
}
