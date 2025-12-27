package net.narutomod.entity;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.DataSerializers;

import net.narutomod.potion.PotionHeaviness;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.item.ItemJutsu;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;

@ElementsNarutomodMod.ModElement.Tag
public class EntityBloodSiphonChains extends ElementsNarutomodMod.ModElement {
	// === SAFE UNIQUE IDS ===
	public static final int ENTITYID = 940;
	public static final int ENTITYID_RANGED = 941;

	public EntityBloodSiphonChains(ElementsNarutomodMod instance) {
		super(instance, 940);
	}

	@Override
	public void initElements() {
		elements.entities.add(() ->
			EntityEntryBuilder.create()
				.entity(EC.class)
				.id(new ResourceLocation("narutomod", "blood_siphon_chains"), ENTITYID)
				.name("blood_siphon_chains")
				.tracker(64, 3, true)
				.build()
		);
	}

	public static class EC extends EntityBeamBase.Base implements ItemJutsu.IJutsu {
		private static final DataParameter<Integer> TARGET_ID = EntityDataManager.createKey(EC.class, DataSerializers.VARINT);
		private static final DataParameter<Float> TARGET_OFFX = EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> TARGET_OFFY = EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);
		private static final DataParameter<Float> TARGET_OFFZ = EntityDataManager.createKey(EC.class, DataSerializers.FLOAT);

		private double initialDistance;
		private int slowAmplifier;
		private final double chakraDrainPerTick = 10.0d;
		private int expireTime = 400; // 20 seconds (400 ticks)

		public EC(World worldIn) {
			super(worldIn);
			this.isImmuneToFire = true;
		}

		public EC(EntityLivingBase shooter, EntityLivingBase targetIn) {
			super(shooter);
			this.setTarget(targetIn);
			this.updatePosition();
		}

		@Override
		public ItemJutsu.JutsuEnum.Type getJutsuType() {
			return ItemJutsu.JutsuEnum.Type.BLOOD;
		}

		@Override
		protected void entityInit() {
			super.entityInit();
			this.dataManager.register(TARGET_ID, -1);
			this.dataManager.register(TARGET_OFFX, 0.0F);
			this.dataManager.register(TARGET_OFFY, 0.0F);
			this.dataManager.register(TARGET_OFFZ, 0.0F);
		}

		@Nullable
		private EntityLivingBase getTarget() {
			Entity e = this.world.getEntityByID(this.dataManager.get(TARGET_ID));
			return e instanceof EntityLivingBase ? (EntityLivingBase)e : null;
		}

		private void setTarget(EntityLivingBase targetIn) {
			this.dataManager.set(TARGET_ID, targetIn.getEntityId());
			this.dataManager.set(TARGET_OFFX, 0.0F);
			this.dataManager.set(TARGET_OFFY, targetIn.getEyeHeight());
			this.dataManager.set(TARGET_OFFZ, 0.0F);
			this.initialDistance = this.getDistance(targetIn) - 1d;
			this.slowAmplifier = 1;
		}

		@Override
		protected void updatePosition() {
			EntityLivingBase shooter = this.getShooter();
			if (shooter != null) {
				this.setPosition(shooter.posX, shooter.posY + shooter.height / 2, shooter.posZ);
			}
		}

		@Override
		public void onUpdate() {
			super.onUpdate();
			EntityLivingBase target = this.getTarget();

			// Kill instantly if shooter or target invalid
			if (this.shootingEntity == null || target == null || !target.isEntityAlive()) {
				if (!this.world.isRemote) this.setDead();
				return;
			}

			// Expire after 20 seconds (400 ticks)
			if (this.ticksExisted > expireTime && !this.world.isRemote) {
				this.setDead();
				return;
			}

			// Every second (20 ticks)
			if (this.ticksExisted % 20 == 0) {
				// Slow effect
				target.addPotionEffect(new PotionEffect(PotionHeaviness.potion, 22, this.slowAmplifier));
				// Chakra drain
				Chakra.pathway(target).consume(this.chakraDrainPerTick);

				// Damage & heal
				float damage = 4.0f;      // === 2 hearts per sec ===
				float healAmount = 2.0f;  // === heal user 1 heart per sec ===

				target.attackEntityFrom(net.minecraft.util.DamageSource.causeIndirectDamage(this, this.getShooter()), damage);

				if (this.getShooter() instanceof EntityLivingBase) {
					((EntityLivingBase)this.getShooter()).heal(healAmount);
				}
			}
		}

		public static class Jutsu implements ItemJutsu.IJutsuCallback {
			@Override
			public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
				RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 50d);
				if (res != null && res.entityHit instanceof EntityLivingBase) {
					entity.world.spawnEntity(new EC(entity, (EntityLivingBase)res.entityHit));
					return true;
				}
				return false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		new Renderer().register();
	}

	public static class Renderer extends EntityRendererRegister {
		@Override
		@SideOnly(Side.CLIENT)
		public void register() {
			RenderingRegistry.registerEntityRenderingHandler(
				EC.class,
				rm -> new CustomRender(rm)
			);
		}

		@SideOnly(Side.CLIENT)
		public class CustomRender extends EntityBeamBase.Renderer<EC> {
			private final ResourceLocation texture =
				new ResourceLocation("narutomod:textures/chainlink_blood.png"); // swap to red png later

			public CustomRender(RenderManager renderManager) {
				super(renderManager);
			}

			@Override
			protected ResourceLocation getEntityTexture(EC entity) {
				return texture;
			}
		}
	}
}
