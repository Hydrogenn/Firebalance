
package hydrogenn.firebalance;

public class SchedulerCache {

	public int id;
	public String callerName;
	public String type;
	public Long taskEnd;

	public SchedulerCache(int id, String callerName, String type, Long taskEnd) {
		this.id = id;
		this.callerName = callerName;
		this.type = type;
		this.taskEnd = taskEnd;
	}
}
