package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;

import net.narutomod.procedure.*;
import net.narutomod.world.WorldKamuiDimension;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.ElementsNarutomodMod;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;

@ElementsNarutomodMod.ModElement.Tag
public class ItemRasheyeeternal extends ElementsNarutomodMod.ModElement {
	@ObjectHolder("narutomod:rasheyeeternalhelmet")
	public static final Item helmet = null;

	// =========================
	// Amenotejikara implementation
	// =========================
	public static class Amenotejikara implements ItemJutsu.IJutsuCallback {
		private static final String LAST_USE_KEY = "AmenotejikaraLastUse";
		private static final double CHAKRA_USAGE = 2000d;
		private static final int COOLDOWN_TICKS = 40; // 2 seconds

		@Override
		public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			if (!(entity instanceof EntityPlayer)) {
				return false;
			}
			EntityPlayer player = (EntityPlayer) entity;
			World world = player.world;

			// === Cooldown check ===
			long lastUse = stack.hasTagCompound() ? stack.getTagCompound().getLong(LAST_USE_KEY) : 0L;
			if (world.getTotalWorldTime() - lastUse < COOLDOWN_TICKS) {
				if (!world.isRemote) {
					player.sendStatusMessage(
						new TextComponentString(TextFormatting.GRAY + "Amenotejikara is recharging..."),
						true
					);
				}
				return false;
			}

			// === Chakra check ===
			Chakra.Pathway pathway = Chakra.pathway(player);
			if (pathway == null || pathway.getAmount() < CHAKRA_USAGE) {
				if (!world.isRemote) {
					player.sendStatusMessage(
						new TextComponentString(TextFormatting.RED + "Not enough Chakra!"),
						true
					);
				}
				return false;
			}

			// Raytrace 40 blocks ahead
			RayTraceResult rtr = ProcedureUtils.objectEntityLookingAt(entity, 40d);
			if (rtr == null || rtr.typeOfHit == RayTraceResult.Type.MISS) {
				if (!world.isRemote) {
					player.sendStatusMessage(
						new TextComponentString(TextFormatting.GRAY + "No target found."),
						true
					);
				}
				return false;
			}

			// Consume Chakra
			pathway.consume(CHAKRA_USAGE);

			// === Swap with entity ===
			if (rtr.typeOfHit == RayTraceResult.Type.ENTITY && rtr.entityHit != null) {
				Entity target = rtr.entityHit;

				double playerX = player.posX;
				double playerY = player.posY;
				double playerZ = player.posZ;

				double targetX = target.posX;
				double targetY = target.posY;
				double targetZ = target.posZ;

				ProcedureOnLivingUpdate.setUntargetable(player, 10);
				ProcedureOnLivingUpdate.setUntargetable(target, 10);

				// swap positions
				target.setPositionAndUpdate(playerX, playerY, playerZ);
				player.setPositionAndUpdate(targetX, targetY, targetZ);

				world.playSound(
					null,
					player.getPosition(),
					SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:swoosh")),
					SoundCategory.PLAYERS,
					1.0f,
					1.0f
				);

				if (!world.isRemote) {
					player.sendStatusMessage(
						new TextComponentString(TextFormatting.AQUA + "Swapped places using Amenotejikara!"),
						true
					);
				}

			// === Teleport to block ===
			} else if (rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos pos = rtr.getBlockPos().offset(rtr.sideHit);

				BlockPos finalPos = pos;
				// Simple safety check: try one block above if needed
				if (!world.isAirBlock(pos) || !world.isAirBlock(pos.up())) {
					finalPos = pos.up();
				}

				player.setPositionAndUpdate(
					finalPos.getX() + 0.5d,
					finalPos.getY() + 1d,
					finalPos.getZ() + 0.5d
				);

				world.playSound(
					null,
					player.getPosition(),
					SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:swoosh")),
					SoundCategory.PLAYERS,
					1.0f,
					1.0f
				);

				if (!world.isRemote) {
					player.sendStatusMessage(
						new TextComponentString(TextFormatting.AQUA + "Teleported with Amenotejikara!"),
						true
					);
				}
			}

			// === Store cooldown timestamp ===
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			stack.getTagCompound().setLong(LAST_USE_KEY, world.getTotalWorldTime());

			return true;
		}

