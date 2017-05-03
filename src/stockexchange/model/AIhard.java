package stockexchange.model;

/* Gergo Kovats */
import java.io.Serializable;
import java.util.ArrayList;
//import java.util.Scanner;

public class AIhard extends AI implements Serializable{

//	Scanner scanner = new Scanner(System.in);
	//ArrayList<Player> players;

	public AIhard(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "AIhard{" +super.toString()+ '}';
	}
	

	@Override
	public int[] makeMove(int position, ArrayList<Commodity> tops, int[] colsSizes) {
		int[][] gains = getGains(position, tops);
		//System.out.println("gains " + Arrays.deepToString(gains));
		int[][] losses = getLosses(position, tops, commodities);
		int[][] forecasts = getForecasts(position, tops, colsSizes);
		//System.out.println("losses " + Arrays.deepToString(losses));
		int[] choice = getMaxIndex(matrixAddition(matrixAddition(gains, losses), forecasts));
		//System.out.println("array choice " + Arrays.toString(choice));
		convertChoiceToPosition(choice, position, tops.size());
		//System.out.println("choice " + Arrays.toString(choice));
		//waitForKey();
		return choice;
	}
}
