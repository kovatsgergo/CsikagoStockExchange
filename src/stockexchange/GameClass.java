/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockexchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Gergo
 */
public class GameClass {//TODO: enum vagy map
  
  Random rnd = new Random();
  ArrayList<Coloumn> coloumns = new ArrayList();
  ArrayList<Player> players = new ArrayList();
  ArrayList<String> goods = new ArrayList();
  //Player winner;
  //ArrayList<Integer> wins = new ArrayList();
  
  static final ArrayList<String> goodTypes = new ArrayList(Arrays.asList(new String[]
  {"Wheat", "Sugar", "Coffee", "Rice", "Cocoa", "Corn"}));
  int position = 0;
  final int[] pricesAtStart = {7, 6, 6, 6, 6, 6};
  int[] prices = {7, 6, 6, 6, 6, 6};
  int[] possible = new int[3];
  boolean gameOver = false;
  int actualPlayer = 0;
  int numPlayers;
  
  public GameClass(int numplayers, int starter, ArrayList<String> playerNames) {
    prices = pricesAtStart;
    numPlayers = numplayers;
    for (int i = 0; i < 9; i++) {
      coloumns.add(new Coloumn());
    }
    for (int i = 0; i < numplayers; i++) {
      String name = playerNames.get(i);
      if (name.length() > 2) {
        if (!(name.substring(0, 2).equals("AI"))) {
          players.add(new Player());
          players.get(i).setName(playerNames.get(i));
        } else if (name.substring(2, 3).equals("e")) {
          players.add(new AIeasy());
          players.get(i).setName("AI " + i + " (easy)");
        } else if (name.substring(2, 3).equals("m")) {
          players.add(new AImedium());
          players.get(i).setName("AI " + i + " (medium)");
        } else if (name.substring(2, 3).equals("h")) {
          players.add(new AIhard());
          players.get(i).setName("AI " + i + " (hard)");
        }        
      } else if (name.equals("AI")) {
        players.add(new AIeasy());
        players.get(i).setName("AI " + i + " (not set easy)");
      } else {
        players.add(new Player());
        players.get(i).setName(playerNames.get(i));
      }
    }
    
    makeGoods();
    
    Collections.shuffle(goods);
    
    mixGoods();
    actualPlayer = Math.max(starter, Math.min(starter, numPlayers - 1));
  }
  
  public void mixGoods() {
    //System.out.println(goods.toString());
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 4; j++) {
        //coloumns.get(i).add(goods.get(0));
        coloumns.get(i).add(goods.remove(0));
      }
    }
    //System.out.println(goods.toString());
  }
  
  public void makeGoods() {
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 6; j++) {
        goods.add(goodTypes.get(i));
      }
    }
  }
  
  public int getNrCols() {
    return coloumns.size();
  }
  
  public Coloumn getCol(int nr) {
    return coloumns.get(nr % getNrCols());
  }
  
  public int[] getPossible() {
    possible[0] = (position + 1) % getNrCols();
    possible[1] = (position + 2) % getNrCols();
    possible[2] = (position + 3) % getNrCols();
    return possible;
  }
  
  public int[] getNeighbors() {
    int[] neighbors = new int[2];
    neighbors[0] = (position + 1) % getNrCols();
    neighbors[1] = (position + getNrCols() - 1) % getNrCols();
    return neighbors;
  }

  /**
   * Returns the neighbor in front [0] and the neighbor in back [1]
   *
   * @param position the figure's current state
   * @return int[]
   */
  public int[] getNeighbors(int position) {
    int[] neighbors = new int[2];
    neighbors[0] = (position + 1) % getNrCols();
    neighbors[1] = (position + getNrCols() - 1) % getNrCols();
    return neighbors;
  }
  
  public int[] getAIMove() {
    //return ((AI) players.get(actualPlayer)).makeMove(this);
		if(players.get(actualPlayer) instanceof AIhard)
			((AIhard)players.get(actualPlayer)).setPlayers(players);
		return ((AI) players.get(actualPlayer)).makeMove(position, getTops(), prices);
  }
  
  public int[] handleChoice(int keep, int out) {
    System.out.println("Handle");
    keep %= getNrCols();
    out %= getNrCols();
    int[] ret = {-1, -1};
    Coloumn actualK = coloumns.get(keep);
    Coloumn actualO = coloumns.get(out);
    int outIdx = goodTypes.indexOf(actualO.getTop());
    //int keepIdx = goodTypes.indexOf(actualK.getTop());
    players.get(actualPlayer).add(actualK.getTop());
    //System.out.println("actualK.getTop() "+actualK.getTop()+ " \t actualO.getTop()"
    //+actualO.getTop());
    actualK.remove();
    if (actualK.goods.size() == 0) {
      coloumns.remove(keep);
      ret[0] = keep;
      if (position > keep) {
        position--;
      }
      if (out > keep) {
        out--;
      }
    }
    actualO.remove();
    if (actualO.goods.size() == 0) {
      coloumns.remove(out);
      ret[1] = out;
      if (position > out) {
        position--;
      }
    }
    prices[outIdx] -= 1;
    countPoints();
    System.out.println(Arrays.toString(prices));
    if (coloumns.size() < 3) {
      gameOver = true;
    }
    actualPlayer = nextPlayer(actualPlayer);
    
    //System.out.printf("keepIdx: %d  outIdx: %d \t prices[keepIdx]: %d \t keep: %d\n",
    //keepIdx, outIdx, prices[keepIdx], keep);
    return ret;
  }
  
  public void countPoints() {
    for (Player player : players) {
      int sum = 0;
      for (String good : player.goods) {
        sum += prices[goodTypes.indexOf(good)];
      }
      player.setPoints(sum);
    }
  }
  
  public int nextPlayer(int actual) {
    return (actual + 1) % numPlayers;
  }
	
	private ArrayList<String> getTops(){
		ArrayList<String> topGoods = new ArrayList<>();
		for (Coloumn coloumn : coloumns) {
			topGoods.add(coloumn.getTop());
		}
		return topGoods;
	}
  
}
