package hydrogenn.quotableRules;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandChangeRule implements CommandExecutor {

	private QuotableRules plugin;

	public CommandChangeRule(QuotableRules plugin) {

		this.plugin = plugin;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender.hasPermission("rules.change")) {

			if (args.length < 1) {
				return false;
			}

			args[0] = args[0].toLowerCase();

			StringBuilder ruleText = new StringBuilder();

			for (String string : args) {

				if (ruleText.length() > 0) {
					ruleText.append(" ");
				}
				ruleText.append(string);

			}

			if (args[0].equals("remove")) {

				try {

					QuotableRules.ruleSet.remove(Integer.parseInt(args[1]));
					sender.sendMessage("Removed rule.");

					save();

				} catch (ArrayIndexOutOfBoundsException e) {

					sender.sendMessage("Please specify a rule.");

				} catch (IndexOutOfBoundsException e) {

					sender.sendMessage("There aren't THAT many rules.");

				}

			} else if (args[0].equals("change")) {

				try {

					ruleText.delete(0, 10);
					QuotableRules.ruleSet.set(Integer.parseInt(args[1]) - 1, ruleText.toString());
					sender.sendMessage("Changed rule.");

					save();

				} catch (ArrayIndexOutOfBoundsException e) {

					sender.sendMessage("Please specify a rule and its new text.");

				} catch (IndexOutOfBoundsException e) {

					sender.sendMessage("There aren't THAT many rules.");

				}

			} else if (args[0].equals("add")) {

				try {

					ruleText.delete(0, 4);
					QuotableRules.ruleSet.add(ruleText.toString());
					sender.sendMessage("Added rule.");

					save();

				} catch (ArrayIndexOutOfBoundsException e) {

					sender.sendMessage("Please specify a new rule.");

				}

			} else if (args[0].equals("reload")) {

				plugin.reload();

			} else {

				sender.sendMessage("Unknown command: '" + args[0] + "'");
				return false;

			}
			
			return true;

		}

		sender.sendMessage("Nice try, pal!");

		return true;

	}

	private void save() {

		plugin.saveRules();

	}

}
