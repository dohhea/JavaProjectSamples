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

// 1) �������������� �ѽð����� ������ �ű�� ������ (�ְ������� ���)

public class GameUsingTimer {
	private final int S_MARGIN = 40;  		// �׸��� �� ������ ������ �� �浹�� ������ �������� ��(���� �׸�)
	private final int B_MARGIN = 80;  		// �׸��� �� ������ ������ �� �浹�� ������ �������� ��(ū �׸�)
	private final int WIN_WIDTH = 660; 		// ��ü frame�� ��
	private final int WIN_HEIGHT = 700; 	// ��ü frame�� ����
	private final int NEW_ATTACKER_INTERVAL= 10;	// ���ο� �����ڰ� ��Ÿ���� �ֱ�
	private final int BIG_ATTACKER_INTERVAL= 10;	// ū �����ڰ� ��Ÿ���� �ֱ� (��� ���)
	private final int SPEED = 50;			// �ִϸ��̼��� �ӵ� (�и���)
	private final int STEPS = 10;			// �׸� ��ü���� �ѹ��� �����̴� �Ƚ� ��
	// ��ư ����� ���� ��Ʈ ���꿡 ���� �����
	private final int START = 1;
	private final int SUSPEND = 2;
	private final int CONT = 4;
	private final int END = 8;
	// ����� ������ �� �÷��̾� �׸� �� ����
	// �׸����� ������ res������ ����� ��
	// src ������ ��Ʈ "/"�� �νĵǹǷ� res ������ �� �ؿ� ����� ��Ʈ���� ��θ��� ��
	private final String ATTACKER_PIC = "/res/8.gif";
	private final String BIG_ATTACKER_PIC = "/res/9.gif";
	private final String PLAYER_PIC = "/res/2.gif";
	private final String MAIN_PIC = "/res/main1.jpg";
	private final String BACKGROUND_SOUND = "C:\\start.wav";
	private final String BOOM_SOUND = "C:\\boom.wav";

	JFrame frame=new JFrame();				// ��ü GUI�� ���� �����ӿ� ���� ���۷���
	int gamePanelWidth, gamePanelHeight;	// ���� ������ �̷���� ������ ũ�� 
	JPanel controlPanel=new JPanel();		// ���� ��Ʈ�Ѱ� �ð�, ����� ���÷��̰� �� �г�
	JButton start=new JButton("����");		// ���۹�ư
	JButton end=new JButton("����");			// �����ư
	JButton suspend=new JButton("�Ͻ�����");	// �Ͻ����� ��ư
	JButton cont=new JButton("���");			// ��� ��ư
	JLabel timing=new JLabel("�ð�  : 0�� 0��");// ���Ӱ�� �ð� ���÷��̸� ���� ��
	JPanel midPanel;						// �߾��� ������ �г�
	JPanel coverPanel;						// �ʱ�ȭ���� ��Ÿ�� �г�	
	GamePanel gamePanel;					// ������ �̷��� �г�
	Container container;					// ������ �̷���� �г��� pane�� ���� �����̳�
	CardLayout card;						// ������ �̷���� �гο� ȭ���� ������ ��ġ�� ���� Card ���̾�
	Timer goAnime;							// �׷��� ��ü�� �������� �����ϱ� ���� Ÿ�̸�
	Timer goClock;							// �ð豸���� ���� ���� Ÿ�̸�
	ClockListener clockListener;			// �ð踦 �����ϱ� ���� ������
	ArrayList<Shape> attackerList;			// ���ӿ� ���Ǵ� (�Ϲ�) ������ ��ü�� ��� ����Ʈ
	ArrayList<Shape> bigAttackerList;		// ���ӿ� ���Ǵ� ū ������ ��ü�� ��� ����Ʈ
	Shape player;							// Ű����� �����̴� Player ��ü
	DirectionListener keyListener;			// ȭ��ǥ �������� �����ϴ� ������
	private Clip backgroundClip;			// ���� ��� ����
	private Clip boomClip;					// �浹����
	static String playerName;				// �÷��̾� �̸�

	public static void main(String [] args) {
		playerName=JOptionPane.showInputDialog("�̸��� �Է����ּ��� :");	// Player�� �̸� �Է�
		new GameUsingTimer().go();									// ������  �ʱ�ȭ
	}

	public void go() {
		//GUI����
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// ���� ���� ��ư �� ���÷��� �󺧵��� �� �г�
		controlPanel.add(start);
		controlPanel.add(suspend);
		controlPanel.add(cont);
		controlPanel.add(end);
		controlPanel.add(timing);
		controlPanel.add(new JLabel(" Player : "));
		controlPanel.add(new JLabel(playerName));
		
		// ������ ������ ���÷��� �� �г�
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);

