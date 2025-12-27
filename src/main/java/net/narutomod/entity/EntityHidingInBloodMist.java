package net.narutomod.entity;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import net.narutomod.item.ItemJutsu;
import net.narutomod.ElementsNarutomodMod;

@ElementsNarutomodMod.ModElement.Tag
public class EntityHidingInBloodMist extends ElementsNarutomodMod.ModElement {
    public static final int ENTITYID = 176;
    public static final int ENTITYID_RANGED = 177;

    public EntityHidingInBloodMist(ElementsNarutomodMod instance) {
        super(instance, 443);
    }

    @Override
    public void initElements() {
        elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
                .id(new ResourceLocation("narutomod", "hiding_in_blood_mist"), ENTITYID)
                .name("hiding_in_blood_mist").tracker(64, 3, true).build());
    }

    public static class EC extends Entity implements ItemJutsu.IJutsu {
        private static final DataParameter<Integer> USER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
        private static final DataParameter<Float> RANGE = EntityDataManager.<Float>createKey(EC.class, DataSerializers.FLOAT);
        private static final int maxLife = 110;

        public EC(World world) {
            super(world);
            this.setSize(0.01f, 0.01f);
        }

        public EC(EntityLivingBase userIn, double rangeIn) {
            this(userIn.world);
            this.setUser(userIn);
            this.setRange((float) rangeIn);
            this.setIdlePosition();

            // User invisibility
            userIn.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, this.maxLife, 0, false, false));
        }

        @Override
        public ItemJutsu.JutsuEnum.Type getJutsuType() {
            return ItemJutsu.JutsuEnum.Type.BLOOD;
        }

        @Override
        protected void entityInit() {
            this.getDataManager().register(USER_ID, -1);
            this.getDataManager().register(RANGE, 1f);
        }

        private void setUser(EntityLivingBase shooter) {
            this.getDataManager().set(USER_ID, shooter.getEntityId());
        }

        protected EntityLivingBase getUser() {
            Entity entity = this.world.getEntityByID(this.dataManager.get(USER_ID));
            return entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null;
        }

        public float getRange() {
            return this.getDataManager().get(RANGE);
        }

        protected void setRange(float range) {
            this.getDataManager().set(RANGE, range);
        }

        protected void setIdlePosition() {
            EntityLivingBase user = this.getUser();
            if (user != null) {
                Vec3d vec3d = user.getLookVec();
                this.setPosition(user.posX + vec3d.x, user.posY + user.getEyeHeight() + vec3d.y - 0.2d, user.posZ + vec3d.z);
            }
        }

        @Override
        public void onUpdate() {
            // follow player
            this.setIdlePosition();

            // client-side particles
            if (this.world.isRemote) {
                EntityLivingBase user = this.getUser();
                float range = this.getRange();
                for (int i = 0; i < (int) (range * 10); i++) {
                    double offsetX = range * (this.rand.nextDouble() - 0.5d) * 0.1d;
                    double offsetY = (this.rand.nextDouble() - 0.5d) * range * 0.1d;
                    double offsetZ = (this.rand.nextDouble() - 0.5d) * 0.1d;
                    this.world.spawnParticle(EnumParticleTypes.REDSTONE,
                            this.posX + offsetX, this.posY + offsetY, this.posZ + offsetZ,
                            1.0d, 0.0d, 0.0d);
                }
            }

            // server logic
            if (!this.world.isRemote) {
                EntityLivingBase user = this.getUser();
                float range = this.getRange();

                // === 🔴 NEW: APPLY WITHER EFFECT TO ENEMIES INSIDE MIST ===
                if (this.ticksExisted % 20 == 0) {
                    for (EntityLivingBase entity : this.world.getEntitiesWithinAABB(EntityLivingBase.class,
                        this.getEntityBoundingBox().grow(range))) {

                        if (entity == user) continue; // don't hurt caster

                        // Wither I (amplifier 0) — change to 1 if you want Wither II
                        entity.addPotionEffect(new PotionEffect(MobEffects.WITHER, 40, 0));
                    }
                }
            }

            if (this.ticksExisted > this.maxLife) {
                this.setDead();
            }
        }

        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {}
        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {}

        public static class Jutsu implements ItemJutsu.IJutsuCallback {
            @Override
            public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
                SoundEvent sound = net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:hiding_in_ash"));
                entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, sound, SoundCategory.NEUTRAL, 5, 1f);
                entity.world.spawnEntity(new EC(entity, power));
                return true;
            }

            @Override
            public float getPowerupDelay() {
                return 15.0f;
            }
        }
    }
}
