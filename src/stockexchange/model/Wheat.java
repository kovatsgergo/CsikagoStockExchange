/*Gergo Kovats*/
package stockexchange.model;

import java.io.Serializable;

public class Wheat extends Commodity implements Serializable {

	private static int price = 7;
	private static int START_PRICE = 7;

	@Override
	public String toString() {
		return "Wheat{price =" + price + ", START_PRICE=" + START_PRICE + '}';
	}

	public String toString(boolean full) {
		if (full)
			return "Wheat{" + price + "}";
		else
			return "Sugar";
	}

	@Override
	public int getPrice() {
		return price;
	}

	@Override
	public int getStartPrice() {
		return START_PRICE;
	}

	@Override
	protected void lowerPrice() {
		price--;
	}

	@Override
	protected void resetPrice() {
		price = START_PRICE;
	}

	public boolean equals(Commodity other) {
		return this.toString(false).equals(other.toString(false));
	}

}