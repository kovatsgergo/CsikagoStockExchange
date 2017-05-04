/*Gergo Kovats*/
package stockexchange;

import stockexchange.model.Player;

public interface ControlModelInterface {
	public void save(String pathFile);
	public boolean load(String pathFile);
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
