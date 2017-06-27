
package hydrogenn.kingdoms;

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
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.kingdoms.command.CommandCapital;
import hydrogenn.kingdoms.command.CommandCreate;
import hydrogenn.kingdoms.command.CommandGoHomeYourDrunk;
import hydrogenn.kingdoms.command.CommandInvite;
import hydrogenn.kingdoms.command.CommandJoin;
import hydrogenn.kingdoms.command.CommandManager;
import hydrogenn.kingdoms.command.CommandRank;
import hydrogenn.kingdoms.command.CommandRename;
import hydrogenn.kingdoms.command.CommandRetire;
import hydrogenn.kingdoms.command.CommandTag;
import hydrogenn.kingdoms.file.ConfigManager;

public class Kingdoms extends JavaPlugin {
	
	FileConfiguration config = getConfig();

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

	@Override
	public void onEnable() {
		
		// Register commands
		CommandManager cmdm = new CommandManager();
		cmdm.registerCommand(new CommandCreate());
		cmdm.registerCommand(new CommandInvite());
		cmdm.registerCommand(new CommandJoin());
		cmdm.registerCommand(new CommandCapital());
		cmdm.registerCommand(new CommandGoHomeYourDrunk());
		cmdm.registerCommand(new CommandRank());
		cmdm.registerCommand(new CommandRename());
		cmdm.registerCommand(new CommandTag());
		cmdm.registerCommand(new CommandRetire());
		this.getCommand("k").setExecutor(cmdm);
		

		// Register the event listener
		getServer().getPluginManager().registerEvents(new MyListener(), this);

		// Set up the new ConfigManager class
		ConfigManager.init(this);

		// Find online players (for compatibility with /rl)
		Iterator<PlayerSpec> iter = PlayerSpec.iterator();
		while (iter.hasNext()) {
			PlayerSpec s = iter.next();
			if (Bukkit.getPlayer(s.getUuid()) != null)
				s.login();
		}

	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {

		ConfigManager.save();

	}
}
