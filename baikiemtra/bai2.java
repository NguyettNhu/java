import javax.swing.*;
import java.awt.*;

public class bai2 extends JFrame {
	private final JTextField txtSo1;
	private final JTextField txtSo2;
	private final JTextField txtKetQua;

	public bai2() {
		setTitle("Bài 2 - Tính tổng 2 số");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 220);
		setLocationRelativeTo(null);

		JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JLabel lblSo1 = new JLabel("Số thứ nhất:");
		JLabel lblSo2 = new JLabel("Số thứ hai:");
		JLabel lblKetQua = new JLabel("Kết quả:");

		txtSo1 = new JTextField();
		txtSo2 = new JTextField();
		txtKetQua = new JTextField();
		txtKetQua.setEditable(false);

		JButton btnTinhTong = new JButton("Tính tổng");
		btnTinhTong.addActionListener(e -> tinhTong());

		mainPanel.add(lblSo1);
		mainPanel.add(txtSo1);
		mainPanel.add(lblSo2);
		mainPanel.add(txtSo2);
		mainPanel.add(lblKetQua);
		mainPanel.add(txtKetQua);
		mainPanel.add(new JLabel());
		mainPanel.add(btnTinhTong);

		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);

		setVisible(true);
	}

	private void tinhTong() {
		try {
			double so1 = Double.parseDouble(txtSo1.getText().trim());
			double so2 = Double.parseDouble(txtSo2.getText().trim());
			double tong = so1 + so2;
			txtKetQua.setText(String.valueOf(tong));
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(
					this,
					"Vui lòng nhập đúng số ở cả hai ô.",
					"Lỗi nhập liệu",
					JOptionPane.ERROR_MESSAGE
			);
			txtKetQua.setText("");
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(bai2::new);
	}
}
