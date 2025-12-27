package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
import net.narutomod.entity.EntityBloodSiphonChains;
import net.narutomod.capabilities.NarutoModVariables;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {
    @ObjectHolder("narutomod:blood_release")
    public static final Item block = null;

    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
        new ItemJutsu.JutsuEnum(0, "blood_dragon", 'S', 200d, new EntityBloodDragon.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum BLOODPRISON =
        new ItemJutsu.JutsuEnum(1, "blood_prison", 'S', 150d, new EntityBloodPrison.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
        new ItemJutsu.JutsuEnum(2, "hiding_in_blood_mist", 'A', 80d, new EntityHidingInBloodMist.EC.Jutsu());
    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
        new ItemJutsu.JutsuEnum(3, "blood_siphon_chains", 'A', 100d, new EntityBloodSiphonChains.EC.Jutsu());

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 999);
    }

    @Override
    public void initElements() {
        this.elements.items.add(() -> new ItemCustom(
            BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS
        ));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:blood_release", "inventory"));
    }

    public static class ItemCustom extends ItemJutsu.Base {
        private final ItemJutsu.JutsuEnum[] myJutsuList;

        public ItemCustom(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            this.myJutsuList = list;
            this.setUnlocalizedName("blood_release");
            this.setRegistryName("blood_release");
            this.setCreativeTab(TabModTab.tab);

            // Example cooldowns (20 ticks = 1 second)
            this.defaultCooldownMap[BLOODDRAGON.index] = 400;       // 20 seconds
            this.defaultCooldownMap[BLOODPRISON.index] = 300;       // 15 seconds
            this.defaultCooldownMap[HIDINGINBLOODMIST.index] = 160; // 8 seconds
            this.defaultCooldownMap[BLOODSIPHONCHAINS.index] = 200; // 10 seconds
        }

        @Override
        public void onCreated(ItemStack stack, World world, EntityPlayer player) {
            if (!world.isRemote) {
                for (ItemJutsu.JutsuEnum j : myJutsuList) {
                    NarutoModVariables.PlayerVariables.get(player).unlockedJutsu.add(j);
                }
            }
        }

        @Override
        public void onUpdate(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean held) {
            super.onUpdate(stack, world, entity, slot, held);
        }
    }
}
