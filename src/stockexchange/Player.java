/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public class Player {

	private int points;
	private String name;
	protected ArrayList<String> goods = new ArrayList();
	protected int[] prices;

//	public Player() {
//		points = 0;
//		name = "Anonymus";
//	}
	public Player(String name) {
		points = 0;
		this.name = name;
	}

	public void add(String good) {
		goods.add(good);
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

	private void setPoints(int points) {
		this.points = points;
	}

	protected void setPrices(int[] prices) {
		this.prices = prices;
		int sum = 0;
		for (String good : goods) {
			sum += prices[GameClass.GOOD_TYPES.indexOf(good)];
		}
		setPoints(sum);
	}

	@Override
	public String toString() {
		return "Player{" + "points=" + points + ", name=" + name + ", goods=" + goods + '}';
	}

}
