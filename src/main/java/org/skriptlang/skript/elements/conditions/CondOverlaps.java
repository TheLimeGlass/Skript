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
package org.skriptlang.skript.elements.conditions;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Overlaps")
@Description("Checks whether boundingboxes overlaps with other boundingboxes.")
@Examples("if bounding box of target entity overlaps with {_boundingBox}")
@Since("INSERT VERSION")
public class CondOverlaps extends Condition {

	static {
		Skript.registerCondition(CondOverlaps.class,
				"%boundingboxes% do[es] overlap %boundingboxes%",
				"%boundingboxes% is overlapping %boundingboxes%",
				"%boundingboxes% overlaps [with] %boundingboxes%",

				"%boundingboxes% (doesn't|does not|do not|don't) overlap %boundingboxes%",
				"%boundingboxes% (isn't|is not|aren't|are not) overlapping %boundingboxes%"
		);
	}

	private Expression<BoundingBox> boundingBox;
	private Expression<BoundingBox> other;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		boundingBox = (Expression<BoundingBox>) exprs[0];
		other = (Expression<BoundingBox>) exprs[1];
		setNegated(matchedPattern > 2);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return boundingBox.check(event, boundingBox -> other.check(event, other -> boundingBox.overlaps(other)), isNegated());
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (boundingBox.isSingle()) {
			return boundingBox.toString(event, debug) + (isNegated() ? " doesn't overlap " : " does overlap ") + other.toString(event, debug);
		} else {
			return boundingBox.toString(event, debug) + (isNegated() ? " don't overlap " : " overlaps ") + other.toString(event, debug);
		}
	}

}
