package hydrogenn.notes;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CommandNote implements CommandExecutor {

	final ChatColor prefix = ChatColor.WHITE; //TODO add chatcolors with dyes
	
    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Unfortunately, you cannot hold paper.");
            return true;
        }

        //Check if help should be sent
        if (args.length < 1 || args[0].equals("help")) {
            sendHelp(sender);
            return true;
        }

        //Cast the sender to a player, which is confirmed at the start
        //The line in the middle was intended as a micro-optimization
        //The computer will read this millions of times, we will only read it once or twice (Hopefully.)
        Player player = (Player) sender;

        
        ItemStack item = player.getInventory().getItemInMainHand();

        //Why is there a test for Material.AIR? whatever
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("Hey, you're kind of forgetting something. The paper?");
            return true;
        }

        if (item.getType() != Material.PAPER) {
            player.sendMessage("You probably don't want to try writing on that.");
            return true;
        }

        ItemMeta itemMeta = item.getItemMeta();

        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        //
        if (isSigned(lore) && legalAfterSigning(args[0])) {
            player.sendMessage("This has been signed. No further changes can be made.");
            return true;
        }

        //Build the new text that is to be used
        StringBuilder builder = new StringBuilder();
        for (int i = numArgs(args[0]); i < args.length; i++) {
            String string = args[i];
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(string);
        }
        String newText = prefix + ChatColor.translateAlternateColorCodes('&', builder.toString());

        if (args[0].equals("name")) {
        	
            if (args.length - numArgs(args[0]) <= 0) {
                itemMeta.setDisplayName(null);
            } else {
                itemMeta.setDisplayName(newText);
            }

            //clear the description entirely
            itemMeta.setLore(null);
            
            item.setItemMeta(itemMeta);
            
        } else if (args[0].equals("desc")) {
        	
            if (args[1].equals("set")) {
                if (lore.size() > 0) {
                    lore.remove(lore.size() - 1); //simply remove the last line of lore before adding a new one.
                } //hacky, but it's fine really
            }

            if (args[1].equals("remove")) { //do something completely different if removing text
                if (lore.size() == 0) {
                    player.sendMessage("Done!"); //there's nothing to remove lol
                    return true;
                } else {
                    try { //for invalid input
                        for (int i = lore.size() - Integer.parseInt(args[2]); i >= 0 && i < lore.size(); ) {
                            lore.remove(i); //this works, just trust me
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    	if (lore.size() == 0)
                    		lore.remove(lore.size() - 1);
                    }
                }
            } else { //otherwise do the intended functionality
                lore.add(newText);
            }

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

        } else if (args[0].equals("sign")) {

            int stampLevel = 0; //how many times the stamp can be copied

            if (args.length > 1 && args[1].equals("copy")) {
                if (args.length > 2) {
                    try {
                        stampLevel = Math.abs(Integer.parseInt(args[2]));
                    } catch (NumberFormatException e) {
                        stampLevel = 1;
                    }
                } else {
                    stampLevel = 1;
                }
            }

            if (hasSignature(player.getName(), lore)) {
                player.sendMessage("Remind me again why you need to sign twice?");
                return true;
            }

            String stampTrailingString;

            if (stampLevel == 0) {
                stampTrailingString = "";
            } else {
                stampTrailingString = " " + StringUtils.repeat("*", stampLevel);
            }

            lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + player.getName() + stampTrailingString);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

        } else if (args[0].equals("copy")) {
            ItemStack stampItem = player.getInventory().getItemInOffHand();
            if (stampItem == null || stampItem.getType() == Material.AIR) {
                player.sendMessage("Um, you're forgetting something. The stamp?");
                return true;
            }

            if (stampItem.getType() != Material.PAPER) {
                player.sendMessage("That doesn't work as a stamp.");
                return true;
            }

            ItemMeta stampMeta = stampItem.getItemMeta();
            if (!stampMeta.hasDisplayName()) {
                player.sendMessage("The paper must be named to act as a stamp.");
                return true;
            }

            List<String> otherLore = stampMeta.hasLore() ? stampMeta.getLore() : new ArrayList<>();
            otherLore = lowerSignage(otherLore);
            stampMeta.setLore(otherLore);
            item.setItemMeta(stampMeta);
        }

        player.updateInventory();
        return true;
    }

    
    private boolean isSigned(List<String> lore) {
		for (String line : lore) {
			if (isSignature(line))
				return true;
		}
		return false;
	}


	/**
     * Returns a copy of the given description, but with any signatures replaced as necessary.
     * Specifically, all signatures have a single star removed from the end of them.
     * If there are no stars at the end, it removes the signature line altogether.
     * @param description
     * @return
     */
    private List<String> lowerSignage(List<String> description) {
    	
        List<Integer> linesToRemove = new ArrayList<>();
        for (int i = 0; i < description.size(); i++) {
        	
            String line = description.get(i);
            
            if (isSignature(line)) {
            	
                if (StringUtils.countMatches(line, "*") > 1) {
                	
                    description.set(i, line.substring(0, line.length() - 1));
                    
                } else if (StringUtils.countMatches(line, "*") == 1) {
                	
                    description.set(i, line.substring(0, line.indexOf(" ")));
                    
                } else {
                	
                    linesToRemove.add(i);
                    
                }
            }
        }

        return description;
    }

    private boolean isSignature(String line) {
		return !line.startsWith(ChatColor.WHITE.toString());
	}


	private int numArgs(String string) {
        return string.equals("desc") ? 2 : 1;
    }

    private void sendHelp(CommandSender sender) {
        String[] messages = {
                "All commands assume you have a paper in your main hand.",
                "/note help: You are here. (Does not require a paper)",
                "/note name <text>: Change the name of a note without XP cost.",
                "/note desc add <text>: Add a line of description to a note.",
                "/note desc set <text>: Set the last line of description to a note.",
                "/note desc remove [# of lines]: Remove the last few lines of a note.",
                "/note sign: Add your name to the description, preventing further changes.",
                "/note copy: Copy a note from the offhand to the main hand. Removes signatures.",
                "/note sign copy [# of times]: Same as '/note sign', but stays after copying a few times."
        };

        sender.sendMessage(messages);
    }

    /**
     * Returns whether the given command is legal for signed notes in the main hand.
     * @param command
     */
    private boolean legalAfterSigning(String command) {
        switch (command) {
            case "sign":
                return true;
            case "desc":
                return false;
            case "name":
            	return false; //Clearing the note by renaming it should not be allowed
            case "copy":
                return false; //Clearing the note by renaming it should not be allowed
        }

        throw new IllegalArgumentException();
    }

    private boolean hasSignature(String username, List<String> description) {
        if (description == null) {
            return false;
        }

        for (String line : description) {
            if (isSignature(line))
                return true;
        }
        return false;
    }
}
