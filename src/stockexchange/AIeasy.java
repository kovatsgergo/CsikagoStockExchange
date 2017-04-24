package stockexchange;

/* Gergo Kovats */

import java.util.ArrayList;

public class AIeasy extends AI {

	public AIeasy(String name) {
		super(name);
	}
	
	@Override
	public int[] makeMove(int position, ArrayList<String> tops, int[] colsSizes) {
		int[][] gains = getGains(position, tops);
//		System.out.println("gains " + Arrays.deepToString(gains));
		int[] choice = getMaxIndex(gains);
//		System.out.println("array choice " + Arrays.toString(choice));
//		choice[0] = (choice[0] + position + 1) % tops.size();
//		choice[1] = getNeighbors(choice[0], tops.size())[choice[1]];
		convertChoiceToPosition(choice, position, tops.size());
//		System.out.println("choice " + Arrays.toString(choice));
		//waitForKey();
		return choice;
	}
}
