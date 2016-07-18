package com.rayzr522.battlebricks;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/* 
 * Competitor.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public class Competitor {

	private Player player;
	private BrickItem brick;
	private boolean fighting;
	private int damageTaken;
	private boolean nextIsLeft;
	private int comboTaken;
	private int recovery;
	private long hp;
	private static Random rand = new Random();
	
	public Competitor(Player player) {

		this.player = player;

		ItemStack item = player.getInventory().getItemInMainHand();

		System.out.println("BrickItem.isValid(item)? " + BrickItem.isValid(item));
		if (BrickItem.isValid(item)) {

			brick = BrickItem.fromItem(item);

		}
		
		fighting = false;
		damageTaken = 0;
		nextIsLeft = rand.nextBoolean();
		comboTaken = 1;
		recovery = 0;
		if (brick!=null)
			hp = brick.getLevel()*12+24;
		else hp = 0;

	}

	public boolean isValid() {
		return brick != null;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public BrickItem getBrick() {
		return brick;
	}

	public void updateBrick() {
		PlayerInventory inv = player.getInventory();
		for (int i = 0; i < inv.getSize(); i++) {
			ItemStack item = inv.getItem(i);
			if (brick.equals(item)) {
				inv.setItem(i, brick);
			}
		}

	}

	public void setBrick(BrickItem brick) {
		this.brick = brick;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Competitor) {
			return ((Competitor) obj).getPlayer().getUniqueId().equals(getPlayer().getUniqueId());
		} else if (obj instanceof Player) {
			return ((Player) obj).getUniqueId().equals(getPlayer().getUniqueId());
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return player.hashCode();
	}

	public void msg(String msg) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}

	public String getName() {
		return player.getDisplayName();
	}

	public boolean isFighting() {
		return fighting;
	}

	public void setFighting(boolean fighting) {
		this.fighting = fighting;
	}

	public boolean nextIsLeft() {
		return nextIsLeft;
	}

	public void setNextThrow(boolean nextIsLeft) {
		this.nextIsLeft = nextIsLeft;
	}

	public void takeHit() {
		BattleBricksCommand.playSound(BattleBricksCommand.requests.get(this).getPlayer(), Sound.BLOCK_ANVIL_PLACE, 1.0f, (float) Math.max(1.75f-0.1*comboTaken,0.5f));
		damageTaken+=comboTaken;
		if (comboTaken>1) comboTaken++;
		BattleBricksCommand.updateActionBar(this);
		if (damageTaken>=hp) BattleBricksCommand.fightComplete(this,BattleBricksCommand.requests.get(this));
	}
	
	public int getDamage() {
		return damageTaken;
	}
	
	public void miss() {
		BattleBricksCommand.playSound(player, Sound.ENTITY_SILVERFISH_DEATH, 1.0f, 1.25f);
		recovery+=3;
		comboTaken++;
		BattleBricksCommand.updateActionBar(this);
	}
	
	public void recover() {
		BattleBricksCommand.playSound(player, Sound.ENTITY_ZOMBIE_INFECT, 1.0f, (float) Math.max(2.0f-0.25*recovery,0.5f));
		if (recovery>0) recovery--;
		if (recovery==0) comboTaken=1;
		BattleBricksCommand.updateActionBar(this);
	}
	
	public boolean mustRecover() {
		if (recovery>0) return true;
		else return false;
	}
	
	public int getCombo() {
		return comboTaken;
	}
	
	public int getRecovery() {
		return recovery;
	}
	
	public void newThrow() {
		nextIsLeft = rand.nextBoolean();
	}

	public long getHealth() {
		return hp;
	}
	
	public String getHealthBar(boolean self) {
		String healthRemain = Strings.repeat('■', (int) ((this.getHealth()-this.getDamage())/6));
		String healthLost = Strings.repeat('□', this.getDamage()/6);
		if (self) return healthLost+healthRemain;
		else return healthRemain+healthLost;
	}
	
	
	
}
