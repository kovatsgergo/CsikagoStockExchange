/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public class Player {

    private int points;
    private String name;
    protected ArrayList<String> goods = new ArrayList();

    public Player() {
        points = 0;
        name = "Anonymus";
    }

    public void add(String good) {
        goods.add(good);
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
		
		protected int getPoints(){
			return points;
		}
		
		protected void setPoints(int points){
			this.points = points;
		}
    
}
