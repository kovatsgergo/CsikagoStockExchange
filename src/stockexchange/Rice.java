/*Gergo Kovats*/
package stockexchange;

public class Rice extends Commodity {

	static int price = 6;
	static int START_PRICE = 6;

	@Override
	public String toString() {
		return "Rice";
	}

	public String toString(boolean full) {
		return "Rice{" + price + "}";
	}

	@Override
	public int getPrice() {
		return price;
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
