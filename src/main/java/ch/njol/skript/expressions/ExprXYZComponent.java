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
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;
import org.joml.Quaternionf;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Changer.ChangerUtils;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

/**
 * Ported by Sashie from skript-vectors with bi0qaw's permission.
 * @author bi0qaw
 */
@Name("Vector/Quaternion - XYZ Component")
@Description({
	"Gets or changes the x, y or z component of <a href='classes.html#vector'>vectors</a>/<a href='classes.html#quaternion'>quaternions</a>.",
	"You cannot use w of vector. W is for quaternions only."
})
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
@Since("2.2-dev28, INSERT VERSION (Quaternions)")
public class ExprXYZComponent extends SimplePropertyExpression<Object, Number> {

	static {
		String types = "vectors";
		if (Skript.isRunningMinecraft(1, 19, 4))
			types += "/quaternions";
		register(ExprXYZComponent.class, Number.class, "[vector|quaternion] (0¦w|1¦x|2¦y|3¦z) [component[s]]", types);
	}

	private final static Character[] axes = new Character[] {'w', 'x', 'y', 'z'};

	private int axis;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		axis = parseResult.mark;
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Number convert(Object object) {
		if (object instanceof Vector) {
			if (axis == 0)
				return null;
			Vector vector = (Vector) object;
			return axis == 1 ? vector.getX() : (axis == 2 ? vector.getY() : vector.getZ());
		} else {
			Quaternionf quaternion = (Quaternionf) object;
			switch (axis) {
				case 0:
					return quaternion.w();
				case 1:
					return quaternion.x();
				case 2:
					return quaternion.y();
				case 3:
					return quaternion.z();
				default:
					return null;
			}
		}
	}

	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (getExpr().getReturnType().equals(Quaternionf.class)) {
			if ((mode == ChangeMode.SET || mode == ChangeMode.ADD || mode == ChangeMode.REMOVE))
				return new Class[] {Number.class};
		}
		if ((mode == ChangeMode.ADD || mode == ChangeMode.REMOVE || mode == ChangeMode.SET)
				&& getExpr().isSingle() && Changer.ChangerUtils.acceptsChange(getExpr(), ChangeMode.SET, Vector.class))
			return CollectionUtils.array(Number.class);
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		assert delta != null;
		for (Object object : getExpr().getArray(event)) {
			if (object instanceof Vector) {
				if (axis == 0)
					return;
				Vector vector = (Vector) object;
				double value = ((Number) delta[0]).doubleValue();
				switch (mode) {
					case REMOVE:
						value = -value;
						//$FALL-THROUGH$
					case ADD:
						if (axis == 1) {
							vector.setX(vector.getX() + value);
						} else if (axis == 2) {
							vector.setY(vector.getY() + value);
						} else {
							vector.setZ(vector.getZ() + value);
						}
						getExpr().change(event, new Vector[] {vector}, ChangeMode.SET);
						break;
					case SET:
						if (axis == 1) {
							vector.setX(value);
						} else if (axis == 2) {
							vector.setY(value);
						} else {
							vector.setZ(value);
						}
						getExpr().change(event, new Vector[] {vector}, ChangeMode.SET);
						break;
					default:
						assert false;
				}
			} else {
				float value = ((Number) delta[0]).floatValue();
				Quaternionf quaternion = (Quaternionf) object;
				switch (mode) {
					case REMOVE:
						value = -value;
						//$FALL-THROUGH$
					case ADD:
						if (axis == 0) {
							quaternion.set(quaternion.w() + value, quaternion.x(), quaternion.y(), quaternion.z());
						} else if (axis == 1) {
							quaternion.set(quaternion.w(), quaternion.x() + value, quaternion.y(), quaternion.z());
						} else if (axis == 2) {
							quaternion.set(quaternion.w(), quaternion.x(), quaternion.y() + value, quaternion.z());
						} else if (axis == 3) {
							quaternion.set(quaternion.w(), quaternion.x(), quaternion.y(), quaternion.z() + value);
						}
						if (ChangerUtils.acceptsChange(getExpr(), ChangeMode.SET, Quaternionf.class))
							getExpr().change(event, new Quaternionf[] {quaternion}, ChangeMode.SET);
						break;
					case SET:
						if (axis == 0) {
							quaternion.set(value, quaternion.x(), quaternion.y(), quaternion.z());
						} else if (axis == 1) {
							quaternion.set(quaternion.w(), value, quaternion.y(), quaternion.z());
						} else if (axis == 2) {
							quaternion.set(quaternion.w(), quaternion.x(), value, quaternion.z());
						} else if (axis == 3) {
							quaternion.set(quaternion.w(), quaternion.x(), quaternion.y(), value);
						}
						if (ChangerUtils.acceptsChange(getExpr(), ChangeMode.SET, Quaternionf.class))
							getExpr().change(event, new Quaternionf[] {quaternion}, ChangeMode.SET);
						break;
					case DELETE:
					case REMOVE_ALL:
					case RESET:
						assert false;
				}
			}
		}
	}

	@Override
	public Class<Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected String getPropertyName() {
		return axes[axis] + " component";
	}

}
