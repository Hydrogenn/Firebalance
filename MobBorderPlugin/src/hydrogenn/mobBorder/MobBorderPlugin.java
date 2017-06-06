package hydrogenn.mobBorder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;

public class MobBorderPlugin extends JavaPlugin {
	public static List<String> ruleSet = new ArrayList<String>();

	@Override
	public void onEnable() {
		
		Bukkit.getPluginCommand("mobbuff").setExecutor(new CommandGetMobBuff());
		
		getServer().getPluginManager().registerEvents(new MobBorderListener(), this);

	}

	@Override
	public void onDisable() {

	}

	public void reload() {

	}

	public static int getLevelByLocation(Location location) {
		World world = location.getWorld();
		int rate;
		if (world.getEnvironment() == Environment.NETHER) {
			rate = 8;
		} else rate = 1;
		Location center = world.getSpawnLocation();
		double distance = location.distance(center);
		return (int) (distance / 256 * rate);
	}

}
