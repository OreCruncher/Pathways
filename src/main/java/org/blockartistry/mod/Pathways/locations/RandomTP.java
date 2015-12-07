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
import org.blockartistry.mod.Pathways.ModLog;
import org.blockartistry.mod.Pathways.ModOptions;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public final class RandomTP extends Target {
	
	private static final int ATTEMPTS = ModOptions.getMaxTeleportAttempts();
	private static final int CHUNK_ATTEMPTS = ATTEMPTS * 10;
	private static final Set<Block> BAD_BLOCKS = new HashSet<Block>();

	static {
		addBadBlock(Blocks.air);
		addBadBlock(Blocks.lava);
		addBadBlock(Blocks.flowing_lava);
		addBadBlock(Blocks.bedrock);
		addBadBlock(Blocks.cactus);
		addBadBlock(Blocks.fire);

		if (!ModOptions.getAllowWaterLandings()) {
			addBadBlock(Blocks.water);
			addBadBlock(Blocks.flowing_water);
		}
	}
	
	public static void addBadBlock(final Block block) {
		BAD_BLOCKS.add(block);
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
		return !BAD_BLOCKS.contains(block);
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

		final Random rand = new Random();

		final int range = maxRange * 2;
		int chunkX = 0;
		int chunkZ = 0;
		boolean hit = false;
		for (int i = 0; i < CHUNK_ATTEMPTS; i++) {
			
			chunkX = rand.nextInt(range) - maxRange;
			chunkZ = rand.nextInt(range) - maxRange;
			
			if(chunkX >= minRange || chunkZ >= minRange) {

				ModLog.debug("Chunk located after %d attempts", i);
				hit = true;
				
				// 50/50 shot at negating the offset
				if(rand.nextInt(2) == 0)
					chunkX = -chunkX;
				if(rand.nextInt(2) == 0)
					chunkZ = -chunkZ;
				
				// Convert to actual chunk coordinates to block
				// coordinates as a base for search.
				chunkX = ((location.x >> 4) + chunkX) << 4;
				chunkZ = ((location.z >> 4) + chunkZ) << 4;
				break;
			}
		}

		// Couldn't find a chunk
		if (!hit) {
			ModLog.debug("Couldn't find a chunk");
			return null;
		}

		// Make the necessary attempts to find a safe location within the chunk.
		// Don't scatter attempts across chunks because that isn't fair to the
		// server and other players.
		for (int i = 0; i < ATTEMPTS; i++) {

			final int newX = chunkX + rand.nextInt(16);
			final int newZ = chunkZ + rand.nextInt(16);
			final int newY = world.getTopSolidOrLiquidBlock(newX, newZ);

			// There may not be an appropriate block for landing
			if (newY != -1) {
				if (isSafeLandingBlock(world, newX, newY - 1, newZ)) {
					if (canBreath(world, newX, newY + 1, newZ)) {
						return new Coordinates(location.dimension, newX, newY, newZ);
					}
				}
			}
		}

		// Couldn't find a spot
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
