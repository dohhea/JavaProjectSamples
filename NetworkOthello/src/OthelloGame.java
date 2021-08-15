import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

public class OthelloGame extends JPanel {
	public enum Direction{NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};
	public enum StoneColor{EMPTY, BLACK, WHITE};
	private StoneColor selectedColor = null;
	private JLabel whiteCount, blackCount, turnLabel, user1Label, user2Label;
	private JPanel labelPanel = new JPanel();	// ���� ��
	public JPanel buttonPanel = new JPanel();	// ���� �г�
	private JPanel panel, countSet, turnLabelPanel, menuPanel, whiteP, blackP, nameP;
	private JButton [][]button;					// ���� �迭
	private StoneColor [][]stoneInform = null;  // �� ��ư�� � ���� ���� ������ �����ϴ� �迭
	
	private JButton help;						// Help : ���ӹ������  ��ư
	private JButton ranking;					// Ranking	: ���� ���� ��ư
	private JButton quit;						// Quit : ������ ��ư
	private JButton start;				// ��ŷ��� ��ư
	private final int ROW, COLUMN;				// ���� �࿭�� ��
	private int rowCenter, columnCenter; 				
	private ImageIcon whiteIcon, blackIcon;		// �� ���� ���� �� �̹���.
	private ImageIcon whiteIcon1, blackIcon1;
	public JTextField user1,user2;
	String my, you;
	private Font font;
	private ActionListener listener;
	private boolean turnCheck=false;
	private boolean startCheck=false;
	public boolean userCheck = false;
	private int empty=0;
	private int white=0;
	private int black=0;
	private AudioClip music;
	private AudioClip BGM;

	
    ObjectInputStream reader;	// ���ſ� ��Ʈ��
    ObjectOutputStream writer;	// �۽ſ� ��Ʈ��
	
