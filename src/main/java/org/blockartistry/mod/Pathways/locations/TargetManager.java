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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.blockartistry.mod.Pathways.ModLog;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public final class TargetManager {

	private static final String CATEGORY_TARGETS = "targets";
	private static final String CONFIG_ENTRIES = "entries";
	private static final Map<String, Target> targets = new TreeMap<String, Target>(String.CASE_INSENSITIVE_ORDER);

	private static Configuration dataFile;
	
	public static void initialize(final Configuration config) {

		dataFile = config;
		
		final String[] targetList = config.getStringList(CONFIG_ENTRIES, CATEGORY_TARGETS, new String[] {},
				"Defined WARP and teleport targets");
		for (final String t : targetList) {
			try {
				final Target target = Target.fromString(t);
				if (target != null)
					targets.put(target.name, target);
				else
					ModLog.warn("Unable to process target: '%s'", t);
			} catch (final Throwable ex) {
				ModLog.warn("Unable to process target: '%s'", t);
			}
		}
	}
	
	protected static void setPlayerLocation(final EntityPlayerMP player, final Coordinates target) {
		// Transfer dimensions
		if (player.dimension != target.dimension) {
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, target.dimension);
		}

		// Don't want the player moving...
		player.motionX = 0;
		player.motionY = 0;
		player.motionZ = 0;
		
		// Offset the location so it goes smoothly
		player.setPositionAndUpdate(target.x + 0.5F, target.y + 1, target.z + 0.5F);
	}

	public static void store(final Configuration config) {
		
		final List<String> temp = new ArrayList<String>();
		for (final Target t : targets.values())
			temp.add(t.toString());

		final Property prop = config.get(CATEGORY_TARGETS, CONFIG_ENTRIES, new String[] {});
		prop.set(temp.toArray(new String[temp.size()]));
	}

	public static void execute(final EntityPlayerMP player, final String target, final boolean onDeath) {
		final Target t = targets.get(target);
		if (t == null) {
			player.addChatComponentMessage(
					new ChatComponentText(StatCollector.translateToLocal("msg.Pathways.TeleportCommandError.unknownTarget")));
		} else {
			t.travel(player, onDeath);
		}
	}
	
	public static boolean targetExists(final String target) {
		return targets.containsKey(target);
	}
	
	public static TeleportType getType(final String target) {
		final Target t = targets.get(target);
		return t == null ? TeleportType.UNKNOWN : t.getType();
	}
	
	public static List<String> getTargetListForDisplay() {
		final List<String> list = new ArrayList<String>();
		for(final Target t: targets.values())
			list.add(t.forDisplay());
		return list;
	}
	
	public static void add(final Target t) {
		targets.put(t.name, t);
		store(dataFile);
		dataFile.save();
	}
	
	public static void remove(final String target) {
		targets.remove(target);
		store(dataFile);
		dataFile.save();
	}
}
