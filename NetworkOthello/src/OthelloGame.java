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
	private JPanel labelPanel = new JPanel();	// 상태 라벨
	public JPanel buttonPanel = new JPanel();	// 격자 패널
	private JPanel panel, countSet, turnLabelPanel, menuPanel, whiteP, blackP, nameP;
	private JButton [][]button;					// 격자 배열
	private StoneColor [][]stoneInform = null;  // 각 버튼에 어떤 돌이 놓여 졌는지 판한하는 배열
	
	private JButton help;						// Help : 게임방법설명  버튼
	private JButton ranking;					// Ranking	: 순위 보기 버튼
	private JButton quit;						// Quit : 끝내기 버튼
	private JButton start;				// 랭킹등록 버튼
	private final int ROW, COLUMN;				// 격자 행열의 수
	private int rowCenter, columnCenter; 				
	private ImageIcon whiteIcon, blackIcon;		// 흰 돌과 검은 돌 이미지.
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

	
    ObjectInputStream reader;	// 수신용 스트림
    ObjectOutputStream writer;	// 송신용 스트림
	
	public OthelloGame()	
	{
		// 초기화.		
		ROW=8;		COLUMN=8;		// 행,열의 수를 8로 초기화. 	
		
		panel = new JPanel();
		countSet = new JPanel();
		turnLabelPanel = new JPanel();
		menuPanel = new JPanel();
		button = new JButton[ROW][COLUMN];					// 8*8 버튼 객체를 지칭하는 8*8포인터 배열 생성.
		buttonPanel.setLayout(new GridLayout(ROW,COLUMN));	// 격자 레이아웃(grid layout)의 9*9의격자에 컨테이너의 컴포넌트 배치
		help = new JButton("Help");
		start = new JButton("Start");
		ranking = new JButton(" Ranking View ");
		quit = new JButton("Quit");
		whiteP = new JPanel();
		blackP = new JPanel();
		nameP = new JPanel();
		
		whiteIcon = new ImageIcon("white_diske1.gif");		// 이미지 아이콘 객체생성
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
		//폰트설정
		font = new Font("Showcard Gothic", Font.PLAIN, 25);
		turnLabel.setFont(font);
		font = new Font("MD아롱체", Font.PLAIN, 25);
		user1.setFont(font);
		user2.setFont(font);
		user1.setEditable(false);
		user2.setEditable(false);
		
		
		stoneInform = new StoneColor[ROW][COLUMN];
		selectedColor=StoneColor.BLACK;
		
		listener = new ButtonListener();
		for(int i=0 ; i<ROW ; i++) {	
			for(int j=0 ; j<COLUMN ; j++) {				
				button[i][j] = new JButton();	// 버튼객체 생성.
				button[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
				button[i][j].setEnabled(false);
				buttonPanel.add(button[i][j]);	// 버튼 추가
				button[i][j].addActionListener(listener);	// 버튼동작 인터페이스 설정
				
				// 돌의 정보를 EMPTY로 초기화
				stoneInform[i][j]=StoneColor.EMPTY; 
			}
		}
		// 버튼 및 라벨 추가
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
		
		menuPanel.setLayout(new GridLayout(1,4));// 고정된 크기를 가진 여백 고정 범위 첨가.	
		menuPanel.add(start);
		menuPanel.add(help);
		menuPanel.add(ranking);
		menuPanel.add(quit);
		
		panel.add(buttonPanel);
		
		// 각 버튼동작 인터페이스 추가.
		help.addActionListener(new HelpButtonListener());		
		//ranking.addActionListener(new RankingListener());
		quit.addActionListener(new QuitListener());
		start.addActionListener(new StartButtonListener());
		
		// 사이즈 설정.
		countSet.setPreferredSize(new Dimension(55,55));
		buttonPanel.setPreferredSize(new Dimension(200,200));		
		countSet.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 20, 20));
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, countSet);
		add(BorderLayout.CENTER, buttonPanel);
		add(BorderLayout.SOUTH, menuPanel);
		
				// 처음 게임시작 시 호출함수.
	}	
	
	// 눌린 버튼의 위치에서 8방향의 같은 색깔의 위치의 돌까지의 간격을 카운터한 후 돌을 바꿔준다
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
	
	//d방향으로 p에서 시작해서  같은 색깔의 돌(color)까지  바꿔야 할 돌의 갯수를 카운터한후 돌을 바꿔준다 
	public void compareAndChagneStone(Direction d,Point p,StoneColor color){
		Point currentP=p;
		int count=0; // 같은 색깔의 돌이 나올 때까지 카운터값을 저장하는 변수
		p=nextPoint(d,p);
		while((p.getX()>=0&&p.getX()<=ROW-1)&&(p.getY()>=0&&p.getY()<=ROW-1)){
			// 빈돌을 만나는 경우 지금까지 카운터한 값을 초기화하고 브레이크 
			if(stoneInform[p.getX()][p.getY()]==StoneColor.EMPTY){
				count=0;
				break;
			}
			//비교할 돌의 색깔이 현재 자신의 돌의 색깔과 바꾸면 브레이크
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
	
	// 현재좌표에서 넘겨준 방향으로 한칸씩 이동한 좌표를 리턴
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
	
	// 현재 좌표(currentP)부터 directionP방향으로 count수 만큼 돌을 세팅한다
	public void setStone(Point currentP, Direction d, int count, StoneColor color){
		for(int i=0;i<count;i++){
			currentP=nextPoint(d,currentP);
			stoneInform[currentP.getX()][currentP.getY()]=color;
			button[currentP.getX()][currentP.getY()].setIcon(color==StoneColor.BLACK ? blackIcon : whiteIcon);
			// 한번 돌이 세팅된 버튼은 이벤트 발생 못하게(버튼 안눌리게) 세팅
			// 버튼의 색이 안바뀌고 이벤트 발생만 못하게 하는 방법은 아직 찾지 못함
			//button[currentP.getX()][currentP.getY()].setEnabled(false);
			if(i==0) backGroundMusic("뒤집.wav");
		}
	}
	// 현재 흰돌과 검은돌의 수를 카운터해서 돌의 숫자를 나타내는 레이블에 업데이트 한다
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
			JOptionPane.showMessageDialog(null, user2.getText()+"님 Black WIN !!"); 
		}
		
		if(black==0){ 
			overCheck=true;
			JOptionPane.showMessageDialog(null, user1.getText()+"님 White WIN !!"); 
		}
		if(empty==0)
		{
			overCheck=true;
			if(white>black) JOptionPane.showMessageDialog(null, user1.getText()+"님 White WIN !!"); 
			else JOptionPane.showMessageDialog(null, user2.getText()+"님 Black WIN !!");
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
			
			if(user1.getBackground().equals(Color.GREEN)) System.out.println("체인지");//user1.setBackground(Color.RED);
			if(user1.getBackground().equals(Color.RED)) user1.setBackground(Color.GREEN);
			if(user2.getBackground().equals(Color.GREEN)) user1.setBackground(Color.RED);
			if(user2.getBackground().equals(Color.RED)) user1.setBackground(Color.GREEN);
			
			this.button[i][j].setIcon(selectedColor==StoneColor.BLACK ? blackIcon : whiteIcon);
			this.button[i][j].setEnabled(true);
			stoneInform[i][j]=selectedColor;
			Point p=new Point(i,j);
			changeStone(p,selectedColor);
			countStone();
			// 턴을 다음 돌로 넘긴다
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
	public void backGroundMusic(String name){	//음악재생
		try{
			music = Applet.newAudioClip(new URL("file:"+name)); 
			music.play(); //노래가 시작됨
			
		}catch (MalformedURLException e) {}
	}
	public void setBGM(String name){	//음악재생
		try{
			BGM = Applet.newAudioClip(new URL("file:"+name)); 
			BGM.loop();
			BGM.play(); //노래가 시작됨			
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
		   		
		   		if(button[i][j].getIcon()!=null) JOptionPane.showMessageDialog(null, "그곳에 놓을수 없습니다.");
		   		else{
		   			backGroundMusic("바둑알.wav");
			   		writer.writeObject(new OthelloMessage(OthelloMessage.MsgType.GAME_INFO,i,j));
			   		writer.flush();
		   		}
		   		 
		   	  } catch(Exception ex) {
		   		  JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
		      	  ex.printStackTrace();
		      }
			}
			else  JOptionPane.showMessageDialog(null, "상대방 차례입니다.");
		}
	}
	private class QuitListener implements ActionListener 	// 종료 버튼(모든 프레임과 창이  강제로 닫힌다.)
	{                
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);	// 프로그램 종료
		}
	}
	public void gameSet(String my, String you){
		for(int i=0 ; i<ROW ; i++) {	
			for(int j=0 ; j<COLUMN ; j++) {				
				button[i][j].setEnabled(true);
				button[i][j].setIcon(null);
				button[i][j].setBackground(new Color(0,80,0));
				// 돌의 정보를 EMPTY로 초기화
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
	
	public void initializeGame()	// 게임 시작시 처음 돌을 setting하는 함수.
	{
		rowCenter = ROW/2;			// 행의 가운데 배열 index값을 찾는다.
		columnCenter = COLUMN/2;	// 열의 가운데 배열 index값을 찾는다.
		
		button[rowCenter-1][columnCenter-1].setIcon(whiteIcon);	//
		button[rowCenter-1][columnCenter].setIcon(blackIcon);	//	○●
		button[rowCenter][columnCenter-1].setIcon(blackIcon);	//	●○  형태로 set시킨다.
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
			   		  JOptionPane.showMessageDialog(null, "메시지 전송중 문제가 발생하였습니다.");
			      	  ex.printStackTrace();
				}
			}
			else  JOptionPane.showMessageDialog(null, "게임이 진행중입니다.");
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



