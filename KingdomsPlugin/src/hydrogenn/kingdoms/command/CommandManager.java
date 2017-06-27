package hydrogenn.kingdoms.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

//thanks to BetaNyan for helping me avoid at least a little static abuse.
//in case you're wondering: https://www.spigotmc.org/threads/command-management.103128/
public class CommandManager implements CommandExecutor {

	private List<CommandClass> commands = new ArrayList<CommandClass>();
	
	public void registerCommand(CommandClass cmd) {
		commands.add(cmd);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
			for (CommandClass command : commands) {
				sender.sendMessage(command.getDescription());
			}
			return true;
		}
		
		String[] nargs = new String[args.length - 1];
		for (int i = 0; i < nargs.length; i++) {
			nargs[i] = args[i+1];
		}
		
		for (CommandClass command : commands) {
			if (args[0].equalsIgnoreCase(command.getName())) {
				command.run(sender, nargs);
			}
		}
		return true;
	}

}
