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
	float rectsStartW;// = Math.round(w * 0.04f) + w / 15;
	float rectsStepW;// = Math.round(w * 0.038f);
	float rectsStartH;// = Math.round(w * 0.169f);
	float rectsStepH;// = Math.round(w * 0.048f);
	int rectsSize;// = Math.round(w * 0.03f);
	int glowSize;

	private final ArrayList<BufferedImage> commodityImgs = new ArrayList();
	private final BufferedImage stockImg;
	private final BufferedImage figureImg;
	private final BufferedImage glowImg;
	private final BufferedImage redGlowImg;
	private ArrayList<Ellipse2D> bounds = new ArrayList();

	int[] possibleCols;
	boolean choiceStage = false;
	int nrCols = GameClass.START_NR_COLOUMS;
	int nrGameCols = GameClass.START_NR_COLOUMS;
	int[] colSizes;

	Font fontB = new Font("Arial", Font.PLAIN, 23);
	Font fontHint = new Font("Arial", Font.PLAIN, 15);

	Color[] priceColors = {new Color(0, 0, 255, 170), new Color(255, 255, 255, 170), new Color(0, 0, 0, 170),
		new Color(0, 255, 0, 170), new Color(205, 130, 80, 170), new Color(255, 255, 0, 170)};
	final float[] scales = {0.5f, 0.5f, 0.5f, 1f};
	final float[] offsets = {20, 20, 20, 0};
	final float[] scalesT = {1.1f, 1.1f, 1.1f, 1.f};
	final float[] offsetsT = {0, 0, 0, 0};
