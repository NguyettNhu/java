package chuong2;

import javax.swing.*;
import java.awt.*;

public class bai6 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("My First Swing App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("Hello World", SwingConstants.CENTER);
            label.setForeground(Color.RED);
            label.setFont(new Font("Arial", Font.BOLD, 30));
            frame.add(label, BorderLayout.NORTH);
            frame.setVisible(true);
        });
    }
}