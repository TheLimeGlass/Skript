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
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.entity;

import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.variables.Variables;

public class RabbitData extends EntityData<Rabbit> {
	
	static {
		if (Skript.classExists("org.bukkit.entity.Rabbit")) {
			EntityData.register(RabbitData.class, "rabbit", Rabbit.class, 0, "rabbit", "black rabbit", "black and white rabbit",
					"brown rabbit", "gold rabbit", "salt and pepper rabbit", "killer rabbit", "white rabbit");
			Variables.yggdrasil.registerSingleClass(Type.class, "Rabbit.Type");
		}
	}
	
	@Nullable
	private Type type;
	
	public RabbitData() {}
	
	public RabbitData(@Nullable Type type) {
		this.type = type;
	}

	@Override
	protected boolean init(Literal<?>[] exprs, int matchedPattern, ParseResult parseResult) {
		switch (matchedPattern) {
			case 0:
				break;
			case 1:
				type = Type.BLACK;
				break;
			case 2:
				type = Type.BLACK_AND_WHITE;
				break;
			case 3:
				type = Type.BROWN;
				break;
			case 4:
				type = Type.GOLD;
				break;
			case 5:
				type = Type.SALT_AND_PEPPER;
				break;
			case 6:
				type = Type.THE_KILLER_BUNNY;
				break;
			case 7:
				type = Type.WHITE;
				break;
		}
		return true;
	}

	@Override
	protected boolean init(@Nullable Class<? extends Rabbit> c, @Nullable Rabbit rabbit) {
		if (rabbit != null)
			type = rabbit.getRabbitType();
		return true;
	}
	
	@Override
	protected boolean match(Rabbit entity) {
		return (type == null || type == entity.getRabbitType());
	}
	
	@Override
	public EntityData getSuperType() {
		return new RabbitData(type);
	}

	@Override
	public void set(Rabbit entity) {
		if (type != null)
			entity.setRabbitType(type);
	}
	
	@Override
	public boolean isSupertypeOf(EntityData<?> data) {
		return equals_i(data);
	}

	@Override
	public Class<? extends Rabbit> getType() {
		return Rabbit.class;
	}

	@Override
	protected int hashCode_i() {
		int prime = 31;
		int result = 1;
		result = prime * result + (type != null ? type.hashCode() : 0);
		return result;
	}

	@Override
	protected boolean equals_i(final EntityData<?> obj) {
		if (!(obj instanceof RabbitData))
			return false;
		RabbitData copy = (RabbitData) obj;
		if (type != copy.type)
			return false;
		return true;
	}

}