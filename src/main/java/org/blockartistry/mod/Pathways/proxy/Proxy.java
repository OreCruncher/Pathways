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

package org.blockartistry.mod.Pathways.proxy;

import org.blockartistry.mod.Pathways.ModOptions;
import org.blockartistry.mod.Pathways.VersionCheck;
import org.blockartistry.mod.Pathways.commands.ConfigureCommand;
import org.blockartistry.mod.Pathways.commands.TeleportCommand;
import org.blockartistry.mod.Pathways.events.PlayerInteractEventHandler;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class Proxy {

	public void preInit(FMLPreInitializationEvent event) {
		// Register early to give the background process a good amount
		// of time to get the mod version data
		VersionCheck.register();
	}

	public void init(FMLInitializationEvent event) {
		PlayerInteractEventHandler.initialize();
	}

	public void postInit(FMLPostInitializationEvent event) {

	}

	public void serverLoad(FMLServerStartingEvent event) {

	}
	
	public void serverStarting(final FMLServerStartingEvent event) {
		if(ModOptions.getCommandTeleportEnable())
			event.registerServerCommand(new TeleportCommand());
		
		if(ModOptions.getCommandConfigureEnable())
			event.registerServerCommand(new ConfigureCommand());
	}
}
