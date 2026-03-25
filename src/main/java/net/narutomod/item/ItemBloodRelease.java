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
import net.narutomod.entity.EntityBloodSiphonChains;



@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {

    @ObjectHolder("narutomod:blood_release")
    public static final Item block = null;

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 999); // change ID later if needed
    }

    /*
     * ======================
     * BLOOD RELEASE JUTSU
     * ======================
     */
     @Override
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:blood_release", "inventory"));
	}


    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
        new ItemJutsu.JutsuEnum(
            0,
            "blood_dragon",
            'S',
            200d,
            new EntityBloodDragon.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum BLOODPRISON =
        new ItemJutsu.JutsuEnum(
            1,
            "blood_prison",
            'S',
            150d,
            new EntityBloodPrison.EC.Jutsu()
        );

    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
        new ItemJutsu.JutsuEnum(
            2,
            "hiding_in_blood_mist",
            'A',
            80d,
            new EntityHidingInBloodMist.EC.Jutsu()
        );
    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
            new ItemJutsu.JutsuEnum(
                    3,
                    "blood_siphon_chains",
                    'A',
                    100d,
                    new EntityBloodSiphonChains.EC.Jutsu()
            );

    @Override
    public void initElements() {
        this.elements.items.add(() ->
            new ItemCustom(
                BLOODDRAGON,
                BLOODPRISON,
                HIDINGINBLOODMIST,
                    BLOODSIPHONCHAINS
            )
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
            this.setRegistryName("blood_release");
            this.setCreativeTab(TabModTab.tab);
            this.defaultCooldownMap[BLOODDRAGON.index]= 0;
            this.defaultCooldownMap[BLOODPRISON.index]= 0;
            this.defaultCooldownMap[HIDINGINBLOODMIST.index]= 0;
            this.defaultCooldownMap[BLOODSIPHONCHAINS.index]= 0;
        }
    }
}