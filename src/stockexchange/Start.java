/*Gergo Kovats*/
package stockexchange;

import java.util.Arrays;
import stockexchange.gui.ControlGuiInterface;
import stockexchange.gui.GameContainerPanel;
import stockexchange.gui.GamePanel;
import stockexchange.gui.MainFrame;
import stockexchange.model.AIeasy;
import stockexchange.model.AIhard;
import stockexchange.model.AImedium;
import stockexchange.model.Model;
import stockexchange.model.Player;

public class Start {

	private static MainFrame frame;

	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		frame = new MainFrame();
		frame.setVisible(true);
	}

	public static void switchToGame(String[][] players, int[] dims) {
		//Model model = new Model();
		Player[] playersArray = createPlayers(players);
		Model model = new Model(playersArray);
		GamePanel gamePanel = new GamePanel(model);
		ControlGuiInterface interfacePanel = gamePanel;
		GuiControlInterface control = new Control(playersArray, model, interfacePanel);
		gamePanel.setInterface(control);
		frame.setControl(control);
		GameContainerPanel gameContainerPanel = new GameContainerPanel(createPlayers(players), dims, gamePanel, control);
		frame.setToGame(gameContainerPanel);
	}

	public static void switchToSetup() {
		frame.setToStartup();
	}

	private static Player[] createPlayers(String[][] players) {
		System.out.println("players at createPlayers: " + Arrays.deepToString(players));
		Player[] playerArray = new Player[players.length];
		for (int i = 0; i < players.length; i++) {
			if (players[i][1].equals("human"))
				playerArray[i] = new Player(players[i][0]);
			else
				switch (players[i][0]) {
					case "Easy":
						playerArray[i] = new AIeasy("AI " + (i + 1) + " (easy)");
						break;
					case "Medium":
						playerArray[i] = new AImedium("AI " + (i + 1) + " (medium)");
						break;
					case "Hard":
						playerArray[i] = new AIhard("AI " + (i + 1) + " (hard)");
						break;
				}
		}
		return playerArray;
	}

}
