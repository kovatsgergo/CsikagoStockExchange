package stockexchange;

/*Gergo Kovats*/
import java.util.ArrayList;

public class ObservedPlayer {

	private ArrayList<Commodity> goods = new ArrayList();

	public ObservedPlayer(Player player) {
		this.goods = player.commodities;
	}

	protected ArrayList<Commodity> getGoods() {
		return goods;
	}

	@Override
	public String toString() {
		return "ObservedPlayer{" + goods.toString() + '}';
	}

}
