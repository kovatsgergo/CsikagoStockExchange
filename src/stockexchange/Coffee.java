/*Gergo Kovats*/
package stockexchange;

public class Coffee extends Commodity {

	static int price = 6;

	@Override
	public String toString() {
		return "Coffee";
	}

	public String toString(boolean full) {
		return "Coffee{" + price + "}";
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
