/*Gergo Kovats*/
package stockexchange.model;

import java.util.ArrayList;

public interface GuiModelInterface {

	public ArrayList<Integer> getPriceArray();//ok
	public String[] getAllNames();//frame, panel
	public int getNrOfCols();//CONTROL, panel
	public int[] getWins();
	public ArrayList<Integer> getPossible();//panel
}
