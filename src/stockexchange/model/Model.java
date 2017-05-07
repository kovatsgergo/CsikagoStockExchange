package stockexchange.model;

/* Gergo Kovats */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import stockexchange.ControlModelInterface;
import stockexchange.GuiModelInterface;

public class Model implements ControlModelInterface, GuiModelInterface {

	public static final int MAX_NR_PLAYERS = 4;
	public static final Commodity[] COMMODITY_TYPES = new Commodity[]{new Wheat(), new Sugar(), new Coffee(), new Rice(), new Cocoa(), new Corn()};
	public static final int START_NR_COLOUMS = 9;
	public static final int START_HEIGHT_COLOUMNS = 4; //(START_NR_COLOUMS * START_HEIGHT_COLOUMNS)%COMMODITY_TYPES.size()=0

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
	private int lastSoldColumn;

	private boolean expert = false;

	private ArrayList<Object> propertiesToSave;
	//ControlModelInterface gamePanel;

	public Model(String[][] players) {
		this.players = createPlayers(players);
		for (Player player : this.players) {
			System.out.println(player.getClass().getSimpleName());
		}
		//System.out.println("constructor players: " + Arrays.getNameAndPrice(players));
		wins = new ArrayList<>();
		for (Player player : this.players) {
			wins.add(0);
		}
		//reStart();
		propertiesToSave = new ArrayList<>();
	}

	private ArrayList<Player> createPlayers(String[][] players) {
		System.out.println("players at createPlayers: " + Arrays.deepToString(players));
		ArrayList<Player> playerArray = new ArrayList<>();
		//boolean isThereAnyAI = false;
		for (int i = 0; i < players.length; i++) {
			if (players[i][1].equals("human"))
				playerArray.add(new Player(players[i][0]));
			else {
				//isThereAnyAI = true;
				switch (players[i][0]) {
					case "Easy":
						playerArray.add(new AIeasy("AI " + (i + 1) + " (easy)"));
						break;
					case "Medium":
						playerArray.add(new AImedium("AI " + (i + 1) + " (medium)"));
						break;
					case "Hard":
						playerArray.add(new AIhard("AI " + (i + 1) + " (hard)"));
						break;
				}
			}
			GuiModelInterface iGuiModel = this;
			AI.setModelInterface(iGuiModel);
		}
		return playerArray;
	}

	@Override //from ControlModelInterface
	public void save(String pathName) {
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
//		System.out.println(propertiesToSave.getNameAndPrice());
		GameSaver.save(propertiesToSave, pathName);
	}

	public boolean load(String pathName) {
		GameSaver.load(propertiesToSave, pathName);
		boolean success = false;
//		for (int i = 0; i < propertiesToSave.size(); i++) {
//			System.out.println(i + ": " + propertiesToSave.get(i).getNameAndPrice());
//		}
		if (propertiesToSave != null /*& isStructured(propertiesToSave)*/) {
			gameOver = (boolean) propertiesToSave.get(0);
			choiceStage = (boolean) propertiesToSave.get(1);
			lastStarter = (int) propertiesToSave.get(2);
			actualPlayer = (int) propertiesToSave.get(3);
			position = (int) propertiesToSave.get(5);
			ArrayList<Object> playersX = new ArrayList<>();
			int n = (int) propertiesToSave.get(6);
			for (int i = 7; i < 7 + n; i++) {
				//System.out.println("players: " + i);
				playersX.add((Object) propertiesToSave.get(i));
			}
			ArrayList<Object> columnsX = new ArrayList<>();
			int j = 7 + n;
			int m = (int) propertiesToSave.get(j);
			int i = j + 1;
			for (; i < j + m + 1; i++) {
				//System.out.println("coloumns: " + i);
				columnsX.add((Object) propertiesToSave.get(i));
			}
			columns.clear();
			for (Object object : columnsX)
				columns.add((Column) object);
			players.clear();
			for (Object object : playersX)
				players.add((Player) object);
			wins = (ArrayList<Integer>) propertiesToSave.get(4);
			ArrayList<Integer> tempPrices = (ArrayList<Integer>) propertiesToSave.get(i);
			priceSetter(tempPrices);
			GuiModelInterface iGuiModel = this;
			AI.setModelInterface(iGuiModel);
			success = true;
		}
		return success;
	}

public static boolean isStructured(String pathFile) {
		ArrayList<Object> list = new ArrayList<>();
		GameSaver.load(list, pathFile);
		boolean structured = true;
		int size = list.size();
		if (size > 7)
			if (list.get(0) instanceof Boolean
							& list.get(1) instanceof Boolean
							& list.get(2) instanceof Integer
							& list.get(3) instanceof Integer
							& list.get(4) instanceof ArrayList
							& list.get(5) instanceof Integer
							& list.get(6) instanceof Integer) {
				int n = 7 + (int) list.get(6);
				if (size > n)
					for (int i = 7; structured & i < n; i++)
						structured = list.get(i) instanceof Player;
				else
					structured = false;
				if (list.get(n) instanceof Integer) {
					int m = n + (int) list.get(n) + 1;
					if (size == m + 1)
						for (int i = n + 1; structured & i < m; i++)
							structured = list.get(i) instanceof Column;
					structured = structured & list.get(m) instanceof ArrayList;
				} else
					structured = false;
			} else
				structured = false;
		else
			structured = false;
		return structured;
	}

