package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ActionResult;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;

import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.procedure.ProcedureKamuiTeleportEntity;
import net.narutomod.procedure.ProcedureSusanoo;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntitySusanooWinged;
import net.narutomod.PlayerTracker;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRangedAdminAbuse extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:ranged_admin_abuse")
    public static final Item block = null;
    public static final int ENTITYID = 169;

    public ItemRangedAdminAbuse(ElementsNarutomodMod instance) {
        super(instance, 331);
    }

    @Override
    public void initElements() {
        elements.items.add(() -> new RangedItem());
        elements.entities.add(() -> EntityEntryBuilder.create().entity(Entityranged_admin_abuse.class)
                .id(new ResourceLocation("narutomod", "entitybulletkunai"), ENTITYID).name("entitybulletranged_admin_abuse")
                .tracker(64, 1, true).build());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:ranged_admin_abuse", "inventory"));
    }

    public static class RangedItem extends Item {
        public RangedItem() {
            super();
            this.setMaxDamage(0);
            this.setFull3D();
            this.setUnlocalizedName("ranged_admin_abuse");
            this.setRegistryName("ranged_admin_abuse");
            this.maxStackSize = 1;
            this.setCreativeTab(TabModTab.tab);
        }

        @Override
        public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
            super.onUpdate(itemstack, world, entity, par4, par5);
            int susanooId = ProcedureSusanoo.getSummonedSusanooId(entity);
            if (!world.isRemote && entity instanceof EntityPlayer && !((EntityPlayer) entity).isCreative()
                    && (susanooId <= 0 || !(world.getEntityByID(susanooId) instanceof EntitySusanooWinged.EntityCustom))) {
                itemstack.shrink(1);
            }
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
            entity.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, entity.getHeldItem(hand));
        }

        @Override
        public EnumAction getItemUseAction(ItemStack itemstack) {
            return EnumAction.BOW;
        }

        @Override
        public int getMaxItemUseDuration(ItemStack itemstack) {
            return 72000;
        }
    }

    public static class Entityranged_admin_abuse extends EntityThrowable {
        private static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(Entityranged_admin_abuse.class, DataSerializers.FLOAT);
        private static final float ogWidth = 0.4f;

        public Entityranged_admin_abuse(World a) {
            super(a);
            this.setSize(this.ogWidth, this.ogWidth);
        }

        public Entityranged_admin_abuse(World worldIn, EntityLivingBase shooter) {
            super(worldIn, shooter);
            this.setSize(this.ogWidth, this.ogWidth);
        }

        @Override
        protected void entityInit() {
            this.getDataManager().register(SCALE, Float.valueOf(1.0F));
        }

        public void setScale(float scale) {
            this.getDataManager().set(SCALE, Float.valueOf(scale));
            this.setSize(this.ogWidth * scale, this.ogWidth * scale);
        }

        public float getScale() {
            return ((Float) this.getDataManager().get(SCALE)).floatValue();
        }

        @Override
        public void notifyDataManagerChange(DataParameter<?> key) {
            super.notifyDataManagerChange(key);
            if (SCALE.equals(key) && this.world.isRemote) {
                float f = this.getScale();
                this.setSize(this.ogWidth * f, this.ogWidth * f);
            }
        }

        @Override
        protected void onImpact(RayTraceResult result) {
            if (result.entityHit != null) {
                for (Entity entity = this.thrower; entity != null; entity = entity.getRidingEntity())
                    if (result.entityHit.equals(entity))
                        return;
            }
            if (!this.world.isRemote) {
                if (result.entityHit != null && this.thrower instanceof EntityPlayer) {
                    EntityPlayer thrower = (EntityPlayer) this.thrower;
                    result.entityHit.getEntityBoundingBox().getAverageEdgeLength();
                    if (result.entityHit instanceof EntityLivingBase) {
                        EntityLivingBase elb = (EntityLivingBase) result.entityHit;
                        elb.attackEntityFrom(DamageSource.OUT_OF_WORLD, elb.getMaxHealth() * (float)100);
                    } else {
                        result.entityHit.onKillCommand();
                    }
                }
                this.setDead();
            }
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            if (this.ticksExisted % 40 == 2) {
                this.playSound(net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:KamuiSFX")),
                        1, 1f / (this.rand.nextFloat() * 0.5f + 1f) + 0.25f);
            }
            if (this.inGround) {
                this.world.removeEntity(this);
            }
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        new Renderer().register();
    }

    public static class Renderer extends EntityRendererRegister {
        @SideOnly(Side.CLIENT)
        @Override
        public void register() {
            RenderingRegistry.registerEntityRenderingHandler(Entityranged_admin_abuse.class, renderManager -> {
                return new Renderranged_admin_abuse(renderManager, Minecraft.getMinecraft().getRenderItem());
            });
        }

        @SideOnly(Side.CLIENT)
        public class Renderranged_admin_abuse extends Render<Entityranged_admin_abuse> {
            protected final Item item;
            private final RenderItem itemRenderer;

            public Renderranged_admin_abuse(RenderManager renderManagerIn, RenderItem itemRendererIn) {
                super(renderManagerIn);
                this.item = block;
                this.itemRenderer = itemRendererIn;
            }

            @Override
            public void doRender(Entityranged_admin_abuse entity, double x, double y, double z, float entityYaw, float partialTicks) {
                GlStateManager.pushMatrix();
                float scale = entity.getScale();
                GlStateManager.translate((float) x, (float) y + (0.125F * scale), (float) z);
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.enableRescaleNormal();
                GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F,
                        0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(5f * ((float)entity.ticksExisted + partialTicks), 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-60f * ((float)entity.ticksExisted + partialTicks), 1.0F, 0.0F, 0.0F);
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.itemRenderer.renderItem(this.getStackToRender(entity), ItemCameraTransforms.TransformType.GROUND);
                GlStateManager.disableRescaleNormal();
                GlStateManager.popMatrix();
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }

            public ItemStack getStackToRender(Entityranged_admin_abuse entityIn) {
                return new ItemStack(this.item);
            }

            @Override
            protected ResourceLocation getEntityTexture(Entityranged_admin_abuse entity) {
                return TextureMap.LOCATION_BLOCKS_TEXTURE;
            }
        }
    }
}
