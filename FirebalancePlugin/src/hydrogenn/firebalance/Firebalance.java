
package hydrogenn.firebalance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

//TODO let's make a plugin where you can enchant pumpkins like a helmet. You cannot place it.
//TODO test adding multiple nations
public class Firebalance extends JavaPlugin {

	FileConfiguration								config			= getConfig();
	// TODO change to hashmap storage
	// TODO add special permissions list
	public static List<PlayerSpec>					playerSpecList	= new ArrayList<>();
	public static List<ChunkSpec>					chunkSpecList	= new ArrayList<>();
	public static List<ChestSpec>					chestSpecList	= new ArrayList<>();
	public static List<String>						nationNameList	= new ArrayList<>();
	public static Hashtable<String, String>			killList		= new Hashtable<String, String>();
	public static Hashtable<String, List<long[]>>	sentenceValues	= new Hashtable<String, List<long[]>>();
	public static Hashtable<String, Long>			sentenceMaxes	= new Hashtable<String, Long>();
	public static List<UUID>						aggressives		= new ArrayList<UUID>();
	public static String							activeSentence	= null;

	public static List<SchedulerCache>				scheduleList	= new ArrayList<>();

	public void storeObject(String input, String file) {
		String dir = "plugins/Firebalance/firebalance." + file;
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir, true), "utf-8"))) {
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
		String dir = "plugins/Firebalance/firebalance." + file;
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
		if (nation == -1 && !filter) result = "undecided";
		if (nation == 0 && !filter) result = "freelancers";
		if (nation == 1) result = nationNameList.get(0);
		if (nation == 2) result = nationNameList.get(1);
		if (nation == 3) result = nationNameList.get(0) + "-" + nationNameList.get(1);
		if (nation == 4) result = nationNameList.get(2);
		if (nation == 5) result = nationNameList.get(2) + "-" + nationNameList.get(0);
		if (nation == 6) result = nationNameList.get(1) + "-" + nationNameList.get(2);
		if (nation == 7) result = "Neutral";
		return result;
	}

	public static byte getNationByte(String nation) {
		byte result = -1;
		if (nation.equalsIgnoreCase("freelance")) result = 0;
		if (nation.equalsIgnoreCase(nationNameList.get(0))) result = 1;
		if (nation.equalsIgnoreCase(nationNameList.get(1))) result = 2;
		if (nation.equalsIgnoreCase(nationNameList.get(0) + "-" + nationNameList.get(1))) result = 3;
		if (nation.equalsIgnoreCase(nationNameList.get(2))) result = 4;
		if (nation.equalsIgnoreCase(nationNameList.get(2) + "-" + nationNameList.get(0))) result = 5;
		if (nation.equalsIgnoreCase(nationNameList.get(1) + "-" + nationNameList.get(2))) result = 6;
		if (nation.equalsIgnoreCase("neutral")) result = 7;
		return result;
	}

	public static String getNationColor(byte nation, boolean filter) {
		if (nation == -1 && !filter) return "";
		if (nation == 0 && !filter) return "§f";
		if (nation == 1) return "§4";
		if (nation == 2) return "§2";
		if (nation == 3) return "§e";
		if (nation == 4) return "§9";
		if (nation == 5) return "§d";
		if (nation == 6) return "§b";
		if (nation == 7) return "§7";
		return null;
	}

	public static String getHeightString(int height) {
		String result = null;
		if (height == -1) result = "Undergrounds";
		if (height == 0) result = "Surface";
		if (height == 1) result = "Skyloft";
		return result;
	}

	public static PlayerSpec getPlayerFromName(String name) {
		PlayerSpec r = null;
		for (PlayerSpec s : playerSpecList) {
			if (s.getName().equals(name)) r = s;
		}
		return r;
	}

	public static String getChannelName(int channel) {
		String r;
		switch (channel) {
		case 1:
			r = "global";
			break;
		case 2:
			r = "nation";
			break;
		case 4:
			r = "econ";
			break;
		default:
			r = null;
			break;
		}
		return r;
	}

	public static byte getChannelType(String channel) {
		byte r = 0;
		if (channel.contains("global")) r = 1;
		if (channel.contains("nation")) r = 2;
		if (channel.contains("econ")) r = 4;
		return r;
	}

	public static void addScheduler(String functionName, String callerName, long delay, Runnable function) {
		final int id = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), function, delay).getTaskId();
		scheduleList.add(new SchedulerCache(id, callerName, functionName, delay + System.currentTimeMillis()));
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {

			public void run() {
				for (Iterator<SchedulerCache> i = scheduleList.iterator(); i.hasNext();) {
					SchedulerCache s = i.next();
					if (s.id == id) {
						i.remove();
					}
				}
			}
		}, delay + 10);
	}

	public static void addCountedScheduler(String functionName, String callerName, long delay, final String message, Runnable function) {
		final int id = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), function, delay).getTaskId();
		scheduleList.add(new SchedulerCache(id, callerName, functionName, delay + System.currentTimeMillis() / 50));
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {

			public void run() {
				for (Iterator<SchedulerCache> i = scheduleList.iterator(); i.hasNext();) {
					SchedulerCache s = i.next();
					if (s.id == id) {
						i.remove();
					}
				}
			}
		}, delay + 10);
		int displayTime = 1;
		while (displayTime * 20 < delay) {
			final String display;
			if (displayTime % 3600 == 0) display = displayTime / 3600 + " hours";
			else if (displayTime % 60 == 0) display = displayTime / 60 + " minutes";
			else display = displayTime + " seconds";
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {

				public void run() {
					Bukkit.broadcastMessage(message + display);
				}
			}, delay - displayTime * 20);
			if (displayTime >= 3600) displayTime *= 2;
			switch (displayTime) {
			case 1:
				displayTime = 2;
				break;
			case 2:
				displayTime = 3;
				break;
			case 3:
				displayTime = 5;
				break;
			case 5:
				displayTime = 10;
				break;
			case 10:
				displayTime = 15;
				break;
			case 15:
				displayTime = 30;
				break;
			case 30:
				displayTime = 45;
				break;
			case 45:
				displayTime = 60;
				break;
			case 1 * 60:
				displayTime = 2 * 60;
				break;
			case 2 * 60:
				displayTime = 3 * 60;
				break;
			case 3 * 60:
				displayTime = 5 * 60;
				break;
			case 5 * 60:
				displayTime = 10 * 60;
				break;
			case 10 * 60:
				displayTime = 15 * 60;
				break;
			case 15 * 60:
				displayTime = 30 * 60;
				break;
			case 30 * 60:
				displayTime = 45 * 60;
				break;
			case 45 * 60:
				displayTime = 60 * 60;
				break;
			}
		}
	}

	public static void addSyncScheduler(String functionName, String callerName, long delay, Runnable function) {
		final int id = Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Firebalance"), function, delay).getTaskId();
		scheduleList.add(new SchedulerCache(id, callerName, functionName, delay + System.currentTimeMillis()));
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {

			public void run() {
				for (Iterator<SchedulerCache> i = scheduleList.iterator(); i.hasNext();) {
					SchedulerCache s = i.next();
					if (s.id == id) {
						i.remove();
					}
				}
			}
		}, delay + 10);
	}

	public static Long getRemainingTaskTicks(String functionName, String callerName) {
		for (SchedulerCache s : scheduleList) {
			if (functionName == null || s.type.equals(functionName)) {
				if (callerName == null || s.callerName.equals(callerName)) { return s.taskEnd - System.currentTimeMillis() / 50; }
			}
		}
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

		// Register the event listener
		getServer().getPluginManager().registerEvents(new MyListener(), this);

		// Register key crafting recipe
		ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta keyMeta = key.getItemMeta();
		List<String> keyLore = new ArrayList<>();
		keyLore.add("§7");
		keyMeta.setLore(keyLore);
		keyMeta.setDisplayName("§fKeyC");
		key.setItemMeta(keyMeta);
		ShapedRecipe keyCraft = new ShapedRecipe(key);
		keyCraft.shape("A", "A");
		keyCraft.setIngredient('A', Material.GOLD_INGOT);
		getServer().addRecipe(keyCraft);
		// Register key dupe recipe
		ItemStack keyD = new ItemStack(Material.TRIPWIRE_HOOK);
		keyMeta.setDisplayName("§fKeyD");
		keyD.setItemMeta(keyMeta);
		ShapelessRecipe keyDupe = new ShapelessRecipe(keyD);
		keyDupe.addIngredient(Material.GOLD_INGOT);
		keyDupe.addIngredient(Material.TRIPWIRE_HOOK);
		getServer().addRecipe(keyDupe);
		// Register key add recipe
		ItemStack keyA = new ItemStack(Material.TRIPWIRE_HOOK);
		keyMeta.setDisplayName("§fKeyA");
		keyA.setItemMeta(keyMeta);
		ShapedRecipe keyAdd = new ShapedRecipe(keyA);
		keyAdd.shape("AA");
		keyAdd.setIngredient('A', Material.TRIPWIRE_HOOK);
		getServer().addRecipe(keyAdd);
		// Set up configs
		config.addDefault("test", true);
		config.options().copyDefaults(true);
		saveConfig();
		// Read and transfer chunk and player data from flatfile
		List<String> lineList = new ArrayList<>();

		lineList = displayObjects("users");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			playerSpecList.add(new PlayerSpec(subList[0], Byte.parseByte(subList[1]), Integer.parseInt(subList[2]), Integer.parseInt(subList[3]), false));
		}

		lineList = displayObjects("chunk");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			chunkSpecList.add(new ChunkSpec(Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2]), Byte.parseByte(subList[3]), subList[4], Boolean.parseBoolean(subList[5]), Boolean.parseBoolean(subList[6]),
				new ArrayList<String>(Arrays.asList(subList[7].split(",")))));
		}

		lineList = displayObjects("chest");
		for (int i = 0; i < lineList.size(); i++) {
			String[] subList = lineList.get(i).split(":");
			if (subList.length < 4) {
				chestSpecList.add(new ChestSpec(new Location(Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()), Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2])), ""));
			} else chestSpecList.add(new ChestSpec(new Location(Bukkit.getWorld(Bukkit.getWorlds().get(0).getName()), Integer.parseInt(subList[0]), Integer.parseInt(subList[1]), Integer.parseInt(subList[2])), subList[3]));
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
		// Find online players (for compatibility with /rl)
		for (PlayerSpec s: playerSpecList) {
			if (Bukkit.getPlayer(s.getName())!=null) s.setOnline(true);
		}
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {
		clearObject("users");
		for (int i = 0; i < playerSpecList.size(); i++) {
			String line = "";
			line += playerSpecList.get(i).getName();
			line += ":" + playerSpecList.get(i).getNation();
			line += ":" + playerSpecList.get(i).getKing();
			line += ":" + playerSpecList.get(i).credits;
			storeObject(line, "users");
		}
		clearObject("chunk");
		for (int i = 0; i < chunkSpecList.size(); i++) {
			String line = "";
			line += chunkSpecList.get(i).x;
			line += ":" + chunkSpecList.get(i).y;
			line += ":" + chunkSpecList.get(i).z;
			line += ":" + chunkSpecList.get(i).nation;
			line += ":" + chunkSpecList.get(i).owner;
			line += ":" + chunkSpecList.get(i).national;
			line += ":" + chunkSpecList.get(i).outpost;
			line += ":";
			for (String s2 : chunkSpecList.get(i).shared) {
				line += s2 + ",";
			}
			if (chunkSpecList.get(i).shared.size() == 0) line += ",";
			storeObject(line, "chunk");
		}
		clearObject("chest");
		for (int i = 0; i < chestSpecList.size(); i++) {
			String line = "";
			line += chestSpecList.get(i).coords.getBlockX();
			line += ":" + chestSpecList.get(i).coords.getBlockY();
			line += ":" + chestSpecList.get(i).coords.getBlockZ();
			line += ":" + chestSpecList.get(i).id;
			storeObject(line, "chest");
		}
		clearObject("misc");
		{
			String line = "";
			line += nationNameList.get(0);
			line += ":" + nationNameList.get(1);
			line += ":" + nationNameList.get(2);
			storeObject(line, "misc");
			if (sentenceMaxes.size() > 0) storeObject("--SENTENCE MAXIMUMS--", "misc");
			for (String s : sentenceMaxes.keySet()) {
				line = "";
				line += s + ":" + sentenceMaxes.get(s);
				storeObject(line, "misc");
			}
		}
	}

}
