package stockexchange;

/* Gergo Kovats */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class GamePanel extends JPanel implements GamePanelInterface {

	GameInterface interf;
	double[] center = new double[2];// = {600, 220};
	int h;
	int w;
	//stock image
	int stockStartV;// = Math.round(w / 10f);
	int stockWidth;// = Math.round(w * 0.35f);
	//price indicators
	int rectsStartW;// = Math.round(w * 0.04f) + w / 15;
	int rectsStepW;// = Math.round(w * 0.038f);
	int rectsStartH;// = Math.round(w * 0.169f);
	int rectsStepH;// = Math.round(w * 0.048f);
	int rectsSize;// = Math.round(w * 0.03f);

	private final ArrayList<BufferedImage> commodityImgs = new ArrayList();
	private final BufferedImage stockImg;
	private final BufferedImage figureImg;
	private ArrayList<Ellipse2D> bounds = new ArrayList();

	int[] possibleCols;
	boolean choiceStage = false;
	int nrCols = GameClass.START_NR_COLOUMS;
	int nrGameCols = GameClass.START_NR_COLOUMS;
	int[] colSizes;

	Font fontB = new Font("Arial Black", 0, 23);
	Font fontHint = new Font("Arial", 0, 15);

	Color[] priceColors = {new Color(0, 0, 255, 170), new Color(255, 255, 255, 170), new Color(0, 0, 0, 170),
		new Color(0, 255, 0, 170), new Color(205, 130, 80, 170), new Color(255, 255, 0, 170)};
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
	ArrayList<Commodity> topGoods;
	int actualPlayer;

	Dimension dimensions;

	int position;
	double[] angleFig = new double[2];
	double[] angleCols = new double[18];

	int[] fallenCols = {-1, -1};
	int sinking = -1;
	int sinking2 = -1;
	BufferedImage sinkImg;// = -1;
	BufferedImage sinkImg2;// = -1;
	int soldGoodX;
	//int sinkX1;
	float[] keptGoodX = new float[2];//the kept item
	float[] keptGoodY = new float[3];
	float[] soldGoodY = new float[3];//the thrown good: from, to, hasarrived(-1)

	float[] pricesDiff = new float[GameClass.COMMODITY_TYPES.length * 2];//{7, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6};

	boolean hints = false;
	ArrayList<Integer> winner;

	double[] figureFromTo = new double[4];
	double[] coordDiff = new double[2];

	int goodsSize;
	int scoreTablePosition;
	int figureSize;

	final int ANIM_INIT_DELAY = 0;
	final int ANIM_TICK = 10;

	javax.swing.Timer timerFig = new javax.swing.Timer(ANIM_TICK, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			moveFigure();
			//System.out.println("Figure moved");
			repaint();
		}
	});

	javax.swing.Timer timerAll = new javax.swing.Timer(ANIM_TICK, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (moveCols() & moveGoods() & movePrices() & moveFigure()) {
				timerAll.stop();
				System.out.println("All stopped");
			} else {
				//System.out.println("All moved");
				repaint();
			}
		}
	});

	public GamePanel(int[] dims) throws IOException {///////////////////////NEM KELL A PACK///////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////e.getSource():
		////////////////////////////////////////////////////////////////////actionListenert adni a panel-hez

		System.out.println(this.getClass().getResource(""));
		URL imageURL = this.getClass().getResource("/images/Wheat.png");
		commodityImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Sugar2.png");
		commodityImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Coffee.png");
		commodityImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Rice.png");
		commodityImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Cocoa.png");
		commodityImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Corn.png");
		commodityImgs.add(ImageIO.read(imageURL));
		imageURL = this.getClass().getResource("/images/Stock.jpg");
		stockImg = ImageIO.read(imageURL);
		URL imageURL2 = this.getClass().getResource("/images/Figure.png");
		figureImg = ImageIO.read(imageURL2);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//repaint();
				Point point = e.getPoint();
				//System.out.println(e.getSource());
				interf.setClickedColoumn(getClickedCol(point));
			}
		});
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == 'p')
					interf.pause();
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == 'p')
					/*pause(false)*/;
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreshSizes();
			}
		});
		timerFig.setInitialDelay(ANIM_INIT_DELAY);
		timerAll.setInitialDelay(ANIM_INIT_DELAY);
	}

