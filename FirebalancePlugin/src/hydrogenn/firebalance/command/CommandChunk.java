
package hydrogenn.firebalance.command;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import hydrogenn.firebalance.ChunkSpec;
import hydrogenn.firebalance.Firebalance;
import hydrogenn.firebalance.PlayerSpec;
import hydrogenn.firebalance.SchedulerCache;
import hydrogenn.firebalance.utils.ArgList;
import hydrogenn.firebalance.utils.Messenger;
import hydrogenn.firebalance.utils.MultiMessage;

@SuppressWarnings("deprecation")
public class CommandChunk implements CommandExecutor {

	private MultiMessage helpMessage = new MultiMessage().setPrefix("&7")
			.addLine("/chunk info: See information about the claim you're in.")
			.addLine("/chunk claim: Claim land next to your nation.")
			.addLine("/chunk outpost: Establish a claim outside of your nation.")
			.addLine("/chunk share: Shares a claim with another player.")
			.addLine("/chunk unshare: Removes someone from being shared.")
			.addLine("/chunk embassy: Shares a claim with another nation.")
			.addLine("/chunk disclaim: Removes your own claim.")
			.addLine("(TEMPORARILY DISABLED) /chunk raze: Destroys an enemy nation's claim.")
			.addLine("/chunk lock: Prevents further changes to the claim.")
			.addLine("/chunk unlock: Allows further changes to the claim.");

