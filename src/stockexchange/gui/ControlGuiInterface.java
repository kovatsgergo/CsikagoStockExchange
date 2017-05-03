package stockexchange.gui;

/*Gergo Kovats*/

import stockexchange.GuiControlInterface;

public interface ControlGuiInterface {

	public void start();
	public void startFromLoaded();
	public void setPossible();
	public void setFigure();
	public void makeChoice(int kept, int[] emptiedColoumns);
	public void setHint();
	public int gameOverPopup();
	public int pausePopup();
	public void setNrGameCols();
	public void setInterface(GuiControlInterface interf);

}
