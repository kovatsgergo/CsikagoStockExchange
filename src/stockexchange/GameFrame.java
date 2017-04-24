/*Gergo Kovats*/
package stockexchange;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
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

	public GameFrame(String[] players, int[] dimensions) throws IOException {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setBounds(0, 0, dimensions[0], dimensions[1]);
		Player[] playerArray = new Player[players.length];
		System.out.println("players at createAndShowGameGUI: " + Arrays.toString(players));
		for (int i = 0; i < players.length; i++) {
			String name = players[i];
			if (name.length() > 2) {
				if (!(name.substring(0, 2).equals("AI"))) {
					//continue;
					playerArray[i] = new Player(players[i]);
				} else if (name.substring(2, 3).equals("e")
								|| name.contains("easy)")) {
					players[i] = "AI " + (i + 1) + " (easy)";
					playerArray[i] = new AIeasy(players[i]);
				} else if (name.substring(2, 3).equals("m")
								|| name.contains("(medium)")) {
					players[i] = "AI " + (i + 1) + " (medium)";
					playerArray[i] = new AImedium(players[i]);
				} else if (name.substring(2, 3).equals("h")
								|| name.contains("(hard)")) {
					players[i] = "AI " + (i + 1) + " (hard)";
					playerArray[i] = new AIhard(players[i]);
				}
			} else if (name.equals("AI")
							|| name.contains("(not set")) {
				players[i] = "AI " + (i + 1) + " (not set easy)";
				playerArray[i] = new AIeasy(players[i]);
			}
		}

		//panels
		JPanel top = new JPanel(new GridLayout(1, players.length * 2 + 2));
		final MyPanel gamePanel = new MyPanel(dimensions);
		final GamePanelInterface interfacePanel = gamePanel;
		final GameInterface theGame = new GameClass(0, playerArray, interfacePanel);
		gamePanel.setInterface(theGame);

		// Hints toggle
		final JToggleButton tglHints = new JToggleButton("hints off");

		tglHints.setFocusable(false);//to have keyboard focus on gamePanel

		tglHints.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JToggleButton temp = (JToggleButton) e.getSource();
				if (e.getStateChange() == ItemEvent.SELECTED) {
					tglHints.setText("HINTS ON");
					gamePanel.setHintsOnOff(true);
					setBounds(getX(), getY(), dimensions[0], dimensions[3]);
				} else {
					tglHints.setText("hints off");
					gamePanel.setHintsOnOff(false);
					setBounds(getX(), getY(), dimensions[0], dimensions[1]);
				}
			}
		}
		);

		//Rules button
		final JButton btRules = new JButton("Rules");

		btRules.setFocusable(false);//to have keyboard focus on gamePanel

		//generate Rules text
		//not improted to .jar
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
		StringBuffer rules = new StringBuffer();
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
		rulesFrame.pack();
		btRules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rulesFrame.setVisible(!rulesFrame.isVisible());
			}
		});

		// Restart button
		JButton btRestart = new JButton("Restart");

		btRestart.setFocusable(false);//to have keyboard focus on gamePanel

		btRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton temp = (JButton) e.getSource();
				String[] tempStrings = players;
				StockExchange.createAndShowStartupGUI(tempStrings.length, dimensions, (String[]) tempStrings);
				gamePanel.setVisible(false);
				theGame.setGameOver(true);
				setVisible(false);
				dispose();
			}
		}
		);
		// Add Player names to the top
		for (int i = 0; i < players.length; i++) {
			top.add(new JLabel(players[i], SwingConstants.CENTER), i * 2);
			if (i < players.length - 1) {
				top.add(new JLabel("vs", SwingConstants.CENTER), i * 2 + 1);
			}
		}
		// Add components to panel
		top.add(tglHints);
		top.add(btRules);
		top.add(btRestart);

		gamePanel.setFocusable(true);
		add(top, BorderLayout.NORTH);
		add(gamePanel, BorderLayout.CENTER);
		//pack();
		gamePanel.requestFocusInWindow();
		setVisible(true);
		setBounds((dimensions[2] - dimensions[0]) / 2, 0, dimensions[0], dimensions[1]);
	}
}
