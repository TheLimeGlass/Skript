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
package ch.njol.skript.expressions;

import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("XYZ Component")
@Description("Gets or changes the x, y or z component of a vector or bounding boxes.")
@Examples({
	"set {_v} to vector 1, 2, 3",
	"send \"%x of {_v}%, %y of {_v}%, %z of {_v}%\"",
	"add 1 to x of {_v}",
	"add 2 to y of {_v}",
	"add 3 to z of {_v}",
	"send \"%x of {_v}%, %y of {_v}%, %z of {_v}%\"",
	"set x component of {_v} to 1",
	"set y component of {_v} to 2",
	"set z component of {_v} to 3",
	"send \"%x component of {_v}%, %y component of {_v}%, %z component of {_v}%\""
})
@Since("2.2-dev28, INSERT VERSION (bounding boxes)")
public class ExprXYZ extends SimplePropertyExpression<Object, Number> {

	static {
		register(ExprXYZ.class, Number.class, "([vector]|bounding box[es] (max[imum]|min:min[minimum]) [point]) (0¦x|1¦y|2¦z) [component[s]]", "vectors/boundingboxes");
	}

	private final static Character[] axes = new Character[] {'x', 'y', 'z'};
	private boolean minimum;
	private int axis;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		axis = parseResult.mark;
		minimum = parseResult.hasTag("minimum");
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Number convert(Object object) {
		if (object instanceof Vector) {
			Vector vector = (Vector) object;
			return axis == 0 ? vector.getX() : (axis == 1 ? vector.getY() : vector.getZ());
		} else {
			BoundingBox boundingBox = (BoundingBox) object;
			if (minimum)
				return axis == 0 ? boundingBox.getMinX() : (axis == 1 ? boundingBox.getMinY() : boundingBox.getMinZ());
			return axis == 0 ? boundingBox.getMaxX() : (axis == 1 ? boundingBox.getMaxY() : boundingBox.getMaxZ());
		}
	}

	@Override
	@SuppressWarnings("null")
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (getReturnType().isAssignableFrom(BoundingBox.class)) {
			Skript.error("A bounding box x/y/z cannot be set. Use the expand effect.");
			return null;
		}
		if ((mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET)
				&& getExpr().isSingle() && Changer.ChangerUtils.acceptsChange(getExpr(), ChangeMode.SET, Vector.class))
			return CollectionUtils.array(Number.class);
		return null;
	}
	
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		assert delta != null;
		Object object = getExpr().getSingle(e);
		if (object == null)
			return;
		Vector vector = (Vector) object;
		double n = ((Number) delta[0]).doubleValue();
		switch (mode) {
			case REMOVE:
				n = -n;
				//$FALL-THROUGH$
			case ADD:
				if (axis == 0)
					vector.setX(vector.getX() + n);
				else if (axis == 1)
					vector.setY(vector.getY() + n);
				else
					vector.setZ(vector.getZ() + n);
				getExpr().change(e, new Vector[] {vector}, ChangeMode.SET);
				break;
			case SET:
				if (axis == 0)
					vector.setX(n);
				else if (axis == 1)
					vector.setY(n);
				else
					vector.setZ(n);
				getExpr().change(e, new Vector[] {vector}, ChangeMode.SET);
			default:
				break;
		}
	}

	@Override
	protected String getPropertyName() {
		return axes[axis] + " component";
	}

	@Override
	public Class<Number> getReturnType() {
		return Number.class;
	}

}
