
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
	private int role;
	private int credits;
	private boolean online;
	public static List<UUID> aggressives = new ArrayList<UUID>();
	@Deprecated
	public static List<PlayerSpec> list = new ArrayList<>();
	public static Hashtable<UUID, PlayerSpec> table = new Hashtable<UUID, PlayerSpec>();

	public PlayerSpec(String name, UUID id, byte nation, int role, int credits, boolean online) {
		this.setName(name);
		this.setUUID(id);
		this.setNation(nation);
		this.setRole(role);
		this.setCredits(credits);
		this.online = online;
		table.put(uuid, this);
	}

	public static PlayerSpec getPlayerFromName(String name) {
		PlayerSpec r = null;
		for (PlayerSpec s : list) {
			if (s.getName().equals(name))
				r = s;
		}
		return r;
	}
	
	public static PlayerSpec getPlayer(UUID uuid) {
		return table.get(uuid);
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

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
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
		int role = config.getInt("role");
		int credits = config.getInt("credits");
		boolean online = Bukkit.getPlayer(uuid) != null;

		return new PlayerSpec(name, uuid, nation, role, credits, online);

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("name", name);
		config.set("uuid", uuid.toString());
		config.set("nation", nation);
		config.set("role", role);
		config.set("credits", credits);
		
		return config;

	}

}
