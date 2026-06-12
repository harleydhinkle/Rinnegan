package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemJutsu;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.google.common.base.Predicate;

import java.util.List;

@ElementsNarutomodMod.ModElement.Tag
public class EntityLightningRatTremor extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 531;
	public static final int ENTITYID_RANGED = 532;

	public EntityLightningRatTremor(ElementsNarutomodMod instance) {
		super(instance, 946);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "lightning_rat_tremor"), ENTITYID)
				.name("lightning_rat_tremor").tracker(64, 3, true).build());
	}

	public static class EC extends EntityScalableProjectile.Base implements ItemJutsu.IJutsu {
		private static final float BASE_DAMAGE = 20.0F;
		private EntityLivingBase target;

		public EC(World worldIn) {
			super(worldIn);
			this.setOGSize(0.6F, 0.1F);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase shooter, EntityLivingBase targetIn) {
			super(shooter);
			this.setOGSize(0.6F, 0.1F);
			Vec3d vec = shooter.getPositionEyes(1.0F).add(shooter.getLookVec().scale(0.9D));
			this.setLocationAndAngles(vec.x, vec.y, vec.z, shooter.rotationYaw, shooter.rotationPitch);
			this.setEntityScale(2.0F);
			this.target = targetIn;
			this.isImmuneToFire = true;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.RAITON;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			this.updateInFlightRotations();
			if (!this.world.isRemote && (this.ticksInAir > 200 || this.isInWater())) {
				this.setDead();
				return;
			}
			if (this.target != null && this.target.isEntityAlive() && this.ticksAlive > 10) {
				Vec3d vec = this.target.getPositionVector().addVector(0.0D, this.target.height * 0.5D, 0.0D).subtract(this.getPositionVector());
				this.shootPrecise(vec.x, vec.y, vec.z, 0.96F);
			}
			if (this.ticksAlive % 6 == 1) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:electricity")),
						0.1F, this.rand.nextFloat() * 1.5F + 0.7F);
			}
			if (this.rand.nextFloat() < 0.8F) {
				EntityLightningArc.spawnAsParticle(this.world,
						this.posX + (this.rand.nextFloat() - 0.5F) * this.width,
						this.posY + this.rand.nextFloat() * this.height,
						this.posZ + (this.rand.nextFloat() - 0.5F) * this.width,
						0.3D, this.motionX, this.motionY, this.motionZ);
			}
		}

		@Override
		protected void onImpact(RayTraceResult result) {
			if (this.world.isRemote) {
				return;
			}
			if (result.entityHit != null) {
				if (!result.entityHit.equals(this.shootingEntity) && !(result.entityHit instanceof EC)) {
					result.entityHit.hurtResistantTime = 10;
					EntityLightningArc.onStruck(result.entityHit,
							this.getDamageSource(), BASE_DAMAGE * (1.0F + this.rand.nextFloat() * 0.2F));
					this.setDead();
				}
			} else if (result.hitVec != null && result.getBlockPos() != null) {
				((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, result.hitVec.x, result.hitVec.y, result.hitVec.z,
						(int)(this.getEntityScale() * 8.0F), 0.0D, 0.0D, 0.0D, 0.4D,
						Block.getIdFromBlock(this.world.getBlockState(result.getBlockPos()).getBlock()));
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:bullet_impact")),
						0.5F, 0.4F + this.rand.nextFloat() * 0.6F);
				this.setDead();
			}
		}

		private DamageSource getDamageSource() {
			return this.shootingEntity != null ? ItemJutsu.causeJutsuDamage(this, this.shootingEntity) : DamageSource.GENERIC;
		}

		@Override
		protected void checkOnGround() {
		}

		@Override
		public void renderParticles() {
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				if (power < 1.0F) {
					return false;
				}
				List<EntityLivingBase> targets = entity.world.getEntitiesWithinAABB(EntityLivingBase.class,
						entity.getEntityBoundingBox().grow(30.0D), new Predicate<EntityLivingBase>() {
							@Override
							public boolean apply(EntityLivingBase target) {
								if (target == null || !target.isEntityAlive() || target == entity) {
									return false;
								}
								Vec3d vec = target.getPositionEyes(1.0F).subtract(entity.getPositionEyes(1.0F));
								ProcedureUtils.Vec2f angles = ProcedureUtils.getYawPitchFromVec(vec).subtract(entity.renderYawOffset, entity.rotationPitch);
								return Math.abs(angles.x) < 80.0F && Math.abs(angles.y) < 80.0F;
							}
						});
				targets.sort(new ProcedureUtils.EntitySorter(entity));
				for (int i = 0; i < (int)power; i++) {
					createJutsu(entity, targets.isEmpty() ? null : targets.get(i % targets.size()));
				}
				return true;
			}

			public static EC createJutsu(EntityLivingBase entity, EntityLivingBase target) {
				Vec3d vec = entity.getLookVec();
				EC projectile = new EC(entity, target);
				projectile.shoot(vec.x, vec.y, vec.z, 0.9F, 0.5F);
				entity.world.spawnEntity(projectile);
				return projectile;
			}

			@Override
			public float getBasePower() {
				return 0.9F;
			}

			@Override
			public float getPowerupDelay() {
				return 50.0F;
			}

			@Override
			public float getMaxPower() {
				return 20.0F;
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/disk1.png");
			private final ModelDisk mainModel = new ModelDisk();

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float scale = entity.getEntityScale();
				float ticks = entity.ticksExisted + partialTicks;
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + entity.height * 0.5F, z);
				float yaw = ProcedureUtils.interpolateRotation(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
				float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
				float roll = entity.prevRotationRoll + (entity.rotationRoll - entity.prevRotationRoll) * partialTicks;
				GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(pitch - 180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(roll, 0.0F, 0.0F, 1.0F);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.disableLighting();
				GlStateManager.color(0.749F, 0.694F, 1.0F, 0.9F);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.mainModel.render(entity, 0.0F, 0.0F, ticks, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableLighting();
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelDisk extends ModelBase {
			private final ModelRenderer bone2;
			private final ModelRenderer bone3;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;

			public ModelDisk() {
				this.textureWidth = 16;
				this.textureHeight = 16;
				this.bone2 = this.createBlade(0.0F, 0.0F, 0.0436F);
				this.bone3 = this.createBlade(0.0F, 0.0F, -0.0436F);
				this.bone4 = this.createBlade(-0.0436F, 0.0F, 0.0F);
				this.bone5 = this.createBlade(0.0436F, 0.0F, 0.0F);
			}

			private ModelRenderer createBlade(float xRot, float yRot, float zRot) {
				ModelRenderer renderer = new ModelRenderer(this);
				renderer.setRotationPoint(0.0F, 0.0F, 0.0F);
				renderer.rotateAngleX = xRot;
				renderer.rotateAngleY = yRot;
				renderer.rotateAngleZ = zRot;
				renderer.addBox(-4.0F, 0.0F, -4.0F, 8, 0, 8);
				return renderer;
			}

			@Override
			public void render(Entity entity, float f, float f1, float age, float f3, float f4, float scale) {
				this.bone2.rotateAngleY = age * 2.4F;
				this.bone3.rotateAngleY = age * 2.6F;
				this.bone4.rotateAngleY = age * 2.8F;
				this.bone5.rotateAngleY = age * 3.0F;
				this.bone2.render(scale);
				this.bone3.render(scale);
				this.bone4.render(scale);
				this.bone5.render(scale);
			}
		}
	}
}
