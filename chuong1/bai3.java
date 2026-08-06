import java.util.Scanner;

public class bai3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Nhập số nguyên n: ");
        int n = scanner.nextInt();
        
        boolean isPrime = true;
        
        // Số nguyên tố phải lớn hơn hoặc bằng 2
        if (n < 2) {
            isPrime = false;
        } else {
            // Chỉ cần kiểm tra từ 2 đến căn bậc hai của n để tối ưu hiệu suất
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) {
                    isPrime = false;
                    break; // Nếu chia hết thì thoát vòng lặp ngay
                }
            }
        }
        
        if (isPrime) {
            System.out.println(n + " là số nguyên tố.");
        } else {
            System.out.println(n + " không phải là số nguyên tố.");
        }
        scanner.close();
    }
}