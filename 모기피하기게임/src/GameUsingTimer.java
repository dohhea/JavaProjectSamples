import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

// Revision : 2021-08-17
//Memo : java.applet.AudioClip; is depreciated.
//Revised using javax.sound.sampled.* classes 

//Revision : 2021-09-13
//Memo : JLayeredPane is not working properly.
//Revised using CardLayout 

// 1) 맞춰질때까지의 총시간으로 점수를 매기면 좋겠음 (최고점수자 등록)

public class GameUsingTimer {
	private final int S_MARGIN = 40;  		// 그림의 얼마 범위에 들어왔을 때 충돌로 결정할 것인지의 값(작은 그림)
	private final int B_MARGIN = 80;  		// 그림의 얼마 범위에 들어왔을 때 충돌로 결정할 것인지의 값(큰 그림)
	private final int WIN_WIDTH = 660; 		// 전체 frame의 폭
	private final int WIN_HEIGHT = 700; 	// 전체 frame의 높이
	private final int NEW_ATTACKER_INTERVAL= 10;	// 새로운 공격자가 나타나는 주기
	private final int BIG_ATTACKER_INTERVAL= 10;	// 큰 공격자가 나타나는 주기 (토글 방식)
	private final int SPEED = 50;			// 애니매이션의 속도 (밀리초)
	private final int STEPS = 10;			// 그림 객체들이 한번에 움직이는 픽슬 수
	// 버튼 토글을 위한 비트 연산에 사용될 상수들
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	// 사용할 공격자 및 플레이어 그림 및 음향
	// 그림등은 별도의 res폴더에 놓기로 함
	// src 폴더가 루트 "/"로 인식되므로 res 폴더를 그 밑에 만들어 루트부터 경로명을 줌
	private final String ATTACKER_PIC = "/res/8.gif";
	private final String BIG_ATTACKER_PIC = "/res/9.gif";
	private final String PLAYER_PIC = "/res/2.gif";
	private final String MAIN_PIC = "/res/main1.jpg";
	private final String BACKGROUND_SOUND = "C:\\start.wav";
	private final String BOOM_SOUND = "C:\\boom.wav";

	JFrame frame=new JFrame();				// 전체 GUI를 담을 프레임에 대한 레퍼런스
	int gamePanelWidth, gamePanelHeight;	// 실제 게임이 이루어질 영역의 크기 
	JPanel controlPanel=new JPanel();		// 게임 컨트롤과 시간, 사용자 디스플레이가 들어갈 패널
	JButton start=new JButton("시작");		// 시작버튼
	JButton end=new JButton("종료");			// 종료버튼
	JButton suspend=new JButton("일시중지");	// 일시중지 버튼
	JButton cont=new JButton("계속");			// 계속 버튼
	JLabel timing=new JLabel("시간  : 0분 0초");// 게임경과 시간 디스플레이를 위한 라벨
	JPanel midPanel;						// 중앙을 차지할 패널
	JPanel coverPanel;						// 초기화면이 나타날 패널	
	GamePanel gamePanel;					// 게임이 이루질 패널
	Container container;					// 게임이 이루어질 패널의 pane을 가질 컨테이너
	CardLayout card;						// 게임이 이루어질 패널에 화면을 여러장 겹치기 위한 Card 레이어
	Timer goAnime;							// 그래픽 객체의 움직임을 관장하기 위한 타이머
	Timer goClock;							// 시계구현을 위한 위한 타이머
	ClockListener clockListener;			// 시계를 구현하기 위한 리스너
	ArrayList<Shape> attackerList;			// 게임에 사용되는 (일반) 공격자 객체를 담는 리스트
	ArrayList<Shape> bigAttackerList;		// 게임에 사용되는 큰 공격자 객체를 담는 리스트
	Shape player;							// 키보드로 움직이는 Player 객체
	DirectionListener keyListener;			// 화살표 움직임을 감지하는 리스너
	private Clip backgroundClip;			// 게임 배경 음악
	private Clip boomClip;					// 충돌음향
	static String playerName;				// 플레이어 이름

	public static void main(String [] args) {
		playerName=JOptionPane.showInputDialog("이름을 입력해주세요 :");	// Player의 이름 입력
		new GameUsingTimer().go();									// 게임의  초기화
	}

