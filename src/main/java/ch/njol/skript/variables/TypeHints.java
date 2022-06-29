/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.variables;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import ch.njol.util.coll.CollectionUtils;

/**
 * This is used to manage variable type hints.
 * 
 * <ul>
 * <li>EffChange adds then when local variables are set
 * <li>Variable checks them when parser tries to create it
 * <li>ScriptLoader clears hints after each section has been parsed
 * <li>ScriptLoader enters and exists scopes as needed
 * <li>Variable uses this class aswell for runtime cache. Adds and clears.
 * </ul>
 */
public class TypeHints {

	private static final Deque<Map<String, Class<?>[]>> variableNameHints = new ArrayDeque<>();
	private static final Deque<Map<String, Class<?>>> returnTypeHints = new ArrayDeque<>();

	public enum HintContext {
		RETURN_TYPE,
		VARIABLE_STRING;
	}

	static {
		// Initialize type hints
		clear(HintContext.VARIABLE_STRING);
		clear(HintContext.RETURN_TYPE);
	}

	@SuppressWarnings("unchecked")
	public static void add(HintContext context, String variable, Class<?>... hints) {
		if (hints == null || hints.length <= 0)
			return;
		if (context == HintContext.VARIABLE_STRING) {
			if (CollectionUtils.containsAll(hints, Object.class)) // Ignore useless type hint
				return;
			
			// Take top of stack, without removing it
			Map<String, Class<?>[]> head = variableNameHints.getFirst();
			head.put(variable, hints);
		} else {
			Class<?> hint = hints[0];
			if (hint.equals(Object.class)) // Ignore useless type hint
				return;

			Map<String, Class<?>> head = returnTypeHints.getFirst();
			head.put(variable, hint);
		}
	}

	/**
	 * Returns the type hints of a variable, can be either return type or variable string of a variable.
	 * Can be null if no type hint was saved.
	 * 
	 * @param variable
	 * @return
	 */
	@Nullable
	public static Class<?>[] get(HintContext context, String variable) {
		// Go through stack of hints for different scopes
		if (context == HintContext.VARIABLE_STRING) {
			for (Map<String, Class<?>[]> map : variableNameHints) {
				Class<?>[] hints = map.get(variable);
				if (hints != null && hints.length > 0) // Found in this scope
					return hints;
			}
			return null;
		} else {
			for (Map<String, Class<?>> map : returnTypeHints) {
				Class<?> hint = map.get(variable);
				if (hint != null) // Found in this scope
					return CollectionUtils.array(hint);
			}
			return null;
		}
	}

	public static Class<?>[] getForVariableString(String variable) {
		for (Map<String, Class<?>[]> map : variableNameHints) {
			Class<?>[] hints = map.get(variable);
			if (hints != null && hints.length > 0) // Found in this scope
				return hints;
		}
		return null;
	}

	public static Class<?> getForReturnType(String variable) {
		for (Map<String, Class<?>> map : returnTypeHints) {
			Class<?> hint = map.get(variable);
			if (hint != null) // Found in this scope
				return hint;
		}
		return null;
	}

	public static void enterScope(HintContext context) {
		if (context == HintContext.VARIABLE_STRING)
			variableNameHints.push(new HashMap<>());
		else
			returnTypeHints.push(new HashMap<>());
	}

	public static void exitScope(HintContext context) {
		if (context == HintContext.VARIABLE_STRING)
			variableNameHints.pop();
		else
			returnTypeHints.pop();
	}

	public static void clear(HintContext context) {
		if (context == HintContext.VARIABLE_STRING)
			variableNameHints.clear();
		else
			returnTypeHints.clear();
		enterScope(context);
	}

}
