package stockexchange;

/*Gergo Kovats*/

import java.util.Arrays;

public class Tester {
	static AI ai = new AIeasy("nothing");

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
	
	public static void main(String[] args) {
		
	}
}