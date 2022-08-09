package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

@Name("Expand World Border")
@Description("Expands the world border over a set amount of time in seconds.")
@Since("INSERT VERSION")
public class EffWorldBorderSize extends Effect {

	static {
		Skript.registerEffect(EffWorldBorderSize.class, "(set|expand) %worldborders% [border['[s]]] (size|radius) [to be] %number% over %timespan%");
	}

	private Expression<WorldBorder> borders;
	private Expression<Timespan> timespan;
	private Expression<Number> size;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		borders = (Expression<WorldBorder>) expressions[0];
		size = (Expression<Number>) expressions[1];
		timespan = (Expression<Timespan>) expressions[2];
		return true;
	}

	@Override
	protected void execute(Event event) {
		double size = this.size.getSingle(event).doubleValue();
		Timespan timespan = this.timespan.getSingle(event);
		for (WorldBorder border : borders.getArray(event))
			border.setSize(size, timespan.getMilliSeconds() / 1000);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "expand " + borders.toString(event, debug) + " to be " + 
				size.toString(event, debug) + " over " + timespan.toString(event, debug) + " seconds";
	}

}
