package hydrogenn.kingdoms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerSpec {

	private static HashMap<UUID,PlayerSpec> players = new HashMap<UUID,PlayerSpec>();

	private Kingdom kingdom;
	private String name;
	private UUID uuid;
	private boolean online;

	public PlayerSpec(Player player) {
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.online = true;
		this.kingdom = null;
		players.put(uuid, this);
	}
	
	private PlayerSpec(Kingdom kingdom, UUID uuid, String name) {
		this.kingdom = kingdom;
		this.name = name;
		this.uuid = uuid;
		this.online = Bukkit.getOfflinePlayer(uuid).isOnline();
		players.put(uuid, this);
	}

	public void login() {
		online = true;
	}

	public void logout() {
		online = false;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Kingdom getKingdom() {
		return kingdom;
	}

	public void joinKingdom(Kingdom kingdom) {
		kingdom.add(uuid); //this will cause an error before everything else.
		if (this.kingdom != null) {
			this.kingdom.remove(uuid);
		}
		this.kingdom = kingdom;
	}

	public boolean isOnline() {
		return online;
	}

	public FileConfiguration saveToConfig(YamlConfiguration conf) {
		conf.set("uuid", uuid.toString());
		conf.set("name", name);
		if (kingdom != null) {
			conf.set("kingdom", kingdom.getName());
		}
		return conf;
	}

	public static void loadFromConfig(YamlConfiguration conf) {
		UUID uuid = UUID.fromString(conf.getString("uuid"));
		String name = conf.getString("name");
		Kingdom kingdom;
		if (conf.contains("kingdom")) {
			kingdom = Kingdom.get(conf.getString("kingdom"));
		}
		else {
			kingdom = null;
		}
		new PlayerSpec(kingdom,uuid,name);
	}
	
	public static Iterator<PlayerSpec> iterator() {
		return players.values().iterator();
	}

	public static PlayerSpec getSpec(Player player) {
		PlayerSpec spec = players.get(player.getUniqueId());
		if (spec == null)
			return new PlayerSpec(player);
		else
			return spec;
	}

	public static PlayerSpec getByUUID(UUID uuid) {
		return players.get(uuid);		
	}

	public static PlayerSpec getByName(String name) {
		for (PlayerSpec spec : players.values()) {
			if (spec.getName().equals(name)) {
				return spec;
			}
		}
		return null;
	}
	
}
