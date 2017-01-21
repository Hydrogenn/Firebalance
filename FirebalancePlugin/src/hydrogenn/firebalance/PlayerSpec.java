
package hydrogenn.firebalance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerSpec {

	private String name;
	private UUID uuid;
	private byte nation;
	private int role;
	private int credits;
	private boolean online;
	public static List<UUID> aggressives = new ArrayList<UUID>();
	private static Hashtable<UUID, PlayerSpec> table = new Hashtable<UUID, PlayerSpec>();

	PlayerSpec(String name, UUID id, byte nation, int role, int power, int credits, boolean online) {
		this.setName(name);
		this.setUUID(id);
		this.setNation(nation);
		this.setRole(role);
		this.setCredits(credits);
		this.setOnline(online);
		table.put(uuid, this);
	}

	public static void addNewPlayer(Player player) {
		PlayerSpec playerSpec = new PlayerSpec(
				player.getName(),
				player.getUniqueId(),
				(byte) -1,
				0,0,0,
				true);
		table.put(player.getUniqueId(),playerSpec);
	}
	
	public static PlayerSpec getPlayerFromName(String name) {
		PlayerSpec r = null;
		for (PlayerSpec s : table.values()) {
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

	public static void loadFromConfig(YamlConfiguration config) {

		String name = config.getString("name");
		UUID uuid = UUID.fromString(config.getString("uuid"));
		byte nation = (byte) config.getInt("nation");
		int role = config.getInt("role");
		int power = config.getInt("power");
		int credits = config.getInt("credits");
		boolean online = Bukkit.getPlayer(uuid) != null;

		table.put(uuid, new PlayerSpec(name, uuid, nation, role, power, credits, online));

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("name", name);
		config.set("uuid", uuid.toString());
		config.set("nation", nation);
		config.set("role", role);
		config.set("credits", credits);
		
		return config;

	}
	
	public static void markAsAggressive(UUID player) {
		aggressives.add(player);
	}

	public static void markAsPassive(UUID player) {
		aggressives.remove(player);
	}

	public static Collection<PlayerSpec> getPlayers() {
		return table.values();
	}

	public static void clearPlayers() {
		table.clear();
	}

}
