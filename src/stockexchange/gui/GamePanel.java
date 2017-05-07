package stockexchange.gui;

/* Gergo Kovats */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import stockexchange.ControlGuiInterface;
import stockexchange.GuiControlInterface;
import stockexchange.GuiModelInterface;
import stockexchange.model.*;

public class GamePanel extends JPanel implements ControlGuiInterface {

//MVC
	private GuiModelInterface model;
	HintPanel hintPanel;
	private GuiControlInterface interf;
//Game state variables
	private String[] playerNames;
	private int[] wins;
	private ArrayList<Commodity> topCommodities;
	private int actualPlayer;
	private int position;
	private ArrayList<Integer> possibleCols;
	private boolean choiceStage = false;
	private int nrCols = Model.START_NR_COLOUMS;
	private int nrGameCols = Model.START_NR_COLOUMS;
	private int[] colSizes;
//Screen-relative sizes
	private int h;
	private int w;
	private double[] center = new double[2];
	private int goodsSize;
	private int scoreTablePosition;
	private int figureSize;
	private ArrayList<Ellipse2D> bounds = new ArrayList();
	//stock image
	private int stockStartV;
	private int stockWidth;
	//price indicators
	private float rectsStartW;
	private float rectsStepW;
	private float rectsStartH;
	private float rectsStepH;
	private int rectsSize;
	private int glowSize;
	private final Color[] priceColors = {new Color(0, 0, 255, 170), new Color(255, 255, 255, 170), new Color(0, 0, 0, 170),
		new Color(0, 255, 0, 170), new Color(205, 130, 80, 170), new Color(255, 255, 0, 170)};
	private final ArrayList<BufferedImage> commodityImgs = new ArrayList();
	private final BufferedImage stockImg;
	private final BufferedImage figureImg;
	private final BufferedImage whiteGlowImg;
	private final BufferedImage redGlowImg;

	private Font fontB = new Font("Arial", Font.PLAIN, 23);
	private final float[] scales = {0.5f, 0.5f, 0.5f, 1f};
	private final float[] offsets = {20, 20, 20, 0};
	private final float[] scalesT = {1.1f, 1.1f, 1.1f, 1.f};
	private final float[] offsetsT = {0, 0, 0, 0};
	private final float[] scalesR = {1.3f, 1.f, 1.f, 1.f};
	private final float[] offsetsR = {20, 0, 0, 0};
	private final float[] scalesA = {.9f, .9f, .9f, 1.f};
	private final float[] offsetsA = {0, 0, 0, 0};

	//Animation variables
	private double[] angleFig = new double[2];
	private double[] angleCols = new double[Model.START_NR_COLOUMS * 2];
	private int[] fallenCols = {-1, -1};
	private int sinkingSold = -1;
	private int sinkingKept = -1;
	private BufferedImage sinkSoldImg;// = -1;
	private BufferedImage sinkKeptImg;// = -1;
	private int soldCommodityX;
	private float[] keptCommodityX = new float[2];//the kept item
	private float[] keptCommodityY = new float[2];
	private float[] soldCommodityY = new float[2];//the thrown good: from, to, hasarrived(-1)
	private float[] pricesDiff = new float[Model.COMMODITY_TYPES.length * 2];//{7, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6};
	private double[] figureFromTo = new double[4];
	private double[] coordDiff = new double[2];
	//for Timer
	private final javax.swing.Timer timerFig;
	private final javax.swing.Timer timerAll;
	private final int ANIM_INIT_DELAY = 0;
	private final int ANIM_TICK = 10;
	private boolean figureStopped = false;
	private boolean colsStopped = false;
	private boolean goodsStopped = false;
	private boolean pricesStopped = false;

	private boolean hints = false;

