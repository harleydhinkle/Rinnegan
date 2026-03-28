package net.narutomod.item;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import net.minecraft.item.Item;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
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
     * BLOOD RELEASE JUTSU
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
            'S',
            200d,
            new EntitySwampPit.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum SWAMPTAR =
        new ItemJutsu.JutsuEnum(
            1,
            "swamp_tar",
            'S',
            150d,
            new EntitySwampTar.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
        new ItemJutsu.JutsuEnum(
            2,
            "hiding_in_blood_mist",
            'A',
            80d,
            new EntityHidingInBloodMist.EC.Jutsu()
        );

    @Override
    public void initElements() {
        this.elements.items.add(() ->
            new ItemCustom(
                BLOODDRAGON,
                BLOODPRISON,
                HIDINGINBLOODMIST
            ).setRegistryName("blood_release")
        );
    }

    /*
     * ======================
     * BLOOD RELEASE ITEM
     * ======================
     */

    public static class ItemCustom extends ItemJutsu.Base {

        public ItemCustom(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            this.setUnlocalizedName("blood_release");
            this.setCreativeTab(TabModTab.tab);
            // Removed TabModTab reference
        }
    }
}
