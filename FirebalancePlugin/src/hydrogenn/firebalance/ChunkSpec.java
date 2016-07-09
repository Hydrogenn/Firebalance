package hydrogenn.firebalance;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

public class ChunkSpec {
	private int x;
	private int y;
	private int z;
	private byte nation;
	private String owner;
	private boolean national;
	private boolean outpost;
	private ArrayList<String> shared;
	public static List<ChunkSpec> list = new ArrayList<>();
	public static Hashtable<int[], ChunkSpec> table = new Hashtable<int[], ChunkSpec>();

	public ChunkSpec(int x, int y, int z, byte nation, String owner, boolean national, boolean outpost,
			ArrayList<String> shared) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setNation(nation);
		this.setOwner(owner);
		this.setNational(national);
		this.setOutpost(outpost);
		this.setShared(shared);
		// TODO send new items to table
		// int[] xyz = {x,y,z};
		// table.put(xyz, this);
	}

	public ChunkSpec(int x, int y, int z, byte nation, String owner, boolean national, boolean outpost) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
		this.setNation(nation);
		this.setOwner(owner);
		this.setNational(national);
		this.setOutpost(outpost);
		this.setShared(new ArrayList<String>());
	}

	public byte getNation() {
		return nation;
	}

	public void setNation(byte nation) {
		this.nation = nation;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public boolean isNational() {
		return national;
	}

	public void setNational(boolean national) {
		this.national = national;
	}

	public ArrayList<String> getShared() {
		return shared;
	}

	public void setShared(ArrayList<String> shared) {
		this.shared = shared;
	}

	public boolean isOutpost() {
		return outpost;
	}

	public void setOutpost(boolean outpost) {
		this.outpost = outpost;
	}

	public static String getHeightString(int height) {
		String result = null;
		if (height == -1)
			result = "Undergrounds";
		if (height == 0)
			result = "Surface";
		if (height == 1)
			result = "Skyloft";
		return result;
	}

	public static ChunkSpec loadFromConfig(YamlConfiguration config) {

		int x = config.getInt("x");
		int y = config.getInt("y");
		int z = config.getInt("z");
		byte nation = (byte) config.getInt("nation");
		String owner = config.getString("owner");
		boolean national = config.getBoolean("national");
		boolean outpost = config.getBoolean("outpost");

		return new ChunkSpec(x, y, z, nation, owner, national, outpost);

	}

	public YamlConfiguration saveToConfig(YamlConfiguration config) {

		config.set("x", x);
		config.set("y", y);
		config.set("z", z);
		config.set("nation", nation);
		config.set("owner", owner);
		config.set("national", national);
		config.set("outpost", outpost);

		return config;

	}

}
