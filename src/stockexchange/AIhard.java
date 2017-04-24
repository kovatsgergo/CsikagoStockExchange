package stockexchange;

/* Gergo Kovats */
import java.util.ArrayList;
import java.util.Scanner;

public class AIhard extends AI {

	Scanner scanner = new Scanner(System.in);
	//ArrayList<Player> players;

	public AIhard(String name) {
		super(name);
	}

	@Override
	public int[] makeMove(int position, ArrayList<String> tops, int[] colsSizes) {
		int[][] gains = getGains(position, tops);
//		System.out.println("gains " + Arrays.deepToString(gains));
		int[][] losses = getLosses(position, tops, goods);
		int[][] forecasts = getForecasts(position, tops, colsSizes);
//		System.out.println("losses " + Arrays.deepToString(losses));
		int[] choice = getMaxIndex(matrixAddition(matrixAddition(gains, losses), forecasts));
//		System.out.println("array choice " + Arrays.toString(choice));
		convertChoiceToPosition(choice, position, tops.size());
//		System.out.println("choice " + Arrays.toString(choice));
		//waitForKey();
		return choice;
	}

	public int nextStepForecast(ArrayList<String> tops, int position, int lessen, int[] prices) {
		//evilpoint = the loss of the next player
		//if the actual would throw a good type which the next has
		int evilpoint = 0;
		int thrownpos = (position + lessen);
		if (thrownpos == -1) {
			thrownpos = tops.size() - 1;
		} else {
			thrownpos = thrownpos % tops.size();
		}
		String lastThrown = tops.get(thrownpos);
		evilpoint = countContain(observedPlayer.getGoods(), lastThrown);
		//calculate the hypothetic price list after the hypothetic choice
		int[] hypotPrices = new int[6];
		System.arraycopy(prices, 0, hypotPrices, 0, 6);
		--hypotPrices[GameClass.GOOD_TYPES.indexOf(lastThrown)];
		int max = getMakeMoveMax(tops, position);
		//System.out.println("evilpoint " + evilpoint + "max " + max);
		return max - evilpoint;
	}

	public int getMakeMoveMax(ArrayList<String> tops, int position) {
		//System.out.println("getMakeMoveMax " + position);
		int[][] gains = getGains(position, tops);
//		System.out.println("gains " + Arrays.deepToString(gains));
		int[][] losses = getLosses(position, tops, goods);
//		System.out.println("losses " + Arrays.deepToString(losses));
		int maximum = getMaxIndex(matrixAddition(gains, losses), true);
//		System.out.println("maximum " + maximum);
		//waitForKey();
		return maximum;
	}
}
