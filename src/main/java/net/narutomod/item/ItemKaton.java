package net.narutomod.item;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemKaton extends ItemFireRelease {
	public ItemKaton(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public static class RangedItem extends ItemFireRelease.RangedItem {
		public RangedItem(ItemJutsu.JutsuEnum... list) {
			super(list);
		}
	}

	@Deprecated
	public static class EntityBigFireball extends ItemFireRelease.EntityBigFireball {
		public EntityBigFireball(net.minecraft.world.World world) {
			super(world);
		}
	}
}
