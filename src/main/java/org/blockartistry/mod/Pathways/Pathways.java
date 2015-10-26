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

package org.blockartistry.mod.Pathways;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.blockartistry.mod.Pathways.locations.TargetManager;
import org.blockartistry.mod.Pathways.proxy.Proxy;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Pathways.MOD_ID, useMetadata = true, dependencies = Pathways.DEPENDENCIES, version = Pathways.VERSION, acceptableRemoteVersions = "*")
public class Pathways {
	public static final String MOD_ID = "pathways";
	public static final String MOD_NAME = "Pathways";
	public static final String VERSION = "@VERSION@";
	public static final String DEPENDENCIES = "";

	@Instance(MOD_ID)
	protected static Pathways instance;

	public static Pathways instance() {
		return instance;
	}

	@SidedProxy(clientSide = "org.blockartistry.mod.Pathways.proxy.ProxyClient", serverSide = "org.blockartistry.mod.Pathways.proxy.Proxy")
	protected static Proxy proxy;

	public static Proxy proxy() {
		return proxy;
	}

	protected static Configuration config;

	public static Configuration config() {
		return config;
	}

	protected static File dataDirectory;

	public static File dataDirectory() {
		return dataDirectory;
	}

	public Pathways() {
		ModLog.setLogger(LogManager.getLogger(MOD_ID));
	}

	@EventHandler
	public void preInit(final FMLPreInitializationEvent event) {

		// Load up our configuration
		dataDirectory = new File(event.getModConfigurationDirectory(),
				Pathways.MOD_ID);
		dataDirectory.mkdirs();
		config = new Configuration(new File(dataDirectory, Pathways.MOD_ID
				+ ".cfg"));

		config.load();
		ModOptions.load(config);
		TargetManager.initialize(config);
		
		// TESTING TESTING TESTING
		TargetManager.store(config);
		config.save();

		proxy.preInit(event);
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		proxy.postInit(event);
		config.save();
	}

	@EventHandler
	public void serverStarting(final FMLServerStartingEvent event) {
		proxy.serverStarting(event);
	}
}