		@Override
		public boolean isActivated(ItemStack stack) {
			// This is an instant-cast ability, not a toggle
			return false;
		}
	}

	public ItemRasheyeeternal(ElementsNarutomodMod instance) {
		super(instance, 204);
	}

	public void initElements() {
		ItemArmor.ArmorMaterial enuma = EnumHelper.addArmorMaterial(
			"MANGEKYOSHARINGANETERNAL",
			"narutomod:sasuke_",
			1024,
			new int[]{2, 5, 6, 10},
			0,
			null,
			2.0F
		);

		this.elements.items.add(() -> new ItemSharingan.Base(enuma) {
			@Override
			public void onArmorTick(World world, EntityPlayer entity, ItemStack itemstack) {
				super.onArmorTick(world, entity, itemstack);
				if (!world.isRemote) {
					entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2, 2, false, false));
					entity.capabilities.allowFlying =
						entity.isCreative() || entity.dimension == WorldKamuiDimension.DIMID;
					entity.sendPlayerAbilities();
					if (entity.getEntityData().getBoolean("kamui_teleport")) {
						Chakra.pathway(entity).consume(ItemMangekyoSharinganObito.getTeleportChakraUsage(entity));
					}
					if (entity.getEntityData().getBoolean("kamui_intangible")) {
						Chakra.pathway(entity).consume(ItemMangekyoSharinganObito.getIntangibleChakraUsage(entity));
						entity.getEntityData().setDouble(NarutomodModVariables.InvulnerableTime, 2.0d);
					}
				}
			}

			@Override
			public boolean isMangekyo() {
				return true;
			}

			@Override
			public boolean isEternal() {
				return true;
			}

			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
				return "narutomod:textures/rasheyehelmet.png";
			}

			@Override
			public int getMaxDamage() {
				return 0;
			}

			@Override
			public boolean isDamageable() {
				return false;
			}

			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
				super.addInformation(stack, worldIn, tooltip, flagIn);
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu1") + ": " +
					TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.amaterasu.jutsu1"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu2") + ": " +
					TextFormatting.GRAY + I18n.translateToLocal("entity.susanooclothed.name"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu3") + ": " +
					TextFormatting.GRAY + I18n.translateToLocal("tooltip.mangekyo.kamui.jutsu1"));
				tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("key.mcreator.specialjutsu4") + ": " +
					TextFormatting.GRAY + "Amenotejikara");
			}

			@Override
			public boolean onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				Map<String, Object> $_dependencies = Maps.newHashMap();
				$_dependencies.put("is_pressed", is_pressed);
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", entity.world);
				$_dependencies.put("x", (int) entity.posX);
				$_dependencies.put("y", (int) entity.posY);
				$_dependencies.put("z", (int) entity.posZ);
				ProcedureAmaterasu.executeProcedure($_dependencies);
				return true;
			}

			@Override
			public boolean onJutsuKey2(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				if (!is_pressed) {
					Map<String, Object> $_dependencies = Maps.newHashMap();
					$_dependencies.put("entity", entity);
					$_dependencies.put("world", entity.world);
					ProcedureSusanoo.executeProcedure($_dependencies);
				}
				return true;
			}

			@Override
			public boolean onJutsuKey3(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				Map<String, Object> $_dependencies = Maps.newHashMap();
				$_dependencies.put("is_pressed", is_pressed);
				$_dependencies.put("entity", entity);
				$_dependencies.put("world", entity.world);
				if (entity.world.provider.getDimension() == WorldKamuiDimension.DIMID && !entity.isSneaking()) {
					ProcedureGrabEntity.executeProcedure($_dependencies);
				} else {
					$_dependencies.put("x", (int) entity.posX);
					$_dependencies.put("y", (int) entity.posY);
					$_dependencies.put("z", (int) entity.posZ);
					ProcedureKamuiJikukanIdo.executeProcedure($_dependencies);
				}
				return true;
			}

			// New Amenotejikara ability (Key 4)
			public boolean onJutsuKey4(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				// Fire on key release, server-side only
				if (!is_pressed && !entity.world.isRemote) {
					new Amenotejikara().createJutsu(stack, entity, 1.0f);
				}
				return true;
			}

			@Override
			public boolean onSwitchJutsuKey(boolean is_pressed, ItemStack stack, EntityPlayer entity) {
				if (entity.getRidingEntity() instanceof EntitySusanooBase) {
					if (!is_pressed) {
						ProcedureSusanoo.upgrade(entity);
					}
					return true;
				}
				return false;
			}
		}.setUnlocalizedName("rasheyeeternalhelmet").setRegistryName("rasheyeeternalhelmet").setCreativeTab(TabModTab.tab));
	}

	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(
			Item.getByNameOrId("narutomod:rasheyeeternalhelmet"),
			0,
			new ModelResourceLocation("narutomod:rasheyeeternalhelmet", "inventory")
		);
	}
}
