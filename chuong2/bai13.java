package chuong2;
import javax.swing.*;
import java.awt.*;

public class bai13 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Colored Background");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(new Color(60, 179, 113));
            panel.add(new JLabel("Colored Background", SwingConstants.CENTER), BorderLayout.CENTER);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }
}