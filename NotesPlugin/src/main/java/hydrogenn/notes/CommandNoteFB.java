package hydrogenn.notes;

import hydrogenn.firebalance.PlayerSpec;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class CommandNoteFB implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        if (args[0].equals("desc")
                && (args.length < 2 || (!args[1].equals("set") && !args[1].equals("add") && !args[1].equals("remove")))) {
            return false;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack paper = player.getInventory().getItemInMainHand();
            ItemMeta paperMeta = paper.getItemMeta();
            boolean king = false;
            String prefix = "§f";
            PlayerSpec s = PlayerSpec.getPlayer(player.getUniqueId());
            int playerNation = s.getNation();
            king = s.getRole() == 1;
            if (playerNation == 0) {
                prefix = "§f";
            }
            if (king) {
                if (playerNation == 1) {
                    prefix = "§4";
                }
                if (playerNation == 2) {
                    prefix = "§2";
                }
                if (playerNation == 4) {
                    prefix = "§9";
                }
            }
            if (paperMeta.getLore() != null) {
                for (int i = 0; i < paperMeta.getLore().size(); i++) {
                    if (paperMeta.getLore().get(i).contains("§7§o")) {
                        player.sendMessage("This has been signed. No further changes can be made.");
                        return true;
                    }
                }
            }
            if (paper.getType() != Material.PAPER && paper.getType() != Material.WOOD_BUTTON) {
                player.sendMessage("You cannot write on that. Well, it wouldn't be practical to.");
            } else if (paperMeta.getDisplayName() == null && args[0].equals("desc")) {
                player.sendMessage("You can only add descriptions to named paper.");
            } else {
                StringBuilder builder = new StringBuilder();
                for (String string : args) {
                    if (builder.length() > 0) {
                        builder.append(" ");
                    }
                    builder.append(string);
                }
                builder.delete(0, 5);
                if (args[0].equals("name")) {
                    if (args.length < 2) {
                        paperMeta.setDisplayName(null);
                    } else {
                        paperMeta.setDisplayName(prefix + builder.toString());
                    }
                    paperMeta.setLore(null);
                    paper.setItemMeta(paperMeta);
                } else if (args[0].equals("desc")) {
                    builder.delete(0, args[1].length() + 1);
                    List<String> lore = new ArrayList<>();
                    if (paperMeta.getLore() != null) {
                        lore.addAll(paperMeta.getLore());
                    }
                    if (args[1].equals("set")) {
                        if (lore.size() > 0) {
                            lore.remove(lore.size() - 1);
                        }
                    }
                    if (args[1].equals("remove")) {
                        try {
                            for (int i = lore.size() - Integer.parseInt(args[2]); i >= 0
                                    && i < paperMeta.getLore().size(); ) {
                                lore.remove(i);
                            }
                        } catch (NumberFormatException e) {
                            lore.remove(lore.size() - 1);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            lore.remove(lore.size() - 1);
                        }

                    } else {
                        lore.add("§f" + builder.toString());
                    }
                    paperMeta.setLore(lore);
                    paper.setItemMeta(paperMeta);
                }
            }
        }

        // If the player (or console) uses our command correct, we can return
        // true
        // Returning false will imply that the command is invalid and will
        // display the usage to the player.
        return true;
    }
}
