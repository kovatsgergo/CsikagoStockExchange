/*Gergo Kovats*/
package stockexchange;

import java.util.ArrayList;

public interface GamePanelInterface {

	public abstract void start(ArrayList<String> topGoods, String[] playerNames,
					int[] sizes, int[] wins);
	public abstract void setPossible(int[] possibleColoumns);
	public abstract void setFigure(int destination);
	public abstract void makeChoice(int chosenColoumn, int[] emptiedColoumns,
					int[] prices, ArrayList<String> topGoods, int[] sizes);
	public abstract void setHint(String[] hintText);
	public abstract int gameOverPopup(String gameOverString);
	public abstract void setNrGameCols(int nrCols);
	public abstract void setInterface(GameInterface interf);
	public abstract void quitGame();

}
