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
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;

@Name("Expand")
@Description("Expands bounding boxes in the specified direction.")
@Examples("expand {_boundingBox} by vector(10, 0, 10)")
@Since("INSERT VERSION")
public class EffExpand extends Effect {

	static {
		Skript.registerEffect(EffExpand.class, "expand [bounding box[es]] %boundingboxes% [by] %direction/vector/number%");
	}

	private Expression<BoundingBox> boxes;
	private Expression<?> direction;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		boxes = (Expression<BoundingBox>) exprs[0];
		direction = LiteralUtils.defendExpression(exprs[1]);
		return LiteralUtils.canInitSafely(direction);
	}

	@Override
	protected void execute(Event event) {
		Object object = direction.getSingle(event);
		if (object == null)
			return;

		BoundingBox[] boxes = this.boxes.getArray(event);
		BoundingBox[] expanded = new BoundingBox[boxes.length];
		for (int i = 0; i < boxes.length; i++) {
			if (object instanceof Direction) {
				expanded[i] = boxes[i].expand(((Direction) object).getDirection());
			} else if (object instanceof Number) {
				expanded[i] = boxes[i].expand(((Number) object).doubleValue());
			} else if (object instanceof Vector) {
				expanded[i] = boxes[i].expand((Vector) object);
			}
		}
		assert boxes.length == expanded.length;
		this.boxes.change(event, boxes, ChangeMode.SET);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "expand " + boxes.toString(event, debug) + " " + direction.toString(event, debug);
	}

}
