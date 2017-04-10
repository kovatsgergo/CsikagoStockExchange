/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public abstract class AI extends Player {

  public AI() {
    super();
  }
  
  //abstract int[] makeMove(GameClass theGame);
	abstract int[] makeMove(int position, ArrayList<String> tops, int[] prices);
}
