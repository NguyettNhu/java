package chuong2;
import javax.swing.*;
import java.awt.*;

public class bai20 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("No Close Button Window");
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> System.exit(0));

            frame.add(exitButton, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}