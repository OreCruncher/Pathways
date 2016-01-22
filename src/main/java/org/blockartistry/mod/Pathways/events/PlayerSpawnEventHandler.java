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

package org.blockartistry.mod.Pathways.events;

import org.apache.commons.lang3.StringUtils;
import org.blockartistry.mod.Pathways.ModOptions;
import org.blockartistry.mod.Pathways.Pathways;
import org.blockartistry.mod.Pathways.locations.TargetManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

public class PlayerSpawnEventHandler {

	private static final boolean IGNORE_BED = ModOptions.getDeathIgnoreBed();
	private static final String RESPAWN_TARGET = ModOptions.getPlayerDeathTarget();

	private static final boolean ONLY_NEW_PLAYERS = ModOptions.getOnlyNewPlayers();
	private static final String PLAYER_JOIN_TARGET = ModOptions.getPlayerJoinTarget();

	public static void initialize() {
		final PlayerSpawnEventHandler handler = new PlayerSpawnEventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
	}

	private static final String NBT_KEY = Pathways.MOD_ID + ".joined";

	private static boolean isFirstJoin(final EntityPlayer player) {

		final NBTTagCompound data = player.getEntityData();
		NBTTagCompound persistent;
		if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
			data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
		} else {
			persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		}

		if (!persistent.hasKey(NBT_KEY)) {
			persistent.setBoolean(NBT_KEY, true);
			return true;
		}

		return false;
	}

	@SubscribeEvent()
	public void playerJoinEvent(final PlayerLoggedInEvent event) {
		if (isFirstJoin(event.player) || !ONLY_NEW_PLAYERS) {
			if (!StringUtils.isEmpty(PLAYER_JOIN_TARGET))
				TargetManager.execute((EntityPlayerMP) event.player, PLAYER_JOIN_TARGET, true);
		}
	}

	@SubscribeEvent()
	public void respawnEvent(final PlayerEvent.PlayerRespawnEvent event) {
		if (!event.player.worldObj.isRemote && event.player instanceof EntityPlayerMP
				&& !StringUtils.isEmpty(RESPAWN_TARGET)) {
			final EntityPlayerMP player = (EntityPlayerMP) event.player;
			if (IGNORE_BED || player.getBedLocation(player.dimension) == null)
				TargetManager.execute((EntityPlayerMP) event.player, RESPAWN_TARGET, true);
		}
	}

}
