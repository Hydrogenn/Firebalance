package hydrogenn.mobBorder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class MobBorderPlugin extends JavaPlugin {

	public double levelUpWeight;
	public double damageBuff;
	public double healthBuff;
	public double experienceYield;
	public boolean pvpMode;
	
	@Override
	public void onEnable() {
		
		FileConfiguration config = getConfig();
		levelUpWeight = config.getDouble("blocks-to-level-up");
		damageBuff = config.getDouble("damage-buff");
		healthBuff = config.getDouble("health-buff");
		experienceYield = config.getDouble("experience-yield");
		pvpMode = config.getBoolean("affects-pvp");
		
		Bukkit.getPluginCommand("mobbuff").setExecutor(new CommandGetMobBuff(this));
		
		getServer().getPluginManager().registerEvents(new MobBorderListener(this), this);

	}

	@Override
	public void onDisable() {

	}

	public void reload() {

	}

	public int getLevelByLocation(Location location) {
		World world = location.getWorld();
		int rate;
		if (world.getEnvironment() == Environment.NETHER) {
			rate = 8;
		} else rate = 1;
		Location center = world.getSpawnLocation();
		double distance = location.distance(center);
		return (int) (distance / levelUpWeight * rate);
	}

	public String getDisplayBuff(int mLevel, int pLevel) {
		double damageBuff = Math.round(100*getDamageBuff(mLevel, pLevel));
		double healthBuff = Math.round(100*getHealthBuff(mLevel, pLevel));
		double experienceYield = Math.round(100*getYield(mLevel, pLevel));
		if (damageBuff == healthBuff && healthBuff == experienceYield)
			return "BUFF: "+damageBuff+"%";
		else if (damageBuff == healthBuff)
			return "STAT/EXP: "+damageBuff+"/"+experienceYield;
		else
			return "DMG/HP/EXP: "+damageBuff+"/"+healthBuff+"/"+experienceYield;
	}

	public double getDamageBuff(int mLevel, int pLevel) {
		int relativeLevel = mLevel - pLevel;
		if (relativeLevel < 0) return 1;
		return (1 + damageBuff*relativeLevel);
	}

	public double getHealthBuff(int mLevel, int pLevel) {
		int relativeLevel = mLevel - pLevel;
		if (relativeLevel < 0) return 1;
		return (1 + healthBuff*relativeLevel);
	}

	public double getYield(int mLevel, int pLevel) {
		int relativeLevel = mLevel - pLevel;
		if (relativeLevel < 0) return 1;
		return (1 + experienceYield*relativeLevel);
	}

	public boolean pvp() {
		return pvpMode;
	}

}
