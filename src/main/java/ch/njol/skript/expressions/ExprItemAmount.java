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

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

@Name("Item Amount")
@Description("The amount of an <a href='classes.html#itemstack'>item stack</a>.")
@Examples({"send \"You have got %item amount of player's tool% %player's tool% in your hand!\" to player",
		"send \"You have got %item amount of stone in player's inventory% stone in your inventory!\" to player"})
@Since("2.2-dev24, INSERT VERSION (ItemType in Slots/Inventories)")
public class ExprItemAmount extends SimpleExpression<Integer> {

	static {
		Skript.registerExpression(ExprItemAmount.class, Integer.class, ExpressionType.PROPERTY, "item[[ ]stack] (amount|size|number) [of %slots/itemtypes%]", "%slots/itemtypes%'[s] item[[ ]stack] (amount|size|number)");
		Skript.registerExpression(ExprItemAmount.class, Integer.class, ExpressionType.COMBINED, "item[[ ]stack] (amount|size|number) of %itemtypes% [with]in %slots/inventories%");
	}

	@Nullable
	private Expression<ItemType> itemtypes;
	private Expression<Object> objects;
	private int pattern;

	private final Converter<Object, Integer> amountConverter = new Converter<>() {
		@Override
		public Integer convert(Object object) {
			if (object instanceof ItemType)
				return ((ItemType) object).getAmount();
			assert object instanceof Slot;
			return ((Slot) object).getAmount();
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		pattern = matchedPattern;
		if (matchedPattern == 0) {
			objects = (Expression<Object>) exprs[0];
			return true;
		}
		itemtypes = (Expression<ItemType>) exprs[0];
		objects = (Expression<Object>) exprs[1];
		return true;
	}

	@Override
	protected @Nullable Integer[] get(Event event) {
		return objects.get(event, pattern == 0 ? amountConverter : new Converter<>() {
			@Override
			public Integer convert(Object object) {
				if (itemtypes == null)
					return null;
				if (object instanceof Inventory) {
					Inventory inventory = (Inventory) object;
					return itemtypes.stream(event)
							.flatMap(type -> inventory.all(type.getMaterial()).values().stream())
							.mapToInt(itemstack -> itemstack.getAmount())
							.sum();
				}
				assert object instanceof Slot;
				Slot slot = (Slot) object;
				if (!itemtypes.stream(event).anyMatch(item -> item.isOfType(slot.getItem())))
					return null;
				return slot.getAmount();
			}
		});
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		if (event == null)
			return pattern == 0 ? "item amount" : "item amount of type";
		return pattern == 0 ? "item amount of " + objects.toString(event, debug) : "item amount of " + itemtypes.toString(event, debug) + " in " + objects.toString(event, debug);
	}

	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		if (pattern != 0 || mode == ChangeMode.REMOVE_ALL)
			return null;
		return CollectionUtils.array(Number.class);
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
		int amount = delta != null ? ((Number) delta[0]).intValue() : 0;
		switch (mode) {
			case ADD:
				for (Object obj : objects.getArray(event))
					if (obj instanceof ItemType) {
						ItemType item = ((ItemType) obj);
						item.setAmount(item.getAmount() + amount);
					} else {
						Slot slot = ((Slot) obj);
						slot.setAmount(slot.getAmount() + amount);
					}
				break;
			case SET:
				for (Object obj : objects.getArray(event))
					if (obj instanceof ItemType)
						((ItemType) obj).setAmount(amount);
					else
						((Slot) obj).setAmount(amount);
				break;
			case REMOVE:
				for (Object obj : objects.getArray(event))
					if (obj instanceof ItemType) {
						ItemType item = ((ItemType) obj);
						item.setAmount(item.getAmount() - amount);
					} else {
						Slot slot = ((Slot) obj);
						slot.setAmount(slot.getAmount() - amount);
					}
				break;
			case REMOVE_ALL:
			case RESET:
			case DELETE:
				for (Object obj : objects.getArray(event))
					if (obj instanceof ItemType)
						((ItemType) obj).setAmount(1);
					else
						((Slot) obj).setAmount(1);
				break;
		}
	}

}
