package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemInton;
import net.narutomod.PlayerInput;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityShadowImitation extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 293;
	public static final int ENTITYID_RANGED = 294;

	public EntityShadowImitation(ElementsNarutomodMod instance) {
		super(instance, 618);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "shadow_imitation"), ENTITYID).name("shadow_imitation").tracker(64, 3, true).build());
	}

	public static class EC extends Entity implements PlayerInput.Hook.IHandler, ItemJutsu.IJutsu {
		private static final DataParameter<Integer> USER_ID =
				EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> TARGET_ID =
				EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);

		// >>> ADDED FOR TIME LIMIT <<<
		// Fixed hold duration (ticks) — 200 ticks = 10 seconds.
		private static final int MAX_HOLD_TICKS = 600;
		// >>> END <<<

		private double chakraBurn;
		private PlayerInput.Hook userInput = new PlayerInput.Hook();

		public EC(World world) {
			super(world);
			this.setSize(0.01f, 0.01f);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase userIn, EntityLivingBase targetIn, double chakraUsagePerSec) {
			this(userIn.world);
			this.setUser(userIn);
			this.setTarget(targetIn);
			this.setPosition(userIn.posX, userIn.posY, userIn.posZ);
			this.chakraBurn = chakraUsagePerSec
					+ Math.max(ProcedureUtils.getPunchDamage(targetIn) * 25d, 75d);
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.INTON;
		}

		@Override
		protected void entityInit() {
			this.getDataManager().register(USER_ID, Integer.valueOf(-1));
			this.getDataManager().register(TARGET_ID, Integer.valueOf(-1));
		}

		private void setUser(@Nullable EntityLivingBase entity) {
			this.getDataManager().set(USER_ID, entity != null ? entity.getEntityId() : -1);
		}

		@Nullable
		private EntityLivingBase getUser() {
			Entity entity = this.world.getEntityByID(this.getDataManager().get(USER_ID));
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		private void setTarget(@Nullable EntityLivingBase entity) {
			this.getDataManager().set(TARGET_ID, entity != null ? entity.getEntityId() : -1);
		}

		@Nullable
		private EntityLivingBase getTarget() {
			Entity entity = this.world.getEntityByID(this.getDataManager().get(TARGET_ID));
			return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
		}

		@Override
		public void handlePacket(@Nullable PlayerInput.Hook.MovementPacket movementPacket,
								 @Nullable PlayerInput.Hook.MousePacket mousePacket) {
			if (movementPacket != null) {
				this.userInput.copyMovementInput(movementPacket);
			}
			if (mousePacket != null) {
				this.userInput.copyMouseInput(mousePacket);
			}
		}

		@Override
		public void setDead() {
			super.setDead();
			if (!this.world.isRemote) {
				EntityLivingBase user = this.getUser();
				EntityLivingBase target = this.getTarget();
				if (user instanceof EntityPlayerMP) {
					PlayerInput.Hook.copyInputFrom((EntityPlayerMP)user, this, false);
				}
				if (target != null) {
					PlayerInput.Hook.haltTargetInput(target, false);
				}
				if (user != null) {
					Jutsu.removeEntity(user, this.getEntityId());
				}
			}
		}

		@Override
		public void onUpdate() {
			EntityLivingBase user = this.getUser();
			EntityLivingBase target = this.getTarget();

			if (!this.world.isRemote) {
				if (user != null && user.isEntityAlive() &&
					ItemJutsu.canTarget(target) && this.canTargetBeSeen()) {

					// >>> ADDED FOR TIME LIMIT <<<
					if (this.ticksExisted >= MAX_HOLD_TICKS) {
						this.setDead();
						return;
					}
					// >>> END <<<

					this.setPosition(user.posX, user.posY, user.posZ);

					if (user.getEntityData().getBoolean(NarutomodModVariables.JutsuKey2Pressed)
					 || (this.ticksExisted % 20 == 1 && !Chakra.pathway(user).consume(this.chakraBurn))) {
						this.setDead();
					} else {
						if (this.ticksExisted == 1) {
							this.playSound(SoundEvent.REGISTRY.getObject(
								new ResourceLocation("narutomod:shadow_sfx")), 1f, 1f);
							PlayerInput.Hook.haltTargetInput(target, true);
							if (user instanceof EntityPlayer) {
								PlayerInput.Hook.copyInputFrom((EntityPlayerMP)user, this, true);
							}
						}

						if (this.userInput.hasNewMovementInput()) {
							this.userInput.handleMovement(target);
						}
						if (this.userInput.hasNewMouseEvent()) {
							this.userInput.handleMouseEvent(target);
						}
					}
				} else {
					this.setDead();
				}
			}
		}

		public boolean canTargetBeSeen() {
			EntityLivingBase user = this.getUser();
			EntityLivingBase target = this.getTarget();
			return this.world.rayTraceBlocks(user.getPositionEyes(1f),
				target.getPositionEyes(1f), false, true, false) == null
				|| this.world.rayTraceBlocks(user.getPositionEyes(1f),
				target.getPositionVector().addVector(0d, 0.2d, 0d),
				false, true, false) == null;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			private static final String ECENTITYID = "ShadowImitationEntityIdKey";

			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 30d);
				if (res != null && res.entityHit instanceof EntityLivingBase) {
					int[] oldintarray = entity.getEntityData().getIntArray(ECENTITYID);
					if (!this.intarrayContains(entity.world, oldintarray, res.entityHit.getEntityId())) {
						int[] newintarray = new int[oldintarray.length + 1];
						for (int i = 0; i < oldintarray.length; i++) {
							newintarray[i] = oldintarray[i];
						}
						EC entity1 = new EC(entity, (EntityLivingBase)res.entityHit,
								ItemInton.SHADOW_IMITATION.chakraUsage);
						entity.world.spawnEntity(entity1);
						newintarray[oldintarray.length] = entity1.getEntityId();
						entity.getEntityData().setIntArray(ECENTITYID, newintarray);
						return true;
					}
				}
				return false;
			}

			private boolean intarrayContains(World world, int[] intarray, int i) {
				for (int j = 0; j < intarray.length; j++) {
					Entity entity = world.getEntityByID(intarray[j]);
					if (entity instanceof EC) {
						EntityLivingBase target = ((EC)entity).getTarget();
						if (target != null && target.getEntityId() == i) {
							return true;
						}
					}
				}
				return false;
			}

			public static void removeEntity(EntityLivingBase user, int entityId) {
				int[] oldintarray = user.getEntityData().getIntArray(ECENTITYID);
				if (oldintarray.length > 1) {
					int[] newintarray = new int[oldintarray.length - 1];
					for (int i = 0, j = 0; j < oldintarray.length; j++) {
						if (oldintarray[j] != entityId) {
							newintarray[i++] = oldintarray[j];
						}
					}
					user.getEntityData().setIntArray(ECENTITYID, newintarray);
				} else {
					user.getEntityData().removeTag(ECENTITYID);
				}
			}
		}

		public static class PlayerHook {
			@SubscribeEvent
			public void onChangeDimension(EntityTravelToDimensionEvent event) {
				Entity entity = event.getEntity();
				if (entity instanceof EntityLivingBase) {
					int[] intarray = entity.getEntityData().getIntArray(Jutsu.ECENTITYID);
					if (intarray.length > 0) {
						for (int i = 0; i < intarray.length; i++) {
							Entity entity1 = entity.world.getEntityByID(intarray[i]);
							if (entity1 instanceof EC) {
								entity1.setDead();
							}
						}
						entity.getEntityData().removeTag(Jutsu.ECENTITYID);
					}
				}
			}
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EC.PlayerHook());
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class,
				renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/black.png");

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public boolean shouldRender(EC livingEntity, ICamera camera, double camX, double camY, double camZ) {
				return true;
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				EntityLivingBase user = entity.getUser();
				EntityLivingBase target = entity.getTarget();
				if (user != null && target != null) {
					// (Rendering code unchanged — omitted here for length)
				}
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}
	}
}
