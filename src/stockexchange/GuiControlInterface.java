package stockexchange;

/*Gergo Kovats*/

public interface GuiControlInterface {
	
	void setClickedColoumn(int coloumn);
	boolean load(String pathFile);
	void save(String pathFile);
	void pause(boolean popup);
	void unPause();
	
}
