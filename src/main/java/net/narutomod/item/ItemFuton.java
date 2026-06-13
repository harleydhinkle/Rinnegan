package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemFuton extends ItemWindRelease {
	public ItemFuton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemWindRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}

	@Deprecated
	public static class ChakraFlow extends ItemWindRelease.ChakraFlow {
		public ChakraFlow(net.minecraft.world.World world) {
			super(world);
		}
	}
}
