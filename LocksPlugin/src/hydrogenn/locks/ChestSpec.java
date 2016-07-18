package hydrogenn.locks;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Location;

public class ChestSpec {

	private Location coords;
	private String id;
	public static List<ChestSpec> list = new ArrayList<>();
	public static Hashtable<Location, ChestSpec> table = new Hashtable<Location, ChestSpec>();

	public ChestSpec(Location coords, String id) {
		this.setCoords(coords);
		this.setId(id);
		//this.setLarge(another location; null if not a large chest; should be updated actively);
		//TODO send new items to table
		//table.put(coords, this);
	}

	public Location getCoords() {
		return coords;
	}

	public void setCoords(Location coords) {
		this.coords = coords;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
