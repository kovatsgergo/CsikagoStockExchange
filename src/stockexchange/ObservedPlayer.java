package stockexchange;

/*Gergo Kovats*/
import java.util.ArrayList;

public class ObservedPlayer {

	private ArrayList<String> goods = new ArrayList();

	public ObservedPlayer(Player player) {
		this.goods = player.goods;
	}

	protected ArrayList<String> getGoods() {
		return goods;
	}

	@Override
	public String toString() {
		return "ObservedPlayer{" + goods.toString() + '}';
	}

}