//	public GamePanel returnThis() {
//		return this;
//	}
	@Override
	public void quitGame() {
		JFrame temp = (JFrame) SwingUtilities.getWindowAncestor(this);
		JPanel temppan = (JPanel) temp.getContentPane();
		JPanel temppan2 = (JPanel) temppan.getComponent(2);
		JButton tempbutt = (JButton) temppan2.getComponent(temppan2.getComponentCount() - 1);
		tempbutt.doClick(10);
		temp.setVisible(false);
	}

	public int pausePopup() {
		//System.out.println("popup called");
		Object[] options = {"Quit", "Change players", "Continue"};
		return JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(this), "", "Game Paused",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
	}

	private void refreshSizes() {
		////drawing constants only changing when panel resized
		//global values
		w = this.getWidth();
		h = this.getHeight();
		//stock image
		stockStartV = Math.round(w / 10f);
		stockWidth = Math.round(w * 0.35f);
		//price indicators
		rectsStartW = Math.round(w * 0.04f) + w / 15;
		rectsStepW = Math.round(w * 0.038f);
		rectsStartH = Math.round(w * 0.169f);
		rectsStepH = Math.round(w * 0.048f);
		rectsSize = Math.round(w * 0.03f);

		scoreTablePosition = w / (playerNames.length * 2 - 1);
		//font
		fontB = fontB.deriveFont((float) (w * 0.013) + 8);
		fontHint = fontHint.deriveFont((float) (w * 0.008) + 8);
		//coloumns
		center[0] = w * 2 / 3;
		center[1] = w * 13 / 50;
		goodsSize = Math.round(w / 10f);
		//figure
		figureSize = Math.round(goodsSize / 5);
		//timerAll.start();
		repaint();
	}

	public int getClickedCol(Point pnt) {
		int col = -1;
		for (int i = 0; i < nrGameCols; i++) {
			if (bounds.get(i).contains(pnt)) {
				col = i;
				break;
			}
		}
		return col;
	}

	private BufferedImage getImage(Commodity commodity) {
		int imageNr = -1;
		for (int i = 0; i < GameClass.COMMODITY_TYPES.length; i++) {
			if (commodity.equals(GameClass.COMMODITY_TYPES[i]))
				imageNr = i;
		}
		if (imageNr > -1)
			return commodityImgs.get(imageNr);
		else
			return null;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(950, 550);
	}

	// Inherited from GamePanelInterface
	@Override
	public void setNrGameCols(int i) {
		nrGameCols = i;
	}

	public void setInterface(GameInterface interf) {
		this.interf = interf;
	}

	// Inherited from GamePanelInterface
	@Override
	public void start(ArrayList<Commodity> topGoods, String[] playerNames, int[] sizes, int[] wins) {
		this.topGoods = topGoods;
		this.wins = wins;
		this.playerNames = playerNames;
		colSizes = sizes;
		setNrGameCols(sizes.length);
		angleFig[0] = angleFig[1] = 0;
		for (int i = 0; i < GameClass.COMMODITY_TYPES.length; i++) {
			pricesDiff[i] = pricesDiff[i + GameClass.COMMODITY_TYPES.length] = GameClass.COMMODITY_TYPES[i].getStartPrice();
		}
		//Move the coloumns to their starting position
		for (int i = 0; i < nrGameCols; i++) {
			//Calculate the coloumns' angle values
			angleCols[i] = i * (Math.PI * 2 / nrGameCols);
			angleCols[i + GameClass.START_NR_COLOUMS] = i * (Math.PI * 2 / nrGameCols);
		}
		possibleCols = new int[]{1, 2, 3};
		repaint();
	}

	// Inherited from GamePanelInterface
	@Override
	public void setPossible(int[] possibleColoumns) {
		possibleCols = possibleColoumns;
		//System.out.println("possible" + Arrays.toString(possibleCols));
		choiceStage = true;
	}

	// Inherited from GamePanelInterface
	@Override
	public void setFigure(int destination) {
		angleFig[1] = destination * (Math.PI * 2 / nrGameCols);
		position = destination;
		startMoveFigure();
	}

	// Inherited from GamePanelInterface
	@Override
	public void makeChoice(int kept, int sold, int[] emptiedColoumns, int[] prices, ArrayList<Commodity> topGoods, int[] sizes) {
		afterMoveGoods();
		repaint();
		sinking = sold;//get the kept commodity
		sinking2 = kept;//get the sold commodity
		//System.out.println("kept: " + kept + "\tsold: " + sold);
		sinkImg = getImage(this.topGoods.get(sold));
		//int[] rgbA = new int[1000];
		//Arrays.fill(rgbA, 200);
		//sinkImg.setRGB(0, 0, 50, 50, rgbA, 0, 0);
		sinkImg2 = getImage(this.topGoods.get(kept));
		setNrGameCols(sizes.length);

		soldGoodY[2] = -1;
		keptGoodY[2] = -1;
		keptGoodX[1] = ((actualPlayer * 2 + 0.5f) * scoreTablePosition);
		//Set prices
		for (int i = 0; i < 6; i++) {
			pricesDiff[i + 6] = prices[i];
		}

		fallenCols = emptiedColoumns;
		colSizes = sizes;
		this.topGoods = topGoods;
		choiceStage = false;
		//Start animating objects
		beforeMoveCols();
		timerAll.start();
		actualPlayer = (actualPlayer + 1) % playerNames.length;
	}

	// Inherited from GamePanelInterface
	@Override
	public void setHint(String[] hintText) {
		this.hintText = hintText;
	}

	// Inherited from JPanel
	@Override
	public void paintComponent(Graphics g) {
		if (true) {//to turn off drawing completely so to test AIs
			//<editor-fold defaultstate="collapsed" desc="comment">
			super.paintComponent(g);

			//Draw the stock image
			g.drawImage(stockImg, w / 15, stockStartV, stockWidth + w / 15,
							stockStartV + stockWidth, 0, 0, 1100, 1100, null);

			//Draw the price indicators
			g.setColor(priceColors[0]);
			g.fillRect(Math.round(rectsStartW + (8 - pricesDiff[0]) * rectsStepW),
							rectsStartH + 0 * rectsStepH, rectsSize, rectsSize);

			for (int i = 1; i < 6; i++) {
				g.setColor(priceColors[i]);
				g.fillRect(Math.round(rectsStartW + (7 - pricesDiff[i]) * rectsStepW),
								rectsStartH + i * rectsStepH, rectsSize, rectsSize);
			}

			g.setColor(Color.BLACK);
			int x;
			int y;
			g.setColor(Color.BLACK);
			g.setFont(fontB);

			bounds.clear();

			//
			//Draw each coloumns
			//
			int before = (position + 1) % nrGameCols;
			int after = (position + nrGameCols - 1) % nrGameCols;
			int coeff = Math.round(w * (float) Math.sqrt(nrGameCols / 9f) / 5f);
			/////////////////////////////////////////////////////////nem jo, es nem is itt kellene
			/////////////////////////////////////////////////////////TODO: kept&sold moving commodities
			for (int i = 0; i < nrGameCols; i++) {
				//Calculate values
				x = (int) (center[0] + coeff * Math.cos(angleCols[i]));
				y = (int) (center[1] + coeff * Math.sin(angleCols[i]));
				//String topGood = topGoods.get(i).toString();
				int height = colSizes[i];
				if (sinking == i && soldGoodY[2] == -1) {
					//System.out.println("sinking1 " + i);
					soldGoodX = x;
					soldGoodY[2] = y;
				}
				if (sinking2 == i && keptGoodY[2] == -1) {
					keptGoodX[0] = x;
					keptGoodY[2] = y;
				}
				if (hints) {
					g.drawString(height + "", x, y + goodsSize);
				}
				/////////////////////////////////////////////////////////////////////
				// Set the coloumns' top good's coloring based on playability
				RescaleOp op = new RescaleOp(scales, offsets, null);
				if (choiceStage) {

					if (i == before || i == after) {
						op = new RescaleOp(scalesR, offsetsR, null);
					}
				} else if (Arrays.toString(possibleCols).contains(i + "")) {
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
				g.drawImage(op.filter(getImage(topGoods.get(i)), null), x, y, x + goodsSize, y + goodsSize, 0, 0, 370, 370, null);
				//bounds.add(new Rectangle(x, y, goodsSize, goodsSize));
				bounds.add(new Ellipse2D.Float(x, y, goodsSize, goodsSize));
//				g.drawOval((int)bounds.get(i).getX(), (int)bounds.get(i).getY(),
//								(int)bounds.get(i).getWidth(), (int)bounds.get(i).getHeight());

			}
			//Draw the sinking goods
			if (sinkImg != null & soldGoodY[0] != soldGoodY[1]) {//aeigo834wbfklsdbovisebuioghwerl
				g.drawImage(sinkImg, soldGoodX, Math.round(soldGoodY[0]), soldGoodX + goodsSize,
								Math.round(soldGoodY[0]) + goodsSize, 0, 0, 370, 370, null);
			}
			if (sinkImg2 != null & keptGoodY[0] != keptGoodY[1]) {
				g.drawImage(sinkImg2, Math.round(keptGoodX[0]), Math.round(keptGoodY[0]),
								Math.round(keptGoodX[0]) + goodsSize, Math.round(keptGoodY[0]) + goodsSize, 0, 0, 370, 370, null);
			}

			// Draw the figure
			x = (int) Math.round(center[0] + coeff * Math.cos(angleFig[0]));
			y = (int) Math.round(center[1] + coeff * Math.sin(angleFig[0]));
			g.drawImage(figureImg, x + figureSize, y + figureSize, x + goodsSize - figureSize,
							y + goodsSize - figureSize, 0, 0, 300, 300, null);

			//Draw win counters
			for (int i = 0; i < playerNames.length; i++) {
				g.drawString(wins[i] + "", scoreTablePosition / 2 + i * 2 * scoreTablePosition, w / 50 + 5);
			}

			//Draw who's turn it is
			String turnText = playerNames[actualPlayer] + "\'s turn";
			g.drawString(turnText, (int) Math.round(w / 15f), stockStartV - g.getFont().getSize());

			//Draw hints
			if (hints) {
				g.setFont(fontHint);
				for (int i = 0; i < hintText.length; i++) {
					g.drawString(hintText[i], w / 15, w * 6 / 10 + i * w / 40);
				}
			}
		}
//</editor-fold>
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
		//System.out.println("popup called");
		Object[] options = {"Quit", "Change players", "REVENGE"};
		return JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(this), helpString, "Game Over",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
	}

	/////////////////////////////////////////////
	//Animation
	////////////////////////////////////////////
	private void startMoveFigure() {
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
		if (Math.abs(figDiff) > 0.05) {
			//repaint();
			angleFig[0] -= figDiff / 7;
			//System.out.println("angle0 " + angleFig[0]);
			finished = false;
		} else {
			//    System.out.printf(" last: nrCols %d   thegamenrcols %d |  thegameposi %d |  angle0 %1.3f   angle1 %1.3f%n",
			//            nrCols, nrGameCols, position, angleFig[0], angleFig[1]);
			angleFig[0] = angleFig[1];
			//System.out.println("MoveFig has stopped");
			timerFig.stop();
			finished = true;
		}
		return finished;
	}

	private void beforeMoveCols() {
		//System.out.println("beforeMove nrCols: " + nrCols + "\tnrGameCols: " + nrGameCols);
		if (nrCols > nrGameCols /*&& nrGameCols < 9*/) {
			for (int i = 0; i < nrGameCols; i++) {
				angleCols[i + GameClass.START_NR_COLOUMS] = i * (Math.PI * 2 / nrGameCols);///////////////////nrGameCols???
				//System.out.println("fallenCols: " + Arrays.toString(fallenCols));
				boolean first = (fallenCols[0] >= 0 && i >= fallenCols[0]);
				boolean second = (fallenCols[1] >= 0 && i >= fallenCols[1]);
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
			}
		}
		//nrCols = nrGameCols;
		fallenCols[0] = -1;
		fallenCols[1] = -1;
	}

	private boolean moveCols() {//timer2
		//repaint();
		//System.out.println("moveCols nrCols: " + nrCols + "\tnrGameCols: " + nrGameCols);
		double sumDiff = 0;
		double[] colsDiff = new double[nrGameCols];
		//if (nrCols > nrGameCols /*&& nrGameCols < 9*/) {
		for (int i = 0; i < nrGameCols; i++) {
			angleCols[i] -= ((int) (angleCols[i] / (Math.PI * 2.))) * Math.PI * 2.;
			colsDiff[i] = angleCols[i] - angleCols[i + GameClass.START_NR_COLOUMS];
			sumDiff += Math.abs(colsDiff[i]);
		}

		//}
		boolean finished = false;
		//System.out.println(" Sum: " + sumDiff);
		if (Math.abs(sumDiff) > 0.02) {
			//repaint();
			for (int i = 0; i < nrGameCols; i++) {
				angleCols[i] -= colsDiff[i] / 5;
			}
			//System.out.println(Arrays.toString(colsDiff));
		} else {
			for (int i = 0; i < nrGameCols; i++) {//////////////////////////////////////////////////////////////////////////////////
				angleCols[i] = angleCols[i + GameClass.START_NR_COLOUMS];
			}
			//System.out.println("MoveCols has stopped");
			//timerCols.stop();
			nrCols = nrGameCols;
			finished = true;
		}
		return finished;
	}

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

	private boolean moveGoods() {
		boolean finished = false;
		//System.out.println("moveGoods event " + sinking + " sinking2 " + sinking2);

		if (soldGoodY[2] > 0) {
			soldGoodY[0] = soldGoodY[2];
			soldGoodY[2] = 0;
		}
		if (keptGoodY[2] > 0) {
			keptGoodY[0] = keptGoodY[2];
			keptGoodY[2] = 0;
		}
		soldGoodY[1] = h;
		keptGoodY[1] = -goodsSize;
		float diff = soldGoodY[1] - soldGoodY[0];
		float diff2 = keptGoodY[0] - keptGoodY[1];
		float diff3 = keptGoodX[0] - keptGoodX[1];
		//System.out.println("diff1 " + diff + "\tdiff2 " + diff2 + "\tdiff3 " + diff3);
		if (diff > 1) {
			//sinkY[0] += Math.sqrt(diff) / 2;
			soldGoodY[0] += Math.max(1, diff / 8f);
//			int extraSpace = (int) Math.round(diff / 10f);
//			repaint(soldGoodX, Math.round(soldGoodY[0] - extraSpace), goodsSize, goodsSize + extraSpace);
		}
		if (Math.abs(diff3) > 1) {
			//sinkX2[0] -= Math.sqrt(Math.abs(diff3)) * Math.signum(diff3);
			keptGoodX[0] -= Math.max(1, Math.abs(diff3 / 3f)) * Math.signum(diff3);
//			int extraSpace = (int) Math.round(diff3 / 4f);
//			repaint(Math.round(keptGoodX[0]), Math.round(keptGoodY[0]), goodsSize + extraSpace, goodsSize);
		}
		if (diff2 > 1) {
			//keptGoodY[0] -= Math.sqrt(diff2);
			keptGoodY[0] -= Math.max(1, diff2 / 6f);
//			int extraSpace = (int) Math.round(diff2 / 2f);
//			repaint(Math.round(keptGoodX[0]), Math.round(keptGoodY[0]) - extraSpace, goodsSize, goodsSize + extraSpace);
		}
		if (diff < 1 && diff2 < 1) {
			finished = afterMoveGoods();
		}
		return finished;
	}

	private boolean afterMoveGoods() {
		//System.out.println("moveGoods has stopped");
		//timerGoods.stop();
		soldGoodY[0] = soldGoodY[1];
		keptGoodY[0] = keptGoodY[1];
		soldGoodY[2] = -1;
		keptGoodY[2] = -1;
		sinking = -1;
		sinking2 = -1;
		//System.out.println(diff + " " + diff2 + " " + Arrays.toString(soldGoodY) + " " + Arrays.toString(keptGoodY));
		return true;
	}

}
