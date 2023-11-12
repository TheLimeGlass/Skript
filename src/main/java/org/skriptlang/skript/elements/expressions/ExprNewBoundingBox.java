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
package org.skriptlang.skript.elements.expressions;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.BoundingBox;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("New Bounding Box")
@Description("Creates a new bounding box between two locations.")
@Examples("set {_box} to a new bounding box between location(0, 10, 0) and location(10, 20, 10)")
@Since("INSERT VERSION")
public class ExprNewBoundingBox extends SimpleExpression<BoundingBox> {

	static {
		Skript.registerExpression(ExprNewBoundingBox.class, BoundingBox.class, ExpressionType.COMBINED, "[a] [new] bounding box within %location% (and|to) %location%");
	}

	private Expression<Location> location1;
	private Expression<Location> location2;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		location1 = (Expression<Location>) exprs[0];
		location2 = (Expression<Location>) exprs[1];
		return true;
	}

	@Override
	@Nullable
	protected BoundingBox[] get(Event event) {
		Location location1 = this.location1.getSingle(event);
		if (location1 == null)
			return new BoundingBox[0];
		Location location2 = this.location2.getSingle(event);
		if (location2 == null)
			return new BoundingBox[0];
		return CollectionUtils.array(BoundingBox.of(location1, location2));
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends BoundingBox> getReturnType() {
		return BoundingBox.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "bounding box within " + location1.toString(event, debug) + " to " + location2.toString(event, debug);
	}

}
