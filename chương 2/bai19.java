import javax.swing.*;
import java.awt.*;

public class bai19 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Option Dialog");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            JLabel resultLabel = new JLabel("Choose an option", SwingConstants.CENTER);
            JButton showMessageButton = new JButton("Show Message");
            showMessageButton.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "Choose Yes, No or Cancel",
                        "Confirm",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );

                if (choice == JOptionPane.YES_OPTION) {
                    resultLabel.setText("You chose Yes");
                } else if (choice == JOptionPane.NO_OPTION) {
                    resultLabel.setText("You chose No");
                } else if (choice == JOptionPane.CANCEL_OPTION) {
                    resultLabel.setText("You chose Cancel");
                } else {
                    resultLabel.setText("Dialog closed");
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(showMessageButton, BorderLayout.NORTH);
            panel.add(resultLabel, BorderLayout.CENTER);
            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }
}