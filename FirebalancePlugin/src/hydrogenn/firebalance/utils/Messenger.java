package hydrogenn.firebalance.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messenger {

	private static String prefix = "";

	public static String getPrefix() {
		return prefix;
	}

	public static void setPrefix(String prefix) {
		Messenger.prefix = prefix;
	}

	public static void send(Player p, String msg) {

		p.sendMessage(TextUtils.colorize(getPrefix() + msg));

	}

	public static void send(CommandSender s, String msg) {

		s.sendMessage(TextUtils.stripColor(getPrefix() + msg));

	}

}
