package net.narutomod.item;

import net.minecraft.item.ItemArmor;

import net.narutomod.ElementsNarutomodMod;

@Deprecated
public class ItemDojutsu extends ItemOcularJutsu {
	public ItemDojutsu(ElementsNarutomodMod instance) {
		super(instance);
	}

	@Deprecated
	public abstract static class Base extends ItemOcularJutsu.Base {
		public Base(ItemArmor.ArmorMaterial material) {
			super(material);
		}
	}

	@Deprecated
	public enum Type {
		BYAKUGAN,
		SHARINGAN,
		RINNE_TENSEI;
	}

	@Deprecated
	public static class ClientModel extends ItemOcularJutsu.ClientModel {
	}
}
