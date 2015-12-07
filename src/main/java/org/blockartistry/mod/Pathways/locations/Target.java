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

package org.blockartistry.mod.Pathways.locations;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.Pathways.player.LastTeleportTracker;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldServer;

public abstract class Target {

	public final String name;
	public final Coordinates location;

	public Target(final String name, final Coordinates location) {
		this.name = name;
		this.location = location;
	}

	public static Target fromString(final String spec) {

		final String[] tokens = StringUtils.split(spec, ",");
		final TeleportType type = TeleportType.valueOf(tokens[0]);

		if (type == TeleportType.UNKNOWN)
			return null;

		final String name = tokens[1];
		final Coordinates loc = new Coordinates(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]),
				Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]));

		if (type == TeleportType.WARP)
			return new Warp(name, loc);

		final int minRange = Integer.parseInt(tokens[6]);
		final int maxRange = Integer.parseInt(tokens[7]);

		return new RandomTP(name, loc, minRange, maxRange);
	}

	protected abstract Coordinates selectLocation();

	protected boolean isDimensionLoaded(final int dimensionId) {
		final WorldServer server = MinecraftServer.getServer().worldServerForDimension(dimensionId);
		return server != null;
	}

	public void travel(final EntityPlayerMP player) {
		String playerMessage = null;
		if (LastTeleportTracker.isCooldownRestricted(player)) {
			playerMessage = StatCollector.translateToLocalFormatted("msg.Pathways.TeleportCooldownActive", LastTeleportTracker.cooldownTicksRemaining(player)/20.0F);
		} else if (!isDimensionLoaded(this.location.dimension)) {
			playerMessage = StatCollector.translateToLocal("msg.Pathways.DimensionNotLoaded");
		} else if (player.riddenByEntity != null || player.ridingEntity != null) {
			playerMessage = StatCollector.translateToLocal("msg.Pathways.CannotRideEntity");
		} else {
			// Set the teleport cool down at this point.  Not sure if a teleport
			// will occur.  Reason is that the location select routine could
			// cause chunk loading and we don't want a player spamming.
			LastTeleportTracker.setLastTick(player);
			
			// Get the target location.  If null is returned that means that a
			// valid location could not be found.
			final Coordinates target = selectLocation();
			if (target != null) {

				// Set the target location
				TargetManager.setPlayerLocation(player, target);

				playerMessage = StatCollector.translateToLocalFormatted("msg.Pathways.TeleportedTo",
						target.getDimensionName(), target.x, target.y, target.z);
			} else {
				playerMessage = StatCollector.translateToLocal("msg.Pathways.NoClearSpot");
			}
		}

		if (playerMessage != null && !playerMessage.isEmpty())
			player.addChatComponentMessage(new ChatComponentText(playerMessage));
	}

	public abstract TeleportType getType();

	@Override
	public String toString() {
		return StringUtils.join(new String[] { this.name, this.location.toString() }, ",");
	}

	public String forDisplay() {
		return String.format("%s: %s (%s %d, %d, %d)", getType().getName(), name, location.getDimensionName(),
				location.x, location.y, location.z);
	}
}
