import javax.swing.*;
import java.awt.*;

public class bai12 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Resizable Window");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setMinimumSize(new Dimension(200, 150));
            frame.setMaximumSize(new Dimension(800, 600));
            frame.setLocationRelativeTo(null);
            frame.add(new JLabel("Resizable Window", SwingConstants.CENTER), BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}