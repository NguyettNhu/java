import javax.swing.*;
import java.awt.*;

public class bai14 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dialog Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);

            JButton openDialogButton = new JButton("Open Dialog");
            openDialogButton.addActionListener(e -> {
                JDialog dialog = new JDialog(frame, "Dialog", true);
                dialog.setSize(200, 150);
                dialog.setLocationRelativeTo(frame);
                dialog.add(new JLabel("This is a dialog", SwingConstants.CENTER), BorderLayout.CENTER);
                dialog.setVisible(true);
            });

            frame.add(openDialogButton, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}