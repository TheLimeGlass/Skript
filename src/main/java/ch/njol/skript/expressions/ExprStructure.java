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
package ch.njol.skript.expressions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Structure Between/Name")
@Description({
	"A structure is a utility that allows you to save a cuboid of blocks and entities.",
	"This syntax will return an existing structure from memory/datapacks or you can also create a structure between two locations.",
	"The register tag adds the structure to the 'all structures' expression.",
	"If the name contains a collon, it'll grab from the Minecraft structure space (Data packs included for namespaces).",
})
@Examples({
	"set {_structure} to a new structure between {location1} and {location2} named \"Example\"",
	"set {_structure} to structure \"Example\"",
	"set {_structure} to structure \"minecraft:end_city\""
})
@RequiredPlugins("Minecraft 1.17.1+")
@Since("INSERT VERSION")
public class ExprStructure extends SimpleExpression<Structure> {

	static {
		if (Skript.classExists("org.bukkit.structure.Structure"))
			Skript.registerExpression(ExprStructure.class, Structure.class, ExpressionType.COMBINED,
					"structure[s] [named] %strings% [and register:register]",
					"[a] [new] structure between %location% (and|to) %location% [(including|with) entities:entities]"
			);
	}

	@Nullable
	private Expression<Location> location1, location2;

	@Nullable
	private Expression<String> names;
	private boolean entities, register;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 0) {
			names = (Expression<String>) exprs[0];
			return true;
		}
		entities = parseResult.hasTag("entities");
		register = parseResult.hasTag("register");
		location1 = (Expression<Location>) exprs[0];
		location2 = (Expression<Location>) exprs[1];
		return true;
	}

	@Override
	protected Structure[] get(Event event) {
		StructureManager manager = Bukkit.getStructureManager();

		// Returning existing structures.
		if (names != null) {
			return names.stream(event)
					.map(name -> Utils.getNamespacedKey(name))
					.map(name -> name != null ? manager.loadStructure(name, register) : null)
					.toArray(Structure[]::new);
		}
		Location location1 = this.location1.getSingle(event);
		Location location2 = this.location2.getSingle(event);
		if (location1 == null || location2 == null)
			return new Structure[0];

		Structure structure = manager.createStructure();
		structure.fill(location1, location2, entities);
		return CollectionUtils.array(structure);
	}

	@Override
	public boolean isSingle() {
		return names == null || names.isSingle();
	}

	@Override
	public Class<? extends Structure> getReturnType() {
		return Structure.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (location1 == null || location2 == null)
			return "structures " + names.toString(event, debug);
		return "structure from " + location1.toString(event, debug) + " to " + location2.toString(event, debug);
	}

}
