import javax.swing.*;
import java.awt.*;

public class Bai21 extends JFrame {

    public Bai21() {
        // Tiêu đề
        setTitle("Bài 21 - Không thể đóng bằng X");

        // Kích thước
        setSize(400, 300);

        // Không làm gì khi nhấn X
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Căn giữa màn hình
        setLocationRelativeTo(null);

        // Tạo nút Exit
        JButton exitButton = new JButton("Exit");

        // Sự kiện Exit
        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        // Đưa nút vào giữa
        setLayout(new FlowLayout());

        add(exitButton);

        // Hiển thị cửa sổ
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Bai21();
        });
    }
}