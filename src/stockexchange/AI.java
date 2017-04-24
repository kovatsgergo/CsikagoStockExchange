package stockexchange;

/* Gergo Kovats */
import java.util.ArrayList;
import java.util.Scanner;

public abstract class AI extends Player {

	protected ObservedPlayer observedPlayer;

	public void setObservedNextPlayer(ObservedPlayer observedPlayer) {
		this.observedPlayer = observedPlayer;
	}

	public AI(String name) {
		super(name);
	}

	abstract int[] makeMove(int position, ArrayList<String> tops, int[] colsSizes);

	//Used by Medium and Hard
	public int countContain(ArrayList<String> list, String element) {
		int i = 0;
		for (String elem : list) {
			if (elem.equals(element)) {
				i++;
			}
		}
		return i;
	}

	// Used by all
	protected int[][] getGains(int position, ArrayList<String> tops) {
		int[][] gains = new int[3][2];
		for (int i = 0; i < 3; i++) {
			int[] neighbors = getNeighbors(position + (i + 1), tops.size());
			gains[i][0] = getPriceAtPosition(neighbors[0], tops);
			gains[i][1] = getPriceAtPosition(neighbors[1], tops);
		}
		//System.out.println("gains: " + Arrays.deepToString(gains));
		return gains;
	}

	// Used by Medium and Hard
	protected int[][] getLosses(int position, ArrayList<String> tops, ArrayList<String> goods) {
		int[][] losses = new int[3][2];
		for (int i = 0; i < 3; i++) {
			int[] neighbors = getNeighbors(position + (i + 1), tops.size());
			String[] neighborsString = {tops.get(neighbors[0]), tops.get(neighbors[1])};
			losses[i][0] = countContain(goods, neighborsString[1]) * -1;
			losses[i][1] = countContain(goods, neighborsString[0]) * -1;
			if (neighborsString[0].equals(neighborsString[1])) {
				losses[i][0] -= 1;
				losses[i][1] -= 1;
			}
		}
		//System.out.println("losses: " + Arrays.deepToString(losses));
		return losses;
	}

	//Used by Medium and Hard
	protected int[][] matrixAddition(int[][] a, int[][] b) {
		int[][] addedMatrix = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				addedMatrix[i][j] = a[i][j] + b[i][j];
			}
		}
		return addedMatrix;
	}

	// Used by all
	protected int[] getNeighbors(int position, int topsSize) {
		int n = position % topsSize;
		return new int[]{(n + 1) % topsSize, (n - 1 + topsSize) % topsSize};
	}

	// Used by all
	private int getPriceAtPosition(int position, ArrayList<String> tops) {
		String type = tops.get(position);
		int priceIndex = GameClass.GOOD_TYPES.indexOf(type);
		return prices[priceIndex];
	}

	// Used by all
	protected int[] getMaxIndex(int[][] array) {
		int max = array[0][0];
		int[] maxIndex = {0, 0};
		for (int i = 0; i < array.length; i++)
			for (int j = 0; j < array[0].length; j++)
				if (max < array[i][j]) {
					maxIndex[0] = i;
					maxIndex[1] = j;
					max = array[i][j];
				}
		return maxIndex;
	}

	// Used by all
	protected int getMaxIndex(int[][] array, boolean maxonly) {
			int max = array[0][0];
			int[] maxIndex = {0, 0};
			for (int i = 0; i < array.length; i++)
				for (int j = 0; j < array[0].length; j++)
					if (max < array[i][j]) {
						maxIndex[0] = i;
						maxIndex[1] = j;
						max = array[i][j];
					}
			return max;
	}

	// Used by all
	protected void convertChoiceToPosition(int[] choice, int position, int topsSize) {
		choice[0] = (choice[0] + position + 1) % topsSize;
		choice[1] = getNeighbors(choice[0], topsSize)[choice[1]];
	}

	protected void waitForKey() {
		Scanner sc = new Scanner(System.in);
		System.out.println("enter to continue");
		String anything = sc.nextLine();
	}

	protected int[][] getForecasts(int position, ArrayList<String> tops, int[] colsSizes) {
		int[][] forecasts = new int[3][2];
		for (int i = 0; i < 3; i++) {
			int n = (i + position + 1) % tops.size();
			ArrayList<String> nextTops = new ArrayList<>();
			int j = 0;
			while (j < tops.size()) {
				//if the coloumn is not emptied after choice
				if (!(((n - 1) % tops.size() == j || (n + 1) % tops.size() == j) && colsSizes[j] == 1))
					nextTops.add(tops.get(j));
				j++;
			}
			//System.out.println("n:" + n + "\t" + nextTops);

			if (nextTops.size() < 3)
				forecasts[i][0] = forecasts[i][1] = 0;
			else {
				forecasts[i][0] = -((AIhard) this).nextStepForecast(nextTops, n, -1, prices);
				forecasts[i][1] = -((AIhard) this).nextStepForecast(nextTops, n, +1, prices);
			}
		}
		//System.out.println("forecasts: " + Arrays.deepToString(forecasts));
		return forecasts;
	}

}
