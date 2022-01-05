import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OthelloGame extends JPanel {
	public enum Direction{NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};
	public enum StoneColor{EMPTY, BLACK, WHITE};
	private StoneColor selectedColor = null;
	private JLabel whiteCount, blackCount, turnLabel;
	private JPanel labelPanel = new JPanel();	// 상태 라벨
	private JPanel buttonPanel = new JPanel();	// 격자 패널
	private JPanel panel, countSet, turnLabelPanel;
	private JButton [][]button;					// 격자 배열
	private StoneColor [][]stoneInform = null;  // 각 버튼에 어떤 돌이 놓여 졌는지 판한하는 배열
	
	private JButton help;						// Help : 게임방법설명  버튼
	private JButton ranking;					// Ranking	: 순위 보기 버튼
	private JButton quit;						// Quit : 끝내기 버튼
	private JButton winnerCheck;				// 랭킹등록 버튼
	private final int ROW, COLUMN;				// 격자 행열의 수
	private int rowCenter, columnCenter; 				
	private ImageIcon whiteIcon, blackIcon;		// 흰 돌과 검은 돌 이미지.
	private Font font;
	private ActionListener listener;
	
	public OthelloGame()	
	{
		// 초기화.		
		ROW=8;		COLUMN=8;		// 행,열의 수를 8로 초기화. 	
		
		panel = new JPanel();
		countSet = new JPanel();
		turnLabelPanel = new JPanel();
		button = new JButton[ROW][COLUMN];					// 8*8 버튼 객체를 지칭하는 8*8포인터 배열 생성.
		buttonPanel.setLayout(new GridLayout(ROW,COLUMN));	// 격자 레이아웃(grid layout)의 9*9의격자에 컨테이너의 컴포넌트 배치
		help = new JButton("          Help         ");
		winnerCheck = new JButton("Winner Check");
		ranking = new JButton(" Ranking View ");
		quit = new JButton("          Quit          ");
		
		
		whiteIcon = new ImageIcon("white_diske.jpg");		// 이미지 아이콘 객체생성
		blackIcon = new ImageIcon("black_diske.jpg");
		whiteCount = new JLabel("2", whiteIcon, SwingConstants.CENTER);
		whiteCount.setHorizontalTextPosition(SwingConstants.RIGHT);
		blackCount = new JLabel("2", blackIcon, SwingConstants.CENTER);
		blackCount.setHorizontalTextPosition(SwingConstants.RIGHT);
		turnLabel = new JLabel("Turn ", whiteIcon, SwingConstants.CENTER);
		turnLabel.setHorizontalTextPosition(SwingConstants.LEFT);		
		//폰트설정
		font = new Font("Showcard Gothic", Font.PLAIN, 25);
		turnLabel.setFont(font);
		stoneInform = new StoneColor[ROW][COLUMN];
		selectedColor=StoneColor.WHITE;
		
		listener = new ButtonListener();
		for(int i=0 ; i<ROW ; i++) {	
			for(int j=0 ; j<COLUMN ; j++) {				
				button[i][j] = new JButton();	// 버튼객체 생성.
				button[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
				buttonPanel.add(button[i][j]);	// 버튼 추가 
				button[i][j].addActionListener(listener);	// 버튼동작 인터페이스 설정
				
				// 돌의 정보를 EMPTY로 초기화
				stoneInform[i][j]=StoneColor.EMPTY; 
			}
		}
		// 버튼 및 라벨 추가
		labelPanel.add(Box.createRigidArea(new Dimension(0,50)));
		countSet.add(whiteCount);
		countSet.add(blackCount);	
		turnLabelPanel.add(turnLabel);		
		countSet.setPreferredSize(new Dimension(180,50));
		turnLabelPanel.setPreferredSize(new Dimension(180,50));
		
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		labelPanel.add(countSet);
		labelPanel.add(turnLabelPanel);
		labelPanel.add(help);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));	// 고정된 크기를 가진 여백 고정 범위 첨가.
		labelPanel.add(winnerCheck);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		labelPanel.add(ranking);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		labelPanel.add(quit);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		panel.add(buttonPanel);
		
		// 각 버튼동작 인터페이스 추가.
		//help.addActionListener(new HelpListener());		
		//ranking.addActionListener(new RankingListener());
		quit.addActionListener(new QuitListener());
		//winnerCheck.addActionListener(new WinnerCheckListener());
		
		// 사이즈 설정.
		labelPanel.setPreferredSize(new Dimension(180,270));
		buttonPanel.setPreferredSize(new Dimension(300,300));		
		
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	// 박스 레이아웃
		add(labelPanel);
		add(panel);
		
		initializeGame();		// 처음 게임시작 시 호출함수.
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
				break;
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
		}
	}
	// 현재 흰돌과 검은돌의 수를 카운터해서 돌의 숫자를 나타내는 레이블에 업데이트 한다
	public void countStone(){
		int empty=0;
		int white=0;
		int black=0;
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
	private class ButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent event){
			Object source= event.getSource();
			for(int i=0;i<ROW;i++){
				for(int j=0;j<COLUMN;j++){
					if(source==button[i][j]){
						button[i][j].setIcon(selectedColor==StoneColor.BLACK ? blackIcon : whiteIcon);
						stoneInform[i][j]=selectedColor;
						Point p=new Point(i,j);
						changeStone(p,selectedColor);
						countStone();
						// 턴을 다음 돌로 넘긴다
						turnLabel.setIcon(selectedColor==StoneColor.BLACK ? whiteIcon : blackIcon);
						selectedColor=(selectedColor==StoneColor.BLACK) ? StoneColor.WHITE : StoneColor.BLACK;
					}
				}
			}
		}
	}
	private class QuitListener implements ActionListener 	// 종료 버튼(모든 프레임과 창이  강제로 닫힌다.)
	{                
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);	// 프로그램 종료
		}
	}
}
