/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public class AImedium extends AI {

	public AImedium() {
		super();
	}

	@Override
	public int[] makeMove(int position, ArrayList<String> tops, int[] prices) {
		int max = 0;
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
			gain[0] = prices[indexes[0]];
			gain[1] = prices[indexes[1]];
			loss[0] = countContain(goods, neighborsString[1]) * -1;//Better than Easy
			loss[1] = countContain(goods, neighborsString[0]) * -1;//Counts the consequence of the thrown good
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}

			if (gain[0] + loss[0] > gain[1] + loss[1]) {
				check = gain[0] + loss[0];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[0];
				}
			} else {
				check = gain[1] + loss[1];
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
	{
/*
	public int[] makeMove(GameClass theGame) {
		int position = theGame.position;
		int max = 0;
		int check = 0;
		int[] choice = new int[2];
		for (int i = position + 1; i < position + 4; i++) {
			int n = i % theGame.getNrCols();
			int[] neighbors = theGame.getNeighbors(n);
			//System.out.println("i: " + n + " neighbors: " + Arrays.toString(neighbors));
			int[] gain = new int[2];
			int[] loss = new int[2];
			String[] neighborsString = {theGame.coloumns.get(neighbors[0]).getTop(),
				theGame.coloumns.get(neighbors[1]).getTop()};
			int[] indexes = {theGame.goodTypes.indexOf(neighborsString[0]),
				theGame.goodTypes.indexOf(neighborsString[1])};

			gain[0] = theGame.prices[indexes[0]];
			gain[1] = theGame.prices[indexes[1]];
			loss[0] = countContain(goods, neighborsString[1]) * -1; // opposite as gain!
			loss[1] = countContain(goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				loss[0] -= 1;
				loss[1] -= 1;
			}

			if (gain[0] + loss[0] > gain[1] + loss[1]) {
				check = gain[0] + loss[0];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[0];
				}
			} else {
				check = gain[1] + loss[1];
				if (check > max) {
					max = check;
					choice[0] = n;
					choice[1] = neighbors[1];
				}
			}
//			System.out.println("gain0 " + gain[0] + " loss0 " + loss[0]
//                    + " | gain1 " + gain[1] + " loss1 " + loss[1]);
//            System.out.println("i " + n + " check " + check + " max " + max);

		}
		//System.out.println("position " + position + "\t" + Arrays.toString(choice));
		return choice;
	}
*/
	}
	public int countContain(ArrayList<String> list, String element) {
		int i = 0;
		for (String elem : list) {
			if (elem.equals(element)) {
				i++;
			}
		}
		return i;
	}

}
