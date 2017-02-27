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
	private byte[] flags = new byte[12];
	private boolean[] flagsMissing = new boolean[13]; //The 13th flag indicates type.
	
	public HID() {
		Random random = new Random();
		type = random.nextBoolean();
		for (int i = 0; i < 12; i++) {
			flags[i] = (byte)ThreadLocalRandom.current().nextInt(0, 10);
		}
	}
	
	/**
	 * Returns a deep copy of the HID with the exact same traits as the original.
	 * @param original The original HID.
	 */
	public HID(HID original) {
		type = original.type;
		for (int i = 0; i < 12; i++) {
			flags[i] = original.flags[i];
			flagsMissing[i] = original.flagsMissing[i];
		}
	}
	
	/**
	 * Generate a HID that correlates to a given UUID. Creates continuity between servers.
	 * @param uuid The UUID to use.
	 */
	public HID(UUID uuid) {
		type = uuid.getLeastSignificantBits() % 2 == 0;
		for (int i = 0; i < 12; i++) {
			flags[i] = (byte) (uuid.getMostSignificantBits() % (11*i + 11) % 10);
		}
	}

	public String toString() {
		String string;
		if (flagsMissing[12])
			string = "*";
		else {
			if (type)
				string = "A";
			else
				string = "B";
		}
		
		for (int i = 0; i < 12; i++) {
			if (i % 4 == 0) { //insert a dash after every 4 flags (and 0 itself)
				string += "-";
			}
			
			if (flagsMissing[i])
				string += "*";
			else
				string += flags[i];
		}
		
		return string;
	}
	
	/**
	 * Removes a single trait from a HID.
	 * @param absolute If enabled, exactly one trait will be removed every time.
	 * Otherwise, it may "remove" a trait that has already been removed.
	 */
	public void decay(boolean absolute) {
		if (isAllMissing()) return;
		int i;
		do {
			i = ThreadLocalRandom.current().nextInt(0, 14);
		} while (absolute && flagsMissing[i]);
		flagsMissing[i] = true;
	}
	
	public boolean isAllMissing() {
		for (boolean flagMissing : flagsMissing) {
			if (!flagMissing) return false;
		}
		return true;
	}
}
