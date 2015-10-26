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

package org.blockartistry.mod.Pathways.sign;

import java.util.HashSet;
import java.util.Set;

import org.blockartistry.mod.Pathways.Pathways;
import org.blockartistry.mod.Pathways.locations.Coordinates;
import org.blockartistry.mod.Pathways.locations.TargetManager;
import org.blockartistry.mod.Pathways.util.FormattingCodes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public final class TeleportSignTracking extends WorldSavedData {

	private final static String IDENTIFIER = Pathways.MOD_ID;
	private final static String COMMAND_COLOR = EnumChatFormatting.DARK_BLUE.toString();
	private final static String TARGET_COLOR = EnumChatFormatting.BLACK.toString();
	private final static String TYPE_COLOR = EnumChatFormatting.DARK_RED.toString();
	
	private final static int LINE_COMMAND = 0;
	private final static int LINE_TARGET = 1;
	private final static int LINE_EMPTY = 2;
	private final static int LINE_TYPE = 3;
	private final static String TELEPORT_SIGN_TEXT = "[Teleport]";

	private final Set<Coordinates> trackingList = new HashSet<Coordinates>();
	
	private final class NBT {
		
		public final static String ENTRIES = "entries";
		
	};
	
	public TeleportSignTracking() {
		this(IDENTIFIER);
	}
	
	public TeleportSignTracking(final String id) {
		super(id);
	}

	private static TeleportSignTracking getData(final TileEntitySign sign) {
		final World world = sign.getWorldObj();
		TeleportSignTracking data = (TeleportSignTracking)world.loadItemData(TeleportSignTracking.class, IDENTIFIER);
		if (data == null) {
			data = new TeleportSignTracking();
			world.setItemData(IDENTIFIER, data);
		}
		return data;
	}
	
	private static void addToTrackingList(final TileEntitySign sign) {
		final TeleportSignTracking data = getData(sign);
		final Coordinates t = new Coordinates(0, sign.xCoord, sign.yCoord, sign.zCoord); 
		data.trackingList.add(t);
		data.markDirty();
	}

	private static void removeFromTrackingList(final TileEntitySign sign) {
		final TeleportSignTracking data = getData(sign);
		final Coordinates t = Coordinates.getCachedKey(0, sign.xCoord, sign.yCoord, sign.zCoord); 
		data.trackingList.remove(t);
		data.markDirty();
	}

	private static boolean isTracked(final TileEntitySign sign) {
		final TeleportSignTracking data = getData(sign);
		final Coordinates t = Coordinates.getCachedKey(0, sign.xCoord, sign.yCoord, sign.zCoord);
		return data.trackingList.contains(t);
	}

	private static boolean isColored(final TileEntitySign sign) {
		return sign.signText[LINE_COMMAND].startsWith(COMMAND_COLOR.toString())
				&& sign.signText[LINE_TARGET].startsWith(TARGET_COLOR.toString());
	}

	public static boolean canBeUsed(final TileEntitySign sign) {
		return isColored(sign) && TargetManager.targetExists(getTargetName(sign)) && isTracked(sign);
	}

	public static boolean canBeConfigured(final TileEntitySign sign) {
		final String stripped = FormattingCodes.getTextWithoutFormattingCodes(sign.signText[LINE_COMMAND]);
		return TELEPORT_SIGN_TEXT.compareToIgnoreCase(stripped) == 0 && TargetManager.targetExists(getTargetName(sign));
	}

	public static String getTargetName(final TileEntitySign sign) {
		return FormattingCodes.getTextWithoutFormattingCodes(sign.signText[LINE_TARGET]);
	}

	public static void configureSign(final TileEntitySign sign) {
		sign.signText[LINE_COMMAND] = COMMAND_COLOR + TELEPORT_SIGN_TEXT;
		sign.signText[LINE_TARGET] = TARGET_COLOR
				+ FormattingCodes.getTextWithoutFormattingCodes(sign.signText[LINE_TARGET]);
		sign.signText[LINE_EMPTY] = "";
		sign.signText[LINE_TYPE] = TYPE_COLOR + "(" + TargetManager.getType(getTargetName(sign)).getName() + ")";
		sign.markDirty();
		sign.getWorldObj().markBlockForUpdate(sign.xCoord, sign.yCoord, sign.zCoord);
		removeFromTrackingList(sign);
		addToTrackingList(sign);
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		trackingList.clear();
		
		final NBTTagList nbttaglist = nbt.getTagList(NBT.ENTRIES, Constants.NBT.TAG_COMPOUND);
		if(nbttaglist == null)
			return;

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			final NBTTagCompound nbtTagCompound = nbttaglist
					.getCompoundTagAt(i);
			final Coordinates coord = new Coordinates();
			trackingList.add(Coordinates.readFromNBT(nbtTagCompound, coord));
		}
	}

	@Override
	public void writeToNBT(final NBTTagCompound nbt) {
		final NBTTagList nbttaglist = new NBTTagList();
		for(final Coordinates coord: trackingList) {
			final NBTTagCompound nbtTagCompound = new NBTTagCompound();
			Coordinates.writeToNBT(nbtTagCompound, coord);
			nbttaglist.appendTag(nbtTagCompound);
		}

		nbt.setTag(NBT.ENTRIES, nbttaglist);
	}
}
