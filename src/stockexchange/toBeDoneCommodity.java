package stockexchange;

/*Gergo Kovats*/

public class toBeDoneCommodity {
	//"Wheat", "Sugar", "Coffee", "Rice", "Cocoa", "Corn"
	static final int WHEAT = 0;
	static final int SUGAR = 1;
	static final int COFFEE = 2;
	static final int RICE = 3;
	static final int COCOA = 4;
	static final int CORN = 5;
	
	private String name;
	private int price;
	
	
	public toBeDoneCommodity(){
		
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public int getPrice(){
		return price;
	}

}
