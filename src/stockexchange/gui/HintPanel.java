/*Gergo Kovats*/
package stockexchange.gui;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import stockexchange.model.Model;

public class HintPanel extends JPanel {

	ArrayList<JLabel> lbAll;
	int w;
	int length;

	public HintPanel(ArrayList<BufferedImage> commodityImgs, int nrPlayers) {
		setLayout(new GridLayout(0, 1));
		//setBackground(new Color(40, 120, 255));
		setBackground(MainFrame.bgColor.brighter());
		//setBackground(new Color(10, 30, 80));
		length = nrPlayers;
		lbAll = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			lbAll.add(new JLabel(i + " bad initialization"));
			lbAll.get(i).setForeground(MainFrame.PLAYER_COLORS[i]);
			add(lbAll.get(i), i);
		}
	}

	public void setHint(String[] hintText) {
		for (int i = 0; i < Model.MAX_NR_PLAYERS; i++)
			if (i < hintText.length) {
				lbAll.get(i).setVisible(true);
				lbAll.get(i).setText(hintText[i]);
			}
//			else
//				lbAll.get(i).setVisible(false);
	}

//	public void resize() {
//		if (this.isVisible())
//			w = getWidth();
//		for (int i = 0; i < length; i++)
//			lbAll.get(i).setFont(lbAll.get(i).getFont().deriveFont((float) (w * 0.011) + 6));
//	}

	public void resize(int w) {
		this.w = w;
		for (int i = 0; i < length; i++)
			lbAll.get(i).setFont(lbAll.get(i).getFont().deriveFont((float) (w * 0.011) + 6));
	}
}
