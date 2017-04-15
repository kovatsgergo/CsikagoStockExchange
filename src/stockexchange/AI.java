/* Gergo Kovats */
package stockexchange;

import java.util.ArrayList;

public abstract class AI extends Player {

	protected ArrayList<ObservedPlayer> observedPlayers;

	public void setObservedPlayers(ArrayList<ObservedPlayer> observedPlayers) {
		this.observedPlayers = observedPlayers;
	}
	
	public AI(String name) {
		super(name);
	}

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
