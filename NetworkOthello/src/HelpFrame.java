import javax.swing.*;


public class HelpFrame extends JFrame{
	ImageIcon help;
	
	public HelpFrame(){
		this.setBounds(100,0,480,650);
		help = new ImageIcon("help.jpg");
		JLabel p = new JLabel(help);
		this.getContentPane().add(p);
		this.setVisible(true);
	}
}
