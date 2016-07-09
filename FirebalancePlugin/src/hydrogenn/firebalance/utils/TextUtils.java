
package hydrogenn.firebalance.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class TextUtils {

	public static String colorize(String text) {

		if (text == null) {
			return text;
		}
		return ChatColor.translateAlternateColorCodes('&', text);

	}

	public static List<String> colorizeMultiple(String... texts) {

		List<String> output = new ArrayList<String>();
		for (String str : texts) {
			output.add(colorize(str));
		}
		return output;

	}

	public static String uncolorize(String text) {

		return text.replace(ChatColor.COLOR_CHAR, '&');

	}

	public static String stripColor(String text) {

		return ChatColor.stripColor(text);

	}

}
