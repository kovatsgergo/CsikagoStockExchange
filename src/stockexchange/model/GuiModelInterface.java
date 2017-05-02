/*Gergo Kovats*/
package stockexchange.model;

import java.util.ArrayList;

public interface GuiModelInterface {

	public ArrayList<Integer> getPriceArray();
	public String[] getAllNames();
	public int getNrOfCols();//Controlbol kiszedni!
	public int[] getWins();
}
