package stockexchange;

/* Gergo Kovats */
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import stockexchange.gui.ControlGuiInterface;
import stockexchange.gui.MainFrame;
import stockexchange.model.*;

public class Control implements GuiControlInterface {

	final int AI_SPEED = 1500;
	private int[] AIchoice = new int[2];
	boolean gameOver;
	//boolean choiceStage;
	Model model;
	private ControlGuiInterface panel; // To communicate with GUI
	static MainFrame frame;

	public Control(Player[] players, Model model, ControlGuiInterface interfacePanel) {
		this.model = model;
		this.panel = interfacePanel;
		reStart();
	}

	@Override //From GuiControlInterface
	public void load() {
		if (model.load())
			panel.start(model.getChoiceStage(), model.getPosition(), model.getActualPlayerIndex());
	}

	@Override	//From GuiControlInterface
	public void setClickedColoumn(int column) {
		if (!(model.getActualPlayer() instanceof AI))
			runRound(column);
	}

	@Override	//From GuiControlInterFace
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
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
				panel.quitGame();
				break;
			case 0:
				System.exit(0);
		}
	}

	private int[] getAIMove() {
		((AI) model.getActualPlayer()).setObservedNextPlayer(model.makeObservedNextPlayers());
		return ((AI) model.getActualPlayer()).makeMove(model);
	}

	/////////////////////////
	///////////////////////AI
	javax.swing.Timer timerAI = new javax.swing.Timer(AI_SPEED, (ActionEvent e) -> {
		aiChoice();
	});

	private void aiPoint() {
		//System.out.println("aiPoint called\t actual: " + players.get(actualPlayer).getName());
		if (model.getActualPlayer() instanceof AI && !gameOver) {
			timerAI.start();
		}
	}

	/**
	 * Handles the AIs' turns at both stages
	 */
	private void aiChoice() {
		//System.out.println("aiChoice run");
		if (model.getActualPlayer() instanceof AI && !gameOver) {
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
	public void runRound(int pointNr) {///////////////IDEIGLENES
		//System.out.println("\trunRound started\tgamover: " + gameOver + "\tcolumns: " + columns.size());
		if (!gameOver) {
			if (!model.getChoiceStage()) {
				System.out.println("Step Stage started pointNr " + pointNr);
				if (model.getPossible().contains(pointNr)) {
					model.setPosition(pointNr);
					panel.setFigure();
					//panel.setPossible(getPossible());////////////////////////
					model.changeStage();
				}
			} else {
				System.out.println("Choice Stage started pointNr " + pointNr);
				int keep;// the kept commodity's index in getNegihbors()
				int[] emptiedColoumns;
				if (model.getNeighbors().contains(pointNr)) {
					keep = model.getNeighbors().indexOf(pointNr);
					model.changeStage();
					int kept = model.getNeighbors().get(keep);
					int sold = model.getNeighbors().get(1 - keep);
					emptiedColoumns = model.handleChoice(kept, sold);
					if (model.getNrOfCols() < 3) {
						gameOver = true;
					}
					panel.makeChoice(kept, sold, emptiedColoumns);
					panel.setNrGameCols();
					panel.setFigure();
					timerAI.stop();
					panel.setPossible(model.getPossible());
				}
			}
			//System.out.println("ai started in runhuman");
			///////////////////

			aiPoint();
		}

		panel.setHint(model.makeHints());
		if (gameOver)
			endGame();
	}

	private void endGame() {
		ArrayList<Integer> winner = model.getWinner();
		int n;
		if (model.isAllPlayersAI()) {
			n = 2;//to test AIs
			//n = panel.gameOverPopup(gameOverString(winner));

		} else {
			n = panel.gameOverPopup(model.gameOverString(winner));
		}

		switch (n) {
			case 2:
				reStart();
				break;
			case 1:
				panel.quitGame();
				break;
			case 0:
				System.exit(0);
		}

	}

	/**
	 * Initialization
	 */
	private void reStart() {
		gameOver = false;
		model.reStart();
		//prices = Arrays.copyOf(PRICES_AT_START, prices.length);
		panel.setNrGameCols();
		panel.start();
		runRound(-1);

	}

}
