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
package org.skriptlang.skript.elements.expressions;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

@Name("Bounding Box")
@Description("The bounding box of entities/blocks.")
@Examples("if player is within bounding box of {_displayEntity}")
@Since("INSERT VERSION")
public class ExprBoundingBox extends SimplePropertyExpression<Object, BoundingBox> {

	static {
		register(ExprBoundingBox.class, BoundingBox.class, "bounding box[es]", "entities/blocks");
	}

	@Nullable
	@Override
	public BoundingBox convert(Object object) {
		if (object instanceof Block) {
			return ((Block) object).getBoundingBox();
		} else {
			return ((Entity) object).getBoundingBox();
		}
	}

	@Override
	public Class<? extends BoundingBox> getReturnType() {
		return BoundingBox.class;
	}

	@Override
	protected String getPropertyName() {
		return "bounding box";
	}

}
