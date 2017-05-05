package stockexchange;

/* Gergo Kovats */
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import stockexchange.gui.GameContainerPanel;
import stockexchange.gui.GamePanel;
import stockexchange.gui.MainFrame;
import stockexchange.model.*;

public class StockExchange implements GuiControlInterface {

	private final int AI_SPEED = 1000;
	private int[] AIchoice = new int[2];
	//private boolean gameOver;
	private ControlModelInterface iControlModel;
	private ControlGuiInterface iControlGui; // To communicate with GUI
	private static MainFrame frame;
	private javax.swing.Timer timerAI = new javax.swing.Timer(AI_SPEED, (ActionEvent e) -> {
		aiChoice();
	});

	public StockExchange(Model model, ControlGuiInterface interfacePanel) {
		URL imageURL = getClass().getResource("/images/Icon_big.png");
		BufferedImage img = GamePanel.readFromURL(imageURL);
		setIcon(img);
		this.iControlModel = model;
		this.iControlGui = interfacePanel;
		reStart();
	}

	@Override //From GuiControlInterface
	public boolean load(String pathFile) {
		if (iControlModel.load(pathFile)) {
			iControlGui.startFromLoaded(iControlModel.getChoiceStage());
			return true;
		}
		return false;
	}

	public void save(String pathFile) {
		iControlModel.save(pathFile);
	}

	@Override	//From GuiControlInterface
	public void setClickedColoumn(int column) {
		if (!(iControlModel.getActualPlayer() instanceof AI))
			runRound(column);
	}

	@Override 	//From GuiControlInterFace
	public void pause(boolean popup) {
		timerAI.stop();
		if (popup) {
			int n = iControlGui.pausePopup();
			switch (n) {
				case 2:
					timerAI.start();
					break;
				case 1:
					switchToSetup();
					break;
				case 0:
					System.exit(0);
			}
		}
	}

	@Override
	public void unPause() {
		runRound(-1);
	}

	/////////////////////////
	///////////////////////AI
	private int[] getAIMove() {
		return ((AI) iControlModel.getActualPlayer()).makeMove();
	}

	private void aiPoint() {
		//System.out.println("aiPoint called\t actual: " + players.get(actualPlayer).getName());
		if (iControlModel.getActualPlayer() instanceof AI && !iControlModel.isGameOver()) {
			timerAI.start();
		}
	}

	/**
	 * Handles the AIs' turns at both stages
	 */
	private void aiChoice() {
		//System.out.println("aiChoice run");
		if (iControlModel.getActualPlayer() instanceof AI && !iControlModel.isGameOver()) {
			int pointNr;
			if (!iControlModel.getChoiceStage()) {
				//System.out.println("aiChoice choiceStage");
				AIchoice = getAIMove();
				pointNr = AIchoice[0];
			} else {
				//System.out.println("aiChoice stepStage");
				pointNr = AIchoice[1];
			}
			runRound(pointNr);
			//timerAI.stop();
		} else
			timerAI.stop();
	}
	///////////////////////AI
	/////////////////////////

	/**
	 * Almost like a "game loop"
	 *
	 * @param pointNr the chosen column's index number
	 */
	public void runRound(int pointNr) {
		//System.out.println("\trunRound started\tgamover: " + gameOver + "\tcolumns: " + columns.size());
		if (!iControlModel.isGameOver()) {
			if (!iControlModel.getChoiceStage()) {
				//System.out.println("Step Stage started pointNr " + pointNr);
				if (iControlModel.isValidStep(pointNr)) {
					iControlModel.setPosition(pointNr);
					iControlGui.setFigure();
					iControlModel.changeStage();
				}
			} else {
				//System.out.println("Choice Stage started pointNr " + pointNr);
				int[] emptiedColumns;
				if (iControlModel.isValidStep(pointNr)) {
					timerAI.stop();
					emptiedColumns = iControlModel.handleChoice(pointNr);
					iControlGui.makeChoice(pointNr, emptiedColumns);
					iControlModel.changeStage();
				}
			}
			//System.out.println("ai started in runhuman");
			aiPoint();
		}

		iControlGui.setHint();
		if (iControlModel.isGameOver())
			endGame();
	}

	private void endGame() {
		int n;
		if (iControlModel.isAllPlayersAI()) {
			//n = 2;//to test AIs
			n = iControlGui.gameOverPopup();
		} else {
			n = iControlGui.gameOverPopup();
		}
		switch (n) {
			case 2:
				reStart();
				break;
			case 1:
				switchToSetup();
				break;
			case 0:
				System.exit(0);
		}
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
			setIcon(icn);
		}
	}

	/**
	 * Initialization
	 */
	private void reStart() {
		iControlModel.reStart();
		//prices = Arrays.copyOf(PRICES_AT_START, prices.length);
		//panel.setNrGameCols();
		iControlGui.start();
		runRound(-1);
	}

	public static void switchToGame(String[][] players, int[] dims) {
		//Player[] playersArray = createPlayers(players);
		Model model = new Model(players);
		GuiModelInterface iGuiModel = model;
		ControlModelInterface iControlModel = model;
		GamePanel gamePanel = new GamePanel(iGuiModel);
		ControlGuiInterface iControlGui = gamePanel;
		GuiControlInterface iGuiControl = new StockExchange(model, iControlGui);
		gamePanel.setInterface(iGuiControl);
		frame.setiGuiControl(iGuiControl);
		frame.setiGuiModel(iGuiModel);
		GameContainerPanel gameContainerPanel = new GameContainerPanel(players, dims, gamePanel, iGuiControl);
		frame.setToGame(gameContainerPanel);
	}

	public static void switchToSetup() {
		frame.setToStartup();
	}

	public static void main(String[] args) {
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new MainFrame();
		frame.setVisible(true);
	}

}
