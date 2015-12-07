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
import org.blockartistry.mod.Pathways.locations.Coordinates;
import org.blockartistry.mod.Pathways.locations.RandomTP;
import org.blockartistry.mod.Pathways.locations.Target;
import org.blockartistry.mod.Pathways.locations.TargetManager;
import org.blockartistry.mod.Pathways.locations.Warp;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

public class ConfigureCommand extends CommandBase {

	private static final String command = "tconfigure";
	private static final int CMD_SUBCOMMAND = 0;
	private static final int CMD_NAME = 1;
	private static final int CMD_MIN_RANGE = 2;
	private static final int CMD_MAX_RANGE = 3;

	@Override
	public String getCommandName() {
		return command;
	}

	@Override
	public String getCommandUsage(final ICommandSender icommandsender) {
		return StatCollector.translateToLocalFormatted("msg.Pathways.ConfigureCommandUsage", command);
	}

	@Override
	public List<String> getCommandAliases() {
		return Arrays.asList(ModOptions.getConfigureCommandAlias());
	}

	@Override
	public boolean canCommandSenderUseCommand(final ICommandSender sender) {
		final EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
		return !ModOptions.getCommandConfigureOpOnly() || (player != null && player.capabilities.isCreativeMode);
	}

	@Override
	public void processCommand(final ICommandSender sender, final String[] as) {

		final EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
		if (player != null) {

			String playerMessage = null;

			if (as.length == 0) {
				throw new SyntaxErrorException("msg.Pathways.ConfigureCommandError.incorrectFormat");
			} else {

				if ("list".compareToIgnoreCase(as[CMD_SUBCOMMAND]) == 0) {

					for (final String t : TargetManager.getTargetListForDisplay())
						player.addChatComponentMessage(new ChatComponentText(t));

				} else {

					if (as.length < 2) {
						throw new SyntaxErrorException("msg.Pathways.ConfigureCommandError.incorrectFormat");
					} else if ("add".compareToIgnoreCase(as[CMD_SUBCOMMAND]) == 0) {

						Target t = null;
						if (as.length == 2) {
							t = new Warp(as[CMD_NAME], Coordinates.fromPlayer(player));
						} else if (as.length < 4) {
							throw new SyntaxErrorException("msg.Pathways.ConfigureCommandError.missingRange");
						} else {
							final int minRange = parseIntWithMin(sender, as[CMD_MIN_RANGE], 1);
							final int maxRange = parseIntWithMin(sender, as[CMD_MAX_RANGE], 1);
							if(minRange > maxRange)
								throw new SyntaxErrorException("msg.Pathways.ConfigureCommandError.minGreaterMax");
							t = new RandomTP(as[CMD_NAME], Coordinates.fromPlayer(player), minRange, maxRange);
						}

						if (t != null) {
							TargetManager.add(t);
							playerMessage = StatCollector
									.translateToLocalFormatted("msg.Pathways.ConfigureCommand.added", t.forDisplay());
						}
					} else if ("remove".compareToIgnoreCase(as[CMD_SUBCOMMAND]) == 0) {

						TargetManager.remove(as[CMD_NAME]);
						playerMessage = StatCollector.translateToLocalFormatted("msg.Pathways.ConfigureCommand.removed",
								as[CMD_NAME]);

					} else {
						throw new SyntaxErrorException("msg.Pathways.ConfigureCommandError.unknownSubcommand");
					}
				}
			}

			if (playerMessage != null && !playerMessage.isEmpty()) {
				player.addChatComponentMessage(new ChatComponentText(playerMessage));
			}
		}
	}
}
