/* Gergo Kovats */
package stockexchange;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GameClass {//TODO: enum vagy map

	static final ArrayList<String> GOOD_TYPES = new ArrayList(Arrays.asList(new String[]{"Wheat", "Sugar", "Coffee", "Rice", "Cocoa", "Corn"}));
	static final int[] PRICES_AT_START = {7, 6, 6, 6, 6, 6};
	static final int START_NR_COLOUMS = 9;
	ArrayList<Coloumn> coloumns = new ArrayList();
	ArrayList<Player> players = new ArrayList();
	ArrayList<String> startGoods = new ArrayList();
	ArrayList<Integer> winner;
	//Player winner;
	//ArrayList<Integer> wins = new ArrayList();

	int position = 0;

	int[] prices = {7, 6, 6, 6, 6, 6};
	boolean gameOver = false;
	int actualPlayer = 0;
	int numPlayers;
	String[] playerNames;
	int[] wins;
	final int AI_SPEED = 2000;
	int[] AIchoice = new int[2];
	boolean choiceStage = false;
	MyPanel panel;
	int lastStarter = -1;

	public GameClass(int starter, String[] playerNames, MyPanel panel) {

		numPlayers = playerNames.length;
//		for (int i = 0; i < numPlayers; i++) {
//			wins.add(0);
//		}
		wins = new int[numPlayers];
		this.playerNames = playerNames;
		this.panel = panel;

		for (int i = 0; i < numPlayers; i++) {
			String name = playerNames[i];
			if (name.length() > 2) {
				if (!(name.substring(0, 2).equals("AI"))) {
					players.add(new Player(playerNames[i]));
				} else if (name.substring(2, 3).equals("e")) {
					players.add(new AIeasy("AI " + i + " (easy)"));
				} else if (name.substring(2, 3).equals("m")) {
					players.add(new AImedium("AI " + i + " (medium)"));
				} else if (name.substring(2, 3).equals("h")) {
					players.add(new AIhard("AI " + i + " (hard)"));
				}
			} else if (name.equals("AI")) {
				players.add(new AIeasy("AI " + i + " (not set easy)"));
			} else {
				players.add(new Player(playerNames[i]));
			}
		}
		reStart();
	}

	private void dealGoods() {
		//System.out.println(startGoods.toString());
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			for (int j = 0; j < 4; j++) {
				//coloumns.get(i).add(startGoods.get(0));
				coloumns.get(i).add(startGoods.remove(0));
			}
		}
		//System.out.println(startGoods.toString());
	}

	/**
	 * Add the startGoods to the startGoods variable in type order
	 */
	private void makeGoods() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				startGoods.add(GOOD_TYPES.get(i));
			}
		}
	}

	private int getNrCols() {
		return coloumns.size();
	}

	public Coloumn getCol(int nr) {/////////////REMOVE
		return coloumns.get(nr % getNrCols());
	}

	public int[] getPossible() {
		int[] possible = new int[3];
		possible[0] = (position + 1) % getNrCols();
		possible[1] = (position + 2) % getNrCols();
		possible[2] = (position + 3) % getNrCols();
		return possible;
	}

	public int[] getNeighbors() {
		int[] neighbors = new int[2];
		neighbors[0] = (position + 1) % getNrCols();
		neighbors[1] = (position + getNrCols() - 1) % getNrCols();
		return neighbors;
	}

	/**
	 * Returns the neighbor in front [0] and the neighbor in back [1]
	 *
	 * @param position the figure's current state
	 * @return int[]
	 */
	public int[] getNeighbors(int position) {
		int[] neighbors = new int[2];
		neighbors[0] = (position + 1) % getNrCols();
		neighbors[1] = (position + getNrCols() - 1) % getNrCols();
		return neighbors;
	}

	public int[] getAIMove() {
		//return ((AI) players.get(actualPlayer)).makeMove(this);
		if (players.get(actualPlayer) instanceof AIhard)
			((AIhard) players.get(actualPlayer)).setPlayers(players);
		return ((AI) players.get(actualPlayer)).makeMove(position, getTops(), prices);
	}

	public int[] handleChoice(int keep, int out) {
		System.out.println("Handle");
		keep %= getNrCols();
		out %= getNrCols();
		int[] ret = {-1, -1};
		Coloumn actualK = coloumns.get(keep);
		Coloumn actualO = coloumns.get(out);
		int outIdx = GOOD_TYPES.indexOf(actualO.getTop());
		//int keepIdx = GOOD_TYPES.indexOf(actualK.getTop());
		players.get(actualPlayer).add(actualK.getTop());
		//System.out.println("actualK.getTop() "+actualK.getTop()+ " \t actualO.getTop()"
		//+actualO.getTop());
		actualK.remove();
		if (actualK.goods.size() == 0) {
			coloumns.remove(keep);
			panel.setNrGameCols(coloumns.size());
			ret[0] = keep;
			if (position > keep) {
				position--;
			}
			if (out > keep) {
				out--;
			}
		}
		actualO.remove();
		if (actualO.goods.size() == 0) {
			coloumns.remove(out);
			panel.setNrGameCols(coloumns.size());
			ret[1] = out;
			if (position > out) {
				position--;
			}
		}
		prices[outIdx] -= 1;
		countPoints();
		System.out.println(Arrays.toString(prices));
		if (coloumns.size() < 3) {
			gameOver = true;
		}
		actualPlayer = nextPlayer(actualPlayer);

		//System.out.printf("keepIdx: %d  outIdx: %d \t prices[keepIdx]: %d \t keep: %d\n",
		//keepIdx, outIdx, prices[keepIdx], keep);
		return ret;
	}

	/**
	 * Recalculates all players' points based on the new prices
	 */
	public void countPoints() {
		for (Player player : players) {
			int sum = 0;
			for (String good : player.goods) {
				sum += prices[GOOD_TYPES.indexOf(good)];
			}
			player.setPoints(sum);
		}
	}

	public int nextPlayer(int actual) {
		return (actual + 1) % numPlayers;
	}

	/**
	 *
	 * @return the top startGoods of each coloumn
	 */
	private ArrayList<String> getTops() {
		ArrayList<String> topGoods = new ArrayList<>();
		for (Coloumn coloumn : coloumns) {
			topGoods.add(coloumn.getTop());
		}
		return topGoods;
	}

	private int[] getColsSizes() {
		int[] colSizes = new int[coloumns.size()];
		for (int i = 0; i < coloumns.size(); i++) {
			colSizes[i] = coloumns.get(i).getHeight();
		}
		return colSizes;
	}
	/////////////////////////////////////////
	/////////////////////////////////////////

	javax.swing.Timer timerAI = new javax.swing.Timer(AI_SPEED, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			aiChoice();
		}
	});

	private void aiPoint() {
		System.out.println("aiPoint called\t actual: " + players.get(actualPlayer).getName());
		if (players.get(actualPlayer) instanceof AI && !gameOver) {
			timerAI.start();
		}
	}

	private void aiChoice() {
		System.out.println("aiChoice run");
		if (players.get(actualPlayer) instanceof AI && !gameOver) {
			int pointNr;
			if (!choiceStage) {
				System.out.println("aiChoice choiceStage");
				AIchoice = getAIMove();
				pointNr = AIchoice[0];
			} else {
				System.out.println("aiChoice stepStage");
				pointNr = AIchoice[1];
			}
			runRound(pointNr);
			//timerAI.stop();
		} else
			timerAI.stop();
	}

	public void runRound(int pointNr) {
		//System.out.println("\trunRound started\tgamover: " + theGame.gameOver + "\tcoloumns: " + theGame.coloumns.size());
		if (!gameOver) {
			if (!choiceStage) {
				System.out.println("Step Stage started");
				if (Arrays.toString(getPossible()).contains(pointNr + "")) {
					position = pointNr;
					panel.setFigure(position);
					panel.setPossible(getPossible());
					choiceStage = !choiceStage;
				}
			} else {
				System.out.println("Choice Stage started");
				int out;
				int[] emptiedColoumns;
				if (Arrays.toString(getNeighbors()).contains(pointNr + "")) {
					if (getNeighbors()[0] == pointNr) {
						choiceStage = !choiceStage;
						out = 1;
					} else {
						choiceStage = !choiceStage;
						out = 0;
					}
					//String thrownStr = coloumns.get(getNeighbors()[out]).getTop();
					//String takenStr = coloumns.get().getTop();
					emptiedColoumns = handleChoice(getNeighbors()[out], getNeighbors()[1 - out]);
					panel.makeChoice(getNeighbors()[1 - out], emptiedColoumns, prices, getTops(), getColsSizes());
				}
				timerAI.stop();
			}
			System.out.println("ai started in runhuman");
			aiPoint();
			timerAI.setDelay(AI_SPEED);
			timerAI.start();
		}
		//repaint();

		makeHints();

		if (gameOver) {
			winner = getWinner();
			boolean aiall = true;
			int i = 0;
			do {
				aiall = aiall && (players.get(i++) instanceof AI);
			} while (i < playerNames.length && aiall);
			int n;
			if (aiall) {
				n = 2;
				//n = panel.gameOverPopup(gameOverString());

			} else {
				n = panel.gameOverPopup(gameOverString());
			}

			switch (n) {
				case 2:
					reStart();
					break;
				case 1:
					JFrame temp = (JFrame) SwingUtilities.getWindowAncestor(panel);
					JPanel temppan = (JPanel) temp.getContentPane();
					JPanel temppan2 = (JPanel) temppan.getComponent(0);
					JButton tempbutt = (JButton) temppan2.getComponent(temppan2.getComponentCount() - 1);
					tempbutt.doClick(10);
					temp.setVisible(false);
					break;
				case 0:
					System.exit(0);
			}
		}
	}

	private void reStart() {
		gameOver = false;
		for (Player player : players) {
			player.goods.clear();
			player.setPoints(0);
		}
		int playAgains = playerNames.length;
		System.out.println("last starter: " + lastStarter);
		//theGame = new GameClass(playAgains, (++lastStarter) % playAgains, playerNames);
		prices = Arrays.copyOf(PRICES_AT_START, 6);
		System.out.println("RESTART prices " + Arrays.toString(prices));
		System.out.println("RESTART prices " + Arrays.toString(GameClass.PRICES_AT_START));
		coloumns.clear();
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			coloumns.add(new Coloumn());
		}
		panel.setNrGameCols(START_NR_COLOUMS);
		makeGoods();
		Collections.shuffle(startGoods);
		dealGoods();

		panel.start(getTops(), getColsSizes(), wins);
