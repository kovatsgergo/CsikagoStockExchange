package stockexchange.model;

/* Gergo Kovats */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import stockexchange.GamePanelInterface;
import stockexchange.GameSaver;

public class Model {

	public static final Commodity[] COMMODITY_TYPES = new Commodity[]{new Wheat(), new Sugar(), new Coffee(), new Rice(), new Cocoa(), new Corn()};
	public static final int START_NR_COLOUMS = 9;
	public static final int START_HEIGHT_COLOUMNS = 4; //(START_NR_COLOUMS * START_HEIGHT_COLOUMNS)%COMMODITY_TYPES.size()=0

	final int AI_SPEED = 500;
	private int[] AIchoice = new int[2];

	private ArrayList<Player> players;
	private ArrayList<Column> columns = new ArrayList();
	private ArrayList<Commodity> startCommodities = new ArrayList();

	private ArrayList<Integer> wins;//number of win points of all players
	private boolean gameOver = false;//is it game over
	private boolean choiceStage = false;// is it the choice stage
	private int lastStarter = -1;//the player('s index, who started the last round)
	private int actualPlayer;//the player whose turn is is
	private int position = 0;//the figure's current position

	private ArrayList<Object> propertiesToSave;
	GamePanelInterface gamePanel;

	public Model(int starter, Player[] players, GamePanelInterface gamePanel) {
		this.players = new ArrayList<Player>(Arrays.asList(players));
		for (Player player : players) {
			System.out.println(player.getClass().getSimpleName());
		}
		//System.out.println("constructor players: " + Arrays.toString(players));
		wins = new ArrayList<>();
		for (Player player : players) {
			wins.add(0);
		}
		reStart();
		propertiesToSave = new ArrayList<>();
		this.gamePanel = gamePanel;
	}

	public void save() {
		propertiesToSave.clear();
		propertiesToSave.add(gameOver);//0
		propertiesToSave.add(choiceStage);//1
		propertiesToSave.add(lastStarter);//2
		propertiesToSave.add(actualPlayer);//3
		propertiesToSave.add(wins);//4
		propertiesToSave.add(position);//5
		propertiesToSave.add(players.size());//6
		for (Player player : players) {
			propertiesToSave.add(player);
		}
		propertiesToSave.add(columns.size());
		for (Column column : columns) {
			propertiesToSave.add(column);
		}
		propertiesToSave.add(getPriceArray());
		System.out.println(propertiesToSave.toString());
		new GameSaver(propertiesToSave, true);
	}

	public void load() {
		new GameSaver(propertiesToSave, false);
		if (propertiesToSave != null) {
			for (int i = 0; i < propertiesToSave.size(); i++) {
				System.out.println(i + ": " + propertiesToSave.get(i).toString());
			}

			System.out.println(propertiesToSave.toString());
			gameOver = (boolean) propertiesToSave.get(0);
			choiceStage = (boolean) propertiesToSave.get(1);
			lastStarter = (int) propertiesToSave.get(2);
			actualPlayer = (int) propertiesToSave.get(3);
			position = (int) propertiesToSave.get(5);
			ArrayList<Object> playersX = new ArrayList<>();
			int n = (int) propertiesToSave.get(6);
			for (int i = 7; i < 7 + n; i++) {
				System.out.println("players: " + i);
				playersX.add((Object) propertiesToSave.get(i));
			}
			//columns = new ArrayList<Column>();
			ArrayList<Object> columnsX = new ArrayList<>();
			int j = 7 + n;
			int m = (int) propertiesToSave.get(j);
			int i = j + 1;
			for (; i < j + m + 1; i++) {
				System.out.println("coloumns: " + i);
				columnsX.add((Object) propertiesToSave.get(i));
			}
			columns.clear();
			for (Object object : columnsX)
				columns.add((Column) object);
			players.clear();
			for (Object object : playersX)
				players.add((Player) object);
//			System.out.println("columns after "+columns.toString());
//			System.out.println("players after "+players.toString());	

//			for (Column column : columns) {
//				System.out.println("top: "+column.getTop());
//			}
			wins = (ArrayList<Integer>) propertiesToSave.get(4);
			ArrayList<Integer> tempPrices = (ArrayList<Integer>) propertiesToSave.get(i);
			priceSetter(tempPrices);
			gamePanel.start(choiceStage, position, actualPlayer);
		}
	}

