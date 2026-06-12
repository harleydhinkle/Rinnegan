package net.narutomod.item;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import net.minecraft.item.Item;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.narutomod.entity.*;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;




@ElementsNarutomodMod.ModElement.Tag

public class ItemSwampRelease extends ElementsNarutomodMod.ModElement {

    @ObjectHolder("narutomod:swamp_release")
    public static final Item block = null;

    public ItemSwampRelease(ElementsNarutomodMod instance) {
        super(instance, 982); // change ID later if needed
    }

    /*
     * ======================
     * Swamp RELEASE JUTSU
     * ======================
     */

     @Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:swamp_release", "inventory"));
	}


    public static final ItemJutsu.JutsuEnum SWAMPPIT =
        new ItemJutsu.JutsuEnum(
            0,
            "swamp_pit",
            'A',
            80d,
            new EntitySwampPit.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum SWAMPTAR =
        new ItemJutsu.JutsuEnum(
            1,
            "swamp_tar",
            'A',
            150d,
            new EntitySwampTar.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum VINETRAP =
        new ItemJutsu.JutsuEnum(
            2,
            "vine_trap",
            'S',
            200d,
            new EntityVineTrap.EC.Jutsu()
        );
 public static final ItemJutsu.JutsuEnum SWAMPSENTRY =
        new ItemJutsu.JutsuEnum(
            2,
            "swamp_sentry",
            'S',
            200d,
            new EntitySwampSentry.Jutsu()
        );
    @Override
    public void initElements() {
        this.elements.items.add(() ->
            new RangedItem(
                SWAMPPIT,
                SWAMPTAR,
                VINETRAP,
				SWAMPSENTRY
            ).setRegistryName("swamp_release")
        );


    }

    /*
     * ======================
     * SWAMP RELEASE ITEM
     * ======================
     */


    public static class RangedItem extends ItemJutsu.Base {

        public RangedItem(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.SWAMP, list);
            this.setUnlocalizedName("swamp_release");
            this.setCreativeTab(TabModTab.tab);
            this.defaultCooldownMap[SWAMPPIT.index] = 0;
            this.defaultCooldownMap[SWAMPTAR.index] = 0;
            this.defaultCooldownMap[VINETRAP.index] = 0;
            this.defaultCooldownMap[SWAMPSENTRY.index] = 0;
            // Removed TabModTab reference
        }
    }

}