	private void priceSetter(ArrayList<Integer> priceArray) {
		for (int i = 0; i < priceArray.size(); i++) {
			COMMODITY_TYPES[i].resetPrice();
			int diff = COMMODITY_TYPES[i].getPrice() - priceArray.get(i);
			for (int j = 0; j < diff; j++) {
				//System.out.println(COMMODITY_TYPES[i]+" lowered");
				COMMODITY_TYPES[i].lowerPrice();
			}
			//System.out.println(COMMODITY_TYPES[i].getNameAndPrice());
		}
	}

	@Override //from GuiModelInterface
	public ArrayList<Integer> getPriceArray() {
		ArrayList<Integer> prices = new ArrayList<>();
		for (int i = 0; i < COMMODITY_TYPES.length; i++) {
			prices.add(COMMODITY_TYPES[i].getPrice());
		}
		return prices;
	}

	@Override //from ControlModelInterface 
	public boolean getChoiceStage() {
		return choiceStage;
	}

	@Override //from ControlModelInterface 
	public void changeStage() {
		choiceStage = !choiceStage;
	}

	/**
	 * Deal all the commodities to the columns
	 */
	private void dealGoods() {
		//System.out.println(startGoods.getNameAndPrice());
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			for (int j = 0; j < START_HEIGHT_COLOUMNS; j++) {
				//columns.get(i).add(startGoods.get(0));
				columns.get(i).add(startCommodities.remove(0));
			}
		}
		//System.out.println(startGoods.getNameAndPrice());
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

