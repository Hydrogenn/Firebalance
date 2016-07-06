
package hydrogenn.firebalance;

public class SchedulerCache {

	int		id;
	String	callerName;
	String	type;
	Long	taskEnd;

	public SchedulerCache(int id, String callerName, String type, Long taskEnd) {
		this.id = id;
		this.callerName = callerName;
		this.type = type;
		this.taskEnd = taskEnd;
	}
}
