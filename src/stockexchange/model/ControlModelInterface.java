/*Gergo Kovats*/
package stockexchange.model;

import java.util.ArrayList;


public interface ControlModelInterface {
	public void save(String pathFile);
	public boolean load(String pathFile);
	public boolean getChoiceStage();//ok
	public void changeStage();//ok
	public boolean isAllPlayersAI();//ok
	public ArrayList<Integer> getNeighbors();//ok
	public Player getActualPlayer();//ok
	

}
