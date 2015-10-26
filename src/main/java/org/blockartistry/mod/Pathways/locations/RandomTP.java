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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.Pathways.ModOptions;
import org.blockartistry.mod.Pathways.util.XorShiftRandom;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class RandomTP extends Target {

	protected static Set<Block> badSpots = new HashSet<Block>();

	static {
		badSpots.add(Blocks.air);
		badSpots.add(Blocks.lava);
		badSpots.add(Blocks.flowing_lava);
		badSpots.add(Blocks.bedrock);
		badSpots.add(Blocks.cactus);
		badSpots.add(Blocks.fire);

		if (!ModOptions.getAllowWaterLandings()) {
			badSpots.add(Blocks.water);
			badSpots.add(Blocks.flowing_water);
		}
	}

	public final int minRange;
	public final int maxRange;

	public RandomTP(final String name, final Coordinates location, final int minRange, final int maxRange) {
		super(name, location);

		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	protected boolean isSafeLandingBlock(final World world, final int x, final int y, final int z) {
		final Block block = world.getBlock(x, y, z);
		return !badSpots.contains(block);
	}

	protected boolean canBreath(final World world, final int x, final int y, final int z) {
		return world.isAirBlock(x, y, z);
	}

	public World getDimensionWorldObject(final int dimensionId) {
		return MinecraftServer.getServer().worldServerForDimension(dimensionId);
	}

	public Coordinates selectLocation() {
		final World world = getDimensionWorldObject(location.dimension);

		// If for some reason we cannot get a world, return
		if (world == null)
			return null;

		final Random rand = XorShiftRandom.shared;

		// Calculate the target chunk
		final int newXBase = ((location.x >> 4) + rand.nextInt(minRange) - minRange / 2) << 4;
		final int newZBase = ((location.z >> 4) + rand.nextInt(minRange) - minRange / 2) << 4;

		// Make the necessary attempts to find a safe location within the chunk.
		// Don't scatter attempts across chunks because that isn't fair to the
		// server and other players.
		final int attempts = ModOptions.getMaxTeleportAttempts();
		for (int i = 0; i < attempts; i++) {

			final int newX = newXBase + rand.nextInt(16);
			final int newZ = newZBase + rand.nextInt(16);
			final int newY = world.getTopSolidOrLiquidBlock(newX, newZ);

			if (newY != -1) {
				if (isSafeLandingBlock(world, newX, newY - 1, newZ)) {
					if (canBreath(world, newX, newY + 1, newZ)) {
						return new Coordinates(location.dimension, newX, newY, newZ);
					}
				}
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return StringUtils.join(new String[] { TeleportType.RANDOM.name(), super.toString(), Integer.toString(minRange),
				Integer.toString(maxRange) }, ",");
	}

	@Override
	public TeleportType getType() {
		return TeleportType.RANDOM;
	}

	@Override
	public String forDisplay() {
		return String.format("%s; minRange %d, maxRange %d", super.forDisplay(), minRange, maxRange);
	}
}
