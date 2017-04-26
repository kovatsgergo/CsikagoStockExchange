package stockexchange;

/*Gergo Kovats*/

public abstract class Commodity {
	//"Wheat", "Sugar", "Coffee", "Rice", "Cocoa", "Corn"
	
	@Override
	public abstract String toString();
	public abstract String toString(boolean full);
	
	public abstract int getPrice();
	
	protected abstract void lowerPrice();
	
//	protected void lowerPrice(int type){
//		prices[type]--;
//	}
	
	
	protected int[] getAllPrices(){
		return new int[]{Wheat.price, Sugar.price};
	}
	

}
