import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OthelloGame extends JPanel {
	public enum Direction{NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST};
	public enum StoneColor{EMPTY, BLACK, WHITE};
	private StoneColor selectedColor = null;
	private JLabel whiteCount, blackCount, turnLabel;
	private JPanel labelPanel = new JPanel();	// ���� ��
	private JPanel buttonPanel = new JPanel();	// ���� �г�
	private JPanel panel, countSet, turnLabelPanel;
	private JButton [][]button;					// ���� �迭
	private StoneColor [][]stoneInform = null;  // �� ��ư�� � ���� ���� ������ �����ϴ� �迭
	
	private JButton help;						// Help : ���ӹ������  ��ư
	private JButton ranking;					// Ranking	: ���� ���� ��ư
	private JButton quit;						// Quit : ������ ��ư
	private JButton winnerCheck;				// ��ŷ��� ��ư
	private final int ROW, COLUMN;				// ���� �࿭�� ��
	private int rowCenter, columnCenter; 				
	private ImageIcon whiteIcon, blackIcon;		// �� ���� ���� �� �̹���.
	private Font font;
	private ActionListener listener;
	
	public OthelloGame()	
	{
		// �ʱ�ȭ.		
		ROW=8;		COLUMN=8;		// ��,���� ���� 8�� �ʱ�ȭ. 	
		
		panel = new JPanel();
		countSet = new JPanel();
		turnLabelPanel = new JPanel();
		button = new JButton[ROW][COLUMN];					// 8*8 ��ư ��ü�� ��Ī�ϴ� 8*8������ �迭 ����.
		buttonPanel.setLayout(new GridLayout(ROW,COLUMN));	// ���� ���̾ƿ�(grid layout)�� 9*9�ǰ��ڿ� �����̳��� ������Ʈ ��ġ
		help = new JButton("          Help         ");
		winnerCheck = new JButton("Winner Check");
		ranking = new JButton(" Ranking View ");
		quit = new JButton("          Quit          ");
		
		
		whiteIcon = new ImageIcon("white_diske.jpg");		// �̹��� ������ ��ü����
		blackIcon = new ImageIcon("black_diske.jpg");
		whiteCount = new JLabel("2", whiteIcon, SwingConstants.CENTER);
		whiteCount.setHorizontalTextPosition(SwingConstants.RIGHT);
		blackCount = new JLabel("2", blackIcon, SwingConstants.CENTER);
		blackCount.setHorizontalTextPosition(SwingConstants.RIGHT);
		turnLabel = new JLabel("Turn ", whiteIcon, SwingConstants.CENTER);
		turnLabel.setHorizontalTextPosition(SwingConstants.LEFT);		
		//��Ʈ����
		font = new Font("Showcard Gothic", Font.PLAIN, 25);
		turnLabel.setFont(font);
		stoneInform = new StoneColor[ROW][COLUMN];
		selectedColor=StoneColor.WHITE;
		
		listener = new ButtonListener();
		for(int i=0 ; i<ROW ; i++) {	
			for(int j=0 ; j<COLUMN ; j++) {				
				button[i][j] = new JButton();	// ��ư��ü ����.
				button[i][j].setBorder(BorderFactory.createLoweredBevelBorder());
				buttonPanel.add(button[i][j]);	// ��ư �߰� 
				button[i][j].addActionListener(listener);	// ��ư���� �������̽� ����
				
				// ���� ������ EMPTY�� �ʱ�ȭ
				stoneInform[i][j]=StoneColor.EMPTY; 
			}
		}
		// ��ư �� �� �߰�
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
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));	// ������ ũ�⸦ ���� ���� ���� ���� ÷��.
		labelPanel.add(winnerCheck);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		labelPanel.add(ranking);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		labelPanel.add(quit);
		labelPanel.add(Box.createRigidArea(new Dimension(0,10)));
		panel.add(buttonPanel);
		
		// �� ��ư���� �������̽� �߰�.
		//help.addActionListener(new HelpListener());		
		//ranking.addActionListener(new RankingListener());
		quit.addActionListener(new QuitListener());
		//winnerCheck.addActionListener(new WinnerCheckListener());
		
		// ������ ����.
		labelPanel.setPreferredSize(new Dimension(180,270));
		buttonPanel.setPreferredSize(new Dimension(300,300));		
		
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));	// �ڽ� ���̾ƿ�
		add(labelPanel);
		add(panel);
		
		initializeGame();		// ó�� ���ӽ��� �� ȣ���Լ�.
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
		}
	}
	// ���� �򵹰� �������� ���� ī�����ؼ� ���� ���ڸ� ��Ÿ���� ���̺� ������Ʈ �Ѵ�
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
						// ���� ���� ���� �ѱ��
						turnLabel.setIcon(selectedColor==StoneColor.BLACK ? whiteIcon : blackIcon);
						selectedColor=(selectedColor==StoneColor.BLACK) ? StoneColor.WHITE : StoneColor.BLACK;
					}
				}
			}
		}
	}
	private class QuitListener implements ActionListener 	// ���� ��ư(��� �����Ӱ� â��  ������ ������.)
	{                
		public void actionPerformed(ActionEvent event) 
		{
			System.exit(0);	// ���α׷� ����
		}
	}
}
