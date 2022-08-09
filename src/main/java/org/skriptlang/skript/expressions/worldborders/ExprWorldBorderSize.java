package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

@Name("World Border Size")
@Description("Grabs the size of a world border.")
@Since("INSERT VERSION")
public class ExprWorldBorderSize extends SimplePropertyExpression<WorldBorder, Double> {

	static {
		register(ExprWorldBorderSize.class, Double.class, "[world] border (size|radius)", "worldborders");
	}

	@Override
	@Nullable
	public Double convert(WorldBorder border) {
		return border.getSize();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		return CollectionUtils.array(Number.class);
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		switch (mode) {
			case ADD:
				if (delta == null)
					return;
				double radius = (double) delta[0];
				for (WorldBorder border : getExpr().getArray(event)) {
					double existing = convert(border);
					border.setSize(Math.max(border.getMaxSize(), existing + radius));
				}
				break;
			case RESET:
			case DELETE:
				for (WorldBorder border : getExpr().getArray(event))
					border.setSize(border.getMaxSize());
				break;
			case REMOVE:
			case REMOVE_ALL:
				if (delta == null)
					return;
				double remove = (double) delta[0];
				for (WorldBorder border : getExpr().getArray(event)) {
					double existing = convert(border);
					border.setSize(Math.min(1.0D, existing - remove));
				}
				break;
			case SET:
				if (delta == null)
					return;
				double set = Math.max(0, (double) delta[0]);
				if (set < 1.0D)
					return;
				for (WorldBorder border : getExpr().getArray(event)) {
					if (set > border.getMaxSize())
						continue;
					border.setSize(set);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public Class<? extends Double> getReturnType() {
		return Double.class;
	}

	@Override
	protected String getPropertyName() {
		return "border size";
	}

}
