/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import stockexchange.StockExchange;
import stockexchange.model.AIeasy;
import stockexchange.model.AIhard;
import stockexchange.model.AImedium;
import stockexchange.model.Model;
import stockexchange.model.Player;

public class StartUpContainerPanel extends JPanel implements ActionListener {

	private final int DEF_NUM_PLAYERS = 2;
	private String[] names;
	private final String[] defaultNames = new String[]{"Pleyer 1's name",
		"Pleyer 2's name", "Pleyer 3's name", "Pleyer 4's name"};
	private Font titleFont = new Font("Arial", Font.BOLD, 40);
	private Font textFont = new Font("Arial", Font.PLAIN, 25);
	private JComboBox cbNumPlayers;
	private RowPanel[] rowPs;
	private int w;
	private int h;

	public StartUpContainerPanel(Player[] playersGiven) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		w = screenSize.width;
		h = screenSize.height;
		setLayout(new BorderLayout());
		setBackground(MainFrame.bgColor);

		titleFont = titleFont.deriveFont(w / 40f);
		textFont = textFont.deriveFont(w / 70f);
		names = new String[Model.MAX_NR_PLAYERS];

		//Title of Frame
		JPanel titlePanel = new JPanel();
		JLabel titleText = new JLabel("Set the name of the Pleyers", SwingConstants.CENTER);
		titleText.setFont(titleFont);
		titlePanel.add(titleText);
		add(titlePanel, BorderLayout.NORTH);

		//The Center
		JPanel theRest = new JPanel(new GridLayout(0, 1));
		JPanel setNumberPanel = new JPanel(new GridBagLayout());

		//JPanel setNumberPanel = new JPanel();
		JLabel lbAI = new JLabel("AI/Human", JLabel.CENTER);
		JLabel lbSetName = new JLabel("Names of players");
		JLabel lbSetNumber = new JLabel("Nr. of players", JLabel.RIGHT);

