package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Location Is Inside World Border")
@Description("If the location(s) are within the world border boundries.")
@Since("INSERT VERSION")
public class CondInsideWorldBorder extends Condition {

	static {
		PropertyCondition.register(CondInsideWorldBorder.class, PropertyType.BE, "(inside|within) %worldborders%", "locations");
	}

	private Expression<WorldBorder> borders;
	private Expression<Location> locations;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		locations = (Expression<Location>) expressions[0];
		borders = (Expression<WorldBorder>) expressions[1];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return locations.check(event, location -> borders.check(event, border -> border.isInside(location)));
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return locations.toString(event, debug) + (locations.isSingle() ? " is " : " are ") + (isNegated() ? "not " : "") +
				"inside of " + borders.toString(event, debug);
	}

}
