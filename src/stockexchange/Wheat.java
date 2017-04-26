/*Gergo Kovats*/
package stockexchange;

public class Wheat extends Commodity {

	static int price = 7;

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
	protected void lowerPrice() {
		price--;
	}

}