//		int nextStarter = (++lastStarter) % playAgains;
//		actualPlayer = Math.max(nextStarter, Math.min(nextStarter, numPlayers - 1));
		actualPlayer = (++lastStarter) % playAgains;
		runRound(-1);
		System.out.println("next starter: " + actualPlayer + "\n");

	}

	public void makeHints() {
		String[] returnHints = new String[playerNames.length * 2];
		Player[] hintPlayers = players.toArray(new Player[players.size()]);
		for (int i = 0; i < playerNames.length; i++) {
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
			returnHints[i * 2] = (i + 1) + stndrdth + " player " + hintPlayers[i].getName()
							+ " has " + hintPlayers[i].getPoints() + " points";
			String temp = "";
			for (String good : hintPlayers[i].goods) {
				temp += good + " (" + prices[GOOD_TYPES.indexOf(good)] + ") ";
			}
			returnHints[i * 2 + 1] = hintPlayers[i].getName() + " has Drawn Yet: " + temp;
		}
		panel.setHint(returnHints);
	}

	private ArrayList<Integer> getWinner() {
		ArrayList<Integer> returnWinner = new ArrayList();
		ArrayList<Integer> points = new ArrayList();
		for (int i = 0; i < playerNames.length; i++) {
			points.add(players.get(i).getPoints());
		}
		int max = Collections.max(points);
		for (int i = 0; i < playerNames.length; i++) {
			if (points.get(i) == max) {
				returnWinner.add(i);
				wins[i] = wins[i] + 1;
			}
		}
		System.out.println(points.toString() + "\tmax: " + max + "\t indexofit: " + points.indexOf(max) + " "
						+ returnWinner.toString() + " wins " + wins.toString());
		return returnWinner;

	}

	public String gameOverString() {
		String helpString = "";
		for (int i = 0; i < winner.size() - 1; i++) {
			helpString += players.get(winner.get(i)).getName() + ", ";
			System.out.println(helpString);
		}
		helpString += players.get(winner.get(winner.size() - 1)).getName();
		helpString += " WINS!\n\n";
		System.out.println(helpString);
		for (int i = 0; i < playerNames.length; i++) {
			int points = players.get(i).getPoints();
			String name = players.get(i).getName();
			helpString += "Player " + (i + 1) + ": " + name + " had " + points + " points\n";
		}
		return helpString;
	}

}
