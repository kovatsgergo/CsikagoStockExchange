package stockexchange.model;

/*Gergo Kovats*/
import java.io.Serializable;
import java.util.ArrayList;

public class ObservedPlayer implements Serializable{

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
