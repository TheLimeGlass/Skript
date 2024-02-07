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
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.entity;

import org.bukkit.entity.TNTPrimed;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class TNTData extends EntityData<TNTPrimed> {

	static {
		EntityData.register(TNTData.class, "tnt", TNTPrimed.class, "tnt");
	}

	@Nullable
	private Integer fuseTime;

	@Override
	@SuppressWarnings("unchecked")
	protected boolean init(Literal<?>[] exprs, int matchedPattern, ParseResult parseResult) {
		if (exprs.length > 0 && exprs[0] != null)
			fuseTime = ((Literal<Integer>) exprs[0]).getSingle();
		return true;
	}

	@Override
	protected boolean init(@Nullable Class<? extends TNTPrimed> tntClass, @Nullable TNTPrimed tnt) {
		fuseTime = (fuseTime == null) ? null : tnt.getFuseTicks();
		return true;
	}

	@Override
	public void set(TNTPrimed tnt) {
		if (fuseTime != null)
			tnt.setFuseTicks(fuseTime);
	}

	@Override
	protected boolean match(TNTPrimed tnt) {
		return fuseTime == null || tnt.getFuseTicks() == fuseTime;
	}

	@Override
	public Class<? extends TNTPrimed> getType() {
		return TNTPrimed.class;
	}

	@Override
	public EntityData getSuperType() {
		return new TNTData();
	}

	@Override
	protected int hashCode_i() {
		return fuseTime != null ? fuseTime.hashCode() : 0;
	}

	@Override
	protected boolean equals_i(EntityData<?> data) {
		return data instanceof TNTData ? fuseTime == ((TNTData) data).fuseTime : false;
	}

	@Override
	public boolean isSupertypeOf(EntityData<?> data) {
		return data instanceof TNTData ? fuseTime == null || fuseTime == ((TNTData) data).fuseTime : false;
	}

}
