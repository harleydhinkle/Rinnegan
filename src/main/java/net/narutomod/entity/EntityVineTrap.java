package com.leolifeless.shinobiaddon.entity.vine;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * ==========================================
 * ENTITY VINE TRAP - HOW IT WORKS
 * ==========================================
 *
 * This jutsu creates vines that:
 * 1. Spawn 2-3 blocks AWAY from the target (in a circle around them)
 * 2. Grow segment-by-segment TOWARD the target (like a snake)
 * 3. Wrap AROUND the target once they reach it
 * 4. BIND the target in place and deal damage over time
 *
 * STRUCTURE:
 * - Each vine strand is a CHAIN of small segment entities
 * - The first segment (index 0) is the "root" that controls growth
 * - New segments spawn at the end of the chain each tick
 * - Each segment is rotated slightly differently, creating curves
 *
 */
public class EntityVineTrap {

    // Unique entity ID for registration (pick one that doesn't conflict with other entities)
    public static final int ENTITYID = 9501;

    /**
     * ==========================================
     * VINE SEGMENT - BASE CLASS
     * ==========================================
     *
     * This is the building block for vines. Each segment is a small piece
     * that connects to other segments to form a chain.
     *
     * Think of it like a chain of paper clips - each one attaches to the next,
     * and together they form a flexible line.
     *
     * KEY CONCEPTS:
     * - "Parent" = The root entity that anchors the whole chain (usually itself for root)
     * - "Offset" = Position/rotation relative to the parent
     * - "Index" = How far along the chain this segment is (0 = root, 1 = first child, etc.)
     */
    public static abstract class VineSegment extends Entity {

        // ========== DATA PARAMETERS ==========
        // These sync data between server and client

        // ID of the parent entity this segment is attached to
        protected static final DataParameter<Integer> PARENT_ID = EntityDataManager.createKey(VineSegment.class, DataSerializers.VARINT);

