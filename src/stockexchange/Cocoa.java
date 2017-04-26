/*Gergo Kovats*/
package stockexchange;

public class Cocoa extends Commodity {

	static int price = 6;

	@Override
	public String toString() {
		return "Cocoa";
	}

	public String toString(boolean full) {
		return "Cocoa{" + price + "}";
	}

	@Override
	public int getPrice() {
		return price;
	}

	@Override
	protected void lowerPrice() {
		price--;
	}
	
	protected boolean equals(Commodity other){
		return this.toString().equals(other.toString());
	}

}
