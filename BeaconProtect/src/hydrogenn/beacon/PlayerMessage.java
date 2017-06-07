package hydrogenn.beacon;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum PlayerMessage {
	PROTECTED(ChatColor.RED + "This block is protected by a nearby beacon."),
	NEW_BEACON(ChatColor.AQUA + "You have placed a beacon. When activated, it will provide grief protection in a wide area.");
	private String message;
	
	private PlayerMessage(String message) {
		this.message = message;
	}

	public void send(Player player) {
		player.sendMessage(message);
	}
}
