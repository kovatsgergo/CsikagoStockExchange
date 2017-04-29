package stockexchange;

/*Gergo Kovats*/

import java.util.ArrayList;
import stockexchange.model.Model;

public interface GamePanelInterface {

	public abstract void start();
	public abstract void setModel(Model model);
	public abstract void setPossible(ArrayList<Integer> possibleColoumns);
	public abstract void setFigure();
	public abstract void makeChoice(int kept, int sold, int[] emptiedColoumns);
	public abstract void setHint(String[] hintText);
	public abstract int gameOverPopup(String gameOverString);
	public abstract int pausePopup();
	public abstract void setNrGameCols();
	public abstract void setInterface(GameInterface interf);
	public abstract void quitGame();

}
