/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public class AIeasy extends AI {

	public AIeasy(String name) {
		super(name);
	}

	@Override
	public int[] makeMove(int position, ArrayList<String> tops, int[] colsSizes) {
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
			int[] indexes = {GameClass.GOOD_TYPES.indexOf(neighborsString[0]),
				GameClass.GOOD_TYPES.indexOf(neighborsString[1])};
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
}
