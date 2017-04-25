package stockexchange;

/* Gergo Kovats */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameClass implements GameInterface {//TODO: enum vagy map

	static final ArrayList<String> GOOD_TYPES = new ArrayList(Arrays.asList(new String[]{"Wheat", "Sugar", "Coffee", "Rice", "Cocoa", "Corn"}));
	static final int[] PRICES_AT_START = {7, 6, 6, 6, 6, 6};
	static final int START_NR_COLOUMS = 9;
	static final int START_HEIGHT_COLOUMNS = 4; //(START_NR_COLOUMS * START_HEIGHT_COLOUMNS)%GOOD_TYPES.size()=0

	final int AI_SPEED = 500;
	private int[] AIchoice = new int[2];

	private final Player[] players;
	private ArrayList<Column> columns = new ArrayList();
	private ArrayList<String> startGoods = new ArrayList();
	//private ArrayList<Integer> winner;

	private int[] wins;//number of win points of all players
	private boolean gameOver = false;//is it game over
	private boolean choiceStage = false;// is it the choice stage
	private int lastStarter = -1;//the player('s index, who started the last round)
	private int actualPlayer;//the player whose turn is is
	private int[] prices = new int[6];// = {7, 6, 6, 6, 6, 6}; //current prices of goods
	private int position = 0;//the figure's current position

	private GamePanelInterface panel; // To communicate with GUI

	public GameClass(int starter, Player[] players, GamePanelInterface panel) {
		this.players = players;
		for (Player player : players) {
			System.out.println(player.getClass().getSimpleName());
		}
		//System.out.println("constructor players: " + Arrays.toString(players));
		wins = new int[players.length];
		this.panel = panel;
		reStart();
	}

	/**
	 * Deal all the goods to the columns
	 */
	private void dealGoods() {
		//System.out.println(startGoods.toString());
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			for (int j = 0; j < START_HEIGHT_COLOUMNS; j++) {
				//columns.get(i).add(startGoods.get(0));
				columns.get(i).add(startGoods.remove(0));
			}
		}
		//System.out.println(startGoods.toString());
	}

	/**
	 * Add the startGoods to the startGoods variable in type order
	 */
	private void makeGoods() {
		for (int i = 0; i < GOOD_TYPES.size(); i++) {
			for (int j = 0; j < (START_HEIGHT_COLOUMNS * START_NR_COLOUMS) / GOOD_TYPES.size(); j++) {
				startGoods.add(GOOD_TYPES.get(i));
			}
		}
	}

	private String[] getAllNames() {
		String[] allNames = new String[players.length];
		for (int i = 0; i < players.length; i++) {
			allNames[i] = players[i].getName();
		}
		return allNames;
	}

	private int getNrCols() {
		return columns.size();
	}

	private int[] getPossible() {
		int[] possible = new int[3];
		possible[0] = (position + 1) % getNrCols();
		possible[1] = (position + 2) % getNrCols();
		possible[2] = (position + 3) % getNrCols();
		return possible;
	}

	private int[] getNeighbors() {
		int[] neighbors = new int[2];
		neighbors[0] = (position + 1) % getNrCols();
		neighbors[1] = (position + getNrCols() - 1) % getNrCols();
		return neighbors;
	}

	//From GameInterface
	@Override
	public void setClickedColoumn(int column) {
		if (!(players[actualPlayer] instanceof AI))
			runRound(column);
	}

	//From GameInterFace
	@Override
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	//From GameInterFace
	@Override
	public void pause(boolean paused) {
		if (paused)
			timerAI.stop();
		else
			timerAI.start();
	}

	private ObservedPlayer makeObservedNextPlayers() {
		return new ObservedPlayer(players[nextPlayer(actualPlayer)]);
	}

	private int[] getAIMove() {
		((AI) players[actualPlayer]).setObservedNextPlayer(makeObservedNextPlayers());
		return ((AI) players[actualPlayer]).makeMove(position, getTops(), getColsSizes());
	}

	/**
	 * Handle the consequences of a choice phase: removes goods, empty columns
	 *
	 * @param kept
	 * @param sold
	 * @return
	 */
	private int[] handleChoice(int kept, int sold) {
		//System.out.println("Handle");
		kept %= getNrCols();
		sold %= getNrCols();
		int[] ret = {-1, -1};
		Column actualKept = columns.get(kept);
		Column actualSold = columns.get(sold);
		int outIdx = GOOD_TYPES.indexOf(actualSold.getTop());
		players[actualPlayer].add(actualKept.getTop());
		//System.out.println("actualK.getTop() "+actualK.getTop()+ " \t actualO.getTop()"
		actualKept.remove();
		if (actualKept.goods.isEmpty()) {
			columns.remove(kept);
			panel.setNrGameCols(columns.size());
			ret[0] = kept;
			if (position > kept) {
				position--;
			}
			if (sold > kept) {
				sold--;
			}
		}
		actualSold.remove();
		if (actualSold.goods.isEmpty()) {
			columns.remove(sold);
			panel.setNrGameCols(columns.size());
			ret[1] = sold;
			if (position > sold) {
				position--;
			}
		}
		prices[outIdx] -= 1;
		countPoints();
		//System.out.println(Arrays.toString(prices));
		if (columns.size() < 3) {
			gameOver = true;
		}
		actualPlayer = nextPlayer(actualPlayer);

		//System.out.printf("keepIdx: %d  outIdx: %d \t prices[keepIdx]: %d \t keep: %d\n",
		//keepIdx, outIdx, prices[keepIdx], keep);
		return ret;
	}

	/**
	 * Recalculates points of all players based on the new prices
	 */
	private void countPoints() {
		for (Player player : players) {
			player.setPrices(prices);
		}
	}

	private int nextPlayer(int actual) {
		return (actual + 1) % players.length;
	}

	/**
	 *
	 * @return the top startGoods of each column
	 */
	private ArrayList<String> getTops() {
		ArrayList<String> topGoods = new ArrayList<>();
		for (Column column : columns) {
			topGoods.add(column.getTop());
		}
		return topGoods;
	}

	private int[] getColsSizes() {
		int[] colSizes = new int[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			colSizes[i] = columns.get(i).getHeight();
		}
		return colSizes;
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
		if (players[actualPlayer] instanceof AI && !gameOver) {
			timerAI.start();
		}
	}

	/**
	 * Handles the AIs' turns at both stages
	 */
	private void aiChoice() {
		//System.out.println("aiChoice run");
		if (players[actualPlayer] instanceof AI && !gameOver) {
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
	private void runRound(int pointNr) {
		//System.out.println("\trunRound started\tgamover: " + theGame.gameOver + "\tcolumns: " + theGame.columns.size());
		if (!gameOver) {
			if (!choiceStage) {
				//System.out.println("Step Stage started pointNr " + pointNr);
				if (Arrays.toString(getPossible()).contains(pointNr + "")) {
					position = pointNr;
					panel.setFigure(position);
					panel.setPossible(getPossible());
					choiceStage = !choiceStage;
				}
			} else {
				//System.out.println("Choice Stage started pointNr " + pointNr);
				int out;
				int[] emptiedColoumns;
				if (Arrays.toString(getNeighbors()).contains(pointNr + "")) {
					if (getNeighbors()[0] == pointNr) {
						choiceStage = !choiceStage;
						out = 0;
					} else {
						choiceStage = !choiceStage;
						out = 1;
					}
					//String thrownStr = columns.get(getNeighbors()[out]).getTop();
					//String takenStr = columns.get().getTop();
					emptiedColoumns = handleChoice(getNeighbors()[out], getNeighbors()[1 - out]);
					panel.setNrGameCols(columns.size());
					panel.makeChoice(getNeighbors()[1 - out], emptiedColoumns, prices, getTops(), getColsSizes());
					panel.setFigure(position);
					//panel.setPossible(getPossible());
				}
				timerAI.stop();
			}
			//System.out.println("ai started in runhuman");
			aiPoint();
		}

		makeHints();

		if (gameOver) {
			ArrayList<Integer> winner = getWinner();
			boolean aiall = true;
			int i = 0;
			do {
				aiall = aiall && (players[i++] instanceof AI);
			} while (i < players.length && aiall);
			int n;
			if (aiall) {
				//n = 2;
				n = panel.gameOverPopup(gameOverString(winner));

			} else {
				n = panel.gameOverPopup(gameOverString(winner));
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
	}

	/**
	 * Initialization
	 */
	private void reStart() {
		gameOver = false;
		prices = Arrays.copyOf(PRICES_AT_START, prices.length);
		for (Player player : players) {
			//System.out.println(player);
			player.goods.clear();
			player.setPrices(prices);
		}

		System.out.println("last starter: " + lastStarter);

		columns.clear();
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			columns.add(new Column());
		}
		panel.setNrGameCols(START_NR_COLOUMS);
		makeGoods();
		Collections.shuffle(startGoods);
		dealGoods();

		panel.start(getTops(), getAllNames(), getColsSizes(), wins);
//		int nextStarter = (++lastStarter) % playAgains;
//		actualPlayer = Math.max(nextStarter, Math.min(nextStarter, numPlayers - 1));
		actualPlayer = nextPlayer(lastStarter++);
		position = 0;
		runRound(-1);
		System.out.println("next starter: " + actualPlayer + "\n");

	}

	private void makeHints() {
		String[] returnHints = new String[players.length * 2];
		for (int i = 0; i < players.length; i++) {
			String stndrdth = "";
			switch (i) {
				case 0:
					stndrdth = "st";
					break;
				case 1:
					stndrdth = "nd";
					break;
				case 2:
					stndrdth = "rd";
					break;
				case 3:
					stndrdth = "th";
					break;
			}
			returnHints[i * 2] = (i + 1) + stndrdth + " player " + players[i].getName()
							+ " has " + players[i].getPoints() + " points";
			String temp = "";
			for (String good : players[i].goods) {
				temp += good + " (" + prices[GOOD_TYPES.indexOf(good)] + ") ";
			}
			returnHints[i * 2 + 1] = players[i].getName() + " has Drawn Yet: " + temp;
		}
		panel.setHint(returnHints);
	}

	private ArrayList<Integer> getWinner() {
		ArrayList<Integer> returnWinner = new ArrayList();
		ArrayList<Integer> points = new ArrayList();
		for (int i = 0; i < players.length; i++) {
			points.add(players[i].getPoints());
		}
		int max = Collections.max(points);
		for (int i = 0; i < players.length; i++) {
			if (points.get(i) == max) {
				returnWinner.add(i);
				wins[i] = wins[i] + 1;
			}
		}
		System.out.println(points.toString() + "\tmax: " + max + "\t indexofit: " + points.indexOf(max) + " "
						+ returnWinner.toString() + " wins " + Arrays.toString(wins));
		return returnWinner;

	}

	private String gameOverString(ArrayList<Integer> winner) {
		StringBuffer helpString = new StringBuffer("");
		for (int i = 0; i < winner.size() - 1; i++) {
			helpString.append(players[winner.get(i)].getName());
			helpString.append(", ");
		}
		helpString.append(players[winner.get(winner.size() - 1)].getName());
		helpString.append(" WINS!\n\n");
		System.out.println(helpString);
		for (int i = 0; i < players.length; i++) {
			int points = players[i].getPoints();
			String name = players[i].getName();
			helpString.append("Player " + (i + 1) + ": " + name + " had " + points + " points\n");
		}
		return helpString.toString();
	}

}
