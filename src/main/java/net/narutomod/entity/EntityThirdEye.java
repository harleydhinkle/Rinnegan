package net.narutomod.entity;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.NarutomodMod;
import net.narutomod.Particles;
import net.narutomod.PlayerInput;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemMagnetRelease;
import net.narutomod.procedure.ProcedureSync;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.MoverType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityThirdEye extends ElementsNarutomodMod.ModElement {
	public static final int ENTITYID = 518;
	public static final int ENTITYID_RANGED = 519;

	public EntityThirdEye(ElementsNarutomodMod instance) {
		super(instance, 935);
	}

	@Override
	public void initElements() {
		elements.entities.add(() -> EntityEntryBuilder.create().entity(EC.class)
				.id(new ResourceLocation("narutomod", "third_eye"), ENTITYID).name("third_eye").tracker(64, 3, true).build());
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
		elements.addNetworkMessage(RestoreCameraMessage.Handler.class, RestoreCameraMessage.class, Side.CLIENT);
	}

	public static class EC extends EntityAltCamView.EntityCustom implements PlayerInput.Hook.IHandler, ProcedureSync.RenderDistance.IHandler {
		private static final DataParameter<Integer> VIEWER_ID = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EC.class, DataSerializers.VARINT);
		private static final String ID_KEY = "ThirdEyeIdKey";
		private final PlayerInput.Hook viewerInput = new PlayerInput.Hook();
		private int oldRenderDistance;
		private boolean setupDone;

		public EC(World worldIn) {
			super(worldIn);
			this.setSize(0.25F, 0.25F);
			this.setNoGravity(true);
			this.noClip = true;
			this.isImmuneToFire = true;
		}

		public EC(EntityPlayer viewer, ItemMagnetRelease.Type sandType) {
			this(viewer.world);
			this.setViewer(viewer);
			this.setColor(sandType.getColor());
			this.setLocationAndAngles(viewer.posX, viewer.posY + 2.5D, viewer.posZ, viewer.rotationYaw, viewer.rotationPitch);
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(VIEWER_ID, Integer.valueOf(-1));
			this.dataManager.register(COLOR, Integer.valueOf(ItemMagnetRelease.Type.IRON.getColor()));
		}

		private void setViewer(EntityPlayer player) {
			if (!this.world.isRemote) {
				this.dataManager.set(VIEWER_ID, Integer.valueOf(player.getEntityId()));
			}
		}

		@Nullable
		public EntityPlayer getViewer() {
			Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(VIEWER_ID)).intValue());
			return entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
		}

		public int getColor() {
			return ((Integer)this.dataManager.get(COLOR)).intValue();
		}

		private void setColor(int color) {
			this.dataManager.set(COLOR, Integer.valueOf(color));
		}

		private void setupViewer(EntityPlayer viewer) {
			if (this.setupDone || !(viewer instanceof EntityPlayerMP)) {
				return;
			}
			EntityPlayerMP mp = (EntityPlayerMP)viewer;
			PlayerInput.Hook.copyInputFrom(mp, this, true);
			mp.connection.sendPacket(new SPacketCamera(this));
			ProcedureSync.RenderDistance.sendToSelf(mp, 16, this);
			this.setupDone = true;
		}

		@Override
		public void setDead() {
			this.restoreViewer();
			if (!this.world.isRemote) {
				Particles.spawnParticle(this.world, Particles.Types.FALLING_DUST, this.posX, this.posY, this.posZ, 40,
						0.2D, 0.2D, 0.2D, 0.0D, -0.05D, 0.0D, this.getColor(), 30, 2);
			}
			super.setDead();
		}

		private void restoreViewer() {
			EntityPlayer viewer = this.getViewer();
			if (viewer instanceof EntityPlayerMP) {
				EntityPlayerMP mp = (EntityPlayerMP)viewer;
				PlayerInput.Hook.copyInputFrom(mp, this, false);
				mp.connection.sendPacket(new SPacketCamera(mp));
				if (this.oldRenderDistance > 0) {
					ProcedureSync.RenderDistance.sendToSelf(mp, this.oldRenderDistance, null);
				}
				RestoreCameraMessage.sendTo(mp);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityPlayer viewer = this.getViewer();
			if (!this.world.isRemote) {
				if (viewer == null || !viewer.isEntityAlive()) {
					this.setDead();
					return;
				}
				this.setupViewer(viewer);
				this.updateMotionFromInput();
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.72D;
				this.motionY *= 0.72D;
				this.motionZ *= 0.72D;
				if (this.ticksExisted % 20 == 0 && this.getDistance(viewer) > 128.0F) {
					this.setDead();
				}
				if (this.ticksExisted % 4 == 0) {
					Particles.spawnParticle(this.world, Particles.Types.SAND, this.posX, this.posY, this.posZ, 3,
							0.1D, 0.1D, 0.1D, 0.0D, 0.02D, 0.0D, this.getColor(), 20, 2);
				}
			}
		}

		private void updateMotionFromInput() {
			if (this.viewerInput.hasNewMouseEvent()) {
				float sensitivity = this.viewerInput.getDX() == 0 && this.viewerInput.getDY() == 0 ? 0.0F : 0.15F;
				this.rotationYaw += this.viewerInput.getDX() * sensitivity;
				this.rotationPitch = MathHelper.clamp(this.rotationPitch + this.viewerInput.getDY() * sensitivity, -89.0F, 89.0F);
				this.viewerInput.clearMouseEvent();
			}
			if (this.viewerInput.hasNewMovementInput()) {
				Vec3d look = this.getLookVec();
				Vec3d side = new Vec3d(look.z, 0.0D, -look.x).normalize();
				double speed = this.viewerInput.isSneakKeyDown() ? 0.25D : 0.5D;
				this.motionX += look.x * this.viewerInput.getForward() * speed + side.x * this.viewerInput.getStrafe() * speed;
				this.motionY += look.y * this.viewerInput.getForward() * speed;
				this.motionZ += look.z * this.viewerInput.getForward() * speed + side.z * this.viewerInput.getStrafe() * speed;
				if (this.viewerInput.isJumpKeyDown()) {
					this.motionY += 0.35D;
				}
				if (this.viewerInput.isSneakKeyDown()) {
					this.motionY -= 0.25D;
				}
				this.viewerInput.clearMovementInput();
			}
		}

		@Override
		public boolean canBeCollidedWith() {
			return !this.isDead;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			this.setDead();
			return true;
		}

		@Override
		public void handlePacket(@Nullable PlayerInput.Hook.MovementPacket movementPacket, @Nullable PlayerInput.Hook.MousePacket mousePacket) {
			if (movementPacket != null) {
				this.viewerInput.copyMovementInput(movementPacket);
			}
			if (mousePacket != null) {
				this.viewerInput.copyMouseInput(mousePacket);
			}
		}

		@Override
		public void handleClientPacket(EntityPlayer player, int oldChunkDistance) {
			this.oldRenderDistance = oldChunkDistance;
		}

		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
			super.readEntityFromNBT(compound);
			this.setColor(compound.getInteger("color"));
		}

		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
			super.writeEntityToNBT(compound);
			compound.setInteger("color", this.getColor());
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, net.minecraft.entity.EntityLivingBase entity, float power) {
				if (!(entity instanceof EntityPlayer)) {
					return false;
				}
				Entity existing = entity.world.getEntityByID(entity.getEntityData().getInteger(ID_KEY));
				if (existing instanceof EC) {
					existing.setDead();
					return true;
				}
				EC eye = new EC((EntityPlayer)entity, ItemMagnetRelease.getSandType(stack));
				entity.world.spawnEntity(eye);
				entity.getEntityData().setInteger(ID_KEY, eye.getEntityId());
				return true;
			}

			@Override
			public void onUsingTick(ItemStack stack, net.minecraft.entity.EntityLivingBase player, float power) {
			}
		}
	}

	public static class RestoreCameraMessage implements IMessage {
		public RestoreCameraMessage() {
		}

		public static void sendTo(EntityPlayerMP player) {
			NarutomodMod.PACKET_HANDLER.sendTo(new RestoreCameraMessage(), player);
		}

		public static class Handler implements IMessageHandler<RestoreCameraMessage, IMessage> {
			@SideOnly(Side.CLIENT)
			@Override
			public IMessage onMessage(RestoreCameraMessage message, MessageContext context) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					Minecraft mc = Minecraft.getMinecraft();
					if (mc.player != null) {
						mc.setRenderViewEntity(mc.player);
					}
				});
				return null;
			}
		}

		@Override
		public void toBytes(ByteBuf buf) {
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			new PacketBuffer(buf);
		}
	}

	public static class Renderer extends EntityRendererRegister {
		@SideOnly(Side.CLIENT)
		@Override
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(EC.class, renderManager -> new RenderCustom(renderManager));
		}

		@SideOnly(Side.CLIENT)
		public class RenderCustom extends Render<EC> {
			private final ResourceLocation texture = new ResourceLocation("narutomod:textures/eyeball.png");
			private final ModelEyeball model = new ModelEyeball();

			public RenderCustom(RenderManager renderManagerIn) {
				super(renderManagerIn);
			}

			@Override
			public void doRender(EC entity, double x, double y, double z, float entityYaw, float partialTicks) {
				if (this.renderManager.renderViewEntity == entity && this.renderManager.options.thirdPersonView == 0) {
					return;
				}
				this.bindEntityTexture(entity);
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y + 0.125D, z);
				float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
				float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
				GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(0.8F, 0.8F, 0.8F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.model.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.popMatrix();
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return this.texture;
			}
		}

		@SideOnly(Side.CLIENT)
		public class ModelEyeball extends ModelBase {
			private final ModelRenderer bone;

			public ModelEyeball() {
				this.textureWidth = 32;
				this.textureHeight = 32;
				this.bone = new ModelRenderer(this);
				this.bone.setRotationPoint(0.0F, 0.0F, 0.0F);
				this.addOctagon(0.0F);
				this.addOctagon(0.7854F);
				this.addOctagon(1.5708F);
				this.addOctagon(2.3562F);
			}

			@Override
			public void render(Entity entity, float f, float f1, float age, float f3, float f4, float scale) {
				this.bone.rotateAngleX = f4 * 0.017453292F;
				this.bone.render(scale);
			}

			private void addOctagon(float zRotation) {
				ModelRenderer octagon = new ModelRenderer(this);
				octagon.setRotationPoint(0.0F, 0.0F, 0.0F);
				octagon.rotateAngleZ = zRotation;
				this.bone.addChild(octagon);
				this.addRod(octagon, 0.0F, 0, 0);
				this.addRod(octagon, -2.3562F, 14, 0);
				this.addRod(octagon, -1.5708F, 0, 14);
				this.addRod(octagon, -0.7854F, 0, 7);
			}

			private void addRod(ModelRenderer parent, float xRotation, int texU, int texV) {
				ModelRenderer rod = new ModelRenderer(this);
				rod.setRotationPoint(0.0F, 0.0F, 0.0F);
				rod.rotateAngleX = xRotation;
				parent.addChild(rod);
				rod.cubeList.add(new ModelBox(rod, texU, texV, -1.0F, -1.0F, -2.5F, 2, 2, 5, 0.06F, false));
			}
		}
	}
}
