import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class bai10 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Digital Clock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(320, 180);
            frame.setLocationRelativeTo(null);

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            JLabel clockLabel = new JLabel(time, SwingConstants.CENTER);
            clockLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
            frame.add(clockLabel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}