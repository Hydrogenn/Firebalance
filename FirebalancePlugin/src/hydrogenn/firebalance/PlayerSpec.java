
package hydrogenn.firebalance;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerSpec {

	private String name;
	private UUID uuid;
	private byte nation;
	private int king;
	private int credits;
	private boolean online;
	public static List<UUID> aggressives = new ArrayList<UUID>();
	public static List<PlayerSpec> list = new ArrayList<>();
	public static Hashtable<UUID, PlayerSpec> table = new Hashtable<UUID, PlayerSpec>();

	public PlayerSpec(String name, UUID id, byte nation, int king, int credits, boolean online) {
		this.setName(name);
		this.setUUID(id);
		this.setNation(nation);
		this.setKing(king);
		this.setCredits(credits);
		this.online = online;
		// TODO send new items to table
		// table.put(uuid, this);
	}

	public static PlayerSpec getPlayerFromName(String name) {
		PlayerSpec r = null;
		for (PlayerSpec s : list) {
			if (s.getName().equals(name))
				r = s;
		}
		return r;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID id) {
		this.uuid = id;
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

	public static PlayerSpec loadFromConfig(YamlConfiguration config) {

		String name = config.getString("name");
		UUID uuid = UUID.fromString(config.getString("uuid"));
		byte nation = (byte) config.getInt("nation");
		int king = config.getInt("role");
		int credits = config.getInt("credits");
		boolean online = Bukkit.getPlayer(uuid) != null;

		return new PlayerSpec(name, uuid, nation, king, credits, online);

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("name", name);
		config.set("uuid", uuid.toString());
		config.set("nation", nation);
		config.set("role", king);
		config.set("credits", credits);
		
		return config;

	}

}
