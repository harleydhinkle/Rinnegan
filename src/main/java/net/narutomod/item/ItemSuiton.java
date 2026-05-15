package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemSuiton extends ItemWaterRelease {
	public ItemSuiton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemWaterRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}

	@Deprecated
	public static class EntityMist extends ItemWaterRelease.EntityMist {
		public EntityMist(net.minecraft.world.World world) {
			super(world);
		}
	}
}
