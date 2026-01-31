/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.EntityAIBase
 *  net.minecraft.entity.ai.EntityAIHurtByTarget
 *  net.minecraft.entity.ai.EntityAILookIdle
 *  net.minecraft.entity.ai.EntityAINearestAttackableTarget
 *  net.minecraft.entity.ai.EntityAISwimming
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.GameType
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.registry.EntityEntryBuilder
 */
package net.narutomod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
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
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.item.ItemSenninka;
import net.narutomod.procedure.ProcedureUtils;

@ElementsNarutomodMod.ModElement.Tag
public class EntitySenninkaClone
extends ElementsNarutomodMod.ModElement {
    public static final int ENTITYID = 527;
    public static final int ENTITYID_RANGED = 528;

    public EntitySenninkaClone(ElementsNarutomodMod instance) {
        super(instance, 942);
    }

    @Override
    public void initElements() {
        this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityCustom.class).id(new ResourceLocation("narutomod", "senninka_clone"), 527).name("senninka_clone").tracker(64, 3, true).build());
    }

    public static class EntityCustom
    extends EntityClone.Base {
        private Chakra.Pathway chakra;
        private int idleTime;

        public EntityCustom(World world) {
            super(world);
            this.shouldDefendSummoner = false;
        }

        public EntityCustom(EntityLivingBase user) {
            super(user);
            this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(64.0);
            this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(ProcedureUtils.getModifiedSpeed(user));
            this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(ProcedureUtils.getModifiedAttackDamage(user));
            this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)user.func_110138_aP());
            this.func_70606_j(user.func_110143_aJ());
            this.shouldDefendSummoner = false;
            this.func_70624_b(user.func_110144_aD());
            this.chakra = Chakra.pathway((EntityLivingBase)this);
            Chakra.Pathway userChakra = Chakra.pathway(user);
            this.chakra.setMax(userChakra.getMax());
            this.chakra.consume(-userChakra.getAmount(), true);
            ItemSenninka.setActivationTicks((Entity)this, ItemSenninka.getActivationTicks((Entity)user));
            if (user instanceof EntityPlayerMP) {
                this.getEntityData().func_74768_a("OriginalGameMode", ((EntityPlayerMP)user).field_71134_c.func_73081_b().func_77148_a());
                ((EntityPlayerMP)user).func_71033_a(GameType.SPECTATOR);
            }
        }

        @Override
        protected void func_184651_r() {
            this.field_70715_bh.func_75776_a(1, (EntityAIBase)new EntityAIHurtByTarget((EntityCreature)this, true, new Class[0]));
            this.field_70715_bh.func_75776_a(2, (EntityAIBase)new EntityAINearestAttackableTarget(this, EntityLivingBase.class, false, false){

                protected AxisAlignedBB func_188511_a(double targetDistance) {
                    return this.func_174813_aQ().func_72314_b(targetDistance, 14.0, targetDistance);
                }
            });
            this.field_70714_bg.func_75776_a(0, (EntityAIBase)new EntityAISwimming((EntityLiving)this));
            this.field_70714_bg.func_75776_a(1, (EntityAIBase)new EntityNinjaMob.AILeapAtTarget((EntityLiving)this, 0.0f, 30.0f));
            this.field_70714_bg.func_75776_a(2, (EntityAIBase)new EntityNinjaMob.AIAttackMelee(this, 1.2, true){

                @Override
                protected void checkAndPerformAttack(EntityLivingBase target, double distanceToTarget) {
                    if (this.attacker.func_70635_at().func_75522_a((Entity)target) && distanceToTarget <= 256.0 && this.attackTick <= 0) {
                        if (distanceToTarget > this.getAttackReachSqr(target)) {
                            target.field_70170_p.func_184148_a(null, this.attacker.field_70165_t, this.attacker.field_70163_u, this.attacker.field_70161_v, SoundEvents.field_187539_bB, SoundCategory.NEUTRAL, 1.0f, this.attacker.func_70681_au().nextFloat() * 0.5f + 0.5f);
                            Vec3d vec = target.func_174791_d().func_178788_d(this.attacker.func_174791_d()).func_72432_b();
                            Particles.Renderer particles = new Particles.Renderer(this.attacker.field_70170_p);
                            int j = 25;
                            for (int i = 1; i <= j; ++i) {
                                Vec3d vec1 = vec.func_186678_a(-0.06 * (double)i);
                                particles.spawnParticles(Particles.Types.SONIC_BOOM, this.attacker.field_70165_t, this.attacker.field_70163_u + 1.4, this.attacker.field_70161_v, 1, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0xFFFFFF | (int)((1.0f - (float)i / (float)j) * 64.0f) << 24, i * 2, (int)(5.0f * (1.0f + (float)i / (float)j * 0.5f)));
                            }
                            particles.send();
                            this.attacker.func_70080_a(target.field_70165_t - vec.field_72450_a, target.field_70163_u - vec.field_72448_b + 0.5, target.field_70161_v - vec.field_72449_c, ProcedureUtils.getYawFromVec(vec), ProcedureUtils.getPitchFromVec(vec));
                        }
                        this.attackTick = 20;
                        this.attacker.func_184609_a(EnumHand.MAIN_HAND);
                        this.attacker.func_70652_k((Entity)target);
                    }
                }
            });
            this.field_70714_bg.func_75776_a(5, (EntityAIBase)new EntityAILookIdle((EntityLiving)this));
        }

        @Override
        public void func_70106_y() {
            if (!this.field_70170_p.field_72995_K && this.getSummoner() instanceof EntityPlayerMP && !this.field_70128_L) {
                EntityPlayerMP user = (EntityPlayerMP)this.getSummoner();
                user.func_71033_a(GameType.func_77146_a((int)this.getEntityData().func_74762_e("OriginalGameMode")));
                user.func_70690_d(new PotionEffect(MobEffects.field_76441_p, 2, 0, false, false));
                if (user.func_70089_S()) {
                    float f = this.func_110143_aJ();
                    user.func_70606_j(f);
                    if (f <= 0.0f) {
                        ItemSenninka.STAGE2.jutsu.deactivate((EntityLivingBase)user);
                    }
                }
            }
            super.func_70106_y();
        }

        @Override
        protected void func_70619_bc() {
            super.func_70619_bc();
            this.idleTime = this.func_70638_az() == null || !this.func_70638_az().func_70089_S() ? ++this.idleTime : 0;
            if (this.idleTime > 200) {
                this.func_70106_y();
                if (this.getSummoner() instanceof EntityPlayer) {
                    ItemSenninka.STAGE2.jutsu.deactivate(this.getSummoner());
                }
            }
        }

        @Override
        public void func_70071_h_() {
            super.func_70071_h_();
            if (!this.field_70170_p.field_72995_K && this.getSummoner() instanceof EntityPlayerMP) {
                EntityPlayerMP user = (EntityPlayerMP)this.getSummoner();
                if (!user.func_70089_S()) {
                    user.func_175399_e((Entity)user);
                } else if (user.func_175398_C() != this) {
                    user.func_175399_e((Entity)this);
                }
                Chakra.PathwayPlayer userChakra = Chakra.pathway((EntityPlayer)user);
                userChakra.consume(userChakra.getAmount() - this.chakra.getAmount());
                if (this.field_70173_aa % 20 == 1) {
                    this.func_70690_d(new PotionEffect(MobEffects.field_76429_m, 22, 2, false, false));
                }
                if (this.field_70173_aa == 5) {
                    this.func_184185_a((SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:maniacal_laughter")), 2.0f, 1.0f);
                }
                ItemSenninka.Stage2.renderExhaust((EntityLivingBase)this);
                ItemSenninka.setActivationTicks((Entity)this, ItemSenninka.getActivationTicks((Entity)this) + 1);
            }
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                ItemStack itemstack = this.func_184582_a(slot);
                if (itemstack.func_190926_b()) continue;
                itemstack.func_77945_a(this.field_70170_p, (Entity)this, slot.func_188452_c(), false);
            }
        }
    }
}

