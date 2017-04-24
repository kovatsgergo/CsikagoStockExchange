package stockexchange;

/* Gergo Kovats */
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.SwingUtilities;

public class StockExchange {

	final static int MAXPLAYERS = 4;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int[] dimensions = {(int) (screenSize.getWidth() * 0.75),
					(int) (screenSize.getWidth() * 0.45), (int) screenSize.getWidth(), (int) screenSize.getHeight()};

				createAndShowStartupGUI(2, dimensions);
			}
		});
	}

	protected static void createAndShowStartupGUI(int plyrs, int[] dims, String... namesGiven) {
		StartUpFrame startUpFrame = new StartUpFrame(plyrs, dims, namesGiven);
	}

	protected static void createAndShowGameGUI(String[] players, int[] dims)
					throws IOException {
		GameFrame gameFrame = new GameFrame(players, dims);
	}

}
