package hydrogenn.heurensics;

public enum LogType {
	BLOCK_PLACE("block"),
	BLOCK_DESTROY("piece of rubble"),
	BLOCK_INTERACT("fingerprint"),
	PLAYER_HURT("bloodstain"),
	PLAYER_MOVE("footprint"),
	PLAYER_DEATH("skull");


	public final String source;
	public double probability;
	LogType(String source) {
		this.source = source;
	}
}
