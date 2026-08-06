import javax.swing.*;
import java.awt.*;

public class bai11 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Custom Icon Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);

            ImageIcon icon = new ImageIcon("logo.png");
            frame.setIconImage(icon.getImage());
            frame.add(new JLabel("Custom Icon Window", SwingConstants.CENTER), BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}