package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.item.ItemJutsu;
import net.narutomod.entity.EntityBloodDragon;
import net.narutomod.entity.EntityBloodPrison;
import net.narutomod.entity.EntityHidingInBloodMist;
import net.narutomod.entity.EntityBloodSiphonChains;
import net.narutomod.entity.EntityRendererRegister;

@ElementsNarutomodMod.ModElement.Tag
public class ItemBloodRelease extends ElementsNarutomodMod.ModElement {

    @ObjectHolder("narutomod:blood_release")
    public static final Item block = null;

    public ItemBloodRelease(ElementsNarutomodMod instance) {
        super(instance, 999); // ID for Blood Release
    }

    /*
     * ======================
     * BLOOD RELEASE JUTSU
     * ======================
     */
    public static final ItemJutsu.JutsuEnum BLOODDRAGON =
        new ItemJutsu.JutsuEnum(0, "blood_dragon", 'S', 200d, new EntityBloodDragon.EC.Jutsu());

    public static final ItemJutsu.JutsuEnum BLOODPRISON =
        new ItemJutsu.JutsuEnum(1, "blood_prison", 'S', 150d, new EntityBloodPrison.EC.Jutsu());

    public static final ItemJutsu.JutsuEnum HIDINGINBLOODMIST =
        new ItemJutsu.JutsuEnum(2, "hiding_in_blood_mist", 'A', 80d, new EntityHidingInBloodMist.EC.Jutsu());

    public static final ItemJutsu.JutsuEnum BLOODSIPHONCHAINS =
        new ItemJutsu.JutsuEnum(3, "blood_siphon_chains", 'A', 100d, new EntityBloodSiphonChains.EC.Jutsu());

    @Override
    public void initElements() {
        this.elements.items.add(() -> new ItemCustom(BLOODDRAGON, BLOODPRISON, HIDINGINBLOODMIST, BLOODSIPHONCHAINS)
            .setRegistryName("blood_release"));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0,
            new ModelResourceLocation("narutomod:blood_release", "inventory"));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        new Renderer().register();
    }

    /*
     * ======================
     * BLOOD RELEASE ITEM
     * ======================
     */
    public static class ItemCustom extends ItemJutsu.Base {

        private final ItemJutsu.JutsuEnum[] myJutsuList;

        public ItemCustom(ItemJutsu.JutsuEnum... list) {
            super(ItemJutsu.JutsuEnum.Type.BLOOD, list);
            this.setUnlocalizedName("blood_release");
            this.setRegistryName("blood_release");
            this.setCreativeTab(TabModTab.tab);
            this.myJutsuList = list;
        }

        @Override
        public void onCreated(ItemStack stack, World world, EntityPlayer player) {
            for (ItemJutsu.JutsuEnum j : myJutsuList) {
                super.unlockJutsu(player, j); // use Base method
            }
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held) {
            super.onUpdate(stack, world, entity, slot, held);
        }
    }

    /*
     * ======================
     * BLOOD RELEASE RENDERER
     * ======================
     */
    public static class Renderer extends EntityRendererRegister {

        @SideOnly(Side.CLIENT)
        @Override
        public void register() {
            RenderingRegistry.registerEntityRenderingHandler(EntityBloodDragon.class, manager -> new RenderBloodDragon(manager));
            RenderingRegistry.registerEntityRenderingHandler(EntityBloodPrison.class, manager -> new RenderBloodPrison(manager));
            RenderingRegistry.registerEntityRenderingHandler(EntityHidingInBloodMist.class, manager -> new RenderBloodMist(manager));
            RenderingRegistry.registerEntityRenderingHandler(EntityBloodSiphonChains.class, manager -> new RenderSiphonChains(manager));
        }

        @SideOnly(Side.CLIENT)
        public static class RenderBloodDragon extends Render<EntityBloodDragon> {
            private final ResourceLocation texture = new ResourceLocation("narutomod:textures/blood_dragon.png");
            protected final ModelBase model = new ModelBase();

            public RenderBloodDragon(RenderManager renderManager) {
                super(renderManager);
                this.shadowSize = 0.5f;
            }

            @Override
            public void doRender(EntityBloodDragon entity, double x, double y, double z, float entityYaw, float pt) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                this.bindEntityTexture(entity);
                GlStateManager.popMatrix();
            }

            @Override
            protected ResourceLocation getEntityTexture(EntityBloodDragon entity) {
                return this.texture;
            }
        }

        @SideOnly(Side.CLIENT)
        public static class RenderBloodPrison extends Render<EntityBloodPrison> {
            private final ResourceLocation texture = new ResourceLocation("narutomod:textures/blood_prison.png");
            protected final ModelBase model = new ModelBase();

            public RenderBloodPrison(RenderManager renderManager) {
                super(renderManager);
                this.shadowSize = 0.5f;
            }

            @Override
            public void doRender(EntityBloodPrison entity, double x, double y, double z, float entityYaw, float pt) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                this.bindEntityTexture(entity);
                GlStateManager.popMatrix();
            }

            @Override
            protected ResourceLocation getEntityTexture(EntityBloodPrison entity) {
                return this.texture;
            }
        }

        @SideOnly(Side.CLIENT)
        public static class RenderBloodMist extends Render<EntityHidingInBloodMist> {
            private final ResourceLocation texture = new ResourceLocation("narutomod:textures/blood_mist.png");
            protected final ModelBase model = new ModelBase();

            public RenderBloodMist(RenderManager renderManager) {
                super(renderManager);
                this.shadowSize = 0.5f;
            }

            @Override
            public void doRender(EntityHidingInBloodMist entity, double x, double y, double z, float entityYaw, float pt) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                this.bindEntityTexture(entity);
                GlStateManager.popMatrix();
            }

            @Override
            protected ResourceLocation getEntityTexture(EntityHidingInBloodMist entity) {
                return this.texture;
            }
        }

        @SideOnly(Side.CLIENT)
        public static class RenderSiphonChains extends Render<EntityBloodSiphonChains> {
            private final ResourceLocation texture = new ResourceLocation("narutomod:textures/blood_siphon_chains.png");
            protected final ModelBase model = new ModelBase();

            public RenderSiphonChains(RenderManager renderManager) {
                super(renderManager);
                this.shadowSize = 0.5f;
            }

            @Override
            public void doRender(EntityBloodSiphonChains entity, double x, double y, double z, float entityYaw, float pt) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                this.bindEntityTexture(entity);
                GlStateManager.popMatrix();
            }

            @Override
            protected ResourceLocation getEntityTexture(EntityBloodSiphonChains entity) {
                return this.texture;
            }
        }
    }
}
