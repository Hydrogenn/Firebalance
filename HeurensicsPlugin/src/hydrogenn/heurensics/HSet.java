package hydrogenn.heurensics;

public class HSet {
	
	private HID hid;
	private LogType logType;
	

	public HSet(HID hid, LogType logType) {
		this.hid = hid;
		this.logType = logType;
	}
	
	public HID getHid() {
		return hid;
	}

	public void setHid(HID hid) {
		this.hid = hid;
	}

	public LogType getLogType() {
		return logType;
	}

	public void setLogType(LogType logType) {
		this.logType = logType;
	}
}
