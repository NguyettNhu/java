package chuong2;
import javax.swing.*;
import java.awt.*;

public class bai6 {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("My First Swing App");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);
			frame.add(new JLabel("Hello World", SwingConstants.CENTER), BorderLayout.CENTER);
			frame.setVisible(true);
		});
	}
}
