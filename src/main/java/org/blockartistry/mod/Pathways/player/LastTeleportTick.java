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

package org.blockartistry.mod.Pathways.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.blockartistry.mod.Pathways.ModOptions;

import net.minecraft.entity.player.EntityPlayerMP;

public final class LastTeleportTick {

	private static final Map<UUID, Integer> tickTrack = new HashMap<UUID, Integer>();
	
	private LastTeleportTick() {
		
	}
	
	public static int getLast(final EntityPlayerMP player) {
		final Integer tick = tickTrack.get(player.getPersistentID());
		return tick == null ? ModOptions.getTimeBetweenAttempts() * -20 : tick.intValue();
	}
	
	public static void setLast(final EntityPlayerMP player, final int tick) {
		tickTrack.put(player.getPersistentID(), new Integer(tick));
	}
}
