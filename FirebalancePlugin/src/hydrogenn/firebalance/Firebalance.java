
package hydrogenn.firebalance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.firebalance.command.CommandAggressive;
import hydrogenn.firebalance.command.CommandAnonymBook;
import hydrogenn.firebalance.command.CommandChunk;
import hydrogenn.firebalance.command.CommandCredit;
import hydrogenn.firebalance.command.CommandEnthrone;
import hydrogenn.firebalance.command.CommandMap;
import hydrogenn.firebalance.command.CommandNationChat;
import hydrogenn.firebalance.command.CommandOops;
import hydrogenn.firebalance.command.CommandRenameNation;
import hydrogenn.firebalance.command.CommandSentence;
import hydrogenn.firebalance.command.CommandSetNation;
import hydrogenn.firebalance.file.ConfigManager;

//TODO let's make a plugin where you can enchant pumpkins like a helmet. You cannot place it.
//TODO test adding multiple nations
public class Firebalance extends JavaPlugin {

	// TODO change to hashmap storage
	// TODO add special permissions list (?)
	FileConfiguration config = getConfig();
	public static List<String> nationNameList = new ArrayList<>();
	public static Hashtable<String, String> killList = new Hashtable<String, String>();
	public static Hashtable<String, List<long[]>> sentenceValues = new Hashtable<String, List<long[]>>();
	public static Hashtable<String, Long> sentenceMaxes = new Hashtable<String, Long>();
	public static String activeSentence = null;

