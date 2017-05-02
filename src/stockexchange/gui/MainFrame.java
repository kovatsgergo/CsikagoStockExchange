/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import stockexchange.GuiControlInterface;
import stockexchange.model.Model;
import stockexchange.model.Player;

public class MainFrame extends JFrame {

	private GameContainerPanel gameContainerPanel;
	private final StartUpContainerPanel startupPanel;
	private GuiControlInterface control;
	private Model model;

	private int w;
	private int h;

	public MainFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		w = screenSize.width;
		h = screenSize.height;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar mb = new JMenuBar();
		JMenu mFile = new JMenu("File");
		mb.add(mFile);
		JMenuItem miOpen = new JMenuItem("Open saved game");
		JMenuItem miSave = new JMenuItem("Save game");
		mFile.add(miOpen);

		miOpen.addActionListener((ActionEvent e) -> {
			control.load();
			gameContainerPanel.setTopnames(model.getAllNames());
			//gameContainerPanel.validate();
			//fc.showOpenDialog((JFrame) SwingUtilities.getWindowAncestor(this));
		});
		mFile.add(miSave);
		miSave.addActionListener((ActionEvent e) -> {
			//fc.showSaveDialog((JFrame) SwingUtilities.getWindowAncestor(this));
			model.save();
		});
		setJMenuBar(mb);

		startupPanel = new StartUpContainerPanel(new Player[0]);
		add(startupPanel);
		setBounds(w / 4, 0, w / 2, h / 2);
		setResizable(false);
	}

	public void setControl(GuiControlInterface control) {
		this.control = control;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setToStartup() {
		gameContainerPanel.setVisible(false);
		startupPanel.setVisible(true);
		setBounds(w / 4, 0, w / 2, h / 2);
		//this.setResizable(false);
	}

	public void setToGame(GameContainerPanel gameContainerPanel) {
		//setResizable(true);
		startupPanel.setVisible(false);
		setContainerPanel(gameContainerPanel);
		setBounds(w / 2 - w / 14 * 5, 0, w / 7 * 5, Math.round(w * 0.45f));
	}

	protected void setContainerPanel(GameContainerPanel gameContainerPanel) {
		this.gameContainerPanel = gameContainerPanel;
		add(gameContainerPanel);
	}

}
