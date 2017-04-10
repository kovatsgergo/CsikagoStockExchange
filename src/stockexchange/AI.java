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
public abstract class AI extends Player {

  public AI() {
    super();
  }
  
  //abstract int[] makeMove(GameClass theGame);
	abstract int[] makeMove(int position, ArrayList<String> tops, int[] prices);
}
