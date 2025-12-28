package net.narutomod.item;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraft.item.Item;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
import net.narutomod.entity.EntityBloodSiphonChains;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {
    @ObjectHolder("narutomod:blood_release")
    public static final Item block = null;

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 999); // OK for now
    }

    /*
     * ======================
     * BLOOD RELEASE JUTSU
     * ======================
     */
    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
        new ItemJutsu.JutsuEnum(0, "blood_dragon", 'A', 100d, new EntityBloodDragon.EC.Jutsu());

    public static final ItemJutsu.JutsuEnum BLOODPRISON =
        new ItemJutsu.JutsuEnum(1, "blood_prison", 'A', 100d, new EntityBloodPrison.EC.Jutsu());

    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
        new ItemJutsu.JutsuEnum(2, "hiding_in_blood_mist", 'A', 100d, new EntityHidingInBloodMist.EC.Jutsu());

    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
        new ItemJutsu.JutsuEnum(3, "blood_siphon_chains", 'A', 100d, new EntityBloodSiphonChains.EC.Jutsu());

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:blood_release", "inventory"));
    }

    @Override
    public void initElements() {
        this.elements.items.add(() ->
            new ItemCustom(
                BLOODDRAGON,
                BLOODPRISON,
                HIDINGINBLOODMIST,
                BLOODSIPHONCHAINS
            ).setRegistryName("blood_release")
        );
    }

    public static class ItemCustom extends ItemJutsu.Base {
        public ItemCustom(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            this.setUnlocalizedName("blood_release");
            this.setCreativeTab(TabModTab.tab);
        }
    }
}
