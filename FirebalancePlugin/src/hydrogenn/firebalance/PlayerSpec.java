
package hydrogenn.firebalance;

import org.bukkit.entity.Player;

public class PlayerSpec {

	private Player	player;
	private String	name;
	private byte	nation;
	private int		king;
	int				credits;
	boolean			online;

	public PlayerSpec(Player player, String name, byte nation, int king, int credits, boolean online) {
		this.setPlayer(player);
		this.setName(name);
		this.setNation(nation);
		this.setKing(king);
		this.credits = credits;
		this.online = online;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
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
}
