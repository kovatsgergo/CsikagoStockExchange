/* Gergo Kovats */
package stockexchange;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class MyPanel extends JPanel {

	GameClass theGame;
	double[] center = new double[2];// = {600, 220};
	int h;
	int w;
	ArrayList<BufferedImage> goodImgs = new ArrayList();
	BufferedImage stockImg;
	BufferedImage figureImg;
	ArrayList<Rectangle> bounds = new ArrayList();
	//Point point;
	int[] poss;
	boolean choiceStage = false;
	int nrcols = 9;

	Font fontB = new Font("Arial Black", 0, 23);
	Font fontHint = new Font("Arial", 0, 15);

	Color[] colors = {Color.BLUE, Color.WHITE, Color.BLACK,
		Color.GREEN, new Color(205, 130, 80), Color.YELLOW};
	final float[] scales = {0.5f, 0.5f, 0.5f, 1f};
	final float[] offsets = {20, 20, 20, 0};
	final float[] scalesT = {1.1f, 1.1f, 1.1f, 1.f};
	final float[] offsetsT = {0, 0, 0, 0};
	final float[] scalesR = {.5f, .8f, .8f, 1.f};
	final float[] offsetsR = {150, 10, 10, 0};
	final float[] scalesA = {.9f, .9f, .9f, 1.f};
	final float[] offsetsA = {0, 0, 0, 0};
	ArrayList<String> playerNames;
	ArrayList<Integer> wins = new ArrayList();
	int lastStarter = -1;
	Dimension dimensions;
	double[] angleFig = new double[2];
	double[] angleCols = new double[18];
	int[] fallenCols = {-1, -1};
	int sinking = -1;
	int sinking2 = -1;
	int sinkImg = -1;
	int sinkImg2 = -1;
	int thrownGoodX;
	//int sinkX1;
	float[] keptGoodX = new float[2];//the kept item
	float[] thrownGoodY = new float[3];//the thrown good: from, to, hasarrived(-1)
	float[] keptGoodY = new float[3];
	String sinkStr;
	String sinkStr2;
	float[] pricesDiff;//{7, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6};

	boolean hints = false;
	ArrayList<Integer> winner;

	double[] figureFromTo = new double[4];
	double[] coordDiff = new double[2];

	int goodsSize;
	int fifth;
	int figureSize;

	int[] AIchoice = new int[2];
	final int AI_SPEED = 100;
	final int ANIM_INIT_DELAY = 10;
	final int ANIM_TICK = 30;

	javax.swing.Timer timerFig = new javax.swing.Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			moveFigure();
		}
	});

	javax.swing.Timer timerCols = new javax.swing.Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			moveCols();
		}
	});

	javax.swing.Timer timerGoods = new javax.swing.Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			moveGoods();
		}
	});

	javax.swing.Timer timerPrices = new javax.swing.Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			movePrices();
		}
	});

	javax.swing.Timer timerAI = new javax.swing.Timer(AI_SPEED, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			aiChoice();
		}
	});

	public MyPanel(String[] playernames, int[] dims) throws IOException {

		URL imageURL = this.getClass().getResource("/images/Wheat.png");
		goodImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Sugar.png");
		goodImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Coffee.png");
		goodImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Rice.png");
		goodImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Cocoa.png");
		goodImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Corn.png");
		goodImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Stock.jpg");
		stockImg = ImageIO.read(imageURL);
		URL imageURL2 = this.getClass().getResource("/images/Figure.png");
		figureImg = ImageIO.read(imageURL2);

		playerNames = new ArrayList<>(Arrays.asList(playernames));
		final int numPlayers = playerNames.size();
		for (int i = 0; i < numPlayers; i++) {
			wins.add(0);
		}
		//int winner = -1;
		reStart();
		//repaint();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				repaint();
				Point point = e.getPoint();
				runHumanRound(getClickedCol(point));
			}
		});

		class ResizeListener extends ComponentAdapter {

			@Override
			public void componentResized(ComponentEvent e) {
				refreshSizes();
			}
		}

		addComponentListener(new ResizeListener());

	}

	public MyPanel returnThis() {
		return this;
	}

	private void refreshSizes() {
		////drawing constants only changing when panel resized
		//global values
		w = this.getWidth();
		h = this.getHeight();
		fifth = w / (theGame.numPlayers * 2 + 1);
		//font
		fontB = fontB.deriveFont((float) (w * 0.013) + 8);
		//coloumns
		center[0] = w * 2 / 3;
		center[1] = w * 11 / 50;
		goodsSize = Math.round(w / 10f);
		//figure
		figureSize = Math.round(goodsSize / 5);
	}

	public int getClickedCol(Point pnt) {
		int col = -1;
		for (int i = 0; i < theGame.getNrCols(); i++) {
			if (bounds.get(i).contains(pnt)) {
				return i;
			}
		}
		return col;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(950, 550);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//Draw the stock image
		int stockStartV = Math.round(w / 10f);
		int stockWidth = Math.round(w * 0.35f);
		g.drawImage(stockImg, w / 15, stockStartV, stockWidth + w / 15,
						stockStartV + stockWidth, 0, 0, 1100, 1100, null);

		//Draw the price indicators
		int rectsStartW = Math.round(w * 0.04f) + w / 15;
		int rectsStepW = Math.round(w * 0.038f);
		int rectsStartH = Math.round(w * 0.169f);
		int rectsStepH = Math.round(w * 0.048f);
		int rectsSize = Math.round(w * 0.03f);

		g.setColor(colors[0]);
		g.fillRect(Math.round(rectsStartW + (8 - pricesDiff[0]) * rectsStepW),
						rectsStartH + 0 * rectsStepH, rectsSize, rectsSize);

		for (int i = 1; i < 6; i++) {
			g.setColor(colors[i]);
			g.fillRect(Math.round(rectsStartW + (7 - pricesDiff[i]) * rectsStepW),
							rectsStartH + i * rectsStepH, rectsSize, rectsSize);
		}

		g.setColor(Color.BLACK);
		int x;
		int y;
		poss = theGame.getPossible();
		g.setColor(Color.BLACK);
		g.setFont(fontB);

		bounds.clear();

		//
		//Draw each coloumns
		//
		int coeff = Math.round(w * (float) Math.sqrt(theGame.getNrCols() / 9f) / 5f);
		for (int i = 0; i < theGame.getNrCols(); i++) {
			//Calculate values
			angleCols[i + 9] = i * (Math.PI * 2 / theGame.getNrCols());
			x = (int) (center[0] + coeff * Math.cos(angleCols[i]));
			y = (int) (center[1] + coeff * Math.sin(angleCols[i]));
			String topGood = theGame.getCol(i).getTop();
			int height = theGame.getCol(i).getHeight();
			if (sinking == i && thrownGoodY[2] == -1) {
				thrownGoodX = x;
				thrownGoodY[2] = y;
			}
			if (sinking2 == i && keptGoodY[2] == -1) {
				keptGoodX[0] = x;
				keptGoodY[2] = y;
			}
			if (hints) {
				g.drawString(height + "", x, y);
			}
			int imgNr = theGame.goodTypes.indexOf(topGood);

			// Set the coloumns' top good's coloring based on playability
			RescaleOp op = new RescaleOp(scales, offsets, null);
			if (choiceStage) {
				if (Arrays.toString(theGame.getNeighbors()).contains(i + "")) {
					op = new RescaleOp(scalesR, offsetsR, null);
				}
			} else if (Arrays.toString(poss).contains(i + "")) {
				op = new RescaleOp(scalesT, offsetsT, null);
			}
			if (i == theGame.position) {
				op = new RescaleOp(scalesA, offsetsA, null);
			}

			//Draw the coloumns' invisible part (height)
			for (int j = height - 1; j > 0; j--) {
				g.fillOval(x - j * 0 * goodsSize / 20, y - j * goodsSize / 20, goodsSize - j, goodsSize - j);
				g.setColor(Color.LIGHT_GRAY);
				g.drawOval(x - j * 0 * goodsSize / 20, y - j * goodsSize / 20, goodsSize - j, goodsSize - j);
				g.drawOval(x - j * 0 * goodsSize / 20, y + 1 - j * goodsSize / 20, goodsSize - j, goodsSize - j);
				g.setColor(Color.DARK_GRAY);
			}
			//Draw the coloums's top good
			g.drawImage(op.filter(goodImgs.get(imgNr), null), x, y, x + goodsSize, y + goodsSize, 0, 0, 370, 370, null);
			bounds.add(new Rectangle(x, y, goodsSize, goodsSize));

		}
		//Draw the sinking goods
		if (sinkImg > -1) {
			g.drawImage(goodImgs.get(sinkImg), thrownGoodX, Math.round(thrownGoodY[0]), thrownGoodX + goodsSize,
							Math.round(thrownGoodY[0]) + goodsSize, 0, 0, 370, 370, null);
		}
		if (sinkImg2 > -1) {
			g.drawImage(goodImgs.get(sinkImg2), Math.round(keptGoodX[0]), Math.round(keptGoodY[0]),
							Math.round(keptGoodX[0]) + goodsSize, Math.round(keptGoodY[0]) + goodsSize, 0, 0, 370, 370, null);
		}

		// Draw the figure
		//int figureSize = Math.round(goodsSize / 5);
		x = (int) Math.round(center[0] + coeff * Math.cos(angleFig[0]));
		y = (int) Math.round(center[1] + coeff * Math.sin(angleFig[0]));
		g.drawImage(figureImg, x + figureSize, y + figureSize, x + goodsSize - figureSize,
						y + goodsSize - figureSize, 0, 0, 300, 300, null);

		//Draw win counters
		for (int i = 0; i < theGame.numPlayers; i++) {
			g.drawString(wins.get(i) + "", fifth / 2 + i * 2 * fifth, w / 50 + 5);
		}

		//Draw who's turn it is
		//Dynamic solution --shrinking size
		/*FontMetrics metrics = g.getFontMetrics(fontB);
    String turnText = theGame.players.get(theGame.actualPlayer).getName() + "\'s turn";
    int textWidth = metrics.stringWidth(turnText);
    if (textWidth > coeff * 2 - goodsSize) {
      fontB = fontB.deriveFont((float) (fontB.getSize() * (coeff * 2 - goodsSize) / textWidth));
      g.setFont(fontB);
      metrics = g.getFontMetrics(fontB);
    }
    int textX = (int) Math.round((center[0] - metrics.stringWidth(turnText) / 2.) + w / 20.);
    g.drawString(turnText, textX, (int) Math.round(center[1] + w / 20));*/
		//Fixed solution --positioning
		String turnText = theGame.players.get(theGame.actualPlayer).getName() + "\'s turn";
		g.drawString(turnText, (int) Math.round(w / 15f), stockStartV - g.getFont().getSize());

		//Draw hints
		if (hints) {
			g.setFont(fontHint);
			String[] print = makeHints();
			for (int i = 0; i < print.length; i++) {
				g.drawString(print[i], w / 10, w * 5 / 10 + i * w / 40);
			}
		}

	}

	public String[] makeHints() {
		String[] returnHints = new String[theGame.numPlayers * 2];
		Player[] hintPlayers = theGame.players.toArray(new Player[theGame.players.size()]);
		for (int i = 0; i < theGame.numPlayers; i++) {
			String stndrdth = "";
			switch (i) {
				case 0:
					stndrdth = "st";
					break;
				case 1:
					stndrdth = "nd";
					break;
				case 2:
					stndrdth = "rd";
					break;
				case 3:
					stndrdth = "th";
					break;
			}
			returnHints[i * 2] = (i + 1) + stndrdth + " player " + hintPlayers[i].getName()
							+ " has " + hintPlayers[i].getPoints() + " points";
			String temp = "";
			for (String good : hintPlayers[i].goods) {
				temp += good + " (" + theGame.prices[theGame.goodTypes.indexOf(good)] + ") ";
			}
			returnHints[i * 2 + 1] = hintPlayers[i].getName() + " has Drawn Yet: " + temp;
		}
		return returnHints;
	}

	public void setHints(boolean onoff) {
		if (onoff) {
			hints = true;
			repaint();
		} else {
			hints = false;
			this.setPreferredSize(new Dimension(950, 550));
			repaint();
		}
	}

	///////////////////////////////////////////////////////////////
	//TODO: put these in GameClass
	///////////////////////////////////////////////////////////////
	public void aiPoint() {
		System.out.println("aiPoint called\t actual: " + theGame.players.get(theGame.actualPlayer).getName());
		if (theGame.players.get(theGame.actualPlayer) instanceof AI && !theGame.gameOver) {
			timerAI.start();
		}
	}

	public void aiChoice() {
		System.out.println("aiChoice run");
		if (theGame.players.get(theGame.actualPlayer) instanceof AI && !theGame.gameOver) {
			int pointNr;
			if (!choiceStage) {
				System.out.println("aiChoice choiceStage");
				AIchoice = theGame.getAIMove();
				pointNr = AIchoice[0];
			} else {
				System.out.println("aiChoice stepStage");
				pointNr = AIchoice[1];
			}
			runHumanRound(pointNr);
			//timerAI.stop();
		} else
			timerAI.stop();
	}

	public void runHumanRound(int pointNr) {
		//System.out.println("\trunHumanRound started\tgamover: " + theGame.gameOver + "\tcoloumns: " + theGame.coloumns.size());
		if (!theGame.gameOver) {
			if (!choiceStage) {
				System.out.println("Step Stage started");
				if (Arrays.toString(poss).contains(pointNr + "")) {
					theGame.position = pointNr;
					angleFig[1] = theGame.position * (Math.PI * 2 / theGame.getNrCols());
					choiceStage = !choiceStage;
				}
			} else {
				System.out.println("Choice Stage started");
				int out;
				if (Arrays.toString(theGame.getNeighbors()).contains(pointNr + "")) {
					if (theGame.getNeighbors()[0] == pointNr) {
						choiceStage = !choiceStage;
						out = 1;
					} else {
						choiceStage = !choiceStage;
						out = 0;
					}
					sinking = theGame.getNeighbors()[out];
					sinking2 = theGame.getNeighbors()[1 - out];
					sinkStr = theGame.coloumns.get(sinking).getTop();
					sinkStr2 = theGame.coloumns.get(sinking2).getTop();
					thrownGoodY[2] = -1;
					keptGoodY[2] = -1;
					keptGoodX[1] = (theGame.actualPlayer * 2 * fifth);
					for (int i = 0; i < 6; i++) {
						pricesDiff[i] = theGame.prices[i];
					}
					fallenCols = theGame.handleChoice(pointNr, theGame.getNeighbors()[out]);
					for (int i = 0; i < 6; i++) {
						pricesDiff[i + 6] = theGame.prices[i];
					}
					angleFig[1] = theGame.position * (Math.PI * 2 / theGame.getNrCols());
					//repaint();

					timerPrices.setInitialDelay(ANIM_INIT_DELAY);
					timerPrices.setDelay(ANIM_TICK);
					timerPrices.start();

					timerGoods.setInitialDelay(ANIM_INIT_DELAY);
					timerGoods.setDelay(ANIM_TICK);
					timerGoods.start();

					timerGoods.setInitialDelay(ANIM_INIT_DELAY);
					timerGoods.setDelay(ANIM_TICK);
					timerGoods.start();
				}
				timerAI.stop();
			}
			System.out.println("ai started in runhuman");
			//aiPoint();///////////////////////////////////////////////////////////////////////////////////////////////
			timerAI.setDelay(AI_SPEED);
			timerAI.start();
		}
		//repaint();

		timerFig.setInitialDelay(ANIM_INIT_DELAY);
		timerFig.setDelay(ANIM_TICK);
		timerFig.start();

		timerCols.setInitialDelay(ANIM_INIT_DELAY);
		timerCols.setDelay(ANIM_TICK);
		timerCols.start();

		if (theGame.gameOver) {
			winner = getWinner();
			boolean aiall = true;
			int i = 0;
			do {
				aiall = aiall && (theGame.players.get(i++) instanceof AI);
			} while (i < playerNames.size() && aiall);
			int n;
			if (aiall) {
				n = 2;
			} else {
				n = gameOverPopup();
			}

			switch (n) {
				case 2:
					reStart();
					break;
				case 1:
					JFrame temp = (JFrame) SwingUtilities.getWindowAncestor(returnThis());
					JPanel temppan = (JPanel) temp.getContentPane();
					JPanel temppan2 = (JPanel) temppan.getComponent(0);
					JButton tempbutt = (JButton) temppan2.getComponent(temppan2.getComponentCount() - 1);
					tempbutt.doClick(10);
					temp.setVisible(false);
					break;
				case 0:
					System.exit(0);
			}
		}
	}

	public void reStart() {
		angleFig[0] = angleFig[1] = 0;
		int playAgains = playerNames.size();
		System.out.println("last starter: " + lastStarter);
		theGame = new GameClass(playAgains, (++lastStarter) % playAgains, playerNames);
		pricesDiff = new float[]{7, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6};
		runHumanRound(-1);
		System.out.println("next starter: " + theGame.actualPlayer + "\n");
		//repaint();
		//Move the coloumns to their starting position
		for (int i = 0; i < theGame.getNrCols(); i++) {
			//Calculate the coloumns' angle values
			angleCols[i] = i * (Math.PI * 2 / theGame.getNrCols());
			angleCols[i + 9] = i * (Math.PI * 2 / theGame.getNrCols());
		}
		repaint();
	}

	public ArrayList<Integer> getWinner() {
		ArrayList<Integer> returnWinner = new ArrayList();
		ArrayList<Integer> points = new ArrayList();
		for (int i = 0; i < playerNames.size(); i++) {
			points.add(theGame.players.get(i).getPoints());
		}
		int max = Collections.max(points);
		for (int i = 0; i < playerNames.size(); i++) {
			if (points.get(i) == max) {
				returnWinner.add(i);
				wins.set(i, wins.get(i) + 1);
			}
		}
		System.out.println(points.toString() + "\tmax: " + max + "\t indexofit: " + points.indexOf(max) + " "
						+ returnWinner.toString() + " wins " + wins.toString());
		return returnWinner;

	}

	public int gameOverPopup() {
		if (returnThis().isVisible()) {
			//System.out.println("popup called");
			String helpString = "";
			for (int i = 0; i < winner.size() - 1; i++) {
				helpString += theGame.players.get(winner.get(i)).getName() + ", ";
				System.out.println(helpString);
			}
			helpString += theGame.players.get(winner.get(winner.size() - 1)).getName();
			helpString += " WINS!\n\n";
			System.out.println(helpString);
			for (int i = 0; i < playerNames.size(); i++) {
				int points = theGame.players.get(i).getPoints();
				String name = theGame.players.get(i).getName();
				helpString += "Player " + (i + 1) + ": " + name + " had " + points + " points\n";
			}
			Object[] options = {"quit", "Change players", "REVENGE"};
			return JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(this), helpString, "Game Over",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		} else {
			return -1;
		}
	}

	/////////////////////////////////////////////
	//Animation
	////////////////////////////////////////////
	public void moveFigure() {
		repaint();
		//Rotate the figure
		//System.out.printf("again: nrcols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
		//        nrcols, theGame.getNrCols(), theGame.position, angleFig[0], angleFig[1]);
		double figDiff;
		angleFig[0] -= ((int) (angleFig[0] / (Math.PI * 2.))) * Math.PI * 2.;
		//System.out.printf("modif: nrcols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
		//        nrcols, theGame.getNrCols(), theGame.position, angleFig[0], angleFig[1]);
		if (angleFig[0] > angleFig[1] && nrcols == theGame.getNrCols()) {
			figDiff = -Math.PI * 2 + angleFig[0] - angleFig[1];
		} else {
			figDiff = angleFig[0] - angleFig[1];
		}
		if (Math.abs(figDiff) > 0.01) {
			repaint();
			angleFig[0] -= figDiff / 2;

		} else {
			//    System.out.printf(" last: nrcols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
			//            nrcols, theGame.getNrCols(), theGame.position, angleFig[0], angleFig[1]);
			nrcols = theGame.getNrCols();
			//    System.out.printf("after: nrcols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
			//            nrcols, theGame.getNrCols(), theGame.position, angleFig[0], angleFig[1]);
			angleFig[0] = angleFig[1];
			timerFig.stop();
		}
	}

	public void moveCols() {//timer2
		repaint();
		double sumDiff = 0;
		double[] colsDiff = new double[9];
		if (nrcols > theGame.getNrCols() && theGame.getNrCols() < 9) {
			for (int i = 0; i < theGame.getNrCols(); i++) {
				//System.out.println("fallenCols: " + Arrays.toString(fallenCols));
				boolean first = (i >= fallenCols[0] && fallenCols[0] >= 0);
				boolean second = (i >= fallenCols[1] && fallenCols[1] >= 0);
				//System.out.println("first: " + first + "\tsecond: " + second);
				if (first || second) {
					//System.out.println("One step off: " + i);
					if (first && second) {
						angleCols[i] = angleCols[i + 2];
						//System.out.println("Two step off: " + i);
					} else {
						angleCols[i] = angleCols[i + 1];
						//System.out.println("One step off: " + i);
					}
				}

				angleCols[i] -= ((int) (angleCols[i] / (Math.PI * 2.))) * Math.PI * 2.;
				colsDiff[i] = angleCols[i] - angleCols[i + 9];
				sumDiff += colsDiff[i];

			}
			fallenCols[0] = -1;
			fallenCols[1] = -1;
		}

		//System.out.println(" Sum: " + sumDiff);
		if (Math.abs(sumDiff) > 0.05) {
			repaint();
			for (int i = 0; i < theGame.getNrCols(); i++) {
				angleCols[i] -= colsDiff[i] / 2;
			}
		} else {
			for (int i = 0; i < theGame.getNrCols(); i++) {
				angleCols[i] = angleCols[i + 9];
			}
			//System.out.println("Timer arrived");
			timerCols.stop();
		}

	}

	public void movePrices() {
		int sum = 0;
		for (int i = 0; i < 6; i++) {
			if (!(pricesDiff[i] == pricesDiff[i + 6])) {
				float diff = pricesDiff[i] - pricesDiff[i + 6];
				if (diff > 0.01) {
					sum++;
					pricesDiff[i] -= (pricesDiff[i] - pricesDiff[i + 6]) / 3f;
					repaint();
				} else {
					pricesDiff[i] = pricesDiff[i + 6];
				}
			}
		}
		if (sum == 0) {
			timerPrices.stop();
		}

	}

	public void moveGoods() {
		//System.out.println("moveGoods event");
		sinkImg = theGame.goodTypes.indexOf(sinkStr);
		sinkImg2 = theGame.goodTypes.indexOf(sinkStr2);
		if (thrownGoodY[2] > 0) {
			thrownGoodY[0] = thrownGoodY[2];
			thrownGoodY[2] = 0;
		}
		if (keptGoodY[2] > 0) {
			keptGoodY[0] = keptGoodY[2];
			keptGoodY[2] = 0;
		}
		thrownGoodY[1] = 800;
		keptGoodY[1] = -goodsSize;
		float diff = thrownGoodY[1] - thrownGoodY[0];
		float diff2 = keptGoodY[0] - keptGoodY[1];
		float diff3 = keptGoodX[0] - keptGoodX[1];
		if (diff > 0.1) {//The good which is thrown away
			//sinkY[0] += Math.sqrt(diff) / 2;
			thrownGoodY[0] += diff / 10f;
			int extraSpace = (int) Math.round(diff / 10f);
			repaint(thrownGoodX, Math.round(thrownGoodY[0] - extraSpace), goodsSize, goodsSize + extraSpace);
		} else {
			thrownGoodY[0] = thrownGoodY[1];
			sinking = -1;
			sinkImg = -1;
			thrownGoodY[2] = -1;
		}
		if (Math.abs(diff3) > 1) {
			//sinkX2[0] -= Math.sqrt(Math.abs(diff3)) * Math.signum(diff3);
			keptGoodX[0] -= diff3 / 3f;
			int extraSpace = (int) Math.round(diff3 / 4f);
			repaint(Math.round(keptGoodX[0]), Math.round(keptGoodY[0]), goodsSize + extraSpace, goodsSize);
		} 
		if (diff2 > 1) {
			//keptGoodY[0] -= Math.sqrt(diff2);
			keptGoodY[0] -= diff2 / 7f;
			int extraSpace = (int) Math.round(diff2 / 2f);
			repaint(Math.round(keptGoodX[0]), Math.round(keptGoodY[0]) - extraSpace, goodsSize, goodsSize + extraSpace);
		} else {
			keptGoodY[0] = keptGoodY[1];
			sinking2 = -1;
			sinkImg2 = -1;
			keptGoodY[2] = -1;
		}
		if (diff < 1 && diff2 < 1) {
//      timer3.cancel();
			//System.out.println("moveGoods has stopped");
			timerGoods.stop();
		}

	}

}
