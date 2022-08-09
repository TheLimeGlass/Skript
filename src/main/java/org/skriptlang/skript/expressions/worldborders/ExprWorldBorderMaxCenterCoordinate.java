package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.WorldBorder;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("World Border Max Center Coordinate")
@Description("Grabs the absolute value of the maximum x/z center coordinate of a world border.")
@Since("INSERT VERSION")
public class ExprWorldBorderMaxCenterCoordinate extends SimplePropertyExpression<WorldBorder, Double> {

	static {
		register(ExprWorldBorderMaxCenterCoordinate.class, Double.class, "max[imum] center coordinate", "worldborders");
	}

	@Override
	@Nullable
	public Double convert(WorldBorder border) {
		return border.getMaxCenterCoordinate();
	}

	@Override
	public Class<? extends Double> getReturnType() {
		return Double.class;
	}

	@Override
	protected String getPropertyName() {
		return "max center coordinate";
	}

}
