import javax.swing.*;

public class OthelloMain {
	public static void main(String[] args) {
		JFrame frame = new JFrame("OTHEELO");		// 제목 표시줄
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// x 박스 클릭시 화면 닫음.	
		frame.getContentPane().add(new OthelloGame());		
		frame.pack();
		frame.setVisible(true);
	}
}
