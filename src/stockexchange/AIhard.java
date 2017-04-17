/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class AIhard extends AI {

	Scanner scanner = new Scanner(System.in);
	//ArrayList<Player> players;

	public AIhard(String name) {
		super(name);
	}

	@Override
	public int[] makeMove(int position, ArrayList<String> tops, int[] colsSizes) {
		//System.out.println("AIHARD prices" + Arrays.toString(prices));
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
			int[] indexes = {GameClass.GOOD_TYPES.indexOf(neighborsString[0]),
				GameClass.GOOD_TYPES.indexOf(neighborsString[1])};
			//System.out.println("MAIN\ti: " + n + " neighbors: " + Arrays.toString(neighbors) + " " + Arrays.toString(neighborsString) + " " + Arrays.toString(indexes));
			gain[0] = prices[indexes[0]];
			gain[1] = prices[indexes[1]];
			loss[0] = countContain(goods, neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}
			ArrayList<String> nextTops = new ArrayList<>();
			int j = 0;
			while (j < tops.size()) {
				if (!(((n - 1) % tops.size() == j || (n + 1) % tops.size() == j) && colsSizes[j] == 1))
					nextTops.add(tops.get(j));
				j++;
			}
			int forecast1, forecast2;
			if (nextTops.size() < 3)
				forecast1 = forecast2 = 0;
			else {
				forecast1 = nextStepForecast(nextTops, n, -1, prices);
				forecast2 = nextStepForecast(nextTops, n, +1, prices);
			}

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
		System.out.println("THE CHOICE IS: " + Arrays.toString(choice));
//		String thing = scanner.nextLine();
//		System.out.println("\n\nHit something" + thing + "\n\n");
		return choice;
	}

//	public void setPlayers(ArrayList<Player> players) {
//		ObservedPlayer player = new ObservedPlayer(players.get(0));
//		player.getGoods();
//		//players.get(0).goods.clear();
//	}
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
		ObservedPlayer nextPlayer = observedPlayers.get((observedPlayers.indexOf(this) + 1) % observedPlayers.size());
		String lastThrown = tops.get(thrownpos);
		evilpoint = countContain(nextPlayer.getGoods(), lastThrown);

		int[] hypotPrices = new int[6];
		System.arraycopy(prices, 0, hypotPrices, 0, 6);
		--hypotPrices[GameClass.GOOD_TYPES.indexOf(lastThrown)];

		//int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % tops.size();
			int[] neighbors = new int[2];
			neighbors[0] = (n + 1) % tops.size();
			neighbors[1] = (n + tops.size() - 1) % tops.size();

			int[] gain = new int[2];
			int[] loss = new int[2];
			String[] neighborsString = {tops.get(neighbors[0]), tops.get(neighbors[1])};
			int[] indexes = {GameClass.GOOD_TYPES.indexOf(neighborsString[0]),
				GameClass.GOOD_TYPES.indexOf(neighborsString[1])};
			//System.out.println("i: " + n + " neighbors: " + Arrays.toString(neighbors) + " " + Arrays.toString(neighborsString) + " " + Arrays.toString(indexes));
			gain[0] = hypotPrices[indexes[0]];
			gain[1] = hypotPrices[indexes[1]];
			loss[0] = countContain(nextPlayer.getGoods(), neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(nextPlayer.getGoods(), neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}
			//System.out.printf("forecasted %d gain %d\tloss %d\tevilpoint: %d\n", n, gain[0], loss[0], evilpoint);

			if (gain[0] + loss[0] > gain[1] + loss[1]) {
				check = gain[0] + loss[0];
				if (check > max) {
					max = check;
				}
			} else {
				check = gain[1] + loss[1];
				if (check > max) {
					max = check;
				}
			}
		}
		//System.out.println("position " + position + "\t" + Arrays.toString(choice));
		return max - evilpoint;

	}
}
