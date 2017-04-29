package stockexchange;

/*Gergo Kovats*/
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import stockexchange.model.AI;
import stockexchange.model.AIeasy;

public class Tester {

	static AI ai = new AIeasy("nothing");

	public static void main(String[] args) {
		String fonts[]
						= GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel(new GridLayout(0,2));
		//pane.setLayout(new GridLayout(0,1));
		//JPanel pane = new JPanel();
		for (int i = 0; i < fonts.length; i++) {
			JTextField temp = new JTextField(fonts[i]);
			temp.setFont(new Font("Arial", Font.PLAIN, 20));
			panel.add(temp);
			//JLabel temp2 = new JLabel("Árvíztűrő tükörfúrógép");
			JLabel temp2 = new JLabel("Player 2's turn");
			temp2.setFont(new Font(fonts[i], Font.PLAIN, 20));
			panel.add(temp2);
			//System.out.println(fonts[i]);
		}
		JScrollPane scPane = new JScrollPane(panel);
		frame.add(scPane);
		frame.pack();
		frame.setVisible(true);
	}

//	public static void main4(String[] args) {
//		Commodity wheat = new Wheat();
//		System.out.println(wheat.toString() + "before " + wheat.getPrice());
//		wheat.lowerPrice();
//		System.out.println("after " + wheat.getPrice());
//		Commodity wheat2 = new Wheat();
//		System.out.println(wheat2.toString());
//	}

//	public static void main1(String[] args) {
//		ai = new AIeasy("nothing");
//		int[] temp = ai.getMaxIndex(new int[][]{{0, 1}, {5, 6}, {9, 9}});
//		System.out.println(Arrays.toString(temp));
//	}

//	public static void main2(String[] args) {
//		int[][] a = new int[][]{{1, 2}, {3, 4}, {5, 6}};
//		int[][] b = new int[][]{{5, 2}, {1, 7}, {-1, -5}};
//		int[][] c = ai.matrixAddition(a, b);
//		System.out.println(Arrays.deepToString(c));
//	}

	public static void main3(String[] args) {
		int w = 600;
		int h = 300;
		int newW = (w + h) * 5 / 8;
		int newH = (w + h) * 3 / 8;
		System.out.println("newW " + newW + " newH " + newH);
	}
}
