package stockexchange;

/*Gergo Kovats*/

public interface GuiControlInterface {
	
	void setClickedColoumn(int coloumn);
	void load(String pathFile);
	void save(String pathFile);
	void pause();
	
}
