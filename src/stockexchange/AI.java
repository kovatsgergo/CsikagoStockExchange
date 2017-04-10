/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public abstract class AI extends Player {

	public AI() {
		super();
	}

	//abstract int[] makeMove(GameClass theGame);
	abstract int[] makeMove(int position, ArrayList<String> tops, int[] prices);

	public int countContain(ArrayList<String> list, String element) {
		int i = 0;
		for (String elem : list) {
			if (elem.equals(element)) {
				i++;
			}
		}
		return i;
	}
}
