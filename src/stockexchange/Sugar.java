/*Gergo Kovats*/
package stockexchange;

public class Sugar extends Commodity {

	static int price = 6;

	@Override
	public String toString() {
		return "Sugar";
	}

	public String toString(boolean full) {
		return "Sugar{" + price + "}";
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
