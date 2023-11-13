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
package org.skriptlang.skript.elements.boundingboxes;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

@Name("Bounding Box Expanded")
@Description("The bounding box of entities/blocks expanded in a direction.")
@Examples({
	"on player move:",
		"\tplayer is within bounding boxes of {displays::*} expanded by vector(1, 1, 1)",
		"\tmessage \"You are within the block displays.\"",
	"",
	"on arm swing:",
		"\texpand metadata \"visual-indicator\" of player 1 metres in the direction of player",
	"",
	"set {_expanded} to {_boundingBox} expanded by vector(1, 1, 1)"
})
@Since("INSERT VERSION")
public class ExprExpanded extends PropertyExpression<BoundingBox, BoundingBox> {

	static {
		Skript.registerExpression(ExprExpanded.class, BoundingBox.class, ExpressionType.COMBINED, "[bounding box[es]] %boundingboxes% expanded [by] %direction/vector/number%");
	}

	private Expression<?> direction;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends BoundingBox>) exprs[0]);
		direction = exprs[1];
		return true;
	}

	@Override
	protected BoundingBox[] get(Event event, BoundingBox[] source) {
		Object object = direction.getSingle(event);
		if (object == null)
			return new BoundingBox[0];

		return this.get(source, box -> {
			if (object instanceof Direction) {
				return box.expand(((Direction) object).getDirection());
			} else if (object instanceof Number) {
				return box.expand(((Number) object).doubleValue());
			} else if (object instanceof Vector) {
				return box.expand((Vector) object);
			}
			return null;
		});
	}

	@Override
	public Class<? extends BoundingBox> getReturnType() {
		return BoundingBox.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return getExpr().toString(event, debug) + " expanded " + direction.toString(event, debug);
	}

}
