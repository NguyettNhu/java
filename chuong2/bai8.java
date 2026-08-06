import javax.swing.*;
import java.awt.*;

public class bai8 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Exit Button");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> System.exit(0));

            frame.add(exitButton, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}