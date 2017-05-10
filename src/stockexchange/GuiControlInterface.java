package stockexchange;

import stockexchange.model.GameFileException;

/*Gergo Kovats*/

public interface GuiControlInterface {
	
	void setClickedColoumn(int coloumn);
	void load(String pathFile) throws GameFileException;
	void save(String pathFile) throws GameFileException;
	void pause(boolean popup);
	void unPause();
	
}
