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
public class EntityStationaryPlant extends ElementsNarutomodMod.ModElement {

    public static final int ENTITYID = 350;
    public static final int ENTITYID_THORN = 351;

    public EntityStationaryPlant(ElementsNarutomodMod instance) {
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

        // Head segment — positioned at flower head for a proper hitbox there.
        private final MultiPartEntityPart headPart;

        // Target of the current attack — locked in when attackEntityWithRangedAttack
        // is called, fired once at bite-snap moment. Cleared after firing.
        private EntityLivingBase pendingTarget;
        private boolean firedThisAttack;

        @Override
        public void entityInit() {
            super.entityInit();
            this.dataManager.register(ATTACK_TIMER, 0);
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
                    // Fire the thorn exactly once at the bite-snap moment.
                    // Timer 17 = Blockbench t=1.625s = jaws clamp shut. This
                    // lines up particle launch with the visual snap.
                    if (!this.firedThisAttack && timer - 1 == 17 && this.pendingTarget != null) {
                        this.fireThorn(this.pendingTarget);
                        this.firedThisAttack = true;
                    }
                }
                // Clean up stale target reference once attack finishes.
                if (timer <= 0 && this.pendingTarget != null) {
                    this.pendingTarget = null;
                    this.firedThisAttack = false;
                }
            }

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
        private void fireThorn(EntityLivingBase target) {
            final float THORN_SIZE_MULTIPLIER = 2.2f;
            final float SPREAD_DEGREES = 5.0f; // cone half-angle for extra thorns

            float f = this.getScale();
            double spawnX = this.posX;
            double spawnY = this.posY + 3.5 * f;
            double spawnZ = this.posZ;

            // Lead target based on motion
            double leadTx = target.posX + target.motionX * 4.0;
            double leadTy = target.posY + target.height * 0.5;
            double leadTz = target.posZ + target.motionZ * 4.0;

            // Roll count: 50% → 1, 30% → 2, 15% → 3, 5% → 4
            int thornCount;
            float roll = this.rand.nextFloat();
            if      (roll < 0.50f) thornCount = 1;
            else if (roll < 0.80f) thornCount = 2;
            else if (roll < 0.95f) thornCount = 3;
            else                   thornCount = 4;

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
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D * f);
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
                // 50 ticks = 2.5s Blockbench attack animation length (20 ticks/s).
                // Thorn fires once at tick 33 (timer=17) — see onUpdate().
                this.dataManager.set(ATTACK_TIMER, 50);
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
            double impactX = this.posX;
            double impactY = this.posY;
            double impactZ = this.posZ;

            // Direct hit bonus damage
            if (hit != null && hit.entityHit instanceof EntityLivingBase && hit.entityHit != shooter) {
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
            EntityStationaryPlant.EC entity1 = new EntityStationaryPlant.EC(entity, power);
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
        private int   attackTimer = 0;

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
            branches_r5  = mkB(art,-6,-17, 9, 0.4363f,0,0); branches_r5.cubeL... (39 KB left)
