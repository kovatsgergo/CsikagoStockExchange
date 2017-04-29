package stockexchange.model;

/* Gergo Kovats */
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable{

	private int points;
	private String name;
	protected ArrayList<Commodity> commodities = new ArrayList<>();
	//static final long serialVersionUID = 12345L;
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

	public void recalcPoints() {
		int sum = 0;
		for (Commodity commodity : commodities) {
			sum += commodity.getPrice();
		}
		setPoints(sum);
	}

	private void setPoints(int points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "Player{" + "points=" + points + ", name=" + name + ", goods=" + commodities.toString() + '}';
	}

}
