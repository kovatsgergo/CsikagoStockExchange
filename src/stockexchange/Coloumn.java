package stockexchange;

/* Gergo Kovats */

import java.util.ArrayList;

public class Coloumn {
    ArrayList<String> goods = new ArrayList<String>();

    public void add(String good) {
        goods.add(good);
    }

    public void remove() {
        goods.remove(0);
    }
    
    public String toString(){
        return goods.toString();
    }
    
    public String getTop(){
        return goods.get(0);
    }
    
    public int getHeight(){
        return goods.size();
    }

}
