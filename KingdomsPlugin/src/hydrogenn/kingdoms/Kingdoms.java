
package hydrogenn.kingdoms;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import hydrogenn.kingdoms.command.CommandCapital;
import hydrogenn.kingdoms.command.CommandCreate;
import hydrogenn.kingdoms.command.CommandGoHomeYourDrunk;
import hydrogenn.kingdoms.command.CommandInvite;
import hydrogenn.kingdoms.command.CommandJoin;
import hydrogenn.kingdoms.command.CommandList;
import hydrogenn.kingdoms.command.CommandManager;
import hydrogenn.kingdoms.command.CommandPermission;
import hydrogenn.kingdoms.command.CommandRank;
import hydrogenn.kingdoms.command.CommandRename;
import hydrogenn.kingdoms.command.CommandRetire;
import hydrogenn.kingdoms.command.CommandTag;
import hydrogenn.kingdoms.file.ConfigManager;

public class Kingdoms extends JavaPlugin {
	
	FileConfiguration config = getConfig();

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
		cmdm.registerCommand(new CommandPermission());
		cmdm.registerCommand(new CommandList());
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
