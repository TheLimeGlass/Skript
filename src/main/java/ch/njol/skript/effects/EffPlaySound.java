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
package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.OptionalLong;
import java.util.regex.Pattern;

@Name("Play Sound")
@Description({
	"Plays a sound at given location for everyone or just for given players, or plays a sound to specified players. " +
	"Both Minecraft sound names and " +
	"<a href=\"https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html\">Spigot sound names</a> " +
	"are supported. Playing resource pack sounds are supported too. The sound category is 'master' by default. ",
	"",
	"Playing a sound from an entity directly will result in the sound coming from said entity, even while moving.",
	"If the sound is custom, a location emitter will follow the entity. Do note that pitch and volume ",
	"are reflected based on the entity, and Minecraft may not use the values from this syntax.",
	"",
	"If using Paper 1.19.4+ or Adventure API 4.12.0+ you can utilize sound seeds. Minecraft sometimes have a set of sounds under one sound ID ",
	"that will randomly play, to counter this, you can directly state which seed to use.",
	"",
	"Please note that sound names can get changed in any Minecraft or Spigot version, or even removed from Minecraft itself.",
})
@Examples({
	"play sound \"block.note_block.pling\" # It is block.note.pling in 1.12.2",
	"play sound \"entity.experience_orb.pickup\" with volume 0.5 to the player",
	"play sound \"custom.music.1\" in jukebox category at {speakerBlock}",
	"play sound \"BLOCK_AMETHYST_BLOCK_RESONATE\" with seed 1 on target entity for the player"
})
@RequiredPlugins("Paper 1.19.4+ or Adventure API 4.12.0+ (sound seed)")
@Since("2.2-dev28, 2.4 (sound categories), INSERT VERSION (sound seed & entity emitter)")
public class EffPlaySound extends Effect {

	private static final boolean ADVENTURE_API = Skript.classExists("net.kyori.adventure.sound.Sound$Builder");
	private static final Pattern KEY_PATTERN = Pattern.compile("([a-z0-9._-]+:)?[a-z0-9/._-]+");

	static {
		String additional = "";
		if (ADVENTURE_API)
			additional = "[[with] seed %-number%] ";
		Skript.registerEffect(EffPlaySound.class,
				"play sound[s] %strings% " + additional + "[(in|from) %-soundcategory%] " +
						"[(at|with) volume %-number%] [(and|at|with) pitch %-number%] (at|on|from) %locations/entities% [(to|for) %-players%]",
				"play sound[s] %strings% " + additional + "[(in|from) %-soundcategory%] " +
						"[(at|with) volume %-number%] [(and|at|with) pitch %-number%] [(to|for) %players%] [(at|on|from) %-locations/entities%]"
		);
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<String> sounds;

	@Nullable
	private Expression<SoundCategory> category;

	@Nullable
	private Expression<Player> players;

	@Nullable
	private Expression<Number> volume;

	@Nullable
	private Expression<Number> pitch;

	@Nullable
	private Expression<Number> seed;

	@Nullable
	private Expression<?> emitters;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		sounds = (Expression<String>) exprs[0];
		int index = 1;
		if (ADVENTURE_API)
			seed = (Expression<Number>) exprs[index++];
		category = (Expression<SoundCategory>) exprs[index++];
		volume = (Expression<Number>) exprs[index++];
		pitch = (Expression<Number>) exprs[index++];
		if (matchedPattern == 0) {
			emitters = exprs[index++];
			players = (Expression<Player>) exprs[index];
		} else {
			players = (Expression<Player>) exprs[index++];
			emitters = exprs[index];
		}
		return true;
	}

