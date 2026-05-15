
package net.narutomod.item;

//import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.world.World;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.EnumAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.Minecraft;

import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;
import net.narutomod.procedure.ProcedureOnLivingUpdate;
import net.narutomod.procedure.ProcedureUpdateworldtick;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.PlayerTracker;
import net.narutomod.ElementsNarutomodMod.ModElement.Tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.annotation.Nullable;

@Tag
public class ItemJutsu extends ElementsNarutomodMod.ModElement {
	public static final String NINJUTSU_TYPE = "ninja_arts";
	public static final String SENJUTSU_TYPE = "sage_arts";
	public static final String LEGACY_NINJUTSU_TYPE = "ninjutsu";
	public static final String LEGACY_SENJUTSU_TYPE = "senjutsu";
	public static final DamageSource NINJUTSU_DAMAGE = new DamageSource(NINJUTSU_TYPE);
	public static final DamageSource SENJUTSU_DAMAGE = new DamageSource(SENJUTSU_TYPE);
	private static final Map<String, JutsuEnum.Type> JUTSU_TYPE_ALIASES = new HashMap<>();
	private static final Map<String, String> RELEASE_ITEM_ALIASES = new HashMap<>();
	private static final Map<JutsuEnum.Type, List<JutsuEnum>> EXTRA_JUTSUS = new HashMap<>();
	private static final Set<String> SYNCED_EXTERNAL_JUTSU_FIELDS = new HashSet<>();
	private static boolean externalInjectedJutsuFieldsScanned;
	private static boolean shinobiAddonJutsusInjected;

