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
package org.skriptlang.skript.test.tests.syntaxes;

import ch.njol.skript.test.runner.SkriptJUnitTest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

// TODO properly add test after merge of https://github.com/SkriptLang/Skript/pull/6261
public class InventoryMoveItemEventTest extends SkriptJUnitTest {

	static {
		setShutdownDelay(1);
	}

	@Test
	public void test() {
		Inventory chestInventory = Bukkit.createInventory(null, InventoryType.CHEST);
		ItemStack itemStack = new ItemStack(Material.STONE);
		Inventory hopperInventory = Bukkit.createInventory(null, InventoryType.HOPPER);
		Bukkit.getPluginManager().callEvent(new InventoryMoveItemEvent(chestInventory, itemStack, hopperInventory, false));
	}

}
