package net.narutomod;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ClientProxyNarutomodMod implements IProxyNarutomodMod {
	public void init(FMLInitializationEvent event) {
	}

	public void preInit(FMLPreInitializationEvent event) {
		OBJLoader.INSTANCE.addDomain("narutomod");
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public void serverLoad(FMLServerStartingEvent event) {
	}
}
