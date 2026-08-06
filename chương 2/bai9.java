import javax.swing.*;

public class bai9 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Image Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ImageIcon imageIcon = new ImageIcon("image.png");
            JLabel imageLabel = new JLabel(imageIcon);
            frame.add(imageLabel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}