package stockexchange.model;

/* Gergo Kovats */

import java.io.Serializable;
import java.util.ArrayList;

public class AIeasy extends AI implements Serializable{

	public AIeasy(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "AIeasy{" +super.toString()+ '}';
	}
	
	
	
	@Override
	public int[] makeMove(int position, ArrayList<Commodity> tops, int[] colsSizes) {
		int[][] gains = getGains(position, tops);
//		System.out.println("gains " + Arrays.deepToString(gains));
		int[] choice = getMaxIndex(gains);
//		System.out.println("array choice " + Arrays.toString(choice));
		convertChoiceToPosition(choice, position, tops.size());
//		System.out.println("choice " + Arrays.toString(choice));
		//waitForKey();
		return choice;
	}
}