	public ItemJutsu(ElementsNarutomodMod instance) {
		super(instance, 369);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new Base.EquipmentHook());
	}

	public void postInit(FMLPostInitializationEvent event) {
		injectShinobiAddonReleaseJutsus();
		syncExternalInjectedJutsuFields();
	}
	
	public static DamageSource causeJutsuDamage(Entity source, @Nullable Entity indirectEntityIn) {
		return indirectEntityIn != null ? new EntityDamageSourceIndirect(NINJUTSU_TYPE, source, indirectEntityIn)
		 : new EntityDamageSource(NINJUTSU_TYPE, source);
	}

	public static DamageSource causeSenjutsuDamage(Entity source, @Nullable Entity indirectEntityIn) {
		return indirectEntityIn != null ? new EntityDamageSourceIndirect(SENJUTSU_TYPE, source, indirectEntityIn).setDamageIsAbsolute()
		 : new EntityDamageSource(SENJUTSU_TYPE, source).setDamageIsAbsolute();
	}

	public static boolean isDamageSourceNinjutsu(DamageSource source) {
		return source.getDamageType().equals(NINJUTSU_TYPE) || source.getDamageType().equals(LEGACY_NINJUTSU_TYPE);
	}

	public static boolean isDamageSourceSenjutsu(DamageSource source) {
		return source.getDamageType().equals(SENJUTSU_TYPE) || source.getDamageType().equals(LEGACY_SENJUTSU_TYPE);
	}

	public static boolean isDamageSourceJutsu(DamageSource source) {
		return isDamageSourceNinjutsu(source) || isDamageSourceSenjutsu(source);
	}

	public static boolean canTarget(@Nullable Entity targetIn) {
		return targetIn != null && targetIn.isEntityAlive() 
		 //&& !targetIn.getEntityData().getBoolean("kamui_intangible")
		 && !ProcedureOnLivingUpdate.isUntargetable(targetIn)
		 && (!(targetIn instanceof EntityPlayer) || !((EntityPlayer)targetIn).isSpectator());
	}

	@Nullable
	public static ItemStack getOwnerMatchingItemstack(EntityPlayer entity, Item itemIn) {
		ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, itemIn);
		return stack == null || (stack.getItem() instanceof Base && ((Base)stack.getItem()).isOwner(stack, entity)) ? stack : null;
	}

	public static boolean hasOwnerMatchingItemstack(EntityPlayer entity, Item itemIn) {
		return getOwnerMatchingItemstack(entity, itemIn) != null;
	}

	public static void setCurrentJutsuCooldown(ItemStack stack, EntityLivingBase player, long cd) {
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).setCurrentJutsuCooldown(stack, (long)((double)cd * ((Base)stack.getItem()).getModifier(stack, player)));
		}
	}

	public static void setCurrentJutsuCooldown(ItemStack stack, long cd) {
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).setCurrentJutsuCooldown(stack, cd);
		}
	}

	public static void setJutsuCooldown(ItemStack stack, EntityLivingBase entity, JutsuEnum jutsuIn, long cd) {
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).setJutsuCooldown(stack, jutsuIn, (long)((double)cd * ((Base)stack.getItem()).getModifier(stack, entity)));
		}
	}

	public static void logBattleXP(EntityPlayer player) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!(stack.getItem() instanceof Base)) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() instanceof Base) {
			Base baseitem = (Base)stack.getItem();
			if (baseitem.getCurrentJutsuXp(stack) < baseitem.getCurrentJutsuRequiredXp(stack)) {
				baseitem.addCurrentJutsuXp(stack, 1);
			}
		}
	}

	public static void addBattleXP(EntityPlayer player, int xp) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!(stack.getItem() instanceof Base)) {
			stack = player.getHeldItemOffhand();
		}
		if (stack.getItem() instanceof Base) {
			((Base)stack.getItem()).addCurrentJutsuXp(stack, xp);
		}
	}

	public static JutsuEnum getCurrentJutsu(ItemStack stack) {
		return stack.getItem() instanceof Base ? ((Base)stack.getItem()).getCurrentJutsu(stack) : null;
	}

	public static double getMaxPower(EntityLivingBase entity, double jutsuCkakraUsage) {
		return Chakra.pathway(entity).getAmount() / jutsuCkakraUsage * 0.9999;
	}

	public static JutsuEnum.Type getCompatibleJutsuType(String typeName) {
		if (typeName == null) {
			return JutsuEnum.Type.OTHER;
		}
		String key = normalizeJutsuTypeName(typeName);
		JutsuEnum.Type type = JUTSU_TYPE_ALIASES.get(key);
		if (type != null) {
			return type;
		}
		try {
			return JutsuEnum.Type.valueOf(key.toUpperCase());
		} catch (IllegalArgumentException e) {
			return JutsuEnum.Type.OTHER;
		}
	}

	public static JutsuEnum.Type getCompatibleJutsuType(JutsuEnum.Type typeIn) {
		return typeIn != null ? typeIn : JutsuEnum.Type.OTHER;
	}

	public static String getCompatibleReleaseItemId(String itemId) {
		if (itemId == null) {
			return null;
		}
		String key = normalizeJutsuTypeName(itemId);
		String alias = RELEASE_ITEM_ALIASES.get(key);
		return alias != null ? alias : key;
	}

	@Nullable
	public static Item getCompatibleReleaseItem(String itemId) {
		String compatibleId = getCompatibleReleaseItemId(itemId);
		return compatibleId != null ? Item.REGISTRY.getObject(new ResourceLocation("narutomod", compatibleId)) : null;
	}

	public static void registerJutsu(String typeName, JutsuEnum... jutsuListIn) {
		registerJutsu(getCompatibleJutsuType(typeName), jutsuListIn);
	}

	public static void registerJutsu(JutsuEnum.Type typeIn, JutsuEnum... jutsuListIn) {
		JutsuEnum.Type type = getCompatibleJutsuType(typeIn);
		if (jutsuListIn == null || jutsuListIn.length == 0) {
			return;
		}
		List<JutsuEnum> list = EXTRA_JUTSUS.get(type);
		if (list == null) {
			list = Lists.newArrayList();
			EXTRA_JUTSUS.put(type, list);
		}
		for (JutsuEnum jutsu : jutsuListIn) {
			if (jutsu != null && !list.contains(jutsu)) {
				list.add(jutsu);
			}
		}
		for (Item item : Item.REGISTRY) {
			if (item instanceof Base && ((Base)item).getJutsuType() == type) {
				((Base)item).addCompatibleJutsus(jutsuListIn);
			}
		}
	}

	@Deprecated
	public static void addJutsu(String typeName, JutsuEnum... jutsuListIn) {
		registerJutsu(typeName, jutsuListIn);
	}

	@Deprecated
	public static void addJutsu(JutsuEnum.Type typeIn, JutsuEnum... jutsuListIn) {
		registerJutsu(typeIn, jutsuListIn);
	}

	private static JutsuEnum[] appendRegisteredJutsus(JutsuEnum.Type typeIn, JutsuEnum[] jutsuListIn) {
		List<JutsuEnum> extras = EXTRA_JUTSUS.get(getCompatibleJutsuType(typeIn));
		if (extras == null || extras.isEmpty()) {
			return jutsuListIn;
		}
		List<JutsuEnum> merged = Lists.newArrayList();
		for (JutsuEnum jutsu : jutsuListIn) {
			merged.add(jutsu);
		}
		for (JutsuEnum jutsu : extras) {
			if (!merged.contains(jutsu)) {
				merged.add(jutsu);
			}
		}
		return merged.toArray(new JutsuEnum[merged.size()]);
	}

	private static String normalizeJutsuTypeName(String typeName) {
		String key = typeName.toLowerCase().replace("narutomod:", "").replace('-', '_').replace(' ', '_');
		while (key.contains("__")) {
			key = key.replace("__", "_");
		}
		return key;
	}

	private static void addJutsuTypeAlias(JutsuEnum.Type type, String... aliases) {
		for (String alias : aliases) {
			JUTSU_TYPE_ALIASES.put(normalizeJutsuTypeName(alias), type);
		}
	}

	private static void addReleaseItemAlias(String canonicalId, String... aliases) {
		RELEASE_ITEM_ALIASES.put(normalizeJutsuTypeName(canonicalId), normalizeJutsuTypeName(canonicalId));
		for (String alias : aliases) {
			RELEASE_ITEM_ALIASES.put(normalizeJutsuTypeName(alias), normalizeJutsuTypeName(canonicalId));
		}
	}

	private static void injectShinobiAddonReleaseJutsus() {
		if (shinobiAddonJutsusInjected) {
			syncExternalInjectedJutsuFields();
			return;
		}
		try {
			Class.forName("com.leolifeless.shinobiaddon.ShinobiAddon");
		} catch (ClassNotFoundException e) {
			syncExternalInjectedJutsuFields();
			return;
		}
		addExternalJutsuToItem("fire_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "PHOENIX_SAGE_FIRE", "phoenix_sage_fire", 'C', 25d,
		 "com.leolifeless.shinobiaddon.jutsu.fire.PhoenixSageFireJutsu");
		addExternalJutsuToItem("fire_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "FIRE_DRAGON_BULLET", "fire_dragon_bullet", 'B', 450d,
		 "com.leolifeless.shinobiaddon.jutsu.fire.FireDragonBullet");
		addExternalJutsuToItem("fire_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "FIRE_CHAKRA_MODE", "fire_chakra_mode", 'B', 20d,
		 "com.leolifeless.shinobiaddon.jutsu.fire.FireChakraMode");
		addExternalJutsuToItem("fire_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "FLAME_RASENGAN", "flame_rasengan", 'A', 800d,
		 "com.leolifeless.shinobiaddon.jutsu.fire.FlameRasenganJutsu");
		addExternalJutsuToItem("water_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "GREAT_WATERFALL", "great_waterfall", 'A', 300d,
		 "com.leolifeless.shinobiaddon.jutsu.water.GreatWaterfallJutsu");
		addExternalJutsuToItem("water_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "WATER_BEAST", "water_beast", 'B', 200d,
		 "com.leolifeless.shinobiaddon.jutsu.water.WaterBeastJutsu");
		addExternalJutsuToItem("water_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "WATER_QUAKING_PILLAR", "water_quaking_pillar", 'B', 120d,
		 "com.leolifeless.shinobiaddon.jutsu.water.WaterQuakingPillarJutsu");
		addExternalJutsuToItem("crystal_release", "com.leolifeless.shinobiaddon.jutsu.ShinobiAddonInjectedJutsu",
		 "TEARING_CRSYTAL_FALLING_DRAGON", "tearing_crystal_falling_dragon", 'A', 1250d,
		 "com.leolifeless.shinobiaddon.jutsu.crystal.CrystalFallingDragonJutsu");
		shinobiAddonJutsusInjected = true;
		syncExternalInjectedJutsuFields();
	}

	private static void syncExternalInjectedJutsuFields() {
		if (externalInjectedJutsuFieldsScanned) {
			return;
		}
		externalInjectedJutsuFieldsScanned = true;
		for (String className : findInjectedJutsuHolderClasses()) {
			try {
				Class<?> holderClass = Class.forName(className, false, ItemJutsu.class.getClassLoader());
				for (Field field : holderClass.getFields()) {
					if (!Modifier.isStatic(field.getModifiers()) || field.getType() != JutsuEnum.class) {
						continue;
					}
					JutsuEnum jutsu = (JutsuEnum)field.get(null);
					if (jutsu == null) {
						continue;
					}
					String itemId = inferInjectedJutsuTargetItem(field, jutsu);
					if (itemId == null) {
						continue;
					}
					Item item = getCompatibleReleaseItem(itemId);
					if (!(item instanceof Base)) {
						continue;
					}
					Base base = (Base)item;
					String syncKey = holderClass.getName() + "#" + field.getName() + "->" + itemId;
					if (SYNCED_EXTERNAL_JUTSU_FIELDS.contains(syncKey)) {
						continue;
					}
					JutsuEnum added = base.addCompatibleJutsu(jutsu);
					if (added != null) {
						field.set(null, added);
						SYNCED_EXTERNAL_JUTSU_FIELDS.add(syncKey);
					}
				}
			} catch (Throwable e) {
				System.err.println("Failed to sync injected jutsu holder " + className + ": " + e.getMessage());
			}
		}
	}

	private static Set<String> findInjectedJutsuHolderClasses() {
		Set<String> classes = new HashSet<>();
		for (ModContainer mod : Loader.instance().getActiveModList()) {
			File source = mod.getSource();
			if (source == null || !source.isFile() || !source.getName().endsWith(".jar")) {
				continue;
			}
			JarFile jar = null;
			try {
				jar = new JarFile(source);
				java.util.Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String name = entry.getName();
					if (!name.endsWith(".class") || name.indexOf('$') >= 0) {
						continue;
					}
					String simpleName = name.substring(name.lastIndexOf('/') + 1, name.length() - ".class".length());
					if (simpleName.toLowerCase().contains("injectedjutsu")) {
						classes.add(name.substring(0, name.length() - ".class".length()).replace('/', '.'));
					}
				}
			} catch (Exception e) {
				System.err.println("Failed to scan injected jutsu classes in " + source + ": " + e.getMessage());
			} finally {
				if (jar != null) {
					try {
						jar.close();
					} catch (Exception ignored) {
					}
				}
			}
		}
		return classes;
	}

	@Nullable
	private static String inferInjectedJutsuTargetItem(Field field, JutsuEnum jutsu) {
		String text = (field.getName() + " " + jutsu.unlocalizedName + " "
		 + jutsu.jutsu.getClass().getName() + " " + String.valueOf(jutsu.getType())).toLowerCase();
		if (text.contains("suiton") || text.contains("water")) return "water_release";
		if (text.contains("katon") || text.contains("fire") || text.contains("flame") || text.contains("phoenix")) return "fire_release";
		if (text.contains("doton") || text.contains("earth")) return "earth_release";
		if (text.contains("futon") || text.contains("wind")) return "wind_release";
		if (text.contains("raiton") || text.contains("lightning") || text.contains("thunder")) return "lightning_release";
		if (text.contains("mokuton") || text.contains("wood")) return "wood_release";
		if (text.contains("hyoton") || text.contains("ice")) return "ice_release";
		if (text.contains("bakuton") || text.contains("explosion")) return "explosion_release";
		if (text.contains("shakuton") || text.contains("scorch")) return "scorch_release";
		if (text.contains("shoton") || text.contains("crystal")) return "crystal_release";
		if (text.contains("ranton") || text.contains("storm")) return "storm_release";
		if (text.contains("jiton") || text.contains("magnet")) return "magnet_release";
		if (text.contains("futton") || text.contains("boil")) return "boil_release";
		if (text.contains("yooton") || text.contains("lava")) return "lava_release";
		if (text.contains("jinton") || text.contains("dust")) return "dust_release";
		if (text.contains("senjutsu") || text.contains("sage")) return "sage_arts";
		if (text.contains("ninjutsu") || text.contains("ninja")) return "ninja_arts";
		if (text.contains("inton") || text.contains("yin")) return "yin_release";
		if (text.contains("yoton") || text.contains("yang")) return "yang_release";
		if (text.contains("iryo") || text.contains("medical")) return "medical_ninjutsu";
		return null;
	}

	private static void addExternalJutsuToItem(String itemId, String holderClassName, String fieldName,
			String jutsuName, char rank, double chakraUsage, String callbackClassName) {
		Item item = getCompatibleReleaseItem(itemId);
		if (!(item instanceof Base)) {
			return;
		}
		Base base = (Base)item;
		try {
			Class<?> holderClass = Class.forName(holderClassName);
			Field field = holderClass.getField(fieldName);
			JutsuEnum jutsu = (JutsuEnum)field.get(null);
			if (jutsu == null) {
				Object callback = Class.forName(callbackClassName).newInstance();
				if (!(callback instanceof IJutsuCallback)) {
					return;
				}
				jutsu = new JutsuEnum(base.getJutsuCount(), jutsuName, rank, chakraUsage, (IJutsuCallback)callback);
			}
			field.set(null, base.addCompatibleJutsu(jutsu));
		} catch (Exception e) {
			System.err.println("Failed to add external jutsu " + fieldName + " to " + itemId + ": " + e.getMessage());
		}
	}
	
	public abstract static class Base extends Item {
		private static final String JUTSU_INDEX_KEY = "JutsuIndexKey";
		private static final String CDMAP_KEY = "JutsuCDMapKey";
		private static final String XPMAP_KEY = "JutsuExperienceMapKey";
		private static final String OWNER_ID_KEY = "OwnerIdKey";
		private static final String AFFINITY_KEY = "IsNatureAffinityKey";
		private final JutsuEnum.Type jutsuType;
		private ImmutableList<JutsuEnum> jutsuList;
		protected long[] defaultCooldownMap;
		private int[] jutsuXpMap;
	
		public Base(JutsuEnum.Type typeIn, JutsuEnum... jutsuListIn) {
			super();
			typeIn = ItemJutsu.getCompatibleJutsuType(typeIn);
			this.jutsuType = typeIn;
			if (jutsuListIn.length > 0) {
				this.setMaxDamage(0);
				this.setFull3D();
				this.maxStackSize = 1;
				this.setCompatibleJutsuList(jutsuListIn);
				List<JutsuEnum> extras = EXTRA_JUTSUS.get(this.jutsuType);
				if (extras != null && !extras.isEmpty()) {
					this.addCompatibleJutsus(extras.toArray(new JutsuEnum[extras.size()]));
				}
			} else {
				throw new IllegalArgumentException("Empty jutsu list!");
			}
		}

		public JutsuEnum.Type getJutsuType() {
			return this.jutsuType;
		}

		public JutsuEnum[] getCompatibleJutsus() {
			return this.jutsuList.toArray(new JutsuEnum[this.jutsuList.size()]);
		}

		public int getJutsuCount() {
			return this.jutsuList.size();
		}

		private void setCompatibleJutsuList(JutsuEnum... jutsuListIn) {
			this.defaultCooldownMap = new long[jutsuListIn.length];
			this.jutsuXpMap = new int[jutsuListIn.length];
			for (int i = 0; i < jutsuListIn.length; i++) {
				this.defaultCooldownMap[i] = -1L;
				this.jutsuXpMap[i] = 0;
				jutsuListIn[i].setType(this.jutsuType);
			}
			this.jutsuList = ImmutableList.copyOf(jutsuListIn);
		}

		public void addCompatibleJutsus(JutsuEnum... jutsuListIn) {
			for (JutsuEnum jutsu : jutsuListIn) {
				this.addCompatibleJutsu(jutsu);
			}
		}

		public JutsuEnum addCompatibleJutsu(JutsuEnum jutsuIn) {
			if (jutsuIn == null) {
				return null;
			}
			for (JutsuEnum existing : this.jutsuList) {
				if (existing == jutsuIn || existing.unlocalizedName.equals(jutsuIn.unlocalizedName)) {
					return existing;
				}
			}
			JutsuEnum copy = jutsuIn.copyForIndex(this.jutsuList.size());
			copy.setType(this.jutsuType);
			List<JutsuEnum> merged = Lists.newArrayList(this.jutsuList);
			merged.add(copy);
			long[] oldCooldownMap = this.defaultCooldownMap;
			int[] oldJutsuXpMap = this.jutsuXpMap;
			this.jutsuList = ImmutableList.copyOf(merged);
			this.defaultCooldownMap = new long[merged.size()];
			this.jutsuXpMap = new int[merged.size()];
			for (int i = 0; i < this.defaultCooldownMap.length; i++) {
				this.defaultCooldownMap[i] = i < oldCooldownMap.length ? oldCooldownMap[i] : -1L;
				this.jutsuXpMap[i] = i < oldJutsuXpMap.length ? oldJutsuXpMap[i] : 0;
			}
			return copy;
		}

		/*public void addCompatibleJutsus(JutsuEnum... jutsuListIn) {
			if (jutsuListIn == null || jutsuListIn.length == 0) {
				return;
			}
			List<JutsuEnum> merged = Lists.newArrayList(this.jutsuList);
			for (JutsuEnum jutsu : jutsuListIn) {
				if (jutsu != null && !merged.contains(jutsu)) {
					JutsuEnum copy = jutsu.copyForIndex(merged.size());
					copy.setType(this.jutsuType);
					merged.add(copy);
				}
			}
			if (merged.size() == this.jutsuList.size()) {
				return;
			}
			long[] oldCooldownMap = this.defaultCooldownMap;
			int[] oldJutsuXpMap = this.jutsuXpMap;
			this.jutsuList = ImmutableList.copyOf(merged);
			this.defaultCooldownMap = new long[merged.size()];
			this.jutsuXpMap = new int[merged.size()];
			for (int i = 0; i < this.defaultCooldownMap.length; i++) {
				this.defaultCooldownMap[i] = i < oldCooldownMap.length ? oldCooldownMap[i] : -1L;
				this.jutsuXpMap[i] = i < oldJutsuXpMap.length ? oldJutsuXpMap[i] : 0;
			}
		}*/

		protected boolean executeJutsu(ItemStack stack, EntityLivingBase entity, float power) {
			JutsuEnum jutsuEnum = this.getCurrentJutsu(stack);
			Chakra.Pathway pw = Chakra.pathway(entity);
			double d = jutsuEnum.chakraUsage * (double)power;
			if (power <= 0.0f || pw.getAmount() < d) {
				return false;
			}
			if (jutsuEnum.jutsu.createJutsu(stack, entity, power)) {
				pw.consume(d);
				return true;
			}
			return false;
		}

		public float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft) {
            JutsuEnum jutsuEnum = this.getCurrentJutsu(stack);
            float base = jutsuEnum.jutsu.getBasePower();
            float delay = jutsuEnum.jutsu.getPowerupDelay(stack, entity);
            return delay > 0.0F ? this.getPower(stack, entity, timeLeft, base, delay) : base;
		}

		protected float getPower(ItemStack stack, EntityLivingBase entity, int timeLeft, float basePower, float powerupDelay) {
			//boolean flag = entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative();
			//int i = flag ? this.getCurrentJutsuRequiredXp(stack) : this.getCurrentJutsuXp(stack);
			//float xpmodifier = i != 0 ? (float)this.getCurrentJutsuRequiredXp(stack) / (float)i : 0f;
			//float xpmodifier = this.getCurrentJutsuXpModifier(stack, entity);
			float f = powerupDelay * this.getModifier(stack, entity);
			return f > 0f ? Math.min(basePower + (float)(this.getMaxUseDuration() - timeLeft) / f, this.getMaxPower(stack, entity)) : 0f;
		}

		public float getModifier(ItemStack stack, EntityLivingBase entity) {
			return (float)Chakra.getChakraModifier(entity) * this.getCurrentJutsuXpModifier(stack, entity);
		}

		public float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			//return (float)ItemJutsu.getMaxPower(entity, this.getCurrentJutsu(stack).chakraUsage);
			JutsuEnum jutsuEnum = this.getCurrentJutsu(stack);
			float mp = (float)ItemJutsu.getMaxPower(entity, jutsuEnum.chakraUsage);
			return Math.min(mp, jutsuEnum.jutsu.getMaxPower(stack, entity));
		}

		@Override
		public void onUsingTick(ItemStack stack, EntityLivingBase player, int timeLeft) {
			if (!player.world.isRemote && (!(player instanceof EntityPlayer) || PlayerTracker.isNinja((EntityPlayer)player))) {
				this.getCurrentJutsu(stack).jutsu.onUsingTick(stack, player, this.getPower(stack, player, timeLeft));
			}
		}

		@Override
		public void onPlayerStoppedUsing(ItemStack itemstack, World world, EntityLivingBase entity, int timeLeft) {
			if (!world.isRemote && this.executeJutsu(itemstack, entity, this.getPower(itemstack, entity, timeLeft))) {
				this.addCurrentJutsuXp(itemstack, 1);
				if (entity instanceof EntityPlayer) {
					((EntityPlayer)entity).addExhaustion(0.4f);
				}
			}
		}

		private void resetJutsuMaps(ItemStack stack) {
			this.resetCooldownMap(stack);
			this.resetJutsuXpMap(stack);
		}

		private void resetJutsuXpMap(ItemStack stack) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setIntArray(XPMAP_KEY, this.jutsuXpMap);
		}
	
		private void resetCooldownMap(ItemStack stack) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			for (int i = 0; i < this.defaultCooldownMap.length; i++) 
				stack.getTagCompound().setLong(CDMAP_KEY+i, this.defaultCooldownMap[i]);
		}

		private void validateMapTags(ItemStack stack, int index) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			if (index >= 0 && !stack.getTagCompound().hasKey(CDMAP_KEY+index))
				stack.getTagCompound().setLong(CDMAP_KEY+index, this.defaultCooldownMap[index]);
			if (!stack.getTagCompound().hasKey(XPMAP_KEY))
				stack.getTagCompound().setIntArray(XPMAP_KEY, this.jutsuXpMap);
		}

		private int[] getJutsuXpMap(ItemStack stack) {
			this.validateMapTags(stack, -1);
			int[] xpmap = stack.getTagCompound().getIntArray(XPMAP_KEY);
			if (xpmap.length < this.jutsuList.size()) {
				int[] map2 = xpmap;
				xpmap = new int[this.jutsuList.size()];
				for (int i = 0; i < map2.length; i++) {
					xpmap[i] = map2[i];
				}
			}
			return xpmap;
		}

		private int getJutsuXp(ItemStack stack, int index) {
			return this.getJutsuXpMap(stack)[index];
		}

		public int getJutsuXp(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) ? this.getJutsuXp(stack, jutsuIn.index) : 0;
		}

		public int getCurrentJutsuXp(ItemStack stack) {
			return this.getJutsuXp(stack, this.getCurrentJutsuIndex(stack));
		}

		private void addJutsuXp(ItemStack stack, int index, int xp) {
			int[] xpmap = this.getJutsuXpMap(stack);
			xpmap[index] += xp;
			stack.getTagCompound().setIntArray(XPMAP_KEY, xpmap);
		}

		public void addJutsuXp(ItemStack stack, JutsuEnum jutsuIn, int xp) {
			if (this.jutsuList.contains(jutsuIn)) {
				this.addJutsuXp(stack, jutsuIn.index,
				 Math.min(this.getRequiredXp(stack, jutsuIn.index) * 3 - this.getJutsuXp(stack, jutsuIn.index), xp));
			}
		}

		public void addCurrentJutsuXp(ItemStack stack, int xp) {
			this.addJutsuXp(stack, this.getCurrentJutsuIndex(stack),
			 Math.min(this.getCurrentJutsuRequiredXp(stack) * 3 - this.getCurrentJutsuXp(stack), xp));
		}

		private int getRequiredXp(ItemStack stack, int index) {
			int requiredXp = this.jutsuList.get(index).requiredXP;
			return this.isAffinity(stack) ? requiredXp : (int)((float)requiredXp * 2.5f);
		}

		public int getRequiredXp(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) ? this.getRequiredXp(stack, jutsuIn.index) : -1;
		}

		public float getXpRatio(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) ?
			 (float)this.getJutsuXp(stack, jutsuIn.index) / (float)this.getRequiredXp(stack, jutsuIn.index) : 0;
		}

		public int getCurrentJutsuRequiredXp(ItemStack stack) {
			return this.getRequiredXp(stack, this.getCurrentJutsuIndex(stack));
		}
	
		public float getCurrentJutsuXpModifier(ItemStack stack, EntityLivingBase entity) {
			int required = this.getCurrentJutsuRequiredXp(stack);
			int has = this.getCurrentJutsuXp(stack);
			if ((!(entity instanceof EntityPlayer) || ((EntityPlayer)entity).isCreative()) && has < required) {
				has = required;
			}
			return has < required ? 1000000f : (float)required / (float)has;
		}

		public boolean canUseAnyJutsu(ItemStack stack) {
			for (int i = 0; i < this.jutsuList.size(); i++) {
				if (this.getJutsuXp(stack, i) >= this.getRequiredXp(stack, i)) {
					return true;
				}
			}
			return false;
		}

		protected long getCurrentJutsuCooldown(ItemStack stack) {
			return this.getJutsuCooldown(stack, this.getCurrentJutsuIndex(stack));
		}
		
		private long getJutsuCooldown(ItemStack stack, int index) {
			this.validateMapTags(stack, index);
			return stack.getTagCompound().getLong(CDMAP_KEY+index);
		}

		public void setCurrentJutsuCooldown(ItemStack stack, long cd) {
			this.setJutsuCooldown(stack, this.getCurrentJutsuIndex(stack), cd);
		}

		private void setJutsuCooldown(ItemStack stack, int index, long cd) {
			this.validateMapTags(stack, index);
			stack.getTagCompound().setLong(CDMAP_KEY+index, ProcedureUpdateworldtick.getTotalWorldTime() + cd);
		}

		public void setJutsuCooldown(ItemStack stack, JutsuEnum jutsuIn, long cd) {
			if (this.jutsuList.contains(jutsuIn) && this.isJutsuEnabled(stack, jutsuIn.index)) {
				this.setJutsuCooldown(stack, jutsuIn.index, cd);
			}
		}

		private void enableJutsu(ItemStack stack, int index, boolean enable) {
			long l = this.getJutsuCooldown(stack, index);
			stack.getTagCompound().setLong(CDMAP_KEY+index, enable ? l < 0 ? 0 : l : -1);
		}

		public void enableJutsu(ItemStack stack, JutsuEnum jutsuIn, boolean enable) {
			if (this.jutsuList.contains(jutsuIn)) {
				this.enableJutsu(stack, jutsuIn.index, enable);
			} else {
				System.err.println("Justu ["+jutsuIn.getName()+"] does not belong in "+this);
			}
		}

		private boolean isJutsuEnabled(ItemStack stack, int index) {
			return this.getJutsuCooldown(stack, index) >= 0;
		}

		public boolean isJutsuEnabled(ItemStack stack, JutsuEnum jutsuIn) {
			return this.jutsuList.contains(jutsuIn) && this.isJutsuEnabled(stack, jutsuIn.index);
		}

		public boolean isAnyJutsuEnabled(ItemStack stack) {
			for (JutsuEnum je : this.jutsuList) {
				if (this.isJutsuEnabled(stack, je.index)) {
					return true;
				}
			}
			return false;
		}

		public void enableAllJutsus(ItemStack stack, boolean enable) {
			for (JutsuEnum je : this.jutsuList) {
				if (this.isJutsuEnabled(stack, je.index) != enable) {
					this.enableJutsu(stack, je.index, enable);
				}
			}
		}

		public List<JutsuEnum> getActivatedJutsus(ItemStack stack) {
			List<JutsuEnum> list = Lists.newArrayList();
			for (JutsuEnum je : this.jutsuList) {
				if (je.jutsu.isActivated(stack)) {
					list.add(je);
				}
			}
			return list;
		}

		private boolean canUseJutsu(ItemStack stack, int index, @Nullable EntityLivingBase entity) {
			return (entity != null && this.isOwner(stack, entity) && this.isJutsuEnabled(stack, index)) ||
			       (entity instanceof EntityPlayer && ((EntityPlayer)entity).isCreative());
		}

		protected boolean canUseJutsu(ItemStack stack, JutsuEnum jutsuIn, @Nullable EntityLivingBase entity) {
			return this.jutsuList.contains(jutsuIn) && this.canUseJutsu(stack, jutsuIn.index, entity);
		}
		
		protected boolean canUseCurrentJutsu(ItemStack stack, @Nullable EntityLivingBase entity) {
			return this.canUseJutsu(stack, this.getCurrentJutsuIndex(stack), entity);
		}

		protected int getCurrentJutsuIndex(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getInteger(JUTSU_INDEX_KEY) : 0;
		}

		protected JutsuEnum getCurrentJutsu(ItemStack stack) {
			return this.jutsuList.get(this.getCurrentJutsuIndex(stack));
		}

		private void setCurrentJutsu(ItemStack stack, int index) {
			stack.getTagCompound().setInteger(JUTSU_INDEX_KEY, index);
		}
	
		public void setCurrentJutsu(ItemStack stack, JutsuEnum jutsuIn) {
			if (this.jutsuList.contains(jutsuIn)) {
				this.setCurrentJutsu(stack, jutsuIn.index);
			}
		}
	
		private void setNextJutsu(ItemStack stack, EntityLivingBase entity) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			int i = 0;
			int next = this.getCurrentJutsuIndex(stack);
			for ( ; i < this.jutsuList.size(); i++) {
				++next;
				if (next >= this.jutsuList.size())
					next = 0;
				if (this.canUseJutsu(stack, next, entity))
					break;
			}
			if (i < this.jutsuList.size()) {
				this.setCurrentJutsu(stack, next);
				if (entity instanceof EntityPlayer && !entity.world.isRemote)
					ProcedureUtils.sendStatusMessage((EntityPlayer)entity, this.jutsuList.get(next).getName(), true);
			}
		}

		public void setIsAffinity(ItemStack stack, boolean b) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setBoolean(AFFINITY_KEY, b);
		}

		private boolean isAffinity(ItemStack stack) {
			return stack.hasTagCompound() ? stack.getTagCompound().getBoolean(AFFINITY_KEY) : false;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack itemstack, World world, List<String> list, ITooltipFlag flag) {
			super.addInformation(itemstack, world, list, flag);
			list.add(new TextComponentTranslation("tooltip.general.shift").getUnformattedComponentText());
			for (JutsuEnum j : this.jutsuList) {
				if (this.canUseJutsu(itemstack, j.index, Minecraft.getMinecraft().player)) {
					list.add((this.getCurrentJutsuIndex(itemstack) == j.index ? ">" : " ")
					 +(j.index+1) + ": " + j.getName() + " (XP: " + TextFormatting.GREEN
					 + this.getJutsuXp(itemstack, j.index) + TextFormatting.GRAY + "/" + this.getRequiredXp(itemstack, j.index) + ")");
				}
			}
		}
	
		public void setOwner(ItemStack stack, EntityLivingBase owner) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
			stack.getTagCompound().setUniqueId(OWNER_ID_KEY, owner.getUniqueID());
			stack.setStackDisplayName(stack.getDisplayName() + " (" + owner.getName() + ")");
		}

		@Nullable
		protected UUID getOwnerUuid(ItemStack stack) {
			return stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(OWNER_ID_KEY) 
			 ? stack.getTagCompound().getUniqueId(OWNER_ID_KEY) : null;
		}

		@Nullable
		private EntityLivingBase getOwner(ItemStack stack) {
			UUID uuid = this.getOwnerUuid(stack);
			if (uuid != null) {
			//if (stack.hasTagCompound() && stack.getTagCompound().hasUniqueId(OWNER_ID_KEY)) {
				//Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(stack.getTagCompound().getUniqueId(OWNER_ID_KEY));
				Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(uuid);
				return entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
			}
			return null;
		}

		protected boolean isOwner(ItemStack stack, EntityLivingBase entity) {
			if (!stack.hasTagCompound() || !stack.getTagCompound().hasUniqueId(OWNER_ID_KEY)) {
				this.setOwner(stack, entity);
				this.resetJutsuMaps(stack);
			}
			return stack.getTagCompound().getUniqueId(OWNER_ID_KEY).equals(entity.getUniqueID());
		}

		@Override
		public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
			super.onUpdate(itemstack, world, entity, par4, par5);
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase livingEntity = (EntityLivingBase) entity;
				if (!this.isOwner(itemstack, livingEntity)) {
					return;
				}
			}
		}

		public static class EquipmentHook {
			@SubscribeEvent
			public void onServerTick(TickEvent.ServerTickEvent event) {
				if (event.phase == TickEvent.Phase.END) {
					ItemJutsu.injectShinobiAddonReleaseJutsus();
				}
			}

			@SubscribeEvent
			public void onEquipmentChange(LivingEquipmentChangeEvent event) {
				EntityLivingBase entity = event.getEntityLiving();
				ItemStack stack = event.getTo();
				if (entity instanceof EntityPlayer && !entity.world.isRemote
 && stack.getItem() instanceof Base
				 && event.getSlot().getSlotType() == EntityEquipmentSlot.Type.HAND && stack.getItem() != event.getFrom().getItem()) {
					if (event.getSlot() == EntityEquipmentSlot.MAINHAND || !(entity.getHeldItemMainhand().getItem() instanceof Base)) {
						ProcedureUtils.sendStatusMessage((EntityPlayer)entity, ItemJutsu.getCurrentJutsu(stack).getName(), true);
					}
				}
			}
		}

		public static void switchNextJutsu(ItemStack stack, EntityLivingBase entity) {
			if (stack.getItem() instanceof Base) {
				((Base)stack.getItem()).setNextJutsu(stack, entity);
			}
		}

		public EnumActionResult canActivateJutsu(ItemStack stack, JutsuEnum jutsuIn, EntityPlayer entity) {
			if (!entity.isCreative()) {
				if (!this.jutsuList.contains(jutsuIn) || !this.canUseJutsu(stack, jutsuIn.index, entity)) {
					return EnumActionResult.FAIL;
				}
				if (this.getJutsuXp(stack, jutsuIn.index) < this.getRequiredXp(stack, jutsuIn.index)
				 || !PlayerTracker.isNinja(entity)) {
					return EnumActionResult.FAIL;
				}
				long cd = this.getJutsuCooldown(stack, jutsuIn.index);
				if (cd > entity.world.getTotalWorldTime()) {
					return EnumActionResult.PASS;
				} else if (cd < 0) {
					return EnumActionResult.FAIL;
				}
			}
			return EnumActionResult.SUCCESS;
		}
	
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer entity, EnumHand hand) {
			ItemStack stack = entity.getHeldItem(hand);
			EnumActionResult res = this.canActivateJutsu(stack, this.getCurrentJutsu(stack), entity);
			if (res == EnumActionResult.PASS && !world.isRemote) {
				entity.sendStatusMessage(new TextComponentTranslation("chattext.cooldown.formatted", 
				 (this.getCurrentJutsuCooldown(stack) - world.getTotalWorldTime()) / 20), true);
			} else if (res == EnumActionResult.SUCCESS) {
				entity.setActiveHand(hand);
			}
			return new ActionResult<ItemStack>(res, stack);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack itemstack) {
			return EnumAction.BOW;
		}
	
		protected int getMaxUseDuration() {
			return 72000;
		}
	
		@Override
		public int getMaxItemUseDuration(ItemStack itemstack) {
			return this.getMaxUseDuration();
		}

		@Override
		public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
			return false;
		}

		@Override
		public int getMaxDamage() {
			return 0;
		}

		@Override
		public boolean isDamageable() {
			return false;
		}
	}

	public interface IJutsu {
		JutsuEnum.Type getJutsuType();
	}
	
	public interface IJutsuCallback {
		boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power);
		
		default boolean isActivated(ItemStack stack) {
			return false;
		}
		
		default boolean isActivated(EntityLivingBase entity) {
			return false;
		}
		
		default void deactivate(EntityLivingBase entity) {
		}
		
		default float getPower(ItemStack stack) {
			return 0.0f;
		}

		default float getBasePower() {
			return 1.0f;
		}

        @Deprecated
		default float getPowerupDelay() {
			return 0.0f;
		}

        default float getPowerupDelay(ItemStack stack, EntityLivingBase entity) {
            return this.getPowerupDelay();
        }
		
		@Deprecated // use entity sensitive version below
		default float getMaxPower() {
			return 1000.0f;
		}

		default float getMaxPower(ItemStack stack, EntityLivingBase entity) {
			return this.getMaxPower();
		}

		default void onUsingTick(ItemStack stack, EntityLivingBase player, float power) {
			if (this.getPowerupDelay() > 0.0f) {
				if (player instanceof EntityPlayer) {
					ProcedureUtils.sendStatusMessage((EntityPlayer)player, String.format("%.1f", power), true);
				}
				Particles.spawnParticle(player.world, Particles.Types.SMOKE, player.posX, player.posY, player.posZ, 
				 40, 0.2d, 0d, 0.2d, 0d, 0.5d, 0d, 0x106AD1FF, 40, 5, 0xF0, player.getEntityId());
				if (player.ticksExisted % 10 == 0) {
					player.world.playSound(null, player.posX, player.posY, player.posZ,
					 net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:charging_chakra")),
					 net.minecraft.util.SoundCategory.PLAYERS, 0.05F, player.getRNG().nextFloat() + 0.5F);
				}
			}
		}

		default Entity getJutsu(EntityLivingBase entity) {
			return null;
		}

		default JutsuData getData(EntityLivingBase entity) {
			return null;
		}

		public static class JutsuData {
			public final Entity entity;
			public final ItemStack stack;

			public JutsuData(Entity entityIn, ItemStack stackIn) {
				this.entity = entityIn;
				this.stack = stackIn;
			}
		}
	}

	public static class JutsuEnum {
		public final int index;
		public final String unlocalizedName;
		public final char rank;
		public final int requiredXP;
		public final double chakraUsage;
		public final IJutsuCallback jutsu;
		private Type type;
		public float basePower = 0f;
		public float powerUpDelay = 50f;
		private static final List<JutsuEnum> jutsuList = Lists.newArrayList();
	
		public JutsuEnum(int idx, String string, int xp, IJutsuCallback jutsuIn) {
			this(idx, string, xp, 0d, jutsuIn);
		}

		public JutsuEnum(int idx, String string, char rankIn, IJutsuCallback jutsuIn) {
			this(idx, string, rankIn, 0d, jutsuIn);
		}

		public JutsuEnum(int idx, String string, char rankIn, double chakraUsageIn, IJutsuCallback jutsuIn) {
			this(idx, string, rankIn, 
			 //rankIn=='S' ? 30 : rankIn=='A' ? 25 : rankIn=='B' ? 20 : rankIn=='C' ? 15 : rankIn=='D' ? 10 : 90, chakraUsageIn, jutsuIn);
			 rankIn=='S' ? 400 : rankIn=='A' ? 250 : rankIn=='B' ? 200 : rankIn=='C' ? 150 : rankIn=='D' ? 100 : 900, chakraUsageIn, jutsuIn);
		}

		public JutsuEnum(int idx, String string, int xp, double chakraUsageIn, IJutsuCallback jutsuIn) {
			this(idx, string, ' ', xp, chakraUsageIn, jutsuIn);
		}

		public JutsuEnum(int idx, String string, char rankIn, int xp, double chakraUsageIn, IJutsuCallback jutsuIn) {
			this.index = idx;
			this.unlocalizedName = string;
			this.rank = (rankIn=='S' || rankIn=='A' || rankIn=='B' || rankIn=='C' || rankIn=='D') ? rankIn : 0;
			this.requiredXP = xp;
			this.chakraUsage = chakraUsageIn;
			this.jutsu = jutsuIn;
			JutsuEnum.jutsuList.add(this);
		}
		
		public String getName() {
			String s = this.unlocalizedName;
			if (!s.contains(".")) {
				s = "entity." + s + ".name";
			}
			return net.minecraft.util.text.translation.I18n.translateToLocal(s);
		}
		
		public static ImmutableList<JutsuEnum> getJutsuList() {
			return ImmutableList.copyOf(JutsuEnum.jutsuList);
		}

		public static ImmutableList<JutsuEnum> getJutsuList(char rankIn) {
			List<JutsuEnum> list = Lists.newArrayList();
			for (JutsuEnum je : JutsuEnum.jutsuList) {
				if (je.rank == rankIn) {
					list.add(je);
				}
			}
			return ImmutableList.copyOf(list);
		}

		public Type getType() {
			return this.type;
		}

		private JutsuEnum setType(Type typeIn) {
			this.type = typeIn;
			return this;
		}

		private JutsuEnum copyForIndex(int indexIn) {
			return new JutsuEnum(indexIn, this.unlocalizedName, this.rank, this.requiredXP, this.chakraUsage, this.jutsu);
		}

		public String toString() {
			return "\nJutsu - " + this.type + ": " + this.getName() + ", rank:" + this.rank + ", callback:" + this.jutsu.getClass();
		}

		public enum Type {
			NINJUTSU,
			DOTON,
			FUTON,
			KATON,
			RAITON,
			SUITON,
			INTON,
			YOTON,
			JINTON,
			MOKUTON,
			JITON,
			IRYO,
			HYOTON,
			BAKUTON,
			SHAKUTON,
			BYAKUGAN,
			SHARINGAN,
			RINNEGAN,
			RANTON,
			FUTTON,
			YOOTON,
			SHIKOTSUMYAKU,
			KUCHIYOSE,
			TENSEIGAN,
			SENJUTSU,
			SIXPATHSENJUTSU,
			KEKKEIMORA,
			BLOOD,
			SHOTON,
            SENNINKA,
			OTHER;
		}
	}

	static {
		addJutsuTypeAlias(JutsuEnum.Type.NINJUTSU, "ninjutsu", "ninja_arts", "ninjaarts", "ninja art", "ninja arts");
		addReleaseItemAlias("ninja_arts", "ninjutsu");
		addJutsuTypeAlias(JutsuEnum.Type.SENJUTSU, "senjutsu", "sage_arts", "sagearts", "sage art", "sage arts");
		addReleaseItemAlias("sage_arts", "senjutsu");
		addJutsuTypeAlias(JutsuEnum.Type.SIXPATHSENJUTSU, "six_path_senjutsu", "six_paths_sage_arts",
		 "sixpaths_sage_arts", "six path senjutsu", "six paths sage arts", "rikudo_senjutsu");
		addReleaseItemAlias("six_paths_sage_arts", "six_path_senjutsu", "rikudo_senjutsu");
		addJutsuTypeAlias(JutsuEnum.Type.KEKKEIMORA, "kekkei_mora", "all_encompassing_bloodline",
		 "all encompassing bloodline");
		addReleaseItemAlias("all_encompassing_bloodline", "kekkei_mora");
		addJutsuTypeAlias(JutsuEnum.Type.IRYO, "iryo", "iryo_jutsu", "medical_ninjutsu", "medical ninjutsu");
		addReleaseItemAlias("medical_ninjutsu", "iryo_jutsu", "iryo");
		addJutsuTypeAlias(JutsuEnum.Type.KATON, "katon", "fire_release", "fire release");
		addReleaseItemAlias("fire_release", "katon");
		addJutsuTypeAlias(JutsuEnum.Type.SUITON, "suiton", "water_release", "water release");
		addReleaseItemAlias("water_release", "suiton");
		addJutsuTypeAlias(JutsuEnum.Type.DOTON, "doton", "earth_release", "earth release");
		addReleaseItemAlias("earth_release", "doton");
		addJutsuTypeAlias(JutsuEnum.Type.FUTON, "futon", "wind_release", "wind release");
		addReleaseItemAlias("wind_release", "futon");
		addJutsuTypeAlias(JutsuEnum.Type.RAITON, "raiton", "lightning_release", "lightning release");
		addReleaseItemAlias("lightning_release", "raiton");
		addJutsuTypeAlias(JutsuEnum.Type.MOKUTON, "mokuton", "wood_release", "wood release");
		addReleaseItemAlias("wood_release", "mokuton");
		addJutsuTypeAlias(JutsuEnum.Type.YOTON, "yoton", "yang_release", "yang release");
		addReleaseItemAlias("yang_release", "yoton");
		addJutsuTypeAlias(JutsuEnum.Type.INTON, "inton", "yin_release", "yin release");
		addReleaseItemAlias("yin_release", "inton");
		addJutsuTypeAlias(JutsuEnum.Type.JINTON, "jinton", "dust_release", "dust release");
		addReleaseItemAlias("dust_release", "jinton");
		addJutsuTypeAlias(JutsuEnum.Type.BAKUTON, "bakuton", "explosion_release", "explosion release");
		addReleaseItemAlias("explosion_release", "bakuton");
		addJutsuTypeAlias(JutsuEnum.Type.SHAKUTON, "shakuton", "scorch_release", "scorch release");
		addReleaseItemAlias("scorch_release", "shakuton");
		addJutsuTypeAlias(JutsuEnum.Type.SHOTON, "shoton", "crystal_release", "crystal release");
		addReleaseItemAlias("crystal_release", "shoton");
		addJutsuTypeAlias(JutsuEnum.Type.HYOTON, "hyoton", "ice_release", "ice release");
		addReleaseItemAlias("ice_release", "hyoton");
		addJutsuTypeAlias(JutsuEnum.Type.RANTON, "ranton", "storm_release", "storm release");
		addReleaseItemAlias("storm_release", "ranton");
		addJutsuTypeAlias(JutsuEnum.Type.JITON, "jiton", "magnet_release", "magnet release");
		addReleaseItemAlias("magnet_release", "jiton");
		addJutsuTypeAlias(JutsuEnum.Type.FUTTON, "futton", "boil_release", "boil release");
		addReleaseItemAlias("boil_release", "futton");
		addJutsuTypeAlias(JutsuEnum.Type.YOOTON, "yooton", "lava_release", "lava release");
		addReleaseItemAlias("lava_release", "yooton");
	}
}


