/*Gergo Kovats*/
package stockexchange.model;

import java.io.Serializable;

public class Cocoa extends Commodity implements Serializable{

	private static int price = 6;
	private static int START_PRICE = 6;

	@Override
	public String toString() {
		return "Cocoa{price =" + price + ", START_PRICE="+START_PRICE+'}';
	}

	public String toString(boolean full) {
		return "Cocoa{" + price + "}";
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

	protected boolean equals(Commodity other) {
		return this.toString().equals(other.toString());
	}

}