	private ArgList possibleArgs = new ArgList().add("info", "claim", "outpost", "conquer", "share", "unshare",
			"embassy", "disclaim", "raze", "lock", "unlock", "help");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {

		if (args.length < 1) {

			helpMessage.sendTo(sender);
			return true;

		}

		if (!possibleArgs.isValid(args[0])) {

			helpMessage.sendTo(sender);
			return true;

		}

		String arg = args[0].toLowerCase();

		if (arg.equals("help")) {
			helpMessage.sendTo(sender);
		}

		if (sender instanceof Player) {

			final Player player = (Player) sender;
			final int x = player.getLocation().getChunk().getX();

			int yp = 0;
			if (player.getLocation().getBlockY() < 56)
				yp = -1;
			if (player.getLocation().getBlockY() > 112)
				yp = 1;

			final int y = yp;
			final int z = player.getLocation().getChunk().getZ();

			byte nationp = 0;
			int outpostCount = 0;
			boolean king = false;

			String nationStringp = "";
			String chunkNationString = "yourself";
			for (PlayerSpec s : PlayerSpec.list) {
				if (s.getName().equals(player.getName())) {
					nationp = s.getNation();
					king = (s.getKing() == 1);
					if (nationp == -1 && !arg.equals("info")) {
						Messenger.send(player, "You need to be a member of a nation before you can do land claims.");
						return true;
					}
					nationStringp = Firebalance.getNationName(nationp, false);
				}
			}
			final byte nation = nationp;
			final String nationString = nationStringp;
			// Return information on current chunk
			if (arg.equals("info")) {
				for (ChunkSpec s : ChunkSpec.list) {
					if (s.getX() == x && s.getY() == y && s.getZ() == z) {
						Messenger.send(player, "&7You are in claimed " + ChunkSpec.getHeightString(s.getY()) + " at " + s.getX()
								+ ", " + s.getZ() + ".");
						Messenger.send(player, "&7It is owned by " + s.getOwner() + ", representing "
								+ Firebalance.getNationName(s.getNation(), false) + ".");
						Messenger.send(player, "&7Public: " + s.isNational() + ", Outpost: " + s.isOutpost() + ".");
						Messenger.send(player, "&7Shared Players: " + s.getShared().toString());
					}
				}
				return true;
			}
			// Filter out attempts to claim land not next to already claimed
			// land
			if (arg.equals("claim") || arg.equals("conquer")) {
				if (nation != 0) {
					boolean test = false;
					boolean verify = false;
					for (ChunkSpec s : ChunkSpec.list) {
						if (s.getNation() == nation) {
							test = true;
						}
						if (!s.isOutpost()
								&& ((z + 1 == s.getZ() && x == s.getX()) || (z - 1 == s.getZ() && x == s.getX())
										|| (x + 1 == s.getX() && z == s.getZ()) || (x - 1 == s.getX() && z == s.getZ()))
								&& s.getNation() == nation) {
							verify = true;
						}
					}
					if (test && !verify) {
						Messenger.send(player, "&cYou can't exand there, it must be next to existing land.");
						return true;
					}
				}
			}
			// Loop through all chunks for one at this exact spot
			for (final Iterator<ChunkSpec> i = ChunkSpec.list.iterator(); i.hasNext();) {
				final ChunkSpec s = (ChunkSpec) i.next();
				if (s.isOutpost() == true && arg.equals("outpost")) {
					outpostCount++;
				}
				if (s.getX() == x && s.getY() == y && s.getZ() == z) {
					final byte chunkNation = s.getNation();
					chunkNationString = Firebalance.getNationName(chunkNation, false);
					// End if the function isn't designed for friendly chunks
					if ((arg.equals("claim") || arg.equals("outpost") || arg.equals("conquer") || arg.equals("raze"))
							&& s.getNation() == nation) {
						Messenger.send(player, "&7Your nation already owns this.");
						return true;
					}
					// End if the function isn't designed for enemy chunks
					if ((arg.equals("claim") || arg.equals("outpost") || arg.equals("embassy")) && s.getNation() != nation) {
						Messenger.send(player, "&cThat chunk is already claimed by " + chunkNationString + ".");
						return true;
					}
					// End if the function isn't designed for other player's
					// chunks (excluding kings)
					if ((arg.equals("disclaim") || arg.equals("lock") || arg.equals("unlock") || arg.equals("share")
							|| arg.equals("unshare")) && !s.getOwner().equals(player.getName())
							&& !(king && s.getNation() == nation)) {
						Messenger.send(player, "&cYou can't do that if you don't own the chunk.");
						return true;
					}
					// Perform the conquer function
					if (arg.equals("conquer")) {
						SchedulerCache.addScheduler("chunkConquer", player.getName(), 300L, new Runnable() {

							public void run() {
								s.setNation(nation);
								Messenger.send(player,
										"&cConquered a chunk for " + nationString + " at " + x + "," + z);
							}
						});
						Messenger.send(player, "&7Conquering chunk... (15s)");
						return true;
					}
					// Perform the raze function
					// if (arg.equals("raze")) {
					// byte verify = 0;
					// for (ChunkSpec s2 : Firebalance.chunkSpecList) {
					// int x2 = s2.x;
					// int y2 = s2.y;
					// int z2 = s2.z;
					// if (((z + 1 == z2 && x == x2 && y == y2) || (z - 1 == z2
					// && x == x2 && y == y2) || (x + 1 == x2 && z == z2 && y ==
					// y2) || (x - 1 == x2 && z == z2 && y == y2) || (x == x2 &&
					// z == z2 && y + 1 == y2)
					// || (x == x2 && z == z2 && y - 1 == y2)) && s2.nation ==
					// chunkNation) {
					// verify++;
					// }
					// }
					// if (verify > 3) {
					// Messenger.send(player, "&cYou can't raze that land, there
					// are too many surrounding chunks.");
					// return true;
					// } else {
					// Firebalance.addScheduler("chunkRaze", player.getName(),
					// 300L, new Runnable() {
					//
					// public void run() {
					// i.remove();
					// Messenger.send(player, "&cRemoved a chunk owned by " +
					// Firebalance.getNationName(chunkNation, false) + " at " +
					// x + "," + z);
					// }
					// });
					// Messenger.send(player, "&7Razing chunk... (15s)");
					// return true;
					// }
					// }
					// Perform the disclaim function
					if (arg.equals("disclaim")) {
						SchedulerCache.addScheduler("chunkDisclaim", player.getName(), 60L, new Runnable() {

							public void run() {
								i.remove();
								if (s.getOwner().equals(player.getName()))
									Messenger.send(player, "&7Removed claim on your chunk at " + x + "," + z);
								else
									Messenger.send(player, "&cRemoved claim another player's chunk at " + x + "," + z);
							}
						});
						Messenger.send(player, "&7Disclaiming chunk... (3s)");
						return true;
					}
					if (arg.equals("lock")) {
						SchedulerCache.addScheduler("chunkLock", player.getName(), 200L, new Runnable() {

							public void run() {
								s.setNational(false);
								if (s.getOwner().equals(player.getName()))
									Messenger.send(player, "&7Locked your chunk at " + x + "," + z);
								else
									Messenger.send(player, "&cLocked another player's chunk at " + x + "," + z);
							}
						});
						Messenger.send(player, "&7Locking chunk... (10s)");
						return true;
					}
					if (arg.equals("unlock")) {
						SchedulerCache.addScheduler("chunkUnlock", player.getName(), 60L, new Runnable() {

							public void run() {
								s.setNational(true);
								if (s.getOwner().equals(player.getName()))
									Messenger.send(player, "&7Unlocked your chunk at " + x + "," + z);
								else
									Messenger.send(player, "&cUnlocked another player's chunk at " + x + "," + z);
							}
						});
						Messenger.send(player, "&7Unlocking chunk... (3s)");
						return true;
					}
					if (arg.equals("share")) {
						if (s.getShared().contains(args[1]))
							Messenger.send(player, "This claim is already shared with that player.");
						else
							try {
								for (PlayerSpec s2 : PlayerSpec.list) {
									if (s2.getName().equals(args[1])) {
										s.getShared().add(args[1]);
										Messenger.send(player, "Shared your chunk with " + args[1]);
										if (Bukkit.getPlayer(args[1]) != null)
											Bukkit.getPlayer(args[1])
													.sendMessage(player.getName() + " has shared a chunk with you.");
									}
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								Messenger.send(player, "You'll have to specify a player to share with.");
							}
						return true;
					}
					if (arg.equals("unshare")) {
						try {
							for (Iterator<String> i2 = s.getShared().iterator(); i2.hasNext();) {
								String s2 = i2.next();
								if (s2.equals(args[1])) {
									i2.remove();
									Messenger.send(player, "Removed a share with " + args[1]);
									if (Bukkit.getPlayer(args[1]) != null)
										Bukkit.getPlayer(args[1]).sendMessage(
												player.getName() + " has removed their shared chunk with you.");
								}
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							Messenger.send(player, "You'll have to specify a player to share with.");
						}
						return true;
					}
					if (arg.equals("embassy")) {
						if (king) {
							try {
								final byte otherNation = Firebalance.getNationByte(args[1]);
								if ((otherNation & nation) > 0) {
									Messenger.send(player, "You are in that nation.");
									return true;
								}
								if (otherNation == 0) {
									Messenger.send(player, "You can't have a freelance embassy!");
									return true;
								}
								SchedulerCache.addScheduler("chunkEmbassy", player.getName(), 200L, new Runnable() {

									public void run() {
										s.setNation((byte) (otherNation | nation));
										for (PlayerSpec s2 : PlayerSpec.list) {
											if ((s2.getNation() & otherNation) > 0 && s2.getKing() == 1) {
												Bukkit.getPlayer(s2.getName())
														.sendMessage(nationString
																+ " has established an embassy with you at " + s.getX()
																+ ", " + s.getZ());
											}
										}
										Messenger.send(player, "Embassy established with " + args[1]);
									}
								});
								Messenger.send(player, "&7Creating embassy... (10s)");

								return true;
							} catch (ArrayIndexOutOfBoundsException e) {
								Messenger.send(player, "You'll need to specify a nation. (By name.)");
								return true;
							}
						} else
							Messenger.send(player, "Only kings can set up embassies.");
						return true;
					}
				}
			}
			if (arg.equals("claim")) {
				SchedulerCache.addScheduler("chunkClaim", player.getName(), 60L, new Runnable() {

					public void run() {
						ChunkSpec.list.add(new ChunkSpec(x, y, z, nation, player.getName(), true, false));
						Messenger.send(player,
								"&7Peacefully claimed a chunk for " + nationString + " at " + x + "," + z);
					}
				});
				Messenger.send(player, "&7Claiming chunk... (3s)");
			}
			if (arg.equals("outpost") && outpostCount < 2) {
				if (outpostCount < 2) {
					SchedulerCache.addScheduler("chunkOutpost", player.getName(), 200L, new Runnable() {

						public void run() {
							ChunkSpec.list.add(new ChunkSpec(x, y, z, nation, player.getName(), true, true));
							Messenger.send(player,
									"&7Peacefully claimed an outpost for " + nationString + " at " + x + "," + z);
						}
					});
					Messenger.send(player, "&7Claiming outpost... (10s)");
				} else
					Messenger.send(player, "&cYou have too many outposts.");
			} else if (arg.equals("raze") || arg.equals("conquer") || arg.equals("share") || arg.equals("unshare")
					|| arg.equals("lock") || arg.equals("unlock") || arg.equals("embassy") || arg.equals("disclaim")) {
				Messenger.send(player, "&7You can't do that in the wilderness.");
			}
		} else {
			for (ChunkSpec s : ChunkSpec.list) {
				String output = Integer.toString(s.getX());
				output = output + " " + s.getY();
				output = output + " " + s.getZ();
				output = output + " " + s.getOwner();
				output = output + " " + s.getNation();
				Bukkit.getConsoleSender().sendMessage(output);
			}
		}
		return true;
	}
}