	public void go() {
		//GUI세팅
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// 게임 조정 버튼 및 디스플레이 라벨들이 들어갈 패널
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		controlPanel.add(timing);
		controlPanel.add(new JLabel(" Player : "));
		controlPanel.add(new JLabel(playerName));
		
		// 게임의 진행이 디스플레이 될 패널
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// 초기화면을 위한 패널
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		
		// 초기화면과 게임화면을 레이어화 함
		midPanel = new JPanel();
		card = new CardLayout();
		midPanel.setLayout(card);
		midPanel.add("1", coverPanel);
		midPanel.add("2", gamePanel);
		
		// 전체 프레임에 배치
		frame.add(BorderLayout.CENTER, midPanel);
		frame.add(BorderLayout.SOUTH, controlPanel);
		
		// 게임이 이루어질 패널의 실제 폭과 넓이 계산
		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -130;

		//출력될 객체들을 생성 (모기)하여 attackerList에 넣어 줌
		prepareAttackers();
		
		// 키보드로 움직일 player 개체 생성
		player = new Shape(getClass().getResource(PLAYER_PIC), B_MARGIN, gamePanelWidth, gamePanelHeight);
		
		// 시간 디스플레이, 객체의 움직임을 자동화 하기 위한 타이머들 
		clockListener = new ClockListener();
		goClock = new Timer(1000, clockListener);			// 시간을 초단위로 나타내기 위한 리스너
		goAnime = new Timer(SPEED, new AnimeListener());	// 그림의 이동을 처리하기 위한 리스너

		// Player의 키보드 움직임을 위한 감청자
		gamePanel.addKeyListener(new DirectionListener());	// 키보드 리스너 설치
		gamePanel.setFocusable(false);						// 초기에는 포키싱 안되게 함(즉 키 안먹음)

		// 버튼  리스너의 설치
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		// 게임을 위한 음향 파일 설치
		// 음향파일의 준비
		try {
			File file = new File(BACKGROUND_SOUND);		// 게임 배경음향 
			backgroundClip = AudioSystem.getClip(); 
			backgroundClip.open(AudioSystem.getAudioInputStream(file));
			
			file = new File(BOOM_SOUND);				// 폭파음향 
			boomClip = AudioSystem.getClip(); 
			boomClip.open(AudioSystem.getAudioInputStream(file));
		} catch (Exception e) { 
			System.out.println("음향 파일 로딩 실패");
		} 		
		
		// 화면의 활성화
		buttonToggler(START);	// 초기에는 start버튼만 비 활성화
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);
	}
	 
	// 서비스 함수들

	// 버튼의 활성 비활성화를 위한 루틴
	private void buttonToggler(int flags) {
		if ((flags & START) != 0)
			start.setEnabled(true);
		else
			start.setEnabled(false);
		if ((flags & SUSPEND) != 0)
			suspend.setEnabled(true);
		else
			suspend.setEnabled(false);
		if ((flags & CONT) != 0)
			cont.setEnabled(true);
		else
			cont.setEnabled(false);
		if ((flags & END) != 0)
			end.setEnabled(true);
		else
			end.setEnabled(false);
	}
	
	// 게임의 시작에 사용될 공격자들
	private void prepareAttackers() {
		bigAttackerList = new ArrayList<Shape>();		// 큰 공격자의 리스트는 비움
		attackerList = new ArrayList<Shape>();			// 공격자 3으로 시작
		attackerList.add(new DiagonallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new VerticallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
	}
	
	// 게임의 종료시 처리해야 될 내용
	private void finishGame() {
		backgroundClip.stop();				// 음향 종료
		goClock.stop();						// 시간 디스플에이 멈춤
		goAnime.stop();						// 그림객체 움직임 멈춤
		gamePanel.setFocusable(false);		// 포커싱 안되게 함(즉 키 안먹음)
		buttonToggler(START);				// 활성화 버튼의 조정
	}
	
	// 여러종류의 움직임을 랜덤으로 발생시키는 공격객체의 생성
	private Shape getRandomAttacker(String pic, int margin, int steps) {
		int rand = (int)(Math.random() * 3) + 1;
		Shape newAttacker;
		switch (rand) {
		case 1 :
			newAttacker =  new DiagonallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
			break;
		case 2 :
			newAttacker =  new HorizontallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
			break;
		case 3 :
			newAttacker =  new VerticallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
			break;
		default :	
			newAttacker =  new DiagonallyMovingShape(getClass().getResource(pic), margin, steps, gamePanelWidth, gamePanelHeight);
		}
		return newAttacker;
	}
	
	// 내부 클래스 들
	
	// goAnime 타이머에 의해 주기적으로 실행될 내용
	// 객체의 움직임, 충돌의 논리를 구현
	public class AnimeListener implements ActionListener {
		private void playBoom() {
			boomClip.loop(1);					// 음향반복 횟수
			boomClip.start();					// 충돌의 음향
			try {								
				Thread.sleep(1000);				// 1000 밀리초(1초) 만큼 프로그램 대기
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			boomClip.stop();					// 음향 종료
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// 만약 충돌하였으면 충돌의 효과음 나타내고 타이머를 중단시킴
			for (Shape s : attackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					playBoom();					// 폭발음 실행	
					finishGame();				// 게임 중단
					return;
				}
			}
			for (Shape s : bigAttackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					playBoom();					// 폭발음 실행	
					finishGame();				// 게임 중단
					return;
				}
			}
			// 그림 객체들을 이동시킴
			for (Shape s : attackerList) {
				s.move();
			}
			for (Shape s : bigAttackerList) {
				s.move();
			}

			// 화면을 전체 다 다시 그려줌. 그런데, 여기서 repaint를 하지 않아도 애니메이션 잘 됨.
			// 이유는 모기가 Animated GIF 라서 자동으로 repaint가 불리게 됨으로써 그러한 현상이 일어는 것임
			// 여기에 중복으로 해 주어도 무방함
			frame.repaint();								
		}
	}

	
	// 시작 버튼의 감청자
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// card.next(midPanel);							// gamePanel 이 앞으로 나오게 함
			card.show(midPanel, "2");							// gamePanel 이 앞으로 나오게 함
			gamePanel.setFocusable(true);					// gamePanel이 포커싱될 수 있게 함
			gamePanel.requestFocus();						// 포커싱을 맞춰줌(이것 반드시 필요)

			backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);	// 게임 배경음악 반복							// 음향반복 횟수
			backgroundClip.start();							// 게임 배경음향

			goAnime.start();								// 그림객체 움직임을 위한 시작

			clockListener.reset();							// 타이머의 시작값 초기화
			timing.setText("시간  : 0분 0초");	
			goClock.start();								// 시간 디스플레이 타이머시작

			prepareAttackers();								// 초기 공격자 준비

			buttonToggler(SUSPEND+END);						// 활성화된 버튼의 조정

		}
	}
	
	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.stop();		
			goAnime.stop();
			gamePanel.setFocusable(false);					// 게임 프레임에 키 안먹게 함
			buttonToggler(CONT+END);						// 활성화 버튼의 조정
		}
	}
	
	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.restart();
			goAnime.restart();
			gamePanel.setFocusable(true);					// 게임 프레임 키 먹게 함
			gamePanel.requestFocus();						// 전체 프레밍에 포커싱해서 키 먹게 함
			buttonToggler(SUSPEND+END);						// 활성화 버튼의 조정
		}
	}

	// 종료버튼을 위한 감청자
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			finishGame();
		}
	}

	// 게임이 진행되는 메인 패널
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0,0,this.getWidth(), this.getHeight());		// 화면 지우기
		
			// 게임에 사용되는 그래픽 객체들 모두 그려줌
			for (Shape s : attackerList) {
				s.draw(g, this);
			}
			for (Shape s : bigAttackerList) {
				s.draw(g, this);
			}
			player.draw(g, this);	
		}
	}
	
	// 초기화면을 나타내는 패널
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 
			g.drawImage(image,0,0,this);
		}
	}
	
	// 시간 디스플레이를 위해 사용하는 시계
	private class ClockListener implements ActionListener {
		int times = 0;
		public void actionPerformed (ActionEvent event) {		
			times++;						
			timing.setText("시간  : "+times/60+"분 "+times%60+"초");

			// 시간이 일정시간 지나면 새로운 모기를 출현시킴
			if (times % NEW_ATTACKER_INTERVAL == 0)
				attackerList.add(getRandomAttacker(ATTACKER_PIC, S_MARGIN, STEPS));

			// 시간이 일정시간 지나면 bigAttacker 출현/소멸 시킴
			if (times % BIG_ATTACKER_INTERVAL == 0) {
				if (bigAttackerList.isEmpty())			// 현재 활동 중이 아니면 하나 추가	
					bigAttackerList.add(getRandomAttacker(BIG_ATTACKER_PIC, B_MARGIN, STEPS));
				else									// 현재 활동 중이면 리스트 비우기
					bigAttackerList = new ArrayList<Shape>();
			}
		}
		
		public void reset() {
			times = 0;
		}
		public int getElaspedTime() {
			return times;
		}
	}
	
	// 키보드 움직임을 감청하는 감청자
	class DirectionListener implements KeyListener {
	   public void keyPressed (KeyEvent event) {
		   switch (event.getKeyCode()){
		   case KeyEvent.VK_UP:
			   if (player.y >= 0)
				   player.y -= STEPS;
			   break;
		   case KeyEvent.VK_DOWN:
			   if (player.y <= gamePanelHeight)
				   player.y += STEPS;
			   break;
		   case KeyEvent.VK_LEFT:
			   if (player.x >= 0)
				   player.x -= STEPS;
			   break;
		   case KeyEvent.VK_RIGHT:
			   if (player.x <= gamePanelWidth)
				   player.x += STEPS;
			   break;
		   }
	   }
	   public void keyTyped (KeyEvent event) {}
	   public void keyReleased (KeyEvent event) {}
   }
}