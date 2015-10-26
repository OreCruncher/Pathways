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

package org.blockartistry.mod.Pathways.commands;

import java.util.Arrays;
import java.util.List;

import org.blockartistry.mod.Pathways.ModOptions;
import org.blockartistry.mod.Pathways.locations.TargetManager;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TeleportCommand extends CommandBase {
	
	private static final String command = "teleport";

	@Override
	public String getCommandName() {
		return command;
	}

	@Override
	public String getCommandUsage(final ICommandSender sender) {
		return StatCollector.translateToLocalFormatted("msg.Pathways.TeleportCommandUsage",
				command);
	}
	
	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList(ModOptions.getTeleportCommandAlias());
	}

	@Override
	public boolean canCommandSenderUseCommand(final ICommandSender sender) {
		final EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
		return !ModOptions.getCommandTeleportOpOnly() || (player != null && player.capabilities.isCreativeMode); 
	}

	@Override
	public void processCommand(final ICommandSender sender, final String[] as) {

		final EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
		if (player != null) {

			if (as.length != 1) {
				player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED
						+ StatCollector.translateToLocal("msg.Pathways.TeleportCommandError.missingName")));
			} else {
				TargetManager.execute(player, as[0]);
			}
		}
	}
}
