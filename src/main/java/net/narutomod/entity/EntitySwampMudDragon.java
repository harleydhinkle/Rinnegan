
package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.SoundEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.BlockLiquid;

import net.narutomod.item.ItemSwampRelease;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureAoeCommand;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ElementsNarutoMod.ModElement.Tag
public class EntitySwampMudDragon extends ElementsNarutoMod.ModElement {
    public static final int ENTITYID = 951;
    public static final int ENTITYID_RANGED = 952;

    public EntitySwampMudDragon(ElementsNarutoMod instance) {
      super(instance, 730);
    }

  @Override
  public void intitElements () {
    elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
      .id(new ResourceLocation("narutomod", "swamp_mud_dragon"), ENTITYID).name("swamp_mud_dragon").tracker(64, 3, true).build());
  }

  public static class EC extends EntityScalableProjectile.Base implements ItemJutsu.Ijutsu {
    private final int wait = 60;
    private Vec3d shootVec;
		private float prevHeadYaw;
		private float prevHeadPitch;
		//public float prevLimbSwingAmount;
		//public float limbSwingAmount;
		//public float limbSwing;
		private Vec3d lastVec;
		private double yOrigin;
		private final List<ProcedureUtils.Vec2f> partRot = Lists.newArrayList(
			new ProcedureUtils.Vec2f(0.0f, 0.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f),
			new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, 30.0f), new ProcedureUtils.Vec2f(0.0f, -15.0f),
			new ProcedureUtils.Vec2f(0.0f, -15.0f), new ProcedureUtils.Vec2f(0.0f, 0.0f)
      );

    public EC(World a) {
      super(a);
      this.setOGSize(1.0F, 1.0F);
    }

    public EC(EntityLivingBase shooter, float power) {
      super(shooter);
      this.setOGSize(1.0F, 1.0F);
			this.setEntityScale(power);
			this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
			this.yOrigin = shooter.posY;
		}
    
	private void setWaitPosition() {
			if (this.shootVec != null) {
				ProcedureUtils.Vec2f v2f = ProcedureUtils.getYawPitchFromVec(this.shootVec);
				this.setRotation(v2f.x, v2f.y);
			} else if (this.shootingEntity != null) {
				Vec3d vec = this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null
				 ? ((EntityLiving)this.shootingEntity).getAttackTarget().getPositionVector().subtract(this.getPositionVector())
				 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d).hitVec.subtract(this.getPositionVector());
				ProcedureUtils.Vec2f v2f = ProcedureUtils.getYawPitchFromVec(vec);
				this.setRotation(v2f.x, v2f.y);
			}
			this.motionY = this.ticksAlive <= this.wait / 2 ? 3d * this.getEntityScale() / (double)this.wait * 2d : 0.0d;
		}

		/*private void updateLimbSwing() {
			this.prevLimbSwingAmount = this.limbSwingAmount;
	        double d5 = this.posX - this.prevPosX;
	        double d7 = this.posZ - this.prevPosZ;
	        double d9 = this.posY - this.prevPosY;
	        float f10 = MathHelper.sqrt(d5 * d5 + d9 * d9 + d7 * d7) * 4.0F;
	        if (f10 > 1.0F) {
	            f10 = 1.0F;
	        }
	        this.limbSwingAmount += (f10 - this.limbSwingAmount) * 0.4F;
	        this.limbSwing += this.limbSwingAmount;
		}*/

		@Override
		public void onUpdate() {
			if (this.prevHeadYaw == 0.0f && this.prevHeadPitch == 0.0f) {
				this.prevHeadYaw = this.rotationYaw;
				this.prevHeadPitch = this.rotationPitch;
			}
			super.onUpdate();
			if (!this.world.isRemote && (this.ticksAlive > 100 || this.shootingEntity == null || !this.shootingEntity.isEntityAlive())) {
				this.setDead();
			} else {
				if (this.ticksAlive <= this.wait) {
					this.lastVec = this.getPositionVector();
					this.setWaitPosition();
					//this.setEntityScale(this.fullScale * MathHelper.clamp((float)this.ticksAlive / (float) this.wait, 0.1F, 1.0F));
				} else if (!this.isLaunched()) {
					if (this.shootVec != null) {
						this.shoot(this.shootVec.x, this.shootVec.y, this.shootVec.z, 0.95f, 0f);
					} else if (this.shootingEntity != null) {
						Vec3d vec = this.shootingEntity instanceof EntityLiving && ((EntityLiving)this.shootingEntity).getAttackTarget() != null
						 ? ((EntityLiving)this.shootingEntity).getAttackTarget().getPositionVector().subtract(this.getPositionVector())
						 : ProcedureUtils.objectEntityLookingAt(this.shootingEntity, 50d).hitVec.subtract(this.getPositionVector());
						this.shoot(vec.x, vec.y, vec.z, 0.95f, 0f);
					}
				}
				this.updateSegments();
				//this.updateLimbSwing();
				this.prevHeadYaw = this.rotationYaw;
				this.prevHeadPitch = this.rotationPitch;
			}
		}

		public void updateSegments() {
			Vec3d cposvec = this.getPositionVector();
			float slength = this.getEntityScale() * 11.0F * 0.0625F;
			ProcedureUtils.Vec2f vec = new ProcedureUtils.Vec2f(this.rotationYaw, this.rotationPitch)
			 .subtract(this.prevRotationYaw, this.prevHeadPitch);
			Vec3d vec4 = cposvec.subtract(this.lastVec);
			double d4 = vec4.lengthVector();
//String s = ">>> ["+(this.world.isRemote?"client":"server")+"], vec="+vec+", moved:"+d4;
			if (d4 >= slength && this.ticksAlive > this.wait) {
				this.partRot.add(0, vec);
				int i = 1;
				for ( ; i < (int)(d4 / slength); i++) {
					this.partRot.add(0, ProcedureUtils.Vec2f.ZERO);
				}
				this.lastVec = vec4.normalize().scale(slength * i).add(this.lastVec);
			} else {
				this.partRot.set(0, this.partRot.get(0).add(vec));
			}
//System.out.println(s+", partRot:"+this.partRot);
		}
	  