	public OthelloGame()	
	{
		// �ʱ�ȭ.		
		ROW=8;		COLUMN=8;		// ��,���� ���� 8�� �ʱ�ȭ. 	
		
		panel = new JPanel();
		countSet = new JPanel();
		turnLabelPanel = new JPanel();
		menuPanel = new JPanel();
		button = new JButton[ROW][COLUMN];					// 8*8 ��ư ��ü�� ��Ī�ϴ� 8*8������ �迭 ����.
		buttonPanel.setLayout(new GridLayout(ROW,COLUMN));	// ���� ���̾ƿ�(grid layout)�� 9*9�ǰ��ڿ� �����̳��� ������Ʈ ��ġ
		help = new JButton("Help");
		start = new JButton("Start");
		ranking = new JButton(" Ranking View ");
		quit = new JButton("Quit");
		whiteP = new JPanel();
		blackP = new JPanel();
		nameP = new JPanel();
		
		whiteIcon = new ImageIcon("white_diske1.gif");		// �̹��� ������ ��ü����
		blackIcon = new ImageIcon("black_diske1.gif");
		whiteIcon1 = new ImageIcon("white_diske.gif");
		blackIcon1 = new ImageIcon("black_diske.gif");
		
		whiteCount = new JLabel("0",whiteIcon1, SwingConstants.CENTER);
		whiteCount.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
		blackCount = new JLabel("0",blackIcon1, SwingConstants.CENTER);
		blackCount.setHorizontalTextPosition(SwingConstants.HORIZONTAL);
		
		turnLabel = new JLabel("Turn ", blackIcon1, SwingConstants.CENTER);
		turnLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		user1 = new JTextField();
		user2 = new JTextField();
		//��Ʈ����
		font = new Font("Showcard Gothic", Font.PLAIN, 25);
		turnLabel.setFont(font);
		font = new Font("MD�Ʒ�ü", Font.PLAIN, 25);
		user1.setFont(font);
		user2.setFont(font);
		user1.setEditable(false);
		user2.setEditable(false);
		
		
		stoneInform = new StoneColor[ROW][COLUMN];
		selectedColor=StoneColor.BLACK;
		
		listener = new ButtonListener();
		for(int i=0 ; i<ROW ; i++) {	
			for(int j=0 ; j<COLUMN ; j++) {				
				button[i][j] = new JButton();	// ��ư��ü ����.
				button[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
				button[i][j].setEnabled(false);
				buttonPanel.add(button[i][j]);	// ��ư �߰�
				button[i][j].addActionListener(listener);	// ��ư���� �������̽� ����
				
				// ���� ������ EMPTY�� �ʱ�ȭ
				stoneInform[i][j]=StoneColor.EMPTY; 
			}
		}
		// ��ư �� �� �߰�
		//labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		countSet.setLayout(null);
	
		user1.setBounds(20,15,120,40);
		whiteCount.setBounds(140,10,50,50);
		user2.setBounds(210,15,110,40);
		blackCount.setBounds(320,10,50,50);
		turnLabel.setBounds(380,5,120,60);
		
		user1.setHorizontalAlignment(JTextField.CENTER);
		user2.setHorizontalAlignment(JTextField.CENTER);
		
		blackCount.setForeground(Color.WHITE);
		font = new Font("Bauhaus 93", Font.PLAIN, 20);
		blackCount.setFont(font);
		whiteCount.setFont(font);
		
		countSet.add(user1);
		countSet.add(whiteCount);
		countSet.add(user2);
		countSet.add(blackCount);
		countSet.add(turnLabel);
		
		//nameP.add(turnLabel);
		
		//turnLabel.setPreferredSize(new Dimension(50,0));
		//turnLabelPanel.setPreferredSize(new Dimension(50,0));
		
		
		//countSet.setLayout(new BoxLayout(countSet, BoxLayout.X_AXIS));
		//labelPanel.add(countSet);
		//labelPanel.add(turnLabelPanel);
		
		menuPanel.setLayout(new GridLayout(1,4));// ������ ũ�⸦ ���� ���� ���� ���� ÷��.	
		menuPanel.add(start);
		menuPanel.add(help);
		menuPanel.add(ranking);
		menuPanel.add(quit);
		
		panel.add(buttonPanel);
		
		// �� ��ư���� �������̽� �߰�.
		help.addActionListener(new HelpButtonListener());		
		//ranking.addActionListener(new RankingListener());
		quit.addActionListener(new QuitListener());
		start.addActionListener(new StartButtonListener());
		
		// ������ ����.
		countSet.setPreferredSize(new Dimension(55,55));
		buttonPanel.setPreferredSize(new Dimension(200,200));		
		countSet.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 20));
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, countSet);
		add(BorderLayout.CENTER, buttonPanel);
		add(BorderLayout.SOUTH, menuPanel);
		
				// ó�� ���ӽ��� �� ȣ���Լ�.
	}	
	
	// ���� ��ư�� ��ġ���� 8������ ���� ������ ��ġ�� �������� ������ ī������ �� ���� �ٲ��ش�
	public void changeStone(Point p,StoneColor color){
		compareAndChagneStone(Direction.NORTH, p, color);
		compareAndChagneStone(Direction.NORTHEAST, p, color);
		compareAndChagneStone(Direction.EAST, p, color);
		compareAndChagneStone(Direction.SOUTHEAST, p, color);
		compareAndChagneStone(Direction.SOUTH, p, color);
		compareAndChagneStone(Direction.SOUTHWEST, p, color);
		compareAndChagneStone(Direction.WEST, p, color);
		compareAndChagneStone(Direction.NORTHWEST, p, color);
		
	}
	
	//d�������� p���� �����ؼ�  ���� ������ ��(color)����  �ٲ�� �� ���� ������ ī�������� ���� �ٲ��ش� 
	public void compareAndChagneStone(Direction d,Point p,StoneColor color){
		Point currentP=p;
		int count=0; // ���� ������ ���� ���� ������ ī���Ͱ��� �����ϴ� ����
		p=nextPoint(d,p);
		while((p.getX()>=0&&p.getX()<=ROW-1)&&(p.getY()>=0&&p.getY()<=ROW-1)){
			// ���� ������ ��� ���ݱ��� ī������ ���� �ʱ�ȭ�ϰ� �극��ũ 
			if(stoneInform[p.getX()][p.getY()]==StoneColor.EMPTY){
				count=0;
				break;
			}
			//���� ���� ������ ���� �ڽ��� ���� ����� �ٲٸ� �극��ũ
			if(stoneInform[p.getX()][p.getY()]==selectedColor)
			{	
				break;
			}
			if(stoneInform[p.getX()][p.getY()]!=selectedColor)
				if(nextPoint(d,p).getX()<0||nextPoint(d,p).getX()>7){
					count=0;
					break;
				}
				else if(nextPoint(d,p).getY()<0||nextPoint(d,p).getY()>7){
					count=0;
					break;
				}
			count++;
			p=nextPoint(d,p);
		}
		setStone(currentP,d,count,color);
	}
	
	// ������ǥ���� �Ѱ��� �������� ��ĭ�� �̵��� ��ǥ�� ����
	public Point nextPoint(Direction d,Point p){
		switch(d){
		case NORTH :
			return new Point(p.getX()-1,p.getY());
		case NORTHEAST :
			return new Point(p.getX()-1,p.getY()+1);
		case EAST :
			return new Point(p.getX(),p.getY()+1);
		case SOUTHEAST :
			return new Point(p.getX()+1,p.getY()+1);
		case SOUTH :
			return new Point(p.getX()+1,p.getY());
		case SOUTHWEST :
			return new Point(p.getX()+1,p.getY()-1);
		case WEST :
			return new Point(p.getX(),p.getY()-1);
		case NORTHWEST :
			return new Point(p.getX()-1,p.getY()-1);
		}
		return null;
	}
	
	// ���� ��ǥ(currentP)���� directionP�������� count�� ��ŭ ���� �����Ѵ�
	public void setStone(Point currentP, Direction d, int count, StoneColor color){
		for(int i=0;i<count;i++){
			currentP=nextPoint(d,currentP);
			stoneInform[currentP.getX()][currentP.getY()]=color;
			button[currentP.getX()][currentP.getY()].setIcon(color==StoneColor.BLACK ? blackIcon : whiteIcon);
			// �ѹ� ���� ���õ� ��ư�� �̺�Ʈ �߻� ���ϰ�(��ư �ȴ�����) ����
			// ��ư�� ���� �ȹٲ�� �̺�Ʈ �߻��� ���ϰ� �ϴ� ����� ���� ã�� ����
			//button[currentP.getX()][currentP.getY()].setEnabled(false);
			if(i==0) backGroundMusic("����.wav");
		}
	}
	// ���� �򵹰� �������� ���� ī�����ؼ� ���� ���ڸ� ��Ÿ���� ���̺� ������Ʈ �Ѵ�
	public void countStone(){
		white=0;
		black=0;
		empty=0;
		
		for(int i=0;i<ROW;i++){
			for(int j=0;j<COLUMN;j++){
				if(stoneInform[i][j]==StoneColor.WHITE)
					white++;
				else if(stoneInform[i][j]==StoneColor.BLACK)
					black++;
				else
					empty++;
			}
		}
		whiteCount.setText(""+white);
		blackCount.setText(""+black);
	}
	public void GameOverCheck(){
		boolean overCheck = false;
		
		if(white==0){ 
			overCheck=true;
			JOptionPane.showMessageDialog(null, user2.getText()+"�� Black WIN !!"); 
		}
		
		if(black==0){ 
			overCheck=true;
			JOptionPane.showMessageDialog(null, user1.getText()+"�� White WIN !!"); 
		}
		if(empty==0)
		{
			overCheck=true;
			if(white>black) JOptionPane.showMessageDialog(null, user1.getText()+"�� White WIN !!"); 
			else JOptionPane.showMessageDialog(null, user2.getText()+"�� Black WIN !!");
		}

		if(overCheck)
		{
			startCheck = false;
			turnCheck = false;
			userCheck = false;
			BGM.stop();
			selectedColor=StoneColor.BLACK;
			for(int i=0;i<ROW;i++){
				for(int j=0;j<COLUMN;j++){
					button[i][j].setEnabled(false);
					button[i][j].setBackground(Color.GRAY);
				}
			}
			turnLabel.setIcon(blackIcon1);
		}
	}
	
	public void BoardSet(int i, int j){
			turnCheck=!turnCheck;
			if(this.turnCheck){buttonPanel.setBackground(new Color(100,200,40)); countSet.setBackground(new Color(100,200,40));}
			else {buttonPanel.setBackground(Color.red); countSet.setBackground(Color.red);}
			
			if(user1.getBackground().equals(Color.GREEN)) System.out.println("ü����");//user1.setBackground(Color.RED);
			if(user1.getBackground().equals(Color.RED)) user1.setBackground(Color.GREEN);
			if(user2.getBackground().equals(Color.GREEN)) user1.setBackground(Color.RED);
			if(user2.getBackground().equals(Color.RED)) user1.setBackground(Color.GREEN);
			
			this.button[i][j].setIcon(selectedColor==StoneColor.BLACK ? blackIcon : whiteIcon);
			this.button[i][j].setEnabled(true);
			stoneInform[i][j]=selectedColor;
			Point p=new Point(i,j);
			changeStone(p,selectedColor);
			countStone();
			// ���� ���� ���� �ѱ��
			turnLabel.setIcon(selectedColor==StoneColor.BLACK ? whiteIcon1 : blackIcon1);
			selectedColor=(selectedColor==StoneColor.BLACK) ? StoneColor.WHITE : StoneColor.BLACK;
	
			GameOverCheck();
	}
	public void setStartCheck(boolean x)
	{
		startCheck = x;
	}
	public void setTurnCheck(boolean x)
	{
		turnCheck = x;
	}
	public AudioClip getBGM()
	{
		return BGM;
	}
	public void backGroundMusic(String name){	//�������
		try{
			music = Applet.newAudioClip(new URL("file:"+name)); 
			music.play(); //�뷡�� ���۵�
			
		}catch (MalformedURLException e) {}
	}
	public void setBGM(String name){	//�������
		try{
			BGM = Applet.newAudioClip(new URL("file:"+name)); 
			BGM.loop();
			BGM.play(); //�뷡�� ���۵�			
		}catch (MalformedURLException e) {}
	}
	private class ButtonListener implements ActionListener{
		private int i,j;
		public void actionPerformed(ActionEvent event){
			if(turnCheck){ 
			try {
		   		for(int i=0;i<ROW;i++){
					for(int j=0;j<COLUMN;j++){
						if(event.getSource()==button[i][j]){
							this.i=i;
							this.j=j;
							button[i][j].setEnabled(false);
							break;
						}
					}
				}
		   		
		   		if(button[i][j].getIcon()!=null) JOptionPane.showMessageDialog(null, "�װ��� ������ �����ϴ�.");
		   		else{
		   			backGroundMusic("�ٵϾ�.wav");
			   		writer.writeObject(new OthelloMessage(OthelloMessage.MsgType.GAME_INFO,i,j));
			   		writer.flush();
		   		}
		   		 
		   	  } catch(Exception ex) {
		   		  JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
		      	  ex.printStackTrace();
		      }
			}
			else  JOptionPane.showMessageDialog(null, "���� �����Դϴ�.");
		}
	}
	private class QuitListener implements ActionListener 	// ���� ��ư(��� �����Ӱ� â��  ������ ������.)
	{                
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);	// ���α׷� ����
		}
	}
	public void gameSet(String my, String you){
		for(int i=0 ; i<ROW ; i++) {	
			for(int j=0 ; j<COLUMN ; j++) {				
				button[i][j].setEnabled(true);
				button[i][j].setIcon(null);
				button[i][j].setBackground(new Color(0,80,0));
				// ���� ������ EMPTY�� �ʱ�ȭ
				stoneInform[i][j]=StoneColor.EMPTY; 
			}
		}
		if(userCheck) {
			user1.setText(my); 
			user2.setText(you); 
		}
		if(!userCheck) {
			user1.setText(you); 
			user2.setText(my);
		}
		
		if(this.turnCheck){buttonPanel.setBackground(new Color(100,200,40)); countSet.setBackground(new Color(100,200,40));}
		else {buttonPanel.setBackground(Color.red); countSet.setBackground(Color.red);}
	}
	
	public void initializeGame()	// ���� ���۽� ó�� ���� setting�ϴ� �Լ�.
	{
		rowCenter = ROW/2;			// ���� ��� �迭 index���� ã�´�.
		columnCenter = COLUMN/2;	// ���� ��� �迭 index���� ã�´�.
		
		button[rowCenter-1][columnCenter-1].setIcon(whiteIcon);	//
		button[rowCenter-1][columnCenter].setIcon(blackIcon);	//	�ۡ�
		button[rowCenter][columnCenter-1].setIcon(blackIcon);	//	�ܡ�  ���·� set��Ų��.
		button[rowCenter][columnCenter].setIcon(whiteIcon);		//
		stoneInform[rowCenter-1][columnCenter-1]=StoneColor.WHITE;
		stoneInform[rowCenter-1][columnCenter]=StoneColor.BLACK;
		stoneInform[rowCenter][columnCenter-1]=StoneColor.BLACK;
		stoneInform[rowCenter][columnCenter]=StoneColor.WHITE;
	}
	public class StartButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(!startCheck){ 
				try {
					
					startCheck = true;
					turnCheck = true;
					userCheck = true;
			   		writer.writeObject(new OthelloMessage(OthelloMessage.MsgType.GAME_START, userCheck));
			   		writer.flush();
		   					   		 
			   	  } catch(Exception ex) {
			   		  JOptionPane.showMessageDialog(null, "�޽��� ������ ������ �߻��Ͽ����ϴ�.");
			      	  ex.printStackTrace();
				}
			}
			else  JOptionPane.showMessageDialog(null, "������ �������Դϴ�.");
		}
	}
	public class HelpButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			new HelpFrame();
		}
		
	}

}



