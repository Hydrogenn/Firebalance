package hydrogenn.beacon.lib;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

public class TimeLib {

	//Created by Brent Nash on StackExchange. Modified.
	public static String breakdown(long millis) {
	    if (millis < 0) {
	      throw new IllegalArgumentException("Duration must be greater than zero!");
	    }

	    long days = TimeUnit.MILLISECONDS.toDays(millis);
	    if (days > 365 * 100) {
	    	return "A long time";
	    }
	    millis -= TimeUnit.DAYS.toMillis(days);
	    long hours = TimeUnit.MILLISECONDS.toHours(millis);
	    millis -= TimeUnit.HOURS.toMillis(hours);
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
	    millis -= TimeUnit.MINUTES.toMillis(minutes);
	    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

	    ArrayList<String> strings = new ArrayList<String>();
	    if (days != 0) {
	    	
	    	strings.add(days + " Day" + (days == 1 ? "" : "s"));
	    }
	    if (hours != 0) {
	    	strings.add(hours + " Hour" + (hours == 1 ? "" : "s"));
	    }
	    if (minutes != 0) {
	    	strings.add(minutes + " Minute" + (minutes == 1 ? "" : "s"));
	    }
	    if (seconds != 0) {
	    	strings.add(seconds + " Second" + (seconds == 1 ? "" : "s"));
	    }
	    while (strings.size() > 2) {
	    	strings.remove(strings.size() - 1);
	    }
	    return StringUtils.join(strings,", ");
	}
	
	public static String breakdownRelative(long start, long end) {
		return breakdown(end - start);
	}
	
	public static String breakdownFromNow(long millis) {
		return breakdownRelative(System.currentTimeMillis(), millis);
	}

}