	private void priceSetter(ArrayList<Integer> priceArray) {
		for (int i = 0; i < priceArray.size(); i++) {
			COMMODITY_TYPES[i].resetPrice();
			int diff = COMMODITY_TYPES[i].getPrice() - priceArray.get(i);
			for (int j = 0; j < diff; j++) {
				//System.out.println(COMMODITY_TYPES[i]+" lowered");
				COMMODITY_TYPES[i].lowerPrice();
			}
			//System.out.println(COMMODITY_TYPES[i].toString());
		}
	}

	public ArrayList<Integer> getPriceArray() {
		ArrayList<Integer> prices = new ArrayList<>();
		for (int i = 0; i < COMMODITY_TYPES.length; i++) {
			prices.add(COMMODITY_TYPES[i].getPrice());
		}
		return prices;
	}
	
	public boolean getChoiceStage(){
		return choiceStage;
	}
	
	public void changeStage(){
		choiceStage = !choiceStage;
	}

	/**
	 * Deal all the commodities to the columns
	 */
	private void dealGoods() {
		//System.out.println(startGoods.toString());
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			for (int j = 0; j < START_HEIGHT_COLOUMNS; j++) {
				//columns.get(i).add(startGoods.get(0));
				columns.get(i).add(startCommodities.remove(0));
			}
		}
		//System.out.println(startGoods.toString());
	}

	/**
	 * Add the startGoods to the startGoods variable in type order
	 */
	private void makeGoods() {
		for (int i = 0; i < COMMODITY_TYPES.length; i++) {
			COMMODITY_TYPES[i].resetPrice();
			for (int j = 0; j < (START_HEIGHT_COLOUMNS * START_NR_COLOUMS) / COMMODITY_TYPES.length; j++) {
				startCommodities.add(COMMODITY_TYPES[i]);
			}
		}
	}

	public String[] getAllNames() {
		String[] allNames = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			allNames[i] = players.get(i).getName();
		}
		return allNames;
	}

	public boolean isAllPlayersAI() {
		int i = 0;
		boolean isAllAi = false;
		do {
			isAllAi = isAllAi && (players.get(i++) instanceof AI);
		} while (i < players.size() && isAllAi);
		return isAllAi;
	}

	public int getNrOfCols() {
		return columns.size();
	}

	public int[] getWins() {
		int[] retWins = new int[players.size()];
		for (int i = 0; i < wins.size(); i++) {
			retWins[i] = wins.get(i);
		}
		return retWins;
	}

	public ArrayList<Integer> getPossible() {
		ArrayList<Integer> possible = new ArrayList<>();
		possible.add((position + 1) % getNrOfCols());
		possible.add((position + 2) % getNrOfCols());
		possible.add((position + 3) % getNrOfCols());
		return possible;
	}

	public ArrayList<Integer> getNeighbors() {
		ArrayList<Integer> neighbors = new ArrayList<>();
		neighbors.add((position + 1) % getNrOfCols());
		neighbors.add((position + getNrOfCols() - 1) % getNrOfCols());
		return neighbors;
	}

	public Player getActualPlayer() {
		return players.get(actualPlayer);
	}

	public ObservedPlayer makeObservedNextPlayers() {
		return new ObservedPlayer(players.get(nextPlayer(actualPlayer)));
	}

	public void setPosition(int position) {
		if (!choiceStage && getPossible().contains(position))
			this.position = position;

	}

	/**
	 * Handle the consequences of a choice phase: removes commodities, empty
	 * columns
	 *
	 * @param kept
	 * @param sold
	 * @return
	 */
	public int[] handleChoice(int kept, int sold) {
		//System.out.println("Handle");
		kept %= getNrOfCols();
		sold %= getNrOfCols();
		int[] ret = {-1, -1};
		Column actualKept = columns.get(kept);
		Column actualSold = columns.get(sold);
//		int outIdx = COMMODITY_TYPES.indexOf(actualSold.getTop());

		actualSold.getTop().lowerPrice();
		players.get(actualPlayer).add(actualKept.getTop());
		//System.out.println("actualK.getTop() "+actualK.getTop()+ " \t actualO.getTop()"
		actualKept.remove();
		if (actualKept.goods.isEmpty()) {
			columns.remove(kept);
			ret[0] = kept;
			if (position > kept) {
				position--;
			}
			if (sold > kept) {
				sold--;
			}
		}
		actualSold.remove();
		if (actualSold.goods.isEmpty()) {
			columns.remove(sold);
			ret[1] = sold;
			if (position > sold) {
				position--;
			}
		}
		countPoints();
		actualPlayer = nextPlayer(actualPlayer);
		return ret;
	}

	/**
	 * Recalculates points of all players based on the new prices
	 */
	private void countPoints() {
		for (Player player : players) {
			player.recalcPoints();
		}
	}

	private int nextPlayer(int actual) {
		return (actual + 1) % players.size();
	}

	public int getPosition() {
		return position;
	}

	/**
	 *
	 * @return the top startGoods of each column
	 */
	public ArrayList<Commodity> getTopCommodities() {
		ArrayList<Commodity> topGoods = new ArrayList<>();
		for (Column column : columns) {
			topGoods.add(column.getTop());
		}
		return topGoods;
	}

	public int[] getColsSizes() {
		int[] colSizes = new int[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			colSizes[i] = columns.get(i).getHeight();
		}
		return colSizes;
	}

	/**
	 * Initialization
	 */
	public void reStart() {
		gameOver = false;
		//prices = Arrays.copyOf(PRICES_AT_START, prices.length);
		for (Player player : players) {
			//System.out.println(player);
			player.commodities.clear();
			//player.setPrices(prices);
		}

		//System.out.println("last starter: " + lastStarter);
		columns.clear();
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			columns.add(new Column());
		}
		makeGoods();
		Collections.shuffle(startCommodities);
		dealGoods();
