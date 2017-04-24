/*Gergo Kovats*/
package stockexchange;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import static stockexchange.StockExchange.MAXPLAYERS;

public class StartUpFrame extends JFrame {
	String[] names;

	public StartUpFrame(int plyrs, int[] dimensions, String... namesgiven) {
		names = new String[MAXPLAYERS];
		final String[] defaultNames = new String[MAXPLAYERS];
		for (int i = 0; i < defaultNames.length; i++) {
			defaultNames[i] = "Pleyer " + (i + 1) + "'s name";
		}
		//names = namesgiven;
		System.arraycopy(namesgiven, 0, names, 0, namesgiven.length);
		//Declare number of players and set their names
		int numPlayers;
		if (namesgiven.length < MAXPLAYERS) {
			numPlayers = plyrs;
			//System.out.println("bifo numPlayers = plyrs: " + plyrs);
			//System.out.println("namesg len" + namesgiven.length);
			//System.out.println("bifo names: " + Arrays.toString(names));
			for (int i = namesgiven.length; i < MAXPLAYERS; i++) {
				names[i] = defaultNames[i];
			}
			//System.out.println("afto numPlayers = plyrs: " + plyrs);
			System.out.println("afto names: " + Arrays.toString(names));
		} else {
			names = namesgiven;
			numPlayers = plyrs;
			System.out.println("numPlayers = names.length: " + names.length);
		}

		JFrame f = new JFrame("Startup");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout());

		JPanel titlePanel = new JPanel(new GridLayout(0, 1));
		JPanel panel = new JPanel(new GridLayout(0, 1));
		JPanel checkPanel = new JPanel(new GridLayout(0, 1));

		//Title of Frame
		JLabel titleText = new JLabel("Set the name of the Pleyers", SwingConstants.CENTER);
		titleText.setFont(f.getFont());
		titlePanel.add(titleText);
		f.add(titlePanel, BorderLayout.NORTH);

		titlePanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				JPanel c = (JPanel) evt.getSource();
				Dimension dim = c.getSize();
				c.setFont(c.getFont().deriveFont(Math.min((dim.width / 20f), c.getSize().height)));
				c.repaint();
			}
		});

		//TexitFields for Player Names
		ArrayList<JTextField> textFields = new ArrayList();
		for (int i = 0; i < MAXPLAYERS; i++) {
			textFields.add(new JTextField(20));
			if (i < names.length) {
				//System.out.println("textfields " + i);
				if (names[i].length() > 0) {
					textFields.get(i).setText(names[i]);
				}
			} else {
				textFields.get(i).setText(names[i]);
			}
			textFields.get(i).selectAll();
			textFields.get(i).setFont(f.getFont());
			textFields.get(i).putClientProperty("2", i);
			textFields.get(i).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JTextField textfield = (JTextField) e.getSource();
					String[] textFieldNames = names;
					int i = (Integer) textfield.getClientProperty("2");
					textFieldNames[i] = textfield.getText();// TODO: set the checkboxes if the text starts with AI
					System.out.println("TxtFld names[" + i + "] set to " + textfield.getText());
					System.out.println("so names: " + Arrays.toString(textFieldNames));
				}
			});
		}

		//CheckBoxes for switching AIs for Human players
		checkPanel.add(new JLabel(""), 0);
		checkPanel.add(new JLabel("AI", SwingConstants.CENTER), 1);
		ArrayList<JCheckBox> checkBoxes = new ArrayList();
		for (int i = 0; i < MAXPLAYERS; i++) {//17, 18-as fejezet
			checkBoxes.add(new JCheckBox());
			checkBoxes.get(i).putClientProperty("1", checkBoxes.get(i));
			checkBoxes.get(i).putClientProperty("2", textFields.get(i));
			if (i < names.length) {
				checkBoxes.get(i).putClientProperty("3", names[i]);
				if (names[i].equals("AI")) {
					checkBoxes.get(i).setSelected(true);
				}
				System.out.println("ChBx names[" + i + "] = " + names[i]);
			}
			checkBoxes.get(i).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					JCheckBox checkbox = (JCheckBox) actionEvent.getSource();
					JTextField textfield = (JTextField) checkbox.getClientProperty("2");
					boolean selected = checkbox.getModel().isSelected();
					if (selected) {
						checkbox.putClientProperty("3", textfield.getText());
						textfield.setText("AI");
						textfield.setEditable(false);
					} else {
						String oldName = (String) checkbox.getClientProperty("3");
						if (!oldName.equals("AI")) {
							textfield.setText(oldName);/////////////////////////
						} else {
							int i = textfield.getParent().getComponentZOrder(textfield) - 2;
							textfield.setText(defaultNames[i]);
						}
						textfield.setEditable(true);
					}
				}
			});
		}

		//Lowest button for starting game
		final JButton OKButton = new JButton("Start game");
		OKButton.setFont(f.getFont());

		//For setting the number of players
		JComboBox cb = new JComboBox();
		cb.addItem(1);
		cb.addItem(2);
		cb.addItem(3);
		cb.addItem(4);
		cb.setSelectedItem(numPlayers);
		cb.setFont(f.getFont());
		cb.putClientProperty("1", checkBoxes);
		cb.putClientProperty("2", panel);
		cb.putClientProperty("3", numPlayers);
		cb.putClientProperty("4", textFields);
		cb.putClientProperty("5", f);
		cb.putClientProperty("6", checkPanel);
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int choiceNum = (Integer) e.getItem();
				JComboBox tempButton = (JComboBox) e.getSource();
				ArrayList<JCheckBox> checkboxes = (ArrayList<JCheckBox>) tempButton.getClientProperty("1");
				JPanel panel = (JPanel) tempButton.getClientProperty("2");
				JPanel checkpanel = (JPanel) tempButton.getClientProperty("6");
				ArrayList<JTextField> textfields = (ArrayList<JTextField>) tempButton.getClientProperty("4");
				JFrame tempFrame = (JFrame) tempButton.getClientProperty("5");
				int oldNumPl = (int) tempButton.getClientProperty("3");
				int newNumPl = choiceNum;

				if (oldNumPl < newNumPl) {
					panel.remove(OKButton);
					while (oldNumPl < newNumPl) {
						panel.add(textfields.get(oldNumPl));
						checkpanel.add(checkboxes.get(oldNumPl), oldNumPl + 2);
						oldNumPl++;
					}
					panel.add(OKButton);
				} else if (oldNumPl > newNumPl) {
					panel.remove(OKButton);
					while (newNumPl < oldNumPl) {
						panel.remove(textfields.get(oldNumPl - 1));
						checkpanel.remove(checkboxes.get(oldNumPl - 1));
						oldNumPl--;
					}
					panel.add(OKButton);
				}
				tempButton.putClientProperty("3", newNumPl);
				int tempW = tempFrame.getSize().width;
				tempFrame.setMinimumSize(new Dimension(tempW, 0));
				tempFrame.pack();
				tempFrame.setMinimumSize(new Dimension(300, 250));
				tempFrame.repaint();
			}
		});

		//Lowest button for starting game
		OKButton.putClientProperty("1", cb);////////////////////////////
		OKButton.putClientProperty("2", textFields);
		OKButton.putClientProperty("3", f);
		OKButton.addActionListener(
						new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton tempButton = (JButton) e.getSource();
				JComboBox getnumplayers = (JComboBox) tempButton.getClientProperty("1");
				int numplayers = (Integer) (getnumplayers.getSelectedItem());
				ArrayList<JTextField> textfields = (ArrayList<JTextField>) tempButton.getClientProperty("2");
				JFrame tempFrame = (JFrame) tempButton.getClientProperty("3");

				String[] temp = new String[numplayers];
				for (int i = 0; i < numplayers; i++) {
					temp[i] = textfields.get(i).getText();
				}
				try {
					StockExchange.createAndShowGameGUI(temp, dimensions);
					tempFrame.setVisible(false);
				} catch (IOException ex) {
					Logger.getLogger(StockExchange.class.getName()).
									log(Level.SEVERE, null, ex);
				}
			}
		}
		);

		JLabel subtitle = new JLabel("set number of players");
		subtitle.setFont(f.getFont());

		panel.add(subtitle, 0);
		panel.add(cb, 1);

		int dothis = numPlayers;
		for (int i = 0; i < dothis; i++) {
			panel.add(textFields.get(i), i + 2);
			checkPanel.add(checkBoxes.get(i), i + 2);
		}
		checkPanel.add(new JLabel(), dothis + 2);

		panel.add(OKButton, dothis + 2);//final, starts game

		panel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				JPanel c = (JPanel) evt.getSource();
				Dimension dim = c.getSize();
				c.setFont(c.getFont().deriveFont(Math.min((float) (dim.width / 25f),
								c.getComponent(0).getHeight())));
				//System.out.println(c.getComponent(0).getHeight());
				c.repaint();
			}
		});
		f.add(panel, BorderLayout.CENTER);
		f.add(checkPanel, BorderLayout.WEST);
		//f.pack();
		f.setBounds(dimensions[0] / 2, 0, dimensions[0] / 3, dimensions[1] / 2);
		f.setMinimumSize(new Dimension(300, (numPlayers + 5) * 30));
		f.repaint();
		f.setVisible(true);
	}
}
