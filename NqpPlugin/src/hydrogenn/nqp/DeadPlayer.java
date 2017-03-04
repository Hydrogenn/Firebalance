package hydrogenn.nqp;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeadPlayer {

	private static HashMap<Inventory,UUID> activeInventories = new HashMap<Inventory,UUID>();
	private static HashMap<UUID,UUID> carriers = new HashMap<UUID,UUID>();
	
	private Location location;
	private Inventory inventory;
	private String name;
	private UUID uuid;
	private UUID carrier;
	private boolean isStillDead;
	
	public DeadPlayer() {
		
	}
	
	public DeadPlayer(Player player) {
		location = player.getLocation();
		name = player.getName();
		inventory = Bukkit.createInventory(null,45,name);
		inventory.setContents(player.getInventory().getContents());
		uuid = player.getUniqueId();
		carrier = null;
		isStillDead = true;
	}
	
	
	public Inventory getMainInventory() {
		return inventory;
	}

	public void setMainInventory(Inventory mainInventory) {
		this.inventory = mainInventory;
	}

	public Location getLocation() {
		if (carrier == null) return location;
		else return Bukkit.getServer().getPlayer(carrier).getLocation();
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public UUID getCarrier() {
		
		return carrier;
	}
	public void setCarrier(UUID carrier) {
		if (this.carrier != null) carriers.remove(this.carrier);
		if (carrier != null) carriers.put(carrier,uuid);
		this.carrier = carrier;
	}

	public boolean isStillDead() {
		return isStillDead;
	}

	public void setStillDead(boolean isStillDead) {
		this.isStillDead = isStillDead;
	}

	public void unload(Player player) {
		ItemStack[] contents = inventory.getContents();
		Inventory playerInventory = player.getInventory();
		for (int i = 0; i < 41; i++) {
			playerInventory.setItem(i, contents[i]);
		}
		player.updateInventory();
		
	}
	
	public Inventory openInventory() {
		activeInventories.put(inventory, uuid);
		return inventory;
	}

	public boolean isBeingLooted() {
		return activeInventories.containsValue(uuid);
	}

	public static boolean isActiveInventory(Inventory inventory) {
		return activeInventories.containsKey(inventory);
	}

	public static void closeInventory(Inventory inventory) {
		activeInventories.remove(inventory);
	}

	public static boolean isCarrier(Player player) {
		return carriers.containsKey(player.getUniqueId());
	}

	public static UUID getCarrying(Player player) {
		return carriers.get(player.getUniqueId());
	}
	
	
}
