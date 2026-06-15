package net.narutomod.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.item.ItemJutsu;
import net.narutomod.potion.PotionCorrosion;
import net.narutomod.procedure.ProcedureUtils;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySwampSentry extends ElementsNarutomodMod.ModElement {

    public static final int ENTITYID = 350;
    public static final int ENTITYID_THORN = 351;

    public EntitySwampSentry(ElementsNarutomodMod instance) {
        super(instance, 700);
    }

    @Override
    public void initElements() {
        elements.entities.add(() -> EntityEntryBuilder.create()
                .entity(EC.class)
                .id(new ResourceLocation("narutomod", "stationary_plant"), ENTITYID)
                .name("stationary_plant")
                .tracker(64, 3, true)
                .build());
        elements.entities.add(() -> EntityEntryBuilder.create()
                .entity(PlantThorn.class)
                .id(new ResourceLocation("narutomod", "plant_thorn"), ENTITYID_THORN)
                .name("plant_thorn")
                .tracker(64, 2, true)
                .build());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EC.class,
                renderManager -> new RenderPlant(renderManager));
        RenderingRegistry.registerEntityRenderingHandler(PlantThorn.class,
                renderManager -> new RenderThorn(renderManager));
    }

    public static class EC extends EntitySummonAnimal.Base implements IRangedAttackMob, IEntityMultiPart {

        private static final DataParameter<Integer> ATTACK_TIMER =
                EntityDataManager.createKey(EC.class, DataSerializers.VARINT);
        private static final DataParameter<Integer> MELEE_TIMER =
                EntityDataManager.createKey(EC.class, DataSerializers.VARINT);
        private static final DataParameter<Float> FACE_YAW =
                EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);

        // Head segment — positioned at flower head for a proper hitbox there.
        private final MultiPartEntityPart headPart;

        // Target of the current attack — locked in when attackEntityWithRangedAttack
        // is called, fired once at bite-snap moment. Cleared after firing.
        private EntityLivingBase pendingTarget;
        private boolean firedThisAttack;
        private boolean dealtMeleeDamage;
        private int meleeCooldown;
        private EntityLivingBase meleeTarget;

        @Override
        public void entityInit() {
            super.entityInit();
            this.dataManager.register(ATTACK_TIMER, 0);
            this.dataManager.register(MELEE_TIMER, 0);
            this.dataManager.register(FACE_YAW, 0.0f);
        }

        public EC(World world) {
            super(world);
            this.setOGSize(2.0f, 3.5f);
            this.headPart = new MultiPartEntityPart(this, "head", 2.0f, 2.0f);
            this.isImmuneToFire = false;
        }

        public EC(EntityLivingBase summoner, float power) {
            super(summoner);
            this.setOGSize(2.0f, 3.5f);
            this.headPart = new MultiPartEntityPart(this, "head", 2.0f, 2.0f);
            this.isImmuneToFire = false;
            // Scale from power: power range 0.0-17.5 → scale 0.4-1.0.
            // Smaller overall so the plant feels like a sneaky garden
            // trap rather than a looming miniboss. Max-power plants are
            // roughly player-height; min-power plants are knee-high.
            float scale = 0.4f + MathHelper.clamp(power / 17.5f, 0.0f, 1.0f) * 0.6f;
            this.setScale(scale);
        }

        @Override
        public Entity[] getParts() {
            return new Entity[]{ this.headPart };
        }

        public int getAttackTimer() {
            return this.dataManager.get(ATTACK_TIMER);
        }

        public int getMeleeTimer() {
            return this.dataManager.get(MELEE_TIMER);
        }


        @Override
        public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
            return this.attackEntityFrom(source, damage);
        }

        @Override
        public World getWorld() {
            return this.world;
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            // Advance attack timer (server-authoritative).
            if (!this.world.isRemote) {
                int timer = this.dataManager.get(ATTACK_TIMER);
                if (timer > 0) {
                    this.dataManager.set(ATTACK_TIMER, timer - 1);
                    // Simple spit (timer=30) fires at t=0.5s (20 ticks remaining).
                    // Heavy spit (timer=70) fires at t=1.5s (40 ticks remaining).
                    int fireAt = (timer > 35) ? 40 : 20;
                    if (!this.firedThisAttack && timer - 1 == fireAt && this.pendingTarget != null) {
                        boolean isHeavy = timer > 35;
                        this.fireThorn(this.pendingTarget, isHeavy ? 3 : 1);
                        this.firedThisAttack = true;
                    }
                }
                // Clean up stale target reference once attack finishes.
                if (timer <= 0 && this.pendingTarget != null) {
                    this.pendingTarget = null;
                    this.firedThisAttack = false;
                }
                // Melee lunge timer: counts down from 40 (2s). Damage fires at tick 25 (t=0.75s).
                int melee = this.dataManager.get(MELEE_TIMER);
                if (melee > 0) {
                    this.dataManager.set(MELEE_TIMER, melee - 1);
                    if (!this.dealtMeleeDamage && melee - 1 == 25) {
                        this.dealMeleeDamage();
                        this.dealtMeleeDamage = true;
                    }
                }
                if (melee <= 0 && this.dealtMeleeDamage) {
                    this.dealtMeleeDamage = false;
                }
                // Proximity trigger: if anything gets within threat range, lunge at it.
                if (this.meleeCooldown > 0) this.meleeCooldown--;
                if (this.meleeCooldown <= 0 && melee <= 0 && this.dataManager.get(ATTACK_TIMER) <= 0) {
                    float triggerRadius = 5.0f * this.getScale() + 1.0f;
                    java.util.List<EntityLivingBase> close = this.world.getEntitiesWithinAABB(
                            EntityLivingBase.class,
                            this.getEntityBoundingBox().grow(triggerRadius));
                    EntityLivingBase summoner = this.getSummoner();
                    for (EntityLivingBase e : close) {
                        if (e == this || e == summoner) continue;
                        this.dataManager.set(MELEE_TIMER, 40);
                        this.dealtMeleeDamage = false;
                        this.meleeCooldown = 80;
                        this.meleeTarget = e;
                        break;
                    }
                }
                // Rotate to face the current target. Movement speed is always 0 so the
                // navigator never updates rotationYaw — we must do it manually.
                EntityLivingBase faceTarget = this.getAttackTarget();
                if (faceTarget == null) faceTarget = this.meleeTarget;
                if (faceTarget != null && !faceTarget.isDead) {
                    double dx = faceTarget.posX - this.posX;
                    double dz = faceTarget.posZ - this.posZ;
                    float targetYaw = (float)(Math.atan2(dx, dz) * 180.0 / Math.PI);
                    float diff = MathHelper.wrapDegrees(targetYaw - this.rotationYaw);
                    this.rotationYaw += MathHelper.clamp(diff, -10.0f, 10.0f);
                    this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
                } else {
                    this.meleeTarget = null;
                }
                // Push current facing yaw to clients via DataManager — Minecraft won't
                // send rotation packets reliably for a stationary entity.
                this.dataManager.set(FACE_YAW, this.rotationYaw);
            }

            // renderYawOffset drives the body render in RenderLiving. Since the plant
            // never moves, onLivingUpdate won't track it — force-sync it here on both sides.
            // On the client, pull from DataManager because rotationYaw isn't reliably
            // updated by the vanilla packet system for non-moving entities.
            if (this.world.isRemote) {
                this.rotationYaw = this.dataManager.get(FACE_YAW);
            }
            this.renderYawOffset = this.rotationYaw;

            // Head hitbox sits directly above the entity center, near the top.
            // The earlier yaw-based forward offset landed in the wrong place
            // because the model's yaw convention differs from world-yaw; plain
            // vertical offset is correct regardless of what yaw field the
            // renderer uses. This 2x2 hitbox covers the whole flower area.
            float f = this.getScale();
            double headX = this.posX;
            double headY = this.posY + 3.2 * f - this.headPart.height * 0.5d;
            double headZ = this.posZ;
            this.headPart.setLocationAndAngles(headX, headY, headZ, this.rotationYaw, 0.0f);
        }

        /**
         * Solve a ballistic launch: find the velocity that lobs a projectile
         * from (sx,sy,sz) to (tx,ty,tz) with a medium arc. Returns motion
         * vector (dx, dy, dz) per tick.
         *
         * Uses the low-angle solution so the thorn flies relatively flat and
         * fast (forgiving aim), not a mortar lob. If the target is too far
         * for the chosen speed, falls back to direct aim with a small upward
         * bias so the thorn at least travels straight toward the target.
         */
        private Vec3d solveBallistic(double sx, double sy, double sz,
                                     double tx, double ty, double tz) {
            final double v = 1.8;       // launch speed, blocks/tick — fast enough to reach ~50 blocks
            final double g = 0.04;      // gravity per tick (matches PlantThorn.onUpdate)

            double dx = tx - sx;
            double dy = ty - sy;
            double dz = tz - sz;
            double horizDist = Math.sqrt(dx * dx + dz * dz);
            if (horizDist < 0.01) {
                // Target directly above/below — shoot straight up, drop will land on them
                return new Vec3d(0, v, 0);
            }

            double v2 = v * v;
            double v4 = v2 * v2;
            double disc = v4 - g * (g * horizDist * horizDist + 2.0 * dy * v2);
            if (disc < 0) {
                // Out of range — fall back to direct aim with slight upward bias
                double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double scale = v / len;
                return new Vec3d(dx * scale, (dy + horizDist * 0.15) * scale, dz * scale);
            }
            double tanTheta = (v2 - Math.sqrt(disc)) / (g * horizDist); // low-angle root
            double cosTheta = 1.0 / Math.sqrt(1.0 + tanTheta * tanTheta);
            double sinTheta = tanTheta * cosTheta;
            double horizV = v * cosTheta;
            double vertV  = v * sinTheta;
            double hx = dx / horizDist;
            double hz = dz / horizDist;
            return new Vec3d(hx * horizV, vertV, hz * horizV);
        }

        /**
         * Spawn 1-4 thorns at the bite moment. Count is random with a weighted
         * distribution favoring single shots, so the burst attacks feel like
         * a satisfying rare payoff rather than the default.
         *
         * Multiple thorns spread in a shotgun cone via small yaw/pitch jitter.
         * Each thorn is sized THORN_SIZE_MULTIPLIER larger than the plant's
         * base scale so the magenta missing-texture cube reads as an actual
         * chunky spike instead of a pixel.
         */
        private void fireThorn(EntityLivingBase target, int thornCount) {
            final float THORN_SIZE_MULTIPLIER = 2.2f;
            final float SPREAD_DEGREES = 5.0f; // cone half-angle for burst thorns

            float f = this.getScale();
            double spawnX = this.posX;
            double spawnY = this.posY + 3.5 * f;
            double spawnZ = this.posZ;

            // Lead target based on motion
            double leadTx = target.posX + target.motionX * 4.0;
            double leadTy = target.posY + target.height * 0.5;
            double leadTz = target.posZ + target.motionZ * 4.0;

            // Base velocity — all extra thorns deviate from this
            Vec3d baseVel = solveBallistic(spawnX, spawnY, spawnZ, leadTx, leadTy, leadTz);

            for (int i = 0; i < thornCount; i++) {
                Vec3d vel = baseVel;
                // First thorn goes straight; extras get spread
                if (i > 0) {
                    double jitterYaw   = (this.rand.nextDouble() - 0.5) * 2.0 * SPREAD_DEGREES;
                    double jitterPitch = (this.rand.nextDouble() - 0.5) * 2.0 * SPREAD_DEGREES;
                    vel = rotateVelocity(baseVel, jitterYaw, jitterPitch);
                }
                PlantThorn thorn = new PlantThorn(this.world, this, spawnX, spawnY, spawnZ,
                        vel.x, vel.y, vel.z, f * THORN_SIZE_MULTIPLIER);
                this.world.spawnEntity(thorn);
            }

            // Louder pitch shift for multi-shots — audible cue that a burst landed
            float soundPitch = 0.8f + this.rand.nextFloat() * 0.4f
                    + (thornCount - 1) * 0.05f;
            this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:waterstream")),
                    1.0f + (thornCount - 1) * 0.3f, soundPitch);
        }

        /** Slam damage to all entities within threat range — called at the lunge snap moment. */
        private void dealMeleeDamage() {
            float f = this.getScale();
            double range = 4.5 * f;
            EntityLivingBase summoner = this.getSummoner();
            java.util.List<EntityLivingBase> victims = this.world.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    this.getEntityBoundingBox().grow(range));
            for (EntityLivingBase v : victims) {
                if (v == this || v == summoner) continue;
                v.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, this), 20.0f * f);
                double dx = v.posX - this.posX;
                double dz = v.posZ - this.posZ;
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.01) {
                    v.addVelocity(dx / len * 1.5, 0.5, dz / len * 1.5);
                }
                v.addPotionEffect(new PotionEffect(PotionCorrosion.potion, 160, 0, false, false));
            }
            this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
                    1.5f, 0.6f + this.rand.nextFloat() * 0.2f);
        }

        /** Rotate a velocity vector by small yaw/pitch jitter for shotgun spread. */
        private Vec3d rotateVelocity(Vec3d v, double yawDeg, double pitchDeg) {
            double speed = v.lengthVector();
            if (speed < 0.0001) return v;
            // Extract base yaw/pitch from velocity
            double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
            double baseYaw = Math.toDegrees(Math.atan2(v.x, v.z));
            double basePitch = Math.toDegrees(Math.atan2(v.y, horiz));
            // Apply jitter
            double yawRad = Math.toRadians(baseYaw + yawDeg);
            double pitchRad = Math.toRadians(basePitch + pitchDeg);
            double cosP = Math.cos(pitchRad);
            return new Vec3d(
                    Math.sin(yawRad) * cosP * speed,
                    Math.sin(pitchRad) * speed,
                    Math.cos(yawRad) * cosP * speed);
        }


        @Override
        protected void postScaleFixup() {
            float f = this.getScale();
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0D * f);
            this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D * f);
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D * f);
            this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
            super.postScaleFixup();
        }

        @Override
        protected void initEntityAI() {
            super.initEntityAI();
            this.tasks.addTask(2, new EntityAIAttackRanged(this, 0.0d, 60, 50.0f));
            this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 50.0f));
            this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
            this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(
                    this, EntityPlayer.class, true));
            this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<net.minecraft.entity.monster.EntityMob>(
                    this, net.minecraft.entity.monster.EntityMob.class, true));
        }

        @Override
        public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
            if (!this.world.isRemote) {
                if (this.dataManager.get(ATTACK_TIMER) > 0) return;
                if (this.dataManager.get(MELEE_TIMER) > 0) return;
                float minRange = 6.0f * this.getScale();
                if (this.getDistanceSq(target) < minRange * minRange) return;
                // Within 15 blocks → quick simple spit (30 ticks, 1 thorn).
                // Beyond 15 blocks → charged heavy burst (70 ticks, 3 thorns).
                boolean heavy = this.getDistanceSq(target) > 15.0 * 15.0;
                this.dataManager.set(ATTACK_TIMER, heavy ? 70 : 30);
                this.pendingTarget = target;
                this.firedThisAttack = false;
            }
        }

        @Override
        public void setSwingingArms(boolean swingingArms) { /* no-op */ }

        @Override
        public float getEyeHeight() {
            return this.height * 1.1f;
        }

        @Override
        protected SoundEvent getAmbientSound() {
            return SoundEvents.BLOCK_GRASS_STEP;
        }

        @Override
        protected SoundEvent getHurtSound(DamageSource ds) {
            return SoundEvents.ENTITY_GENERIC_HURT;
        }

        @Override
        protected SoundEvent getDeathSound() {
            return SoundEvents.ENTITY_GENERIC_DEATH;
        }
    }

    // =========================================================================
    // PlantThorn — ballistic projectile with AoE corrosion splash.
    //
    // Fired once per attack at the bite-snap moment. Flies in a medium arc,
    // explodes on block/entity impact, deals direct-hit damage to struck
    // target and splash damage + corrosion to everything within 4 blocks.
    // Self-contained — no particle entities, no EntityAcidScattering coupling.
    // =========================================================================
    public static class PlantThorn extends Entity {
        private static final DataParameter<Integer> SHOOTER_ID =
                EntityDataManager.<Integer>createKey(PlantThorn.class, DataSerializers.VARINT);
        private static final DataParameter<Float> THORN_SCALE =
                EntityDataManager.<Float>createKey(PlantThorn.class, DataSerializers.FLOAT);

        private static final float SPLASH_RADIUS   = 10.0f;
        private static final float DIRECT_DAMAGE   = 35.0f;
        private static final float SPLASH_DAMAGE   = 20.0f;
        private static final int   CORROSION_TICKS = 240;     // 6 seconds
        private static final int   MAX_LIFE        = 100;     // 5 seconds before timing out

        public PlantThorn(World world) {
            super(world);
            this.setSize(0.3f, 0.3f);
        }

        public PlantThorn(World world, EntityLivingBase shooter,
                          double x, double y, double z,
                          double mx, double my, double mz,
                          float scale) {
            super(world);
            this.setSize(0.3f * scale, 0.3f * scale);
            this.setPosition(x, y, z);
            this.motionX = mx;
            this.motionY = my;
            this.motionZ = mz;
            this.setShooterId(shooter != null ? shooter.getEntityId() : -1);
            this.setThornScale(scale);
            // Orient the thorn to face its initial velocity for a clean visual.
            float horizLen = MathHelper.sqrt(mx * mx + mz * mz);
            this.rotationYaw   = (float)(Math.atan2(mx, mz) * 180.0 / Math.PI);
            this.rotationPitch = (float)(Math.atan2(my, horizLen) * 180.0 / Math.PI);
            this.prevRotationYaw   = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        @Override
        protected void entityInit() {
            this.getDataManager().register(SHOOTER_ID, Integer.valueOf(-1));
            this.getDataManager().register(THORN_SCALE, Float.valueOf(1.0f));
        }

        private void setShooterId(int id) {
            this.getDataManager().set(SHOOTER_ID, Integer.valueOf(id));
        }

        @Nullable
        private EntityLivingBase getShooter() {
            Entity e = this.world.getEntityByID(this.getDataManager().get(SHOOTER_ID).intValue());
            return e instanceof EntityLivingBase ? (EntityLivingBase) e : null;
        }

        public void setThornScale(float s) {
            this.getDataManager().set(THORN_SCALE, Float.valueOf(s));
        }

        public float getThornScale() {
            return this.getDataManager().get(THORN_SCALE).floatValue();
        }

        @Override
        public void onUpdate() {
            super.onUpdate();

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.prevRotationYaw   = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;

            // Time out if it flies forever (misses everything).
            if (this.ticksExisted > MAX_LIFE) {
                if (!this.world.isRemote) {
                    this.detonate(null);
                }
                return;
            }

            // Raycast for block collision along this tick's trajectory.
            Vec3d from = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d to   = new Vec3d(this.posX + this.motionX, this.posY + this.motionY,
                    this.posZ + this.motionZ);
            RayTraceResult blockHit = this.world.rayTraceBlocks(from, to, false, true, false);
            // Check entity collision along the same path (exclude shooter).
            EntityLivingBase shooter = this.getShooter();
            Entity entityHit = findEntityInPath(from, to, shooter);

            // Whichever hit is closer wins.
            RayTraceResult finalHit = null;
            if (entityHit != null) {
                // Build a fake entity-hit result at the entity's position.
                finalHit = new RayTraceResult(entityHit);
            }
            if (blockHit != null && blockHit.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (finalHit == null ||
                        from.squareDistanceTo(blockHit.hitVec) < from.squareDistanceTo(new Vec3d(
                                entityHit.posX, entityHit.posY + entityHit.height * 0.5, entityHit.posZ))) {
                    finalHit = blockHit;
                }
            }

            if (finalHit != null && !this.world.isRemote) {
                this.detonate(finalHit);
                return;
            }

            // Apply ballistic motion.
            this.move(net.minecraft.entity.MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionY -= 0.04D;      // gravity — matches solveBallistic's g=0.04
            this.motionX *= 0.995D;     // minimal drag (thorn is aerodynamic)
            this.motionY *= 0.995D;
            this.motionZ *= 0.995D;

            // Update rotation to follow current trajectory so the thorn points
            // the way it's flying even after gravity bends the arc.
            float horizLen = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw   = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.rotationPitch = (float)(Math.atan2(this.motionY, horizLen) * 180.0 / Math.PI);

            // Trailing particle effect — heavy corrosion trail with dripping venom.
            // Three layers: behind-the-thorn spit droplets, purple magic flash on
            // the thorn body, and downward-falling drips. Together they sell the
            // idea that the thorn is shedding venom as it flies.
            if (this.world.isRemote) {
                float s = this.getThornScale();
                // Layer 1: behind-the-tip spit droplets, 4 per tick
                for (int i = 0; i < 4; i++) {
                    double ox = (this.rand.nextDouble() - 0.5) * 0.3 * s;
                    double oy = (this.rand.nextDouble() - 0.5) * 0.3 * s;
                    double oz = (this.rand.nextDouble() - 0.5) * 0.3 * s;
                    this.world.spawnParticle(net.minecraft.util.EnumParticleTypes.SPIT,
                            this.posX - this.motionX * 0.5 + ox,
                            this.posY - this.motionY * 0.5 + oy,
                            this.posZ - this.motionZ * 0.5 + oz,
                            0, 0, 0);
                }
                // Layer 2: magical purple flash on the thorn body itself, 2 per tick
                for (int i = 0; i < 2; i++) {
                    this.world.spawnParticle(net.minecraft.util.EnumParticleTypes.CRIT_MAGIC,
                            this.posX + (this.rand.nextDouble() - 0.5) * 0.2 * s,
                            this.posY + (this.rand.nextDouble() - 0.5) * 0.2 * s,
                            this.posZ + (this.rand.nextDouble() - 0.5) * 0.2 * s,
                            -this.motionX * 0.1, -this.motionY * 0.1, -this.motionZ * 0.1);
                }
                // Layer 3: falling drip particles — spawn with downward velocity
                // so they peel off the thorn and fall like venom beads. Spawns
                // every other tick to avoid overkill density.
                if ((this.ticksExisted & 1) == 0) {
                    for (int i = 0; i < 3; i++) {
                        double jitterX = (this.rand.nextDouble() - 0.5) * 0.2 * s;
                        double jitterZ = (this.rand.nextDouble() - 0.5) * 0.2 * s;
                        this.world.spawnParticle(net.minecraft.util.EnumParticleTypes.TOWN_AURA,
                                this.posX + jitterX,
                                this.posY - 0.1 * s,
                                this.posZ + jitterZ,
                                jitterX * 0.5, -0.15, jitterZ * 0.5);
                    }
                }
            }
        }

        /**
         * Find the first living entity intersected by the segment (from → to),
         * excluding the shooter. Returns null if none.
         */
        @Nullable
        private Entity findEntityInPath(Vec3d from, Vec3d to, @Nullable Entity exclude) {
            java.util.List<Entity> nearby = this.world.getEntitiesWithinAABBExcludingEntity(this,
                    this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(0.5));
            Entity closest = null;
            double closestDist = Double.MAX_VALUE;
            for (Entity e : nearby) {
                if (e == exclude || e == this) continue;
                if (!(e instanceof EntityLivingBase)) continue;
                if (!e.canBeCollidedWith()) continue;
                net.minecraft.util.math.AxisAlignedBB box =
                        e.getEntityBoundingBox().grow(0.3);
                RayTraceResult r = box.calculateIntercept(from, to);
                if (r != null) {
                    double d = from.squareDistanceTo(r.hitVec);
                    if (d < closestDist) {
                        closestDist = d;
                        closest = e;
                    }
                }
            }
            return closest;
        }

        /**
         * Apply damage + corrosion to the direct hit target (if any) and all
         * living entities within SPLASH_RADIUS of impact, then destroy self
         * with particle burst.
         */
        private void detonate(@Nullable RayTraceResult hit) {
            EntityLivingBase shooter = this.getShooter();
            // Also exclude the plant's summoner so they never take self-damage
            EntityLivingBase summoner = (shooter instanceof EC) ? ((EC) shooter).getSummoner() : null;
            double impactX = this.posX;
            double impactY = this.posY;
            double impactZ = this.posZ;

            // Direct hit bonus damage
            if (hit != null && hit.entityHit instanceof EntityLivingBase
                    && hit.entityHit != shooter && hit.entityHit != summoner) {
                EntityLivingBase direct = (EntityLivingBase) hit.entityHit;
                direct.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, shooter), DIRECT_DAMAGE);
                direct.addPotionEffect(new PotionEffect(PotionCorrosion.potion,
                        CORROSION_TICKS, 1, false, false));
                impactX = direct.posX;
                impactY = direct.posY + direct.height * 0.3;
                impactZ = direct.posZ;
            } else if (hit != null && hit.hitVec != null) {
                impactX = hit.hitVec.x;
                impactY = hit.hitVec.y;
                impactZ = hit.hitVec.z;
            }

            // AoE splash — corrosion + damage with 1/r² falloff
            java.util.List<EntityLivingBase> victims = this.world.getEntitiesWithinAABB(
                    EntityLivingBase.class,
                    new net.minecraft.util.math.AxisAlignedBB(
                            impactX - SPLASH_RADIUS, impactY - SPLASH_RADIUS, impactZ - SPLASH_RADIUS,
                            impactX + SPLASH_RADIUS, impactY + SPLASH_RADIUS, impactZ + SPLASH_RADIUS));
            for (EntityLivingBase v : victims) {
                if (v == shooter) continue;
                if (v == summoner) continue;
                // Skip the direct-hit target — already damaged above
                if (hit != null && v == hit.entityHit) continue;
                double dx = v.posX - impactX;
                double dy = (v.posY + v.height * 0.5) - impactY;
                double dz = v.posZ - impactZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (dist > SPLASH_RADIUS) continue;
                float falloff = 1.0f - (float)(dist / SPLASH_RADIUS);
                v.attackEntityFrom(ItemJutsu.causeJutsuDamage(this, shooter),
                        SPLASH_DAMAGE * falloff);
                v.addPotionEffect(new PotionEffect(PotionCorrosion.potion,
                        (int)(CORROSION_TICKS * falloff), 1, false, false));
            }

            // Visual burst of corrosion particles at impact
            Particles.Renderer particles = new Particles.Renderer(this.world);
            for (int i = 0; i < 60; i++) {
                double vx = (this.rand.nextDouble() - 0.5) * 0.8;
                double vy = this.rand.nextDouble() * 0.6 + 0.1;
                double vz = (this.rand.nextDouble() - 0.5) * 0.8;
                particles.spawnParticles(Particles.Types.SPIT,
                        impactX, impactY, impactZ, 1, 0, 0, 0,
                        vx, vy, vz, 0xF0D030FF, 30 + this.rand.nextInt(20),
                        shooter != null ? shooter.getEntityId() : -1, 40);
            }
            particles.send();

            this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:waterstream")),
                    2.0f, 1.4f + this.rand.nextFloat() * 0.3f);
            this.setDead();
        }

        @Override protected void readEntityFromNBT(NBTTagCompound compound) {}
        @Override protected void writeEntityToNBT(NBTTagCompound compound) {}
    }

    @SideOnly(Side.CLIENT)
    public static class RenderThorn extends net.minecraft.client.renderer.entity.Render<PlantThorn> {
        private static final ResourceLocation TEXTURE =
                new ResourceLocation("narutomod:textures/plant_thorn.png");
        private final ModelThorn model = new ModelThorn();

        public RenderThorn(RenderManager rm) {
            super(rm);
        }

        @Override
        protected ResourceLocation getEntityTexture(PlantThorn entity) {
            return TEXTURE;
        }

        @Override
        public void doRender(PlantThorn entity, double x, double y, double z,
                             float entityYaw, float partialTicks) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            float scale = entity.getThornScale();
            GlStateManager.scale(scale, scale, scale);
            // Orient thorn along velocity.
            float yaw   = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            GlStateManager.rotate(yaw,   0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-pitch, 1.0f, 0.0f, 0.0f);
            // Flip so tip leads
            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);

            this.bindEntityTexture(entity);
            this.model.render(entity, 0, 0, 0, 0, 0, 0.0625f);
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ModelThorn extends ModelBase {
        private final ModelRenderer body;

        public ModelThorn() {
            textureWidth  = 32;
            textureHeight = 32;
            body = new ModelRenderer(this);
            body.setRotationPoint(0.0F, 0.0F, 0.0F);
            // Tapered thorn: wide base, pointed tip along +Y in model space
            // (which becomes +Z after the 90° X-rotation in RenderThorn).
            body.cubeList.add(new ModelBox(body, 0, 0,  -1.5F, -8.0F, -1.5F, 3, 3, 3, 0.0F, false));
            body.cubeList.add(new ModelBox(body, 0, 6,  -1.0F, -5.0F, -1.0F, 2, 3, 2, 0.0F, false));
            body.cubeList.add(new ModelBox(body, 0, 11, -0.5F, -2.0F, -0.5F, 1, 2, 1, 0.0F, false));
        }

        @Override
        public void render(Entity entity, float f1, float f2, float f3, float f4, float f5, float scale) {
            body.render(scale);
        }
    }


    @SideOnly(Side.CLIENT)
    public static class RenderPlant extends RenderLiving<EC> {

        private static final ResourceLocation TEXTURE =
                new ResourceLocation("narutomod:textures/stationary_plant.png");

        public RenderPlant(RenderManager renderManagerIn) {
            super(renderManagerIn, new ModelPlant(), 0.8f);
        }

        @Override
        protected ResourceLocation getEntityTexture(EC entity) {
            return TEXTURE;
        }

        @Override
        protected void preRenderCallback(EC entity, float partialTicks) {
            float f = entity.getScale();
            GlStateManager.scale(f, f, f);
        }
    }

    public static class Jutsu implements ItemJutsu.IJutsuCallback {
        @Override
        public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            Particles.spawnParticle(entity.world, Particles.Types.SEAL_FORMULA,
                    entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d, 0d, 0d, 0d, (int)(power * 40), 0, 60);
            for (int i = 0; i < 500; i++) {
                Particles.spawnParticle(entity.world, Particles.Types.SMOKE,
                        entity.posX, entity.posY + 0.015d, entity.posZ, 1, 0d, 0d, 0d,
                        (entity.getRNG().nextDouble() - 0.5d) * 0.8d, entity.getRNG().nextDouble() * 0.6d + 0.2d,
                        (entity.getRNG().nextDouble() - 0.5d) * 0.8d,
                        0xD0FFFFFF, (int)(power * 30), (int)(16.0d / (entity.getRNG().nextDouble() * 0.8d + 0.2d)));
            }
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
                    SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
                    net.minecraft.util.SoundCategory.PLAYERS, 1f, 0.8f);
            EntitySwampSentry.EC entity1 = new EntitySwampSentry.EC(entity, power);
            entity1.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0.0f);
            net.narutomod.event.SpecialEvent.setDelayedSpawnEvent(entity.world, entity1, 0, 0, 0,
                    entity.world.getTotalWorldTime() + 20);
            return true;
        }
        @Override public float getBasePower() { return 0.0f; }
        @Override public float getPowerupDelay() { return 80.0f; }
        @Override public float getMaxPower() { return 17.5f; }
    }

    // =========================================================================
    // ModelPlant — uses keyframe lerp for the attack, idle unchanged
    // =========================================================================
    @SideOnly(Side.CLIENT)
    public static class ModelPlant extends ModelBase {

        private final ModelRenderer stationaryPlant;
        private final ModelRenderer articulation;
        private final ModelRenderer leaf5, group9, group10;
        private final ModelRenderer leaf6, group11, group12;
        private final ModelRenderer art;
        private final ModelRenderer branches_r1, branches_r2, branches_r3, branches_r4,
                branches_r5, branches_r6, branches_r7, branches_r8, branches_r9, branches_r10,
                branches_r11, branches_r12, branches_r13, branches_r14, branches_r15, branches_r16,
                branches_r17, branches_r18, branches_r19, branches_r20, branches_r21, branches_r22,
                branches_r23, branches_r24, branches_r25, branches_r26, branches_r27, branches_r28;
        private final ModelRenderer art2;
        private final ModelRenderer branches_r29, branches_r30, branches_r31, branches_r32,
                branches_r33, branches_r34, branches_r35, branches_r36, branches_r37, branches_r38,
                branches_r39, branches_r40, branches_r41, branches_r42, branches_r43, branches_r44,
                branches_r45, branches_r46, branches_r47, branches_r48, branches_r49, branches_r50,
                branches_r51, branches_r52, branches_r53, branches_r54, branches_r55, branches_r56,
                branches_r57, branches_r58, branches_r59, branches_r60;
        private final ModelRenderer neck, head, flower;
        private final ModelRenderer mouth_north, mn_articulation, mn_articulation2;
        private final ModelRenderer mouth_north2, mn_articulation3, mn_articulation4;
        private final ModelRenderer mouth_west, mw_articulation, mw_articulation2;
        private final ModelRenderer mouth_east, me_articulation, me_articulation2;
        private final ModelRenderer tongue;
        private final ModelRenderer upper_leaf, group17, group18;
        private final ModelRenderer upper_leaf2, group19, group20;
        private final ModelRenderer upper_leaf3, group27, group28;
        private final ModelRenderer upper_leaf4, group29, group30;
        private final ModelRenderer cubenotvisible;
        private final ModelRenderer leaf4, group7, group8;
        private final ModelRenderer leaf8, group15, group16;
        private final ModelRenderer leaf7, group13, group14;
        private final ModelRenderer leaf3, group5, group6;
        private final ModelRenderer leaf2, group3, group4;
        private final ModelRenderer leaf, group, group2;

        private float animAge     = 0.0f;
        private int attackTimer = 0;
        private int meleeTimer  = 0;

        private static final float RAD = 0.017453292f;
        private static final float MN_REST_X   =  0.7854f;
        private static final float MN2_REST_X  = -2.3562f;
        private static final float MNA_REST_X  = -1.0472f;
        private static final float MNA2_REST_X = -0.3491f;
        private static final float LEAF5_REST_X =  0.9065f, LEAF5_REST_Y =  0.6250f, LEAF5_REST_Z =  1.1412f;
        private static final float LEAF6_REST_X = -1.4446f, LEAF6_REST_Y =  0.4317f, LEAF6_REST_Z = -1.4579f;
        private static final float LEAF7_REST_X =  0.0f,    LEAF7_REST_Y = -0.3491f, LEAF7_REST_Z = -1.2217f;
        private static final float LEAF8_REST_X =  0.0f,    LEAF8_REST_Y = -0.2182f, LEAF8_REST_Z =  0.8727f;
        private static final float UL_REST_X  = -0.0420f, UL_REST_Y  =  0.2183f, UL_REST_Z  =  1.0530f;
        private static final float UL2_REST_X =  0.0020f, UL2_REST_Y =  0.1379f, UL2_REST_Z = -1.0936f;
        private static final float UL3_REST_X = -1.6714f, UL3_REST_Y =  0.4253f, UL3_REST_Z = -1.8107f;
        private static final float UL4_REST_X =  1.6477f, UL4_REST_Y = -0.2988f, UL4_REST_Z = -1.7519f;

        public ModelPlant() {
            textureWidth  = 512;
            textureHeight = 512;

            stationaryPlant = new ModelRenderer(this);
            stationaryPlant.setRotationPoint(0.0F, 24.0F, 0.0F);
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 136, 88, -16.0F, -8.0F, -16.0F, 32, 8, 32, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 0, 88, -17.0F, -16.0F, -17.0F, 34, 8, 34, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 0, 48, -18.0F, -20.0F, -18.0F, 36, 4, 36, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 0, 0, -20.0F, -24.0F, -22.0F, 40, 4, 44, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 0, 130, -17.0F, -28.0F, -17.0F, 34, 4, 34, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -1.0F, -8.0F, -24.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -7.0F, -8.0F, -23.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -13.0F, -8.0F, -24.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -18.0F, -8.0F, -22.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -22.0F, -8.0F, -17.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -21.0F, -8.0F, -12.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -24.0F, -8.0F, -6.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -22.0F, -8.0F, -1.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -23.0F, -8.0F, 5.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -20.0F, -8.0F, 11.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -21.0F, -8.0F, 16.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -18.0F, -8.0F, 21.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -12.0F, -8.0F, 20.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -7.0F, -8.0F, 22.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -2.0F, -8.0F, 19.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 4.0F, -8.0F, 20.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 10.0F, -8.0F, 22.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 17.0F, -8.0F, 20.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 19.0F, -8.0F, 16.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 22.0F, -8.0F, 11.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 19.0F, -8.0F, 5.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 17.0F, -8.0F, -1.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 20.0F, -8.0F, -6.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 20.0F, -8.0F, -12.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 19.0F, -8.0F, -18.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 20.0F, -8.0F, -23.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 15.0F, -8.0F, -21.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 11.0F, -8.0F, -24.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 7.0F, -8.0F, -19.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 3.0F, -8.0F, -21.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -1.0F, -20.0F, -24.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 5.0F, -20.0F, -23.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 11.0F, -20.0F, -24.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 16.0F, -20.0F, -22.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -13.0F, -20.0F, -24.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 19.0F, -20.0F, -12.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 20.0F, -20.0F, -17.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 18.0F, -20.0F, 11.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 20.0F, -20.0F, -1.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 22.0F, -20.0F, -6.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 21.0F, -20.0F, 5.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 19.0F, -20.0F, 16.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 16.0F, -20.0F, 21.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -9.0F, -20.0F, -19.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -5.0F, -20.0F, -21.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -17.0F, -20.0F, -21.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -22.0F, -20.0F, -23.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -21.0F, -20.0F, -18.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -19.0F, -20.0F, -1.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -22.0F, -20.0F, -6.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -22.0F, -20.0F, -12.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -21.0F, -20.0F, 5.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -24.0F, -20.0F, 11.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -21.0F, -20.0F, 16.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -19.0F, -20.0F, 20.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -12.0F, -20.0F, 22.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, -6.0F, -20.0F, 20.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 0.0F, -20.0F, 19.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 5.0F, -20.0F, 22.0F, 2, 8, 2, 0.0F, false));
            stationaryPlant.cubeList.add(new ModelBox(stationaryPlant, 168, 76, 10.0F, -20.0F, 20.0F, 2, 8, 2, 0.0F, false));

            articulation = new ModelRenderer(this);
            articulation.setRotationPoint(0.0F, -24.0F, 0.0F);
            stationaryPlant.addChild(articulation);
            articulation.cubeList.add(new ModelBox(articulation, 136, 128, -11.0F, -24.0F, -11.0F, 22, 35, 22, 0.0F, false));

            leaf5 = new ModelRenderer(this); leaf5.setRotationPoint(4.0F,-3.0F,-8.0F); articulation.addChild(leaf5); setRotationAngle(leaf5,LEAF5_REST_X,LEAF5_REST_Y,LEAF5_REST_Z); leaf5.cubeList.add(new ModelBox(leaf5,72,196,-1,-24,-8,2,24,16,0,false));
            group9  = new ModelRenderer(this); group9.setRotationPoint(0,-24,0);  leaf5.addChild(group9);  setRotationAngle(group9, 0,0, 0.5236F); group9.cubeList.add(new ModelBox(group9, 200,66,-1,-8,-7,2,8,14,0,false));
            group10 = new ModelRenderer(this); group10.setRotationPoint(0, -8,0);  group9.addChild(group10); setRotationAngle(group10,0,0, 0.5236F); group10.cubeList.add(new ModelBox(group10,108,213,-1,-4,-6,2,4,12,0,false));
            leaf6 = new ModelRenderer(this); leaf6.setRotationPoint(-4.0F,-3.0F,8.0F); articulation.addChild(leaf6); setRotationAngle(leaf6,LEAF6_REST_X,LEAF6_REST_Y,LEAF6_REST_Z); leaf6.cubeList.add(new ModelBox(leaf6,72,196,-1,-24,-8,2,24,16,0,false));
            group11 = new ModelRenderer(this); group11.setRotationPoint(0,-24,0); leaf6.addChild(group11);  setRotationAngle(group11,0,0,-0.5236F); group11.cubeList.add(new ModelBox(group11,200,66,-1,-8,-7,2,8,14,0,false));
            group12 = new ModelRenderer(this); group12.setRotationPoint(0, -8,0); group11.addChild(group12); setRotationAngle(group12,0,0,-0.5236F); group12.cubeList.add(new ModelBox(group12,108,213,-1,-4,-6,2,4,12,0,false));

            art = new ModelRenderer(this); art.setRotationPoint(0.0F,-24.0F,0.0F); articulation.addChild(art);
            art.cubeList.add(new ModelBox(art,0,168,-9.0F,-24.0F,-9.0F,18,32,18,0.0F,false));

            branches_r1  = mkB(art,-2,-11, 9, 0.4363f,0,0); branches_r1.cubeList.add(new ModelBox(branches_r1, 218,70,-1,0,-2,2,8,2,0,false));
            branches_r2  = mkB(art, 5, -8, 9, 0.4363f,0,0); branches_r2.cubeList.add(new ModelBox(branches_r2, 218,70,-1,0,-2,2,8,2,0,false));
            branches_r3  = mkB(art, 5,-23, 9, 0.4363f,0,0); branches_r3.cubeList.add(new ModelBox(branches_r3, 218,70,-1,0,-2,2,8,2,0,false)); branches_r3.cubeList.add(new ModelBox(branches_r3, 218,70,-9,0,-2,2,8,2,0,false));
            branches_r4  = mkB(art, 1,-19, 9, 0.4363f,0,0); branches_r4.cubeList.add(new ModelBox(branches_r4, 218,70,-1,0,-2,2,8,2,0,false));
            branches_r5  = mkB(art,-6,-17, 9, 0.4363f,0,0); branches_r5.cubeList.add(new ModelBox(branches_r5, 218,70,-1,0,-2,2,8,2,0,false));
            branches_r6  = mkB(art,-7, -5, 9, 0.4363f,0,0); branches_r6.cubeList.add(new ModelBox(branches_r6, 218,70,-1,0,-2,2,8,2,0,false));
            branches_r7  = mkB(art, 6,-17,-9,-0.4363f,0,0); branches_r7.cubeList.add(new ModelBox(branches_r7, 218,70,-1,0, 0,2,8,2,0,false));
            branches_r8  = mkB(art,-1,-19,-9,-0.4363f,0,0); branches_r8.cubeList.add(new ModelBox(branches_r8, 218,70,-1,0, 0,2,8,2,0,false));
            branches_r9  = mkB(art, 9,-22, 3, 0,0,-0.5236f); branches_r9.cubeList.add(new ModelBox(branches_r9, 218,70,-2,0,-2,2,8,2,0,false));
            branches_r10 = mkB(art, 9,-23,-3, 0,0,-0.5236f); branches_r10.cubeList.add(new ModelBox(branches_r10,218,70,-2,0,-2,2,8,2,0,false));
            branches_r11 = mkB(art, 9,-18,-6, 0,0,-0.5236f); branches_r11.cubeList.add(new ModelBox(branches_r11,218,70,-2,0,-2,2,8,2,0,false)); branches_r11.cubeList.add(new ModelBox(branches_r11,218,70,-2,0,12,2,8,2,0,false));
            branches_r12 = mkB(art, 9, -7,-5, 0,0,-0.5236f); branches_r12.cubeList.add(new ModelBox(branches_r12,218,70,-2,0,-2,2,8,2,0,false));
            branches_r13 = mkB(art, 9,-11,-2, 0,0,-0.5236f); branches_r13.cubeList.add(new ModelBox(branches_r13,218,70,-2,0,-2,2,8,2,0,false)); branches_r13.cubeList.add(new ModelBox(branches_r13,218,70,-2,0, 7,2,8,2,0,false));
            branches_r14 = mkB(art, 9,-13, 1, 0,0,-0.5236f); branches_r14.cubeList.add(new ModelBox(branches_r14,218,70,-2,0,-2,2,8,2,0,false));
            branches_r15 = mkB(art, 9, -8, 4, 0,0,-0.5236f); branches_r15.cubeList.add(new ModelBox(branches_r15,218,70,-2,0,-2,2,8,2,0,false));
            branches_r16 = mkB(art, 9,-24, 7, 0,0,-0.5236f); branches_r16.cubeList.add(new ModelBox(branches_r16,218,70,-2,0,-2,2,8,2,0,false));
            branches_r17 = mkB(art,-9,-22,-3, 0,0, 0.5236f); branches_r17.cubeList.add(new ModelBox(branches_r17,218,70, 0,0, 0,2,8,2,0,false));
            branches_r18 = mkB(art,-9,-18,-8, 0,0, 0.5236f); branches_r18.cubeList.add(new ModelBox(branches_r18,218,70, 0,0, 0,2,8,2,0,false)); branches_r18.cubeList.add(new ModelBox(branches_r18,218,70, 0,0,14,2,8,2,0,false));
            branches_r19 = mkB(art,-9, -8,-4, 0,0, 0.5236f); branches_r19.cubeList.add(new ModelBox(branches_r19,218,70, 0,0, 0,2,8,2,0,false));
            branches_r20 = mkB(art,-9,-13,-1, 0,0, 0.5236f); branches_r20.cubeList.add(new ModelBox(branches_r20,218,70, 0,0, 0,2,8,2,0,false));
            branches_r21 = mkB(art,-9,-24,-7, 0,0, 0.5236f); branches_r21.cubeList.add(new ModelBox(branches_r21,218,70, 0,0, 0,2,8,2,0,false));
            branches_r22 = mkB(art,-9, -7, 5, 0,0, 0.5236f); branches_r22.cubeList.add(new ModelBox(branches_r22,218,70, 0,0, 0,2,8,2,0,false));
            branches_r23 = mkB(art,-9,-23, 3, 0,0, 0.5236f); branches_r23.cubeList.add(new ModelBox(branches_r23,218,70, 0,0, 0,2,8,2,0,false));
            branches_r24 = mkB(art,-9,-11, 2, 0,0, 0.5236f); branches_r24.cubeList.add(new ModelBox(branches_r24,218,70, 0,0, 0,2,8,2,0,false)); branches_r24.cubeList.add(new ModelBox(branches_r24,218,70, 0,0,-9,2,8,2,0,false));
            branches_r25 = mkB(art, 7, -5,-9,-0.4363f,0,0); branches_r25.cubeList.add(new ModelBox(branches_r25,218,70,-1,0, 0,2,8,2,0,false));
            branches_r26 = mkB(art, 3,-23,-9,-0.4363f,0,0); branches_r26.cubeList.add(new ModelBox(branches_r26,218,70,-1,0, 0,2,8,2,0,false)); branches_r26.cubeList.add(new ModelBox(branches_r26,218,70,-9,0, 0,2,8,2,0,false));
            branches_r27 = mkB(art,-5, -8,-9,-0.4363f,0,0); branches_r27.cubeList.add(new ModelBox(branches_r27,218,70,-1,0, 0,2,8,2,0,false));
            branches_r28 = mkB(art, 2,-11,-9,-0.4363f,0,0); branches_r28.cubeList.add(new ModelBox(branches_r28,218,70,-1,0, 0,2,8,2,0,false));

            art2 = new ModelRenderer(this); art2.setRotationPoint(0.0F,-24.0F,0.0F); art.addChild(art2);
            setRotationAngle(art2,45.0f*0.017453292f,0.0f,0.0f);
            cubenotvisible = new ModelRenderer(this); cubenotvisible.setRotationPoint(0,0,0); art2.addChild(cubenotvisible);
            cubenotvisible.cubeList.add(new ModelBox(cubenotvisible,168,0,-7,-24,-7,14,30,14,0,false));

            branches_r29 = mkB(art2, 7,-18,-5, 0,       -1.5708f,-0.5236f); branches_r29.cubeList.add(new ModelBox(branches_r29,218,70, 0,0, 0,2,8,2,0,false));
            branches_r30 = mkB(art2, 7, -6,-5, 0,       -1.5708f,-0.5236f); branches_r30.cubeList.add(new ModelBox(branches_r30,218,70, 0,0, 0,2,8,2,0,false));
            branches_r31 = mkB(art2, 7, -8,-2, 0,       -1.5708f,-0.5236f); branches_r31.cubeList.add(new ModelBox(branches_r31,218,70, 0,0, 0,2,8,2,0,false));
            branches_r32 = mkB(art2, 7, -4, 4, 0,       -1.5708f,-0.5236f); branches_r32.cubeList.add(new ModelBox(branches_r32,218,70, 0,0, 0,2,8,2,0,false));
            branches_r33 = mkB(art2, 7,-13, 4, 0,       -1.5708f,-0.5236f); branches_r33.cubeList.add(new ModelBox(branches_r33,218,70, 0,0, 0,2,8,2,0,false));
            branches_r34 = mkB(art2, 7,-15, 1, 0,       -1.5708f,-0.5236f); branches_r34.cubeList.add(new ModelBox(branches_r34,218,70, 0,0, 0,2,8,2,0,false));
            branches_r35 = mkB(art2, 7,-22, 4, 0,       -1.5708f,-0.5236f); branches_r35.cubeList.add(new ModelBox(branches_r35,218,70, 0,0, 0,2,8,2,0,false));
            branches_r36 = mkB(art2, 7,-23,-2, 0,       -1.5708f,-0.5236f); branches_r36.cubeList.add(new ModelBox(branches_r36,218,70, 0,0, 0,2,8,2,0,false));
            branches_r37 = mkB(art2,-7, -4,-4, 0,       -1.5708f, 0.5236f); branches_r37.cubeList.add(new ModelBox(branches_r37,218,70,-2,0,-2,2,8,2,0,false));
            branches_r38 = mkB(art2,-7,-22,-4, 0,       -1.5708f, 0.5236f); branches_r38.cubeList.add(new ModelBox(branches_r38,218,70,-2,0,-2,2,8,2,0,false));
            branches_r39 = mkB(art2,-7, -8, 2, 0,       -1.5708f, 0.5236f); branches_r39.cubeList.add(new ModelBox(branches_r39,218,70,-2,0,-2,2,8,2,0,false));
            branches_r40 = mkB(art2,-7, -6, 5, 0,       -1.5708f, 0.5236f); branches_r40.cubeList.add(new ModelBox(branches_r40,218,70,-2,0,-2,2,8,2,0,false));
            branches_r41 = mkB(art2,-7,-18, 5, 0,       -1.5708f, 0.5236f); branches_r41.cubeList.add(new ModelBox(branches_r41,218,70,-2,0,-2,2,8,2,0,false));
            branches_r42 = mkB(art2,-7,-23, 2, 0,       -1.5708f, 0.5236f); branches_r42.cubeList.add(new ModelBox(branches_r42,218,70,-2,0,-2,2,8,2,0,false));
            branches_r43 = mkB(art2,-7,-15,-1, 0,       -1.5708f, 0.5236f); branches_r43.cubeList.add(new ModelBox(branches_r43,218,70,-2,0,-2,2,8,2,0,false));
            branches_r44 = mkB(art2,-7,-12,-4, 0,       -1.5708f, 0.5236f); branches_r44.cubeList.add(new ModelBox(branches_r44,218,70,-2,0,-2,2,8,2,0,false));
            branches_r45 = mkB(art2,-3, -4, 7, 0.5236f,  0,0); branches_r45.cubeList.add(new ModelBox(branches_r45,218,70,-2,0,-2,2,8,2,0,false));
            branches_r46 = mkB(art2,-3,-22, 7, 0.5236f,  0,0); branches_r46.cubeList.add(new ModelBox(branches_r46,218,70,-2,0,-2,2,8,2,0,false));
            branches_r47 = mkB(art2, 3, -8, 7, 0.5236f,  0,0); branches_r47.cubeList.add(new ModelBox(branches_r47,218,70,-2,0,-2,2,8,2,0,false));
            branches_r48 = mkB(art2, 6, -6, 7, 0.5236f,  0,0); branches_r48.cubeList.add(new ModelBox(branches_r48,218,70,-2,0,-2,2,8,2,0,false));
            branches_r49 = mkB(art2, 6,-18, 7, 0.5236f,  0,0); branches_r49.cubeList.add(new ModelBox(branches_r49,218,70,-2,0,-2,2,8,2,0,false));
            branches_r50 = mkB(art2, 3,-23, 7, 0.5236f,  0,0); branches_r50.cubeList.add(new ModelBox(branches_r50,218,70,-2,0,-2,2,8,2,0,false));
            branches_r51 = mkB(art2, 0,-15, 7, 0.5236f,  0,0); branches_r51.cubeList.add(new ModelBox(branches_r51,218,70,-2,0,-2,2,8,2,0,false));
            branches_r52 = mkB(art2,-4,-13, 7, 0.5236f,  0,0); branches_r52.cubeList.add(new ModelBox(branches_r52,218,70,-2,0,-2,2,8,2,0,false));
            branches_r53 = mkB(art2,-6,-18,-7,-0.5236f,  0,0); branches_r53.cubeList.add(new ModelBox(branches_r53,218,70, 0,0, 0,2,8,2,0,false));
            branches_r54 = mkB(art2, 0,-15,-7,-0.5236f,  0,0); branches_r54.cubeList.add(new ModelBox(branches_r54,218,70, 0,0, 0,2,8,2,0,false));
            branches_r55 = mkB(art2, 3,-22,-7,-0.5236f,  0,0); branches_r55.cubeList.add(new ModelBox(branches_r55,218,70, 0,0, 0,2,8,2,0,false));
            branches_r56 = mkB(art2, 4,-13,-7,-0.5236f,  0,0); branches_r56.cubeList.add(new ModelBox(branches_r56,218,70, 0,0, 0,2,8,2,0,false));
            branches_r57 = mkB(art2, 3, -4,-7,-0.5236f,  0,0); branches_r57.cubeList.add(new ModelBox(branches_r57,218,70, 0,0, 0,2,8,2,0,false));
            branches_r58 = mkB(art2,-3,-23,-7,-0.5236f,  0,0); branches_r58.cubeList.add(new ModelBox(branches_r58,218,70, 0,0, 0,2,8,2,0,false));
            branches_r59 = mkB(art2,-6, -6,-7,-0.5236f,  0,0); branches_r59.cubeList.add(new ModelBox(branches_r59,218,70, 0,0, 0,2,8,2,0,false));
            branches_r60 = mkB(art2,-3, -8,-7,-0.5236f,  0,0); branches_r60.cubeList.add(new ModelBox(branches_r60,218,70, 0,0, 0,2,8,2,0,false));

            neck = new ModelRenderer(this); neck.setRotationPoint(0.0F,-22.0F,0.0F); art2.addChild(neck);
            head = new ModelRenderer(this); head.setRotationPoint(0.0F, 0.0F,0.0F);  neck.addChild(head);
            flower = new ModelRenderer(this); flower.setRotationPoint(0.0F,-5.0F,0.0F); head.addChild(flower);

            mouth_north = new ModelRenderer(this); mouth_north.setRotationPoint(0,0,-9); flower.addChild(mouth_north); setRotationAngle(mouth_north,MN_REST_X,0,0); mouth_north.cubeList.add(new ModelBox(mouth_north,72,168,-8,-16,0,16,16,12,0.025f,false));
            mn_articulation  = new ModelRenderer(this); mn_articulation.setRotationPoint(0,-16,0);  mouth_north.addChild(mn_articulation);  setRotationAngle(mn_articulation, MNA_REST_X,0,0); mn_articulation.cubeList.add(new ModelBox(mn_articulation, 144,48,-8,-16,0,16,16,12,0,false));
            mn_articulation2 = new ModelRenderer(this); mn_articulation2.setRotationPoint(0,-16,0); mn_articulation.addChild(mn_articulation2); setRotationAngle(mn_articulation2,MNA2_REST_X,0,0); mn_articulation2.cubeList.add(new ModelBox(mn_articulation2,184,185,-7,-16,0,14,16,12,0,false));
            mouth_north2 = new ModelRenderer(this); mouth_north2.setRotationPoint(0,0,9); flower.addChild(mouth_north2); setRotationAngle(mouth_north2,MN2_REST_X,0,3.1416f); mouth_north2.cubeList.add(new ModelBox(mouth_north2,72,168,-8,-16,0,16,16,12,0.025f,false));
            mn_articulation3 = new ModelRenderer(this); mn_articulation3.setRotationPoint(0,-16,0); mouth_north2.addChild(mn_articulation3);  setRotationAngle(mn_articulation3, MNA_REST_X,0,0); mn_articulation3.cubeList.add(new ModelBox(mn_articulation3,144,48,-8,-16,0,16,16,12,0,false));
            mn_articulation4 = new ModelRenderer(this); mn_articulation4.setRotationPoint(0,-16,0); mn_articulation3.addChild(mn_articulation4); setRotationAngle(mn_articulation4,MNA2_REST_X,0,0); mn_articulation4.cubeList.add(new ModelBox(mn_articulation4,184,185,-7,-16,0,14,16,12,0,false));
            mouth_west = new ModelRenderer(this); mouth_west.setRotationPoint(10,0,0); flower.addChild(mouth_west); setRotationAngle(mouth_west,0,-1.5708f,0.7854f); mouth_west.cubeList.add(new ModelBox(mouth_west,72,168,-8,-16,0,16,16,12,0.025f,false));
            mw_articulation  = new ModelRenderer(this); mw_articulation.setRotationPoint(0,-16,0);  mouth_west.addChild(mw_articulation);  setRotationAngle(mw_articulation, MNA_REST_X,0,0); mw_articulation.cubeList.add(new ModelBox(mw_articulation, 144,48,-8,-16,0,16,16,12,0,false));
            mw_articulation2 = new ModelRenderer(this); mw_articulation2.setRotationPoint(0,-16,0); mw_articulation.addChild(mw_articulation2); setRotationAngle(mw_articulation2,MNA2_REST_X,0,0); mw_articulation2.cubeList.add(new ModelBox(mw_articulation2,184,185,-7,-16,0,14,16,12,0,false));
            mouth_east = new ModelRenderer(this); mouth_east.setRotationPoint(-10,0,0); flower.addChild(mouth_east); setRotationAngle(mouth_east,0,1.5708f,-0.7854f); mouth_east.cubeList.add(new ModelBox(mouth_east,72,168,-8,-16,0,16,16,12,0.025f,false));
            me_articulation  = new ModelRenderer(this); me_articulation.setRotationPoint(0,-16,0);  mouth_east.addChild(me_articulation);  setRotationAngle(me_articulation, MNA_REST_X,0,0); me_articulation.cubeList.add(new ModelBox(me_articulation, 144,48,-8,-16,0,16,16,12,0,false));
            me_articulation2 = new ModelRenderer(this); me_articulation2.setRotationPoint(0,-16,0); me_articulation.addChild(me_articulation2); setRotationAngle(me_articulation2,MNA2_REST_X,0,0); me_articulation2.cubeList.add(new ModelBox(me_articulation2,184,185,-7,-16,0,14,16,12,0,false));
            tongue = new ModelRenderer(this); tongue.setRotationPoint(0,0,0); flower.addChild(tongue);
            tongue.cubeList.add(new ModelBox(tongue,128,185,-7,-14,-7,14,14,14,0,false));
            tongue.cubeList.add(new ModelBox(tongue,200,44,-5,-26,-5,10,12,10,0,false));
            tongue.cubeList.add(new ModelBox(tongue,144,76,-3,-32,-3,6,6,6,0,false));

            upper_leaf  = new ModelRenderer(this); upper_leaf.setRotationPoint( 6,-2,0); head.addChild(upper_leaf);  setRotationAngle(upper_leaf, UL_REST_X, UL_REST_Y, UL_REST_Z);  upper_leaf.cubeList.add(new ModelBox(upper_leaf, 72,196,-1,-24,-8,2,24,16,0,false));
            group17 = new ModelRenderer(this); group17.setRotationPoint(0,-24,0); upper_leaf.addChild(group17);  setRotationAngle(group17, 0,0, 0.5236f); group17.cubeList.add(new ModelBox(group17, 200,66,-1,-8,-7,2,8,14,0,false));
            group18 = new ModelRenderer(this); group18.setRotationPoint(0, -8,0); group17.addChild(group18);     setRotationAngle(group18, 0,0, 0.5236f); group18.cubeList.add(new ModelBox(group18, 108,213,-1,-4,-6,2,4,12,0,false));
            upper_leaf2 = new ModelRenderer(this); upper_leaf2.setRotationPoint(-6,-2,0); head.addChild(upper_leaf2); setRotationAngle(upper_leaf2,UL2_REST_X,UL2_REST_Y,UL2_REST_Z); upper_leaf2.cubeList.add(new ModelBox(upper_leaf2,72,196,-1,-24,-8,2,24,16,0,false));
            group19 = new ModelRenderer(this); group19.setRotationPoint(0,-24,0); upper_leaf2.addChild(group19); setRotationAngle(group19, 0,0,-0.5236f); group19.cubeList.add(new ModelBox(group19, 200,66,-1,-8,-7,2,8,14,0,false));
            group20 = new ModelRenderer(this); group20.setRotationPoint(0, -8,0); group19.addChild(group20);     setRotationAngle(group20, 0,0,-0.5236f); group20.cubeList.add(new ModelBox(group20, 108,213,-1,-4,-6,2,4,12,0,false));
            upper_leaf3 = new ModelRenderer(this); upper_leaf3.setRotationPoint(-1,-2, 5); head.addChild(upper_leaf3); setRotationAngle(upper_leaf3,UL3_REST_X,UL3_REST_Y,UL3_REST_Z); upper_leaf3.cubeList.add(new ModelBox(upper_leaf3,72,196,-1,-24,-8,2,24,16,0,false));
            group27 = new ModelRenderer(this); group27.setRotationPoint(0,-24,0); upper_leaf3.addChild(group27); setRotationAngle(group27, 0,0,-0.5236f); group27.cubeList.add(new ModelBox(group27, 200,66,-1,-8,-7,2,8,14,0,false));
            group28 = new ModelRenderer(this); group28.setRotationPoint(0, -8,0); group27.addChild(group28);     setRotationAngle(group28, 0,0,-0.5236f); group28.cubeList.add(new ModelBox(group28, 108,213,-1,-4,-6,2,4,12,0,false));
            upper_leaf4 = new ModelRenderer(this); upper_leaf4.setRotationPoint(-1,-2,-5); head.addChild(upper_leaf4); setRotationAngle(upper_leaf4,UL4_REST_X,UL4_REST_Y,UL4_REST_Z); upper_leaf4.cubeList.add(new ModelBox(upper_leaf4,72,196,-1,-24,-8,2,24,16,0,false));
            group29 = new ModelRenderer(this); group29.setRotationPoint(0,-24,0); upper_leaf4.addChild(group29); setRotationAngle(group29, 0,0,-0.5236f); group29.cubeList.add(new ModelBox(group29, 200,66,-1,-8,-7,2,8,14,0,false));
            group30 = new ModelRenderer(this); group30.setRotationPoint(0, -8,0); group29.addChild(group30);     setRotationAngle(group30, 0,0,-0.5236f); group30.cubeList.add(new ModelBox(group30, 108,213,-1,-4,-6,2,4,12,0,false));

            leaf4 = new ModelRenderer(this); leaf4.setRotationPoint(-14,-21,-12); stationaryPlant.addChild(leaf4); setRotationAngle(leaf4,0.6761f,-0.4721f,-0.7042f); leaf4.cubeList.add(new ModelBox(leaf4,72,196,-1,-24,-8,2,24,16,0,false));
            group7 = new ModelRenderer(this); group7.setRotationPoint(0,-24,0); leaf4.addChild(group7);  setRotationAngle(group7, 0,0,-0.5236f); group7.cubeList.add(new ModelBox(group7, 200,66,-1,-8,-7,2,8,14,0,false));
            group8 = new ModelRenderer(this); group8.setRotationPoint(0, -8,0); group7.addChild(group8); setRotationAngle(group8, 0,0,-0.5236f); group8.cubeList.add(new ModelBox(group8, 108,213,-1,-4,-6,2,4,12,0,false));
            leaf8 = new ModelRenderer(this); leaf8.setRotationPoint(19,-12,0); stationaryPlant.addChild(leaf8); setRotationAngle(leaf8,LEAF8_REST_X,LEAF8_REST_Y,LEAF8_REST_Z); leaf8.cubeList.add(new ModelBox(leaf8,72,196,-1,-24,-8,2,24,16,0,false));
            group15 = new ModelRenderer(this); group15.setRotationPoint(0,-24,0); leaf8.addChild(group15);  setRotationAngle(group15, 0,0, 0.5236f); group15.cubeList.add(new ModelBox(group15,200,66,-1,-8,-7,2,8,14,0,false));
            group16 = new ModelRenderer(this); group16.setRotationPoint(0, -8,0); group15.addChild(group16); setRotationAngle(group16, 0,0, 0.5236f); group16.cubeList.add(new ModelBox(group16,108,213,-1,-4,-6,2,4,12,0,false));
            leaf7 = new ModelRenderer(this); leaf7.setRotationPoint(-19,-12,0); stationaryPlant.addChild(leaf7); setRotationAngle(leaf7,LEAF7_REST_X,LEAF7_REST_Y,LEAF7_REST_Z); leaf7.cubeList.add(new ModelBox(leaf7,72,196,-1,-24,-8,2,24,16,0,false));
            group13 = new ModelRenderer(this); group13.setRotationPoint(0,-24,0); leaf7.addChild(group13);  setRotationAngle(group13, 0,0,-0.5236f); group13.cubeList.add(new ModelBox(group13,200,66,-1,-8,-7,2,8,14,0,false));
            group14 = new ModelRenderer(this); group14.setRotationPoint(0, -8,0); group13.addChild(group14); setRotationAngle(group14, 0,0,-0.5236f); group14.cubeList.add(new ModelBox(group14,108,213,-1,-4,-6,2,4,12,0,false));
            leaf3 = new ModelRenderer(this); leaf3.setRotationPoint(-14,-21,12); stationaryPlant.addChild(leaf3); setRotationAngle(leaf3,-0.4868f,0.1466f,-1.0768f); leaf3.cubeList.add(new ModelBox(leaf3,72,196,-1,-24,-8,2,24,16,0,false));
            group5 = new ModelRenderer(this); group5.setRotationPoint(0,-24,0); leaf3.addChild(group5);  setRotationAngle(group5, 0,0,-0.5236f); group5.cubeList.add(new ModelBox(group5, 200,66,-1,-8,-7,2,8,14,0,false));
            group6 = new ModelRenderer(this); group6.setRotationPoint(0, -8,0); group5.addChild(group6); setRotationAngle(group6, 0,0,-0.5236f); group6.cubeList.add(new ModelBox(group6, 108,213,-1,-4,-6,2,4,12,0,false));
            leaf2 = new ModelRenderer(this); leaf2.setRotationPoint(14,-21,-12); stationaryPlant.addChild(leaf2); setRotationAngle(leaf2,0.168f,0.1617f,0.9898f); leaf2.cubeList.add(new ModelBox(leaf2,72,196,-1,-24,-8,2,24,16,0,false));
            group3 = new ModelRenderer(this); group3.setRotationPoint(0,-24,0); leaf2.addChild(group3);  setRotationAngle(group3, 0,0, 0.5236f); group3.cubeList.add(new ModelBox(group3, 200,66,-1,-8,-7,2,8,14,0,false));
            group4 = new ModelRenderer(this); group4.setRotationPoint(0, -8,0); group3.addChild(group4); setRotationAngle(group4, 0,0, 0.5236f); group4.cubeList.add(new ModelBox(group4, 108,213,-1,-4,-6,2,4,12,0,false));
            leaf  = new ModelRenderer(this); leaf.setRotationPoint(14,-21,15); stationaryPlant.addChild(leaf);  setRotationAngle(leaf, -0.5208f,-0.6178f,0.7805f); leaf.cubeList.add(new ModelBox(leaf, 72,196,-1,-24,-8,2,24,16,0,false));
            group  = new ModelRenderer(this); group.setRotationPoint(0,-24,0);  leaf.addChild(group);   setRotationAngle(group,  0,0, 0.5236f); group.cubeList.add(new ModelBox(group,  200,66,-1,-8,-7,2,8,14,0,false));
            group2 = new ModelRenderer(this); group2.setRotationPoint(0, -8,0); group.addChild(group2); setRotationAngle(group2, 0,0, 0.5236f); group2.cubeList.add(new ModelBox(group2, 108,213,-1,-4,-6,2,4,12,0,false));
        }

        @Override
        public void setLivingAnimations(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount, float partialTicks) {
            this.animAge = entityIn.ticksExisted + partialTicks;
            if (entityIn instanceof EC) {
                this.attackTimer = ((EC) entityIn).getAttackTimer();
                this.meleeTimer  = ((EC) entityIn).getMeleeTimer();
            }
        }

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            // ---- IDLE VALUES ----
            float tFast = this.animAge * 0.10472f;
            float wave  = 0.5f * (1.0f - MathHelper.cos(tFast));
            float sinT  = MathHelper.sin(tFast);
            float sinH  = MathHelper.sin(tFast * 0.5f);
            float sinS2 = MathHelper.sin(tFast + 0.7f);

            float idle_articulation_X = (-30.0f * RAD) * wave;
            float idle_art_X           = (55.0f * RAD) * wave;
            float idle_art2_X          = (45.0f * RAD) - (5.0f * RAD) * wave;
            float idle_neck_X          = 0.0f;
            float idle_head_X          = (25.0f * RAD) + (7.0f * RAD) * wave;
            float idle_head_Y          = (17.0f * RAD) * wave;
            float idle_head_Z          = (10.0f * RAD) * wave;
            float idle_flower_X        = (25.0f * RAD) * (1.0f - wave);
            float idle_flower_Y        = (-9.0f * RAD) * sinT;
            float idle_flower_Z        = (-5.0f * RAD) * sinT;

            // Mouth petals: only tiny breathing offsets in idle
            float idlePetal = (5.0f * RAD) * wave;
            float idleArt   = (3.0f * RAD) * wave;

            // ---- ATTACK VALUES (Blockbench keyframes, lerped by animation time) ----
            // Attack timer counts down from 50 → 0 over 2.5s; tAnim=(50-timer)/20
            float atk_articulation_X, atk_art_X, atk_art2_X, atk_neck_X;
            float atk_head_X, atk_head_Y, atk_head_Z;
            float atk_flower_X, atk_flower_Y, atk_flower_Z;
            float atk_mn_X, atk_mna_X, atk_mna2_X;
            float atk_mn2_X, atk_mna3_X, atk_mna4_X;
            float atk_mw_X, atk_mwa_X, atk_mwa2_X;
            float atk_me_X, atk_mea_X, atk_mea2_X;

            boolean isAttacking = this.attackTimer > 0;
            float attackBlend = 0.0f;

            if (isAttacking) {
                if (this.attackTimer > 35) {
                    // Heavy spit: 70 ticks (3.5s). Stalk winds up backward, holds flower open, snaps hard.
                    float tAnim = (70 - this.attackTimer) / 20.0f;
                    atk_articulation_X = heavy_articulation_X(tAnim) * RAD;
                    atk_art_X          = heavy_art_X(tAnim)          * RAD;
                    atk_art2_X         = heavy_art2_X(tAnim)         * RAD;
                    atk_neck_X         = heavy_neck_X(tAnim)         * RAD;
                    atk_head_X         = 32.0f * RAD;
                    atk_head_Y         = 0.0f;
                    atk_head_Z         = 0.0f;
                    atk_flower_X       = heavy_flower_X(tAnim)       * RAD;
                    atk_flower_Y       = 0.0f;
                    atk_flower_Z       = 30.45f * RAD;
                    float mX    = heavy_mouth_X(tAnim)  * RAD;
                    float mnaX  = heavy_mna_X(tAnim)    * RAD;
                    float mna2X = heavy_mna2_X(tAnim)   * RAD;
                    atk_mn_X  = mX;  atk_mna_X  = mnaX;  atk_mna2_X  = mna2X;
                    atk_mn2_X = mX;  atk_mna3_X = mnaX;  atk_mna4_X  = mna2X;
                    atk_mw_X  = mX;  atk_mwa_X  = mnaX;  atk_mwa2_X  = mna2X;
                    atk_me_X  = mX;  atk_mea_X  = mnaX;  atk_mea2_X  = mna2X;
                    if (tAnim < 0.25f)       attackBlend = tAnim / 0.25f;
                    else if (tAnim > 3.25f)  attackBlend = Math.max(0.0f, (3.5f - tAnim) / 0.25f);
                    else                     attackBlend = 1.0f;
                } else {
                    // Simple spit: 30 ticks (1.5s). Small forward lean, flower snaps, single thorn.
                    float tAnim = (30 - this.attackTimer) / 20.0f;
                    atk_articulation_X = simple_articulation_X(tAnim) * RAD;
                    atk_art_X          = simple_art_X(tAnim)          * RAD;
                    atk_art2_X         = 40.0f * RAD;
                    atk_neck_X         = simple_neck_X(tAnim)         * RAD;
                    atk_head_X         = 32.0f * RAD;
                    atk_head_Y         = 0.0f;
                    atk_head_Z         = 0.0f;
                    atk_flower_X       = simple_flower_X(tAnim)       * RAD;
                    atk_flower_Y       = 0.0f;
                    atk_flower_Z       = 0.0f;
                    float mX    = simple_mouth_X(tAnim)  * RAD;
                    float mnaX  = simple_mna_X(tAnim)    * RAD;
                    float mna2X = simple_mna2_X(tAnim)   * RAD;
                    atk_mn_X  = mX;  atk_mna_X  = mnaX;  atk_mna2_X  = mna2X;
                    atk_mn2_X = mX;  atk_mna3_X = mnaX;  atk_mna4_X  = mna2X;
                    atk_mw_X  = mX;  atk_mwa_X  = mnaX;  atk_mwa2_X  = mna2X;
                    atk_me_X  = mX;  atk_mea_X  = mnaX;  atk_mea2_X  = mna2X;
                    if (tAnim < 0.1f)       attackBlend = tAnim / 0.1f;
                    else if (tAnim > 1.4f)  attackBlend = Math.max(0.0f, (1.5f - tAnim) / 0.1f);
                    else                    attackBlend = 1.0f;
                }
            } else {
                atk_articulation_X = atk_art_X = atk_art2_X = atk_neck_X = 0;
                atk_head_X = atk_head_Y = atk_head_Z = 0;
                atk_flower_X = atk_flower_Y = atk_flower_Z = 0;
                atk_mn_X = atk_mna_X = atk_mna2_X = 0;
                atk_mn2_X = atk_mna3_X = atk_mna4_X = 0;
                atk_mw_X = atk_mwa_X = atk_mwa2_X = 0;
                atk_me_X = atk_mea_X = atk_mea2_X = 0;
            }

            // ---- MELEE ANIMATION (overrides attack variables when active) ----
            // Reuses the atk_ slots so the compose section below needs no changes.
            if (this.meleeTimer > 0) {
                float tAnim = (40 - this.meleeTimer) / 20.0f;
                atk_articulation_X = melee_articulation_X(tAnim) * RAD;
                atk_art_X          = melee_art_X(tAnim)          * RAD;
                atk_art2_X         = melee_art2_X(tAnim)         * RAD;
                atk_neck_X         = melee_neck_X(tAnim)         * RAD;
                atk_head_X         = melee_head_X(tAnim)         * RAD;
                atk_head_Y         = 0.0f;
                atk_head_Z         = 0.0f;
                atk_flower_X       = melee_flower_X(tAnim)       * RAD;
                atk_flower_Y       = 0.0f;
                atk_flower_Z       = 0.0f;
                float mX    = melee_mouth_X(tAnim)  * RAD;
                float mnaX  = melee_mna_X(tAnim)    * RAD;
                float mna2X = melee_mna2_X(tAnim)   * RAD;
                atk_mn_X  = mX;  atk_mna_X  = mnaX;  atk_mna2_X  = mna2X;
                atk_mn2_X = mX;  atk_mna3_X = mnaX;  atk_mna4_X  = mna2X;
                atk_mw_X  = mX;  atk_mwa_X  = mnaX;  atk_mwa2_X  = mna2X;
                atk_me_X  = mX;  atk_mea_X  = mnaX;  atk_mea2_X  = mna2X;
                if (tAnim < 0.2f)       attackBlend = tAnim / 0.2f;
                else if (tAnim > 1.8f)  attackBlend = Math.max(0.0f, (2.0f - tAnim) / 0.2f);
                else                    attackBlend = 1.0f;
            }

            // ---- COMPOSE IDLE + ATTACK via blend ----
            float b = attackBlend;
            float ib = 1.0f - b;

            articulation.rotateAngleX = idle_articulation_X * ib + atk_articulation_X * b;
            art.rotateAngleX          = idle_art_X          * ib + atk_art_X          * b;
            art2.rotateAngleX         = idle_art2_X         * ib + atk_art2_X         * b;
            neck.rotateAngleX         = idle_neck_X         * ib + atk_neck_X         * b;
            head.rotateAngleX         = idle_head_X         * ib + atk_head_X         * b;
            head.rotateAngleY         = idle_head_Y         * ib + atk_head_Y         * b;
            head.rotateAngleZ         = idle_head_Z         * ib + atk_head_Z         * b;
            flower.rotateAngleX       = idle_flower_X       * ib + atk_flower_X       * b;
            flower.rotateAngleY       = idle_flower_Y       * ib + atk_flower_Y       * b;
            flower.rotateAngleZ       = idle_flower_Z       * ib + atk_flower_Z       * b;

            mouth_north.rotateAngleX      = MN_REST_X   + idlePetal * ib + atk_mn_X   * b;
            mn_articulation.rotateAngleX  = MNA_REST_X  + idleArt   * ib + atk_mna_X  * b;
            mn_articulation2.rotateAngleX = MNA2_REST_X + idleArt   * ib + atk_mna2_X * b;
            mouth_north2.rotateAngleX     = MN2_REST_X  + idlePetal * ib + atk_mn2_X  * b;
            mn_articulation3.rotateAngleX = MNA_REST_X  + idleArt   * ib + atk_mna3_X * b;
            mn_articulation4.rotateAngleX = MNA2_REST_X + idleArt   * ib + atk_mna4_X * b;
            mouth_west.rotateAngleX       =               idlePetal * ib + atk_mw_X   * b;
            mw_articulation.rotateAngleX  = MNA_REST_X  + idleArt   * ib + atk_mwa_X  * b;
            mw_articulation2.rotateAngleX = MNA2_REST_X + idleArt   * ib + atk_mwa2_X * b;
            mouth_east.rotateAngleX       =               idlePetal * ib + atk_me_X   * b;
            me_articulation.rotateAngleX  = MNA_REST_X  + idleArt   * ib + atk_mea_X  * b;
            me_articulation2.rotateAngleX = MNA2_REST_X + idleArt   * ib + atk_mea2_X * b;

            // ---- UPPER HEAD LEAVES (unchanged — just idle ripple) ----
            float leafFast = tFast * 2.0f;
            float PI2 = (float) Math.PI;
            applyUpperLeaf(upper_leaf,  group17, group18, tFast,              leafFast,              UL_REST_X, UL_REST_Y, UL_REST_Z,  0.5236f);
            applyUpperLeaf(upper_leaf2, group19, group20, tFast+PI2*0.5f,     leafFast+PI2*0.5f,     UL2_REST_X,UL2_REST_Y,UL2_REST_Z,-0.5236f);
            applyUpperLeaf(upper_leaf3, group27, group28, tFast+PI2,          leafFast+PI2,          UL3_REST_X,UL3_REST_Y,UL3_REST_Z,-0.5236f);
            applyUpperLeaf(upper_leaf4, group29, group30, tFast+PI2*1.5f,     leafFast+PI2*1.5f,     UL4_REST_X,UL4_REST_Y,UL4_REST_Z,-0.5236f);

            // ---- OUTER GROUND LEAVES (unchanged — just slow rustle) ----
            leaf.rotateAngleZ  = ( 3.0f*RAD)+(19.0f*RAD)*sinH; leaf.rotateAngleX = -0.5208f;
            leaf2.rotateAngleX =  0.168f+(21.0f*RAD)*sinS2; leaf2.rotateAngleY = 0.1617f+(18.0f*RAD)*sinS2; leaf2.rotateAngleZ = 0.9898f+(11.0f*RAD)*sinS2;
            leaf3.rotateAngleZ = ( 3.0f*RAD)-(26.0f*RAD)*sinH;
            leaf4.rotateAngleX =  0.6761f+(-19.0f*RAD)*sinH; leaf4.rotateAngleY = -0.4721f+(7.0f*RAD)*sinH; leaf4.rotateAngleZ = (-3.0f*RAD)-(24.0f*RAD)*sinH;
            leaf5.rotateAngleX = LEAF5_REST_X+(2.0f*RAD)*sinH; leaf5.rotateAngleY = LEAF5_REST_Y+(-25.0f*RAD)*sinH; leaf5.rotateAngleZ = LEAF5_REST_Z+(9.0f*RAD)*sinH;
            leaf6.rotateAngleX = LEAF6_REST_X+(-18.0f*RAD)*sinS2; leaf6.rotateAngleY = LEAF6_REST_Y+(15.0f*RAD)*sinS2; leaf6.rotateAngleZ = LEAF6_REST_Z+(11.0f*RAD)*sinS2;
            leaf7.rotateAngleX = (14.0f*RAD)*sinH; leaf7.rotateAngleY = LEAF7_REST_Y+(7.0f*RAD)*sinH; leaf7.rotateAngleZ = (3.0f*RAD)-(13.0f*RAD)*sinH;
            leaf8.rotateAngleX = LEAF8_REST_X+(-10.0f*RAD)*sinH; leaf8.rotateAngleY = LEAF8_REST_Y+(15.0f*RAD)*sinS2; leaf8.rotateAngleZ = LEAF8_REST_Z+(-5.0f*RAD)*sinH;

            stationaryPlant.render(scale);
        }

        private void applyUpperLeaf(ModelRenderer base, ModelRenderer mid, ModelRenderer tip,
                                    float t, float tFast2, float restX, float restY, float restZ, float tipRestZ) {
            base.rotateAngleX = restX + (5.0f*RAD)*MathHelper.sin(t);
            base.rotateAngleY = restY + (4.0f*RAD)*MathHelper.sin(t-0.5f);
            base.rotateAngleZ = restZ + (5.0f*RAD)*MathHelper.sin(t+0.5f);
            mid.rotateAngleX = 0.0f; mid.rotateAngleZ = tipRestZ + (5.0f*RAD)*MathHelper.sin(tFast2);
            tip.rotateAngleX = 0.0f; tip.rotateAngleZ = tipRestZ + (5.0f*RAD)*MathHelper.sin(tFast2+0.3f);
        }

        private ModelRenderer mkB(ModelRenderer parent, float px, float py, float pz,
                                  float rx, float ry, float rz) {
            ModelRenderer r = new ModelRenderer(this);
            r.setRotationPoint(px, py, pz);
            parent.addChild(r);
            setRotationAngle(r, rx, ry, rz);
            return r;
        }

        private void setRotationAngle(ModelRenderer m, float x, float y, float z) {
            m.rotateAngleX = x; m.rotateAngleY = y; m.rotateAngleZ = z;
        }

        // =====================================================================
        // Blockbench attack keyframes → linear interpolation functions.
        // All values in degrees; times in seconds (0 to 2.5).
        // Source of truth: plant_summonAnimation.attack keyframes.
        // =====================================================================
        // =====================================================================
        // Simple spit — 30 ticks (1.5s). Fires at t=0.5s. 1 thorn.
        // Small forward lean, neck extends, flower opens and snaps, recoils.
        // =====================================================================
        private static float simple_articulation_X(float t) {
            // 0:-30, 0.35:-44, 0.5:-46, 0.75:-35, 1.5:-30
            if (t < 0.35f) return lerp(t, 0f, 0.35f, -30f, -44f);
            if (t < 0.5f)  return lerp(t, 0.35f, 0.5f, -44f, -46f);
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, -46f, -35f);
            return lerp(t, 0.75f, 1.5f, -35f, -30f);
        }
        private static float simple_art_X(float t) {
            // Upper stalk counterbalances — 0:55, 0.35:48, 0.5:46, 0.75:51, 1.5:55
            if (t < 0.35f) return lerp(t, 0f, 0.35f, 55f, 48f);
            if (t < 0.5f)  return lerp(t, 0.35f, 0.5f, 48f, 46f);
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 46f, 51f);
            return lerp(t, 0.75f, 1.5f, 51f, 55f);
        }
        private static float simple_neck_X(float t) {
            // 0:0, 0.25:8, 0.5:0, 1.5:0
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 0f, 8f);
            if (t < 0.5f)  return lerp(t, 0.25f, 0.5f, 8f, 0f);
            return 0f;
        }
        private static float simple_flower_X(float t) {
            // Opens (negative) then snaps shut (positive): 0:0, 0.25:-20, 0.5:22, 0.8:0
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 0f, -20f);
            if (t < 0.5f)  return lerp(t, 0.25f, 0.5f, -20f, 22f);
            if (t < 0.8f)  return lerp(t, 0.5f, 0.8f, 22f, 0f);
            return 0f;
        }
        private static float simple_mouth_X(float t) {
            // 0:5, 0.25:22, 0.5:-8, 0.8:5
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 5f, 22f);
            if (t < 0.5f)  return lerp(t, 0.25f, 0.5f, 22f, -8f);
            if (t < 0.8f)  return lerp(t, 0.5f, 0.8f, -8f, 5f);
            return 5f;
        }
        private static float simple_mna_X(float t) {
            // 0:3, 0.25:25, 0.5:-15, 0.8:3
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 3f, 25f);
            if (t < 0.5f)  return lerp(t, 0.25f, 0.5f, 25f, -15f);
            if (t < 0.8f)  return lerp(t, 0.5f, 0.8f, -15f, 3f);
            return 3f;
        }
        private static float simple_mna2_X(float t) {
            // 0:3, 0.25:15, 0.5:-10, 0.8:3
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 3f, 15f);
            if (t < 0.5f)  return lerp(t, 0.25f, 0.5f, 15f, -10f);
            if (t < 0.8f)  return lerp(t, 0.5f, 0.8f, -10f, 3f);
            return 3f;
        }

        // =====================================================================
        // Heavy spit — 70 ticks (3.5s). Fires at t=1.5s. 3 thorns burst.
        // Big backward wind-up, flower holds wide open, snaps hard forward.
        // =====================================================================
        private static float heavy_articulation_X(float t) {
            // 0:-30, 0.5:-8 (lean back), 1.5:-8 (hold), 1.625:-55 (snap fwd), 2.0:-40, 3.5:-30
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, -30f, -8f);
            if (t < 1.5f)   return -8f;
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, -8f, -55f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, -55f, -40f);
            return lerp(t, 2.0f, 3.5f, -40f, -30f);
        }
        private static float heavy_art_X(float t) {
            // 0:55, 0.5:68, 1.5:68, 1.625:44, 2.0:50, 3.5:55
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 55f, 68f);
            if (t < 1.5f)   return 68f;
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, 68f, 44f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, 44f, 50f);
            return lerp(t, 2.0f, 3.5f, 50f, 55f);
        }
        private static float heavy_art2_X(float t) {
            // 0:40, 0.5:55, 1.5:55, 1.625:32, 2.0:42, 3.5:40
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 40f, 55f);
            if (t < 1.5f)   return 55f;
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, 55f, 32f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, 32f, 42f);
            return lerp(t, 2.0f, 3.5f, 42f, 40f);
        }
        private static float heavy_neck_X(float t) {
            // 0:0, 0.5:5, 1.5:8, 1.625:0, 3.5:0
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 0f, 5f);
            if (t < 1.5f)   return lerp(t, 0.5f, 1.5f, 5f, 8f);
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, 8f, 0f);
            return 0f;
        }
        private static float heavy_flower_X(float t) {
            // Opens wide (negative) during hold, slams shut (positive) at fire.
            // 0:0, 0.5:-15, 1.5:-48, 1.625:35, 2.0:0, 3.5:0
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 0f, -15f);
            if (t < 1.5f)   return lerp(t, 0.5f, 1.5f, -15f, -48f);
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, -48f, 35f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, 35f, 0f);
            return 0f;
        }
        private static float heavy_mouth_X(float t) {
            // 0:5, 0.5:22, 1.5:38, 1.625:-18, 2.0:5
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 5f, 22f);
            if (t < 1.5f)   return lerp(t, 0.5f, 1.5f, 22f, 38f);
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, 38f, -18f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, -18f, 5f);
            return 5f;
        }
        private static float heavy_mna_X(float t) {
            // 0:3, 0.5:25, 1.5:42, 1.625:-20, 2.0:3
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 3f, 25f);
            if (t < 1.5f)   return lerp(t, 0.5f, 1.5f, 25f, 42f);
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, 42f, -20f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, -20f, 3f);
            return 3f;
        }
        private static float heavy_mna2_X(float t) {
            // 0:3, 0.5:15, 1.5:25, 1.625:-12, 2.0:3
            if (t < 0.5f)   return lerp(t, 0f, 0.5f, 3f, 15f);
            if (t < 1.5f)   return lerp(t, 0.5f, 1.5f, 15f, 25f);
            if (t < 1.625f) return lerp(t, 1.5f, 1.625f, 25f, -12f);
            if (t < 2.0f)   return lerp(t, 1.625f, 2.0f, -12f, 3f);
            return 3f;
        }

        // =====================================================================
        // Melee lunge keyframes — fast territorial snap when targets get close.
        // Timer 40→0 over 2.0s; damage fires at t=0.75s (the snap moment).
        // Phase 1 (0–0.5s): hard lunge forward. Phase 2 (0.5–0.75s): head snaps.
        // Phase 3 (0.75–2.0s): recoil back to idle.
        // =====================================================================
        private static float melee_articulation_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, -30f, -90f);
            if (t < 0.5f)  return -90f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, -90f, -30f);
            return -30f;
        }
        private static float melee_art_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 55f, 5f);
            if (t < 0.5f)  return 5f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 5f, 65f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, 65f, 55f);
            return 55f;
        }
        private static float melee_art2_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 40f, 10f);
            if (t < 0.5f)  return 10f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 10f, 50f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, 50f, 40f);
            return 40f;
        }
        private static float melee_neck_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 0f, 30f);
            if (t < 0.5f)  return 30f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 30f, 0f);
            return 0f;
        }
        private static float melee_head_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 32f, -30f);
            if (t < 0.5f)  return -30f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, -30f, 55f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, 55f, 32f);
            return 32f;
        }
        private static float melee_flower_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 0f, -40f);
            if (t < 0.5f)  return -40f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, -40f, 50f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, 50f, 0f);
            return 0f;
        }
        private static float melee_mouth_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 5f, 40f);
            if (t < 0.5f)  return 40f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 40f, -35f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, -35f, 5f);
            return 5f;
        }
        private static float melee_mna_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 3f, 25f);
            if (t < 0.5f)  return 25f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 25f, 55f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, 55f, 3f);
            return 3f;
        }
        private static float melee_mna2_X(float t) {
            if (t < 0.25f) return lerp(t, 0f, 0.25f, 3f, 15f);
            if (t < 0.5f)  return 15f;
            if (t < 0.75f) return lerp(t, 0.5f, 0.75f, 15f, 30f);
            if (t < 1.25f) return lerp(t, 0.75f, 1.25f, 30f, 3f);
            return 3f;
        }

        private static float lerp(float t, float t0, float t1, float v0, float v1) {
            float a = (t - t0) / (t1 - t0);
            return v0 + (v1 - v0) * a;
        }
    }
}