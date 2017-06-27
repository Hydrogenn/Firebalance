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

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equals("help")) {
            sendHelp(sender);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Unfortunately, you cannot hold paper.");
            return true;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null) {
            player.sendMessage("Hey, you're kind of forgetting something. The paper?");
            return true;
        }
        if (item.getType() != Material.PAPER) {
            player.sendMessage("You probably don't want to try writing on that.");
            return true;
        }

        ChatColor prefix = ChatColor.WHITE; //TODO implement color prefixes with dyes

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.getDisplayName() == null && !args[0].equals("name") && !args[0].equals("copy")) {
            player.sendMessage("You can only write on named paper. Who knows why.");
            return true;
        }

        List<String> lore = itemMeta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        if (isSigned(lore) && changesContent(args[0])) {
            player.sendMessage("This has been signed. No further changes can be made.");
            return true;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = numArgs(args[0]); i < args.length; i++) {
            String string = args[i];
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(string);
        }

        if (args[0].equals("name")) {
            if (args.length < 2) {
                itemMeta.setDisplayName(null);
            } else {
                itemMeta.setDisplayName(prefix + builder.toString());
            }

            itemMeta.setLore(null);
            item.setItemMeta(itemMeta);
        } else if (args[0].equals("desc")) {

            if (args[1].equals("set")) {
                if (lore.size() > 0) {
                    lore.remove(lore.size() - 1); //simply remove the last line of lore before adding a new one.
                }
            }

            if (args[1].equals("remove")) {
                if (itemMeta.getLore().size() == 0) {
                    player.sendMessage("Done!");
                } else {
                    try {
                        for (int i = lore.size() - Integer.parseInt(args[2]); i >= 0 && i < lore.size(); ) {
                            lore.remove(i);
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        lore.remove(lore.size() - 1);
                    }
                }
            } else {
                lore.add(prefix + builder.toString());
            }

            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

        } else if (args[0].equals("sign")) {

            int copyLevel = 0;

            if (args.length > 1 && args[1].equals("copy")) {
                if (!canBeCopied(lore)) {
                    player.sendMessage(firstSigner(lore) + " probably wouldn't appreciate that.");
                    return true;
                } else {
                    if (args.length > 2) {
                        try {
                            copyLevel = Math.abs(Integer.parseInt(args[2]));
                        } catch (NumberFormatException e) {
                            copyLevel = 1;
                        }
                    } else {
                        copyLevel = 1;
                    }
                }
            }

            if (hasSigned(player.getName(), lore)) {
                player.sendMessage("Signing twice might sound fun, but will it get you anywhere?");
                return true;
            }

            String copyString;
            if (copyLevel == 0) {
                copyString = "";
            } else {
                copyString = " " + StringUtils.repeat("*", copyLevel);
            }
            lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + player.getName() + copyString);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
        } else if (args[0].equals("copy")) {
            ItemStack otherItem = player.getInventory().getItemInOffHand();
            if (otherItem == null) {
                player.sendMessage("Um, you're forgetting something. The stamp?");
                return true;
            }
            if (otherItem.getType() != Material.PAPER) {
                player.sendMessage("That doesn't work as a stamp.");
                return true;
            }
            ItemMeta otherMeta = otherItem.getItemMeta();
            if (otherMeta.getDisplayName() == null) {
                player.sendMessage("The paper must be named to act as a stamp.");
                return true;
            }
            List<String> otherLore = otherMeta.getLore();
            otherLore = lowerSignage(otherLore);
            otherMeta.setLore(otherLore);
            item.setItemMeta(otherMeta);
        }

        player.updateInventory();
        return true;
    }

    private List<String> lowerSignage(List<String> description) {
        List<Integer> linesToRemove = new ArrayList<>();
        for (int i = 0; i < description.size(); i++) {
            String line = description.get(i);
            if (line.contains(ChatColor.GRAY.toString() + ChatColor.ITALIC)) {
                if (StringUtils.countMatches(line, "*") > 1) {
                    description.set(i, line.substring(0, line.length() - 1));
                } else if (StringUtils.countMatches(line, "*") == 1) {
                    description.set(i, line.substring(0, line.indexOf(" ")));
                } else {
                    linesToRemove.add(i); //cannot be removed in this scope, so this is done instead.
                }
            }
        }

        int linesRemoved = 0;
        for (int index : linesToRemove) { //assumes that the lines are in order, which they are.
            description.remove(index - linesRemoved);
            linesRemoved++;
        }

        return description;
    }

    private int numArgs(String string) {
        if (string.equals("desc")) {
            return 2;
        }
        return 1;
    }

    private void sendHelp(CommandSender sender) {
        String[] messages = {
                "All commands assume you have a paper in your main hand.",
                "/note help: You are here. (Does not require a paper.)",
                "/note name <text>: Change the name of a note without xp cost.",
                "/note desc add <text>: Add a line of description to a note.",
                "/note desc set <text>: Set the last line of description to a note.",
                "/note desc remove [# of lines]: Remove the last few lines of a note.",
                "/note sign: Add your name to the description, preventing further changes.",
                "/note copy: Copy a note from the offhand to the main hand. Removes signatures.",
                "/note sign copy [# of times]: Same as '/note sign', but stays after copying a few times."
        };

        sender.sendMessage(messages);
    }

    private boolean changesContent(String command) throws IllegalArgumentException {
        switch (command) {
            case "name":
                return true;
            case "desc":
                return true;
            case "sign":
                return false;
            case "copy":
                throw new IllegalArgumentException("Attempted to determine whether '/note copy' changes the content, which it does for one paper but not the other");
        }

        throw new IllegalArgumentException();
    }

    private boolean canBeCopied(List<String> description) {
        if (description == null) {
            return true;
        }

        for (String line : description) {
            if (line.contains(ChatColor.GRAY.toString() + ChatColor.ITALIC)) {
                return line.contains("*");
            }
        }

        return true;
    }

    private boolean hasSigned(String username, List<String> description) {
        if (description == null) {
            return false;
        }

        for (String line : description) {
            if (line.contains(ChatColor.GRAY.toString() + ChatColor.ITALIC + username + " ")) {
                return true;
            } else if (line.equals(ChatColor.GRAY.toString() + ChatColor.ITALIC + username)) {
                return true;
            }
        }

        return false;
    }

    private String firstSigner(List<String> description) {
        if (description == null) {
            return null;
        }

        for (String line : description) {
            if (line.contains(ChatColor.GRAY.toString() + ChatColor.ITALIC)) {
                return removeStars(line);
            }
        }

        return null;
    }

    private boolean isSigned(List<String> description) {
        if (description == null) {
            return false;
        }

        for (String line : description) {
            if (line.contains(ChatColor.GRAY.toString() + ChatColor.ITALIC)) {
                return true;
            }
        }

        return false;
    }

    private String removeStars(String name) { //removes stars if present and the space before it in a name.
        if (name.contains("*")) {
            name = name.substring(0, name.indexOf("*") - 1);
        }

        return name;
    }
}

/*
  Paste Bin:
  if (args[0].equals("desc") && (args.length<2 || (
  !args[1].equals("set") &&
  !args[1].equals("add") &&
  !args[1].equals("remove") ) ) ) return false;
 */

