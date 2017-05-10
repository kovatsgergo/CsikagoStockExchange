/*Gergo Kovats*/
package stockexchange;

import stockexchange.model.GameFileException;
import stockexchange.model.Player;

public interface ControlModelInterface {
	public void save(String pathFile) throws GameFileException;
	public void load(String pathFile)  throws GameFileException;
	public boolean getChoiceStage();//ok
	public void changeStage();//ok
	public boolean isAllPlayersAI();//ok
	public Player getActualPlayer();//ok
	public void setPosition(int position);//ok
	public int[] handleChoice(int pointNr);//ok
	public boolean isGameOver();//ok
	public boolean isValidStep(int pointNr);//ok
	public void reStart();//ok
}
