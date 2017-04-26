/*Gergo Kovats*/
package stockexchange;

public class Sugar extends Commodity {

	static int price = 6;
	static int START_PRICE = 6;

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
	
		@Override
	protected void resetPrice(){
		price = START_PRICE;
	}

	protected boolean equals(Commodity other) {
		return this.toString().equals(other.toString());
	}

}
