package net.narutomod.entity;

import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.block.BlockMud;
import net.narutomod.item.ItemDoton;
import net.narutomod.item.ItemEarthRelease;
import net.narutomod.item.ItemJutsu;
import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

@ElementsNarutomodMod.ModElement.Tag
public class EntityEarthDragon extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 529;
	public static final int ENTITYID_RANGED = 530;

	public EntityEarthDragon(ElementsNarutomodMod instance) {
		super(instance, 943);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "earth_dragon"), ENTITYID).name("earth_dragon").tracker(64, 3, true).build());
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EntitySpit.class)
				.id(new ResourceLocation("narutomod", "earth_dragon_spit"), ENTITYID_RANGED).name("earth_dragon_spit").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> AGE = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		public static final int GROW_TIME = 30;
		private final Vec3d mouthVec = new Vec3d(0.0D, -0.5D, 2.25D);
		private EntityLivingBase user;
		private int duration = 20;
		private float spitSpeed = 1.0F;

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(2.0F, 2.0F);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, float speed) {
			this(userIn.world);
			Vec3d vec = userIn.getPositionVector().add(Vec3d.fromPitchYaw(0.0F, userIn.renderYawOffset).scale(3.0D));
			double y = ProcedureUtils.getTopSolidBlockY(userIn.world, new BlockPos(vec));
			this.setLocationAndAngles(vec.x, y, vec.z, userIn.renderYawOffset, 0.0F);
			this.user = userIn;
			this.spitSpeed = speed;
		}

		@Override
		protected void entityInit() {
			this.dataManager.register(AGE, Integer.valueOf(0));
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.DOTON;
		}

		public int getAge() {
			return ((Integer)this.dataManager.get(AGE)).intValue();
		}

		private void setAge(int age) {
			this.dataManager.set(AGE, Integer.valueOf(age));
		}

		private Vec3d getMouthVec() {
			float pitch = Math.max(-this.rotationPitch, 30.0F);
			return this.mouthVec.rotatePitch(pitch * 0.017453292F).rotateYaw(-this.rotationYaw * 0.017453292F).add(this.getPositionVector());
		}

		private void aimAtUserTarget() {
			if (this.user == null) {
				return;
			}
			Vec3d targetVec = null;
			if (this.user instanceof EntityLiving && ((EntityLiving)this.user).getAttackTarget() != null) {
				EntityLivingBase target = ((EntityLiving)this.user).getAttackTarget();
				targetVec = target.getPositionEyes(1.0F).subtract(this.getPositionVector());
			}
			if (targetVec == null) {
				RayTraceResult result = ProcedureUtils.objectEntityLookingAt(this.user, 50.0D);
				if (result != null && result.hitVec != null) {
					targetVec = result.hitVec.subtract(this.getPositionVector());
				}
			}
			if (targetVec == null || targetVec.lengthSquared() < 0.0001D) {
				targetVec = this.user.getLookVec();
			}
			float yaw = (float)(-MathHelper.atan2(targetVec.x, targetVec.z) * (180.0D / Math.PI));
			float pitch = (float)(-MathHelper.atan2(targetVec.y, MathHelper.sqrt(targetVec.x * targetVec.x + targetVec.z * targetVec.z)) * (180.0D / Math.PI));
			float dyaw = MathHelper.clamp(ProcedureUtils.subtractDegreesWrap(yaw, this.rotationYaw), -10.0F, 10.0F);
			float dpitch = MathHelper.clamp(ProcedureUtils.subtractDegreesWrap(pitch, this.rotationPitch), -10.0F, 10.0F);
			this.setRotation(this.rotationYaw + dyaw, Math.min(this.rotationPitch + dpitch, 0.0F));
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			int age = this.getAge();
			if (age == 0) {
				this.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:rocks")), 2.0F, 0.8F);
			}
			if (!this.world.isRemote) {
				if (age > this.duration || this.user == null || this.user.isDead) {
					this.setDead();
				} else {
					this.aimAtUserTarget();
					if (age > GROW_TIME && age <= this.duration - 20 && age % 4 == 0) {
						Vec3d mouth = this.getMouthVec();
						Vec3d motion = this.getLookVec().scale(this.spitSpeed);
						EntitySpit spit = new EntitySpit(this.user, mouth.x, mouth.y, mouth.z, motion.x, motion.y, motion.z, this.spitSpeed * 10.0F);
						this.world.spawnEntity(spit);
						this.world.playSound(null, mouth.x, mouth.y, mouth.z,
								SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:spitbig")),
								SoundCategory.PLAYERS, 1.0F, 0.8F + this.rand.nextFloat() * 0.3F);
						Particles.spawnParticle(this.world, Particles.Types.SPIT, mouth.x, mouth.y, mouth.z, 80,
								0.2D, 0.2D, 0.2D, motion.x * 0.4D, motion.y * 0.4D, motion.z * 0.4D, 0xFF223020, 16, spit.getEntityId());
					}
				}
			} else {
				for (int i = 0; i < 10; i++) {
					this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
							this.posX + (this.rand.nextDouble() - 0.5D) * this.width,
							this.posY + this.rand.nextDouble() * this.height,
							this.posZ + (this.rand.nextDouble() - 0.5D) * this.width,
							0.0D, 0.02D, 0.0D, Block.getStateId(BlockMud.block != null ? BlockMud.block.getDefaultState() : Blocks.DIRT.getDefaultState()));
				}
			}
			this.setAge(age + 1);
		}

		@Override
		public void setDead() {
			if (!this.world.isRemote) {
				Particles.spawnParticle(this.world, Particles.Types.BLOCK_DUST, this.posX, this.posY + this.height * 0.5D, this.posZ,
						180, this.width * 0.4D, this.height * 0.3D, this.width * 0.4D, 0.0D, 0.1D, 0.0D,
						Block.getStateId(BlockMud.block != null ? BlockMud.block.getDefaultState() : Blocks.DIRT.getDefaultState()), 30);
			}
			super.setDead();
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			this.setAge(compound.getInteger("age"));
			this.duration = compound.getInteger("duration");
			this.spitSpeed = compound.getFloat("spitSpeed");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			compound.setInteger("age", this.getAge());
			compound.setInteger("duration", this.duration);
			compound.setFloat("spitSpeed", this.spitSpeed);
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String TAG_KEY = "EarthDragonEntityIdKey";

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				EC dragon = this.getECentity(stack, entity.world);
				if (dragon == null || dragon.isDead) {
					float multiplier = 1.0F;
					if (stack.getItem() instanceof ItemDoton.RangedItem) {
						multiplier = 1.0F / ((ItemDoton.RangedItem)stack.getItem()).getCurrentJutsuXpModifier(stack, entity);
					}
					dragon = new EC(entity, 1.0F + multiplier);
					entity.world.spawnEntity(dragon);
					if (!stack.hasTagCompound()) {
						stack.setTagCompound(new NBTTagCompound());
					}
					stack.getTagCompound().setInteger(TAG_KEY, dragon.getEntityId());
				} else {
					dragon.duration++;
				}
				return true;
			}

			@Override
			public void onUsingTick(ItemStack stack, EntityLivingBase player, float power) {
				if (!player.world.isRemote && ItemJutsu.getCurrentJutsu(stack) == ItemEarthRelease.DRAGON
						&& (!(player instanceof EntityPlayer) || ((ItemJutsu.Base)stack.getItem()).canActivateJutsu(stack, ItemEarthRelease.DRAGON, (EntityPlayer)player) == EnumActionResult.SUCCESS)) {
					if (this.createJutsu(stack, player, power) && player.ticksExisted % 5 == 0) {
						Chakra.pathway(player).consume(ItemEarthRelease.DRAGON.chakraUsage * 0.05D);
					}
				}
			}

			private EC getECentity(ItemStack stack, World world) {
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_KEY)) {
					Entity entity = world.getEntityByID(stack.getTagCompound().getInteger(TAG_KEY));
					return entity instanceof EC ? (EC)entity : null;
				}
				return null;
			}
		}
	}

	public static class EntitySpit extends EntityParticle.Base implements ItemJutsu.IJutsu {
		private float damage;
		private EntityLivingBase shooter;

		public EntitySpit(World worldIn) {
			super(worldIn);
		}

		public EntitySpit(EntityLivingBase shooter, double x, double y, double z, double mx, double my, double mz, float damageIn) {
			super(shooter.world, x, y, z, mx, my, mz, 0xF8203020, 3.0F, 0);
			this.setMaxAge(40);
			this.shooter = shooter;
			this.damage = damageIn;
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.DOTON;
		}

		@Override
		public void onUpdate() {
			int age = this.getAge();
			int maxAge = this.getMaxAge();
			this.setParticleTextureOffset(MathHelper.clamp(7 - age * 8 / maxAge, 0, 7));
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			boolean shouldDie = false;
			if (!this.world.isRemote) {
				RayTraceResult result = EntityScalableProjectile.forwardsRaycast(this, true, false, this.shooter);
				if (result != null) {
					if (result.entityHit instanceof EntityLivingBase && !result.entityHit.equals(this.shooter)) {
						result.entityHit.attackEntityFrom(this.getDamageSource(), this.damage);
						((EntityLivingBase)result.entityHit).addPotionEffect(new PotionEffect(PotionHeaviness.potion, 100, 2, false, false));
						shouldDie = true;
					} else if (result.entityHit instanceof EntitySpit) {
						shouldDie = true;
					}
				}
			}
			this.motionY -= 0.05D;
			this.move(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.96D;
			this.motionY *= 0.96D;
			this.motionZ *= 0.96D;
			if (this.onGround) {
				this.motionX *= 0.5D;
				this.motionZ *= 0.5D;
			}
			if (!this.world.isRemote) {
				this.setAge(++age);
				if (age > maxAge || shouldDie || this.collidedHorizontally) {
					this.onDeath();
				}
			}
		}

		private DamageSource getDamageSource() {
			return this.shooter != null ? ItemJutsu.causeJutsuDamage(this, this.shooter) : DamageSource.GENERIC;
		}

		@Override
		protected int getTexV() {
			return 2;
		}

		@Override
		public boolean shouldDisableDepth() {
			return true;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.damage = compound.getFloat("damage");
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setFloat("damage", this.damage);
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
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderDragon(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderDragon extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/dragon_gray.png");
			private final ResourceLocation texture2 = new ResourceLocation("narutomod:textures/gas256.png");
			private final ModelDragonHead model = new ModelDragonHead();

			public RenderDragon(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				float age = entity.getAge() + partialTicks;
				float grow = MathHelper.clamp(age / EC.GROW_TIME, 0.0F, 1.0F);
				float pitch = Math.max(-(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks), 30.0F) * grow;
				GlStateManager.pushMatrix();
				this.bindEntityTexture(entity);
				GlStateManager.translate(x, y + (1.5F - (1.0F - grow) * 0.5F) * 2.0F, z);
				GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.prevRotationYaw + MathHelper.wrapDegrees(entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
				GlStateManager.scale(2.0F, 2.0F, 2.0F);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();
				GlStateManager.color(0.235F, 0.188F, 0.102F, 0.95F);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				this.model.teethUpper.showModel = true;
				this.model.teethLower.showModel = true;
				this.model.eyes.showModel = true;
				this.model.render(entity, 0.0F, 0.0F, age, 0.0F, pitch, 0.0625F);
				this.bindTexture(this.texture2);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.translate(0.0F, age * 0.01F, 0.0F);
				GlStateManager.matrixMode(5888);
				this.model.teethUpper.showModel = false;
				this.model.teethLower.showModel = false;
				this.model.eyes.showModel = false;
				GlStateManager.color(0.365F, 0.255F, 0.078F, 1.0F);
				this.model.render(entity, 0.0F, 0.0F, age, 0.0F, pitch, 0.06125F);
				GlStateManager.matrixMode(5890);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.enableCull();
				GlStateManager.disableBlend();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelDragonHead extends ModelBase {
			private final ModelRenderer head;
			private final ModelRenderer teethUpper;
			private final ModelRenderer flair;
			private final ModelRenderer bone4;
			private final ModelRenderer bone5;
			private final ModelRenderer bone6;
			private final ModelRenderer jaw;
			private final ModelRenderer teethLower;
			private final ModelRenderer spine;
			private final ModelRenderer eyes;

			public ModelDragonHead() {
				this.textureWidth = 128;
				this.textureHeight = 128;
				this.head = new ModelRenderer(this);
				this.head.setRotationPoint(0.0F, 21.5F, 5.0F);
				this.head.cubeList.add(new ModelBox(this.head, 64, 0, -6.0F, -1.5F, -31.0F, 12, 5, 16, 1.0F, false));
				this.head.cubeList.add(new ModelBox(this.head, 0, 0, -8.0F, -8.5F, -16.0F, 16, 16, 16, 1.0F, false));
				this.head.cubeList.add(new ModelBox(this.head, 32, 32, 2.0F, -3.5F, -33.0F, 4, 4, 6, 0.0F, true));
				this.head.cubeList.add(new ModelBox(this.head, 32, 32, -6.0F, -3.5F, -33.0F, 4, 4, 6, 0.0F, false));

				this.teethUpper = new ModelRenderer(this);
				this.teethUpper.setRotationPoint(0.0F, 16.5F, -5.0F);
				this.head.addChild(this.teethUpper);
				this.teethUpper.cubeList.add(new ModelBox(this.teethUpper, 0, 52, -6.0F, -12.0F, -26.0F, 12, 3, 16, 0.5F, false));

				this.flair = new ModelRenderer(this);
				this.flair.setRotationPoint(0.0F, -9.5F, -17.0F);
				this.head.addChild(this.flair);
				this.bone4 = new ModelRenderer(this);
				this.bone4.setRotationPoint(9.0F, 9.0F, 0.0F);
				this.flair.addChild(this.bone4);
				this.setRotationAngle(this.bone4, 0.0F, -0.7854F, 0.0F);
				this.bone4.cubeList.add(new ModelBox(this.bone4, 0, 52, 0.0F, -8.0F, 0.0F, 10, 16, 0, 0.0F, false));
				this.bone4.cubeList.add(new ModelBox(this.bone4, 0, 52, -2.0F, -12.0F, 2.0F, 10, 16, 0, 0.0F, false));
				this.bone5 = new ModelRenderer(this);
				this.bone5.setRotationPoint(-9.0F, 9.0F, 0.0F);
				this.flair.addChild(this.bone5);
				this.setRotationAngle(this.bone5, 0.0F, 0.7854F, 0.0F);
				this.bone5.cubeList.add(new ModelBox(this.bone5, 0, 52, -10.0F, -8.0F, 0.0F, 10, 16, 0, 0.0F, true));
				this.bone5.cubeList.add(new ModelBox(this.bone5, 0, 52, -8.0F, -12.0F, 2.0F, 10, 16, 0, 0.0F, true));
				this.bone6 = new ModelRenderer(this);
				this.bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.flair.addChild(this.bone6);
				this.setRotationAngle(this.bone6, -0.8727F, 0.0F, 0.0F);
				this.bone6.cubeList.add(new ModelBox(this.bone6, 84, 42, -8.0F, -10.0F, 0.0F, 16, 10, 0, 0.0F, false));

				this.jaw = new ModelRenderer(this);
				this.jaw.setRotationPoint(0.0F, 3.5F, -14.0F);
				this.head.addChild(this.jaw);
				this.setRotationAngle(this.jaw, 0.7854F, 0.0F, 0.0F);
				this.jaw.cubeList.add(new ModelBox(this.jaw, 64, 22, -6.0F, 0.0F, -16.75F, 12, 4, 16, 1.0F, false));
				this.teethLower = new ModelRenderer(this);
				this.teethLower.setRotationPoint(0.0F, 13.0F, 9.0F);
				this.jaw.addChild(this.teethLower);
				this.teethLower.cubeList.add(new ModelBox(this.teethLower, 42, 42, -6.0F, -16.0F, -25.75F, 12, 2, 16, 0.5F, false));

				this.spine = new ModelRenderer(this);
				this.spine.setRotationPoint(0.0F, 19.5F, 5.0F);
				this.setRotationAngle(this.spine, -0.7854F, 0.0F, 0.0F);
				this.spine.cubeList.add(new ModelBox(this.spine, 0, 32, -5.0F, -4.5F, 2.0F, 10, 10, 10, 2.0F, false));

				this.eyes = new ModelRenderer(this);
				this.eyes.setRotationPoint(0.0F, 21.5F, 5.0F);
				this.setRotationAngle(this.eyes, -0.5236F, 0.0F, 0.0F);
				this.eyes.cubeList.add(new ModelBox(this.eyes, 18, 20, -6.6F, -4.9F, -17.15F, 3, 2, 0, 0.0F, false));
				this.eyes.cubeList.add(new ModelBox(this.eyes, 18, 20, 3.6F, -4.9F, -17.15F, 3, 2, 0, 0.0F, true));
			}

			@Override
			public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
				this.setRotationAngles(f, f1, f2, f3, f4, scale, entity);
				this.head.render(scale);
				this.spine.render(scale);
				if (this.eyes.showModel) {
					GlStateManager.disableLighting();
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					this.eyes.render(scale);
					int i = entity.getBrightnessForRender();
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(i % 65536), (float)(i / 65536));
					GlStateManager.enableLighting();
				}
			}

			@Override
			public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
				this.head.rotateAngleX = -headPitch * 0.017453292F;
				this.eyes.rotateAngleX = this.head.rotateAngleX;
			}

			private void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
				modelRenderer.rotateAngleX = x;
				modelRenderer.rotateAngleY = y;
				modelRenderer.rotateAngleZ = z;
			}
		}

	}
}
