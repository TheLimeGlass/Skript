package org.skriptlang.skript.lang.comparator;

import org.jetbrains.annotations.NotNull;

public class Priority implements Comparable<Priority> {

	/**
	 * The default {@link Priority}.
	 */
	public static final Priority DEFAULT_PRIORITY = new Priority(1000);

	private int priority;

	public Priority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public void increment() {
		priority++;
	}

	public void decrement() {
		priority--;
	}

	@Override
	public int compareTo(@NotNull Priority other) {
		return Integer.compare(this.priority, other.priority);
	}

}
