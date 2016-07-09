
package hydrogenn.firebalance;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public class PlayerSpec {

	private String name;
	private byte nation;
	private int king;
	private int credits;
	private boolean online;
	public static List<UUID>					aggressives	= new ArrayList<UUID>();
	public static List<PlayerSpec>				list		= new ArrayList<>();
	public static Hashtable<UUID,PlayerSpec>	table		= new Hashtable<UUID,PlayerSpec>();

	public PlayerSpec(String name, byte nation, int king, int credits, boolean online) {
		this.setName(name);
		this.setNation(nation);
		this.setKing(king);
		this.setCredits(credits);
		this.online = online;
		//TODO send new items to table
		//table.put(uuid, this);
	}

	public static PlayerSpec getPlayerFromName(String name) {
		PlayerSpec r = null;
		for (PlayerSpec s : list) {
			if (s.getName().equals(name)) r = s;
		}
		return r;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getNation() {
		return nation;
	}

	public void setNation(byte nation) {
		this.nation = nation;
	}

	public int getKing() {
		return king;
	}

	public void setKing(int king) {
		this.king = king;
	}

	public boolean getOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}
}
