package stockexchange;

/*Gergo Kovats*/
import java.util.Arrays;

public class Tester {

	static AI ai = new AIeasy("nothing");

	public static void main(String[] args) {
		Commodity wheat = new Wheat();
		System.out.println(wheat.toString()+"before "+wheat.getPrice());
		wheat.lowerPrice();
		System.out.println("after "+wheat.getPrice());
		Commodity wheat2 = new Wheat();
		System.out.println(wheat2.toString());
	}

	public static void main1(String[] args) {
		ai = new AIeasy("nothing");
		int[] temp = ai.getMaxIndex(new int[][]{{0, 1}, {5, 6}, {9, 9}});
		System.out.println(Arrays.toString(temp));
	}

	public static void main2(String[] args) {
		int[][] a = new int[][]{{1, 2}, {3, 4}, {5, 6}};
		int[][] b = new int[][]{{5, 2}, {1, 7}, {-1, -5}};
		int[][] c = ai.matrixAddition(a, b);
		System.out.println(Arrays.deepToString(c));
	}

	public static void main3(String[] args) {
		int w = 600;
		int h = 300;
		int newW = (w + h) * 5 / 8;
		int newH = (w + h) * 3 / 8;
		System.out.println("newW " + newW + " newH " + newH);
	}
}
