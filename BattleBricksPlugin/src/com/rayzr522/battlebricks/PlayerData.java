package com.rayzr522.battlebricks;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/* 
 * PlayerData.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class PlayerData {

	public static HashMap<UUID, PlayerData> map = new HashMap<UUID, PlayerData>();

	private UUID uuid;
	private String name = "unknown";
	private Player player = null;
	private int wins = 0;
	private int losses = 0;

	public PlayerData(UUID uuid) {

		this.uuid = uuid;

	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID id) {
		this.uuid = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}

	public static PlayerData loadFromConfig(YamlConfiguration conf) {

		UUID uuid = UUID.fromString(conf.getString("uuid"));
		String name = conf.getString("name");
		int wins = conf.getInt("wins");
		int losses = conf.getInt("losses");

		PlayerData data = new PlayerData(uuid);
		data.name = name;
		data.wins = wins;
		data.losses = losses;

		return data;

	}

	public YamlConfiguration saveToConfig(YamlConfiguration conf) {

		conf.set("uuid", uuid.toString());
		conf.set("name", name);
		conf.set("wins", wins);
		conf.set("losses", losses);

		return conf;

	}

	public static boolean hasData(Player player) {

		return map.containsKey(player.getUniqueId());

	}

	public static void createData(Player player) {

		PlayerData data = new PlayerData(player.getUniqueId());
		data.setName(player.getName());
		map.put(player.getUniqueId(), data);

	}

}
