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

package org.blockartistry.mod.Pathways.network;

import java.lang.reflect.Field;
import java.util.Queue;

import org.blockartistry.mod.Pathways.ModLog;

import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C12PacketUpdateSign;

public final class SignUpdatePacketHandler extends SimpleChannelInboundHandler<C12PacketUpdateSign> {
	
	private static Field incomingPacketsField;
	
	static {
		
		try {
			incomingPacketsField = ReflectionHelper.findField(NetworkManager.class, "receivedPacketsQueue", "field_150748_i");
		} catch(Throwable t) {
			ModLog.warn("Unable to hook incoming packets");
		}
	}

	// The player this handler is associated with
	private EntityPlayerMP player;
	
	public SignUpdatePacketHandler(final ServerConnectionFromClientEvent event) {
		final EntityPlayerMP player = ((NetHandlerPlayServer)event.handler).playerEntity;
		this.player = player;
	}
	
	// Don't want to own the packet - just want to snoop it and
	// use the information.  Need to return false so the main
	// framework doesn't assume we handled the packet.
	@Override
	public boolean acceptInboundMessage(final Object msg) throws Exception {
		return false;
	}
	
	// Hook to see if this is the message we want, and handle
	// it appropriately.
	@Override
	public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

		// Forward on up the chain for main processing.  If this doesn't
		// happen the game doesn't go.
		super.channelRead(ctx, msg);

		// If it is the the packet we want forward to our own channelRead0 handling
		// routine for processing.
		if (player.capabilities.isCreativeMode && msg instanceof C12PacketUpdateSign) {
			channelRead0(ctx, (C12PacketUpdateSign) msg);
		}
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final C12PacketUpdateSign msg) throws Exception {
		
		// If we didn't hook just ignore
		if(incomingPacketsField == null)
			return;
		
		// Get the general handler.  We need to grab the packet queue
		// so we can stuff in a special packet to handle.  Reason is
		// that the regular sign update has to have occured first
		// before our logic runs.  And since that packet can be
		// queued we need to queue ours.
		final ChannelHandler handler = ctx.channel().pipeline().get("packet_handler");
		if(handler instanceof NetworkManager) {
			final NetworkManager manager = (NetworkManager)handler;
			@SuppressWarnings("unchecked")
			final Queue<Packet> queue = (Queue<Packet>)incomingPacketsField.get(manager);
			final ProcessSignUpdatePacket packet = new ProcessSignUpdatePacket(player.worldObj, msg);
			if(msg.hasPriority()) {
				packet.processPacket(manager.getNetHandler());
			} else {
				queue.add(packet);
			}
		}
	}
}
