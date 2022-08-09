package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.WorldBorder;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("World Border Max Size")
@Description("Grabs the max size of a world border.")
@Since("INSERT VERSION")
public class ExprWorldBorderMaxSize extends SimplePropertyExpression<WorldBorder, Double> {

	static {
		register(ExprWorldBorderMaxSize.class, Double.class, "max[imum] size", "worldborders");
	}

	@Override
	@Nullable
	public Double convert(WorldBorder border) {
		return border.getMaxSize();
	}

	@Override
	public Class<? extends Double> getReturnType() {
		return Double.class;
	}

	@Override
	protected String getPropertyName() {
		return "max size";
	}

}
