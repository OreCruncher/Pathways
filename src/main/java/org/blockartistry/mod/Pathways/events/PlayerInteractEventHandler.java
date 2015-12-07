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

import org.blockartistry.mod.Pathways.locations.TargetManager;
import org.blockartistry.mod.Pathways.sign.TeleportSignTracking;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public final class PlayerInteractEventHandler {
	
	private static Item CONFIGURE_ITEM = Items.nether_star;

	private enum InteractionType {

		Use, Configure, Nothing;

		public static InteractionType getInteractionType(final PlayerInteractEvent event) {

			if (event.action != Action.RIGHT_CLICK_BLOCK)
				return Nothing;

			if(event.entityPlayer.capabilities.isCreativeMode) {
				final ItemStack held = event.entityPlayer.getHeldItem();
				if(held != null && held.getItem() == CONFIGURE_ITEM) {
					return Configure;
				}
			}
			
			return Use;
		}
	}

	private PlayerInteractEventHandler() {
		
	}

	public static void initialize() {
		MinecraftForge.EVENT_BUS.register(new PlayerInteractEventHandler());
	}

	@SubscribeEvent(receiveCanceled = false)
	public void onBlockInteract(final PlayerInteractEvent event) {

		final World world = event.entityPlayer.worldObj;
		if (world.isRemote)
			return;

		final TileEntity te = world.getTileEntity(event.x, event.y, event.z);
		if (te instanceof TileEntitySign) {
			
			String playerMessage = null;
			final EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
			final TileEntitySign sign = (TileEntitySign) te;
			
			switch (InteractionType.getInteractionType(event)) {
			case Configure:
				if(TeleportSignTracking.canBeConfigured(sign)) {
					TeleportSignTracking.configureSign(sign);
					event.useItem = Result.DENY;
				} else
					playerMessage = StatCollector.translateToLocal("msg.Pathways.IncorrectSignFormat");
				break;
			case Use:
				if(TeleportSignTracking.canBeUsed(sign)) {
					final String target = TeleportSignTracking.getTargetName(sign);
					TargetManager.execute(player, target);
				}
				break;
			case Nothing:
			default:
				;
			}
			
			if (playerMessage != null && !playerMessage.isEmpty())
				player.addChatComponentMessage(new ChatComponentText(playerMessage));
		}
	}
}
