package hydrogenn.kingdoms.utils;

import org.bukkit.Bukkit;
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

		p.sendMessage(prefix + TextUtils.colorize(getPrefix() + msg));

	}

	public static void send(CommandSender s, String msg) {

		s.sendMessage(prefix + TextUtils.stripColor(getPrefix() + msg));

	}

	public static void broadcast(String msg) {

		Bukkit.broadcastMessage(prefix + TextUtils.colorize(msg));

	}

	public static void forceChat(Player p, String msg) {

		p.chat(TextUtils.colorize(msg));

	}

}
