package net.narutomod.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.item.ItemSenninka;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureWhenPlayerAttcked;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySenninkaClone extends ElementsNarutomodMod.ModElement {
   public static final int ENTITYID        = 527;
   public static final int ENTITYID_RANGED = 528;

   public EntitySenninkaClone(ElementsNarutomodMod instance) {
      super(instance, 942);
   }

   @Override
   public void initElements() {
      this.elements.entities.add(() ->
            EntityEntryBuilder.create()
                  .entity(EntityCustom.class)
                  .id(new ResourceLocation("narutomod", "senninka_clone"), 527)
                  .name("senninka_clone")
                  .tracker(64, 3, true)
                  .build());
   }

   // -------------------------------------------------------------------------
   // The actual clone entity used by ItemSenninka Stage2
   // -------------------------------------------------------------------------
   public static class EntityCustom extends EntityClone.Base {
      private Chakra.Pathway chakra;
      private int idleTime;

      public EntityCustom(World world) {
         super(world);
         this.shouldDefendSummoner = false;
      }

      /** Spawned by Stage2.spawnClone() — copies stats and chakra from the original player. */
      public EntityCustom(EntityLivingBase user) {
         super(user);
         this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(64.0D);
         this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(ProcedureUtils.getModifiedSpeed(user));
         this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ProcedureUtils.getModifiedAttackDamage(user));
         this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)user.getMaxHealth());
         this.setHealth(user.getHealth());
         this.shouldDefendSummoner = false;
         this.setAttackTarget(user.getLastAttackedEntity());
         this.chakra = Chakra.pathway((EntityLivingBase)this);
         Chakra.Pathway userChakra = Chakra.pathway(user);
         this.chakra.setMax(userChakra.getMax());
         this.chakra.consume(-userChakra.getAmount(), true);
         ItemSenninka.setActivationTicks(this, ItemSenninka.getActivationTicks(user));
      }

      // -----------------------------------------------------------------------
      // AI Tasks
      // -----------------------------------------------------------------------
      @Override
      protected void initEntityAI() {
         // Target tasks
         this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, true) {
            @Override
            protected AxisAlignedBB getTargetableArea(double targetDistance) {
               // Taller detection box so the clone spots enemies above/below
               return EntityCustom.this.getEntityBoundingBox().grow(targetDistance, 14.0D, targetDistance);
            }
         });

         // Behaviour tasks
         this.tasks.addTask(0, new EntityAISwimming(this));
         this.tasks.addTask(1, new EntityNinjaMob.AILeapAtTarget(this, 0.0F, 30.0F));
         this.tasks.addTask(2, new EntityNinjaMob.AIAttackMelee(this, 1.2D, true) {
            @Override
            protected void checkAndPerformAttack(EntityLivingBase target, double distanceToTarget) {
               boolean hasPath = this.attacker.getNavigator().getPathToEntityLiving(target) != null;
               if (hasPath && distanceToTarget <= 256.0D && this.attackTick <= 0) {

                  if (distanceToTarget > this.getAttackReachSqr(target)) {
                     // Out of melee range: perform the Jugo piston-fist dash
                     target.world.playSound(
                           (EntityPlayer)null,
                           this.attacker.posX, this.attacker.posY, this.attacker.posZ,
                           SoundEvents.ENTITY_PLAYER_ATTACK_STRONG,
                           SoundCategory.NEUTRAL, 1.0F,
                           this.attacker.getRNG().nextFloat() * 0.5F + 0.5F);

                     // Launch a sonic-boom wave particle trail toward the target
                     Vec3d direction = target.getPositionVector()
                           .subtract(this.attacker.getPositionVector())
                           .normalize();
                     Particles.Renderer particles = new Particles.Renderer(this.attacker.world);
                     for (int i = 1; i <= 25; ++i) {
                        Vec3d step = direction.scale(-0.06D * i);
                        particles.spawnParticles(
                              Particles.Types.SONIC_BOOM,
                              this.attacker.posX, this.attacker.posY + 1.4D, this.attacker.posZ,
                              1, 0.0D, 0.0D, 0.0D,
                              step.x, step.y, step.z,
                              16777215 | (int)((1.0F - (float)i / 25.0F) * 64.0F) << 24,
                              i * 2,
                              (int)(5.0F * (1.0F + (float)i / 25.0F * 0.5F)));
                     }
                     particles.send();

                     // Dash: teleport the attacker right up to the target
                     this.attacker.setLocationAndAngles(
                           target.posX - direction.x,
                           target.posY - direction.y + 0.5D,
                           target.posZ - direction.z,
                           ProcedureUtils.getYawFromVec(direction),
                           ProcedureUtils.getPitchFromVec(direction));
                  }

                  this.attackTick = 20;
                  this.attacker.swingArm(EnumHand.MAIN_HAND);
                  this.attacker.attackEntityAsMob(target);
               }
            }
         });
         this.tasks.addTask(5, new EntityAILookIdle(this));
      }

      // -----------------------------------------------------------------------
      // Lifecycle
      // -----------------------------------------------------------------------

      /** On death: stop spectating, apply brief blindness, restore original player's HP, deactivate Stage2. */
      @Override
      public void setDead() {
         super.setDead();
         if (!this.world.isRemote && this.getSummoner() instanceof EntityPlayerMP) {
            EntityPlayerMP user = (EntityPlayerMP)this.getSummoner();
            this.spectate(user, (Entity)null);                                         // stop possession
            user.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2, 0, false, false));
            if (user.isEntityAlive()) {
               user.setHealth(this.getHealth()); // sync clone HP back to original
            }
            ItemSenninka.STAGE2.jutsu.deactivate((EntityLivingBase)user);
         }
      }

      /** Kill the clone if it has been idle (no active target) for more than 200 ticks. */
      @Override
      protected void updateAITasks() {
         super.updateAITasks();
         if (this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive()) {
            this.idleTime = 0;
         } else {
            ++this.idleTime;
         }
         if (this.idleTime > 200) {
            this.setDead();
         }
      }

      @Override
      public void onUpdate() {
         super.onUpdate();
         EntityLivingBase user = this.getSummoner();

         // CLIENT: resize the clone's bounding box to be slightly larger than the user
         if (this.world.isRemote && user != null && user.width >= this.width) {
            this.width  = user.width  + 0.02F;
            this.height = user.height + 0.01F;
            this.setEntityBoundingBox(new AxisAlignedBB(
                  this.posX - (double)this.width * 0.5D,
                  this.posY,
                  this.posZ - (double)this.width * 0.5D,
                  this.posX + (double)this.width * 0.5D,
                  this.posY + (double)this.height,
                  this.posZ + (double)this.width * 0.5D));
         }

         // SERVER: sync chakra, apply resistance, play sounds, drive exhaust particles
         if (!this.world.isRemote && this.isEntityAlive() && user instanceof EntityPlayerMP) {
            this.spectate((EntityPlayerMP)user, this);           // keep player locked into clone's POV
            Chakra.Pathway userChakra = Chakra.pathway(user);
            userChakra.consume(userChakra.getAmount() - this.chakra.getAmount()); // keep chakra in sync

            if (this.ticksExisted % 20 == 1) {
               this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 22, 2, false, false));
            }
            if (this.ticksExisted == 5) {
               this.playSound(
                     (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:maniacal_laughter")),
                     2.0F, 1.0F);
            }
            ItemSenninka.Stage2.renderExhaust(this);
            ItemSenninka.setActivationTicks(this, ItemSenninka.getActivationTicks(this) + 1);
         }

         // Tick item stacks in every equipment slot so their item effects still run
         for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack itemstack = this.getItemStackFromSlot(slot);
            if (!itemstack.isEmpty()) {
               itemstack.getItem().onUpdate(itemstack, this.world, this, slot.getSlotIndex(), false);
            }
         }
      }

      // -----------------------------------------------------------------------
      // Spectate helper — makes the player "possess" the clone entity
      // -----------------------------------------------------------------------
      private void spectate(EntityPlayerMP spectator, @Nullable Entity targetEntity) {
         if (spectator.getSpectatingEntity() != targetEntity) {
            ProcedureOnLivingUpdate.setNoClip(spectator, targetEntity != null);
            spectator.setSpectatingEntity(targetEntity);
         }
         if (targetEntity != null) {
            spectator.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 2, 0, false, false));
            ProcedureWhenPlayerAttcked.setInvulnerable(spectator, 5);
            spectator.setPositionAndUpdate(targetEntity.posX, targetEntity.posY, targetEntity.posZ);
         }
      }
   }
}