        // Position offset from parent (where this segment sits relative to parent)
        protected static final DataParameter<Float> OFFSET_X = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);
        protected static final DataParameter<Float> OFFSET_Y = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);
        protected static final DataParameter<Float> OFFSET_Z = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);

        // Rotation offset from parent
        // YAW = horizontal rotation (turning left/right, like a compass)
        // PITCH = vertical rotation (tilting up/down)
        protected static final DataParameter<Float> OFFSET_YAW = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);
        protected static final DataParameter<Float> OFFSET_PITCH = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);

        // Which segment in the chain this is (0 = root, 1 = first child, etc.)
        protected static final DataParameter<Integer> SEG_IDX = EntityDataManager.createKey(VineSegment.class, DataSerializers.VARINT);

        // Size of this segment (for rendering)
        protected static final DataParameter<Float> WIDTH = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);
        protected static final DataParameter<Float> HEIGHT = EntityDataManager.createKey(VineSegment.class, DataSerializers.FLOAT);

        // For tracking position changes
        private double lastX;
        private double lastY;
        private double lastZ;

        // List of segments that branch off from this one (usually just one - the next in chain)
        protected final List<VineSegment> nextSegments;

        // The segment that came before this one in the chain
        protected VineSegment prevSegment;

        /**
         * Basic constructor - just creates an empty segment
         */
        public VineSegment(World worldIn) {
            super(worldIn);
            this.nextSegments = Lists.newArrayList();
            // Vines are thin! 0.15 blocks wide, 0.3 blocks tall
            this.setSize(0.15F, 0.3F);
        }

        /**
         * Constructor for creating a new segment attached to an existing one.
         * This is the main way new segments are added to the chain.
         *
         * @param segment The segment to attach to (the "previous" segment)
         * @param yawOffset How much to turn left/right from the previous segment's direction
         * @param pitchOffset How much to tilt up/down from the previous segment's direction
         *
         * EXAMPLE:
         * If previous segment faces North and you pass yawOffset=10, new segment faces slightly NE
         * If previous segment is horizontal and you pass pitchOffset=-5, new segment tilts up slightly
         */
        public VineSegment(VineSegment segment, float yawOffset, float pitchOffset) {
            // Spawn at the END of the previous segment (at its "height" position)
            // The 0.05F subtraction makes segments overlap slightly for smooth visuals
            this(segment, 0.0D, (double)(segment.height - 0.05F), 0.0D, yawOffset, pitchOffset);
        }

        /**
         * Full constructor with custom offset position.
         * Calculates where this segment should be based on the previous segment's position and rotation.
         */
        public VineSegment(VineSegment segment, double offsetX, double offsetY, double offsetZ, float yawOffset, float pitchOffset) {
            this(segment.world);

            // Get the previous segment's rotation
            ProcedureUtils.Vec2f vec2f = segment.getOffsetRotation();

            // Calculate position: take the offset and rotate it to match the previous segment's facing
            // This is like saying "move forward 1 block" but "forward" depends on which way you're facing
            Vec3d vec = (new Vec3d(offsetX, offsetY, offsetZ))
                    .rotatePitch(-vec2f.y * 0.017453292F)   // Rotate by pitch (0.0174... converts degrees to radians)
                    .rotateYaw(-vec2f.x * 0.017453292F)     // Rotate by yaw
                    .add(segment.getOffsetPosition());       // Add to previous segment's position

            // Set this segment's offset (position + rotation relative to root parent)
            this.setOffset(vec.x, vec.y, vec.z, vec2f.x + yawOffset, vec2f.y + pitchOffset);

            // Same parent as previous segment (all segments share the root as parent)
            this.setParent(segment.getParent());

            // Update actual world position based on offset
            this.setPositionAndRotationFromParent(1.0F);

            // This segment is one further in the chain
            this.setIndex(segment.getIndex() + 1);

            // Link segments together
            segment.nextSegments.add(this);
            this.prevSegment = segment;
        }

        /**
         * Initialize all the synced data with default values
         */
        @Override
        protected void entityInit() {
            this.dataManager.register(PARENT_ID, -1);
            this.dataManager.register(OFFSET_X, 0.0F);
            this.dataManager.register(OFFSET_Y, 0.0F);
            this.dataManager.register(OFFSET_Z, 0.0F);
            this.dataManager.register(OFFSET_YAW, 0.0F);
            this.dataManager.register(OFFSET_PITCH, 0.0F);
            this.dataManager.register(SEG_IDX, 0);
            this.dataManager.register(WIDTH, 0.15F);
            this.dataManager.register(HEIGHT, 0.3F);
            this.setSize(0.15F, 0.3F);
        }

        /**
         * Set which entity is the "anchor" for this segment chain
         */
        protected void setParent(Entity entity) {
            this.dataManager.set(PARENT_ID, entity.getEntityId());
        }

        /**
         * Get the parent entity (the anchor)
         */
        @Nullable
        protected Entity getParent() {
            return this.world.getEntityByID(this.dataManager.get(PARENT_ID));
        }

        /**
         * Set position and rotation offset from parent
         */
        protected void setOffset(double x, double y, double z, float yaw, float pitch) {
            this.dataManager.set(OFFSET_X, (float) x);
            this.dataManager.set(OFFSET_Y, (float) y);
            this.dataManager.set(OFFSET_Z, (float) z);
            this.dataManager.set(OFFSET_YAW, yaw);
            this.dataManager.set(OFFSET_PITCH, pitch);
        }

        /**
         * Get position offset as a vector
         */
        protected Vec3d getOffsetPosition() {
            return new Vec3d(
                    this.dataManager.get(OFFSET_X),
                    this.dataManager.get(OFFSET_Y),
                    this.dataManager.get(OFFSET_Z)
            );
        }

        /**
         * Get rotation offset (yaw = x, pitch = y)
         */
        protected ProcedureUtils.Vec2f getOffsetRotation() {
            return new ProcedureUtils.Vec2f(
                    this.dataManager.get(OFFSET_YAW),
                    this.dataManager.get(OFFSET_PITCH)
            );
        }

        /**
         * Set which number in the chain this segment is
         */
        protected void setIndex(int index) {
            this.dataManager.set(SEG_IDX, index);
        }

        /**
         * Get which number in the chain this segment is
         */
        public int getIndex() {
            return this.dataManager.get(SEG_IDX);
        }

        /**
         * Update this segment's actual world position based on its offset from parent.
         * Called every frame for smooth rendering.
         *
         * @param partialTicks For smooth interpolation between ticks (0.0 to 1.0)
         */
        public void setPositionAndRotationFromParent(float partialTicks) {
            Entity parent = this.getParent();
            if (parent != null) {
                Vec3d offset = this.getOffsetPosition();
                ProcedureUtils.Vec2f rotation = this.getOffsetRotation();

                // Interpolate parent position for smooth movement
                double px = parent.lastTickPosX + (parent.posX - parent.lastTickPosX) * partialTicks;
                double py = parent.lastTickPosY + (parent.posY - parent.lastTickPosY) * partialTicks;
                double pz = parent.lastTickPosZ + (parent.posZ - parent.lastTickPosZ) * partialTicks;

                // Final position = parent position + offset
                Vec3d pos = offset.add(px, py, pz);
                this.setPosition(pos.x, pos.y, pos.z);
                this.rotationYaw = rotation.x;
                this.rotationPitch = rotation.y;
            }
        }

        @Override
        public void setSize(float width, float height) {
            super.setSize(width, height);
            this.dataManager.set(WIDTH, width);
            this.dataManager.set(HEIGHT, height);
        }

        // Vines don't block movement or get pushed around
        @Override
        public boolean canBeCollidedWith() {
            return false;
        }

        @Override
        public boolean canBePushed() {
            return false;
        }

        // No need to save/load - vines are temporary
        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {
        }

        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {
        }
    }

    /**
     * ==========================================
     * EC - THE ACTUAL VINE TRAP ENTITY
     * ==========================================
     *
     * This extends VineSegment and adds all the "smart" behavior:
     * - Tracking the target
     * - Growing toward the target
     * - Wrapping around when close
     * - Binding and damaging
     *
     * LIFECYCLE:
     * 1. Jutsu creates multiple EC entities around target (each is a vine strand root)
     * 2. Each root (index 0) spawns child segments every tick
     * 3. Children are aimed toward target, creating curved growth
     * 4. When segments reach target, they switch to wrapping mode
     * 5. After enough wrapping, target gets bound
     * 6. Bound target can't move and takes damage
     * 7. After lifespan expires, all segments die
     */
    public static class EC extends VineSegment implements ItemJutsu.IJutsu {

        // How long this vine lives (in ticks, 20 ticks = 1 second)
        private int lifespan;

        // Reference to the LAST segment in this vine's chain
        // This is where new segments get added
        private EC lastSegment;

        // The entity we're trying to capture
        private Entity target;

        // Once captured, this stores where we locked the target
        // Target gets teleported here every tick to keep them bound
        private Vec3d targetVec;

        // Counts how many segments have reached the target
        // Used to determine when to "capture" them
        private int reachedCount;

        // Flag: have we successfully captured the target?
        private boolean hasReachedTarget;

        // ========== SCALED VALUES (calculated based on target size) ==========
        // These are set when the vine is created and used throughout its lifetime

        // How many segments must reach target to capture (bigger target = more needed)
        private int scaledCaptureThreshold;

        // How long to grow (bigger target = longer growth)
        private int scaledGrowthTicks;

        // Distance to consider "reached" (bigger target = larger reach distance)
        private float scaledReachDistance;

        // Distance where vine starts rising toward target
        private float scaledApproachDistance;

        // ========== CONFIGURATION ==========
        // Tweak these BASE values to change vine behavior!
        // These get SCALED based on target entity size

        // Base spawn distance (will be multiplied by target width)
        // Final distance = BASE + (target.width * SCALE_FACTOR)
        private static final float SPAWN_DISTANCE_BASE = 1.5F;
        private static final float SPAWN_DISTANCE_PER_WIDTH = 1.0F;  // Add 1 block per 1 width
        private static final float SPAWN_DISTANCE_RANDOM = 1.0F;    // Random extra 0-1 blocks

        // Base capture threshold (will be increased for larger targets)
        // Bigger targets need more vine segments to fully capture
        private static final int CAPTURE_THRESHOLD_BASE = 3;
        private static final float CAPTURE_THRESHOLD_PER_WIDTH = 3.0F;  // +3 segments per 1 width

        // Base vine count (will be increased for larger targets)
        private static final int VINE_COUNT_BASE = 5;
        private static final float VINE_COUNT_PER_WIDTH = 4.0F;  // +4 vines per 1 width
        private static final int VINE_COUNT_MAX = 16;

        // Growth duration scaling (bigger targets = longer growth time)
        private static final int GROWTH_TICKS_BASE = 40;
        private static final float GROWTH_TICKS_PER_WIDTH = 20.0F;  // +20 ticks (1 sec) per 1 width
        private static final int GROWTH_TICKS_MAX = 120;  // Max 6 seconds of growth

        // How close vine must be to "reach" target (scaled by target width)
        private static final float REACH_DISTANCE_FACTOR = 0.75F;  // 75% of target width
        private static final float REACH_DISTANCE_MIN = 1.0F;      // At least 1 block

        // Wrap height - how high vines climb (scaled by target height)
        private static final float WRAP_HEIGHT_FACTOR = 0.8F;  // Wrap up to 80% of target height

        /**
         * Basic constructor - empty vine
         */
        public EC(World world) {
            super(world);
            this.lifespan = 300; // 15 seconds
            this.reachedCount = 0;
            this.hasReachedTarget = false;

            // Default scaled values (will be overwritten when target is set)
            this.scaledCaptureThreshold = CAPTURE_THRESHOLD_BASE;
            this.scaledGrowthTicks = GROWTH_TICKS_BASE;
            this.scaledReachDistance = REACH_DISTANCE_MIN;
            this.scaledApproachDistance = REACH_DISTANCE_MIN + 1.0F;
        }

        /**
         * Main constructor - creates a vine strand that will grow toward target.
         *
         * @param caster Who cast the jutsu (not used much, but good to have)
         * @param targetIn The entity to capture
         * @param spawnOffset Where to spawn relative to target (e.g., 3 blocks north)
         *
         * HOW IT WORKS:
         * 1. Calculate spawn position = target position + offset
         * 2. Find ground level at spawn position
         * 3. Calculate which direction faces the target
         * 4. Set pitch to nearly horizontal (85°) so vine grows along ground
         * 5. Calculate all scaled values based on target size
         */
        public EC(EntityLivingBase caster, Entity targetIn, Vec3d spawnOffset) {
            this(targetIn.world);

            // This segment is its own parent (it's the root of the chain)
            this.setParent(this);
            this.target = targetIn;

            // === CALCULATE SCALED VALUES BASED ON TARGET SIZE ===
            // Bigger targets need more vines, longer growth, etc.
            float targetWidth = targetIn.width;
            float targetHeight = targetIn.height;

            this.scaledCaptureThreshold = getScaledCaptureThreshold(targetWidth);
            this.scaledGrowthTicks = getScaledGrowthTicks(targetWidth);
            this.scaledReachDistance = getScaledReachDistance(targetWidth);
            this.scaledApproachDistance = this.scaledReachDistance + 1.0F + (targetWidth * 0.5F);

            // Log for debugging (remove in production)
            // System.out.println("Vine targeting entity with width=" + targetWidth + ", height=" + targetHeight);
            // System.out.println("  Capture threshold: " + scaledCaptureThreshold);
            // System.out.println("  Growth ticks: " + scaledGrowthTicks);
            // System.out.println("  Reach distance: " + scaledReachDistance);

            // === CALCULATE SPAWN POSITION ===
            // Start at target, add offset, find ground level
            Vec3d targetPos = targetIn.getPositionVector();
            Vec3d spawnPos = targetPos.add(spawnOffset);
            int groundY = ProcedureUtils.getTopSolidBlockY(this.world, new BlockPos(spawnPos));

            // Spawn slightly above ground (0.1 blocks) so we don't clip into it
            this.setPosition(spawnPos.x, groundY + 0.1D, spawnPos.z);

            // === CALCULATE FACING DIRECTION ===
            // We want to face TOWARD the target
            // getYawFromVec converts a direction vector into a yaw angle
            float yawToTarget = ProcedureUtils.getYawFromVec(targetPos.subtract(spawnPos));

            // === SET INITIAL ROTATION ===
            // Yaw = face toward target
            // Pitch = 85° = nearly horizontal (90° would be perfectly flat)
            // This makes the vine grow ALONG the ground, not upward
            this.setOffset(0, 0, 0, yawToTarget, 85.0F);
            this.setPositionAndRotationFromParent(1.0F);

            // This is the last segment so far (it's also the first!)
            this.lastSegment = this;

            // === SCALE VINE THICKNESS BASED ON TARGET SIZE ===
            // Bigger targets get slightly thicker vines
            float baseWidth = 0.12F + this.rand.nextFloat() * 0.06F;
            float baseHeight = 0.28F + this.rand.nextFloat() * 0.08F;
            float sizeMultiplier = 1.0F + (targetWidth - 0.6F) * 0.3F;  // 0.6 is player width
            sizeMultiplier = MathHelper.clamp(sizeMultiplier, 0.8F, 2.0F);

            this.setSize(baseWidth * sizeMultiplier, baseHeight * sizeMultiplier);
        }

        /**
         * Constructor for child segments (segments spawned during growth).
         * Just copies state from parent and applies rotation offset.
         */
        public EC(EC segment, float yawOffset, float pitchOffset) {
            super(segment, yawOffset, pitchOffset);
            this.lifespan = 300;
            // Copy target info from parent segment
            this.target = segment.target;
            this.targetVec = segment.targetVec;
            this.reachedCount = segment.reachedCount;
            this.hasReachedTarget = segment.hasReachedTarget;

            // Copy scaled values from parent (all segments in a chain share the same scaling)
            this.scaledCaptureThreshold = segment.scaledCaptureThreshold;
            this.scaledGrowthTicks = segment.scaledGrowthTicks;
            this.scaledReachDistance = segment.scaledReachDistance;
            this.scaledApproachDistance = segment.scaledApproachDistance;
        }

        @Override
        public ItemJutsu.JutsuEnum.Type getJutsuType() {
            return ItemJutsu.JutsuEnum.Type.MOKUTON;
        }

        private void setLifespan(int ticks) {
            this.lifespan = ticks;
        }

        /**
         * ==========================================
         * MAIN UPDATE LOOP - CALLED EVERY TICK
         * ==========================================
         *
         * This is where all the magic happens!
         * The root segment (index 0) controls growth by spawning new segments.
         */
        @Override
        public void onUpdate() {
            super.onUpdate();

            // === SOUND EFFECTS ===
            // Play vine/grass sound occasionally when growing
            if (this.ticksExisted == 1 && this.rand.nextFloat() < 0.1F) {
                this.playSound(
                        SoundEvent.REGISTRY.getObject(new ResourceLocation("block.grass.break")),
                        0.5F,
                        this.rand.nextFloat() * 0.3F + 0.7F
                );
            }

            // === CHECK IF STILL ALIVE ===
            if (this.getParent() != null && this.ticksExisted < this.lifespan) {

                // ===========================================
                // GROWTH LOGIC - ONLY ROOT SEGMENT DOES THIS
                // ===========================================
                // Only index 0 (the root) spawns new segments
                // Growth duration is scaled based on target size
                if (!this.world.isRemote && this.getIndex() == 0 && this.ticksExisted > 0 && this.ticksExisted <= this.scaledGrowthTicks) {

                    // These will determine how the next segment is rotated
                    float yawAdjust = (this.rand.nextFloat() - 0.5F) * 15.0F;  // Random wiggle by default
                    float pitchAdjust = 0.0F;

                    // Get current position of the last segment in our chain
                    int idx = this.lastSegment.getIndex();

                    // === TARGETING LOGIC ===
                    if (this.hasLivingTarget() && !this.hasReachedTarget) {

                        // Where is the last segment?
                        Vec3d segmentPos = this.lastSegment.getPositionVector();

                        // Where is the target? (aim for their center, not feet)
                        Vec3d targetPos = this.target.getPositionVector().add(0, this.target.height * 0.5, 0);

                        // How far away is the target?
                        double distanceToTarget = segmentPos.distanceTo(targetPos);

                        // === CALCULATE TURN AMOUNT ===
                        // What direction do we NEED to face?
                        float yawToTarget = ProcedureUtils.getYawFromVec(targetPos.subtract(segmentPos));

                        // What direction are we CURRENTLY facing?
                        // The difference tells us how much to turn
                        yawAdjust = MathHelper.wrapDegrees(yawToTarget - this.lastSegment.rotationYaw);

                        // Limit how sharply we can turn (max 25° per segment)
                        // This creates smooth curves instead of sharp angles
                        yawAdjust = MathHelper.clamp(yawAdjust, -25.0F, 25.0F);

                        // Add a little randomness so vines don't look robotic
                        yawAdjust += (this.rand.nextFloat() - 0.5F) * 8.0F;

                        // === DISTANCE-BASED BEHAVIOR ===
                        // Vines act differently based on how close they are to target
                        // Distances are SCALED based on target size!

                        if (distanceToTarget < this.scaledReachDistance) {
                            // -----------------------------------------
                            // CLOSE TO TARGET - START WRAPPING UPWARD
                            // -----------------------------------------
                            // Negative pitch = tilt upward
                            // Pitch adjustment scales slightly with target height
                            float heightFactor = 1.0F + (this.target.height - 1.8F) * 0.2F;  // 1.8 is player height
                            pitchAdjust = (-8.0F - this.rand.nextFloat() * 5.0F) * MathHelper.clamp(heightFactor, 0.8F, 1.5F);

                            // Count this as a "reached" segment
                            this.reachedCount++;

                            // After enough segments reach, CAPTURE the target!
                            // Threshold is SCALED based on target size
                            if (this.reachedCount >= this.scaledCaptureThreshold && this.targetVec == null) {
                                // Lock in their current position
                                this.targetVec = this.target.getPositionVector();
                                this.hasReachedTarget = true;

                                // Play a sound to indicate capture
                                this.world.playSound(null, this.target.getPosition(),
                                        SoundEvent.REGISTRY.getObject(new ResourceLocation("block.grass.place")),
                                        SoundCategory.PLAYERS, 0.8F, 0.5F);
                            }

                        } else if (distanceToTarget < this.scaledApproachDistance) {
                            // -----------------------------------------
                            // APPROACHING - START RISING SLIGHTLY
                            // -----------------------------------------
                            // Prepare to wrap by tilting up a little
                            pitchAdjust = -3.0F - this.rand.nextFloat() * 2.0F;

                        } else {
                            // -----------------------------------------
                            // FAR AWAY - STAY LOW, SNAKE ALONG GROUND
                            // -----------------------------------------
                            // Small random pitch changes create a slithering look
                            pitchAdjust = (this.rand.nextFloat() - 0.5F) * 4.0F;
                        }

                    } else if (this.hasReachedTarget) {
                        // -----------------------------------------
                        // ALREADY CAPTURED - SPIRAL AROUND TARGET
                        // -----------------------------------------
                        // Keep adding segments that wrap around
                        // Large yaw = tight spiral (20-35° turn per segment)
                        yawAdjust = 20.0F + this.rand.nextFloat() * 15.0F;

                        // Keep climbing upward
                        pitchAdjust = -5.0F - this.rand.nextFloat() * 8.0F;
                    }

                    // === SPAWN THE NEXT SEGMENT ===
                    // Create a new segment at the end of the chain with calculated rotation
                    this.lastSegment = new EC(this.lastSegment, yawAdjust, pitchAdjust);
                    this.lastSegment.setSize(this.width, this.height);
                    this.lastSegment.setLifespan(this.lifespan - this.ticksExisted);

                    // Pass along our state so all segments know if target is captured
                    this.lastSegment.reachedCount = this.reachedCount;
                    this.lastSegment.hasReachedTarget = this.hasReachedTarget;
                    this.lastSegment.targetVec = this.targetVec;

                    // Scaled values are already copied by the constructor, but update these just in case
                    this.lastSegment.scaledCaptureThreshold = this.scaledCaptureThreshold;
                    this.lastSegment.scaledGrowthTicks = this.scaledGrowthTicks;
                    this.lastSegment.scaledReachDistance = this.scaledReachDistance;
                    this.lastSegment.scaledApproachDistance = this.scaledApproachDistance;

                    // Add to world
                    this.world.spawnEntity(this.lastSegment);
                }

                // ===========================================
                // BINDING EFFECT - HOLD TARGET IN PLACE
                // ===========================================
                // If we've captured the target, keep them bound
                if (this.targetVec != null && this.targetTargetable()) {

                    // Deal damage periodically (every 25 ticks = 1.25 seconds)
                    if (this.ticksExisted > 30 && this.ticksExisted % 25 == 0) {
                        this.target.attackEntityFrom(
                                ItemJutsu.causeJutsuDamage(this, null).setDamageBypassesArmor(),
                                2.5F  // Damage amount
                        );
                    }

                    // Teleport target back to locked position (they can't escape!)
                    this.target.setPositionAndUpdate(this.targetVec.x, this.targetVec.y, this.targetVec.z);

                    // Stop all movement
                    this.target.motionX = 0;
                    this.target.motionY = 0;
                    this.target.motionZ = 0;
                }

            } else if (!this.world.isRemote) {
                // Lifespan over or parent gone - die
                this.setDead();
            }
        }

        /**
         * Check if we have a living target
         */
        private boolean hasLivingTarget() {
            return this.target != null && this.target.isEntityAlive();
        }

        /**
         * Check if target can still be affected (not in creative, etc.)
         */
        private boolean targetTargetable() {
            if (!ItemJutsu.canTarget(this.target)) {
                this.target = null;
                return false;
            }
            return true;
        }

        // ========== SIZE SCALING HELPER METHODS ==========

        /**
         * Calculate spawn distance based on target size.
         * Bigger targets = vines spawn further away.
         *
         * @param targetWidth The target's width
         * @param random World random for variation
         * @return Distance in blocks from target to spawn vines
         */
        private static float getScaledSpawnDistance(float targetWidth, java.util.Random random) {
            float distance = SPAWN_DISTANCE_BASE + (targetWidth * SPAWN_DISTANCE_PER_WIDTH);
            distance += random.nextFloat() * SPAWN_DISTANCE_RANDOM;
            return distance;
        }

        /**
         * Calculate how many vines to spawn based on target size.
         * Bigger targets = more vines needed to surround them.
         *
         * @param targetWidth The target's width
         * @return Number of vine strands to spawn
         */
        private static int getScaledVineCount(float targetWidth) {
            int count = VINE_COUNT_BASE + (int)(targetWidth * VINE_COUNT_PER_WIDTH);
            return Math.min(count, VINE_COUNT_MAX);
        }

        /**
         * Calculate capture threshold based on target size.
         * Bigger targets = need more vine segments to fully bind them.
         *
         * @param targetWidth The target's width
         * @return Number of segments needed to capture
         */
        private static int getScaledCaptureThreshold(float targetWidth) {
            return CAPTURE_THRESHOLD_BASE + (int)(targetWidth * CAPTURE_THRESHOLD_PER_WIDTH);
        }

        /**
         * Calculate growth duration based on target size.
         * Bigger targets = vines need longer to reach and wrap them.
         *
         * @param targetWidth The target's width
         * @return Number of ticks the vine should grow
         */
        private static int getScaledGrowthTicks(float targetWidth) {
            int ticks = GROWTH_TICKS_BASE + (int)(targetWidth * GROWTH_TICKS_PER_WIDTH);
            return Math.min(ticks, GROWTH_TICKS_MAX);
        }

        /**
         * Calculate the distance at which a vine is considered to have "reached" the target.
         * Bigger targets = can be reached from further away.
         *
         * @param targetWidth The target's width
         * @return Distance in blocks
         */
        private static float getScaledReachDistance(float targetWidth) {
            float distance = targetWidth * REACH_DISTANCE_FACTOR;
            return Math.max(distance, REACH_DISTANCE_MIN);
        }

        /**
         * ==========================================
         * JUTSU CALLBACK - ACTIVATES THE JUTSU
         * ==========================================
         *
         * Called when player right-clicks with the jutsu item.
         * Spawns multiple vine strands in a circle around the target.
         * Number of vines and spawn distance SCALE with target size!
         */
        public static class Jutsu implements ItemJutsu.IJutsuCallback {
            @Override
            public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {

                // === FIND TARGET ===
                // Look for a living entity within 20 blocks, 3 block search radius
                RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 20.0D, 3.0D, false, false,
                        (Predicate<Entity>) target -> target instanceof EntityLivingBase && target != entity
                );

                // Did we find someone?
                if (res != null && res.entityHit != null) {
                    Entity target = res.entityHit;

                    // === CHECK IF TARGET IS ON GROUND ===
                    // Vines come from the ground, so target can't be too high up
                    // Height check scales with target size (bigger targets can be higher)
                    int groundY = ProcedureUtils.getGroundBelow(target).getY();
                    double heightAboveGround = target.posY - (groundY + 1);
                    double maxHeight = 3.0D + target.height;  // Scale with target height

                    if (heightAboveGround < maxHeight) {
                        World world = entity.world;

                        // === CALCULATE SCALED VALUES BASED ON TARGET SIZE ===
                        float targetWidth = target.width;
                        float targetHeight = target.height;

                        // More vines for bigger targets
                        int vineCount = getScaledVineCount(targetWidth);

                        // Log for debugging (remove in production)
                        // System.out.println("Creating vine trap for entity: " + target.getName());
                        // System.out.println("  Target size: " + targetWidth + " x " + targetHeight);
                        // System.out.println("  Vine count: " + vineCount);

                        // === SPAWN VINES IN A CIRCLE ===
                        for (int i = 0; i < vineCount; i++) {
                            // Calculate angle around the circle
                            // If vineCount=6: angles are 0°, 60°, 120°, 180°, 240°, 300°
                            double angle = (2 * Math.PI / vineCount) * i;

                            // Add randomness so it's not a perfect circle
                            angle += (world.rand.nextDouble() - 0.5) * 0.4;

                            // Spawn distance SCALES with target size
                            // Bigger targets = vines spawn further away
                            float distance = getScaledSpawnDistance(targetWidth, world.rand);

                            // Convert angle + distance to X/Z offset
                            // cos(angle) = X component, sin(angle) = Z component
                            Vec3d spawnOffset = new Vec3d(
                                    Math.cos(angle) * distance,
                                    0,  // Y = 0, we'll find ground level later
                                    Math.sin(angle) * distance
                            );

                            // Create and spawn the vine
                            EC vine = new EC(entity, target, spawnOffset);
                            world.spawnEntity(vine);
                        }

                        // Play activation sound
                        world.playSound(null, target.getPosition(),
                                SoundEvent.REGISTRY.getObject(new ResourceLocation("block.grass.break")),
                                SoundCategory.PLAYERS, 1.0F, 0.6F);

                        // Set cooldown (300 ticks = 15 seconds)
                        ((ItemJutsu.Base) stack.getItem()).setCurrentJutsuCooldown(stack, 300L);
                        return true;  // Success!
                    }
                }
                return false;  // No valid target found
            }
        }
    }

    /**
     * ==========================================
     * RENDERER REGISTRATION
     * ==========================================
     *
     * Call this in your mod's client proxy preInit:
     * new EntityVineTrap.Renderer().register();
     */
    public static class Renderer extends EntityRendererRegister {
        @SideOnly(Side.CLIENT)
        @Override
        public void register() {
            RenderingRegistry.registerEntityRenderingHandler(EC.class, RenderVineSegment::new);
        }
    }

    /**
     * ==========================================
     * VINE RENDERER - DRAWS EACH SEGMENT
     * ==========================================
     *
     * This tells Minecraft how to draw vine segments.
     * It positions, rotates, and scales the model appropriately.
     */
    @SideOnly(Side.CLIENT)
    public static class RenderVineSegment extends Render<VineSegment> {
        // Path to the vine texture file
        private static final ResourceLocation TEXTURE = new ResourceLocation("shinobiaddon:textures/entity/vine_segment.png");
        // For the modid you put the mod's here: narutomod
        // You can use any texture here: textures/entity/vine_segment.png

        // The 3D model for vine segments
        private final ModelVineSegment model = new ModelVineSegment();

        public RenderVineSegment(RenderManager renderManager) {
            super(renderManager);
            this.shadowSize = 0.0F;  // No shadow for vines
        }

        @Override
        public void doRender(VineSegment entity, double x, double y, double z, float entityYaw, float partialTicks) {
            // Update position from parent (for smooth movement)
            if (entity.getParent() != null) {
                entity.setPositionAndRotationFromParent(partialTicks);
                x = entity.posX - this.renderManager.viewerPosX;
                y = entity.posY - this.renderManager.viewerPosY;
                z = entity.posZ - this.renderManager.viewerPosZ;
            }

            // Bind our texture
            this.bindEntityTexture(entity);

            // Start transformations
            GlStateManager.pushMatrix();

            // Move to entity position
            GlStateManager.translate(x, y, z);

            // Apply rotation
            GlStateManager.rotate(-entity.rotationYaw, 0.0F, 1.0F, 0.0F);      // Horizontal rotation
            GlStateManager.rotate(entity.rotationPitch - 180.0F, 1.0F, 0.0F, 0.0F);  // Vertical rotation

            // Add twist based on segment index (makes vines look more natural)
            GlStateManager.rotate(12.0F * entity.getIndex(), 0.0F, 1.0F, 0.0F);

            // Scale based on entity size
            GlStateManager.scale(entity.width / 0.15F, entity.height / 0.25F, entity.width / 0.15F);

            // Draw the model
            this.model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

            // End transformations
            GlStateManager.popMatrix();
        }

        @Override
        protected ResourceLocation getEntityTexture(VineSegment entity) {
            return TEXTURE;
        }
    }

    /**
     * ==========================================
     * VINE MODEL - THE 3D SHAPE
     * ==========================================
     *
     * Defines what a vine segment looks like.
     * Simple thin cylinder with small leaf decorations.
     */
    @SideOnly(Side.CLIENT)
    public static class ModelVineSegment extends ModelBase {
        private final ModelRenderer vine;   // Main stem
        private final ModelRenderer leaf1;  // Decorative leaf
        private final ModelRenderer leaf2;  // Another leaf

        public ModelVineSegment() {
            // Texture is 32x32 pixels
            this.textureWidth = 32;
            this.textureHeight = 32;

            // === MAIN VINE STEM ===
            // A thin rectangular box
            this.vine = new ModelRenderer(this);
            this.vine.setRotationPoint(0.0F, 0.0F, 0.0F);
            // Parameters: texture X, texture Y, box X, Y, Z, width, height, depth
            this.vine.cubeList.add(new ModelBox(vine, 0, 0, -1.0F, -4.0F, -1.0F, 2, 5, 2, 0.0F, false));

            // === DECORATIVE LEAVES ===
            // Small flat boxes sticking out from the stem

            // Leaf 1 - right side
            this.leaf1 = new ModelRenderer(this);
            this.leaf1.setRotationPoint(1.0F, -2.0F, 0.0F);
            this.vine.addChild(leaf1);  // Attach to main stem
            setRotationAngle(leaf1, 0.0F, 0.0F, 0.4F);  // Tilt outward
            this.leaf1.cubeList.add(new ModelBox(leaf1, 8, 0, 0.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F, false));

            // Leaf 2 - left side
            this.leaf2 = new ModelRenderer(this);
            this.leaf2.setRotationPoint(-1.0F, -1.0F, 0.0F);
            this.vine.addChild(leaf2);
            setRotationAngle(leaf2, 0.0F, 0.0F, -0.4F);  // Tilt outward (opposite direction)
            this.leaf2.cubeList.add(new ModelBox(leaf2, 8, 0, -2.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F, true));
        }

        @Override
        public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
            this.vine.render(f5);
        }

        /**
         * Helper to set rotation on a model part
         */
        public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }
    }
}
