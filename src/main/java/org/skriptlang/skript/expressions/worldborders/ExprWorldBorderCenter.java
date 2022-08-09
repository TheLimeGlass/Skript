package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

@Name("World Border Center")
@Description("Grabs the center location of a world border.")
@Since("INSERT VERSION")
public class ExprWorldBorderCenter extends SimplePropertyExpression<WorldBorder, Location> {

	static {
		register(ExprWorldBorderCenter.class, Location.class, "center [location[s]]", "worldborders");
	}

	@Override
	@Nullable
	public Location convert(WorldBorder border) {
		return border.getCenter();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode != ChangeMode.SET)
			return null;
		return CollectionUtils.array(Location.class);
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null)
			return;
		Location location = (Location) delta[0];
		for (WorldBorder border : getExpr().getArray(event))
			border.setCenter(location);
	}

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	protected String getPropertyName() {
		return "center location";
	}

}
