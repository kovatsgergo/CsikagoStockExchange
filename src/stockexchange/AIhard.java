/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockexchange;

import java.util.ArrayList;
//import java.util.Scanner;

/**
 *
 * @author Gergo
 */
public class AIhard extends AI {

	//Scanner scanner = new Scanner(System.in);
	ArrayList<Player> players;

	public AIhard() {
		super();
	}

	@Override
	public int[] makeMove(int position, ArrayList<String> tops, int[] prices) {
		int max = -5;
		int check = 0;
		int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % tops.size();
			int[] neighbors = new int[2];
			neighbors[0] = (n + 1) % tops.size();
			neighbors[1] = (n + tops.size() - 1) % tops.size();
			int[] gain = new int[2];
			int[] loss = new int[2];
			String[] neighborsString = {tops.get(neighbors[0]), tops.get(neighbors[1])};
			int[] indexes = {GameClass.goodTypes.indexOf(neighborsString[0]),
				GameClass.goodTypes.indexOf(neighborsString[1])};
			//System.out.println("MAIN\ti: " + n + " neighbors: " + Arrays.toString(neighbors) + " " + Arrays.toString(neighborsString) + " " + Arrays.toString(indexes));
			gain[0] = prices[indexes[0]];
			gain[1] = prices[indexes[1]];
			loss[0] = countContain(goods, neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}
			int forecast1 = nextStepForecast(tops, n, -1, prices);
			int forecast2 = nextStepForecast(tops, n, +1, prices);

			if (gain[0] + loss[0] - forecast1 > gain[1] + loss[1] - forecast2) {

				//System.out.printf("MAIN\t%d gain %d\tloss %d\tnextforecast: %d\t check: %d\n", n, gain[0], loss[0], forecast1, gain[0] + loss[0] - forecast1);
				check = gain[0] + loss[0] - forecast1;
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[0];
				}
			} else {

				//System.out.printf("MAIN\t%d gain %d\tloss %d\tnextforecast: %d\t check: %d\n", n, gain[1], loss[1], forecast2, gain[1] + loss[1] - forecast2);
				check = gain[1] + loss[1] - forecast2;

				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[1];
				}
			}
		}
		//String thing = scanner.nextLine();
		//System.out.println("\n\nHit something" + thing + "\n\n");
		return choice;
	}

