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
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

class MyPanel extends AbstractGamePanel {

	//GameClass theGame;
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
	int nrCols = 9;
	int nrGameCols = 9;
	int[] colSizes;

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
	String[] playerNames;
	int[] wins;
	String[] hintText;
	ArrayList<String> topGoods;
	int actualPlayer;

	Dimension dimensions;

	int position;
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

	final int ANIM_INIT_DELAY = 10;
	final int ANIM_TICK = 30;

	javax.swing.Timer timerFig = new javax.swing.Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			moveFigure();
			//System.out.println("Figure moved");
			repaint();
		}
	});

	javax.swing.Timer timerAll = new javax.swing.Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (moveCols() & moveGoods() & movePrices() /*& moveFigure()*/) {
				timerAll.stop();
				System.out.println("All stopped");
			} else {
				//System.out.println("All moved");
				repaint();
			}
		}
	});

//	javax.swing.Timer timerCols = new javax.swing.Timer(0, new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			moveCols();
//		}
//	});
//
//	javax.swing.Timer timerGoods = new javax.swing.Timer(0, new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			moveGoods();
//		}
//	});
//
//	javax.swing.Timer timerPrices = new javax.swing.Timer(0, new ActionListener() {
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			movePrices();
//		}
//	});
	public MyPanel(int[] dims) throws IOException {
		//playerNames = playernames;
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

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				repaint();
				Point point = e.getPoint();
				////////runHumanRound(getClickedCol(point));
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
		fifth = w / (playerNames.length * 2 + 1);
		//font
		fontB = fontB.deriveFont((float) (w * 0.013) + 8);
		//coloumns
		center[0] = w * 2 / 3;
		center[1] = w * 11 / 50;
		goodsSize = Math.round(w / 10f);
		//figure
		figureSize = Math.round(goodsSize / 5);
	}

	public int getClickedCol(Point pnt) {//TODO: Human Player
		int col = -1;
		for (int i = 0; i < nrGameCols; i++) {
			if (bounds.get(i).contains(pnt)) {
				col = i;
				break;
			}
		}
		return col;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(950, 550);
	}

	// Inherited from AbstractGamePanel
	@Override
	public void start(ArrayList<String> topGoods, String[] playerNames, int[] sizes, int[] wins) {
		this.topGoods = topGoods;
		this.wins = wins;
		this.playerNames = playerNames;
		colSizes = sizes;
		setNrGameCols(sizes.length);
		angleFig[0] = angleFig[1] = 0;
		pricesDiff = new float[]{7, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6};
		//Move the coloumns to their starting position
		for (int i = 0; i < nrGameCols; i++) {
			//Calculate the coloumns' angle values
			angleCols[i] = i * (Math.PI * 2 / nrGameCols);
			angleCols[i + GameClass.START_NR_COLOUMS] = i * (Math.PI * 2 / nrGameCols);
		}
		poss = new int[]{1, 2, 3};
		repaint();
	}

	// Inherited from AbstractGamePanel
	@Override
	public void setPossible(int[] possibleColoumns) {
		poss = possibleColoumns;
		System.out.println("possible" + Arrays.toString(poss));
	}

	// Inherited from AbstractGamePanel
	@Override
	public void setFigure(int destination) {
		angleFig[1] = destination * (Math.PI * 2 / nrGameCols);
		position = destination;
		startMoveFigure();
		choiceStage = true;
	}

	// Inherited from AbstractGamePanel
	@Override
	public void makeChoice(int chosenColoumn, int[] emptiedColoumns, int[] prices, ArrayList<String> topGoods, int[] sizes) {
		setNrGameCols(sizes.length);
		int out = (position + (position - chosenColoumn) + sizes.length) % sizes.length;
		sinking = chosenColoumn;
		//System.out.println("makeChoice out " + out + " chosen " + chosenColoumn);
		sinking2 = out;
		sinkStr = this.topGoods.get(sinking);
		sinkStr2 = this.topGoods.get(sinking2);
		thrownGoodY[2] = -1;
		keptGoodY[2] = -1;
		keptGoodX[1] = (actualPlayer * 2 * fifth);
		//Set prices
		for (int i = 0; i < 6; i++) {
			pricesDiff[i + 6] = prices[i];
		}
		
		fallenCols = emptiedColoumns;
		for (int colNr : fallenCols)
			if (colNr > -1 && colNr < position) {
				setFigure(--position);
			}
		colSizes = sizes;
		choiceStage = false;
		this.topGoods = topGoods;
		//Start animating objects
		timerAll.setInitialDelay(ANIM_INIT_DELAY);
		timerAll.setDelay(ANIM_TICK);
		timerAll.start();
		actualPlayer = (actualPlayer + 1) % playerNames.length;
	}

	// Inherited from AbstractGamePanel
	@Override
	public void setHint(String[] hintText) {
		this.hintText = hintText;
	}

	// Inherited from JPanel
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
		//poss = theGame.getPossible();
		g.setColor(Color.BLACK);
		g.setFont(fontB);

		bounds.clear();

		//
		//Draw each coloumns
		//
		int before = (position + 1) % nrGameCols;
		int after = (position + nrGameCols - 1) % nrGameCols;
		int coeff = Math.round(w * (float) Math.sqrt(nrGameCols / 9f) / 5f);
		//System.out.println("SINKINGS" + sinking + " " + sinking2);
		for (int i = 0; i < nrGameCols; i++) {
			//Calculate values
			angleCols[i + 9] = i * (Math.PI * 2 / nrGameCols);
			x = (int) (center[0] + coeff * Math.cos(angleCols[i]));
			y = (int) (center[1] + coeff * Math.sin(angleCols[i]));
			String topGood = topGoods.get(i);
			int height = colSizes[i];
			if (sinking == i && thrownGoodY[2] == -1) {
				//System.out.println("sinking1 " + i);
				thrownGoodX = x;
				thrownGoodY[2] = y;
			}
			if (sinking2 == i && keptGoodY[2] == -1) {
				//System.out.println("sinking2 " + i);
				keptGoodX[0] = x;
				keptGoodY[2] = y;
			}
			if (hints) {
				g.drawString(height + "", x, y);
			}
			int imgNr = GameClass.GOOD_TYPES.indexOf(topGood);

			// Set the coloumns' top good's coloring based on playability
			RescaleOp op = new RescaleOp(scales, offsets, null);
			if (choiceStage) {

				if (i == before || i == after) {
					op = new RescaleOp(scalesR, offsetsR, null);
				}
			} else if (Arrays.toString(poss).contains(i + "")) {
				op = new RescaleOp(scalesT, offsetsT, null);
			}
			if (i == position) {
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
		for (int i = 0; i < playerNames.length; i++) {
			g.drawString(wins[i] + "", fifth / 2 + i * 2 * fifth, w / 50 + 5);
		}

		//Draw who's turn it is
		String turnText = playerNames[actualPlayer] + "\'s turn";
		g.drawString(turnText, (int) Math.round(w / 15f), stockStartV - g.getFont().getSize());

		//Draw hints
		if (hints) {
			g.setFont(fontHint);
			for (int i = 0; i < hintText.length; i++) {
				g.drawString(hintText[i], w / 10, w * 5 / 10 + i * w / 40);
			}
		}

	}

	//Called from StockExchange
	public void setHintsOnOff(boolean onoff) {
		if (onoff) {
			hints = true;
			repaint();
		} else {
			hints = false;
			this.setPreferredSize(new Dimension(950, 550));
			repaint();
		}
	}

	//Called from GameClass
	public int gameOverPopup(String helpString) {
		if (returnThis().isVisible()) {
			//System.out.println("popup called");
			//String helpString = THEGAME.gameOverString();
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

	private void startMoveFigure() {
		timerFig.setInitialDelay(ANIM_INIT_DELAY);
		timerFig.setDelay(ANIM_TICK);
		timerFig.start();
	}

	private boolean moveFigure() {
		repaint();
		boolean finished = true;
		//Rotate the figure
		//System.out.printf("again: nrCols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
		//        nrCols, nrGameCols, position, angleFig[0], angleFig[1]);
		double figDiff;
		//Keep angle between 0 and 2PI
		angleFig[0] -= ((int) (angleFig[0] / (Math.PI * 2.))) * Math.PI * 2.;
		//System.out.printf("modif: nrCols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
		//        nrCols, nrGameCols, position, angleFig[0], angleFig[1]);
		if (angleFig[0] > angleFig[1] && nrCols == nrGameCols) {
			figDiff = -Math.PI * 2 + angleFig[0] - angleFig[1];
		} else {
			figDiff = angleFig[0] - angleFig[1];
		}
		if (Math.abs(figDiff) > 0.5) {
			//repaint();
			angleFig[0] -= figDiff / 2;
			System.out.println("angle0 " + angleFig[0]);
			finished = false;
		} else {
			//    System.out.printf(" last: nrCols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
			//            nrCols, nrGameCols, position, angleFig[0], angleFig[1]);
			nrCols = nrGameCols;
			//    System.out.printf("after: nrCols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
			//            nrCols, nrGameCols, position, angleFig[0], angleFig[1]);
			angleFig[0] = angleFig[1];
			System.out.println("MoveFig has stopped");
			timerFig.stop();
			finished = true;
		}
		return finished;
	}

	//Called from GameClass (TODO: remove)
	public void setNrGameCols(int i) {
		nrGameCols = i;
	}

//	public void startMoveCols() {
//		timerCols.setInitialDelay(ANIM_INIT_DELAY);
//		timerCols.setDelay(ANIM_TICK);
//		timerCols.start();
//	}
	private boolean moveCols() {//timer2
		repaint();
		double sumDiff = 0;
		double[] colsDiff = new double[9];
		if (nrCols > nrGameCols && nrGameCols < 9) {
			for (int i = 0; i < nrGameCols; i++) {
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
		boolean finished = false;
		//System.out.println(" Sum: " + sumDiff);
		if (Math.abs(sumDiff) > 0.05) {
			//repaint();
			for (int i = 0; i < nrGameCols; i++) {
				angleCols[i] -= colsDiff[i] / 2;
			}
		} else {
			for (int i = 0; i < nrGameCols; i++) {
				angleCols[i] = angleCols[i + 9];
			}
			//System.out.println("MoveCols has stopped");
			//timerCols.stop();
			finished = true;
		}
		return finished;
	}

//	public void startMovePrices() {
//		timerPrices.setInitialDelay(ANIM_INIT_DELAY);
//		timerPrices.setDelay(ANIM_TICK);
//		timerPrices.start();
//	}
	private boolean movePrices() {
		int sum = 0;
		boolean finished = false;
		for (int i = 0; i < 6; i++) {
			if (!(pricesDiff[i] == pricesDiff[i + 6])) {
				float diff = pricesDiff[i] - pricesDiff[i + 6];
				if (diff > 0.01) {
					sum++;
					pricesDiff[i] -= (pricesDiff[i] - pricesDiff[i + 6]) / 3f;
					//repaint();
				} else {
					pricesDiff[i] = pricesDiff[i + 6];
				}
			}
		}
		if (sum == 0) {
			finished = true;
			//System.out.println("MovePrices has stopped");
			//timerPrices.stop();
		}
		return finished;
	}

//	public void startMoveGoods() {
//		timerGoods.setInitialDelay(ANIM_INIT_DELAY);
//		timerGoods.setDelay(ANIM_TICK);
//		timerGoods.start();
//	}
	private boolean moveGoods() {
		boolean finished = false;
		//System.out.println("moveGoods event " + sinking + " sinking2 " + sinking2);
		sinkImg = GameClass.GOOD_TYPES.indexOf(sinkStr);
		sinkImg2 = GameClass.GOOD_TYPES.indexOf(sinkStr2);
		if (thrownGoodY[2] > 0) {
			thrownGoodY[0] = thrownGoodY[2];
			thrownGoodY[2] = 0;
		}
		if (keptGoodY[2] > 0) {
			keptGoodY[0] = keptGoodY[2];
			keptGoodY[2] = 0;
		}
		thrownGoodY[1] = h;
		keptGoodY[1] = -goodsSize;
		float diff = thrownGoodY[1] - thrownGoodY[0];
		float diff2 = keptGoodY[0] - keptGoodY[1];
		float diff3 = keptGoodX[0] - keptGoodX[1];
		if (diff > 1) {//The good which is thrown away
			//sinkY[0] += Math.sqrt(diff) / 2;
			thrownGoodY[0] += diff / 10f;
			int extraSpace = (int) Math.round(diff / 10f);
			repaint(thrownGoodX, Math.round(thrownGoodY[0] - extraSpace), goodsSize, goodsSize + extraSpace);
		} else {
//			System.out.println("------------sinking1 arrived");
//			thrownGoodY[0] = thrownGoodY[1];
//			sinking = -1;
//			sinkImg = -1;
//			thrownGoodY[2] = -1;
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
//			System.out.println("------------sinking2 arrived");
//			keptGoodY[0] = keptGoodY[1];
//			sinking2 = -1;
//			sinkImg2 = -1;
//			keptGoodY[2] = -1;
		}
		if (diff < 1 && diff2 < 1) {
			System.out.println("moveGoods has stopped");
			//timerGoods.stop();
			finished = true;
		}
		return finished;
	}

}