	public void storeObject(String input, String file) {
		String dir = getDataFolder() + File.separator + "firebalance." + file;
		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dir, true), "utf-8"))) {
			writer.write(input);
			writer.newLine();
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().info("Unsupported Encoding");
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().info("File Not Found");
		} catch (IOException e) {
			Bukkit.getLogger().info("IO Exception");
		}
	}

	public void clearObject(String file) {
		String dir = getDataFolder() + File.separator + "firebalance." + file;
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir), "utf-8"))) {
			writer.write("");
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().info("Unsupported Encoding");
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().info("File Not Found");
		} catch (IOException e) {
			Bukkit.getLogger().info("IO Exception");
		}
	}

	public ArrayList<String> displayObjects(String file) {
		String dir = "plugins/Firebalance/firebalance." + file;
		ArrayList<String> lineList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dir), "utf-8"))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lineList.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			Bukkit.getLogger().info("Unsupported Encoding");
		} catch (FileNotFoundException e) {
			Bukkit.getLogger().info("File Not Found");
		} catch (IOException e) {
			Bukkit.getLogger().info("IO Exception");
		}
		return lineList;
	}

	public static String getNationName(byte nation, boolean filter) {
		String result = null;
		if (nation == -1 && !filter)
			result = "undecided";
		if (nation == 0 && !filter)
			result = "freelancers";
		if (nation == 1)
			result = nationNameList.get(0);
		if (nation == 2)
			result = nationNameList.get(1);
		if (nation == 3)
			result = nationNameList.get(0) + "-" + nationNameList.get(1);
		if (nation == 4)
			result = nationNameList.get(2);
		if (nation == 5)
			result = nationNameList.get(2) + "-" + nationNameList.get(0);
		if (nation == 6)
			result = nationNameList.get(1) + "-" + nationNameList.get(2);
		if (nation == 7)
			result = "Neutral";
		return result;
	}

	public static byte getNationByte(String nation) {
		byte result = -1;
		if (nation.equalsIgnoreCase("freelance"))
			result = 0;
		if (nation.equalsIgnoreCase(nationNameList.get(0)))
			result = 1;
		if (nation.equalsIgnoreCase(nationNameList.get(1)))
			result = 2;
		if (nation.equalsIgnoreCase(nationNameList.get(0) + "-" + nationNameList.get(1)))
			result = 3;
		if (nation.equalsIgnoreCase(nationNameList.get(2)))
			result = 4;
		if (nation.equalsIgnoreCase(nationNameList.get(2) + "-" + nationNameList.get(0)))
			result = 5;
		if (nation.equalsIgnoreCase(nationNameList.get(1) + "-" + nationNameList.get(2)))
			result = 6;
		if (nation.equalsIgnoreCase("neutral"))
			result = 7;
		return result;
	}

	public static String getNationColor(byte nation, boolean filter) {
		if (nation == -1 && !filter)
			return "";
		if (nation == 0 && !filter)
			return "§f";
		if (nation == 1)
			return "§4";
		if (nation == 2)
			return "§2";
		if (nation == 3)
			return "§e";
		if (nation == 4)
			return "§9";
		if (nation == 5)
			return "§d";
		if (nation == 6)
			return "§b";
		if (nation == 7)
			return "§7";
		return null;
	}

	@Override
	public void onEnable() {

		// Register commands
		this.getCommand("nation").setExecutor(new CommandSetNation());
		this.getCommand("chunk").setExecutor(new CommandChunk());
		this.getCommand("enthrone").setExecutor(new CommandEnthrone());
		this.getCommand("credits").setExecutor(new CommandCredit());
		this.getCommand("renation").setExecutor(new CommandRenameNation());
		this.getCommand("map").setExecutor(new CommandMap());
		this.getCommand("nationchat").setExecutor(new CommandNationChat());
		this.getCommand("anonsign").setExecutor(new CommandAnonymBook());
		this.getCommand("sentence").setExecutor(new CommandSentence());
		this.getCommand("oopsmybad").setExecutor(new CommandOops());
		this.getCommand("aggressive").setExecutor(new CommandAggressive());

		// Register the event listener
		getServer().getPluginManager().registerEvents(new MyListener(), this);

		// Set up the new ConfigManager class
		ConfigManager.init(this);
		
		//TODO move loading chests into the locks plugin
		loadMiscellaneous();

		// Find online players (for compatibility with /rl)
		for (PlayerSpec s : PlayerSpec.list) {
			if (Bukkit.getPlayer(s.getName()) != null)
				s.setOnline(true);
		}

		getLogger().info("Firebalance v" + getDescription().getVersion() + " has been loaded");

	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {

		ConfigManager.save();

		OLD_configSave();

		getLogger().info("Firebalance v" + getDescription().getVersion() + " has been unloaded");

	}

	@Deprecated
	public void loadMiscellaneous() {

		
		// Set up configs
		config.addDefault("test", true);
		config.options().copyDefaults(true);
		saveConfig();
		// Read and transfer chunk and player data from flatfile
		List<String> lineList = new ArrayList<>();

		/*lineList = displayObjects("users");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			PlayerSpec.list.add(new PlayerSpec(subList[0], subList.length > 4 ? UUID.fromString(subList[4]) : null,
					Byte.parseByte(subList[1]), Integer.parseInt(subList[2]), Integer.parseInt(subList[3]), false));
		}

		lineList = displayObjects("chunk");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			ChunkSpec.list.add(new ChunkSpec(Integer.parseInt(subList[0]), Integer.parseInt(subList[1]),
					Integer.parseInt(subList[2]), Byte.parseByte(subList[3]), subList[4],
					Boolean.parseBoolean(subList[5]), Boolean.parseBoolean(subList[6]),
					new ArrayList<String>(Arrays.asList(subList[7].split(",")))));
		}*/

		lineList = displayObjects("chest");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			if (subList.length < 4) {
				ChestSpec.list.add(new ChestSpec(new Location(Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()),
						Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2])), ""));
			} else
				ChestSpec.list.add(new ChestSpec(new Location(Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()),
						Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2])),
						subList[3]));
		}

		lineList = displayObjects("misc");
		{
			{
				String[] subList = lineList.get(0).split(":");
				for (int i = 0; i <= 2; i++) {
					nationNameList.add(subList[i]);
				}
			}
			{
				if (lineList.contains("--SENTENCE MAXIMUMS--")) {
					// TODO make this not hacky
					for (int i = 2; i < lineList.size(); i++) {
						String[] subList = lineList.get(i).split(":");
						sentenceMaxes.put(subList[0], Long.parseLong(subList[1]));
					}
				}
			}
		}

	}

	@Deprecated
	public void OLD_configSave() {

		clearObject("users");
		for (int i = 0; i < PlayerSpec.list.size(); i++) {
			String line = "";
			line += PlayerSpec.list.get(i).getName();
			line += ":" + PlayerSpec.list.get(i).getNation();
			line += ":" + PlayerSpec.list.get(i).getKing();
			line += ":" + PlayerSpec.list.get(i).getCredits();
			line += ":" + PlayerSpec.list.get(i).getUUID();
			storeObject(line, "users");
		}
		clearObject("chunk");
		for (int i = 0; i < ChunkSpec.list.size(); i++) {
			String line = "";
			line += ChunkSpec.list.get(i).getX();
			line += ":" + ChunkSpec.list.get(i).getY();
			line += ":" + ChunkSpec.list.get(i).getZ();
			line += ":" + ChunkSpec.list.get(i).getNation();
			line += ":" + ChunkSpec.list.get(i).getOwner();
			line += ":" + ChunkSpec.list.get(i).isNational();
			line += ":" + ChunkSpec.list.get(i).isOutpost();
			line += ":";
			for (String s2 : ChunkSpec.list.get(i).getShared()) {
				line += s2 + ",";
			}
			if (ChunkSpec.list.get(i).getShared().size() == 0)
				line += ",";
			storeObject(line, "chunk");
		}
		clearObject("chest");
		for (int i = 0; i < ChestSpec.list.size(); i++) {
			String line = "";
			line += ChestSpec.list.get(i).getCoords().getBlockX();
			line += ":" + ChestSpec.list.get(i).getCoords().getBlockY();
			line += ":" + ChestSpec.list.get(i).getCoords().getBlockZ();
			line += ":" + ChestSpec.list.get(i).getId();
			storeObject(line, "chest");
		}
		clearObject("misc");
		{
			String line = "";
			line += nationNameList.get(0);
			line += ":" + nationNameList.get(1);
			line += ":" + nationNameList.get(2);
			storeObject(line, "misc");
			if (sentenceMaxes.size() > 0)
				storeObject("--SENTENCE MAXIMUMS--", "misc");
			for (String s : sentenceMaxes.keySet()) {
				line = "";
				line += s + ":" + sentenceMaxes.get(s);
				storeObject(line, "misc");
			}
		}

	}

}
