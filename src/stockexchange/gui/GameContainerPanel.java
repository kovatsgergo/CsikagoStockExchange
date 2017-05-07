/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import stockexchange.GuiControlInterface;
import stockexchange.StockExchange;

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

	public GameContainerPanel(String[][] players, int[] dimensions, GamePanel gamePanel, GuiControlInterface iGuiControl) {
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
					//tglHints.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
					double hintRatio = 1.5 - (1.5 - 1.35) / 4 * (players.length);
					SwingUtilities.getWindowAncestor(gamePanel).setSize(getWidth(), (int) (getWidth() / hintRatio));
					SwingUtilities.getWindowAncestor(gamePanel).setSize(getWidth(), (int) (getWidth() / 1.5 + players.length * 30));
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
					//tglHints.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
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
//		URL textURL = gamePanel.getClass().getResource("/images/RuleBook.txt");//imported to .jar
//		StringBuilder rules = new StringBuilder();
//		try {
//			java.util.Scanner s = new Scanner(textURL.openStream());
//			while (s.hasNextLine()) {
//				rules.append(s.nextLine());
//				rules.append("\n");
//			}
//		} catch (IOException e) {
//			System.out.println(e.getStackTrace());
//			rules.append("Error while reading rules");
//		}
		URL htmlURL = gamePanel.getClass().getResource("/images/RuleBook.html");//imported to .jar
		JEditorPane jepRules = null;
		try {
			jepRules = new JEditorPane(htmlURL);
		} catch (IOException e) {
			System.out.println("RULES FILE NOT FOUND");
		}
//		JTextArea textArea = new JTextArea(40, 42);
//		textArea.setText(rules.toString());
//		textArea.setEditable(false);
		//JScrollPane scrollPane = new JScrollPane(textArea);
		jepRules.setEditable(false);
		jepRules.setPreferredSize(new Dimension(500,
						Toolkit.getDefaultToolkit().getScreenSize().height - 200));
		JScrollPane scrollPane = new JScrollPane(jepRules);
		final JFrame rulesFrame = new JFrame("Game Rules");
		rulesFrame.setLayout(new FlowLayout());
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
			if (iGuiControl != null)
				iGuiControl.pause(false);
			if (JOptionPane.showConfirmDialog(this,
							"Are you sure you want to quit this game?",
							"Confirm Quit", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE) == 0)
				StockExchange.switchToSetup();
			else if (iGuiControl != null)
				iGuiControl.unPause();

		});
		// Add Player names to the top
		//setTopNames(this.players);
		// Add components to panel
		bottom.add(tglHints, 0);
		bottom.add(new JLabel(), 1);
		bottom.add(btRules, 2);
		bottom.add(new JLabel(), 3);
		bottom.add(btRestart, 4);

		top.setBackground(new Color(10, 30, 80));
		gamePanel.setFocusable(true);
		add(top);//, BorderLayout.NORTH);
		add(gamePanel);//, BorderLayout.CENTER);
		add(bottom);//, BorderLayout.SOUTH);
		gamePanel.requestFocusInWindow();
		bottom.setBackground(MainFrame.bgColor.brighter());
		setAllButtons(bottom);
		MainFrame.setAllForeground(bottom, Color.LIGHT_GRAY);
		//MainFrame.setAllBackground(bottom, MainFrame.bgColor.brighter());
	}

	void setTopNames(String[] players) {
		top.removeAll();
		String[] names = players;//gamePanel.model.getAllNames();
		int max = names[0].length();
		for (int i = 1; i < names.length; i++) {
			max = (max < names[i].length()) ? names[i].length() : max;
		}
		int fontSize = Math.max(13, getPreferredSize().width * 2 / (names.length * 2 + 1) / max * 2);
		fontSize = Math.min(fontSize, 20);
		System.out.println("fontSize " + fontSize);
		for (int i = 0; i < names.length; i++) {
			JLabel temp = new JLabel(names[i], SwingConstants.CENTER);
			//System.out.println("top "+getPreferredSize().width);
			temp.setFont(new Font("LucidaGrande", Font.PLAIN, fontSize));
			temp.setForeground(MainFrame.PLAYER_COLORS[i]);
			top.add(temp/*, i * 2*/);
			if (i < names.length - 1) {
				temp = new JLabel("vs", SwingConstants.CENTER);
				temp.setForeground(Color.LIGHT_GRAY);
				top.add(temp/*, i * 2 + 1*/);
			}
		}
	}

	private void setAllButtons(JPanel panel) {
		for (int i = 0; i < panel.getComponentCount(); i++) {
			//System.out.println(panel.getComponent(i));
			if (panel.getComponent(i) instanceof AbstractButton) {
				((AbstractButton) panel.getComponent(i)).setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
				((AbstractButton) panel.getComponent(i)).setOpaque(true);
				panel.getComponent(i).setBackground(MainFrame.bgColor.brighter().brighter());
			}

		}
	}

}