		// �ʱ�ȭ���� ���� �г�
		coverPanel = new CoverPanel();
		coverPanel.setBounds(0,0,WIN_WIDTH,WIN_HEIGHT);
		
		// �ʱ�ȭ��� ����ȭ���� ���̾�ȭ ��
		midPanel = new JPanel();
		card = new CardLayout();
		midPanel.setLayout(card);
		midPanel.add("1", coverPanel);
		midPanel.add("2", gamePanel);
		
		// ��ü �����ӿ� ��ġ
		frame.add(BorderLayout.CENTER, midPanel);
		frame.add(BorderLayout.SOUTH, controlPanel);
		
		// ������ �̷���� �г��� ���� ���� ���� ���
		gamePanelWidth = gamePanel.getWidth() -70;
		gamePanelHeight = gamePanel.getHeight() -130;

		//��µ� ��ü���� ���� (���)�Ͽ� attackerList�� �־� ��
		prepareAttackers();
		
		// Ű����� ������ player ��ü ����
		player = new Shape(getClass().getResource(PLAYER_PIC), B_MARGIN, gamePanelWidth, gamePanelHeight);
		
		// �ð� ���÷���, ��ü�� �������� �ڵ�ȭ �ϱ� ���� Ÿ�̸ӵ� 
		clockListener = new ClockListener();
		goClock = new Timer(1000, clockListener);			// �ð��� �ʴ����� ��Ÿ���� ���� ������
		goAnime = new Timer(SPEED, new AnimeListener());	// �׸��� �̵��� ó���ϱ� ���� ������

		// Player�� Ű���� �������� ���� ��û��
		gamePanel.addKeyListener(new DirectionListener());	// Ű���� ������ ��ġ
		gamePanel.setFocusable(false);						// �ʱ⿡�� ��Ű�� �ȵǰ� ��(�� Ű �ȸ���)

		// ��ư  �������� ��ġ
		start.addActionListener(new StartListener());
		suspend.addActionListener(new SuspendListener());
		cont.addActionListener(new ContListener());
		end.addActionListener(new EndListener());

		// ������ ���� ���� ���� ��ġ
		// ���������� �غ�
		try {
			File file = new File(BACKGROUND_SOUND);		// ���� ������� 
			backgroundClip = AudioSystem.getClip(); 
			backgroundClip.open(AudioSystem.getAudioInputStream(file));
			
			file = new File(BOOM_SOUND);				// �������� 
			boomClip = AudioSystem.getClip(); 
			boomClip.open(AudioSystem.getAudioInputStream(file));
		} catch (Exception e) { 
			System.out.println("���� ���� �ε� ����");
		} 		
		
