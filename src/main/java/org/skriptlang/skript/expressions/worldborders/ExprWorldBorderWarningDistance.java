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

@Name("World Border Warning Distance")
@Description("Grabs the warning distance in blocks of a world border.")
@Since("INSERT VERSION")
public class ExprWorldBorderWarningDistance extends SimplePropertyExpression<WorldBorder, Integer> {

	static {
		register(ExprWorldBorderWarningDistance.class, Integer.class, "[[world] border] warning distance", "worldborders");
	}

	@Override
	@Nullable
	public Integer convert(WorldBorder border) {
		return border.getWarningDistance();
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
				int amount = (int) delta[0];
				for (WorldBorder border : getExpr().getArray(event)) {
					int existing = convert(border);
					border.setWarningDistance(existing + amount);
				}
				break;
			case RESET:
				for (WorldBorder border : getExpr().getArray(event))
					border.setWarningDistance(15);
				break;
			case DELETE:
				for (WorldBorder border : getExpr().getArray(event))
					border.setWarningDistance(0);
				break;
			case REMOVE:
			case REMOVE_ALL:
				if (delta == null)
					return;
				int remove = (int) delta[0];
				for (WorldBorder border : getExpr().getArray(event)) {
					int existing = convert(border);
					border.setWarningDistance(Math.max(0, existing - remove));
				}
				break;
			case SET:
				if (delta == null)
					return;
				int set = Math.max(0, (int) delta[0]);
				for (WorldBorder border : getExpr().getArray(event))
					border.setWarningDistance(set);
				break;
			default:
				break;
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "warning distance";
	}

}
