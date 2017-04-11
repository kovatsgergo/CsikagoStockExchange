/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public class AIeasy extends AI {

	public AIeasy() {
		super();
	}

	@Override
	protected int[] makeMove(int position, ArrayList<String> tops, int[] prices) {
		int max = 0;
		int check = 0;
		int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % tops.size();
			int[] neighbors = new int[2];
			neighbors[0] = (n + 1) % tops.size();
			neighbors[1] = (n + tops.size() - 1) % tops.size();
			int[] gain = new int[2];
			String[] neighborsString = {tops.get(neighbors[0]), tops.get(neighbors[1])};
			int[] indexes = {GameClass.goodTypes.indexOf(neighborsString[0]),
				GameClass.goodTypes.indexOf(neighborsString[1])};
			gain[0] = prices[indexes[0]];
			gain[1] = prices[indexes[1]];
			if (gain[0] > gain[1]) {
				check = gain[0];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[0];
				}

			} else {
				check = gain[1];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[1];
				}
			}
			//System.out.println("i " + n + " check " + check + " max " + max);

		}
		//System.out.println("position " + position + "\t" + Arrays.toString(choice));
		return choice;
	}
/*
	public int[] makeMove(GameClass theGame) {//TODO: öröklődés a könyvből
		//AI thisPlayer = (AIhard)theGame.players.get(theGame.nextPlayer(theGame.actualPlayer));
		//thisPlayer.makeMove(theGame);

		int position = theGame.position;
		int max = 0;
		int check = 0;
		int[] choice = new int[2];
		for (int i = position + 1; i < 4 + position; i++) {
			int n = i % theGame.getNrCols();
			int[] neighbors = theGame.getNeighbors(n);
			int[] gain = new int[2];
			String[] neighborsString = {theGame.coloumns.get(neighbors[0]).getTop(),
				theGame.coloumns.get(neighbors[1]).getTop()};
			int[] indexes = {theGame.goodTypes.indexOf(neighborsString[0]),
				theGame.goodTypes.indexOf(neighborsString[1])};
			gain[0] = theGame.prices[indexes[0]];
			gain[1] = theGame.prices[indexes[1]];
			if (gain[0] > gain[1]) {
				check = gain[0];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[0];
				}

			} else {
				check = gain[1];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[1];
				}
			}
			//System.out.println("i " + n + " check " + check + " max " + max);

		}
		//System.out.println("position " + position + "\t" + Arrays.toString(choice));
		return choice;
	}
*/


}