	@Override //from GuiModelInterface
	public String[] getAllNames() {
		String[] allNames = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			allNames[i] = players.get(i).getName();
		}
		return allNames;
	}

	@Override //from ControlModelInterface 
	public boolean isAllPlayersAI() {
		int i = 0;
		boolean isAllAi = true;
		do {
			isAllAi = isAllAi && (players.get(i++) instanceof AI);
		} while (i < players.size() && isAllAi);
		return isAllAi;
	}

	@Override //from GuiModelInterface
	public int getNrOfCols() {
		return columns.size();
	}

	@Override //from GuiModelInterface
	public int[] getWins() {
		int[] retWins = new int[players.size()];
		for (int i = 0; i < wins.size(); i++) {
			retWins[i] = wins.get(i);
		}
		return retWins;
	}

	@Override //from GuiModelInterface
	public ArrayList<Integer> getPossible() {
		ArrayList<Integer> possible = new ArrayList<>();
		if (!expert) {
			possible.add((position + 1) % getNrOfCols());
			possible.add((position + 2) % getNrOfCols());
			possible.add((position + 3) % getNrOfCols());
		} else {
			int i = 1;
			ArrayList<Commodity> tops = getTopCommodities();
			int posPlusI = (position + i) % getNrOfCols();
			while (!(tops.get(posPlusI).equals(tops.get(position)))
							&& posPlusI != position) {
				System.out.println(position + ": " + tops.get(position) + " "
								+ posPlusI + ":" + tops.get(posPlusI));
				possible.add(posPlusI);
				i++;
				posPlusI = (position + i) % getNrOfCols();
			}
			possible.add(posPlusI);
		}
		return possible;
	}

	@Override //from GuiModelInterface
	public ArrayList<Integer> getNeighbors() {
		ArrayList<Integer> neighbors = new ArrayList<>();
		neighbors.add((position + 1) % getNrOfCols());
		neighbors.add((position + getNrOfCols() - 1) % getNrOfCols());
		return neighbors;
	}

	@Override //from ControlModelInterface 
	public Player getActualPlayer() {
		return players.get(actualPlayer);
	}

	@Override //from ControlModelInterface 
	public int getActualPlayerIndex() {
		return actualPlayer;
	}

	//only AI uses
	@Override //from GuiModelInterface
	public ArrayList<ObservedPlayer> makeObservedOtherPlayers() {
		ArrayList<ObservedPlayer> others = new ArrayList<>();
		//System.out.println(players.size());
		for (int i = 0; i < players.size(); i++) {
			//System.out.println("i: "+i);
			if (i != actualPlayer) {
				int j = nextPlayer(i + actualPlayer);
				//System.out.println("happening i: "+i+"\t: "+j);
				others.add(new ObservedPlayer(players.get(j)));
			}
		}
		return others;
	}

	@Override //from ControlModelInterface 
	public void setPosition(int position) {
		if (!choiceStage && getPossible().contains(position))
			this.position = position;

	}

	/**
	 * Handle the consequences of a choice phase: removes commodities, empty
	 * columns
	 *
	 * @param pointNr
	 * @return
	 */
	@Override //from ControlModelInterface 
	public int[] handleChoice(int pointNr) {
		int kept = pointNr;
		int keep = getNeighbors().indexOf(pointNr);
		int sold = getNeighbors().get(1 - keep);
		//System.out.println("Handle");
//		kept %= getNrOfCols();
//		sold %= getNrOfCols();
		int[] emptiedColumns = {-1, -1};
		Column keptColumn = columns.get(kept);
		Column soldColumn = columns.get(sold);
//		int outIdx = COMMODITY_TYPES.indexOf(actualSold.getTop());
		lastSoldColumn = sold;
		soldColumn.getTop().lowerPrice();
		players.get(actualPlayer).add(keptColumn.getTop());
		//System.out.println("actualK.getTop() "+actualK.getTop()+ " \t actualO.getTop()"
		keptColumn.remove();
		if (keptColumn.goods.isEmpty()) {
			columns.remove(kept);
			emptiedColumns[0] = kept;
			if (position > kept) {
				position--;
			}
			if (sold > kept) {
				sold--;
			}
		}
		soldColumn.remove();
		if (soldColumn.goods.isEmpty()) {
			columns.remove(sold);
			emptiedColumns[1] = sold;
			if (position > sold) {
				position--;
			}
		}
		countPoints();
		actualPlayer = nextPlayer(actualPlayer);
		if (getNrOfCols() < 3) {
			gameOver = true;
		}
		return emptiedColumns;
	}

	public int getLastSoldColumn() {
		return lastSoldColumn;
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

	@Override// form GuiModelInterface
	public int getPosition() {
		return position;
	}

	@Override //from ControlModelInterface 
	public boolean isGameOver() {
		return gameOver;
	}

	@Override // form GuiModelInterface
	public ArrayList<Commodity> getTopCommodities() {
		ArrayList<Commodity> topGoods = new ArrayList<>();
		for (Column column : columns) {
			topGoods.add(column.getTop());
		}
		return topGoods;
	}
	
		@Override // form GuiModelInterface
	public ArrayList<String> getTopCommoditiesString() {
		ArrayList<String> topGoods = new ArrayList<>();
		for (Column column : columns) {
			topGoods.add(column.getTop().getNameAndPrice(false));
		}
		return topGoods;
	}

	@Override // form GuiModelInterface
	public int[] getColsSizes() {
		int[] colSizes = new int[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			colSizes[i] = columns.get(i).getHeight();
		}
		return colSizes;
	}

	@Override //from ControlModelInterface 
	public boolean isValidStep(int pointNr) {
		if (!choiceStage)
			return getPossible().contains(pointNr);
		else
			return getNeighbors().contains(pointNr);
	}

	/**
	 * Initialization
	 */
	@Override //from ControlModelInterface 
	public void reStart() {
		//System.out.println(gameOverString());
		gameOver = false;
		//prices = Arrays.copyOf(PRICES_AT_START, prices.length);
		for (Player player : players) {
			//System.out.println(player);
			player.commodities.clear();
			//player.setPrices(prices);
		}

		System.out.println("last starter: " + lastStarter);
		columns.clear();
		for (int i = 0; i < START_NR_COLOUMS; i++) {
			columns.add(new Column());
		}
		makeGoods();
		Collections.shuffle(startCommodities);
		dealGoods();
		actualPlayer = nextPlayer(lastStarter++);
		position = 0;
		System.out.println("next starter: " + actualPlayer + "\n");
	}

	@Override // form GuiModelInterface
	public String[] makeHints() {
		String[] returnHints = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			StringBuffer temp = new StringBuffer("");
			for (Commodity commodity : players.get(i).commodities) {
				temp.append(commodity.getNameAndPrice(true));
			}
			returnHints[i] = players.get(i).getName() + " has " + players.get(i).getPoints() + " points." + " Kept: " + temp.toString();
		}
		return returnHints;
	}

	private ArrayList<Integer> getWinner() {
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

	@Override // form GuiModelInterface
	public String gameOverString() {
		ArrayList<Integer> winner = getWinner();
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
