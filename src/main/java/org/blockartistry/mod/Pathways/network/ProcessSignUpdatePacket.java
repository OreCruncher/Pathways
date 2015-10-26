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

import java.io.IOException;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;

public class ProcessSignUpdatePacket extends Packet {
	
	protected final World world;
	protected final int x;
	protected final int y;
	protected final int z;
	
	public ProcessSignUpdatePacket(final World world, final C12PacketUpdateSign msg) {
		this.world = world;
		this.x = msg.func_149588_c();
		this.y = msg.func_149586_d();
		this.z = msg.func_149585_e();
	}

	@Override
	public void readPacketData(final PacketBuffer packetbuffer) throws IOException {
		throw new NotImplementedException("readPacketData");
	}

	@Override
	public void writePacketData(final PacketBuffer packetbuffer) throws IOException {
		throw new NotImplementedException("writePacketData");
	}

	@Override
	public void processPacket(final INetHandler inethandler) {
		// Process the sign to ensure proper form and tracking
		final TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntitySign) {
			//final TileEntitySign sign = (TileEntitySign)te;
		}
	}
}
