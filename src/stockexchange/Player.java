package stockexchange;

/* Gergo Kovats */
import java.util.ArrayList;

public class Player {

	private int points;
	private String name;
	protected ArrayList<Commodity> commodities = new ArrayList<>();
//	protected int[] prices;

//	public Player() {
//		points = 0;
//		name = "Anonymus";
//	}
	public Player(String name) {
		setPoints(0);
		this.name = name;
	}

	public void add(Commodity good) {
		commodities.add(good);
	}

	public String getName() {
		return name;
	}

//	public void setName(String name) {
//		this.name = name;
//	}
	protected int getPoints() {
		return points;
	}

	protected void recalcPoints() {
		int sum = 0;
		for (Commodity commodity : commodities) {
			sum += commodity.getPrice();
		}
		setPoints(sum);
	}

	private void setPoints(int points) {
		this.points = points;
	}

//	protected void setPrices(int[] prices) {
//		this.prices = prices;
//	}

	@Override
	public String toString() {
		return "Player{" + "points=" + points + ", name=" + name + ", goods=" + commodities + '}';
	}

}
