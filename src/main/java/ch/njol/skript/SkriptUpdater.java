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
package ch.njol.skript;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.localization.ArgsMessage;
import ch.njol.skript.localization.Message;
import ch.njol.skript.update.ReleaseManifest;
import ch.njol.skript.update.ReleaseStatus;
import ch.njol.skript.update.UpdateManifest;
import ch.njol.skript.update.Updater;

/**
 * Skript's update checker.
 */
public class SkriptUpdater extends Updater {
	
	public final static Message m_running_latest_version_beta = new Message("updater.running latest version (beta)");
	public final static Message m_running_latest_version = new Message("updater.running latest version");
	public final static ArgsMessage m_update_available = new ArgsMessage("updater.update available");
	public final static Message m_download_in_progress = new Message("updater.download in progress");
	public final static ArgsMessage m_download_error = new ArgsMessage("updater.download error");
	public final static Message m_check_in_progress = new Message("updater.check in progress");
	public final static Message m_updater_disabled = new Message("updater.updater disabled");
	public final static ArgsMessage m_check_error = new ArgsMessage("updater.check error");
	public final static ArgsMessage m_downloading = new ArgsMessage("updater.downloading");
	public final static ArgsMessage m_downloaded = new ArgsMessage("updater.downloaded");
	public final static Message m_internal_error = new Message("updater.internal error");
	public final static Message m_custom_version = new Message("updater.custom version");
	public final static Message m_not_started = new Message("updater.not started");
	public final static Message m_nightly = new Message("updater.nightly build");
	public final static Message m_checking = new Message("updater.checking");
	
	SkriptUpdater() {
		super(loadManifest());
	}
	
	/**
	 * Loads the release manifest from Skript jar.
	 * @return Release manifest.
	 */
	@Nullable
	private static ReleaseManifest loadManifest() {
		String manifest = null;
		try {
			Optional<URI> optional = Skript.getInstance().getPluginContainer().locateResource(new URI("release-manifest.json"));
			if (!optional.isPresent())
				return null;
			try (InputStream is = optional.get().toURL().openStream();
					Scanner s = new Scanner(is)) {
				s.useDelimiter("\\\\A");
				manifest = s.next();
			} catch (IOException e) {
				throw new IllegalStateException("Skript is missing release-manifest.json!");
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		assert manifest != null;
		return ReleaseManifest.load(manifest);
	}
	
	/**
	 * Checks for updates and messages the sender.
	 * @param sender Who should we message.
	 * @return Future that completes when we're done.
	 */
	public CompletableFuture<Void> updateCheck(Logger logger) {
		CompletableFuture<Void> future = checkUpdates().thenAccept(none -> {
			ReleaseStatus status = getReleaseStatus();
			switch (status) {
				case CUSTOM:
					logger.info(m_custom_version);
					break;
				case DEVELOPMENT:
					logger.info(m_nightly);
					break;
				case LATEST:
					logger.info(m_running_latest_version);
					break;
				case OUTDATED:
					UpdateManifest update = getUpdateManifest();
					assert update != null; // Because we just checked that one is available
					logger.info(m_update_available.toString(update.id, Skript.getVersion()));
					logger.info("Download it at: " + update.downloadUrl);
					break;
				case UNKNOWN:
					if (isEnabled()) {
						logger.error(m_internal_error);
					} else {
						logger.error(m_updater_disabled);
					}
					break;
			}
		});
		assert future != null;
		return future;
	}

	/**
	 * Checks for update change log and messages the sender.
	 * @param sender Who should we message
	 * @return Future that completes when we're done.
	 */
	public CompletableFuture<Void> changesCheck(Logger logger) {
		CompletableFuture<Void> future = updateCheck(logger).thenAccept(none -> {
			if (getReleaseStatus() == ReleaseStatus.OUTDATED) {
				UpdateManifest update = getUpdateManifest();
				if (update != null) { // Avoid a race condition
					logger.info("");
					logger.info("Patch notes:");
					for (String line : update.patchNotes.split("\\n")) {
						// Minecraft doesn't like CRLF, remove it
						line = line.replace("\r", "");
						
						// Find #issue references and make them links
						String processed = line;
						for (int start = line.indexOf('#'); start != -1; start = line.indexOf('#', start + 1)) {
							StringBuilder issue = new StringBuilder();
							for (int i = start + 1; i < line.length();) {
								int c = line.codePointAt(i);
								if (Character.isDigit(c)) {
									issue.appendCodePoint(c);
								} else {
									break;
								}
								i += Character.charCount(c);
							}
							
							// Ok, looks like valid issue reference
							if (issue.length() > 0) {
								// TODO get issue tracker URL from manifest or something
								processed = processed.replace("#" + issue,
										"https://github.com/SkriptLang/Skript/issues/"
										+ issue + "#" + issue);
							}
						}
						line = processed;
						
						assert line != null;
						logger.info(line);
					}
				}
			}
		});
		assert future != null;
		return future;
	}
}
