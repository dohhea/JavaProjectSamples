import javax.swing.*;

public class OthelloMain {
	public static void main(String[] args) {
		JFrame frame = new JFrame("OTHEELO");		// ���� ǥ����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// x �ڽ� Ŭ���� ȭ�� ����.	
		frame.getContentPane().add(new OthelloGame());		
		frame.pack();
		frame.setVisible(true);
	}
}
