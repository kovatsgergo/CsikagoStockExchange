package stockexchange;

/* Gergo Kovats */
import java.io.IOException;
import java.util.Arrays;
import stockexchange.model.AIeasy;
import stockexchange.model.AIhard;
import stockexchange.model.AImedium;
import stockexchange.model.Player;

public class StockExchange {

	final static int MAXPLAYERS = 4;

	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		createAndShowStartupGUI(new Player[0]);
	}

	protected static void createAndShowStartupGUI(Player[] players) {
		//StartUpFrame startUpFrame = new StartUpFrame(plyrs, dims, namesGiven);
		StartUpFrame startUpFrame = new StartUpFrame(players);
	}

	protected static void createAndShowGameGUI(String[][] players, int[] dims)
					throws IOException {
		GamePanel gamePanel = new GamePanel(dims);
		GamePanelInterface interfacePanel = gamePanel;
		GameInterface theGame = new Control(0, createPlayers(players), interfacePanel);
		gamePanel.setInterface(theGame);
		GameFrame gameFrame = new GameFrame(createPlayers(players), dims, gamePanel, theGame);
		gameFrame.setVisible(true);

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
