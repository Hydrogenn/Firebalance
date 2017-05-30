package hydrogenn.heurensics;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Heurensics Identification format.
 * @author Hydrogenn
 *
 */
public class HID {
	private boolean type;
	private byte[] flags = new byte[9];
	
	/**
	 * Generate a new HID.
	 * @param b Whether or not to generate random values.
	 */
	public HID(boolean b) {
		if (b) {
			Random random = new Random();
			type = random.nextBoolean();
			for (int i = 0; i < 9; i++) {
				flags[i] = (byte)ThreadLocalRandom.current().nextInt(0, 4);
			}
		}
	}
	
	/**
	 * Returns a deep copy of the HID with the exact same traits as the original.
	 * @param original The original HID.
	 */
	public HID(HID original) {
		type = original.type;
		for (int i = 0; i < 9; i++) {
			flags[i] = original.flags[i];
		}
	}
	
	/**
	 * Generate a HID that correlates to a given UUID. Creates continuity between servers.
	 * @param uuid The UUID to use.
	 */
	public HID(UUID uuid) {
		type = (uuid.getLeastSignificantBits() & 1) == 0;
		for (int i = 0; i < 9; i++) {
			flags[i] = (byte) (uuid.getLeastSignificantBits() >> (1 + (i<<1)) & 3);
		}
	}

	public String toString() {
		String string;
		if (type)
			string = "A";
		else
			string = "B";
		
		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0) { //insert a dash after every 3 flags (and 0 itself)
				string += "|";
			}
			if (flags[i] == 0)
				string += "ᚁ";
			if (flags[i] == 1)
				string += "ᚕ";
			if (flags[i] == 2)
				string += "ᚆ";
			if (flags[i] == 3)
				string += "ᚖ";
			if (flags[i] == -1)
				string += " ";
		}
		
		return string;
	}
	
	public static HID fromString(String string) {
		HID hid = new HID(false);
		hid.type = string.charAt(0) == 'A';
		int skip = 1;
		for (int i = 0; i < 9; i++) {
			if (string.charAt(i+skip) == '|') skip++;
			if (string.charAt(i+skip) == 'ᚁ') hid.flags[i] = 0;
			else if (string.charAt(i+skip) == 'ᚕ') hid.flags[i] = 1;
			else if (string.charAt(i+skip) == 'ᚆ') hid.flags[i] = 2;
			else if (string.charAt(i+skip) == 'ᚖ') hid.flags[i] = 3;
			else if (string.charAt(i+skip) == ' ') hid.flags[i] = -1;
		}
		return hid;
	}
	
	/**
	 * Removes a single trait from a HID.
	 * @return true if successful, false if only the 'type' remains.
	 */
	public boolean decay() {
		if (isAllMissing()) return false; //only the type remains, the whole thing should be deleted.
		int i;
		do {
			i = ThreadLocalRandom.current().nextInt(0, 9);
		} while (flags[i] == -1);
		flags[i] = -1;
		if (isAllMissing()) return true;
		do {
			i = ThreadLocalRandom.current().nextInt(0, 9);
		} while (flags[i] == -1);
		flags[i] += 2;
		flags[i] %= 4;
		return true;
	}
	
	public boolean isAllMissing() {
		return flags() == 1;
	}

	public int flags() {
		int numFlags = 0;
		for (byte flag : flags) {
			if (flag != -1) numFlags++;
		}
		return numFlags + 1; //the mere fact that it exists means that the type flag is visible
	}
}