/*
	public int[] makeMove(GameClass theGame) {
		int position = theGame.position;
		int max = -5;
		int check = 0;
		int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % theGame.getNrCols();
			int[] neighbors = theGame.getNeighbors(n);
			int[] gain = new int[2];
			int[] loss = new int[2];
			String[] neighborsString = {theGame.coloumns.get(neighbors[0]).getTop(),
				theGame.coloumns.get(neighbors[1]).getTop()};
			int[] indexes = {theGame.goodTypes.indexOf(neighborsString[0]),
				theGame.goodTypes.indexOf(neighborsString[1])};
			//System.out.println("MAIN\ti: " + n + " neighbors: " + Arrays.toString(neighbors) + " " + Arrays.toString(neighborsString) + " " + Arrays.toString(indexes));
			gain[0] = theGame.prices[indexes[0]];
			gain[1] = theGame.prices[indexes[1]];
			loss[0] = countContain(goods, neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}
			int forecast1 = nextStepForecast(theGame, n, -1);
			int forecast2 = nextStepForecast(theGame, n, +1);

			if (gain[0] + loss[0] - forecast1 > gain[1] + loss[1] - forecast2) {

				//System.out.printf("MAIN\t%d gain %d\tloss %d\tnextforecast: %d\t check: %d\n", n, gain[0], loss[0], forecast1, gain[0] + loss[0] - forecast1);
				check = gain[0] + loss[0] - forecast1;
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[0];
				}
			} else {

				//System.out.printf("MAIN\t%d gain %d\tloss %d\tnextforecast: %d\t check: %d\n", n, gain[1], loss[1], forecast2, gain[1] + loss[1] - forecast2);
				check = gain[1] + loss[1] - forecast2;

				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[1];
				}
			}
		}
		//String thing = scanner.nextLine();
		//System.out.println("\n\nHit something" + thing + "\n\n");
		return choice;
	}
*/
	public int countContain(ArrayList<String> list, String element) {
		int i = 0;
		for (String elem : list) {
			if (elem.equals(element)) {
				i++;
			}
		}
		return i;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	/*public int nextStepForecast(GameClass theGame, int pos, int lessen) {
		int position = pos;
		int max = 0;
		int check = 0;
		int evilpoint = 0;
		int thrownpos = (pos + lessen);
		if (thrownpos == -1) {
			thrownpos = theGame.coloumns.size() - 1;
		} else {
			thrownpos = thrownpos % theGame.coloumns.size();
		}
		Player nextPlayer = players.get((players.indexOf(this) + 1) % players.size());
		String lastThrown = theGame.coloumns.get(thrownpos).getTop();
		evilpoint = countContain(nextPlayer.goods, lastThrown);

		int[] hypotPrices = new int[6];
		System.arraycopy(theGame.prices, 0, hypotPrices, 0, 6);
		--hypotPrices[theGame.goodTypes.indexOf(lastThrown)];

		//int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % theGame.getNrCols();
			int[] neighbors = theGame.getNeighbors(n);

			int[] gain = new int[2];
			int[] loss = new int[2];
			String[] neighborsString = {theGame.coloumns.get(neighbors[0]).getTop(),
				theGame.coloumns.get(neighbors[1]).getTop()};
			int[] indexes = {GameClass.goodTypes.indexOf(neighborsString[0]),
				GameClass.goodTypes.indexOf(neighborsString[1])};
			//System.out.println("i: " + n + " neighbors: " + Arrays.toString(neighbors) + " " + Arrays.toString(neighborsString) + " " + Arrays.toString(indexes));
			gain[0] = hypotPrices[indexes[0]];
			gain[1] = hypotPrices[indexes[1]];
			loss[0] = countContain(nextPlayer.goods, neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(nextPlayer.goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}
			//System.out.printf("forecasted %d gain %d\tloss %d\tevilpoint: %d\n", n, gain[0], loss[0], evilpoint);

			if (gain[0] + loss[0] > gain[1] + loss[1]) {
				check = gain[0] + loss[0];
				if (check > max) {
					max = check;
//          choice[0] = n;
//          choice[1] = neighbors[0];
				}
			} else {
				check = gain[1] + loss[1];
				if (check > max) {
					max = check;
//          choice[0] = n;
//          choice[1] = neighbors[1];
				}
			}
		}
		//System.out.println("position " + position + "\t" + Arrays.toString(choice));
		return max - evilpoint;

	}
*/
	public int nextStepForecast(ArrayList<String> tops, int pos, int lessen, int[] prices) {
		int position = pos;
		int max = 0;
		int check = 0;
		int evilpoint = 0;
		int thrownpos = (pos + lessen);
		if (thrownpos == -1) {
			thrownpos = tops.size() - 1;
		} else {
			thrownpos = thrownpos % tops.size();
		}
		Player nextPlayer = players.get((players.indexOf(this) + 1) % players.size());
		String lastThrown = tops.get(thrownpos);
		evilpoint = countContain(nextPlayer.goods, lastThrown);

		int[] hypotPrices = new int[6];
		System.arraycopy(prices, 0, hypotPrices, 0, 6);
		--hypotPrices[GameClass.goodTypes.indexOf(lastThrown)];

		//int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % tops.size();
			int[] neighbors = new int[2];
			neighbors[0] = (n + 1) % tops.size();
			neighbors[1] = (n + tops.size() - 1) % tops.size();

			int[] gain = new int[2];
			int[] loss = new int[2];
			String[] neighborsString = {tops.get(neighbors[0]), tops.get(neighbors[1])};
			int[] indexes = {GameClass.goodTypes.indexOf(neighborsString[0]),
				GameClass.goodTypes.indexOf(neighborsString[1])};
			//System.out.println("i: " + n + " neighbors: " + Arrays.toString(neighbors) + " " + Arrays.toString(neighborsString) + " " + Arrays.toString(indexes));
			gain[0] = hypotPrices[indexes[0]];
			gain[1] = hypotPrices[indexes[1]];
			loss[0] = countContain(nextPlayer.goods, neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(nextPlayer.goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}
			//System.out.printf("forecasted %d gain %d\tloss %d\tevilpoint: %d\n", n, gain[0], loss[0], evilpoint);

			if (gain[0] + loss[0] > gain[1] + loss[1]) {
				check = gain[0] + loss[0];
				if (check > max) {
					max = check;
//          choice[0] = n;
//          choice[1] = neighbors[0];
				}
			} else {
				check = gain[1] + loss[1];
				if (check > max) {
					max = check;
//          choice[0] = n;
//          choice[1] = neighbors[1];
				}
			}
		}
		//System.out.println("position " + position + "\t" + Arrays.toString(choice));
		return max - evilpoint;

	}
}
