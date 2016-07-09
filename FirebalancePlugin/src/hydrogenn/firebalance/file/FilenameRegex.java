package hydrogenn.firebalance.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/* 
 * FilenameRegex.java
 * Made by Rayzr522
 * Date: Jul 9, 2016
 */
public class FilenameRegex implements FilenameFilter {

	private Pattern pattern;

	public FilenameRegex(String regex) {
		this.pattern = Pattern.compile(regex);
	}

	@Override
	public boolean accept(File dir, String name) {
		return pattern.matcher(name).matches();
	}

	/**
	 * @return the regex currently used for matching file names
	 */
	public String getRegex() {
		return pattern.pattern();
	}

	/**
	 * @param regex
	 *            the regex to use for matching file names
	 */
	public void setRegex(String regex) {
		this.pattern = Pattern.compile(regex);
	}

}
