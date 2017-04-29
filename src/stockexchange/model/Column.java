package stockexchange.model;

/* Gergo Kovats */

import java.io.Serializable;
import java.util.ArrayList;

public class Column implements Serializable{
    ArrayList<Commodity> goods = new ArrayList<>();

    public void add(Commodity good) {
        goods.add(good);
    }

    public void remove() {
        goods.remove(0);
    }
    
    public String toString(){
        return goods.toString();
    }
    
    public Commodity getTop(){
        return goods.get(0);
    }
    
    public int getHeight(){
        return goods.size();
    }

}
