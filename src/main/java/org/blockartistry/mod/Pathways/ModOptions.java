/*
 * This file is part of Pathways, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.blockartistry.mod.Pathways;

import org.apache.commons.lang3.StringUtils;

import net.minecraftforge.common.config.Configuration;

public final class ModOptions {

	private ModOptions() {
	}

	protected static final String CATEGORY_LOGGING_CONTROL = "logging";
	protected static final String CONFIG_ENABLE_DEBUG_LOGGING = "Enable Debug Logging";
	protected static boolean enableDebugLogging = false;

	protected static final String CONFIG_ENABLE_ONLINE_VERSION_CHECK = "Enable Online Version Check";
	protected static boolean enableVersionChecking = true;

	protected static final String CATEGORY_GLOBAL = "global";
	protected static final String CONFIG_MAX_TELEPORT_ATTEMPTS = "Max Teleport Attempts";
	protected static int maxTeleportAttempts = 5;
	protected static final String CONFIG_TIME_BETWEEN_ATTEMPTS = "Wait Between Attempts";
	protected static int timeBetweenAttempts = 10;
	protected static final String CONFIG_ALLOW_WATER_LANDING = "Allow Water Landings";
	protected static boolean allowWaterLandings = false;

	protected static final String CATEGORY_COMMANDS = "commands";
	protected static final String CONFIG_COMMAND_ALIAS = "Command Alias";
	protected static final String CONFIG_COMMAND_OP_ONLY = "Op Only";
	protected static final String CONFIG_COMMAND_ENABLE = "Enable Command";

	protected static final String CATEGORY_TELEPORT = "commands.teleport";
	protected static boolean commandTeleportEnable = true;
	protected static String commandTeleportAlias = "port,tele";
	protected static boolean commandTeleportOpOnly = true;
	protected static final String CATEGORY_CONFIGURE = "commands.configure";
	protected static boolean commandConfigureEnable = true;
	protected static String commandConfigureAlias = "tconfig,tc";
	protected static boolean commandConfigureOpOnly = true;

	protected static final String CATEGORY_DEATH = "global.player death";
	protected static final String CONFIG_DEATH_TARGET = "Target";
	protected static String deathTarget = "";
	protected static final String CONFIG_DEATH_IGNORE_BED = "Ignore Bed";
	protected static boolean deathIgnoreBed = false;

	protected static final String CATEGORY_PLAYER_JOIN = "global.player join";
	protected static final String CONFIG_JOIN_TARGET = "Target";
	protected static String joinTarget = "";
	protected static final String CONFIG_ONLY_NEW_PLAYER = "Only New Players";
	protected static boolean onlyNewPlayers = true;

	public static void load(final Configuration config) {

		// CATEGORY: Logging
		String comment = "Enables/disables debug logging of the mod";
		enableDebugLogging = config.getBoolean(CONFIG_ENABLE_DEBUG_LOGGING, CATEGORY_LOGGING_CONTROL,
				enableDebugLogging, comment);

		comment = "Enables/disables online version checking";
		enableVersionChecking = config.getBoolean(CONFIG_ENABLE_ONLINE_VERSION_CHECK, CATEGORY_LOGGING_CONTROL,
				enableVersionChecking, comment);

		// CATEGORY: Global
		comment = "Max attempts at teleport for a given execution";
		maxTeleportAttempts = config.getInt(CONFIG_MAX_TELEPORT_ATTEMPTS, CATEGORY_GLOBAL, maxTeleportAttempts, 1, 100,
				comment);

		comment = "Wait time between attempts in seconds";
		timeBetweenAttempts = config.getInt(CONFIG_TIME_BETWEEN_ATTEMPTS, CATEGORY_GLOBAL, timeBetweenAttempts, 0,
				60 * 15, comment);

		comment = "Allow teleport landings in water";
		allowWaterLandings = config.getBoolean(CONFIG_ALLOW_WATER_LANDING, CATEGORY_GLOBAL, allowWaterLandings,
				comment);

		// CATEGORY: command.teleport
		comment = "Enable /teleport command";
		commandTeleportEnable = config.getBoolean(CONFIG_COMMAND_ENABLE, CATEGORY_TELEPORT, commandTeleportEnable,
				comment);

		comment = "Alias names for the /teleport command";
		commandTeleportAlias = config.getString(CONFIG_COMMAND_ALIAS, CATEGORY_TELEPORT, commandTeleportAlias, comment);

		comment = "Restrict teleport command to ops";
		commandTeleportOpOnly = config.getBoolean(CONFIG_COMMAND_OP_ONLY, CATEGORY_TELEPORT, commandTeleportOpOnly,
				comment);

		// CATEGORY: command.configure
		comment = "Enable /tconfigure command";
		commandConfigureEnable = config.getBoolean(CONFIG_COMMAND_ENABLE, CATEGORY_CONFIGURE, commandConfigureEnable,
				comment);

		comment = "Alias names for the /tconfigure command";
		commandConfigureAlias = config.getString(CONFIG_COMMAND_ALIAS, CATEGORY_CONFIGURE, commandConfigureAlias,
				comment);

		comment = "Restrict configure command to ops";
		commandConfigureOpOnly = config.getBoolean(CONFIG_COMMAND_OP_ONLY, CATEGORY_CONFIGURE, commandConfigureOpOnly,
				comment);

		// CATEGORY: global.player death
		comment = "Teleport target for when a player dies/respawns";
		deathTarget = config.getString(CONFIG_DEATH_TARGET, CATEGORY_DEATH, deathTarget, comment);

		comment = "Ignore player bed position when respawning";
		deathIgnoreBed = config.getBoolean(CONFIG_DEATH_IGNORE_BED, CATEGORY_DEATH, deathIgnoreBed, comment);

		// CATEGORY: global.player join
		comment = "Teleport target for when a new player joins";
		joinTarget = config.getString(CONFIG_JOIN_TARGET, CATEGORY_PLAYER_JOIN, joinTarget, comment);

		comment = "Only when new players join, not returning old players";
		onlyNewPlayers = config.getBoolean(CONFIG_ONLY_NEW_PLAYER, CATEGORY_PLAYER_JOIN, onlyNewPlayers, comment);
	}

	public static boolean getEnableDebugLogging() {
		return enableDebugLogging;
	}

	public static boolean getOnlineVersionChecking() {
		return enableVersionChecking;
	}

	public static int getMaxTeleportAttempts() {
		return maxTeleportAttempts;
	}

	public static int getTimeBetweenAttempts() {
		return timeBetweenAttempts;
	}

	public static boolean getAllowWaterLandings() {
		return allowWaterLandings;
	}

	public static boolean getCommandTeleportEnable() {
		return commandTeleportEnable;
	}

	public static String[] getTeleportCommandAlias() {
		return StringUtils.split(commandTeleportAlias, ',');
	}

	public static boolean getCommandTeleportOpOnly() {
		return commandTeleportOpOnly;
	}

	public static boolean getCommandConfigureEnable() {
		return commandConfigureEnable;
	}

	public static String[] getConfigureCommandAlias() {
		return StringUtils.split(commandConfigureAlias, ',');
	}

	public static boolean getCommandConfigureOpOnly() {
		return commandConfigureOpOnly;
	}

	public static String getPlayerDeathTarget() {
		return deathTarget;
	}

	public static boolean getDeathIgnoreBed() {
		return deathIgnoreBed;
	}

	public static String getPlayerJoinTarget() {
		return joinTarget;
	}
	
	public static boolean getOnlyNewPlayers() {
		return onlyNewPlayers;
	}
}