		// ȭ���� Ȱ��ȭ
		buttonToggler(START);	// �ʱ⿡�� start��ư�� �� Ȱ��ȭ
		frame.setSize(WIN_WIDTH,WIN_HEIGHT);
		frame.setVisible(true);
	}
	 
	// ���� �Լ���

	// ��ư�� Ȱ�� ��Ȱ��ȭ�� ���� ��ƾ
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
	
	// ������ ���ۿ� ���� �����ڵ�
	private void prepareAttackers() {
		bigAttackerList = new ArrayList<Shape>();		// ū �������� ����Ʈ�� ���
		attackerList = new ArrayList<Shape>();			// ������ 3���� ����
		attackerList.add(new DiagonallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new HorizontallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
		attackerList.add(new VerticallyMovingShape(getClass().getResource(ATTACKER_PIC), S_MARGIN, STEPS, gamePanelWidth, gamePanelHeight));
	}
	
	// ������ ����� ó���ؾ� �� ����
	private void finishGame() {
		backgroundClip.stop();				// ���� ����
		goClock.stop();						// �ð� ���ÿ��� ����
		goAnime.stop();						// �׸���ü ������ ����
		gamePanel.setFocusable(false);		// ��Ŀ�� �ȵǰ� ��(�� Ű �ȸ���)
		buttonToggler(START);				// Ȱ��ȭ ��ư�� ����
	}
	
	// ���������� �������� �������� �߻���Ű�� ���ݰ�ü�� ����
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
	
	// ���� Ŭ���� ��
	
	// goAnime Ÿ�̸ӿ� ���� �ֱ������� ����� ����
	// ��ü�� ������, �浹�� ���� ����
	public class AnimeListener implements ActionListener {
		private void playBoom() {
			boomClip.loop(1);					// ����ݺ� Ƚ��
			boomClip.start();					// �浹�� ����
			try {								
				Thread.sleep(1000);				// 1000 �и���(1��) ��ŭ ���α׷� ���
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			boomClip.stop();					// ���� ����
		}
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// ���� �浹�Ͽ����� �浹�� ȿ���� ��Ÿ���� Ÿ�̸Ӹ� �ߴܽ�Ŵ
			for (Shape s : attackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					playBoom();					// ������ ����	
					finishGame();				// ���� �ߴ�
					return;
				}
			}
			for (Shape s : bigAttackerList) {
				if (s.collide(new Point(player.x, player.y))) {
					playBoom();					// ������ ����	
					finishGame();				// ���� �ߴ�
					return;
				}
			}
			// �׸� ��ü���� �̵���Ŵ
			for (Shape s : attackerList) {
				s.move();
			}
			for (Shape s : bigAttackerList) {
				s.move();
			}

			// ȭ���� ��ü �� �ٽ� �׷���. �׷���, ���⼭ repaint�� ���� �ʾƵ� �ִϸ��̼� �� ��.
			// ������ ��Ⱑ Animated GIF �� �ڵ����� repaint�� �Ҹ��� �����ν� �׷��� ������ �Ͼ�� ����
			// ���⿡ �ߺ����� �� �־ ������
			frame.repaint();								
		}
	}

	
	// ���� ��ư�� ��û��
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// card.next(midPanel);							// gamePanel �� ������ ������ ��
			card.show(midPanel, "2");							// gamePanel �� ������ ������ ��
			gamePanel.setFocusable(true);					// gamePanel�� ��Ŀ�̵� �� �ְ� ��
			gamePanel.requestFocus();						// ��Ŀ���� ������(�̰� �ݵ�� �ʿ�)

			backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);	// ���� ������� �ݺ�							// ����ݺ� Ƚ��
			backgroundClip.start();							// ���� �������

			goAnime.start();								// �׸���ü �������� ���� ����

			clockListener.reset();							// Ÿ�̸��� ���۰� �ʱ�ȭ
			timing.setText("�ð�  : 0�� 0��");	
			goClock.start();								// �ð� ���÷��� Ÿ�̸ӽ���

			prepareAttackers();								// �ʱ� ������ �غ�

			buttonToggler(SUSPEND+END);						// Ȱ��ȭ�� ��ư�� ����

		}
	}
	
	class SuspendListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.stop();		
			goAnime.stop();
			gamePanel.setFocusable(false);					// ���� �����ӿ� Ű �ȸ԰� ��
			buttonToggler(CONT+END);						// Ȱ��ȭ ��ư�� ����
		}
	}
	
	class ContListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			goClock.restart();
			goAnime.restart();
			gamePanel.setFocusable(true);					// ���� ������ Ű �԰� ��
			gamePanel.requestFocus();						// ��ü �����ֿ� ��Ŀ���ؼ� Ű �԰� ��
			buttonToggler(SUSPEND+END);						// Ȱ��ȭ ��ư�� ����
		}
	}

	// �����ư�� ���� ��û��
	class EndListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			finishGame();
		}
	}

	// ������ ����Ǵ� ���� �г�
	class GamePanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0,0,this.getWidth(), this.getHeight());		// ȭ�� �����
		
			// ���ӿ� ���Ǵ� �׷��� ��ü�� ��� �׷���
			for (Shape s : attackerList) {
				s.draw(g, this);
			}
			for (Shape s : bigAttackerList) {
				s.draw(g, this);
			}
			player.draw(g, this);	
		}
	}
	
	// �ʱ�ȭ���� ��Ÿ���� �г�
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			Image image = new ImageIcon(getClass().getResource(MAIN_PIC)).getImage(); 
			g.drawImage(image,0,0,this);
		}
	}
	
	// �ð� ���÷��̸� ���� ����ϴ� �ð�
	private class ClockListener implements ActionListener {
		int times = 0;
		public void actionPerformed (ActionEvent event) {		
			times++;						
			timing.setText("�ð�  : "+times/60+"�� "+times%60+"��");

			// �ð��� �����ð� ������ ���ο� ��⸦ ������Ŵ
			if (times % NEW_ATTACKER_INTERVAL == 0)
				attackerList.add(getRandomAttacker(ATTACKER_PIC, S_MARGIN, STEPS));

			// �ð��� �����ð� ������ bigAttacker ����/�Ҹ� ��Ŵ
			if (times % BIG_ATTACKER_INTERVAL == 0) {
				if (bigAttackerList.isEmpty())			// ���� Ȱ�� ���� �ƴϸ� �ϳ� �߰�	
					bigAttackerList.add(getRandomAttacker(BIG_ATTACKER_PIC, B_MARGIN, STEPS));
				else									// ���� Ȱ�� ���̸� ����Ʈ ����
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
	
	// Ű���� �������� ��û�ϴ� ��û��
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