package stockexchange;

/* Gergo Kovats */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import stockexchange.model.*;

public class Control implements GameInterface {

	final int AI_SPEED = 500;
	private int[] AIchoice = new int[2];
	boolean gameOver;
	boolean choiceStage;
	Model model;

	private GamePanelInterface panel; // To communicate with GUI

	public Control(int starter, Player[] players, GamePanelInterface panel) {
		this.panel = panel;
		model = new Model(starter, players, panel);
		panel.setModel(model);
		reStart();
	}

	//From GameInterface
	@Override
	public void setClickedColoumn(int column) {
		if (!(model.getActualPlayer() instanceof AI))
			runRound(column);
	}

	//From GameInterFace
	@Override
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	//From GameInterFace
	@Override
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
	javax.swing.Timer timerAI = new javax.swing.Timer(AI_SPEED, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			aiChoice();
		}
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
			if (!choiceStage) {
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
			if (!choiceStage) {
				//System.out.println("Step Stage started pointNr " + pointNr);
				if (model.getPossible().contains(pointNr)) {
					model.setPosition(pointNr);
					panel.setFigure();
					//panel.setPossible(getPossible());////////////////////////
					choiceStage = !choiceStage;
				}
			} else {
				//System.out.println("Choice Stage started pointNr " + pointNr);
				int keep;// the kept commodity's index in getNegihbors()
				int[] emptiedColoumns;
				if (model.getNeighbors().contains(pointNr)) {
					keep =model.getNeighbors().indexOf(pointNr);
					choiceStage = !choiceStage;
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
