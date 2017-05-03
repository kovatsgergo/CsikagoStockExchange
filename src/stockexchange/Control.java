package stockexchange;

/* Gergo Kovats */
import java.awt.event.ActionEvent;
import java.util.Arrays;
import stockexchange.gui.ControlGuiInterface;
import stockexchange.gui.GameContainerPanel;
import stockexchange.gui.GamePanel;
import stockexchange.gui.MainFrame;
import stockexchange.model.*;

public class Control implements GuiControlInterface {

	private final int AI_SPEED = 1500;
	private int[] AIchoice = new int[2];
	//private boolean gameOver;
	private Model model;
	private ControlGuiInterface panel; // To communicate with GUI
	private static MainFrame frame;

	public Control(Player[] players, Model model, ControlGuiInterface interfacePanel) {
		this.model = model;
		this.panel = interfacePanel;
		reStart();
	}

	@Override //From GuiControlInterface
	public void load(String pathFile) {
		if (model.load(pathFile))
			panel.startFromLoaded();
	}

	@Override	//From GuiControlInterface
	public void setClickedColoumn(int column) {
		if (!(model.getActualPlayer() instanceof AI))
			runRound(column);
	}

	@Override 	//From GuiControlInterFace
	public void pause() {
		timerAI.stop();
		int n = panel.pausePopup();
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

	/////////////////////////
	///////////////////////AI
	private int[] getAIMove() {
		return ((AI) model.getActualPlayer()).makeMove(model);
	}

	private void aiPoint() {
		//System.out.println("aiPoint called\t actual: " + players.get(actualPlayer).getName());
		if (model.getActualPlayer() instanceof AI && !model.isGameOver()) {
			timerAI.start();
		}
	}

	javax.swing.Timer timerAI = new javax.swing.Timer(AI_SPEED, (ActionEvent e) -> {
		aiChoice();
	});

	/**
	 * Handles the AIs' turns at both stages
	 */
	private void aiChoice() {
		//System.out.println("aiChoice run");
		if (model.getActualPlayer() instanceof AI && !model.isGameOver()) {
			int pointNr;
			if (!model.getChoiceStage()) {
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
		if (!model.isGameOver()) {
			if (!model.getChoiceStage()) {
				//System.out.println("Step Stage started pointNr " + pointNr);
				if (model.isValidStep(pointNr)) {
					model.setPosition(pointNr);
					panel.setFigure();
					model.changeStage();
				}
			} else {
				//System.out.println("Choice Stage started pointNr " + pointNr);
				int[] emptiedColumns;
				if (model.isValidStep(pointNr)) {
					timerAI.stop();
					emptiedColumns = model.handleChoice(pointNr);
					panel.makeChoice(pointNr, emptiedColumns);
					model.changeStage();
				}
			}
			//System.out.println("ai started in runhuman");
			aiPoint();
		}

		panel.setHint();
		if (model.isGameOver())
			endGame();
	}

	private void endGame() {
		int n;
		if (model.isAllPlayersAI()) {
			n = 2;//to test AIs
			//n = panel.gameOverPopup(gameOverString(winner));
		} else {
			n = panel.gameOverPopup();
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

	/**
	 * Initialization
	 */
	private void reStart() {
		model.reStart();
		//prices = Arrays.copyOf(PRICES_AT_START, prices.length);
		//panel.setNrGameCols();
		panel.start();
		runRound(-1);
	}

	public static void switchToGame(String[][] players, int[] dims) {
		Player[] playersArray = createPlayers(players);
		Model model = new Model(playersArray);
		GamePanel gamePanel = new GamePanel(model);
		ControlGuiInterface interfacePanel = gamePanel;
		GuiControlInterface control = new Control(playersArray, model, interfacePanel);
		gamePanel.setInterface(control);
		frame.setControl(control);
		frame.setModel(model);
		GameContainerPanel gameContainerPanel = new GameContainerPanel(players, dims, gamePanel, control);
		frame.setToGame(gameContainerPanel);
	}

	public static void switchToSetup() {
		frame.setToStartup();
	}

	private static Player[] createPlayers(String[][] players) {
		System.out.println("players at createPlayers: " + Arrays.deepToString(players));
		Player[] playerArray = new Player[players.length];
		for (int i = 0; i < players.length; i++) {
			if (players[i][1].equals("human"))
				playerArray[i] = new Player(players[i][0]);
			else
				switch (players[i][0]) {
					case "Easy":
						playerArray[i] = new AIeasy("AI " + (i + 1) + " (easy)");
						break;
					case "Medium":
						playerArray[i] = new AImedium("AI " + (i + 1) + " (medium)");
						break;
					case "Hard":
						playerArray[i] = new AIhard("AI " + (i + 1) + " (hard)");
						break;
				}
		}
		return playerArray;
	}

	public static void main(String[] args) {
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		frame = new MainFrame();
		frame.setVisible(true);
	}

}
