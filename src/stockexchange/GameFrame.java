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

	public GameFrame(Player[] players, int[] dimensions, GamePanel gamePanel, GameInterface theGame) throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setLayout(new BorderLayout());
		//setBounds(0, 0, dimensions[0], dimensions[1]);

		//panels
		JPanel top = new JPanel(new GridLayout(1, players.length * 2 - 1));
		JPanel bottom = new JPanel(new GridLayout(1, 5));
		

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
					setBounds(getX(), getY(), getWidth(), getWidth() * 4 / 5);
				} else {
					tglHints.setText("hints off");
					gamePanel.setHintsOnOff(false);
					setBounds(getX(), getY(), getWidth(), getWidth() * 3 / 5);
				}
			}
		}
		);

//
//		//fix aspect ratio
//		gamePanel.addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent e) {
//				int w = getWidth();
//				int h = getHeight();
//				double ratio = w / (double) h;
//				if (!tglHints.isSelected()) {
//					if (ratio - 5. / 3 > 0.001)
//						setSize(w, Math.round(w * 3 / 5f));
//					else if (ratio - 5. / 3 < -0.001)
//						setSize(Math.round(h * 5 / 3f), h);
//				}
//			}
//		});
//
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
		rulesFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		rulesFrame.pack();//nem tudok jobbat
		btRules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!rulesFrame.isVisible())
					rulesFrame.setVisible(true);
				else
					rulesFrame.dispose();
			}
		});

		// Restart button
		JButton btRestart = new JButton("Restart");

		btRestart.setFocusable(false);//to have keyboard focus on gamePanel

		btRestart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton temp = (JButton) e.getSource();
				//String[] tempStrings = players;
				StockExchange.createAndShowStartupGUI(players);
				gamePanel.setVisible(false);
				theGame.setGameOver(true);
				setVisible(false);
				dispose();
			}
		}
		);
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
		setBounds((dimensions[2] - dimensions[0]) / 2, 0, dimensions[0], dimensions[1]);
	}
}
