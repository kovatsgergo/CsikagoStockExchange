/*Gergo Kovats*/
package stockexchange;

import java.util.ArrayList;
import stockexchange.model.Commodity;
import stockexchange.model.ObservedPlayer;

public interface GuiModelInterface {
	public ArrayList<Integer> getPriceArray();//ok
	public String[] getAllNames();//frame, panel
	public int getNrOfCols();//ok
	public ArrayList<Integer> getNeighbors();//ok
	public int[] getWins();//ok
	public ArrayList<Integer> getPossible();//ok
	public int getActualPlayerIndex();//ok
	public int getPosition();//panel, AI
	public ArrayList<Commodity> getTopCommodities();//panel, AI
	public int[] getColsSizes();//AI, panel
	public String[] makeHints();//ok
	public String gameOverString();//ok
	public ObservedPlayer makeObservedNextPlayers();//AI
	public int getLastSoldColumn();//ok
}
