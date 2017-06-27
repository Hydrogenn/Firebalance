
package hydrogenn.kingdoms;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MyListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerSpec.getSpec(event.getPlayer())
			.login();
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		PlayerSpec.getSpec(event.getPlayer())
			.logout();
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		PlayerSpec spec = PlayerSpec.getSpec(event.getPlayer());
		if (spec.getKingdom() != null) {
			String tag = spec.getKingdom().getTag();
			event.setFormat(ChatColor.GRAY + "[" + tag + ChatColor.GRAY + "] %1$s: "+ ChatColor.WHITE +"%2$s");
		}
		else {
			event.setFormat(ChatColor.GRAY + "%1$s: " + ChatColor.WHITE + "%2$s");
		}
	}
	
	@EventHandler
	public void onLeaderDeath(PlayerDeathEvent event) {
		PlayerSpec spec = PlayerSpec.getSpec(event.getEntity());
		Kingdom kingdom = spec.getKingdom();
		if (kingdom != null && kingdom.isInLine(spec)) {
			kingdom.removeFromChain(spec.getUuid());
		}
	}
}
