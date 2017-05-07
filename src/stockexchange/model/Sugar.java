/*Gergo Kovats*/
package stockexchange.model;

import java.io.Serializable;

public class Sugar extends Commodity implements Serializable {

	private static int price = 6;
	private static int START_PRICE = 6;
	private static String name = "Sugar";

	@Override
	public String toString() {
		return name + "{price =" + price + ", START_PRICE=" + START_PRICE + '}';
	}

	public String getNameAndPrice(boolean full) {
		if (full)
			return name + "_" + price + "-";
		else
			return name;
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
		return this.getNameAndPrice(false).equals(other.getNameAndPrice(false));
	}

}
