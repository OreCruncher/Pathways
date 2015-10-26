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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public final class Coordinates {

	private final class NBT {
		public static final String XCOORD = "x";
		public static final String YCOORD = "y";
		public static final String ZCOORD = "z";
		public static final String DIMENSION = "d";
	}
	
	public Coordinates() {

	}

	// The cached key is used by the various framework routines where a
	// temporary
	// key is generated just to index an internal table. It's thread local so
	// there should be no collision. They key should not be cached or used in
	// an index - unpredictable results will occur.
	private static final ThreadLocal<Coordinates> cachedKey = new ThreadLocal<Coordinates>() {
		@Override
		protected Coordinates initialValue() {
			return new Coordinates();
		}
	};

	public static Coordinates getCachedKey(final int dimension, final int x, final int y, final int z) {
		final Coordinates key = cachedKey.get();
		key.dimension = dimension;
		key.x = x;
		key.y = y;
		key.z = z;
		key.hashCode = calculateHash(x, y, z);
		return key;
	}

	public int dimension;
	public int x;
	public int y;
	public int z;
	public int hashCode;
	private String dimensionName;

	private static int calculateHash(final int x, final int y, final int z) {
		int working = (x << 24) | (z << 16) | y;
		int hash = 0;
		for (int i = 0; i < 4; i++) {
			hash = (33 * hash) ^ (working & 0xFF);
			working >>>= 8;
		}
		return hash;
	}

	public Coordinates(final int dimension, final int x, final int y, final int z) {
		this.dimension = dimension;
		this.x = x;
		this.y = y;
		this.z = z;
		this.hashCode = calculateHash(x, y, z);
	}

	public String getDimensionName() {
		if (dimensionName != null)
			return dimensionName;
		final WorldProvider provider = DimensionManager.getProvider(this.dimension);
		dimensionName = provider != null ? provider.getDimensionName() : Integer.toString(this.dimension);
		return dimensionName;
	}

	@Override
	public String toString() {
		return StringUtils.join(new int[] { this.dimension, this.x, this.y, this.z }, ',');
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		final Coordinates t = (Coordinates) obj;
		return t == this || (this.x == t.x && this.y == t.y && this.z == t.z && this.dimension == t.dimension);
	}

	public static Coordinates readFromNBT(final NBTTagCompound nbt, final Coordinates coord) {
		coord.x = nbt.getInteger(NBT.XCOORD);
		coord.z = nbt.getInteger(NBT.ZCOORD);
		coord.y = nbt.getShort(NBT.YCOORD);
		coord.dimension = nbt.getShort(NBT.DIMENSION);
		coord.hashCode = calculateHash(coord.x, coord.y, coord.z);
		return coord;
	}

	public static void writeToNBT(final NBTTagCompound nbt, final Coordinates coord) {
		nbt.setInteger(NBT.XCOORD, coord.x);
		nbt.setInteger(NBT.ZCOORD, coord.z);
		nbt.setShort(NBT.YCOORD, (short) coord.y);
		nbt.setShort(NBT.DIMENSION, (short) coord.dimension);
	}
	
	public static Coordinates fromPlayer(final EntityPlayerMP player) {
		return new Coordinates(player.dimension, (int)player.posX, (int)player.posY, (int)player.posZ);
	}
}
