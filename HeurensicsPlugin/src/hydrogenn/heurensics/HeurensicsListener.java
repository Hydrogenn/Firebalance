package hydrogenn.heurensics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class HeurensicsListener implements Listener {
	
	public HeurensicsListener() {

	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		HSet.addPlayer(uniqueId);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		if (HSet.investigators.contains(uniqueId)) {
			HSet.investigators.remove(uniqueId);
		}
	}
	
	// TODO MINOR Detect when blocks are turned into air and remove evidence at that spot.
	@EventHandler
	public void onBlockRemoved(EntityChangeBlockEvent event) {
		if (!event.getTo().equals(Material.AIR)) return; //this only concerns blocks that have been turned into air
		Location blockLocation = event.getBlock().getLocation();
		HSet.removeHSet(blockLocation);
	}
	
	@EventHandler
	public void logBreakBlock(BlockBreakEvent event) {
		for ( Location location : getNeighborLocations(event.getBlock())) {
			mark(
					location,
					event.getPlayer(),
					LogType.BLOCK_DESTROY,
					1);
		}
	}

	@EventHandler
	public void logPlaceBlock(BlockPlaceEvent event) {
		mark(
				event.getBlockPlaced().getLocation(),
				event.getPlayer(),
				LogType.BLOCK_PLACE,
				1);
	}
	
	@EventHandler
	public void logInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		Player player = event.getPlayer();
		if (HSet.isInvestigator(player.getUniqueId())
				&& event.getAction() == Action.RIGHT_CLICK_BLOCK
				&& player.isSneaking())
			return; //We don't want investigators to leave marks
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; //We only care about right-clicks
		mark(
				event.getClickedBlock().getLocation(),
				player,
				LogType.BLOCK_INTERACT,
				1);
	}
	
	@EventHandler
	public void logPlayerMove(PlayerMoveEvent event) {
		if (event.getPlayer().isSneaking() && !Heurensics.detectSneaking()) return;
		
		mark(
				getLocationUnderneath(event.getPlayer()),
				event.getPlayer(),
				LogType.PLAYER_MOVE,
				event.getTo().distance(event.getFrom()));
	}

	@EventHandler
	public void logPlayerHurt(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) return; //We only care about players being damaged
		
		Player player = (Player) event.getEntity();
		mark(
				getLocationUnderneath(player),
				player,
				LogType.PLAYER_HURT,
				event.getDamage());
	}

	@EventHandler
	public void logPlayerDeath(PlayerDeathEvent event) {
		
		Player player = (Player) event.getEntity();
		mark(
				getLocationUnderneath(player),
				player,
				LogType.PLAYER_DEATH,
				1);
	}
	
	@EventHandler
	public void investigate(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!HSet.isInvestigator(player.getUniqueId())) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return; //don't care about anything but right-clicks
		if (!player.isSneaking()) return; //player must be sneaking to investigate
		ItemStack item = player.getInventory().getItemInMainHand();
		Location location = event.getClickedBlock().getLocation();
		HSet hSet = HSet.getHSet(location);
		
		if (hSet == null) {
			int minDistance = 10;
			for (int x = -5; x <= 5; x++) {
				for (int y = -5; y <= 5; y++) {
					for (int z = -5; z <= 5; z++) {
						Location nLocation = location.clone().add(x,y,z);
						HSet nHSet = HSet.getHSet(nLocation);
						if (nHSet != null) {
							minDistance = Math.min(minDistance,
									Math.max(  Math.max(  Math.abs(x),Math.abs(y)  ), Math.abs(z) )  );
						}
					}
				}
			}
			if (minDistance == 10)
				player.sendMessage("No sequence found.");
			else
				player.sendMessage("Sequence found "+Integer.toString(minDistance)+" blocks away");
			return;
		}
		
		//this is what it takes to detect a water bottle. :\
		if (item.getType()==Material.POTION&&((PotionMeta)item.getItemMeta()).getBasePotionData().getType()==PotionType.WATER) {
			
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.WHITE + "Evidence Bottle");
			
			List<String> lore = new ArrayList<String>();
			
			lore.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Evidence: "+hSet.getHid());
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			lore.add(ChatColor.WHITE + "Date Collected: "+dateFormat.format(new Date()));
			
			lore.add(ChatColor.WHITE + "Source: "+hSet.getLogType().source);
			itemMeta.setLore(lore);
			
			item.setItemMeta(itemMeta);
			player.getInventory().setItemInMainHand(item);
			
			HSet.removeHSet(event.getClickedBlock().getLocation());
		}
		
		player.sendMessage("Found sequence "+hSet.getHid()+" on a "+hSet.getLogType().source);
	}
	
	private Location getLocationUnderneath(Player player) {
		Location playerLocation = player.getLocation();
		playerLocation.setY(playerLocation.getY()-1);
		return playerLocation.getBlock().getLocation();
	}

	private ArrayList<Location> getNeighborLocations(Block block) {
		Location center = block.getLocation();
		ArrayList<Location> neighbors = new ArrayList<Location>();
		neighbors.add(center.clone().add(1,0,0));
		neighbors.add(center.clone().add(0,1,0));
		neighbors.add(center.clone().add(0,0,1));
		neighbors.add(center.clone().add(-1,0,0));
		neighbors.add(center.clone().add(0,-1,0));
		neighbors.add(center.clone().add(0,0,-1));
		return neighbors;
	}
	
	private void mark(Location location, Player player, LogType logType, double weight) {
		if (location.getBlock().getType().equals(Material.AIR)) return; //leaving a mark on air is useless.
		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && !Heurensics.detectInvisible()) return;
		HID id = HSet.getId(player);
		if (Math.random() <= logType.probability * weight) {
			new HSet(id,logType,location);
		}
	}
}
