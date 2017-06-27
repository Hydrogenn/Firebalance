
package hydrogenn.kingdoms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;

public class SchedulerCache {

	private int id;
	private String callerName;
	private String type;
	private Long taskEnd;
	public static List<SchedulerCache> list = new ArrayList<>();

	public SchedulerCache(int id, String callerName, String type, Long taskEnd) {
		this.setId(id);
		this.setCallerName(callerName);
		this.setType(type);
		this.setTaskEnd(taskEnd);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCallerName() {
		return callerName;
	}

	public void setCallerName(String callerName) {
		this.callerName = callerName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTaskEnd() {
		return taskEnd;
	}

	public void setTaskEnd(Long taskEnd) {
		this.taskEnd = taskEnd;
	}

	public static Long getRemainingTaskTicks(String functionName, String callerName) {
		for (SchedulerCache s : list) {
			if (functionName == null || s.getType().equals(functionName)) {
				if (callerName == null || s.getCallerName().equals(callerName)) { return s.getTaskEnd() - System.currentTimeMillis() / 50; }
			}
		}
		return null;
	}

	public static void addSyncScheduler(String functionName, String callerName, long delay, Runnable function) {
		final int id = Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Firebalance"), function, delay).getTaskId();
		list.add(new SchedulerCache(id, callerName, functionName, delay + System.currentTimeMillis()));
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {
	
			public void run() {
				for (Iterator<SchedulerCache> i = list.iterator(); i.hasNext();) {
					SchedulerCache s = i.next();
					if (s.getId() == id) {
						i.remove();
					}
				}
			}
		}, delay + 10);
	}

	public static void addCountedScheduler(String functionName, String callerName, long delay, final String message, Runnable function) {
		final int id = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), function, delay).getTaskId();
		list.add(new SchedulerCache(id, callerName, functionName, delay + System.currentTimeMillis() / 50));
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {
	
			public void run() {
				for (Iterator<SchedulerCache> i = list.iterator(); i.hasNext();) {
					SchedulerCache s = i.next();
					if (s.getId() == id) {
						i.remove();
					}
				}
			}
		}, delay + 10);
		int displayTime = 1;
		while (displayTime * 20 < delay) {
			final String display;
			if (displayTime % 3600 == 0) display = displayTime / 3600 + " hours";
			else if (displayTime % 60 == 0) display = displayTime / 60 + " minutes";
			else display = displayTime + " seconds";
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {
	
				public void run() {
					Bukkit.broadcastMessage(message + display);
				}
			}, delay - displayTime * 20);
			if (displayTime >= 3600) displayTime *= 2;
			switch (displayTime) {
			case 1:
				displayTime = 2;
				break;
			case 2:
				displayTime = 3;
				break;
			case 3:
				displayTime = 5;
				break;
			case 5:
				displayTime = 10;
				break;
			case 10:
				displayTime = 15;
				break;
			case 15:
				displayTime = 30;
				break;
			case 30:
				displayTime = 45;
				break;
			case 45:
				displayTime = 60;
				break;
			case 1 * 60:
				displayTime = 2 * 60;
				break;
			case 2 * 60:
				displayTime = 3 * 60;
				break;
			case 3 * 60:
				displayTime = 5 * 60;
				break;
			case 5 * 60:
				displayTime = 10 * 60;
				break;
			case 10 * 60:
				displayTime = 15 * 60;
				break;
			case 15 * 60:
				displayTime = 30 * 60;
				break;
			case 30 * 60:
				displayTime = 45 * 60;
				break;
			case 45 * 60:
				displayTime = 60 * 60;
				break;
			}
		}
	}

	public static void addScheduler(String functionName, String callerName, long delay, Runnable function) {
		final int id = Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), function, delay).getTaskId();
		list.add(new SchedulerCache(id, callerName, functionName, delay + System.currentTimeMillis()));
		Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("Firebalance"), new Runnable() {
	
			public void run() {
				for (Iterator<SchedulerCache> i = list.iterator(); i.hasNext();) {
					SchedulerCache s = i.next();
					if (s.getId() == id) {
						i.remove();
					}
				}
			}
		}, delay + 10);
	}
}
