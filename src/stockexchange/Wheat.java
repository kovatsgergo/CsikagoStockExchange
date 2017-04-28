/*Gergo Kovats*/
package stockexchange;

public class Wheat extends Commodity {

	private static int price = 7;
	private static int START_PRICE = 7;

	@Override
	public String toString() {
		return "Wheat";
	}

	public String toString(boolean full) {
		return "Wheat{" + price + "}";
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
