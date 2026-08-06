import javax.swing.*;

public class bai7 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Welcome");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            JOptionPane.showMessageDialog(frame, "Welcome to Java Swing");
            frame.dispose();
            System.exit(0);
        });
    }
}