package org.skriptlang.skript.expressions.worldborders;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;

@Name("World Border")
@Description("Grabs the world border of a world or a player. Player requires 1.19+. Can only set border of a player.")
@Since("INSERT VERSION")
public class ExprWorldBorder extends SimplePropertyExpression<Object, WorldBorder> {

	private static final boolean SUPPORTS_PLAYER = Skript.methodExists(Player.class, "getWorldBorder");

	static {
		if (SUPPORTS_PLAYER) {
			register(ExprWorldBorder.class, WorldBorder.class, "world border[s]", "worlds/players");
		} else {
			register(ExprWorldBorder.class, WorldBorder.class, "world border[s]", "worlds");
		}
	}

	@Override
	@Nullable
	public WorldBorder convert(Object object) {
		if (object instanceof Player) {
			if (!SUPPORTS_PLAYER)
				return null;
			return ((Player)object).getWorldBorder();
		}
		return ((World)object).getWorldBorder();
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode != ChangeMode.SET && mode != ChangeMode.RESET && mode != ChangeMode.DELETE)
			return null;
		return CollectionUtils.array(WorldBorder.class);
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		WorldBorder border = delta != null ? (WorldBorder) delta[0] : null;
		if (border == null && mode == ChangeMode.SET) {
			return;
		} else if (border == null) {
			for (Object object : getExpr().getArray(event)) {
				if (object instanceof Player) {
					((Player)object).getWorldBorder().reset();
				} else {
					((World)object).getWorldBorder().reset();
				}
			}
			return;
		}
		for (Object object : getExpr().getArray(event)) {
			if (object instanceof Player)
				((Player)object).setWorldBorder(border);
		}
	}

	@Override
	public Class<? extends WorldBorder> getReturnType() {
		return WorldBorder.class;
	}

	@Override
	protected String getPropertyName() {
		return "world border";
	}

}