	@Override
	protected void execute(Event event) {
		OptionalLong seed = OptionalLong.empty();
		if (this.seed != null) {
			Number number = this.seed.getSingle(event);
			if (number != null)
				seed = OptionalLong.of(number.longValue());
		}
		SoundCategory category = this.category == null ? SoundCategory.MASTER : this.category.getOptionalSingle(event)
				.orElse(SoundCategory.MASTER);
		float volume = this.volume == null ? 1 : this.volume.getOptionalSingle(event)
				.orElse(1)
				.floatValue();
		float pitch = this.pitch == null ? 1 : this.pitch.getOptionalSingle(event)
				.orElse(1)
				.floatValue();

		if (players != null) {
			if (emitters == null) {
				for (Player player : players.getArray(event)) {
					SoundReceiver.play(Player::playSound, Player::playSound, ADVENTURE_API ? Player::playSound : null, ADVENTURE_API ? Player::playSound : null,
							player,	player.getLocation(), sounds.getArray(event), category, volume, pitch, seed);
				}
			} else {
				for (Player player : players.getArray(event)) {
					for (Object emitter : emitters.getArray(event)) {
						if (emitter instanceof Entity) {
							Entity entity = (Entity) emitter;
							SoundReceiver.play(Player::playSound, Player::playSound, ADVENTURE_API ? Player::playSound : null, ADVENTURE_API ? Player::playSound : null,
									player,	entity, sounds.getArray(event), category, volume, pitch, seed);
						} else if (emitter instanceof Location) {
							Location location = (Location) emitter;
							SoundReceiver.play(Player::playSound, Player::playSound, ADVENTURE_API ? Player::playSound : null, ADVENTURE_API ? Player::playSound : null,
									player, location, sounds.getArray(event), category, volume, pitch, seed);
						}
					}
				}
			}
		} else if (emitters != null) {
			for (Object emitter : emitters.getArray(event)) {
				if (emitter instanceof Entity) {
					Entity entity = (Entity) emitter;
					SoundReceiver.play(World::playSound, World::playSound, ADVENTURE_API ? World::playSound : null, ADVENTURE_API ? World::playSound : null,
							entity.getWorld(), entity, sounds.getArray(event), category, volume, pitch, seed);
				} else if (emitter instanceof Location) {
					Location location = (Location) emitter;
					SoundReceiver.play(World::playSound, World::playSound, ADVENTURE_API ? World::playSound : null, ADVENTURE_API ? World::playSound : null,
							location.getWorld(), location, sounds.getArray(event), category, volume, pitch, seed);
				}
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		StringBuilder builder = new StringBuilder()
				.append("play sound ")
				.append(sounds.toString(event, debug));

		if (seed != null)
			builder.append(" with seed ").append(seed.toString(event, debug));
		if (category != null)
			builder.append(" in ").append(category.toString(event, debug));
		if (volume != null)
			builder.append(" with volume ").append(volume.toString(event, debug));
		if (pitch != null)
			builder.append(" with pitch ").append(pitch.toString(event, debug));
		if (emitters != null)
			builder.append(" from ").append(emitters.toString(event, debug));
		if (players != null)
			builder.append(" to ").append(players.toString(event, debug));
		
		return builder.toString();
	}

	@FunctionalInterface
	private interface SoundReceiver<T, E> {
		void play(
			@NotNull T receiver, @NotNull E emitter, @NotNull String sound,
			@NotNull SoundCategory category, float volume, float pitch
		);

		static <T, E> void play(
			@NotNull SoundReceiver<T, Entity> entityReceiver,
			@NotNull SoundReceiver<T, Location> locationReceiver,
			@NotNull AdventureSoundReceiver<T> adventureReceiver,
			@NotNull AdventureEntitySoundReceiver<T> adventureEmitterReceiver,
			@NotNull T receiver, @NotNull E emitter, @NotNull String[] sounds,
			@NotNull SoundCategory category, float volume, float pitch, OptionalLong seed
		) {
			for (String sound : sounds) {
				NamespacedKey key = null;
				try {
					Sound enumSound = Sound.valueOf(sound.toUpperCase(Locale.ENGLISH));
					key = enumSound.getKey();
				} catch (IllegalArgumentException alternative) {
					sound = sound.toLowerCase(Locale.ENGLISH);
					if (!KEY_PATTERN.matcher(sound).matches())
						continue;
					key = NamespacedKey.minecraft(sound);
				}

				if (key == null)
					continue;
				if (!ADVENTURE_API) {
					if (emitter instanceof Location) {
						locationReceiver.play(receiver, (Location) emitter, key.getKey(), category, volume, pitch);
					} else if (emitter instanceof Entity) {
						entityReceiver.play(receiver, (Entity) emitter, key.getKey(), category, volume, pitch);
					}
					return;
				}
				assert adventureReceiver != null;
				net.kyori.adventure.sound.Sound adventureSound = net.kyori.adventure.sound.Sound.sound()
						.source(category)
						.volume(volume)
						.pitch(pitch)
						.seed(seed)
						.type(key)
						.build();
				AdventureSoundReceiver.play(adventureReceiver, adventureEmitterReceiver, receiver, adventureSound, emitter);
			}
		}
	}

	@FunctionalInterface
	private interface AdventureSoundReceiver<T> {
		void play(
			@NotNull T receiver, @NotNull net.kyori.adventure.sound.Sound sound, double x, double y, double z
		);

		static <T, E> void play(
			@NotNull AdventureSoundReceiver<T> soundReceiver,
			@NotNull AdventureEntitySoundReceiver<T> emitterReceiver,
			@NotNull T receiver, @NotNull net.kyori.adventure.sound.Sound sound, @NotNull E emitter
		) {
			if (emitter instanceof Location) {
				Location location = (Location) emitter;
				soundReceiver.play(receiver, sound, location.getX(), location.getY(), location.getZ());
			} else if (emitter instanceof Entity) {
				Entity entity = (Entity) emitter;
				emitterReceiver.play(receiver, sound, entity);
			}
		}
	}

	@FunctionalInterface
	private interface AdventureEntitySoundReceiver<T> {
		void play(
			@NotNull T receiver, @NotNull net.kyori.adventure.sound.Sound sound, net.kyori.adventure.sound.Sound.Emitter emitter
		);
	}

}
