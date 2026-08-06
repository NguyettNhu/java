import javax.swing.*;
import java.awt.*;

public class bai18 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Parent Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            JButton openChildButton = new JButton("Open Child Window");
            openChildButton.addActionListener(e -> {
                JFrame child = new JFrame("Child Window");
                child.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                child.setSize(200, 200);
                child.setLocationRelativeTo(frame);
                child.add(new JLabel("Child Window", SwingConstants.CENTER), BorderLayout.CENTER);
                child.setVisible(true);
            });

            frame.add(openChildButton, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}