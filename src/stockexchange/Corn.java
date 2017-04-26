/*Gergo Kovats*/
package stockexchange;

public class Corn extends Commodity {

	static int price = 6;
	static int START_PRICE = 6;

	@Override
	public String toString() {
		return "Corn";
	}

	public String toString(boolean full) {
		return "Corn{" + price + "}";
	}

	@Override
	public int getPrice() {
		return price;
	}

	@Override
	protected void lowerPrice() {
		price--;
	}

}
