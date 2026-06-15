package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemWoodRelease;
import net.narutomod.ElementsNarutomodMod;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.entity.RenderManager;
import net.narutomod.entity.EntityRendererRegister;

@ElementsNarutomodMod.ModElement.Tag
public class EntityVineTrap extends ElementsNarutomodMod.ModElement {
    public static final int ENTITYID = 9501;

    public EntityVineTrap(ElementsNarutomodMod instance) {
        super(instance, 9501);
    }

    @Override
    public void initElements() {
        elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
            .id(new ResourceLocation("narutomod", "vine_trap"), ENTITYID)
            .name("vine_trap").tracker(64, 3, true).build());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient()) {
            new Renderer().register();
        }
    }

    public static class Renderer extends EntityRendererRegister {
        @SideOnly(Side.CLIENT)
        @Override
        public void register() {
            RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderVineSegment(renderManager));
        }

        @SideOnly(Side.CLIENT)
        public static class RenderVineSegment extends ItemWoodRelease.Renderer.RenderSegment {
            private final ResourceLocation vineTexture = new ResourceLocation("narutomod:textures/vine_segment.png");

            public RenderVineSegment(RenderManager renderManagerIn) {
                super(renderManagerIn);
            }

            @Override
            protected ResourceLocation getEntityTexture(ItemWoodRelease.WoodSegment entity) {
                return this.vineTexture;
            }
        }
    }

    public static class EC extends ItemWoodRelease.WoodSegment implements ItemJutsu.IJutsu {
        private int lifespan = 300;
        private EC prevSegment;
        private Entity target;
        private Vec3d targetVec;

        public EC(World world) {
            super(world);
            this.ignoreFrustumCheck = true;
            this.forceSpawn = true;
            this.noClip = false;
            this.isImmuneToFire = true;
        }

        public EC(Entity targetIn) {
            this(targetIn.world);
            this.setParent(this);
            this.setLocationAndAngles(targetIn.posX,
                    ProcedureUtils.getTopSolidBlockY(targetIn.world, new BlockPos(targetIn)) - 0.5d,
                    targetIn.posZ,
                    0f, 0f);
            this.setPositionAndRotationFromParent(1f);
            this.prevSegment = this;
            this.target = targetIn;
            this.targetVec = targetIn.getPositionVector();
        }

        public EC(EC segment, float yawOffset, float pitchOffset) {
            super(segment, yawOffset, pitchOffset);
            this.target = segment.target;
            this.targetVec = segment.targetVec;
            this.ignoreFrustumCheck = true;
            this.forceSpawn = true;
        }

        public EC(EC segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
            super(segment, offsetX, offsetY, offsetZ, yawOffset, pitchOffset);
            this.target = segment.target;
            this.targetVec = segment.targetVec;
            this.ignoreFrustumCheck = true;
            this.forceSpawn = true;
        }

        @Override
        public ItemJutsu.JutsuEnum.Type getJutsuType() {
            return ItemJutsu.JutsuEnum.Type.MOKUTON;
        }

        private void setLifespan(int ticks) {
            this.lifespan = ticks;
        }

        @Override
        public boolean isInRangeToRenderDist(double distance) {
            return true;
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            if (this.ticksExisted == 1 && this.rand.nextFloat() < 0.1f) {
                this.playSound(
                        net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("block.grass.break")),
                        0.8f,
                        this.rand.nextFloat() * 0.3f + 0.7f
                );
            }

            if (this.getParent() != null && this.ticksExisted < this.lifespan) {
                // Root (index 0) spawns all vine strands on the first tick
                if (this.getIndex() == 0 && this.ticksExisted == 1 && this.hasLivingTarget()) {
                    int vineCount = MathHelper.clamp((int)(this.target.width * 3f) + 3, 4, 10);
                    for (int i = 0; i < vineCount; i++) {
                        Vec3d vec = new Vec3d(
                                (this.rand.nextDouble() - 0.5d) * this.target.width * 2.5d,
                                0d,
                                (this.rand.nextDouble() - 0.5d) * this.target.width * 2.5d
                        );
                        float f = ProcedureUtils.getYawFromVec(this.targetVec.subtract(this.getPositionVector().add(vec)));
                        EC segment = new EC(this, vec.x, vec.y, vec.z, f + ((this.rand.nextFloat() - 0.5f) * 120f), 80f);
                        segment.setLifespan(this.lifespan - this.ticksExisted * 2);
                        segment.prevSegment = segment;
                        float segSize = this.target.width * 0.15f + 0.25f;
                        segment.setSize(segSize, segSize);
                        if (!this.world.isRemote)
                            this.world.spawnEntity(segment);
                    }
                }

                // Each strand (index 1) grows upward toward target, capped at target height
                if (!this.world.isRemote && this.getIndex() == 1 && this.ticksExisted > 1 && this.ticksExisted <= 35) {
                    boolean heightCapReached = this.targetVec != null && this.target != null
                            && this.prevSegment.posY > this.targetVec.y + this.target.height * 0.85f;

                    if (!heightCapReached) {
                        float yaw = (this.rand.nextFloat() - 0.5f) * 20f;
                        int i = this.prevSegment.getIndex();

                        if (this.hasLivingTarget() && i > 1) {
                            yaw = MathHelper.wrapDegrees(
                                    ProcedureUtils.getYawFromVec(this.targetVec.subtract(this.prevSegment.getPositionVector()))
                                            - this.prevSegment.rotationYaw
                            );
                            float f = this.height + 0.5f;
                            yaw *= f / (this.target.width + Math.max(4.4f - (float)i * 0.075f, f));
                        }

                        this.prevSegment = new EC(this.prevSegment, yaw, -0.5f);
                        this.prevSegment.setSize(this.width, this.height);
                        this.prevSegment.setLifespan(this.lifespan - this.ticksExisted * 2);

                        if (this.world instanceof net.minecraft.world.WorldServer)
                            ((net.minecraft.world.WorldServer) this.world).spawnEntity(this.prevSegment);
                    }
                }

                // Bind and damage target
                if (this.targetVec != null && this.targetTargetable()) {
                    if (this.ticksExisted > 30 && this.ticksExisted % 25 == 0) {
                        this.target.attackEntityFrom(
                                ItemJutsu.causeJutsuDamage(this, null).setDamageBypassesArmor(), 2.5f);
                    }
                    this.target.setPositionAndUpdate(this.targetVec.x, this.targetVec.y, this.targetVec.z);
                    this.target.motionX = 0;
                    this.target.motionY = 0;
                    this.target.motionZ = 0;
                }

            } else if (!this.world.isRemote) {
                this.setDead();
            }
        }

        @Override
        public boolean writeToNBTOptional(net.minecraft.nbt.NBTTagCompound compound) {
            return true;
        }

        private boolean hasLivingTarget() {
            return this.target != null && this.target.isEntityAlive();
        }

        private boolean targetTargetable() {
            if (!ItemJutsu.canTarget(this.target)) {
                this.target = null;
                return false;
            }
            return true;
        }

        public static class Jutsu implements ItemJutsu.IJutsuCallback {
            @Override
            public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
                RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 20d, 3d, false, false,
                        new Predicate<Entity>() {
                            public boolean apply(@Nullable Entity e) {
                                return e instanceof EntityLivingBase && e != entity;
                            }
                        });

                if (res != null && res.entityHit != null
                        && res.entityHit.posY - (ProcedureUtils.getGroundBelow(res.entityHit).getY() + 1)
                        < res.entityHit.height * 1.5f) {
                    if (!entity.world.isRemote) {
                        entity.world.spawnEntity(new EC(res.entityHit));
                    }
                    ((ItemJutsu.Base) stack.getItem()).setCurrentJutsuCooldown(stack, 300L);
                    return true;
                }
                return false;
            }
        }
    }
}
