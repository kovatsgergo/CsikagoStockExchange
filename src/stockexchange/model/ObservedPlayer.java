package stockexchange.model;

/*Gergo Kovats*/
import stockexchange.model.Player;
import stockexchange.model.Commodity;
import java.util.ArrayList;

public class ObservedPlayer {

	private ArrayList<Commodity> commodities = new ArrayList();

	public ObservedPlayer(Player player) {
		this.commodities = player.commodities;
	}

	protected ArrayList<Commodity> getCommodities() {
		return commodities;
	}

	@Override
	public String toString() {
		return "ObservedPlayer{" + commodities.toString() + '}';
	}

}
