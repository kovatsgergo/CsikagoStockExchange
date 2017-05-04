package stockexchange.model;

/* Gergo Kovats */
import java.util.ArrayList;
import stockexchange.GuiModelInterface;

public abstract class AI extends Player {

	protected ArrayList<ObservedPlayer> observedPlayers;
	private static GuiModelInterface iGuiModel;

	public void setObservedNextPlayer(ObservedPlayer observedPlayer) {

	}

	public AI(String name) {
		super(name);
	}

	protected abstract int[] makeMove(int position, ArrayList<Commodity> tops, int[] colsSizes);

	public int[] makeMove() {
		observedPlayers = iGuiModel.makeObservedOtherPlayers();
		return makeMove(iGuiModel.getPosition(), iGuiModel.getTopCommodities(), iGuiModel.getColsSizes());
	}

	protected static void setModelInterface(GuiModelInterface GuiModelint) {
		iGuiModel = GuiModelint;
	}

	//Used by Medium and Hard
	public int countContain(ArrayList<Commodity> list, Commodity element) {
		int i = 0;
		for (Commodity elem : list) {
			if (elem.equals(element)) {
				i++;
			}
		}
		return i;
	}

	// Used by all
	protected int[][] getGains(int position, ArrayList<Commodity> tops) {
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
	protected int[][] getLosses(int position, ArrayList<Commodity> tops, ArrayList<Commodity> commodities) {
		int[][] losses = new int[3][2];
		for (int i = 0; i < 3; i++) {
			int[] neighbors = getNeighbors(position + (i + 1), tops.size());
			//String[] neighborsString = {tops.get(neighbors[0]), tops.get(neighbors[1])};
			losses[i][0] = countContain(commodities, tops.get(neighbors[1])) * -1;
			losses[i][1] = countContain(commodities, tops.get(neighbors[0])) * -1;
			if (tops.get(neighbors[0]).equals(tops.get(neighbors[1]))) {
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
	private int getPriceAtPosition(int position, ArrayList<Commodity> tops) {
		return tops.get(position).getPrice();
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

//	protected void waitForKey() {
//		java.util.Scanner sc = new java.util.Scanner(System.in);
//		System.out.println("enter to continue");
//		String anything = sc.nextLine();
//	}
	protected int[][] getForecasts(int position, ArrayList<Commodity> tops, int[] colsSizes) {
		int[][] forecasts = new int[3][2];
		for (int i = 0; i < 3; i++) {
			int n = (i + position + 1) % tops.size();
			ArrayList<Commodity> nextTops = new ArrayList<>();
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
				forecasts[i][0] = -((AIhard) this).nextStepForecast(nextTops, n, -1);
				forecasts[i][1] = -((AIhard) this).nextStepForecast(nextTops, n, +1);
			}
		}
		//System.out.println("forecasts: " + Arrays.deepToString(forecasts));
		return forecasts;
	}

	protected int nextStepForecast(ArrayList<Commodity> tops, int position, int lessen) {
		//evilpoint: the loss of the next player
		//if the actual would throw a commodity type which the next has
		int evilpoint = 0;
		int thrownpos = (position + lessen);
		if (thrownpos == -1) {
			thrownpos = tops.size() - 1;
		} else {
			thrownpos = thrownpos % tops.size();
		}
		Commodity lastThrown = tops.get(thrownpos);
		for (ObservedPlayer observedPlayer : observedPlayers) {
			evilpoint += countContain(observedPlayer.getCommodities(), lastThrown);
		}
		//calculate the hypothetic price list after the hypothetic choice
//		int[] hypotPrices = new int[GameClass.COMMODITY_TYPES.length];
//		for (int i = 0; i < GameClass.COMMODITY_TYPES.length; i++) {
//			hypotPrices[i] = (lastThrown.equals(GameClass.COMMODITY_TYPES[i]))
//							? lastThrown.getPrice() - 1 : GameClass.COMMODITY_TYPES[i].getPrice();
//		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		int max = getMakeMoveMax(tops, position, lastThrown);
		//System.out.println("evilpoint " + evilpoint + "max " + max);
		return max - evilpoint;
	}

	//Used by Hard to estimate the best choice of next player
	protected int getMakeMoveMax(ArrayList<Commodity> tops, int position, Commodity lastThrown) {
		//System.out.println("\ngetMakeMoveMax " + position);
		int[][] gains = getGains(position, tops);
		//System.out.println("gains " + Arrays.deepToString(gains));
		int[][] losses = getLosses(position, tops, observedPlayers.get(0).getCommodities());
		//System.out.println("losses " + Arrays.deepToString(losses));
		int maximum = getMaxIndex(matrixAddition(gains, losses), true);
		//System.out.println("maximum " + maximum);
		//waitForKey();
		return maximum;
	}

	protected int[][] getGains(int position, ArrayList<Commodity> tops, Commodity lastThrown) {
		int[][] gains = new int[3][2];
		for (int i = 0; i < 3; i++) {
			int[] neighbors = getNeighbors(position + (i + 1), tops.size());
			gains[i][0] = getPriceAtPosition(neighbors[0], tops, lastThrown);
			gains[i][1] = getPriceAtPosition(neighbors[1], tops, lastThrown);
		}
		//System.out.println("gains: " + Arrays.deepToString(gains));
		return gains;
	}

	private int getPriceAtPosition(int position, ArrayList<Commodity> tops, Commodity lastThrown) {
		Commodity commAtPos = tops.get(position);
		return (lastThrown.equals(commAtPos)) ? commAtPos.getPrice() - 1 : commAtPos.getPrice();
	}

}
