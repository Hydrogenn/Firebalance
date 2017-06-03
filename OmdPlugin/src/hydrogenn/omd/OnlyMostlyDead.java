package hydrogenn.omd;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;

import hydrogenn.omd.file.ConfigManager;
import net.md_5.bungee.api.ChatColor;

public class OnlyMostlyDead extends JavaPlugin {
	
	private static int useDistance;
	private static int chatDistance;
	private static int viewDistance;
	private static String banMessage;
	
	private static ProtocolManager protocolManager;
	private static OnlyMostlyDead accessor;

	FileConfiguration config = getConfig();
	
	@Override
    public void onEnable() {
		accessor = this;
		//Set up configs
        config.addDefault("use-distance", 10);
        config.addDefault("chat-distance", 80);
        config.addDefault("view-distance", 30);
        config.addDefault("ban-message", ChatColor.DARK_RED + "You have died! You can only return when someone has revived you." +
        		"\n" + ChatColor.RESET + "This server has no contact. Just check in every now and then.");
        config.options().copyDefaults(true);
        saveConfig();
        
        useDistance = config.getInt("use-distance");
        banMessage = config.getString("ban-message");
        viewDistance = config.getInt("view-distance");
        
		// Register command
        getCommand("omd").setExecutor(new CommandOmd());

		// Register the event listener
        getServer().getPluginManager().registerEvents(new OmdListener(), this);
        
        // Register protocol manager
        protocolManager = ProtocolLibrary.getProtocolManager();
        
        ConfigManager.init(this);

        // Censor
        protocolManager.addPacketListener(new PacketAdapter(this,
                ListenerPriority.NORMAL, 
                PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    PacketContainer packet = event.getPacket();
                    if (packet.getEntityUseActions().read(0) == EntityUseAction.INTERACT) {
                    	if (packet.getHands().getValues().size() >= 1 && packet.getHands().read(0) == Hand.MAIN_HAND) {
                    		int id = packet.getIntegers().read(0);
                    		if (DeadPlayer.isDummy(id)) {
                    			DeadPlayer.interact(event.getPlayer(), DeadPlayer.getDeadPlayer(DeadPlayer.getOwner(id)));
                    		}
                    	}
                    }
                }
            }
        });
        
	}
	
	@Override
    public void onDisable() {
		ConfigManager.save();
	}
	
	public static final int getUseDistance() {
		return useDistance;
	}
	public static final int getViewDistance() {
		return viewDistance;
	}
	public static final int getChatDistance() {
		return chatDistance;
	}
	public static String getBanMessage() {
		return banMessage;
	}

	public static ProtocolManager getProtocol() {
		return protocolManager;
	}
	public static void displayCorpsesTo(Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(accessor,
                new Runnable() {

					@Override
					public void run() {
						player.sendMessage("Welcome delayed");
						for (DeadPlayer deadPlayer : DeadPlayer.getList()) {
							if (deadPlayer.inRange(player.getLocation())) {
								deadPlayer.show(player);
							}
						}
					}},
                200);
	}
}