//		int nextStarter = (++lastStarter) % playAgains;
//		actualPlayer = Math.max(nextStarter, Math.min(nextStarter, numPlayers - 1));
		actualPlayer = nextPlayer(lastStarter++);
		position = 0;
		//System.out.println("next starter: " + actualPlayer + "\n");
	}

	public String[] makeHints() {
		String[] returnHints = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			StringBuffer temp = new StringBuffer("");
			for (Commodity commodity : players.get(i).commodities) {
				temp.append(commodity.toString(true));
			}
			returnHints[i] = players.get(i).getName() + " has " + players.get(i).getPoints() + " points." + " Kept: " + temp.toString();
		}
		return returnHints;
	}

	public ArrayList<Integer> getWinner() {
		ArrayList<Integer> returnWinner = new ArrayList();
		ArrayList<Integer> points = new ArrayList();
		for (int i = 0; i < players.size(); i++) {
			points.add(players.get(i).getPoints());
		}
		int max = Collections.max(points);
		for (int i = 0; i < players.size(); i++) {
			if (points.get(i) == max) {
				returnWinner.add(i);
				wins.set(i, wins.get(i) + 1);
			}
		}
		System.out.println(points.toString() + "\tmax: " + max + "\t indexofit: " + points.indexOf(max) + " "
						+ returnWinner.toString() + " wins " + wins.toString());
		return returnWinner;

	}

	public String gameOverString(ArrayList<Integer> winner) {
		StringBuffer helpString = new StringBuffer("");
		for (int i = 0; i < winner.size() - 1; i++) {
			helpString.append(players.get(winner.get(i)).getName());
			helpString.append(", ");
		}
		helpString.append(players.get(winner.get(winner.size() - 1)).getName());
		helpString.append(" WINS!\n\n");
		System.out.println(helpString);
		for (int i = 0; i < players.size(); i++) {
			int points = players.get(i).getPoints();
			String name = players.get(i).getName();
			helpString.append("Player " + (i + 1) + ": " + name + " had " + points + " points\n");
		}
		return helpString.toString();
	}

}
