package hydrogenn.kingdoms.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiMessage {

	private List<String> messages = new ArrayList<String>();
	private boolean colorMode = true;
	private String prefix = null;

	public MultiMessage colorMode(boolean mode) {

		colorMode = mode;

		return this;

	}

	public MultiMessage addLine(String line) {

		if (prefix != null && prefix.length() > 0) {
			line = prefix + line;
		}

		messages.add(colorMode ? TextUtils.colorize(line) : line);

		return this;

	}

	public MultiMessage sendTo(Player p) {

		for (String msg : messages) {

			p.sendMessage(msg);

		}

		return this;

	}

	public MultiMessage sendTo(CommandSender s) {

		for (String msg : messages) {

			s.sendMessage(msg);

		}

		return this;

	}

	public MultiMessage setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

}
