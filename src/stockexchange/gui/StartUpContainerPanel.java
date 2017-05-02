/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import stockexchange.Control;
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
	private Font titleFont = new Font("Arial", Font.BOLD, 25);
	private Font textFont = new Font("Arial", Font.PLAIN, 25);
	private JComboBox cbNumPlayers;
	private RowPanel[] rowPs;
	private int w;
	private int h;

	public StartUpContainerPanel(Player[] playersGiven) {
		System.out.println("namesgiven " + Arrays.toString(playersGiven));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		w = screenSize.width;
		h = screenSize.height;
		setLayout(new BorderLayout());

		titleFont = titleFont.deriveFont(w / 40f);
		textFont = textFont.deriveFont(w / 70f);
		names = new String[Model.MAX_NR_PLAYERS];

		//setLayout(new GridLayout(4,1));
		//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//setLayout(new FlowLayout());
		//Title of Frame
		JPanel titlePanel = new JPanel();
		JLabel titleText = new JLabel("Set the name of the Pleyers", SwingConstants.CENTER);
		titleText.setFont(this.getFont());
		titlePanel.add(titleText);
		titlePanel.setFont(titleFont);
		add(titlePanel, BorderLayout.NORTH);

		//The Center
		JPanel theRest = new JPanel(new GridLayout(0, 1));
		JPanel setNumberPanel = new JPanel(new GridLayout(1, 0));
		//JPanel setNumberPanel = new JPanel();
		JLabel lbAI = new JLabel("AI/Human");
		JLabel lbSetName = new JLabel("Names of players");
		JLabel lbSetNumber = new JLabel("Nr. of players", JLabel.RIGHT);

		Integer[] adf = new Integer[]{1, 2, 3, 4};
		cbNumPlayers = new JComboBox(adf);
		cbNumPlayers.addActionListener(this);

		setNumberPanel.add(lbAI);
		setNumberPanel.add(lbSetName);
		setNumberPanel.add(lbSetNumber);
		setNumberPanel.add(cbNumPlayers);
		theRest.add(setNumberPanel);
		cbNumPlayers.setFont(textFont);
		rowPs = new RowPanel[Model.MAX_NR_PLAYERS];
		for (int i = 0; i < Model.MAX_NR_PLAYERS; i++) {
			if (i >= playersGiven.length)
				rowPs[i] = new RowPanel(i, w);
			else
				rowPs[i] = new RowPanel(i, w, playersGiven[i].getName());
			theRest.add(rowPs[i]);
		}
		initialize(playersGiven);
		add(theRest, BorderLayout.CENTER);
		JButton btStart = new JButton("Start Game");
		btStart.addActionListener(this);
		add(btStart, BorderLayout.SOUTH);
		int width = w / 3;
		int height = h / 3;
		setBounds(w / 2 - width / 2, 0, width, height);
		//setSize(width, height);
		setVisible(true);
		System.out.println("startup size "+getSize().toString());
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

	private void startGame() {
		String[][] names = new String[cbNumPlayers.getSelectedIndex() + 1][2];
		for (int i = 0; i < names.length; i++) {
			names[i] = rowPs[i].getNameAndType();
		}
//		System.out.println(Arrays.deepToString(names));
			Control.switchToGame(names, new int[]{w / 7 * 5, Math.round(w * 0.45f), w, h});
	}

	public class RowPanel extends JPanel implements ActionListener {

		JTextField tf;
		JCheckBox chb;
		JComboBox cb;
		int rowNumber;

		public RowPanel(int i, int w, String... name) {
			//i = rowNumber;
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			chb = new JCheckBox();
			if (name.length == 0)
				tf = new JTextField(defaultNames[i], 10);
			else
				tf = new JTextField(name[0]);
			cb = new JComboBox(new String[]{"Easy", "Medium", "Hard"});
			tf.setFont(textFont);
			//tf.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
			//tf.putClientProperty("TextField.inactiveBackground", new ColorUIResource(new Color(255, 0, 0)));
			cb.setFont(textFont);
			chb.addActionListener(this);
			cb.addActionListener(this);
			tf.addActionListener(this);
			add(chb);
			add(tf);
			cb.setVisible(false);
			add(cb);
		}

		protected void setName(int i, String name) {
			names[i] = name;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(950, 550);
		}

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
			if (active)
				tf.setForeground(Color.BLACK);
			else
				tf.setForeground(Color.LIGHT_GRAY);

		}
	}
}
