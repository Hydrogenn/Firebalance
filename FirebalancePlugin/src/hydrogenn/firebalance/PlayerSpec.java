
package hydrogenn.firebalance;

public class PlayerSpec {

	private String name;
	private byte nation;
	private int king;
	public int credits;
	boolean online;

	public PlayerSpec(String name, byte nation, int king, int credits, boolean online) {
		this.setName(name);
		this.setNation(nation);
		this.setKing(king);
		this.credits = credits;
		this.online = online;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getNation() {
		return nation;
	}

	public void setNation(byte nation) {
		this.nation = nation;
	}

	public int getKing() {
		return king;
	}

	public void setKing(int king) {
		this.king = king;
	}

	public boolean getOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
}
