/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import stockexchange.GuiControlInterface;
import stockexchange.GuiModelInterface;
import stockexchange.model.Player;

public class MainFrame extends JFrame {

	private GameContainerPanel gameContainerPanel;
	private StartUpContainerPanel startupPanel = null;
	private GuiControlInterface iGuiControl;
	private GuiModelInterface iGuiModel;
	//static final javafx.scene.paint.Color[] PLAYER_COLORSx = {javafx.scene.paint.Color.YELLOWGREEN, javafx.scene.paint.Color.YELLOWGREEN, javafx.scene.paint.Color.CHARTREUSE, javafx.scene.paint.Color.PALETURQUOISE};
	static final Color[] PLAYER_COLORS = {Color.decode("#9ACD32"), Color.decode("#EF4347"), Color.decode("#AFCEFE"), Color.decode("#CABD32")};
	public static final Color bgColor = new Color(5, 15, 40);

	private JMenuItem miSave;

	private int w;
	private int h;

	public MainFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		w = screenSize.width;
		h = screenSize.height;
		
		setBackground(MainFrame.bgColor);

		URL iconURL = getClass().getResource("/images/Icon_big.png");
		BufferedImage img = GamePanel.readFromURL(iconURL);
		setIcon(img);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Stock Exchange");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (iGuiControl != null)
					iGuiControl.pause(false);
				if (JOptionPane.showConfirmDialog(MainFrame.this,
								"Are you sure you want to exit?",
								"Confirm Exit", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE) == 0)
					System.exit(0);
				else if (iGuiControl != null)
					iGuiControl.unPause();
			}
		});

		JMenuBar mb = new JMenuBar();
		JMenu mFile = new JMenu("File");
		mb.add(mFile);
		JMenuItem miOpen = new JMenuItem("Open saved game");
		miSave = new JMenuItem("Save game");
		miSave.setEnabled(false);
		mFile.add(miOpen);

		miOpen.addActionListener((ActionEvent e) -> {
			iGuiControl.pause(false);
			FileDialog chooser = new FileDialog(this, "Open saved game", FileDialog.LOAD);
			chooser.setFilenameFilter((File dir, String name1) -> name1.substring(name1.length() - 4).equals(".cse"));
			chooser.setFile(".cse");
			chooser.setVisible(true);
			String chosenDir = chooser.getDirectory();
			String chosenFile = chooser.getFile();
			chooser.dispose();
			if (chosenFile != null) {
				if (startupPanel.isVisible())
					startupPanel.startGame();
				if (!iGuiControl.load(chosenDir + chosenFile)) {
					JOptionPane.showMessageDialog(MainFrame.this,
									"Error while loading saved game",
									"Load error", JOptionPane.ERROR_MESSAGE);

				} else
					gameContainerPanel.setTopNames(iGuiModel.getAllNames());
			}
			//gameContainerPanel.validate();

		});
		mFile.add(miSave);
		miSave.addActionListener((ActionEvent e) -> {
			FileDialog chooser = new FileDialog(this, "Save game as", FileDialog.SAVE);
			chooser.setFile(".cse");
			chooser.setVisible(true);
			String chosenDir = chooser.getDirectory();
			String chosenFile = chooser.getFile();
			chooser.dispose();
//			JFileChooser chooser = new JFileChooser();
//			int returnVal = chooser.showSaveDialog(SwingUtilities.getWindowAncestor(this));
//			String pathName = "";
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (chosenFile != null) {
//				File file = chooser.getSelectedFile();
//				pathName = file.getPath();
//				iGuiModel.save(pathName);
				iGuiControl.save(chosenDir + chosenFile);
			}
		});
		setJMenuBar(mb);

		startupPanel = new StartUpContainerPanel(new Player[0]);
		add(startupPanel);
		setBounds(w / 4, 0, w / 2, h / 2);
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
		private boolean exists(String className) {
		try {
			Class.forName(className, false, null);
			return true;
		} catch (ClassNotFoundException exception) {
			return false;
		}
	}

	private void setIcon(BufferedImage icn) {
		if (exists("com.apple.eawt.Application")) {
			com.apple.eawt.Application.getApplication().setDockIconImage(icn);
		}else{
			setIconImage(icn);
		}
	}

	public void setiGuiControl(GuiControlInterface iGuiControl) {
		this.iGuiControl = iGuiControl;
	}

	public void setiGuiModel(GuiModelInterface iGuiModel) {
		this.iGuiModel = iGuiModel;
	}

	public void setToStartup() {
		iGuiControl.pause(false);
		gameContainerPanel.setVisible(false);
		startupPanel.setVisible(true);
		setBounds(w / 4, 0, w / 2, h / 2);
		miSave.setEnabled(false);
		setLocationRelativeTo(null);
		//this.setResizable(false);
	}

	public void setToGame(GameContainerPanel gameContainerPanel) {
		//setResizable(true);
		startupPanel.setVisible(false);
		miSave.setEnabled(true);
		gameContainerPanel.setTopNames(iGuiModel.getAllNames());
		setContainerPanel(gameContainerPanel);
		int frameWidth = w * 5 / 7;
		//pack();
		setLocationRelativeTo(null);
		setBounds(w / 2 - frameWidth / 2, 0, frameWidth, Math.round(frameWidth / 1.5f));
	}

	protected void setContainerPanel(GameContainerPanel gameContainerPanel) {
		this.gameContainerPanel = gameContainerPanel;
		add(gameContainerPanel);
	}

}
