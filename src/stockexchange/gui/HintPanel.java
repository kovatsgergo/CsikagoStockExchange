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
			lbAll.get(i).setSize(w, 30);
			add(lbAll.get(i), i);
		}
	}

	public void setHint(String[] hintText) {
		for (int i = 0; i < Model.MAX_NR_PLAYERS; i++)
			if (i < hintText.length) {
				lbAll.get(i).setVisible(true);
				lbAll.get(i).setText(convertHintText(hintText[i]));
				//lbAll.get(i).setText(hintText[i]);
			}
//			else
//				lbAll.get(i).setVisible(false);
	}

	private String convertHintText(String hintText) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		String[] twoparts = hintText.split(":");
		sb.append(twoparts[0]);
		int size = (int)((w * 0.011) + 6);
		//System.out.println(Arrays.toString(twoparts));
		String[] array = twoparts[1].split("-");
		for (String string : array) {
			//System.out.println("array "+string.length());
			if (string.length() > 1) {
				String[] commodity = string.split("_");
				//System.out.println(Arrays.toString(commodity));
				//System.out.println("<img src=\"/Users/Gergo/BrainingHub/CsikagoStockExchange/src/images/" + commodity[0].trim() + ".png\" width=\"70\" height=\"70\">");
				//System.out.println("<img src=\""+this.getClass().getResource("/images/" + commodity[0].trim() + ".png")+"\" width=\"30\" height=\"30\">");
				sb.append("<img src=\""+this.getClass().getResource("/images/" + commodity[0].trim() + ".png")+"\" width=\""+size+"\" height=\""+size+"\">");
				sb.append(commodity[1]);
			}
		}
		sb.append("</html>");
		System.out.println(sb.toString());
		return sb.toString();
	}

//	public void resize() {
//		if (this.isVisible())
//			w = getWidth();
//		for (int i = 0; i < length; i++)
//			lbAll.get(i).setFont(lbAll.get(i).getFont().deriveFont((float) (w * 0.011) + 6));
//	}
	public void resize(int w) {
		this.w = w;
		System.out.println(w+"width");
		for (int i = 0; i < length; i++)
			lbAll.get(i).setFont(lbAll.get(i).getFont().deriveFont((float) (w * 0.011) + 6));
	}
}