		Integer[] adf = new Integer[]{1, 2, 3, 4};
		cbNumPlayers = new JComboBox(adf);
		cbNumPlayers.setUI(ColorArrowUI.createUI(cbNumPlayers));
		((JLabel) cbNumPlayers.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
		cbNumPlayers.setFont(textFont);
		cbNumPlayers.addActionListener(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 3;
		setNumberPanel.add(lbAI, gbc);
		gbc.weightx = 13;
		setNumberPanel.add(lbSetName, gbc);
		gbc.weightx = 20;
		setNumberPanel.add(lbSetNumber, gbc);
		gbc.weightx = 1;
		setNumberPanel.add(cbNumPlayers, gbc);
		theRest.add(setNumberPanel);

		add(theRest, BorderLayout.CENTER);
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton btStart = new JButton("      Start Game      ");
		//btStart.setOpaque(false);
		//btStart.setContentAreaFilled(false);
		btStart.setBorder(BorderFactory.createEtchedBorder());
		btStart.addActionListener(this);
		btStart.setFont(textFont);
		bottom.add(btStart);
		add(bottom, BorderLayout.SOUTH);
		MainFrame.setAllForeground(this, Color.LIGHT_GRAY);
		rowPs = new RowPanel[Model.MAX_NR_PLAYERS];
		for (int i = 0; i < Model.MAX_NR_PLAYERS; i++) {
			if (i >= playersGiven.length)
				rowPs[i] = new RowPanel(i, w);
			else
				rowPs[i] = new RowPanel(i, w, playersGiven[i].getName());
			theRest.add(rowPs[i]);
		}
		initialize(playersGiven);
		int width = w / 3;
		int height = h / 3;
		setBounds(w / 2 - width / 2, 0, width, height);
		//setSize(width, height);
		setVisible(true);
		MainFrame.setAllBackground(this, MainFrame.bgColor);
		btStart.setOpaque(true);
		btStart.setBackground(MainFrame.bgColor.brighter().brighter());

		//System.out.println("startup size "+getSize().toString());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println(e.getSource());
		if (e.getSource() instanceof JButton)
			startGame();
		else if (e.getSource() instanceof JComboBox) {
			int players = ((JComboBox) e.getSource()).getSelectedIndex() + 1;
			for (int i = 0; i < Model.MAX_NR_PLAYERS; i++) {
				if (i < players)
					rowPs[i].setActive(true);
				else
					rowPs[i].setActive(false);
			}
		}
	}

//	private void printComponents(Container container) {
//		for (int i = 0; i < container.getComponentCount(); i++) {
//			System.out.println(container.getComponent(i));
//			if (container.getComponent(i) instanceof Container)
//				printComponents((Container) container.getComponent(i));
//		}
//	}

	private void initialize(Player[] players) {
		int numPlayers = (players.length == 0) ? DEF_NUM_PLAYERS : players.length;
		if (players.length == 0)
			names = Arrays.copyOf(defaultNames, Model.MAX_NR_PLAYERS);
		else
			for (int i = 0; i < players.length; i++)
				names[i] = players[i].getName();
		//combobox: number of players
		cbNumPlayers.setSelectedItem(numPlayers);
		//activation of rows
		for (int i = 0; i < Model.MAX_NR_PLAYERS; i++) {
			if (i > numPlayers - 1)
				rowPs[i].setActive(false);
			if (i < players.length)
				if (players[i] instanceof AIeasy)
					rowPs[i].setAI(0);
				else if (players[i] instanceof AImedium)
					rowPs[i].setAI(1);
				else if (players[i] instanceof AIhard)
					rowPs[i].setAI(2);
		}
	}

	protected void startGame() {
		String[][] names = new String[cbNumPlayers.getSelectedIndex() + 1][2];
		for (int i = 0; i < names.length; i++) {
			names[i] = rowPs[i].getNameAndType();
		}
//		System.out.println(Arrays.deepToString(names));
		StockExchange.switchToGame(names, new int[]{w / 7 * 5, Math.round(w * 0.45f), w, h});
	}

	public class RowPanel extends JPanel implements ActionListener, FocusListener {

		JTextField tf;
		JCheckBox chb;
		JComboBox cb;
		int rowNumber;

		public RowPanel(int i, int w, String... name) {
			rowNumber = i;
			//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			setLayout(new FlowLayout(FlowLayout.LEFT, w / 40, 0));
			chb = new JCheckBox();
			if (name.length == 0)
				tf = new JTextField(defaultNames[i], 30);
			else
				tf = new JTextField(name[0]);
			cb = new JComboBox(new String[]{"Easy", "Medium", "Hard"});
			tf.setFont(textFont);
			tf.setBorder(BorderFactory.createEmptyBorder());
			cb.setFont(textFont);
			//cb.setForeground(MainFrame.PLAYER_COLORS[i]);
			chb.addActionListener(this);
			cb.addActionListener(this);
			tf.addActionListener(this);
			tf.addFocusListener(this);
			add(chb);
			add(tf);
			add(cb);
			cb.setVisible(false);
			//cb.setEnabled(false);
			cb.setUI(ColorArrowUI.createUI(cbNumPlayers));
			cb.setRenderer(new DefaultListCellRenderer() {
				public void paint(Graphics g) {
					if (cb.isEnabled()) {
						setBackground(MainFrame.bgColor);
						setForeground(MainFrame.PLAYER_COLORS[i]);
					} else {
						setBackground(MainFrame.bgColor);
						setForeground(MainFrame.bgColor.brighter());
					}
					super.paint(g);
				}
			});
			MainFrame.setAllBackground(this, MainFrame.bgColor);
		}

		protected void setName(int i, String name) {
			names[i] = name;
		}

		@Override
		public void focusGained(FocusEvent e) {

		}

		@Override
		public void focusLost(FocusEvent e) {
			if (tf.isEnabled() && tf.getText().equals(""))
				tf.setText("Anonymus");
		}

//		@Override
//		public Dimension getPreferredSize() {
//			return new Dimension(950, 550);
//		}
		protected void setAI(int type) {
			chb.setSelected(true);
			tf.setVisible(false);
			cb.setVisible(true);
			cb.setSelectedIndex(type);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println(e.getSource());
			if (e.getSource() instanceof JCheckBox) {
				tf.setVisible(!chb.getModel().isSelected());
				cb.setVisible(chb.getModel().isSelected());
			}
		}

		protected String[] getNameAndType() {
			if (tf.isVisible())
				return new String[]{tf.getText(), "human"};
			else
				return new String[]{(String) cb.getSelectedItem(), "ai"};
		}

		protected void setActive(boolean active) {
			tf.setEditable(active);
			cb.setEnabled(active);
			chb.setEnabled(active);
			if (active) {
				tf.setForeground(MainFrame.PLAYER_COLORS[rowNumber]);
				tf.setCaretColor(MainFrame.PLAYER_COLORS[rowNumber]);
			} else {
				tf.setForeground(MainFrame.bgColor.brighter());
			}

		}
	}

}

class ColorArrowUI extends BasicComboBoxUI {

	public static ComboBoxUI createUI(JComponent c) {
		return new ColorArrowUI();
	}
	
	@Override
	protected JButton createArrowButton() {
		JButton button = null;
		try {
			button = new JButton(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("./images/arrow2.png"))));
		} catch (IOException ex) {
			//Logger.getLogger(ColorArrowUI.class.getName()).log(Level.SEVERE, null, ex);
		}
		button.setBorderPainted(false);
		return button;
	}
}
