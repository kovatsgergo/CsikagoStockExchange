package stockexchange;

/* Gergo Kovats */
import java.util.ArrayList;

public class AImedium extends AI {

	public AImedium(String name) {
		super(name);
	}
	
	@Override
	public int[] makeMove(int position, ArrayList<Commodity> tops, int[] colsSizes) {
		int[][] gains = getGains(position, tops);
//		System.out.println("gains " + Arrays.deepToString(gains));
		int[][] losses = getLosses(position, tops, commodities);
//		System.out.println("losses " + Arrays.deepToString(losses));
		int[] choice = getMaxIndex(matrixAddition(gains, losses));
//		System.out.println("array choice " + Arrays.toString(choice));
		convertChoiceToPosition(choice, position, tops.size());
//		System.out.println("choice " + Arrays.toString(choice));
		//waitForKey();
		return choice;
	}
}
