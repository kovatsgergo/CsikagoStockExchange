package stockexchange;

/*Gergo Kovats*/

import java.util.ArrayList;

public interface GamePanelInterface {

	public abstract void start(ArrayList<Commodity> topCommodities, String[] playerNames,
					int[] sizes, int[] wins);
	public abstract void setPossible(int[] possibleColoumns);
	public abstract void setFigure(int destination);
	public abstract void makeChoice(int kept, int sold, int[] emptiedColoumns,
					int[] prices, ArrayList<Commodity> topCommodities, int[] sizes);
	public abstract void setHint(String[] hintText);
	public abstract int gameOverPopup(String gameOverString);
	public abstract int pausePopup();
	public abstract void setNrGameCols(int nrCols);
	public abstract void setInterface(GameInterface interf);
	public abstract void quitGame();

}
