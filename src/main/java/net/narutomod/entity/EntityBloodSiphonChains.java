package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;

import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import net.narutomod.justuconfig;

@ElementsNarutomodMod.ModElement.Tag
public class EntityBloodSiphonChains extends ElementsNarutomodMod.ModElement {
    // === SAFE UNIQUE IDS ===
    public static final int ENTITYID = 940;

    public EntityBloodSiphonChains(ElementsNarutomodMod instance) {
        super(instance, ENTITYID);
    }

    @Override
    public void initElements() {
        elements.entities.add(() ->
                EntityEntryBuilder.create()
                        .entity(EC.class)
                        .id(new ResourceLocation("narutomod", "blood_siphon_chains"), ENTITYID)
                        .name("blood_siphon_chains")
                        .tracker(64, 3, true)
                        .build()
        );
    }

    public static class EC extends EntityBeamBase.Base implements ItemJutsu.IJutsu {
        private static final DataParameter<Integer> TARGET_ID   = EntityDataManager.createKey(EC.class, DataSerializers.VARINT);
        private static final DataParameter<Float>   TARGET_OFFX = EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);
        private static final DataParameter<Float>   TARGET_OFFY = EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);
        private static final DataParameter<Float>   TARGET_OFFZ = EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);

        private double initialDistance;
        private int slowAmplifier;

        private final double chakraDrainPerSecond = 10.0d; // target chakra drain per second
        private final int expireTime = 400; // 20 seconds (400 ticks)

        public EC(World worldIn) {
            super(worldIn);
            this.isImmuneToFire = true;
        }

        public EC(EntityLivingBase shooter, EntityLivingBase targetIn) {
            super(shooter);
            this.setTarget(targetIn);
            this.updatePosition();
        }

        @Override
        public ItemJutsu.JutsuEnum.Type getJutsuType() {
            return ItemJutsu.JutsuEnum.Type.BLOOD;
        }

        @Override
        protected void entityInit() {
            super.entityInit();
            this.dataManager.register(TARGET_ID, -1);
            this.dataManager.register(TARGET_OFFX, 0.0F);
            this.dataManager.register(TARGET_OFFY, 0.0F);
            this.dataManager.register(TARGET_OFFZ, 0.0F);
        }

        @Nullable
        private EntityLivingBase getTarget() {
            Entity e = this.world.getEntityByID(this.dataManager.get(TARGET_ID));
            return e instanceof EntityLivingBase ? (EntityLivingBase)e : null;
        }

        private void setTarget(EntityLivingBase targetIn) {
            this.dataManager.set(TARGET_ID, targetIn.getEntityId());

            // attach point on target (same idea as SealingChains)
            if (targetIn.isPotionActive(PotionHeaviness.potion)) {
                this.setTargetAttachVec(
                        (this.rand.nextDouble() - 0.5d) * targetIn.width * 0.9d,
                        this.rand.nextDouble() * (targetIn.getEyeHeight() - 0.2d * targetIn.height) + 0.1d * targetIn.height,
                        (this.rand.nextDouble() - 0.5d) * targetIn.width * 0.9d
                );
                this.slowAmplifier = targetIn.getActivePotionEffect(PotionHeaviness.potion).getAmplifier() + 1;
            } else {
                this.setTargetAttachVec(0d, targetIn.getEyeHeight() - 0.1d * targetIn.height, 0d);
                this.slowAmplifier = 1;
            }

            this.initialDistance = this.getDistance(targetIn) - 1d;
        }

        private void setTargetAttachVec(double x, double y, double z) {
            this.dataManager.set(TARGET_OFFX, (float)x);
            this.dataManager.set(TARGET_OFFY, (float)y);
            this.dataManager.set(TARGET_OFFZ, (float)z);
        }

        private Vec3d getTargetAttachVec() {
            EntityLivingBase target = this.getTarget();
            return target.getPositionVector().addVector(
                    ((Float)this.dataManager.get(TARGET_OFFX)).floatValue(),
                    ((Float)this.dataManager.get(TARGET_OFFY)).floatValue(),
                    ((Float)this.dataManager.get(TARGET_OFFZ)).floatValue()
            );
        }

        private boolean isTargetable(@Nullable Entity targetIn) {
            return ItemJutsu.canTarget(targetIn) && this.getDistance(targetIn) < this.initialDistance * 2;
        }

        @Override
        protected void updatePosition() {
            EntityLivingBase shooter = this.getShooter();
            if (shooter != null) {
                this.setPosition(shooter.posX, shooter.posY + shooter.height / 2, shooter.posZ);
            }

            // IMPORTANT: shoot beam toward the target so the renderer/model has a real beam vector
            if (!this.world.isRemote) {
                EntityLivingBase target = this.getTarget();
                if (target != null) {
                    Vec3d vec = this.getTargetAttachVec().subtract(this.getPositionVector());
                    // instant full extension
                    this.shoot(vec.x, vec.y, vec.z);
                }
            }
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            EntityLivingBase target = this.getTarget();

            // Kill instantly if shooter or target invalid
            if (this.shootingEntity == null || !this.shootingEntity.isEntityAlive() || target == null || !target.isEntityAlive()
                    || !this.isTargetable(target)) {
                if (!this.world.isRemote) this.setDead();
                return;
            }

            // Expire after 20 seconds (400 ticks)
            if (this.ticksExisted > expireTime) {
                if (!this.world.isRemote) this.setDead();
                return;
            }

            // Red particles on target (client side)
            if (this.world.isRemote) {
                for (int i = 0; i < 5; i++) {
                    double px = target.posX + (this.rand.nextDouble() - 0.5) * target.width;
                    double py = target.posY + this.rand.nextDouble() * target.height;
                    double pz = target.posZ + (this.rand.nextDouble() - 0.5) * target.width;
                    this.world.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0.0D, 0.05D, 0.0D);
                }
            }

            // Every second (20 ticks): slow + chakra drain + damage/heal
            if (this.ticksExisted % 20 == 0) {
                target.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 22, this.slowAmplifier));

                // Chakra drain target
                Chakra.pathway(target).consume(this.chakraDrainPerSecond);

                // Damage & heal
                float damage = justuconfig.bloodchainsdamage;
                float healAmount = justuconfig.bloodchainsheal;

                target.attackEntityFrom(net.minecraft.util.DamageSource.causeIndirectDamage(this, this.getShooter()), damage);

                EntityLivingBase shooter = this.getShooter();
                if (shooter != null) {
                    shooter.heal(healAmount);
                }
            }
        }

        public static class Jutsu implements ItemJutsu.IJutsuCallback {
            @Override
            public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
                RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 50d);
                if (res != null && res.entityHit instanceof EntityLivingBase) {
                    entity.world.spawnEntity(new EC(entity, (EntityLivingBase)res.entityHit));
                    return true;
                }
                return false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        new Renderer().register();
    }

    public static class Renderer extends EntityRendererRegister {
        @SideOnly(Side.CLIENT)
        @Override
        public void register() {
            RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new CustomRender(renderManager));
        }

        @SideOnly(Side.CLIENT)
        public class CustomRender extends EntityBeamBase.Renderer<EC> {
            private final ResourceLocation texture = new ResourceLocation("narutomod:textures/chainlink_blood.png");
            private final ModelChainLink model = new ModelChainLink();

            public CustomRender(RenderManager renderManagerIn) {
                super(renderManagerIn);
            }

            @Override
            public EntityBeamBase.Model getMainModel(EC entity, float pt) {
                this.model.addLinks(MathHelper.ceil(entity.getBeamLength() * 6.4f) - 1);
                return this.model;
            }

            @Override
            public void doRender(EC bullet, double x, double y, double z, float yaw, float pt) {
                EntityLivingBase shooter = bullet.getShooter();
                if (shooter != null) {
                    bullet.setPosition(
                            shooter.lastTickPosX + (shooter.posX - shooter.lastTickPosX) * (double)pt,
                            shooter.lastTickPosY + (shooter.posY - shooter.lastTickPosY) * (double)pt + shooter.height/2,
                            shooter.lastTickPosZ + (shooter.posZ - shooter.lastTickPosZ) * (double)pt
                    );
                    x = bullet.posX - this.getRenderManager().viewerPosX;
                    y = bullet.posY - this.getRenderManager().viewerPosY;
                    z = bullet.posZ - this.getRenderManager().viewerPosZ;
                }

                // only render if we have a valid target
                EntityLivingBase target = bullet.getTarget();
                if (target != null) {
                    Vec3d attach = bullet.getTargetAttachVec();
                    ProcedureUtils.Vec2f vec2f = ProcedureUtils.getYawPitchFromVec(attach.subtract(bullet.getPositionVector()));

                    this.bindEntityTexture(bullet);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x, y, z);
                    GlStateManager.rotate(-vec2f.x, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(90.0F + vec2f.y, 1.0F, 0.0F, 0.0F);

                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GlStateManager.disableCull();
                    GlStateManager.disableLighting();
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                    this.getMainModel(bullet, pt).render(bullet, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

                    GlStateManager.enableLighting();
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }

            @Override
            protected ResourceLocation getEntityTexture(EC entity) {
                return this.texture;
            }
        }

        // Made with Blockbench 3.8.4
        // Exported for Minecraft version 1.7 - 1.12
        @SideOnly(Side.CLIENT)
        public class ModelChainLink extends EntityBeamBase.Model {
            private final ModelRenderer chain;
            private final ModelRenderer[] link = new ModelRenderer[640];
            private final ModelRenderer tip;

            public void addLinks(int howmany) {
                howmany = MathHelper.clamp(howmany, 1, link.length);
                for (int i = 0; i < link.length; i++) {
                    link[i].showModel = i < howmany;
                }
                tip.setRotationPoint(0.0F, (float)howmany * 2.5F, 0.0F);
            }

            public ModelChainLink() {
                textureWidth = 16;
                textureHeight = 16;
                chain = new ModelRenderer(this);

                for (int i = 0; i < link.length; i++) {
                    link[i] = new ModelRenderer(this);
                    setRotationAngle(link[i], 0.0F, (float)((double)i * Math.PI * 0.4722222D), 0.0F);
                    link[i].setRotationPoint(0.0F, (float)i * 2.5F, 0.0F);
                    link[i].cubeList.add(new ModelBox(link[i], 0, 0, -0.5F, 0.0F, -1.0F, 1, 3, 2, 0.0F, false));
                    link[i].cubeList.add(new ModelBox(link[i], 8, 2, -0.5F, 0.0F, -1.0F, 1, 3, 2, 0.2F, false));
                    chain.addChild(link[i]);
                }

                tip = new ModelRenderer(this);
                setRotationAngle(tip, 0.0F, -0.7854F, 0.0F);
                chain.addChild(tip);
                this.addLinks(1);

                ModelRenderer bone = new ModelRenderer(this);
                bone.setRotationPoint(0.0F, 2.0F, 0.0F);
                tip.addChild(bone);
                setRotationAngle(bone, 0.0F, 0.0F, 0.7854F);
                bone.cubeList.add(new ModelBox(bone, 1, 10, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.0F, false));
                bone.cubeList.add(new ModelBox(bone, 12, 2, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.2F, false));

                bone = new ModelRenderer(this);
                bone.setRotationPoint(0.0F, 2.0F, 0.0F);
                tip.addChild(bone);
                setRotationAngle(bone, -0.7854F, 0.0F, 0.0F);
                bone.cubeList.add(new ModelBox(bone, 2, 8, 0.0F, -1.0F, -1.0F, 0, 2, 2, 0.0F, false));
                bone.cubeList.add(new ModelBox(bone, 12, 2, 0.0F, -1.0F, -1.0F, 0, 2, 2, 0.2F, false));

                ModelRenderer bone2 = new ModelRenderer(this);
                bone2.setRotationPoint(0.0F, 3.0F, 0.0F);
                tip.addChild(bone2);
                setRotationAngle(bone2, 0.0F, 0.0F, 0.7854F);
                bone2.cubeList.add(new ModelBox(bone2, 1, 10, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.0F, false));
                bone2.cubeList.add(new ModelBox(bone2, 12, 2, -1.0F, -1.0F, 0.0F, 2, 2, 0, 0.2F, false));

                bone2 = new ModelRenderer(this);
                bone2.setRotationPoint(0.0F, 3.0F, 0.0F);
                tip.addChild(bone2);
                setRotationAngle(bone2, -0.7854F, 0.0F, 0.0F);
                bone2.cubeList.add(new ModelBox(bone2, 2, 8, 0.0F, -1.0F, -1.0F, 0, 2, 2, 0.0F, false));
                bone2.cubeList.add(new ModelBox(bone2, 12, 2, 0.0F, -1.0F, -1.0F, 0, 2, 2, 0.2F, false));
            }

            @Override
            public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
                chain.render(f5);
            }

            public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
                modelRenderer.rotateAngleX = x;
                modelRenderer.rotateAngleY = y;
                modelRenderer.rotateAngleZ = z;
            }
        }
    }
}
