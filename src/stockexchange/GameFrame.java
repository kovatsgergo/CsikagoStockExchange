/*Gergo Kovats*/
package stockexchange;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

public class GameFrame extends JFrame {
	
	double ratio;
	Dimension lastSize = new Dimension();
	final JToggleButton tglHints;
	JPanel top;
	JPanel bottom;
	
	public GameFrame(Player[] players, int[] dimensions, GamePanel gamePanel, GameInterface theGame) throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ratio = dimensions[0] / (double) dimensions[1];
		System.out.println("min: " + getMinimumSize().toString());
		//System.out.println("dimensions: "+Arrays.toString(dimensions));
		//setLayout(new BorderLayout());
		//setBounds(0, 0, dimensions[0], dimensions[1]);

		//panels
		top = new JPanel(new GridLayout(1, players.length * 2 - 1));
		bottom = new JPanel(new GridLayout(1, 5));

		// Hints toggle
		tglHints = new JToggleButton("hints off");
		
		tglHints.setFocusable(false);//to have keyboard focus on gamePanel

		tglHints.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				//JToggleButton temp = (JToggleButton) e.getSource();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					tglHints.setText("HINTS ON");
					gamePanel.setHintsOnOff(true);
					//gamePanel.setSize(getWidth(), (int) (getWidth() / 1.3));
					setBounds(getX(), getY(), getWidth(), (int) (getWidth() / 1.3));
				} else {
					tglHints.setText("hints off");
					gamePanel.setHintsOnOff(false);
					//gamePanel.setSize(getWidth(), (int) (getWidth() / 1.5));
					setBounds(getX(), getY(), getWidth(), (int) (getWidth() / 1.5));
				}
			}
		}
		);

//		//fix aspect ratio
//		addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent e) {
//				System.out.println("source\t\t"+e.getSource());
//				System.out.println("component\t"+e.getComponent());
//				JFrame frame = (JFrame) e.getComponent();
//				System.out.println(this);
//				int w = gamePanel.getWidth();
//				int h = gamePanel.getHeight();
//				System.out.println("frame.size" + frame.getHeight() + "\tgamePanel.height" + gamePanel.getHeight()
//								+ "\ttop " + top.getHeight() + "\tbottom " + bottom.getHeight());
//				int topAndBottom = getHeight() - h;
//				//System.out.println("before: w: " + w + "   h:" + h + "\tratio: " + w / (h - topAndBottom) + "\tgoal: " + ratio);
//				Point p = MouseInfo.getPointerInfo().getLocation();
//				int diffX = Math.min(Math.abs(frame.getX() - p.x), Math.abs(frame.getX() + frame.getWidth() - p.x));
//				int diffY = Math.min(Math.abs(frame.getY() - p.y), Math.abs(frame.getY() + frame.getHeight() - p.y));
//				//if (diffX < diffY)
//				//frame.setSize(p.x, (int) Math.round(p.y / ratio) + topAndBottom);
//				//else if (diffY<diffX)
//				//frame.setSize((int) Math.round(p.y * ratio), (int) p.y+topAndBottom);
//				ratio = (tglHints.isSelected()) ? 1.5 : 1.7;
//				//if (!tglHints.isSelected()) {
//				if (diffX < diffY) {//if (w / (h - topAndBottom) > ratio + 0.01) {
//					frame.setSize((int) w, (int) Math.round(w / ratio) + topAndBottom);
//					gamePanel.setSize((int) w, (int) Math.round(w / ratio));
//					//frame.setSize((int) w - 1, (int) h);
//					//frame.setBounds(frame.getX(), frame.getY(), (int) w, (int) Math.round(w / ratio) + topAndBottom);
//					//frame.setPreferredSize(new Dimension((int) w, (int) Math.round(w / ratio)));
//				}
//				if (diffY < diffX) {// else if (w / (h - topAndBottom) < ratio - 0.01) {
//					frame.setSize((int) Math.round(h * ratio), (int) h + topAndBottom);
//					gamePanel.setSize((int) Math.round(h * ratio), (int) h);
//					//frame.setSize((int) w, (int) h - 1);
//					//frame.setBounds(frame.getX(), frame.getY(), (int) Math.round(h * ratio), (int) h + topAndBottom);
//					//frame.setPreferredSize(new Dimension((int) Math.round(h * ratio), (int) h));
//					//frame.pack();
//				}
//				//}
////				w = getWidth();
////				h = getHeight();
////				System.out.println("after: w: " + w + "   h:" + h + "\tratio: " + w / (h - topAndBottom) + "\tgoal: " + ratio);
//			}
//		});

		//Rules button
		final JButton btRules = new JButton("Rules");
		
		btRules.setFocusable(false);//to have keyboard focus on gamePanel

		//generate Rules text
		//not imported to .jar
//<editor-fold defaultstate="collapsed" desc="comment">
/*
//		File rulesFile = new File("./files/RuleBook.txt");
//		FileReader fileReader = new FileReader(rulesFile);
//		char[] chB = new char[(int) rulesFile.length()];
//		fileReader.read(chB);
//		final String rulesString = String.valueOf(chB);
		 */
//</editor-fold>
		URL textURL = gamePanel.getClass().getResource("/images/RuleBook.txt");//imported to .jar
		java.util.Scanner s = new Scanner(textURL.openStream());
		StringBuilder rules = new StringBuilder();
		while (s.hasNextLine()) {
			rules.append(s.nextLine());
			rules.append("\n");
		}
		
		JTextArea textArea = new JTextArea(40, 42);
		textArea.setText(rules.toString());
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		final JFrame rulesFrame = new JFrame("Game Rules");
		rulesFrame.add(scrollPane);
		rulesFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		rulesFrame.pack();//nem tudok jobbat
		btRules.addActionListener((ActionEvent e) -> {
			if (!rulesFrame.isVisible())
				rulesFrame.setVisible(true);
			else
				rulesFrame.dispose();
		});

		// Restart button
		JButton btRestart = new JButton("Restart");
		
		btRestart.setFocusable(false);//to have keyboard focus on gamePanel

		btRestart.addActionListener((ActionEvent e) -> {
			JButton temp = (JButton) e.getSource();
			//String[] tempStrings = players;
			StockExchange.createAndShowStartupGUI(players);
			gamePanel.setVisible(false);
			theGame.setGameOver(true);
			setVisible(false);
			dispose();
		});
		// Add Player names to the top
		for (int i = 0; i < players.length; i++) {
			top.add(new JLabel(players[i].getName(), SwingConstants.CENTER), i * 2);
			if (i < players.length - 1) {
				top.add(new JLabel("vs", SwingConstants.CENTER), i * 2 + 1);
			}
		}
		// Add components to panel
		bottom.add(tglHints, 0);
		bottom.add(new JLabel(), 1);
		bottom.add(btRules, 2);
		bottom.add(new JLabel(), 3);
		bottom.add(btRestart, 4);
		
		gamePanel.setFocusable(true);
		add(top, BorderLayout.NORTH);
		add(gamePanel, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		//pack();
		gamePanel.requestFocusInWindow();
		setResizable(false);
		setBounds((dimensions[2] - dimensions[0]) / 2, 0, dimensions[0], dimensions[1]);
		setMaximumSize(new Dimension(dimensions[2], dimensions[3]));
		setMinimumSize(new Dimension(dimensions[0]/2, dimensions[1]/2));
	}
	
	public JFrame returnThis() {
		return this;
	}
}
