/*Gergo Kovats*/
package stockexchange.model;


public interface ControlModelInterface {
	public void save();
	public boolean load();
	public boolean getChoiceStage();
	public void changeStage();
	public boolean isAllPlayersAI();

}
