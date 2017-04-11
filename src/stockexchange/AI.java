/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public abstract class AI extends Player {

//	public AI() {
//		super();
//	}

	//abstract int[] makeMove(GameClass theGame);
	abstract protected int[] makeMove(int position, ArrayList<String> tops, int[] prices);

	protected int countContain(ArrayList<String> list, String element) {
		int i = 0;
		for (String elem : list) {
			if (elem.equals(element)) {
				i++;
			}
		}
		return i;
	}
}