//	final float[] scalesR = {.5f, .8f, .8f, 1.f};
//	final float[] offsetsR = {150, 10, 10, 0};
	final float[] scalesR = {1.3f, 1.f, 1.f, 1.f};
	final float[] offsetsR = {20, 0, 0, 0};
	final float[] scalesA = {.9f, .9f, .9f, 1.f};
	final float[] offsetsA = {0, 0, 0, 0};
	String[] playerNames;
	int[] wins;
	String[] hintText;
	ArrayList<Commodity> topCommodities;
	int actualPlayer;

	Dimension dimensions;

	int position;
	double[] angleFig = new double[2];
	double[] angleCols = new double[GameClass.START_NR_COLOUMS * 2];

	int[] fallenCols = {-1, -1};
	int sinkingSold = -1;
	int sinkingKept = -1;
	BufferedImage sinkSoldImg;// = -1;
	BufferedImage sinkKeptImg;// = -1;
	int soldCommodityX;
	//int sinkX1;
	float[] keptCommodityX = new float[2];//the kept item
	float[] keptCommodityY = new float[2];
	float[] soldCommodityY = new float[2];//the thrown good: from, to, hasarrived(-1)

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
	boolean figureStopped = false;
	boolean colsStopped = false;
	boolean goodsStopped = false;
	boolean pricesStopped = false;

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
//			System.out.println(figureStopped + " " + colsStopped + " " + goodsStopped
//							+ " " + pricesStopped);
			if (!figureStopped)
				figureStopped = moveFigure();
			if (!colsStopped)
				colsStopped = moveCols();
			if (!goodsStopped)
				goodsStopped = moveGoods();
			if (!pricesStopped)
				pricesStopped = movePrices();
			if (figureStopped && colsStopped && goodsStopped && pricesStopped) {
				timerAll.stop();
				repaint();
				System.out.println("All stopped");
			} else {
				//System.out.println("All moved");
				//choiceStage = false;
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
		imageURL2 = this.getClass().getResource("/images/glow2.png");
		glowImg = ImageIO.read(imageURL2);
		imageURL2 = this.getClass().getResource("/images/glowR2.png");
		redGlowImg = ImageIO.read(imageURL2);

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
		setBackground(new Color(5, 15, 40));
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
		return JOptionPane.showOptionDialog(GamePanel.this, "", "Game Paused",
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
		rectsStartW = w * 0.0363f + w / 15;
		rectsStepW = w * 0.03777f;
		rectsStartH = w * 0.1633f;
		rectsStepH = w * 0.0485f;
		rectsSize = Math.round(w * 0.032f);
		glowSize = Math.round(w*0.023f);

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
		this.topCommodities = topGoods;
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
		nrCols = nrGameCols;
		repaint();
	}

	// Inherited from GamePanelInterface
	@Override
	public void setPossible(int[] possibleColoumns) {
		possibleCols = possibleColoumns;
		//choiceStage = false;
		//System.out.println("possible" + Arrays.toString(possibleCols));
	}

	// Inherited from GamePanelInterface
	@Override
	public void setFigure(int destination) {
		beforeMoveFigure(destination);
		timerFig.start();
		position = destination;
		choiceStage = !choiceStage;
	}

	// Inherited from GamePanelInterface
	@Override
	public void makeChoice(int kept, int sold, int[] emptiedColoumns, int[] prices, ArrayList<Commodity> topGoods, int[] sizes) {
		repaint();
		sinkingSold = sold;//get the kept commodity
		sinkingKept = kept;//get the sold commodity
		System.out.println("\nkept: " + kept + "\tsold: " + sold);
		sinkSoldImg = getImage(this.topCommodities.get(sold));
		sinkKeptImg = getImage(this.topCommodities.get(kept));
		setNrGameCols(sizes.length);

		//Set prices
		for (int i = 0; i < 6; i++) {
			pricesDiff[i + 6] = prices[i];
		}

		fallenCols = emptiedColoumns;
		colSizes = sizes;
		this.topCommodities = topGoods;
		//Start animating objects
		beforeMoveGoods();
		beforeMoveCols();
		//repaint();
		figureStopped = false;
		colsStopped = false;
		goodsStopped = false;
		pricesStopped = false;
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
	public void paint(Graphics g) {
		if (true) {//to turn off drawing completely so to test AIs
			//<editor-fold defaultstate="collapsed" desc="comment">
			super.paint(g);

			//Draw the stock image
			g.drawImage(stockImg, w / 15, stockStartV, stockWidth + w / 15,
							stockStartV + stockWidth, 10, 10, 1100, 1100, null);

			//Draw the price indicators
			g.setColor(priceColors[0]);
			g.fillRect(Math.round(rectsStartW + (8 - pricesDiff[0]) * rectsStepW),
							Math.round(rectsStartH + 0 * rectsStepH), rectsSize, rectsSize);

			for (int i = 1; i < 6; i++) {
				g.setColor(priceColors[i]);
				g.fillRect(Math.round(rectsStartW + (7 - pricesDiff[i]) * rectsStepW),
								Math.round(rectsStartH + i * rectsStepH), rectsSize, rectsSize);
			}

			int x;
			int y;
			g.setFont(fontB);

			bounds.clear();

			//Draw each coloumns
			int before = (position + 1) % nrGameCols;
			int after = (position + nrGameCols - 1) % nrGameCols;
			int coeff = Math.round(w * (float) Math.sqrt(nrGameCols / 9f) / 5f);
			for (int i = 0; i < nrGameCols; i++) {
				//Calculate values
				x = (int) (center[0] + coeff * Math.cos(angleCols[i]));
				y = (int) (center[1] + coeff * Math.sin(angleCols[i]));
				int height = colSizes[i];
				g.setColor(Color.LIGHT_GRAY);
				if (hints) {
					g.drawString(height + "", x, y + goodsSize);
				}

				//Draw the coloumns' invisible part (height)
				for (int j = height - 1; j > 0; j--) {
					g.setColor(Color.BLACK);//Inner
					g.fillOval(x - j * 0 * goodsSize / 20, y - j * goodsSize / 20, goodsSize - j, goodsSize - j);
					g.setColor(new Color(200,200,200));
					g.drawOval(x - j * 0 * goodsSize / 20, y - j * goodsSize / 20, goodsSize - j, goodsSize - j);
					g.setColor(new Color(150,150,150));
					g.drawOval(x - j * 0 * goodsSize / 20, y + 1 - j * goodsSize / 20, goodsSize - j, goodsSize - j);
				}
				/////////////////////////////////////////////////////////////////////
				// Set the coloumns' top good's coloring based on playability
				RescaleOp op = new RescaleOp(scales, offsets, null);
				if (choiceStage) {
					if (i == before || i == after) {
						op = new RescaleOp(scalesR, offsetsR, null);
						g.drawImage(redGlowImg, x - glowSize, y - glowSize, x + glowSize + goodsSize, y + glowSize + goodsSize, 0, 0, 400, 400, null);
					}
				} else if (Arrays.toString(possibleCols).contains(i + "")) {
					op = new RescaleOp(scalesT, offsetsT, null);
					g.drawImage(glowImg, x - glowSize, y - glowSize, x + glowSize + goodsSize, y + glowSize + goodsSize, 0, 0, 400, 400, null);
				}
				if (i == position) {
					op = new RescaleOp(scalesA, offsetsA, null);
				}

				//Draw the coloums's top good
				g.drawImage(op.filter(getImage(topCommodities.get(i)), null), x, y, x + goodsSize, y + goodsSize, 0, 0, 370, 370, null);
				bounds.add(new Ellipse2D.Float(x, y, goodsSize, goodsSize));
			}

			// Draw the figureStopped
			x = (int) Math.round(center[0] + coeff * Math.cos(angleFig[0]));
			y = (int) Math.round(center[1] + coeff * Math.sin(angleFig[0]));
			g.drawImage(figureImg, x + figureSize, y + figureSize, x + goodsSize - figureSize,
							y + goodsSize - figureSize, 0, 0, 300, 300, null);

			g.setColor(Color.LIGHT_GRAY);
			//Draw win counters
			for (int i = 0; i < playerNames.length; i++) {
				g.drawString(wins[i] + "", scoreTablePosition / 2 + i * 2 * scoreTablePosition, w / 50 + 5);
			}

			//Draw who's turn it is
			String turnText = playerNames[actualPlayer] + "\'s turn";
			g.drawString(turnText, (int) Math.round(w / 15f), stockStartV - g.getFont().getSize());

			//Draw the sinkingSold goods
			if (sinkSoldImg != null & soldCommodityY[0] != soldCommodityY[1]) {
				g.drawImage(sinkSoldImg, soldCommodityX, Math.round(soldCommodityY[0]), soldCommodityX + goodsSize,
								Math.round(soldCommodityY[0]) + goodsSize, 0, 0, 370, 370, null);
			}
			if (sinkKeptImg != null & keptCommodityY[0] != keptCommodityY[1]) {
				g.drawImage(sinkKeptImg, Math.round(keptCommodityX[0]), Math.round(keptCommodityY[0]),
								Math.round(keptCommodityX[0]) + goodsSize, Math.round(keptCommodityY[0]) + goodsSize, 0, 0, 370, 370, null);
			}

			//Draw hints
			if (hints) {
				g.setFont(fontHint);
				for (int i = 0; i < hintText.length; i++) {
					g.drawString(hintText[i], w / 15, w * 6 / 10 + i * w / 40);
				}
			}
			//Is is choice stage
			//g.drawString(Boolean.toString(choiceStage), w / 20, w * 5 / 10);
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
		return JOptionPane.showOptionDialog(GamePanel.this,
						helpString,"Game Over",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
						null, options, options[0]);
	}

	/////////////////////////////////////////////
	//Animation
	////////////////////////////////////////////
	private void beforeMoveFigure(int destination) {
		//Wrap angles around 0 and 2PI
		angleFig[0] -= ((int) (angleFig[0] / (Math.PI * 2.))) * Math.PI * 2.;
		angleFig[1] -= ((int) (angleFig[1] / (Math.PI * 2.))) * Math.PI * 2.;
//		System.out.printf("\nchoice: " + choiceStage + "\tposition: %d\tdestination: %d\n"
//						+ "angleFig[0]: %2.2f\tangleFig[1]: %2.2f\n",
//						position, destination, angleFig[0], angleFig[1]);
		if ((!choiceStage && position > destination)
						| (destination == position && angleFig[0] > angleFig[1])
						| (!choiceStage && nrGameCols == 3 && position == destination)) {
			angleFig[1] = (destination + nrGameCols) / (double) nrGameCols * Math.PI * 2;
		} else {
			angleFig[1] = destination * (Math.PI * 2 / nrGameCols);
		}
		//System.out.printf("after: angleFig[0]: %2.2f\tangleFig[1]: %2.2f\n",
		//				angleFig[0], angleFig[1]);

	}

	private boolean moveFigure() {
		boolean finished;
		//Rotate the figure
		double figDiff;
		figDiff = angleFig[0] - angleFig[1];
		//}
		if (Math.abs(figDiff) > 0.01) {
			angleFig[0] -= figDiff / 7;
			finished = false;
		} else {
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
				angleCols[i + GameClass.START_NR_COLOUMS] = i * (Math.PI * 2 / nrGameCols);
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
		nrCols = nrGameCols;
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
			for (int i = 0; i < nrGameCols; i++) {
				angleCols[i] = angleCols[i + GameClass.START_NR_COLOUMS];
			}
			//System.out.println("MoveCols has stopped");
			//timerCols.stop();
			//nrCols = nrGameCols;
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

	private void beforeMoveGoods() {
//		System.out.printf("sinkingSold %d\tsinkingKept %d\n", sinkingSold, sinkingKept);
		for (int i = 0; i < nrCols; i++) {
			int coeff = Math.round(w * (float) Math.sqrt(nrCols / 9f) / 5f);
			int x = (int) (center[0] + coeff * Math.cos(angleCols[i]));
			int y = (int) (center[1] + coeff * Math.sin(angleCols[i]));
			if (sinkingSold == i) {
				soldCommodityX = x;
				soldCommodityY[0] = y;
			}
			if (sinkingKept == i) {
				keptCommodityX[0] = x;
				keptCommodityY[0] = y;
			}
		}
		soldCommodityY[1] = h;
		keptCommodityX[1] = ((actualPlayer * 2 + 0.5f) * scoreTablePosition);
		keptCommodityY[1] = -goodsSize;
//		System.out.println("beforeMove: keptY" + Arrays.toString(keptCommodityY));
//		System.out.println("beforeMove: keptX" + Arrays.toString(keptCommodityX));
//		System.out.println("beforeMove: soldY" + Arrays.toString(soldCommodityY));
	}

	private boolean moveGoods() {
		boolean finished = false;
		//System.out.println("moveGoods event " + sinkingSold + " sinkingKept " + sinkingKept);
		float diff = soldCommodityY[1] - soldCommodityY[0];
		float diff2 = keptCommodityY[0] - keptCommodityY[1];
		float diff3 = keptCommodityX[0] - keptCommodityX[1];
		//System.out.println("diff1 " + diff + "\tdiff2 " + diff2 + "\tdiff3 " + diff3);
		if (diff > 1) {
			//sinkY[0] += Math.sqrt(diff) / 2;
			soldCommodityY[0] += Math.max(1, diff / 8f);
//			int extraSpace = (int) Math.round(diff / 10f);
//			repaint(soldCommodityX, Math.round(soldCommodityY[0] - extraSpace), goodsSize, goodsSize + extraSpace);
		}
		if (Math.abs(diff3) > 1) {
			//sinkX2[0] -= Math.sqrt(Math.abs(diff3)) * Math.signum(diff3);
			keptCommodityX[0] -= Math.max(1, Math.abs(diff3 / 3f)) * Math.signum(diff3);
//			int extraSpace = (int) Math.round(diff3 / 4f);
//			repaint(Math.round(keptCommodityX[0]), Math.round(keptCommodityY[0]), goodsSize + extraSpace, goodsSize);
		}
		if (diff2 > 1) {
			//keptGoodY[0] -= Math.sqrt(diff2);
			keptCommodityY[0] -= Math.max(1, diff2 / 6f);
//			int extraSpace = (int) Math.round(diff2 / 2f);
//			repaint(Math.round(keptCommodityX[0]), Math.round(keptCommodityY[0]) - extraSpace, goodsSize, goodsSize + extraSpace);
		}
		if (diff < 1 && diff2 < 1) {
			//System.out.println("moveGoods has stopped");
			//timerGoods.stop();
			soldCommodityY[0] = soldCommodityY[1];
			keptCommodityY[0] = keptCommodityY[1];
			sinkingSold = -1;
			sinkingKept = -1;
			sinkSoldImg = null;
			sinkKeptImg = null;
			//System.out.println(diff + " " + diff2 + " " + Arrays.toString(soldCommodityY) + " " + Arrays.toString(keptCommodityY));
			finished = true;
		}
		return finished;
	}

}
