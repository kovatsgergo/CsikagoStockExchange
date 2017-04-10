/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockexchange;

import java.util.ArrayList;

/**
 *
 * @author Gergo
 */
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
