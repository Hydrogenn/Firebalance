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
			
			int wordsToRemove;
			if (args[0] == "change")
				wordsToRemove = 2;
			else
				wordsToRemove = 1;
			
			String[] words = new String[args.length-wordsToRemove];
			for (int i = 0; i < words.length; i++) {
				words[i] = args[i+wordsToRemove];
			}
			
			for (String word : words) {

				if (ruleText.length() > 0) {
					ruleText.append(" ");
				}
				ruleText.append(word);

			}

			if (args[0].equals("remove")) {

				try {

					QuotableRules.ruleSet.remove(Integer.parseInt(args[1]) - 1);
					sender.sendMessage("Removed rule.");

					save();

				} catch (ArrayIndexOutOfBoundsException e) {

					sender.sendMessage("Please specify a rule.");

				} catch (IndexOutOfBoundsException e) {

					sender.sendMessage("There aren't THAT many rules.");

				} catch (NumberFormatException e) {
					
					sender.sendMessage("That's not a number.");
					
				}

			} else if (args[0].equals("change")) {

				try {
					
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

		sender.sendMessage("Nice try, pal! You don't have permission.");

		return true;

	}

	private void save() {

		plugin.saveRules();

	}

}
