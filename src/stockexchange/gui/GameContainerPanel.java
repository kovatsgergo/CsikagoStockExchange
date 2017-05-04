/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import stockexchange.StockExchange;
import stockexchange.GuiControlInterface;

public class GameContainerPanel extends JPanel {

//	double ratio = 1.5;
//	private Dimension lastSize = new Dimension();
//	private ComponentListener caFixRatio;
//	private boolean allowResizeListener;
	private final JToggleButton tglHints;
	private JPanel top;
	private JPanel bottom;
	//private GamePanel gamePanel;
	private String[] players;

	public GameContainerPanel(String[][] players, int[] dimensions, GamePanel gamePanel, GuiControlInterface theGame) {
		//System.out.println("min: " + getMinimumSize().toString());
		this.players = new String[players.length];
		for (int i = 0; i < players.length; i++) {
			this.players[i] = players[i][0];
		}
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//panels
		//this.gamePanel = gamePanel;
		top = new JPanel(new GridLayout(1, players.length * 2 - 1));
		bottom = new JPanel(new GridLayout(1, 5));

		// Hints toggle
		tglHints = new JToggleButton("hints off");
		tglHints.setFocusable(false);//to have keyboard focus on gamePanel
		tglHints.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				/////////////LISTENER KIKAPCS, BEKAPCS
				int w = gamePanel.getWidth();
				int h = gamePanel.getHeight();
//				int topAndBottom = getHeight() - h;

				if (e.getStateChange() == ItemEvent.SELECTED) {
					tglHints.setText("HINTS ON");
					double hintRatio = 1.5 - (1.5 - 1.35) / 4 * (players.length);
					SwingUtilities.getWindowAncestor(gamePanel).setSize(getWidth(), (int) (getWidth() / hintRatio));
					gamePanel.setHintsOnOff(true);
					//gamePanel.setSize(getWidth(), (int) (getWidth() / 1.35));
					System.out.println("size: " + gamePanel.getSize().toString());
					//GameFrame.this.addCompList(false);
					//GameFrame.this.removeComponentListener(caFixRatio);
					//if (getWidth()/1.3>getMaximumSize().height)
					//setBounds(getX(), getY(), getMaximumSize().width, getMaximumSize().height);
					//setBounds(getX(), getY(), getWidth(), (int) (getWidth() / 1.4) + topAndBottom);
				} else {
					tglHints.setText("hints off");
					SwingUtilities.getWindowAncestor(gamePanel).setSize(getWidth(), (int) (getWidth() / 1.5));
					gamePanel.setHintsOnOff(false);
					//gamePanel.setSize(getWidth(), (int) (getWidth() / 1.7));
					System.out.println("size: " + gamePanel.getSize().toString());
					//GameFrame.this.removeComponentListener(caFixRatio);
					//setBounds(getX(), getY(), getWidth(), (int) (getWidth() / 1.7) + topAndBottom);
				}
				//allowResizeListener = false;
				//GameFrame.this.addComponentListener(caFixRatio);
			}
		});
//Fixed frame ratio fail
//<editor-fold defaultstate="collapsed" desc="comment">
//		caFixRatio = new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent e) {
////				System.out.println("source\t\t" + e.getSource());
//				JFrame frame = (JFrame) e.getComponent();
//				//System.out.println(this);
//				int w = gamePanel.getWidth();
//				int h = gamePanel.getHeight();
////				System.out.println("frame.size" + frame.getHeight() + "\tgamePanel.height" + gamePanel.getHeight()
////								+ "\ttop " + top.getHeight() + "\tbottom " + bottom.getHeight());
//				int topAndBottom = getHeight() - h;
//				//System.out.println("before: w: " + w + "   h:" + h + "\tratio: " + w / (h - topAndBottom) + "\tgoal: " + ratio);
//				Point p = MouseInfo.getPointerInfo().getLocation();
//				int diffX = Math.min(Math.abs(frame.getX() - p.x), Math.abs(frame.getX() + frame.getWidth() - p.x));
//				int diffY = Math.min(Math.abs(frame.getY() - p.y), Math.abs(frame.getY() + frame.getHeight() - p.y));
//				ratio = (tglHints.isSelected()) ? 1.4 : 1.7;
//				//if (!tglHints.isSelected()) {
//				if (allowResizeListener && diffX < diffY) {//if (w / (h - topAndBottom) > ratio + 0.01) {
//					frame.setSize((int) w, (int) Math.round(w / ratio) + topAndBottom);
//					gamePanel.setSize((int) w, (int) Math.round(w / ratio));
//				}
//				if (allowResizeListener && diffY < diffX) {// else if (w / (h - topAndBottom) < ratio - 0.01) {
//					frame.setSize((int) Math.round(h * ratio), (int) h + topAndBottom);
//					gamePanel.setSize((int) Math.round(h * ratio), (int) h);
//				}
//				//}
////				w = getWidth();
////				h = getHeight();
////				System.out.println("after: w: " + w + "   h:" + h + "\tratio: " + w / (h - topAndBottom) + "\tgoal: " + ratio);
//				allowResizeListener = true;
//			}
//		};
//		addComponentListener(caFixRatio);
//</editor-fold>
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
		StringBuilder rules = new StringBuilder();
		try {
			java.util.Scanner s = new Scanner(textURL.openStream());
			while (s.hasNextLine()) {
				rules.append(s.nextLine());
				rules.append("\n");
			}
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			rules.append("Error while reading rules");
		}

		JTextArea textArea = new JTextArea(40, 42);
		textArea.setText(rules.toString());
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		final JFrame rulesFrame = new JFrame("Game Rules");
		rulesFrame.add(scrollPane);
		rulesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		rulesFrame.pack();//nem tudok jobbat
		btRules.addActionListener((ActionEvent e) -> {
			if (!rulesFrame.isVisible()) {
				rulesFrame.setVisible(true);
				scrollPane.getViewport().setViewPosition(new Point(0, 0));
			} else
				rulesFrame.dispose();
		});

		// Restart button
		JButton btRestart = new JButton("Restart");
		btRestart.setFocusable(false);//to have keyboard focus on gamePanel
		btRestart.addActionListener((ActionEvent e) -> {
			JButton temp = (JButton) e.getSource();
			StockExchange.switchToSetup();
		});
		// Add Player names to the top
		setTopNames(this.players);
		// Add components to panel
		bottom.add(tglHints, 0);
		bottom.add(new JLabel(), 1);
		bottom.add(btRules, 2);
		bottom.add(new JLabel(), 3);
		bottom.add(btRestart, 4);

		gamePanel.setFocusable(true);
		add(top);//, BorderLayout.NORTH);
		add(gamePanel);//, BorderLayout.CENTER);
		add(bottom);//, BorderLayout.SOUTH);
		gamePanel.requestFocusInWindow();
	}

	void setTopNames(String[] players) {
		top.removeAll();
		String[] names = players;//gamePanel.model.getAllNames();
		for (int i = 0; i < names.length; i++) {
			top.add(new JLabel(names[i], SwingConstants.CENTER), i * 2);
			if (i < names.length - 1)
				top.add(new JLabel("vs", SwingConstants.CENTER), i * 2 + 1);
		}
	}

}
