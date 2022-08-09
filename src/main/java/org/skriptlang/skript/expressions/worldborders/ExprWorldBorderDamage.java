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

@Name("World Border Damage")
@Description("Grabs the damage amount of a world border.")
@Since("INSERT VERSION")
public class ExprWorldBorderDamage extends SimplePropertyExpression<WorldBorder, Double> {

	static {
		register(ExprWorldBorderDamage.class, Double.class, "[world] border damage [amount]", "worldborders");
	}

	@Override
	@Nullable
	public Double convert(WorldBorder border) {
		return border.getDamageAmount();
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
				double amount = (double) delta[0];
				for (WorldBorder border : getExpr().getArray(event)) {
					double existing = convert(border);
					border.setDamageAmount(existing + amount);
				}
				break;
			case RESET:
				for (WorldBorder border : getExpr().getArray(event))
					border.setDamageAmount(0.2);
				break;
			case DELETE:
				for (WorldBorder border : getExpr().getArray(event))
					border.setDamageAmount(0);
				break;
			case REMOVE:
			case REMOVE_ALL:
				if (delta == null)
					return;
				double remove = (double) delta[0];
				for (WorldBorder border : getExpr().getArray(event)) {
					double existing = convert(border);
					border.setDamageAmount(Math.max(0, existing - remove));
				}
				break;
			case SET:
				if (delta == null)
					return;
				double set = Math.max(0, (double) delta[0]);
				for (WorldBorder border : getExpr().getArray(event))
					border.setDamageAmount(set);
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
		return "border damage amount";
	}

}
