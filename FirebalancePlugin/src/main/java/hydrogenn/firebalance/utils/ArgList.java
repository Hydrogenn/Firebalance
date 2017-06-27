package hydrogenn.firebalance.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgList {

	private List<String> args = new ArrayList<String>();
	private boolean caseMode = false;

	public ArgList caseSpecific(boolean mode) {
		this.caseMode = mode;
		return this;
	}

	public ArgList add(String... arg) {
		args.addAll(Arrays.asList(arg));
		return this;
	}

	public boolean isValid(String arg) {

		if (!validString(arg)) {
			return false;
		}

		for (String possible : args) {
			if (caseMode ? arg.equals(possible) : arg.equalsIgnoreCase(possible)) {
				return true;
			}
		}

		return false;

	}

	private boolean validString(String str) {
		return (str != null && str.trim().length() > 0);
	}

}
