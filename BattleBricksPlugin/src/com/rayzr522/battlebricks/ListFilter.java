package com.rayzr522.battlebricks;

import java.util.ArrayList;
import java.util.List;

/* 
 * ListFilter.java
 * Made by Rayzr522
 * Date: Jul 11, 2016
 */
public abstract class ListFilter<T> {

	public abstract boolean keep(T input);

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
