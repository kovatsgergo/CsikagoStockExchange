/*Gergo Kovats*/
package stockexchange;

import java.util.ArrayList;
import javax.swing.JPanel;

public abstract class AbstractGamePanel extends JPanel {

	public abstract void start(ArrayList<String> topGoods,
					int[] sizes, int[] wins);
	public abstract void setPossible(int[] possibleColoumns);
	public abstract void setFigure(int destination);
	public abstract void makeChoice(int chosenColoumn, int[] emptiedColoumns,
					int[] prices, ArrayList<String> topGoods, int[] sizes);
	public abstract void setHint(String[] hintText);
	public abstract int gameOverPopup(String gameOverString);

}
