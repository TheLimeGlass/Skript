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

import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.profile.PlayerTextures.SkinModel;
import org.eclipse.jdt.annotation.Nullable;

import com.destroystokyo.paper.profile.PlayerProfile;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.EnumClassInfo;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.registrations.Classes;

@Name("Player Cape")
@Description("Returns or sets the cape of players. URL must be on mojang textures service.")
@Since("1.0")
public class ExprPlayerCape extends SimplePropertyExpression<Player, String> {

	static {
		register(ExprPlayerCape.class, String.class, "cape", "players");
		// Place this class register enum in another class.
		Classes.registerClass(new EnumClassInfo<>(Cape.class, "cape", "capes") // in default.lang the type would be capes:
				.user("capes?")
				.name("Cape")
				.description("Represents a preset cape URL for player skins.")
				.since("1.0"));
	}

	// Simple URL enum. Can be placed in another class.
	public static enum Cape {
		RESET(null),
		Minecon_2016("http://textures.minecraft.net/texture/e7dfea16dc83c97df01a12fabbd1216359c0cd0ea42f9999b6e97c584963e980");

		private URL url;

		Cape(String url) {
			try {
				if (url != null)
					this.url = new URL(url);
			} catch (MalformedURLException ignored) {}
		}

		@Nullable
		public URL getURL() {
			return url;
		}

	}

	@Override
	@Nullable
	public String convert(Player player) {
		return player.getPlayerProfile().getTextures().getCape() + "";
	}

	@Override
	public Class<String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "cape";
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET)
			return new Class[] {Cape.class, String.class}; // Accepts multiple types. "http://textures.minecraft.net/texture/e7dfea16dc83c97df01a12fabbd1216359c0cd0ea42f9999b6e97c584963e980" or minecon 2016
		return null;
	}

	@Override
	public void change(Event event, @Nullable Object[] delta, ChangeMode mode) throws UnsupportedOperationException {
		URL url = null;
		Object object = delta == null ? null : delta[0];
		// Handle the multiple types either Cape.class or String.class
		if (object != null) {
			if (object instanceof String) {
				try {
					url = new URL((String) object);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else if (object instanceof Cape) {
				url = ((Cape) object).getURL();
			}
		}
		if (url == null && mode == ChangeMode.SET) // Their provided URL was invalid.
			return;
		for (Player player : getExpr().getArray(event)) {
			PlayerProfile profile = player.getPlayerProfile();
			PlayerTextures textures = profile.getTextures();
			URL skin = textures.getSkin();
			SkinModel model = textures.getSkinModel();
			textures.setCape(url); // Null url will reset the cape.
			textures.setSkin(skin, model); // Keep reference of the original Skin. As setting the cape reset the skin.
			profile.setTextures(textures);
		    player.setPlayerProfile(profile);
		}
	}

}
