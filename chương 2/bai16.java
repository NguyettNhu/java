import javax.swing.*;
import java.awt.*;

public class bai16 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("System Information");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(2, 1));
            panel.add(new JLabel("OS: " + System.getProperty("os.name"), SwingConstants.CENTER));
            panel.add(new JLabel("Java: " + System.getProperty("java.version"), SwingConstants.CENTER));
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }
}