	public GamePanel(GuiModelInterface model) {
		this.model = model;
		//System.out.println(this.getClass().getResource(""));
		URL imageURL = this.getClass().getResource("/images/Wheat.png");
		commodityImgs.add(readFromURL(imageURL));
		imageURL = this.getClass().getResource("/images/Sugar.png");
		commodityImgs.add(readFromURL(imageURL));
		imageURL = this.getClass().getResource("/images/Coffee.png");
		commodityImgs.add(readFromURL(imageURL));
		imageURL = this.getClass().getResource("/images/Rice.png");
		commodityImgs.add(readFromURL(imageURL));
		imageURL = this.getClass().getResource("/images/Cocoa.png");
		commodityImgs.add(readFromURL(imageURL));
		imageURL = this.getClass().getResource("/images/Corn.png");
		commodityImgs.add(readFromURL(imageURL));
		imageURL = this.getClass().getResource("/images/Stock.jpg");
		stockImg = readFromURL(imageURL);
		URL imageURL2 = this.getClass().getResource("/images/Figure.png");
		figureImg = readFromURL(imageURL2);
		imageURL2 = this.getClass().getResource("/images/glow2.png");
		whiteGlowImg = readFromURL(imageURL2);
		imageURL2 = this.getClass().getResource("/images/glowR2.png");
		redGlowImg = readFromURL(imageURL2);

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
					interf.pause(true);
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				refreshSizes();
			}
		});
		timerAll = new javax.swing.Timer(ANIM_TICK, new ActionListener() {
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
					//System.out.println("All stopped");
				} else {
					//System.out.println("All moved");
					repaint();
				}
			}
		});

		timerFig = new javax.swing.Timer(ANIM_TICK, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveFigure();
				//System.out.println("Figure moved");
				repaint();
			}
		});

		timerFig.setInitialDelay(ANIM_INIT_DELAY);
		timerAll.setInitialDelay(ANIM_INIT_DELAY);
		setBackground(MainFrame.bgColor);
		setLayout(new BorderLayout());
		hintPanel = new HintPanel(commodityImgs, model.getWins().length);
		add(hintPanel, BorderLayout.SOUTH);
		hintPanel.setVisible(false);
	}

	public static BufferedImage readFromURL(URL url) {
		BufferedImage temp = null;
		try {
			temp = ImageIO.read(url);
		} catch (IOException e) {
			System.err.println("Not found file at" + url);
		}
		return temp;
	}

	@Override //from ControlGuiInterface
	public int pausePopup() {
		Object[] options = {"Quit", "Change players", "Continue"};
		return JOptionPane.showOptionDialog(GamePanel.this, "", "Game Paused",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
	}

	private void refreshSizes() {
		////drawing constants only changing when panel resized
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
		glowSize = Math.round(w * 0.023f);
		//win counters
		scoreTablePosition = w / (playerNames.length * 2 - 1);
		//font
		fontB = fontB.deriveFont((float) (w * 0.013) + 8);
		//coloumns
		center[0] = w * 2 / 3;
		center[1] = w * 13 / 50;
		goodsSize = Math.round(w / 10f);
		//figure
		figureSize = Math.round(goodsSize / 5);
		//timerAll.startFromLoaded();
		hintPanel.resize(w);
		repaint();
	}
	
//	@Override
//	public Dimension getPreferredSize(){
//		return new Dimension(900, 600);
//	}

	private int getClickedCol(Point pnt) {
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
		for (int i = 0; i < Model.COMMODITY_TYPES.length; i++) {
			if (commodity.equals(Model.COMMODITY_TYPES[i]))
				imageNr = i;
		}
		if (imageNr > -1)
			return commodityImgs.get(imageNr);
		else
			return null;
	}

	@Override //from ControlGuiInterface
	public void setNrGameCols() {
		nrGameCols = model.getNrOfCols();
	}

	@Override //from ControlGuiInterface
	public void setInterface(GuiControlInterface interf) {
		this.interf = interf;
	}

	/**
	 * Start from a saved position
	 */
	public void startFromLoaded(boolean choiceStage) {
		start();

		setFigure();
		this.choiceStage = choiceStage;//model.getChoiceStage();
		this.position = model.getPosition();
		this.actualPlayer = model.getActualPlayerIndex();
		possibleCols = model.getPossible();
		//nrCols = nrGameCols;
		System.out.println("prajsz: " + model.getPriceArray().toString());
		for (int i = 0; i < 6; i++) {
			pricesDiff[i] = model.getPriceArray().get(i);
			pricesDiff[i + 6] = model.getPriceArray().get(i);
		}
		remove(hintPanel);
		hintPanel = new HintPanel(commodityImgs, model.getWins().length);
		add(hintPanel, BorderLayout.SOUTH);
		setHint();
		hintPanel.setVisible(hints);
		beforeMoveCols();
		beforeMoveFigure(position);
		beforeMoveGoods();
		repaint();
		refreshSizes();
		timerAll.start();
	}

	@Override //from ControlGuiInterface
	public void start() {
		topCommodities = model.getTopCommodities();
		wins = model.getWins();
		playerNames = model.getAllNames();
		colSizes = model.getColsSizes();
		setNrGameCols();
		angleFig[0] = angleFig[1] = 0;
		for (int i = 0; i < Model.COMMODITY_TYPES.length; i++) {
			pricesDiff[i] = pricesDiff[i + Model.COMMODITY_TYPES.length] = Model.COMMODITY_TYPES[i].getStartPrice();
		}
		//Move the coloumns to their starting position
		for (int i = 0; i < nrGameCols; i++) {
			//Calculate the coloumns' angle values
			angleCols[i] = i * (Math.PI * 2 / nrGameCols);
			angleCols[i + Model.START_NR_COLOUMS] = i * (Math.PI * 2 / nrGameCols);
		}
		if (model != null)
			possibleCols = model.getPossible();//new ArrayList<Integer>();//{1, 2, 3};
		nrCols = nrGameCols;
		repaint();
	}

	@Override //from ControlGuiInterface
	public void setPossible() {
		possibleCols = model.getPossible();
	}

	@Override //from ControlGuiInterface
	public void setFigure() {
		beforeMoveFigure(model.getPosition());
		timerFig.start();
		position = model.getPosition();
		choiceStage = !choiceStage;
	}

	@Override //from ControlGuiInterface
	public void makeChoice(int kept, int[] emptiedColumns) {
		repaint();
		sinkingSold = model.getLastSoldColumn();//model.getNeighbors().get(1 - model.getNeighbors().indexOf(kept));
		sinkingKept = kept;
		//System.out.println("\nkept: " + sinkingKept + "\tsold: " + sinkingSold);
		sinkSoldImg = getImage(this.topCommodities.get(sinkingSold));
		//System.out.println(model.getLastSoldCommodity());
		sinkKeptImg = getImage(this.topCommodities.get(sinkingKept));
		setNrGameCols();
		//Set prices
		for (int i = 0; i < 6; i++) {
			pricesDiff[i + 6] = model.getPriceArray().get(i);
		}

		fallenCols = emptiedColumns;
		colSizes = model.getColsSizes();
		this.topCommodities = model.getTopCommodities();
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
		setNrGameCols();
		setFigure();
		setPossible();
	}

	@Override //from ControlGuiInterface
	public void setHint() {
		hintPanel.setHint(model.makeHints());
	}

	@Override	// from JPanel
	public void paint(Graphics g) {
		paint((Graphics2D) g);
	}

	public void paint(Graphics2D g) {
		if (true) {//to turn off drawing completely so to test AIs
			//<editor-fold defaultstate="collapsed" desc="comment">
			super.paint(g);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                             RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_DITHERING,
//							RenderingHints.VALUE_DITHER_ENABLE);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING,
//                             RenderingHints.VALUE_RENDER_QUALITY);

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
					g.setColor(new Color(200, 200, 200));
					g.drawOval(x - j * 0 * goodsSize / 20, y - j * goodsSize / 20, goodsSize - j, goodsSize - j);
					g.setColor(new Color(150, 150, 150));
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
				} else if ((possibleCols).contains(i)) {
					op = new RescaleOp(scalesT, offsetsT, null);
					g.drawImage(whiteGlowImg, x - glowSize, y - glowSize, x + glowSize + goodsSize, y + glowSize + goodsSize, 0, 0, 400, 400, null);
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

			//g.setColor(Color.LIGHT_GRAY);
			//Draw win counters
			for (int i = 0; i < playerNames.length; i++) {
				g.setColor(MainFrame.PLAYER_COLORS[i]);
				g.drawString(wins[i] + "", scoreTablePosition / 2 + i * 2 * scoreTablePosition, w / 50 + 5);
			}

			//Draw who's turn it is
			String turnText = playerNames[actualPlayer] + "\'s turn";
			g.setColor(MainFrame.PLAYER_COLORS[actualPlayer]);
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

			//Is is choice stage
			//g.drawString(Boolean.toString(choiceStage), w / 20, w * 5 / 10);
		}
//</editor-fold>
	}

	void setHintsOnOff(boolean onoff) {
		if (onoff) {
			hints = true;
			hintPanel.setVisible(true);
			repaint();
		} else {
			hints = false;
			hintPanel.setVisible(false);
			this.setPreferredSize(new Dimension(950, 550));
			repaint();
		}
	}

	@Override //from ControlGuiInterface
	public int gameOverPopup() {
		//System.out.println("popup called");
		String helpString = model.gameOverString();
		Object[] options = {"Quit", "Change players", "REVENGE"};
		return JOptionPane.showOptionDialog(GamePanel.this,
						helpString, "Game Over",
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
				angleCols[i + Model.START_NR_COLOUMS] = i * (Math.PI * 2 / nrGameCols);
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
			colsDiff[i] = angleCols[i] - angleCols[i + Model.START_NR_COLOUMS];
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
				angleCols[i] = angleCols[i + Model.START_NR_COLOUMS];
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
