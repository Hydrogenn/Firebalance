package com.rayzr522.battlebricks;

import java.util.ArrayList;
import java.util.List;

/* 
 * ListFilter.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
/**
 * 
 * ListFilters are an easy way to remove extraneous elements from a list.
 * 
 * @author Rayzr522
 *
 * @param <T>
 */
public abstract class ListFilter<T> {

	/**
	 * An abstract method which you can modify to use whatever logic you need to determine whether to keep an element or not.
	 * @param input = the input of type T that you must determine is worthy of keeping or not.
	 * @return Whether or not to keep <code>input</code> in the list.
	 */
	public abstract boolean keep(T input);
	
	/**
	 * Applies a given {@link ListFilter} to the specified {@link List}.
	 * @param list = the list to filter.
	 * @param filter = the filter to apply to the list.
	 * @return A filtered list of type T.
	 */
	public static <T> List<T> apply(List<T> list, ListFilter<T> filter) {

		List<T> output = new ArrayList<T>();

		for (T item : list) {

			if (filter.keep(item)) {

				output.add(item);

			}

		}

		return output;

	}

}
