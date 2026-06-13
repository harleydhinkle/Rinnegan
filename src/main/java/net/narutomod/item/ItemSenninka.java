package net.narutomod.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntitySenninkaClone;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenninka extends ElementsNarutomodMod.ModElement {
   @ObjectHolder("narutomod:senninka")
   public static final Item block = null;
   public static final int ENTITYID = 524;
   private static final String START_TIME = "SenninkaStartTime";
   private static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("0de3e80f-e3d8-4ccf-a5b4-53613f43d3aa");
   private static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("fd56f085-e8cb-4ee3-8a8f-a4d519296ef8");
   private static final UUID MOVEMENT_SPEED_MODIFIER = UUID.fromString("fd518521-beb8-4da0-9b59-bd9712bb3caf");
   public static final ItemJutsu.JutsuEnum BROADAXE = new ItemJutsu.JutsuEnum(0, "item.senninka_broadaxe.name", 'S', 50, 50.0D, new Broadaxe());
   public static final ItemJutsu.JutsuEnum PISTONFIST = new ItemJutsu.JutsuEnum(1, "item.senninka.pistonfist", 'S', 50, 50.0D, new PistonFist());
   public static final ItemJutsu.JutsuEnum STAGE2 = new ItemJutsu.JutsuEnum(2, "item.senninka.stage2", 'S', 100, 50.0D, new Stage2());
   public static final ItemJutsu.JutsuEnum CANNON = new ItemJutsu.JutsuEnum(3, "entitysennikacannon", 'S', 50, 100.0D, new EntityMultiCannon.Jutsu());
   public static final ItemJutsu.JutsuEnum ABSORB = new ItemJutsu.JutsuEnum(4, "item.senninka.absorb", 'S', 50, 50.0D, new Absorption());

   public ItemSenninka(ElementsNarutomodMod instance) {
      super(instance, 939);
   }

   public void initElements() {
      this.elements.items.add(() -> {
         return new RangedItem(new ItemJutsu.JutsuEnum[]{BROADAXE, PISTONFIST, STAGE2, CANNON, ABSORB});
      });
      this.elements.entities.add(() -> {
         return EntityEntryBuilder.create().entity(EntityMultiCannon.class).id(new ResourceLocation("narutomod", "entitysennikacannon"), 524).name("entitysennikacannon").tracker(64, 1, true).build();
      });
   }

   @SideOnly(Side.CLIENT)
   public void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:senninka", "inventory"));
   }

   public void init(FMLInitializationEvent event) {
      ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
   }

   public static int getActivationTicks(Entity entity) {
      return entity.getEntityData().getInteger("SenninkaStartTime");
   }

   public static void setActivationTicks(Entity entity, int ticks) {
      ProcedureSync.EntityNBTTag.setAndSync(entity, "SenninkaStartTime", ticks);
   }

   public void preInit(FMLPreInitializationEvent event) {
      (new Renderer()).register();
   }

   public static class Renderer extends EntityRendererRegister {
      @SideOnly(Side.CLIENT)
      public void register() {
         RenderingRegistry.registerEntityRenderingHandler(EntityMultiCannon.class, (renderManager) -> {
            return new CustomRender(renderManager);
         });
      }

      @SideOnly(Side.CLIENT)
      public static class ModelJugo extends ModelBiped {
         private final ModelRenderer headStage0;
         private final ModelRenderer hair;
         private final ModelRenderer bone1;
         private final ModelRenderer bone7;
         private final ModelRenderer bone9;
         private final ModelRenderer bone15;
         private final ModelRenderer bone2;
         private final ModelRenderer bone6;
         private final ModelRenderer bone3;
         private final ModelRenderer bone8;
         private final ModelRenderer bone11;
         private final ModelRenderer bone18;
         private final ModelRenderer bone12;
         private final ModelRenderer bone17;
         private final ModelRenderer bone10;
         private final ModelRenderer bone16;
         private final ModelRenderer bone4;
         private final ModelRenderer bone13;
         private final ModelRenderer bone14;
         private final ModelRenderer bone5;
         private final ModelRenderer headStage1;
         private final ModelRenderer headStage2;
         private final ModelRenderer eyeRight;
         private final ModelRenderer eyeLeft;
         private final ModelRenderer bodyStage1;
         private final ModelRenderer bodyStage2;
         private final ModelRenderer exhaust1;
         private final ModelRenderer bone33;
         private final ModelRenderer bone47;
         private final ModelRenderer exhaustExtension1;
         private final ModelRenderer bone67;
         private final ModelRenderer bone69;
         private final ModelRenderer bone70;
         private final ModelRenderer bone71;
         private final ModelRenderer ball1;
         private final ModelRenderer exhaust2;
         private final ModelRenderer bone30;
         private final ModelRenderer bone31;
         private final ModelRenderer exhaustExtension2;
         private final ModelRenderer bone99;
         private final ModelRenderer bone100;
         private final ModelRenderer bone101;
         private final ModelRenderer bone102;
         private final ModelRenderer ball2;
         private final ModelRenderer exhaust3;
         private final ModelRenderer bone74;
         private final ModelRenderer bone75;
         private final ModelRenderer exhaustExtension3;
         private final ModelRenderer bone77;
         private final ModelRenderer bone78;
         private final ModelRenderer bone79;
         private final ModelRenderer bone80;
         private final ModelRenderer ball3;
         private final ModelRenderer exhaust4;
         private final ModelRenderer bone84;
         private final ModelRenderer bone90;
         private final ModelRenderer exhaustExtension4;
         private final ModelRenderer bone93;
         private final ModelRenderer bone98;
         private final ModelRenderer bone103;
         private final ModelRenderer bone104;
         private final ModelRenderer ball4;
         private final ModelRenderer exhaust5;
         private final ModelRenderer bone82;
         private final ModelRenderer bone83;
         private final ModelRenderer exhaustExtension5;
         private final ModelRenderer bone85;
         private final ModelRenderer bone87;
         private final ModelRenderer bone88;
         private final ModelRenderer bone89;
         private final ModelRenderer bone81;
         private final ModelRenderer ball5;
         private final ModelRenderer exhaust6;
         private final ModelRenderer bone64;
         private final ModelRenderer bone65;
         private final ModelRenderer exhaustExtension6;
         private final ModelRenderer bone66;
         private final ModelRenderer bone68;
         private final ModelRenderer bone73;
         private final ModelRenderer bone76;
         private final ModelRenderer bone105;
         private final ModelRenderer ball6;
         private final ModelRenderer exhaust7;
         private final ModelRenderer bone91;
         private final ModelRenderer bone92;
         private final ModelRenderer exhaustExtension7;
         private final ModelRenderer bone94;
         private final ModelRenderer bone95;
         private final ModelRenderer bone96;
         private final ModelRenderer bone97;
         private final ModelRenderer ball7;
         private final ModelRenderer exhaust8;
         private final ModelRenderer bone58;
         private final ModelRenderer bone59;
         private final ModelRenderer exhaustExtension8;
         private final ModelRenderer bone60;
         private final ModelRenderer bone61;
         private final ModelRenderer bone62;
         private final ModelRenderer bone63;
         private final ModelRenderer ball8;
         private final ModelRenderer rightArmSpikes;
         private final ModelRenderer bone129;
         private final ModelRenderer bone72;
         private final ModelRenderer bone25;
         private final ModelRenderer bone125;
         private final ModelRenderer bone126;
         private final ModelRenderer bone127;
         private final ModelRenderer bone128;
         private final ModelRenderer bone124;
         private final ModelRenderer bone86;
         private final ModelRenderer bone131;
         private final ModelRenderer bone132;
         private final ModelRenderer bone133;
         private final ModelRenderer bone134;
         private final ModelRenderer bone135;
         private final ModelRenderer bone136;
         private final ModelRenderer bone137;
         private final ModelRenderer bone138;
         private final ModelRenderer bone139;
         private final ModelRenderer bone140;
         private final ModelRenderer bone141;
         private final ModelRenderer bone142;
         private final ModelRenderer bone143;
         private final ModelRenderer bone144;
         private final ModelRenderer bone145;
         private final ModelRenderer bone146;
         private final ModelRenderer bone147;
         private final ModelRenderer bone148;
         private final ModelRenderer bone150;
         private final ModelRenderer bone151;
         private final ModelRenderer bone152;
         private final ModelRenderer bone153;
         private final ModelRenderer bone154;
         private final ModelRenderer bone157;
         private final ModelRenderer bone158;
         private final ModelRenderer armExhaust;
         private final ModelRenderer bone106;
         private final ModelRenderer bone109;
         private final ModelRenderer bone110;
         private final ModelRenderer bone107;
         private final ModelRenderer bone108;
         private final ModelRenderer needle;
         private final ModelRenderer bulge;
         private final ModelRenderer leftArmSpikes;
         private final ModelRenderer bone19;
         private final ModelRenderer bone20;
         private final ModelRenderer bone21;
         private final ModelRenderer bone22;
         private final ModelRenderer bone23;
         private final ModelRenderer bone24;
         private final ModelRenderer bone26;
         private final ModelRenderer bone27;
         private final ModelRenderer bone28;
         private final ModelRenderer bone29;
         private final ModelRenderer bone32;
         private final ModelRenderer bone34;
         private final ModelRenderer bone35;
         private final ModelRenderer bone36;
         private final ModelRenderer bone37;
         private final ModelRenderer bone38;
         private final ModelRenderer bone39;
         private final ModelRenderer bone40;
         private final ModelRenderer bone41;
         private final ModelRenderer bone42;
         private final ModelRenderer bone43;
         private final ModelRenderer bone44;
         private final ModelRenderer bone45;
         private final ModelRenderer bone46;
         private final ModelRenderer bone48;
         private final ModelRenderer bone49;
         private final ModelRenderer bone50;
         private final ModelRenderer bone51;
         private final ModelRenderer bone52;
         private final ModelRenderer bone53;
         private final ModelRenderer bone54;
         private final ModelRenderer bone55;
         private final ModelRenderer bone56;
         private final ModelRenderer bone57;
         private final ModelRenderer blasts;
         private final ModelRenderer blast1;
         private final ModelRenderer blast2;
         private final ModelRenderer blast3;
         private final ModelRenderer blast4;
         private final ModelRenderer blast5;
         private final ModelRenderer blast6;
         private final ModelRenderer blast7;
         private final ModelRenderer blast8;
         private ModelBiped wearerModel;

         public ModelJugo() {
            this.textureWidth = 64;
            this.textureHeight = 96;
            this.bipedHead = new ModelRenderer(this);
            this.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.headStage0 = new ModelRenderer(this);
            this.headStage0.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedHead.addChild(this.headStage0);
            this.headStage0.cubeList.add(new ModelBox(this.headStage0, 32, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F, false));
            this.hair = new ModelRenderer(this);
            this.hair.setRotationPoint(0.0F, -1.0F, 0.0F);
            this.headStage0.addChild(this.hair);
            this.bone1 = new ModelRenderer(this);
            this.bone1.setRotationPoint(-2.0F, -5.25F, 0.0F);
            this.hair.addChild(this.bone1);
            this.setRotationAngle(this.bone1, -0.1745F, 0.0F, -0.5236F);
            this.bone1.cubeList.add(new ModelBox(this.bone1, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone7 = new ModelRenderer(this);
            this.bone7.setRotationPoint(2.0F, -5.25F, 0.0F);
            this.hair.addChild(this.bone7);
            this.setRotationAngle(this.bone7, -0.1745F, 0.0F, 0.5236F);
            this.bone7.cubeList.add(new ModelBox(this.bone7, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone9 = new ModelRenderer(this);
            this.bone9.setRotationPoint(-2.25F, -4.25F, 0.0F);
            this.hair.addChild(this.bone9);
            this.setRotationAngle(this.bone9, -0.5236F, 0.5236F, -1.0472F);
            this.bone9.cubeList.add(new ModelBox(this.bone9, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone15 = new ModelRenderer(this);
            this.bone15.setRotationPoint(2.25F, -4.25F, 0.0F);
            this.hair.addChild(this.bone15);
            this.setRotationAngle(this.bone15, -0.5236F, -0.5236F, 1.0472F);
            this.bone15.cubeList.add(new ModelBox(this.bone15, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone2 = new ModelRenderer(this);
            this.bone2.setRotationPoint(-2.0F, -5.0F, -2.0F);
            this.hair.addChild(this.bone2);
            this.setRotationAngle(this.bone2, 0.1745F, 0.0F, -0.3491F);
            this.bone2.cubeList.add(new ModelBox(this.bone2, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone6 = new ModelRenderer(this);
            this.bone6.setRotationPoint(2.0F, -5.0F, -2.0F);
            this.hair.addChild(this.bone6);
            this.setRotationAngle(this.bone6, 0.1745F, 0.0F, 0.3491F);
            this.bone6.cubeList.add(new ModelBox(this.bone6, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone3 = new ModelRenderer(this);
            this.bone3.setRotationPoint(-2.0F, -4.5F, 2.0F);
            this.hair.addChild(this.bone3);
            this.setRotationAngle(this.bone3, -0.5236F, 0.0F, -0.3491F);
            this.bone3.cubeList.add(new ModelBox(this.bone3, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone8 = new ModelRenderer(this);
            this.bone8.setRotationPoint(2.0F, -4.5F, 2.0F);
            this.hair.addChild(this.bone8);
            this.setRotationAngle(this.bone8, -0.5236F, 0.0F, 0.3491F);
            this.bone8.cubeList.add(new ModelBox(this.bone8, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone11 = new ModelRenderer(this);
            this.bone11.setRotationPoint(-2.0F, -3.5F, 2.0F);
            this.hair.addChild(this.bone11);
            this.setRotationAngle(this.bone11, -1.309F, 0.0F, -0.3491F);
            this.bone11.cubeList.add(new ModelBox(this.bone11, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone18 = new ModelRenderer(this);
            this.bone18.setRotationPoint(2.0F, -3.5F, 2.0F);
            this.hair.addChild(this.bone18);
            this.setRotationAngle(this.bone18, -1.309F, 0.0F, 0.3491F);
            this.bone18.cubeList.add(new ModelBox(this.bone18, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone12 = new ModelRenderer(this);
            this.bone12.setRotationPoint(-3.0F, -2.5F, 1.0F);
            this.hair.addChild(this.bone12);
            this.setRotationAngle(this.bone12, -2.0944F, 0.0F, -0.3491F);
            this.bone12.cubeList.add(new ModelBox(this.bone12, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone17 = new ModelRenderer(this);
            this.bone17.setRotationPoint(3.0F, -2.5F, 1.0F);
            this.hair.addChild(this.bone17);
            this.setRotationAngle(this.bone17, -2.0944F, 0.0F, 0.3491F);
            this.bone17.cubeList.add(new ModelBox(this.bone17, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone10 = new ModelRenderer(this);
            this.bone10.setRotationPoint(-2.0F, -3.5F, 1.0F);
            this.hair.addChild(this.bone10);
            this.setRotationAngle(this.bone10, -0.9599F, -0.0873F, -1.309F);
            this.bone10.cubeList.add(new ModelBox(this.bone10, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone16 = new ModelRenderer(this);
            this.bone16.setRotationPoint(2.0F, -3.5F, 1.0F);
            this.hair.addChild(this.bone16);
            this.setRotationAngle(this.bone16, -0.9599F, 0.0873F, 1.309F);
            this.bone16.cubeList.add(new ModelBox(this.bone16, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone4 = new ModelRenderer(this);
            this.bone4.setRotationPoint(0.0F, -5.0F, -2.0F);
            this.hair.addChild(this.bone4);
            this.setRotationAngle(this.bone4, 0.5236F, 0.0F, 0.0F);
            this.bone4.cubeList.add(new ModelBox(this.bone4, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone13 = new ModelRenderer(this);
            this.bone13.setRotationPoint(0.0F, -6.0F, -2.0F);
            this.hair.addChild(this.bone13);
            this.setRotationAngle(this.bone13, 0.0873F, 0.0F, 0.0F);
            this.bone13.cubeList.add(new ModelBox(this.bone13, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, true));
            this.bone14 = new ModelRenderer(this);
            this.bone14.setRotationPoint(0.0F, -6.0F, 0.0F);
            this.hair.addChild(this.bone14);
            this.setRotationAngle(this.bone14, -0.2618F, 0.0F, 0.0F);
            this.bone14.cubeList.add(new ModelBox(this.bone14, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.bone5 = new ModelRenderer(this);
            this.bone5.setRotationPoint(0.0F, -5.0F, 2.0F);
            this.hair.addChild(this.bone5);
            this.setRotationAngle(this.bone5, -0.7854F, 0.0F, 0.0F);
            this.bone5.cubeList.add(new ModelBox(this.bone5, 24, 0, -2.0F, -4.0F, -2.0F, 4, 4, 4, -0.1F, false));
            this.headStage1 = new ModelRenderer(this);
            this.headStage1.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedHead.addChild(this.headStage1);
            this.headStage1.cubeList.add(new ModelBox(this.headStage1, 0, 64, -8.0F, -12.0F, -8.0F, 16, 16, 16, -3.95F, false));
            this.headStage2 = new ModelRenderer(this);
            this.headStage2.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedHead.addChild(this.headStage2);
            this.headStage2.cubeList.add(new ModelBox(this.headStage2, 0, 0, -4.0F, -8.0F, -4.0F, 8, 8, 8, 0.1F, false));
            this.eyeRight = new ModelRenderer(this);
            this.eyeRight.setRotationPoint(0.5F, 0.3F, -2.25F);
            this.headStage2.addChild(this.eyeRight);
            this.eyeRight.cubeList.add(new ModelBox(this.eyeRight, 0, 55, -6.0F, -6.95F, -5.05F, 7, 7, 0, -3.15F, false));
            this.eyeLeft = new ModelRenderer(this);
            this.eyeLeft.setRotationPoint(-0.5F, 0.3F, -2.25F);
            this.headStage2.addChild(this.eyeLeft);
            this.eyeLeft.cubeList.add(new ModelBox(this.eyeLeft, 0, 55, -1.0F, -6.95F, -5.05F, 7, 7, 0, -3.15F, true));
            this.bipedHeadwear = new ModelRenderer(this);
            this.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedHeadwear.cubeList.add(new ModelBox(this.bipedHeadwear, 16, 40, -4.0F, -8.0F, -4.0F, 8, 8, 1, 0.4F, false));
            this.bipedBody = new ModelRenderer(this);
            this.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bodyStage1 = new ModelRenderer(this);
            this.bodyStage1.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedBody.addChild(this.bodyStage1);
            this.bodyStage1.cubeList.add(new ModelBox(this.bodyStage1, 40, 42, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.05F, false));
            this.bodyStage2 = new ModelRenderer(this);
            this.bodyStage2.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bipedBody.addChild(this.bodyStage2);
            this.bodyStage2.cubeList.add(new ModelBox(this.bodyStage2, 16, 16, -4.0F, 0.0F, -2.0F, 8, 12, 4, 0.11F, false));
            this.bodyStage2.cubeList.add(new ModelBox(this.bodyStage2, 16, 32, -4.0F, 0.0F, -2.0F, 8, 4, 4, 0.34F, false));
            this.exhaust1 = new ModelRenderer(this);
            this.exhaust1.setRotationPoint(-2.0F, 3.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust1);
            this.setRotationAngle(this.exhaust1, -1.0472F, -0.7854F, 0.0F);
            this.exhaust1.cubeList.add(new ModelBox(this.exhaust1, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, true));
            this.bone33 = new ModelRenderer(this);
            this.bone33.setRotationPoint(2.0F, -5.9F, 2.1F);
            this.exhaust1.addChild(this.bone33);
            this.setRotationAngle(this.bone33, 0.5236F, 0.0F, 0.0F);
            this.bone33.cubeList.add(new ModelBox(this.bone33, 0, 43, -4.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, true));
            this.bone47 = new ModelRenderer(this);
            this.bone47.setRotationPoint(0.1F, -3.8F, 0.2F);
            this.bone33.addChild(this.bone47);
            this.setRotationAngle(this.bone47, 0.3491F, 0.0F, 0.0F);
            this.bone47.cubeList.add(new ModelBox(this.bone47, 0, 43, -4.0F, -4.0F, -4.0F, 4, 4, 4, -0.2F, true));
            this.exhaustExtension1 = new ModelRenderer(this);
            this.exhaustExtension1.setRotationPoint(-2.0F, -3.3F, 0.0F);
            this.bone47.addChild(this.exhaustExtension1);
            this.setRotationAngle(this.exhaustExtension1, 0.3491F, 0.1745F, -0.3491F);
            this.exhaustExtension1.cubeList.add(new ModelBox(this.exhaustExtension1, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, true));
            this.bone67 = new ModelRenderer(this);
            this.bone67.setRotationPoint(0.0F, -3.25F, 0.0F);
            this.exhaustExtension1.addChild(this.bone67);
            this.setRotationAngle(this.bone67, 0.3491F, 0.0873F, -0.2618F);
            this.bone67.cubeList.add(new ModelBox(this.bone67, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, true));
            this.bone69 = new ModelRenderer(this);
            this.bone69.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone67.addChild(this.bone69);
            this.setRotationAngle(this.bone69, 0.3491F, 0.0F, -0.2618F);
            this.bone69.cubeList.add(new ModelBox(this.bone69, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, true));
            this.bone70 = new ModelRenderer(this);
            this.bone70.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone69.addChild(this.bone70);
            this.setRotationAngle(this.bone70, 0.3491F, -0.0873F, -0.2618F);
            this.bone70.cubeList.add(new ModelBox(this.bone70, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, true));
            this.bone71 = new ModelRenderer(this);
            this.bone71.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone70.addChild(this.bone71);
            this.setRotationAngle(this.bone71, 0.1745F, 0.0F, -0.1745F);
            this.bone71.cubeList.add(new ModelBox(this.bone71, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, true));
            this.ball1 = new ModelRenderer(this);
            this.ball1.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone71.addChild(this.ball1);
            this.exhaust2 = new ModelRenderer(this);
            this.exhaust2.setRotationPoint(2.0F, 3.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust2);
            this.setRotationAngle(this.exhaust2, -1.0472F, 0.7854F, 0.0F);
            this.exhaust2.cubeList.add(new ModelBox(this.exhaust2, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, false));
            this.bone30 = new ModelRenderer(this);
            this.bone30.setRotationPoint(-2.0F, -5.9F, 2.1F);
            this.exhaust2.addChild(this.bone30);
            this.setRotationAngle(this.bone30, 0.5236F, 0.0F, 0.0F);
            this.bone30.cubeList.add(new ModelBox(this.bone30, 0, 43, 0.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, false));
            this.bone31 = new ModelRenderer(this);
            this.bone31.setRotationPoint(-0.1F, -3.8F, 0.2F);
            this.bone30.addChild(this.bone31);
            this.setRotationAngle(this.bone31, 0.3491F, 0.0F, 0.0F);
            this.bone31.cubeList.add(new ModelBox(this.bone31, 0, 43, 0.0F, -4.0F, -4.0F, 4, 4, 4, -0.2F, false));
            this.exhaustExtension2 = new ModelRenderer(this);
            this.exhaustExtension2.setRotationPoint(2.0F, -3.3F, 0.0F);
            this.bone31.addChild(this.exhaustExtension2);
            this.setRotationAngle(this.exhaustExtension2, 0.3491F, -0.1745F, 0.3491F);
            this.exhaustExtension2.cubeList.add(new ModelBox(this.exhaustExtension2, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, false));
            this.bone99 = new ModelRenderer(this);
            this.bone99.setRotationPoint(0.0F, -3.25F, 0.0F);
            this.exhaustExtension2.addChild(this.bone99);
            this.setRotationAngle(this.bone99, 0.3491F, -0.0873F, 0.2618F);
            this.bone99.cubeList.add(new ModelBox(this.bone99, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, false));
            this.bone100 = new ModelRenderer(this);
            this.bone100.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone99.addChild(this.bone100);
            this.setRotationAngle(this.bone100, 0.3491F, 0.0F, 0.2618F);
            this.bone100.cubeList.add(new ModelBox(this.bone100, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, false));
            this.bone101 = new ModelRenderer(this);
            this.bone101.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone100.addChild(this.bone101);
            this.setRotationAngle(this.bone101, 0.3491F, 0.0873F, 0.2618F);
            this.bone101.cubeList.add(new ModelBox(this.bone101, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, false));
            this.bone102 = new ModelRenderer(this);
            this.bone102.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone101.addChild(this.bone102);
            this.setRotationAngle(this.bone102, 0.1745F, 0.0F, 0.1745F);
            this.bone102.cubeList.add(new ModelBox(this.bone102, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, false));
            this.ball2 = new ModelRenderer(this);
            this.ball2.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone102.addChild(this.ball2);
            this.exhaust3 = new ModelRenderer(this);
            this.exhaust3.setRotationPoint(-2.0F, 3.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust3);
            this.setRotationAngle(this.exhaust3, -1.9199F, -0.6109F, 0.0F);
            this.exhaust3.cubeList.add(new ModelBox(this.exhaust3, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, true));
            this.bone74 = new ModelRenderer(this);
            this.bone74.setRotationPoint(2.0F, -5.9F, 2.1F);
            this.exhaust3.addChild(this.bone74);
            this.setRotationAngle(this.bone74, 0.5236F, 0.0F, -0.2618F);
            this.bone74.cubeList.add(new ModelBox(this.bone74, 0, 43, -4.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, true));
            this.bone75 = new ModelRenderer(this);
            this.bone75.setRotationPoint(0.1F, -3.8F, 0.2F);
            this.bone74.addChild(this.bone75);
            this.setRotationAngle(this.bone75, 0.3478F, 0.0298F, -0.3438F);
            this.bone75.cubeList.add(new ModelBox(this.bone75, 0, 43, -4.0F, -4.0F, -4.0F, 4, 4, 4, -0.2F, true));
            this.exhaustExtension3 = new ModelRenderer(this);
            this.exhaustExtension3.setRotationPoint(-2.0F, -3.3F, 0.0F);
            this.bone75.addChild(this.exhaustExtension3);
            this.setRotationAngle(this.exhaustExtension3, 0.1745F, 0.1745F, -0.2618F);
            this.exhaustExtension3.cubeList.add(new ModelBox(this.exhaustExtension3, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, true));
            this.bone77 = new ModelRenderer(this);
            this.bone77.setRotationPoint(0.0F, -3.25F, 0.0F);
            this.exhaustExtension3.addChild(this.bone77);
            this.setRotationAngle(this.bone77, 0.2618F, 0.0873F, -0.2618F);
            this.bone77.cubeList.add(new ModelBox(this.bone77, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, true));
            this.bone78 = new ModelRenderer(this);
            this.bone78.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone77.addChild(this.bone78);
            this.setRotationAngle(this.bone78, 0.2641F, -0.0183F, -0.3979F);
            this.bone78.cubeList.add(new ModelBox(this.bone78, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, true));
            this.bone79 = new ModelRenderer(this);
            this.bone79.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone78.addChild(this.bone79);
            this.setRotationAngle(this.bone79, 0.1742F, 0.0076F, -0.6105F);
            this.bone79.cubeList.add(new ModelBox(this.bone79, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, true));
            this.bone80 = new ModelRenderer(this);
            this.bone80.setRotationPoint(0.0F, -4.5F, 0.0F);
            this.bone79.addChild(this.bone80);
            this.setRotationAngle(this.bone80, 0.1743F, 0.0113F, -0.3039F);
            this.bone80.cubeList.add(new ModelBox(this.bone80, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, true));
            this.ball3 = new ModelRenderer(this);
            this.ball3.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone80.addChild(this.ball3);
            this.exhaust4 = new ModelRenderer(this);
            this.exhaust4.setRotationPoint(2.0F, 3.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust4);
            this.setRotationAngle(this.exhaust4, -1.9199F, 0.6109F, 0.0F);
            this.exhaust4.cubeList.add(new ModelBox(this.exhaust4, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, false));
            this.bone84 = new ModelRenderer(this);
            this.bone84.setRotationPoint(-2.0F, -5.9F, 2.1F);
            this.exhaust4.addChild(this.bone84);
            this.setRotationAngle(this.bone84, 0.5236F, 0.0F, 0.2618F);
            this.bone84.cubeList.add(new ModelBox(this.bone84, 0, 43, 0.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, false));
            this.bone90 = new ModelRenderer(this);
            this.bone90.setRotationPoint(-0.1F, -3.8F, 0.2F);
            this.bone84.addChild(this.bone90);
            this.setRotationAngle(this.bone90, 0.3478F, -0.0298F, 0.3438F);
            this.bone90.cubeList.add(new ModelBox(this.bone90, 0, 43, 0.0F, -4.0F, -4.0F, 4, 4, 4, -0.2F, false));
            this.exhaustExtension4 = new ModelRenderer(this);
            this.exhaustExtension4.setRotationPoint(2.0F, -3.3F, 0.0F);
            this.bone90.addChild(this.exhaustExtension4);
            this.setRotationAngle(this.exhaustExtension4, 0.1745F, -0.1745F, 0.2618F);
            this.exhaustExtension4.cubeList.add(new ModelBox(this.exhaustExtension4, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, false));
            this.bone93 = new ModelRenderer(this);
            this.bone93.setRotationPoint(0.0F, -3.25F, 0.0F);
            this.exhaustExtension4.addChild(this.bone93);
            this.setRotationAngle(this.bone93, 0.2618F, -0.0873F, 0.2618F);
            this.bone93.cubeList.add(new ModelBox(this.bone93, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, false));
            this.bone98 = new ModelRenderer(this);
            this.bone98.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone93.addChild(this.bone98);
            this.setRotationAngle(this.bone98, 0.2641F, 0.0183F, 0.3979F);
            this.bone98.cubeList.add(new ModelBox(this.bone98, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, false));
            this.bone103 = new ModelRenderer(this);
            this.bone103.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone98.addChild(this.bone103);
            this.setRotationAngle(this.bone103, 0.1742F, -0.0076F, 0.6105F);
            this.bone103.cubeList.add(new ModelBox(this.bone103, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, false));
            this.bone104 = new ModelRenderer(this);
            this.bone104.setRotationPoint(0.0F, -4.5F, 0.0F);
            this.bone103.addChild(this.bone104);
            this.setRotationAngle(this.bone104, 0.1743F, -0.0113F, 0.3039F);
            this.bone104.cubeList.add(new ModelBox(this.bone104, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, false));
            this.ball4 = new ModelRenderer(this);
            this.ball4.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone104.addChild(this.ball4);
            this.exhaust5 = new ModelRenderer(this);
            this.exhaust5.setRotationPoint(-2.0F, 3.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust5);
            this.setRotationAngle(this.exhaust5, -2.618F, -0.2618F, 0.0F);
            this.exhaust5.cubeList.add(new ModelBox(this.exhaust5, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, true));
            this.bone82 = new ModelRenderer(this);
            this.bone82.setRotationPoint(2.25F, -5.9F, 2.1F);
            this.exhaust5.addChild(this.bone82);
            this.setRotationAngle(this.bone82, 0.5087F, 0.1298F, -0.228F);
            this.bone82.cubeList.add(new ModelBox(this.bone82, 0, 43, -4.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, true));
            this.bone83 = new ModelRenderer(this);
            this.bone83.setRotationPoint(-2.0F, -3.55F, -2.0F);
            this.bone82.addChild(this.bone83);
            this.setRotationAngle(this.bone83, 0.3491F, 0.1658F, -0.1658F);
            this.bone83.cubeList.add(new ModelBox(this.bone83, 0, 43, -2.0F, -3.25F, -2.0F, 4, 4, 4, -0.2F, true));
            this.exhaustExtension5 = new ModelRenderer(this);
            this.exhaustExtension5.setRotationPoint(0.0F, -2.55F, 2.0F);
            this.bone83.addChild(this.exhaustExtension5);
            this.setRotationAngle(this.exhaustExtension5, 0.3245F, 0.2178F, -0.475F);
            this.exhaustExtension5.cubeList.add(new ModelBox(this.exhaustExtension5, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, true));
            this.bone85 = new ModelRenderer(this);
            this.bone85.setRotationPoint(0.0F, -3.25F, 0.0F);
            this.exhaustExtension5.addChild(this.bone85);
            this.setRotationAngle(this.bone85, 0.2126F, 0.1339F, -0.2903F);
            this.bone85.cubeList.add(new ModelBox(this.bone85, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, true));
            this.bone87 = new ModelRenderer(this);
            this.bone87.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone85.addChild(this.bone87);
            this.setRotationAngle(this.bone87, 0.0805F, 0.1428F, -0.3239F);
            this.bone87.cubeList.add(new ModelBox(this.bone87, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, true));
            this.bone88 = new ModelRenderer(this);
            this.bone88.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone87.addChild(this.bone88);
            this.setRotationAngle(this.bone88, 0.1815F, 0.0499F, -0.6301F);
            this.bone88.cubeList.add(new ModelBox(this.bone88, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, true));
            this.bone89 = new ModelRenderer(this);
            this.bone89.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone88.addChild(this.bone89);
            this.setRotationAngle(this.bone89, 0.2601F, 0.0887F, -0.4182F);
            this.bone89.cubeList.add(new ModelBox(this.bone89, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, true));
            this.bone81 = new ModelRenderer(this);
            this.bone81.setRotationPoint(0.0F, -5.0F, 0.0F);
            this.bone89.addChild(this.bone81);
            this.setRotationAngle(this.bone81, 0.1478F, -0.0058F, -0.169F);
            this.bone81.cubeList.add(new ModelBox(this.bone81, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, true));
            this.ball5 = new ModelRenderer(this);
            this.ball5.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone81.addChild(this.ball5);
            this.exhaust6 = new ModelRenderer(this);
            this.exhaust6.setRotationPoint(2.0F, 3.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust6);
            this.setRotationAngle(this.exhaust6, -2.618F, 0.2618F, 0.0F);
            this.exhaust6.cubeList.add(new ModelBox(this.exhaust6, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, false));
            this.bone64 = new ModelRenderer(this);
            this.bone64.setRotationPoint(-2.25F, -5.9F, 2.1F);
            this.exhaust6.addChild(this.bone64);
            this.setRotationAngle(this.bone64, 0.5087F, -0.1298F, 0.228F);
            this.bone64.cubeList.add(new ModelBox(this.bone64, 0, 43, 0.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, false));
            this.bone65 = new ModelRenderer(this);
            this.bone65.setRotationPoint(2.0F, -3.55F, -2.0F);
            this.bone64.addChild(this.bone65);
            this.setRotationAngle(this.bone65, 0.3491F, -0.1658F, 0.1658F);
            this.bone65.cubeList.add(new ModelBox(this.bone65, 0, 43, -2.0F, -3.25F, -2.0F, 4, 4, 4, -0.2F, false));
            this.exhaustExtension6 = new ModelRenderer(this);
            this.exhaustExtension6.setRotationPoint(0.0F, -2.55F, 2.0F);
            this.bone65.addChild(this.exhaustExtension6);
            this.setRotationAngle(this.exhaustExtension6, 0.3245F, -0.2178F, 0.475F);
            this.exhaustExtension6.cubeList.add(new ModelBox(this.exhaustExtension6, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, false));
            this.bone66 = new ModelRenderer(this);
            this.bone66.setRotationPoint(0.0F, -3.25F, 0.0F);
            this.exhaustExtension6.addChild(this.bone66);
            this.setRotationAngle(this.bone66, 0.2126F, -0.1339F, 0.2903F);
            this.bone66.cubeList.add(new ModelBox(this.bone66, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, false));
            this.bone68 = new ModelRenderer(this);
            this.bone68.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone66.addChild(this.bone68);
            this.setRotationAngle(this.bone68, 0.0805F, -0.1428F, 0.3239F);
            this.bone68.cubeList.add(new ModelBox(this.bone68, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, false));
            this.bone73 = new ModelRenderer(this);
            this.bone73.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone68.addChild(this.bone73);
            this.setRotationAngle(this.bone73, 0.1815F, -0.0499F, 0.6301F);
            this.bone73.cubeList.add(new ModelBox(this.bone73, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, false));
            this.bone76 = new ModelRenderer(this);
            this.bone76.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone73.addChild(this.bone76);
            this.setRotationAngle(this.bone76, 0.2601F, -0.0887F, 0.4182F);
            this.bone76.cubeList.add(new ModelBox(this.bone76, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, false));
            this.bone105 = new ModelRenderer(this);
            this.bone105.setRotationPoint(0.0F, -5.0F, 0.0F);
            this.bone76.addChild(this.bone105);
            this.setRotationAngle(this.bone105, 0.1478F, 0.0058F, 0.169F);
            this.bone105.cubeList.add(new ModelBox(this.bone105, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, false));
            this.ball6 = new ModelRenderer(this);
            this.ball6.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone105.addChild(this.ball6);
            this.exhaust7 = new ModelRenderer(this);
            this.exhaust7.setRotationPoint(-2.0F, 7.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust7);
            this.setRotationAngle(this.exhaust7, -2.618F, -0.2618F, 0.0F);
            this.exhaust7.cubeList.add(new ModelBox(this.exhaust7, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, true));
            this.bone91 = new ModelRenderer(this);
            this.bone91.setRotationPoint(2.25F, -5.9F, 2.1F);
            this.exhaust7.addChild(this.bone91);
            this.setRotationAngle(this.bone91, 0.5087F, 0.1298F, -0.228F);
            this.bone91.cubeList.add(new ModelBox(this.bone91, 0, 43, -4.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, true));
            this.bone92 = new ModelRenderer(this);
            this.bone92.setRotationPoint(-1.75F, -3.1F, -1.8F);
            this.bone91.addChild(this.bone92);
            this.setRotationAngle(this.bone92, 0.2443F, 0.1134F, -0.3316F);
            this.bone92.cubeList.add(new ModelBox(this.bone92, 0, 43, -2.0F, -3.45F, -2.0F, 4, 4, 4, -0.2F, true));
            this.exhaustExtension7 = new ModelRenderer(this);
            this.exhaustExtension7.setRotationPoint(0.25F, -2.5F, 2.0F);
            this.bone92.addChild(this.exhaustExtension7);
            this.setRotationAngle(this.exhaustExtension7, -0.0436F, 0.1745F, -0.3491F);
            this.exhaustExtension7.cubeList.add(new ModelBox(this.exhaustExtension7, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, true));
            this.bone94 = new ModelRenderer(this);
            this.bone94.setRotationPoint(0.25F, -3.0F, 0.0F);
            this.exhaustExtension7.addChild(this.bone94);
            this.setRotationAngle(this.bone94, -0.0322F, 0.0892F, -0.612F);
            this.bone94.cubeList.add(new ModelBox(this.bone94, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, true));
            this.bone95 = new ModelRenderer(this);
            this.bone95.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone94.addChild(this.bone95);
            this.setRotationAngle(this.bone95, -0.0027F, 0.0111F, -0.5668F);
            this.bone95.cubeList.add(new ModelBox(this.bone95, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, true));
            this.bone96 = new ModelRenderer(this);
            this.bone96.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone95.addChild(this.bone96);
            this.setRotationAngle(this.bone96, 0.0169F, -0.0701F, -0.4798F);
            this.bone96.cubeList.add(new ModelBox(this.bone96, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, true));
            this.bone97 = new ModelRenderer(this);
            this.bone97.setRotationPoint(0.0F, -4.5F, 0.0F);
            this.bone96.addChild(this.bone97);
            this.setRotationAngle(this.bone97, 0.001F, 0.0227F, -0.3481F);
            this.bone97.cubeList.add(new ModelBox(this.bone97, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, true));
            this.ball7 = new ModelRenderer(this);
            this.ball7.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone97.addChild(this.ball7);
            this.exhaust8 = new ModelRenderer(this);
            this.exhaust8.setRotationPoint(2.0F, 7.0F, 0.25F);
            this.bodyStage2.addChild(this.exhaust8);
            this.setRotationAngle(this.exhaust8, -2.618F, 0.2618F, 0.0F);
            this.exhaust8.cubeList.add(new ModelBox(this.exhaust8, 0, 43, -2.0F, -6.0F, -2.0F, 4, 6, 4, 0.0F, false));
            this.bone58 = new ModelRenderer(this);
            this.bone58.setRotationPoint(-2.25F, -5.9F, 2.1F);
            this.exhaust8.addChild(this.bone58);
            this.setRotationAngle(this.bone58, 0.5087F, -0.1298F, 0.228F);
            this.bone58.cubeList.add(new ModelBox(this.bone58, 0, 43, 0.0F, -4.0F, -4.0F, 4, 4, 4, -0.1F, false));
            this.bone59 = new ModelRenderer(this);
            this.bone59.setRotationPoint(1.75F, -3.1F, -1.8F);
            this.bone58.addChild(this.bone59);
            this.setRotationAngle(this.bone59, 0.2443F, -0.1134F, 0.3316F);
            this.bone59.cubeList.add(new ModelBox(this.bone59, 0, 43, -2.0F, -3.45F, -2.0F, 4, 4, 4, -0.2F, false));
            this.exhaustExtension8 = new ModelRenderer(this);
            this.exhaustExtension8.setRotationPoint(-0.25F, -2.5F, 2.0F);
            this.bone59.addChild(this.exhaustExtension8);
            this.setRotationAngle(this.exhaustExtension8, -0.0436F, -0.1745F, 0.3491F);
            this.exhaustExtension8.cubeList.add(new ModelBox(this.exhaustExtension8, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.0F, false));
            this.bone60 = new ModelRenderer(this);
            this.bone60.setRotationPoint(-0.25F, -3.0F, 0.0F);
            this.exhaustExtension8.addChild(this.bone60);
            this.setRotationAngle(this.bone60, -0.0322F, -0.0892F, 0.612F);
            this.bone60.cubeList.add(new ModelBox(this.bone60, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.2F, false));
            this.bone61 = new ModelRenderer(this);
            this.bone61.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone60.addChild(this.bone61);
            this.setRotationAngle(this.bone61, -0.0027F, -0.0111F, 0.5668F);
            this.bone61.cubeList.add(new ModelBox(this.bone61, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.4F, false));
            this.bone62 = new ModelRenderer(this);
            this.bone62.setRotationPoint(0.0F, -3.5F, 0.0F);
            this.bone61.addChild(this.bone62);
            this.setRotationAngle(this.bone62, 0.0169F, 0.0701F, 0.4798F);
            this.bone62.cubeList.add(new ModelBox(this.bone62, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.6F, false));
            this.bone63 = new ModelRenderer(this);
            this.bone63.setRotationPoint(0.0F, -4.5F, 0.0F);
            this.bone62.addChild(this.bone63);
            this.setRotationAngle(this.bone63, 0.001F, -0.0227F, 0.3481F);
            this.bone63.cubeList.add(new ModelBox(this.bone63, 0, 43, -2.0F, -4.0F, -4.0F, 4, 4, 4, 0.8F, false));
            this.ball8 = new ModelRenderer(this);
            this.ball8.setRotationPoint(0.0F, -8.0F, -2.0F);
            this.bone63.addChild(this.ball8);
            this.bipedRightArm = new ModelRenderer(this);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
            this.setRotationAngle(this.bipedRightArm, -0.3927F, 0.0F, 0.0F);
            this.bipedRightArm.cubeList.add(new ModelBox(this.bipedRightArm, 40, 16, -3.0F, -2.0F, -2.0F, 4, 12, 4, 0.11F, false));
            this.rightArmSpikes = new ModelRenderer(this);
            this.rightArmSpikes.setRotationPoint(-1.0F, 6.0F, 0.0F);
            this.bipedRightArm.addChild(this.rightArmSpikes);
            this.bone129 = new ModelRenderer(this);
            this.bone129.setRotationPoint(0.25F, 4.5F, 0.0F);
            this.rightArmSpikes.addChild(this.bone129);
            this.setRotationAngle(this.bone129, 0.0F, 0.0F, -0.3491F);
            this.bone72 = new ModelRenderer(this);
            this.bone72.setRotationPoint(-2.0F, 1.0F, 0.0F);
            this.bone129.addChild(this.bone72);
            this.setRotationAngle(this.bone72, 0.0F, 0.0F, -0.3491F);
            this.bone25 = new ModelRenderer(this);
            this.bone25.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone72.addChild(this.bone25);
            this.setRotationAngle(this.bone25, 0.0F, 0.7854F, 0.0F);
            this.bone25.cubeList.add(new ModelBox(this.bone25, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone125 = new ModelRenderer(this);
            this.bone125.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone129.addChild(this.bone125);
            this.setRotationAngle(this.bone125, 0.3491F, 0.0F, 0.0F);
            this.bone126 = new ModelRenderer(this);
            this.bone126.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone125.addChild(this.bone126);
            this.setRotationAngle(this.bone126, 0.0F, -0.7854F, 0.0F);
            this.bone126.cubeList.add(new ModelBox(this.bone126, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone127 = new ModelRenderer(this);
            this.bone127.setRotationPoint(0.0F, 1.0F, 2.0F);
            this.bone129.addChild(this.bone127);
            this.setRotationAngle(this.bone127, -0.3491F, 0.0F, 0.0F);
            this.bone128 = new ModelRenderer(this);
            this.bone128.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone127.addChild(this.bone128);
            this.setRotationAngle(this.bone128, 0.0F, 2.3562F, 0.0F);
            this.bone128.cubeList.add(new ModelBox(this.bone128, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone124 = new ModelRenderer(this);
            this.bone124.setRotationPoint(-1.5F, 1.0F, -1.5F);
            this.bone129.addChild(this.bone124);
            this.setRotationAngle(this.bone124, 0.2618F, 0.0F, -0.2618F);
            this.bone124.cubeList.add(new ModelBox(this.bone124, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone86 = new ModelRenderer(this);
            this.bone86.setRotationPoint(-1.5F, 1.0F, 1.5F);
            this.bone129.addChild(this.bone86);
            this.setRotationAngle(this.bone86, -1.5708F, 1.309F, -1.8326F);
            this.bone86.cubeList.add(new ModelBox(this.bone86, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone131 = new ModelRenderer(this);
            this.bone131.setRotationPoint(0.0F, 2.5F, 0.0F);
            this.rightArmSpikes.addChild(this.bone131);
            this.setRotationAngle(this.bone131, 0.0F, -0.3491F, -0.1745F);
            this.bone132 = new ModelRenderer(this);
            this.bone132.setRotationPoint(-2.0F, 1.0F, 0.0F);
            this.bone131.addChild(this.bone132);
            this.setRotationAngle(this.bone132, 0.0F, 0.0F, -0.3491F);
            this.bone133 = new ModelRenderer(this);
            this.bone133.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone132.addChild(this.bone133);
            this.setRotationAngle(this.bone133, 0.0F, 0.7854F, 0.0F);
            this.bone133.cubeList.add(new ModelBox(this.bone133, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone134 = new ModelRenderer(this);
            this.bone134.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone131.addChild(this.bone134);
            this.setRotationAngle(this.bone134, 0.3491F, 0.0F, 0.0F);
            this.bone135 = new ModelRenderer(this);
            this.bone135.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone134.addChild(this.bone135);
            this.setRotationAngle(this.bone135, 0.0F, -0.7854F, 0.0F);
            this.bone135.cubeList.add(new ModelBox(this.bone135, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone136 = new ModelRenderer(this);
            this.bone136.setRotationPoint(0.0F, 1.0F, 2.0F);
            this.bone131.addChild(this.bone136);
            this.setRotationAngle(this.bone136, -0.3491F, 0.0F, 0.0F);
            this.bone137 = new ModelRenderer(this);
            this.bone137.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone136.addChild(this.bone137);
            this.setRotationAngle(this.bone137, 0.0F, 2.3562F, 0.0F);
            this.bone137.cubeList.add(new ModelBox(this.bone137, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone138 = new ModelRenderer(this);
            this.bone138.setRotationPoint(-1.5F, 1.0F, -1.5F);
            this.bone131.addChild(this.bone138);
            this.setRotationAngle(this.bone138, 0.2618F, 0.0F, -0.2618F);
            this.bone138.cubeList.add(new ModelBox(this.bone138, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone139 = new ModelRenderer(this);
            this.bone139.setRotationPoint(-1.5F, 1.0F, 1.5F);
            this.bone131.addChild(this.bone139);
            this.setRotationAngle(this.bone139, -1.5708F, 1.309F, -1.8326F);
            this.bone139.cubeList.add(new ModelBox(this.bone139, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone140 = new ModelRenderer(this);
            this.bone140.setRotationPoint(0.0F, 0.5F, 0.0F);
            this.rightArmSpikes.addChild(this.bone140);
            this.setRotationAngle(this.bone140, 0.0F, 0.0F, -0.0873F);
            this.bone141 = new ModelRenderer(this);
            this.bone141.setRotationPoint(-2.0F, 1.0F, 0.0F);
            this.bone140.addChild(this.bone141);
            this.setRotationAngle(this.bone141, 0.0F, 0.0F, -0.3491F);
            this.bone142 = new ModelRenderer(this);
            this.bone142.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone141.addChild(this.bone142);
            this.setRotationAngle(this.bone142, 0.0F, 0.7854F, 0.0F);
            this.bone142.cubeList.add(new ModelBox(this.bone142, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone143 = new ModelRenderer(this);
            this.bone143.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone140.addChild(this.bone143);
            this.setRotationAngle(this.bone143, 0.3491F, 0.0F, 0.0F);
            this.bone144 = new ModelRenderer(this);
            this.bone144.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone143.addChild(this.bone144);
            this.setRotationAngle(this.bone144, 0.0F, -0.7854F, 0.0F);
            this.bone144.cubeList.add(new ModelBox(this.bone144, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone145 = new ModelRenderer(this);
            this.bone145.setRotationPoint(0.0F, 1.0F, 2.0F);
            this.bone140.addChild(this.bone145);
            this.setRotationAngle(this.bone145, -0.3491F, 0.0F, 0.0F);
            this.bone146 = new ModelRenderer(this);
            this.bone146.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone145.addChild(this.bone146);
            this.setRotationAngle(this.bone146, 0.0F, 2.3562F, 0.0F);
            this.bone146.cubeList.add(new ModelBox(this.bone146, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone147 = new ModelRenderer(this);
            this.bone147.setRotationPoint(-1.5F, 1.0F, -1.5F);
            this.bone140.addChild(this.bone147);
            this.setRotationAngle(this.bone147, 0.2618F, 0.0F, -0.2618F);
            this.bone147.cubeList.add(new ModelBox(this.bone147, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone148 = new ModelRenderer(this);
            this.bone148.setRotationPoint(-1.5F, 1.0F, 1.5F);
            this.bone140.addChild(this.bone148);
            this.setRotationAngle(this.bone148, -1.5708F, 1.309F, -1.8326F);
            this.bone148.cubeList.add(new ModelBox(this.bone148, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone150 = new ModelRenderer(this);
            this.bone150.setRotationPoint(0.0F, -1.5F, 0.0F);
            this.rightArmSpikes.addChild(this.bone150);
            this.setRotationAngle(this.bone150, 0.0F, 0.3491F, 0.0F);
            this.bone151 = new ModelRenderer(this);
            this.bone151.setRotationPoint(-2.0F, 1.0F, 0.0F);
            this.bone150.addChild(this.bone151);
            this.setRotationAngle(this.bone151, 0.0F, 0.0F, -0.3491F);
            this.bone152 = new ModelRenderer(this);
            this.bone152.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone151.addChild(this.bone152);
            this.setRotationAngle(this.bone152, 0.0F, 0.7854F, 0.0F);
            this.bone152.cubeList.add(new ModelBox(this.bone152, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone153 = new ModelRenderer(this);
            this.bone153.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone150.addChild(this.bone153);
            this.setRotationAngle(this.bone153, 0.3491F, 0.0F, 0.0F);
            this.bone154 = new ModelRenderer(this);
            this.bone154.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone153.addChild(this.bone154);
            this.setRotationAngle(this.bone154, 0.0F, -0.7854F, 0.0F);
            this.bone154.cubeList.add(new ModelBox(this.bone154, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone157 = new ModelRenderer(this);
            this.bone157.setRotationPoint(-1.5F, 1.0F, -1.5F);
            this.bone150.addChild(this.bone157);
            this.setRotationAngle(this.bone157, 0.2618F, 0.0F, -0.2618F);
            this.bone157.cubeList.add(new ModelBox(this.bone157, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.bone158 = new ModelRenderer(this);
            this.bone158.setRotationPoint(-1.5F, 1.0F, 1.5F);
            this.bone150.addChild(this.bone158);
            this.setRotationAngle(this.bone158, -1.5708F, 1.309F, -1.8326F);
            this.bone158.cubeList.add(new ModelBox(this.bone158, 40, 32, 0.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, false));
            this.armExhaust = new ModelRenderer(this);
            this.armExhaust.setRotationPoint(-3.5F, 2.5F, 0.0F);
            this.bipedRightArm.addChild(this.armExhaust);
            this.setRotationAngle(this.armExhaust, 0.0F, 0.0F, -0.1309F);
            this.bone106 = new ModelRenderer(this);
            this.bone106.setRotationPoint(0.5F, -0.5F, 0.0F);
            this.armExhaust.addChild(this.bone106);
            this.setRotationAngle(this.bone106, -0.0315F, -0.8124F, -0.501F);
            this.bone106.cubeList.add(new ModelBox(this.bone106, 0, 0, -1.0F, -6.0F, -1.0F, 2, 6, 2, 0.2F, false));
            this.bone109 = new ModelRenderer(this);
            this.bone109.setRotationPoint(1.0F, -2.5F, -1.0F);
            this.armExhaust.addChild(this.bone109);
            this.setRotationAngle(this.bone109, 0.3182F, -0.7925F, -0.6233F);
            this.bone109.cubeList.add(new ModelBox(this.bone109, 0, 0, -1.0F, -6.0F, -1.0F, 2, 6, 2, 0.2F, false));
            this.bone110 = new ModelRenderer(this);
            this.bone110.setRotationPoint(1.0F, -2.5F, 1.0F);
            this.armExhaust.addChild(this.bone110);
            this.setRotationAngle(this.bone110, -0.3864F, -0.9815F, -0.0904F);
            this.bone110.cubeList.add(new ModelBox(this.bone110, 0, 0, -1.0F, -6.0F, -1.0F, 2, 6, 2, 0.2F, false));
            this.bone107 = new ModelRenderer(this);
            this.bone107.setRotationPoint(1.0F, -0.5F, -1.0F);
            this.armExhaust.addChild(this.bone107);
            this.setRotationAngle(this.bone107, 0.6902F, -0.7106F, -1.0887F);
            this.bone107.cubeList.add(new ModelBox(this.bone107, 0, 0, -1.0F, -6.0F, -1.0F, 2, 6, 2, 0.2F, false));
            this.bone108 = new ModelRenderer(this);
            this.bone108.setRotationPoint(1.0F, -0.5F, 1.0F);
            this.armExhaust.addChild(this.bone108);
            this.setRotationAngle(this.bone108, -0.7646F, -0.8326F, 0.018F);
            this.bone108.cubeList.add(new ModelBox(this.bone108, 0, 0, -1.0F, -6.0F, -1.0F, 2, 6, 2, 0.2F, false));
            this.needle = new ModelRenderer(this);
            this.needle.setRotationPoint(0.0F, 10.0F, 0.0F);
            this.bipedRightArm.addChild(this.needle);
            this.setRotationAngle(this.needle, 0.0F, -0.7854F, 0.0F);
            this.needle.cubeList.add(new ModelBox(this.needle, 56, 16, -0.5F, 0.0F, -0.5F, 1, 8, 1, 0.0F, false));
            this.needle.cubeList.add(new ModelBox(this.needle, 56, 16, -0.5F, 7.75F, -0.5F, 1, 4, 1, -0.1F, false));
            this.needle.cubeList.add(new ModelBox(this.needle, 56, 16, -0.5F, 11.25F, -0.5F, 1, 2, 1, -0.2F, false));
            this.needle.cubeList.add(new ModelBox(this.needle, 56, 16, -0.5F, 12.75F, -0.5F, 1, 2, 1, -0.3F, false));
            this.needle.cubeList.add(new ModelBox(this.needle, 56, 16, -0.5F, 13.75F, -0.5F, 1, 2, 1, -0.4F, false));
            this.bulge = new ModelRenderer(this);
            this.bulge.setRotationPoint(-0.5F, 16.0F, 0.5F);
            this.needle.addChild(this.bulge);
            this.bulge.cubeList.add(new ModelBox(this.bulge, 56, 17, 0.0F, -2.0F, -1.0F, 1, 2, 1, 0.4F, false));
            this.bulge.cubeList.add(new ModelBox(this.bulge, 56, 17, 0.0F, -3.0F, -1.0F, 1, 4, 1, 0.2F, false));
            this.bulge.cubeList.add(new ModelBox(this.bulge, 56, 17, 0.0F, -4.0F, -1.0F, 1, 6, 1, 0.0F, false));
            this.bipedLeftArm = new ModelRenderer(this);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.setRotationAngle(this.bipedLeftArm, 0.3927F, 0.0F, 0.0F);
            this.bipedLeftArm.cubeList.add(new ModelBox(this.bipedLeftArm, 40, 16, -1.0F, -2.0F, -2.0F, 4, 12, 4, 0.11F, true));
            this.leftArmSpikes = new ModelRenderer(this);
            this.leftArmSpikes.setRotationPoint(1.0F, 6.0F, 0.0F);
            this.bipedLeftArm.addChild(this.leftArmSpikes);
            this.bone19 = new ModelRenderer(this);
            this.bone19.setRotationPoint(-0.25F, 4.5F, 0.0F);
            this.leftArmSpikes.addChild(this.bone19);
            this.setRotationAngle(this.bone19, 0.0F, 0.0F, 0.3491F);
            this.bone20 = new ModelRenderer(this);
            this.bone20.setRotationPoint(2.0F, 1.0F, 0.0F);
            this.bone19.addChild(this.bone20);
            this.setRotationAngle(this.bone20, 0.0F, 0.0F, 0.3491F);
            this.bone21 = new ModelRenderer(this);
            this.bone21.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone20.addChild(this.bone21);
            this.setRotationAngle(this.bone21, 0.0F, -0.7854F, 0.0F);
            this.bone21.cubeList.add(new ModelBox(this.bone21, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone22 = new ModelRenderer(this);
            this.bone22.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone19.addChild(this.bone22);
            this.setRotationAngle(this.bone22, 0.3491F, 0.0F, 0.0F);
            this.bone23 = new ModelRenderer(this);
            this.bone23.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone22.addChild(this.bone23);
            this.setRotationAngle(this.bone23, 0.0F, 0.7854F, 0.0F);
            this.bone23.cubeList.add(new ModelBox(this.bone23, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone24 = new ModelRenderer(this);
            this.bone24.setRotationPoint(0.0F, 1.0F, 2.0F);
            this.bone19.addChild(this.bone24);
            this.setRotationAngle(this.bone24, -0.3491F, 0.0F, 0.0F);
            this.bone26 = new ModelRenderer(this);
            this.bone26.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone24.addChild(this.bone26);
            this.setRotationAngle(this.bone26, 0.0F, -2.3562F, 0.0F);
            this.bone26.cubeList.add(new ModelBox(this.bone26, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone27 = new ModelRenderer(this);
            this.bone27.setRotationPoint(1.5F, 1.0F, -1.5F);
            this.bone19.addChild(this.bone27);
            this.setRotationAngle(this.bone27, 0.2618F, 0.0F, 0.2618F);
            this.bone27.cubeList.add(new ModelBox(this.bone27, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone28 = new ModelRenderer(this);
            this.bone28.setRotationPoint(1.5F, 1.0F, 1.5F);
            this.bone19.addChild(this.bone28);
            this.setRotationAngle(this.bone28, -1.5708F, -1.309F, 1.8326F);
            this.bone28.cubeList.add(new ModelBox(this.bone28, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone29 = new ModelRenderer(this);
            this.bone29.setRotationPoint(0.0F, 2.5F, 0.0F);
            this.leftArmSpikes.addChild(this.bone29);
            this.setRotationAngle(this.bone29, 0.0F, 0.3491F, 0.1745F);
            this.bone32 = new ModelRenderer(this);
            this.bone32.setRotationPoint(2.0F, 1.0F, 0.0F);
            this.bone29.addChild(this.bone32);
            this.setRotationAngle(this.bone32, 0.0F, 0.0F, 0.3491F);
            this.bone34 = new ModelRenderer(this);
            this.bone34.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone32.addChild(this.bone34);
            this.setRotationAngle(this.bone34, 0.0F, -0.7854F, 0.0F);
            this.bone34.cubeList.add(new ModelBox(this.bone34, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone35 = new ModelRenderer(this);
            this.bone35.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone29.addChild(this.bone35);
            this.setRotationAngle(this.bone35, 0.3491F, 0.0F, 0.0F);
            this.bone36 = new ModelRenderer(this);
            this.bone36.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone35.addChild(this.bone36);
            this.setRotationAngle(this.bone36, 0.0F, 0.7854F, 0.0F);
            this.bone36.cubeList.add(new ModelBox(this.bone36, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone37 = new ModelRenderer(this);
            this.bone37.setRotationPoint(0.0F, 1.0F, 2.0F);
            this.bone29.addChild(this.bone37);
            this.setRotationAngle(this.bone37, -0.3491F, 0.0F, 0.0F);
            this.bone38 = new ModelRenderer(this);
            this.bone38.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone37.addChild(this.bone38);
            this.setRotationAngle(this.bone38, 0.0F, -2.3562F, 0.0F);
            this.bone38.cubeList.add(new ModelBox(this.bone38, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone39 = new ModelRenderer(this);
            this.bone39.setRotationPoint(1.5F, 1.0F, -1.5F);
            this.bone29.addChild(this.bone39);
            this.setRotationAngle(this.bone39, 0.2618F, 0.0F, 0.2618F);
            this.bone39.cubeList.add(new ModelBox(this.bone39, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone40 = new ModelRenderer(this);
            this.bone40.setRotationPoint(1.5F, 1.0F, 1.5F);
            this.bone29.addChild(this.bone40);
            this.setRotationAngle(this.bone40, -1.5708F, -1.309F, 1.8326F);
            this.bone40.cubeList.add(new ModelBox(this.bone40, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone41 = new ModelRenderer(this);
            this.bone41.setRotationPoint(0.0F, 0.5F, 0.0F);
            this.leftArmSpikes.addChild(this.bone41);
            this.setRotationAngle(this.bone41, 0.0F, 0.0F, 0.0873F);
            this.bone42 = new ModelRenderer(this);
            this.bone42.setRotationPoint(2.0F, 1.0F, 0.0F);
            this.bone41.addChild(this.bone42);
            this.setRotationAngle(this.bone42, 0.0F, 0.0F, 0.3491F);
            this.bone43 = new ModelRenderer(this);
            this.bone43.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone42.addChild(this.bone43);
            this.setRotationAngle(this.bone43, 0.0F, -0.7854F, 0.0F);
            this.bone43.cubeList.add(new ModelBox(this.bone43, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone44 = new ModelRenderer(this);
            this.bone44.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone41.addChild(this.bone44);
            this.setRotationAngle(this.bone44, 0.3491F, 0.0F, 0.0F);
            this.bone45 = new ModelRenderer(this);
            this.bone45.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone44.addChild(this.bone45);
            this.setRotationAngle(this.bone45, 0.0F, 0.7854F, 0.0F);
            this.bone45.cubeList.add(new ModelBox(this.bone45, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone46 = new ModelRenderer(this);
            this.bone46.setRotationPoint(0.0F, 1.0F, 2.0F);
            this.bone41.addChild(this.bone46);
            this.setRotationAngle(this.bone46, -0.3491F, 0.0F, 0.0F);
            this.bone48 = new ModelRenderer(this);
            this.bone48.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone46.addChild(this.bone48);
            this.setRotationAngle(this.bone48, 0.0F, -2.3562F, 0.0F);
            this.bone48.cubeList.add(new ModelBox(this.bone48, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone49 = new ModelRenderer(this);
            this.bone49.setRotationPoint(1.5F, 1.0F, -1.5F);
            this.bone41.addChild(this.bone49);
            this.setRotationAngle(this.bone49, 0.2618F, 0.0F, 0.2618F);
            this.bone49.cubeList.add(new ModelBox(this.bone49, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone50 = new ModelRenderer(this);
            this.bone50.setRotationPoint(1.5F, 1.0F, 1.5F);
            this.bone41.addChild(this.bone50);
            this.setRotationAngle(this.bone50, -1.5708F, -1.309F, 1.8326F);
            this.bone50.cubeList.add(new ModelBox(this.bone50, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone51 = new ModelRenderer(this);
            this.bone51.setRotationPoint(0.0F, -1.5F, 0.0F);
            this.leftArmSpikes.addChild(this.bone51);
            this.setRotationAngle(this.bone51, 0.0F, -0.3491F, 0.0F);
            this.bone52 = new ModelRenderer(this);
            this.bone52.setRotationPoint(2.0F, 1.0F, 0.0F);
            this.bone51.addChild(this.bone52);
            this.setRotationAngle(this.bone52, 0.0F, 0.0F, 0.3491F);
            this.bone53 = new ModelRenderer(this);
            this.bone53.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone52.addChild(this.bone53);
            this.setRotationAngle(this.bone53, 0.0F, -0.7854F, 0.0F);
            this.bone53.cubeList.add(new ModelBox(this.bone53, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone54 = new ModelRenderer(this);
            this.bone54.setRotationPoint(0.0F, 1.0F, -2.0F);
            this.bone51.addChild(this.bone54);
            this.setRotationAngle(this.bone54, 0.3491F, 0.0F, 0.0F);
            this.bone55 = new ModelRenderer(this);
            this.bone55.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.bone54.addChild(this.bone55);
            this.setRotationAngle(this.bone55, 0.0F, 0.7854F, 0.0F);
            this.bone55.cubeList.add(new ModelBox(this.bone55, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone56 = new ModelRenderer(this);
            this.bone56.setRotationPoint(1.5F, 1.0F, -1.5F);
            this.bone51.addChild(this.bone56);
            this.setRotationAngle(this.bone56, 0.2618F, 0.0F, 0.2618F);
            this.bone56.cubeList.add(new ModelBox(this.bone56, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bone57 = new ModelRenderer(this);
            this.bone57.setRotationPoint(1.5F, 1.0F, 1.5F);
            this.bone51.addChild(this.bone57);
            this.setRotationAngle(this.bone57, -1.5708F, -1.309F, 1.8326F);
            this.bone57.cubeList.add(new ModelBox(this.bone57, 40, 32, -4.0F, -6.0F, 0.0F, 4, 6, 4, 0.2F, true));
            this.bipedRightLeg = new ModelRenderer(this);
            this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
            this.setRotationAngle(this.bipedRightLeg, 0.3927F, 0.0F, 0.0F);
            this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, false));
            this.bipedRightLeg.cubeList.add(new ModelBox(this.bipedRightLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.35F, false));
            this.bipedLeftLeg = new ModelRenderer(this);
            this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
            this.setRotationAngle(this.bipedLeftLeg, -0.3927F, 0.0F, 0.0F);
            this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 16, -2.0F, 0.0F, -2.0F, 4, 12, 4, 0.1F, true));
            this.bipedLeftLeg.cubeList.add(new ModelBox(this.bipedLeftLeg, 0, 32, -2.0F, 0.0F, -2.0F, 4, 7, 4, 0.35F, true));
            this.blasts = new ModelRenderer(this);
            this.blasts.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.blast1 = new ModelRenderer(this);
            this.blast1.setRotationPoint(-6.6F, -7.6F, -8.45F);
            this.blasts.addChild(this.blast1);
            this.setRotationAngle(this.blast1, -1.2204F, -0.0806F, -0.0335F);
            this.blast1.cubeList.add(new ModelBox(this.blast1, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, false));
            this.blast2 = new ModelRenderer(this);
            this.blast2.setRotationPoint(6.6F, -7.6F, -8.45F);
            this.blasts.addChild(this.blast2);
            this.setRotationAngle(this.blast2, -1.2204F, 0.0806F, 0.0335F);
            this.blast2.cubeList.add(new ModelBox(this.blast2, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, true));
            this.blast3 = new ModelRenderer(this);
            this.blast3.setRotationPoint(-12.55F, -2.55F, -5.7F);
            this.blasts.addChild(this.blast3);
            this.setRotationAngle(this.blast3, -1.4785F, -0.2143F, -0.0423F);
            this.blast3.cubeList.add(new ModelBox(this.blast3, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, false));
            this.blast4 = new ModelRenderer(this);
            this.blast4.setRotationPoint(12.55F, -2.55F, -5.7F);
            this.blasts.addChild(this.blast4);
            this.setRotationAngle(this.blast4, -1.4785F, 0.2143F, 0.0423F);
            this.blast4.cubeList.add(new ModelBox(this.blast4, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, true));
            this.blast5 = new ModelRenderer(this);
            this.blast5.setRotationPoint(-14.4F, 6.1F, -6.7F);
            this.blasts.addChild(this.blast5);
            this.setRotationAngle(this.blast5, -1.5708F, -0.1745F, 0.0F);
            this.blast5.cubeList.add(new ModelBox(this.blast5, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, false));
            this.blast6 = new ModelRenderer(this);
            this.blast6.setRotationPoint(14.4F, 6.1F, -6.7F);
            this.blasts.addChild(this.blast6);
            this.setRotationAngle(this.blast6, -1.5708F, 0.1745F, 0.0F);
            this.blast6.cubeList.add(new ModelBox(this.blast6, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, true));
            this.blast7 = new ModelRenderer(this);
            this.blast7.setRotationPoint(-14.4F, 14.25F, -5.95F);
            this.blasts.addChild(this.blast7);
            this.setRotationAngle(this.blast7, -1.6144F, -0.0873F, 0.0F);
            this.blast7.cubeList.add(new ModelBox(this.blast7, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, false));
            this.blast8 = new ModelRenderer(this);
            this.blast8.setRotationPoint(14.4F, 14.25F, -5.95F);
            this.blasts.addChild(this.blast8);
            this.setRotationAngle(this.blast8, -1.6144F, 0.0873F, 0.0F);
            this.blast8.cubeList.add(new ModelBox(this.blast8, 11, 49, -2.5F, 0.0F, -2.5F, 5, 0, 5, 0.0F, true));
         }

         public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
         }

         public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (this.blasts.showModel) {
               GlStateManager.pushMatrix();
               if (entityIn.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               GlStateManager.enableBlend();
               GlStateManager.disableLighting();
               OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
               this.blasts.render(scale);
               GlStateManager.disableBlend();
               GlStateManager.enableLighting();
               GlStateManager.popMatrix();
            }

         }

         public void setVisible(boolean visible) {
            super.setVisible(visible);
            this.headStage0.showModel = visible;
            this.headStage1.showModel = visible;
            this.headStage2.showModel = visible;
            this.armExhaust.showModel = visible;
            this.bodyStage1.showModel = visible;
            this.bodyStage2.showModel = visible;
            this.exhaustExtension1.showModel = visible;
            this.exhaustExtension2.showModel = visible;
            this.exhaustExtension3.showModel = visible;
            this.exhaustExtension4.showModel = visible;
            this.exhaustExtension5.showModel = visible;
            this.exhaustExtension6.showModel = visible;
            this.exhaustExtension7.showModel = visible;
            this.exhaustExtension8.showModel = visible;
            this.blasts.showModel = visible;
            this.needle.showModel = visible;
            this.bulge.showModel = visible;
         }

         public void setModelAttributes(ModelBase model) {
            super.setModelAttributes(model);
            if (model instanceof ModelBiped) {
               this.wearerModel = (ModelBiped)model;
            }

         }

         public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
            if (entity instanceof AbstractClientPlayer && ((AbstractClientPlayer)entity).getSkinType().equals("slim")) {
               this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
               this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
            }

            super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
            if (this.wearerModel != null) {
               copyModelAngles(this.wearerModel.bipedLeftArm, this.bipedLeftArm);
               copyModelAngles(this.wearerModel.bipedRightArm, this.bipedRightArm);
               copyModelAngles(this.wearerModel.bipedLeftLeg, this.bipedLeftLeg);
               copyModelAngles(this.wearerModel.bipedRightLeg, this.bipedRightLeg);
            }

            float f6 = (float)entity.getEntityData().getInteger("SenninkaStartTime") + f2 - (float)entity.ticksExisted;
            if (f6 <= 40.0F) {
               float gb = MathHelper.clamp((f6 - 20.0F) / 20.0F, 0.0F, 1.0F);
               float a = MathHelper.clamp(f6 / 20.0F, 0.0F, 1.0F);
               GlStateManager.enableBlend();
               GlStateManager.alphaFunc(516, 0.001F);
               GlStateManager.color(1.0F, gb, gb, a);
               GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
               OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            }

         }
      }

      @SideOnly(Side.CLIENT)
      public class CustomRender extends Render<EntityMultiCannon> {
         private final ResourceLocation texture = new ResourceLocation("narutomod:textures/beam_gold.png");

         public CustomRender(RenderManager renderManagerIn) {
            super(renderManagerIn);
         }

         public boolean shouldRender(EntityMultiCannon livingEntity, ICamera camera, double camX, double camY, double camZ) {
            return true;
         }

         public void doRender(EntityMultiCannon bullet, double x, double y, double z, float yaw, float pt) {
            float age = (float)bullet.ticksExisted + pt;
            float f = age * 0.01F;
            float max_l = bullet.getBeamLength();
            this.bindEntityTexture(bullet);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(ProcedureUtils.interpolateRotation(bullet.prevRotationYaw, bullet.rotationYaw, pt), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F - bullet.prevRotationPitch - (bullet.rotationPitch - bullet.prevRotationPitch) * pt, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(age * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.alphaFunc(516, 0.001F);
            GlStateManager.disableCull();
            GlStateManager.depthFunc(7425);
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
            float f5 = 0.0F - f;
            float f6 = max_l / 32.0F - f;
            float f10 = Math.min(age / 100.0F, 1.0F);
            float f13 = f10 - 0.5F;
            float f11 = 1.0F - f13 * f13 * f13 * f13 * 15.0F;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(5, DefaultVertexFormats.POSITION_TEX_COLOR);

            for(float f12 = 0.0F; f12 < f11; f12 += 0.05F) {
               for(int j = 0; j <= 8; ++j) {
                  float f7 = MathHelper.sin((float)(j % 8) * 6.2831855F / 8.0F) * 1.0F;
                  float f8 = MathHelper.cos((float)(j % 8) * 6.2831855F / 8.0F) * 1.0F;
                  float f9 = (float)(j % 8) / 8.0F;
                  bufferbuilder.pos((double)f7, 0.0D, (double)f8).tex((double)f9, (double)f5).color(1.0F, 1.0F, 1.0F, 0.15F).endVertex();
                  bufferbuilder.pos((double)(f7 * f12 * max_l * 0.5F), (double)(max_l * f11), (double)(f8 * f12 * max_l * 0.5F)).tex((double)f9, (double)f6).color(1.0F, 1.0F, 1.0F, 0.0F).endVertex();
               }
            }

            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(7424);
            GlStateManager.popMatrix();
         }

         protected ResourceLocation getEntityTexture(EntityMultiCannon entity) {
            return this.texture;
         }
      }
   }

   public static class Absorption extends SenninkaJutsu {
      private static final String ID_KEY = "SenninkaAbsorbing";

      public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
         return true;
      }

      public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
         if (!world.isRemote && entity instanceof EntityLivingBase) {
            boolean flag = this.isActivated((EntityLivingBase)entity, stack);
            if (flag) {
               boolean absorbed = false;
               RayTraceResult res = ProcedureUtils.objectEntityLookingAt(entity, 2.2D);
               if (res != null && res.entityHit instanceof EntityLivingBase && res.entityHit.isEntityAlive()) {
                  res.entityHit.hurtResistantTime = 10;
                  res.entityHit.getEntityData().setBoolean("TempData_disableKnockback", true);
                  if (res.entityHit.attackEntityFrom(ItemJutsu.causeSenjutsuDamage(entity, (Entity)null).setDamageAllowedInCreativeMode(), 0.25F)) {
                     ((EntityLivingBase)entity).heal(0.25F);
                     if (entity.ticksExisted % 20 == 2) {
                        entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 0.5F, 1.0F - MathHelper.sin(0.02F * (float)entity.ticksExisted) * 0.3F);
                     }

                     if (!entity.getEntityData().getBoolean("SenninkaAbsorbing")) {
                        ProcedureSync.EntityNBTTag.setAndSync(entity, "SenninkaAbsorbing", true);
                     }

                     absorbed = true;
                  } else {
                     res.entityHit.getEntityData().removeTag("TempData_disableKnockback");
                  }
               }

               if (!absorbed && entity.getEntityData().getBoolean("SenninkaAbsorbing")) {
                  ProcedureSync.EntityNBTTag.removeAndSync(entity, "SenninkaAbsorbing");
               }
            }

            if (!this.anyOtherActivated((EntityLivingBase)entity, stack)) {
               if (flag) {
                  ProcedureSync.EntityNBTTag.setAndSync(entity, "SenninkaStartTime", entity.getEntityData().getInteger("SenninkaStartTime") + 1);
               } else if (entity.getEntityData().hasKey("SenninkaStartTime")) {
                  ProcedureSync.EntityNBTTag.removeAndSync(entity, "SenninkaStartTime");
               }
            }
         }

      }

      @SideOnly(Side.CLIENT)
      public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
         if (this.isActivated(living, stack) && !this.anyOtherActivated(living, stack)) {
            model.setVisible(false);
            model.bipedHead.showModel = true;
            model.headStage1.showModel = true;
            model.bipedRightArm.showModel = true;
            model.rightArmSpikes.rotateAngleX = 3.1416F;
            model.bipedBody.showModel = true;
            model.bodyStage1.showModel = true;
            model.isSneak = living.isSneaking();
            model.isRiding = living.isRiding();
            model.isChild = living.isChild();
            this.showNeedle(living, model);
            return true;
         } else {
            return false;
         }
      }

      @SideOnly(Side.CLIENT)
      protected void showNeedle(EntityLivingBase living, Renderer.ModelJugo model) {
         model.needle.showModel = true;
         if (living.getEntityData().getBoolean("SenninkaAbsorbing")) {
            model.bulge.showModel = true;
            model.bulge.offsetY = (float)(16 - living.getEntityData().getInteger("SenninkaStartTime") % 16);
         }

      }

      public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
         return ItemJutsu.getCurrentJutsu(stack) == ItemSenninka.ABSORB && entity.getActiveItemStack().equals(stack);
      }
   }

   public static class EntityMultiCannon extends EntityBeamBase.Base implements ItemJutsu.IJutsu {
      private final int duration = 100;
      private final AirPunch beam = new AirPunch();
      private float power;

      public EntityMultiCannon(World worldIn) {
         super(worldIn);
      }

      public EntityMultiCannon(EntityLivingBase shooter, float powerIn) {
         super(shooter);
         this.power = powerIn;
         this.updatePosition();
         this.shoot((double)powerIn);
      }

      public ItemJutsu.JutsuEnum.Type getJutsuType() {
         return ItemJutsu.JutsuEnum.Type.SENNINKA;
      }

      protected void updatePosition() {
         EntityLivingBase shooter = this.getShooter();
         if (shooter != null) {
            Vec3d vec = shooter.getLookVec().addVector(shooter.posX, shooter.posY + 1.2D, shooter.posZ);
            this.setPosition(vec.x, vec.y, vec.z);
         }

      }

      public void onUpdate() {
         super.onUpdate();
         if (!this.world.isRemote) {
            if (this.shootingEntity != null) {
               int var10000 = this.ticksAlive;
               this.getClass();
               if (var10000 <= 100) {
                  this.shoot((double)this.power);
                  if (this.ticksAlive > 5) {
                     this.beam.execute(this.shootingEntity, (double)this.getBeamLength(), (double)(this.power * 0.35F));
                  }

                  return;
               }
            }

            this.setDead();
         }

      }

      public void setDead() {
         super.setDead();
         if (!this.world.isRemote && this.getShooter() != null) {
            Jutsu.deactivateCleanup(this.getShooter());
         }

      }

      public static class Jutsu implements ItemJutsu.IJutsuCallback {
         private static final String ID_KEY = "MultiCannonActivated";

         public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            if (ItemSenninka.STAGE2.jutsu.isActivated(stack) && power >= this.getBasePower()) {
               EntityMultiCannon jutsuEntity = new EntityMultiCannon(entity, power);
               entity.world.spawnEntity(jutsuEntity);
               stack.getTagCompound().setBoolean("MultiCannonActivated", true);
               entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY + 2.0D, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:explosion")), SoundCategory.BLOCKS, 5.0F, 1.0F);
               return true;
            } else {
               deactivateCleanup(entity);
               return false;
            }
         }

         public float getBasePower() {
            return 10.0F;
         }

         public float getPowerupDelay() {
            return 20.0F;
         }

         public float getMaxPower() {
            return 40.0F;
         }

         public boolean isActivated(ItemStack stack) {
            return stack.hasTagCompound() ? stack.getTagCompound().getBoolean("MultiCannonActivated") : false;
         }

         public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
            return this.isActivated(stack) || ItemSenninka.STAGE2.jutsu.isActivated(stack) && ItemJutsu.getCurrentJutsu(stack) == ItemSenninka.CANNON && entity.getActiveItemStack().equals(stack) && ((ItemJutsu.Base)stack.getItem()).getPower(stack, entity, entity.getItemInUseCount()) >= this.getBasePower();
         }

         protected static void deactivateCleanup(EntityLivingBase entity) {
            ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, ItemSenninka.block);
            if (stack != null && stack.hasTagCompound()) {
               stack.getTagCompound().removeTag("MultiCannonActivated");
               ItemJutsu.setJutsuCooldown(stack, entity, ItemSenninka.CANNON, 400L);
            }

         }
      }

      public class AirPunch extends ProcedureAirPunch {
         public AirPunch() {
            this.blockDropChance = -1.0F;
            this.particlesPre = null;
         }

         protected void attackEntityFrom(Entity player, Entity target) {
            target.hurtResistantTime = 10;
            target.attackEntityFrom(ItemJutsu.causeJutsuDamage(EntityMultiCannon.this, player), EntityMultiCannon.this.power * 0.25F);
         }

         @Nullable
         protected EntityItem processAffectedBlock(Entity player, BlockPos pos, EnumFacing facing) {
            if (EntityMultiCannon.this.rand.nextFloat() < 0.005F) {
               player.world.playSound((EntityPlayer)null, pos, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:explosion")), SoundCategory.BLOCKS, 4.0F, EntityMultiCannon.this.rand.nextFloat() * 0.5F + 0.75F);
            }

            return super.processAffectedBlock(player, pos, facing);
         }

         protected void breakBlockParticles(World world, BlockPos pos) {
            Particles.spawnParticle(world, Particles.Types.SMOKE, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, Integer.MIN_VALUE, 40 + world.rand.nextInt(30));
         }

         protected float getBreakChance(BlockPos pos, Entity player, double range) {
            return (1.0F - (float)((double)MathHelper.sqrt(player.getDistanceSqToCenter(pos)) / range)) * 0.05F;
         }
      }
   }

   public static class Stage2 extends SenninkaJutsu {
      private final String idKey = "Stage2StackKey";
      private final Map<IAttribute, AttributeModifier> buffMap;

      public Stage2() {
         this.buffMap = ImmutableMap.<IAttribute, AttributeModifier>builder().put(SharedMonsterAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemSenninka.ATTACK_DAMAGE_MODIFIER, "senninka.damage", 60.0D, 0)).put(SharedMonsterAttributes.ATTACK_SPEED, new AttributeModifier(ItemSenninka.ATTACK_SPEED_MODIFIER, "senninka.damagespeed", 2.0D, 1)).put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(ItemSenninka.MOVEMENT_SPEED_MODIFIER, "senninka.movement", 1.8D, 1)).build();
      }

      public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
         if (!this.isActivated(stack)) {
            entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 1.0F, 0.8F);
            ItemSenninka.PISTONFIST.jutsu.deactivate(entity);
            NBTTagCompound var10000 = stack.getTagCompound();
            this.getClass();
            var10000.setBoolean("Stage2StackKey", true);
            Iterator var4 = this.buffMap.entrySet().iterator();

            while(var4.hasNext()) {
               Entry<IAttribute, AttributeModifier> entry = (Entry)var4.next();
               IAttributeInstance attr = entity.getEntityAttribute((IAttribute)entry.getKey());
               if (attr != null && !attr.hasModifier((AttributeModifier)entry.getValue())) {
                  attr.applyModifier((AttributeModifier)entry.getValue());
               }
            }

            return true;
         } else {
            this.deactivate(entity);
            return false;
         }
      }

      public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
         if (entity instanceof EntityLivingBase && !world.isRemote) {
            if (this.isActivated(itemstack)) {
               EntityLivingBase living = (EntityLivingBase)entity;
               if (entity.ticksExisted % 20 == 3) {
                  living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 22, 8, false, false));
                  living.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 22, 2, false, false));
               }

               int weartime = entity.getEntityData().getInteger("SenninkaStartTime");
               if (weartime > 40 && (!(entity instanceof EntityPlayerMP) || ((EntityPlayerMP)entity).getSpectatingEntity() == entity)) {
                  renderExhaust(living);
               }

               float xpRatio = ((RangedItem)itemstack.getItem()).getXpRatio(itemstack, ItemSenninka.STAGE2);
               if (entity instanceof EntityPlayerMP && ((EntityPlayerMP)entity).getSpectatingEntity() == entity && weartime > (int)(xpRatio * 300.0F)) {
                  this.spawnClone((EntityPlayer)entity, itemstack);
               }

               if (ItemSenninka.CANNON.jutsu.isActivated(itemstack) && living.getItemInUseCount() % 20 == 1) {
                  entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY + 2.0D, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:blast_charge")), SoundCategory.PLAYERS, 2.0F, 1.0F + (float)living.getItemInUseCount() / 20.0F * 0.1F);
               }

               if (!((RangedItem)itemstack.getItem()).isJutsuEnabled(itemstack, ItemSenninka.CANNON)) {
                  ((RangedItem)itemstack.getItem()).enableJutsu(itemstack, ItemSenninka.CANNON, true);
               }

               ProcedureSync.EntityNBTTag.setAndSync(entity, "SenninkaStartTime", weartime + 1);
            } else if (((RangedItem)itemstack.getItem()).isJutsuEnabled(itemstack, ItemSenninka.CANNON)) {
               ((RangedItem)itemstack.getItem()).enableJutsu(itemstack, ItemSenninka.CANNON, false);
            }
         }

      }

      public static void renderExhaust(EntityLivingBase living) {
         Vec3d vec = (new Vec3d(-0.365625D, 0.884375D, -0.440625D)).rotatePitch(-living.renderYawOffset * 3.1415927F / 180.0F).add(living.getPositionVector());
         Vec3d vec1 = (new Vec3d(-0.25D, -0.0625D, -0.25D)).scale(1.4D).rotatePitch(-living.renderYawOffset * 3.1415927F / 180.0F);
         Particles.spawnParticle(living.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 20, 0.0D, 0.0D, 0.0D, vec1.x, vec1.y, vec1.z, 553648127, 10, 3, 240);
         vec = (new Vec3d(-0.365625D, 0.61875D, -0.409375D)).rotatePitch(-living.renderYawOffset * 3.1415927F / 180.0F).add(living.getPositionVector());
         Particles.spawnParticle(living.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 20, 0.0D, 0.0D, 0.0D, vec1.x, vec1.y, vec1.z, 553648127, 10, 3, 240);
         vec = (new Vec3d(0.365625D, 0.884375D, -0.440625D)).rotatePitch(-living.renderYawOffset * 3.1415927F / 180.0F).add(living.getPositionVector());
         vec1 = (new Vec3d(0.25D, -0.0625D, -0.25D)).scale(1.4D).rotatePitch(-living.renderYawOffset * 3.1415927F / 180.0F);
         Particles.spawnParticle(living.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 20, 0.0D, 0.0D, 0.0D, vec1.x, vec1.y, vec1.z, 553648127, 10, 3, 240);
         vec = (new Vec3d(0.365625D, 0.61875D, -0.409375D)).rotatePitch(-living.renderYawOffset * 3.1415927F / 180.0F).add(living.getPositionVector());
         Particles.spawnParticle(living.world, Particles.Types.SMOKE, vec.x, vec.y, vec.z, 20, 0.0D, 0.0D, 0.0D, vec1.x, vec1.y, vec1.z, 553648127, 10, 3, 240);
      }

      private void setClone(ItemStack itemstack, EntitySenninkaClone.EntityCustom clone) {
         itemstack.getTagCompound().setInteger("CloneID", clone.getEntityId());
      }

      @Nullable
      private static EntitySenninkaClone.EntityCustom getClone(World world, ItemStack itemstack) {
         int id = getCloneId(itemstack);
         if (id <= 0) {
            return null;
         } else {
            Entity entity = world.getEntityByID(id);
            return entity instanceof EntitySenninkaClone.EntityCustom && entity.isEntityAlive() ? (EntitySenninkaClone.EntityCustom)entity : null;
         }
      }

      private static int getCloneId(ItemStack stack) {
         return stack.hasTagCompound() && stack.getTagCompound().hasKey("CloneID") ? stack.getTagCompound().getInteger("CloneID") : -1;
      }

      private void spawnClone(EntityPlayer original, ItemStack stack) {
         if (!original.world.isRemote && getClone(original.world, stack) == null) {
            EntitySenninkaClone.EntityCustom entity = new EntitySenninkaClone.EntityCustom(original);
            original.world.spawnEntity(entity);
            this.setClone(stack, entity);
         }

      }

      public static void revertOriginal(EntityPlayer player, ItemStack stack) {
         EntitySenninkaClone.EntityCustom clone = getClone(player.world, stack);
         if (clone != null && !clone.isDead) {
            clone.setDead();
         }

         stack.getTagCompound().removeTag("CloneID");
      }

      public void onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
         if (attacker.equals(target)) {
            target = ProcedureUtils.objectEntityLookingAt(attacker, 16.0D, 3.0D).entityHit;
            if (target instanceof EntityLivingBase) {
               attacker.attackTargetEntityWithCurrentItem(target);
            }
         } else if (target instanceof EntityLivingBase) {
            target.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.NEUTRAL, 1.0F, attacker.getRNG().nextFloat() * 0.5F + 0.5F);
            Vec3d vec = target.getPositionVector().subtract(attacker.getPositionVector()).normalize();
            Particles.Renderer particles = new Particles.Renderer(attacker.world);
            int i = 1;

            for(byte j = 25; i <= j; ++i) {
               Vec3d vec1 = vec.scale(-0.06D * (double)i);
               particles.spawnParticles(Particles.Types.SONIC_BOOM, attacker.posX, attacker.posY + 1.4D, attacker.posZ, 1, 0.0D, 0.0D, 0.0D, vec1.x, vec1.y, vec1.z, 16777215 | (int)((1.0F - (float)i / (float)j) * 64.0F) << 24, i * 2, (int)(5.0F * (1.0F + (float)i / (float)j * 0.5F)));
            }

            particles.send();
            attacker.rotationYaw = ProcedureUtils.getYawFromVec(vec);
            attacker.rotationPitch = ProcedureUtils.getPitchFromVec(vec);
            attacker.setPositionAndUpdate(target.posX - vec.x, target.posY - vec.y + 0.5D, target.posZ - vec.z);
         }

      }

      @SideOnly(Side.CLIENT)
      public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
         if (this.isActivated(stack)) {
            model.setVisible(false);
            model.bipedHead.showModel = true;
            model.bipedHeadwear.showModel = true;
            model.headStage0.showModel = true;
            model.headStage2.showModel = true;
            model.bipedRightArm.showModel = true;
            model.bipedLeftArm.showModel = true;
            model.armExhaust.showModel = true;
            model.rightArmSpikes.rotateAngleX = 0.0F;
            model.bipedBody.showModel = true;
            model.bodyStage2.showModel = true;
            if (((EntityMultiCannon.Jutsu)ItemSenninka.CANNON.jutsu).isActivated(living, stack)) {
               model.exhaustExtension1.showModel = true;
               model.exhaustExtension2.showModel = true;
               model.exhaustExtension3.showModel = true;
               model.exhaustExtension4.showModel = true;
               model.exhaustExtension5.showModel = true;
               model.exhaustExtension6.showModel = true;
               model.exhaustExtension7.showModel = true;
               model.exhaustExtension8.showModel = true;
               model.blasts.showModel = true;
            }

            if (((Absorption)ItemSenninka.ABSORB.jutsu).isActivated(living, stack)) {
               ((Absorption)ItemSenninka.ABSORB.jutsu).showNeedle(living, model);
            }

            model.isSneak = living.isSneaking();
            model.isRiding = living.isRiding();
            model.isChild = living.isChild();
            return true;
         } else {
            return false;
         }
      }

      public boolean isActivated(ItemStack stack) {
         boolean var2;
         if (stack.hasTagCompound()) {
            NBTTagCompound var10000 = stack.getTagCompound();
            this.getClass();
            if (var10000.getBoolean("Stage2StackKey")) {
               var2 = true;
               return var2;
            }
         }

         var2 = false;
         return var2;
      }

      public void deactivate(EntityLivingBase entity) {
         Iterator var2 = this.buffMap.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<IAttribute, AttributeModifier> entry = (Entry)var2.next();
            IAttributeInstance attr = entity.getEntityAttribute((IAttribute)entry.getKey());
            if (attr != null && attr.hasModifier((AttributeModifier)entry.getValue())) {
               attr.removeModifier((AttributeModifier)entry.getValue());
            }
         }

         ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, ItemSenninka.block);
         if (stack != null) {
            if (entity instanceof EntityPlayer) {
               revertOriginal((EntityPlayer)entity, stack);
            }

            NBTTagCompound var10000 = stack.getTagCompound();
            this.getClass();
            var10000.removeTag("Stage2StackKey");
            ItemJutsu.setJutsuCooldown(stack, entity, ItemSenninka.STAGE2, 400L);
         }

         ProcedureSync.EntityNBTTag.removeAndSync(entity, "SenninkaStartTime");
      }
   }

   public static class PistonFist extends SenninkaJutsu {
      private final String idKey = "PistonFistStackKey";
      private final Map<IAttribute, AttributeModifier> buffMap;

      public PistonFist() {
         this.buffMap = ImmutableMap.<IAttribute, AttributeModifier>builder().put(SharedMonsterAttributes.ATTACK_DAMAGE, new AttributeModifier(ItemSenninka.ATTACK_DAMAGE_MODIFIER, "senninka.damage", 50.0D, 0)).put(SharedMonsterAttributes.MOVEMENT_SPEED, new AttributeModifier(ItemSenninka.MOVEMENT_SPEED_MODIFIER, "senninka.movement", 1.5D, 1)).build();
      }

      public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
         if (!this.isActivated(stack)) {
            entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 1.0F, 0.8F);
            ItemSenninka.STAGE2.jutsu.deactivate(entity);
            NBTTagCompound var10000 = stack.getTagCompound();
            this.getClass();
            var10000.setBoolean("PistonFistStackKey", true);
            Iterator var4 = this.buffMap.entrySet().iterator();

            while(var4.hasNext()) {
               Entry<IAttribute, AttributeModifier> entry = (Entry)var4.next();
               IAttributeInstance attr = entity.getEntityAttribute((IAttribute)entry.getKey());
               if (attr != null && !attr.hasModifier((AttributeModifier)entry.getValue())) {
                  attr.applyModifier((AttributeModifier)entry.getValue());
               }
            }

            return true;
         } else {
            this.deactivate(entity);
            return false;
         }
      }

      public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
         if (!world.isRemote && this.isActivated(itemstack)) {
            int ticks = entity.getEntityData().getInteger("SenninkaStartTime");
            ProcedureSync.EntityNBTTag.setAndSync(entity, "SenninkaStartTime", ticks + 1);
            if (entity instanceof EntityLivingBase && ticks > (int)(((RangedItem)itemstack.getItem()).getXpRatio(itemstack, ItemSenninka.PISTONFIST) * 300.0F)) {
               this.deactivate((EntityLivingBase)entity);
            }
         }

      }

      public void onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
         if (attacker.equals(target)) {
            target = ProcedureUtils.objectEntityLookingAt(attacker, 16.0D, 3.0D).entityHit;
            if (target instanceof EntityLivingBase) {
               attacker.attackTargetEntityWithCurrentItem(target);
            }
         } else if (target instanceof EntityLivingBase) {
            target.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.NEUTRAL, 1.0F, attacker.getRNG().nextFloat() * 0.5F + 0.5F);
            Vec3d vec = target.getPositionVector().subtract(attacker.getPositionVector()).normalize();
            Particles.Renderer particles = new Particles.Renderer(attacker.world);
            int i = 1;

            for(byte j = 25; i <= j; ++i) {
               Vec3d vec1 = vec.scale(-0.06D * (double)i);
               particles.spawnParticles(Particles.Types.SONIC_BOOM, attacker.posX, attacker.posY + 1.4D, attacker.posZ, 1, 0.0D, 0.0D, 0.0D, vec1.x, vec1.y, vec1.z, 16777215 | (int)((1.0F - (float)i / (float)j) * 64.0F) << 24, i * 2, (int)(5.0F * (1.0F + (float)i / (float)j * 0.5F)));
            }

            particles.send();
            attacker.rotationYaw = ProcedureUtils.getYawFromVec(vec);
            attacker.rotationPitch = ProcedureUtils.getPitchFromVec(vec);
            attacker.setPositionAndUpdate(target.posX - vec.x, target.posY - vec.y + 0.5D, target.posZ - vec.z);
         }

      }

      @SideOnly(Side.CLIENT)
      public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
         if (this.isActivated(stack)) {
            model.setVisible(false);
            model.bipedHead.showModel = true;
            model.headStage1.showModel = true;
            model.bipedRightArm.showModel = true;
            model.armExhaust.showModel = true;
            model.rightArmSpikes.rotateAngleX = 0.0F;
            model.bipedBody.showModel = true;
            model.bodyStage1.showModel = true;
            model.isSneak = living.isSneaking();
            model.isRiding = living.isRiding();
            model.isChild = living.isChild();
            if (ItemJutsu.getCurrentJutsu(stack) == ItemSenninka.ABSORB && living.isHandActive()) {
               ((Absorption)ItemSenninka.ABSORB.jutsu).showNeedle(living, model);
            }

            return true;
         } else {
            return false;
         }
      }

      public boolean isActivated(ItemStack stack) {
         boolean var2;
         if (stack.hasTagCompound()) {
            NBTTagCompound var10000 = stack.getTagCompound();
            this.getClass();
            if (var10000.getBoolean("PistonFistStackKey")) {
               var2 = true;
               return var2;
            }
         }

         var2 = false;
         return var2;
      }

      public void deactivate(EntityLivingBase entity) {
         Iterator var2 = this.buffMap.entrySet().iterator();

         while(var2.hasNext()) {
            Entry<IAttribute, AttributeModifier> entry = (Entry)var2.next();
            IAttributeInstance attr = entity.getEntityAttribute((IAttribute)entry.getKey());
            if (attr != null && attr.hasModifier((AttributeModifier)entry.getValue())) {
               attr.removeModifier((AttributeModifier)entry.getValue());
            }
         }

         ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, ItemSenninka.block);
         if (stack != null) {
            NBTTagCompound var10000 = stack.getTagCompound();
            this.getClass();
            var10000.removeTag("PistonFistStackKey");
            ItemJutsu.setJutsuCooldown(stack, entity, ItemSenninka.PISTONFIST, 300L);
         }

         ProcedureSync.EntityNBTTag.removeAndSync(entity, "SenninkaStartTime");
      }
   }

   public static class Broadaxe extends SenninkaJutsu {
      public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
         if (entity instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemSenninkaBroadaxe.block)) {
            entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, (SoundEvent)SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 1.0F, 0.8F);
            ItemStack itemstack = new ItemStack(ItemSenninkaBroadaxe.block);
            ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.MAINHAND, itemstack);
            return true;
         } else {
            return false;
         }
      }

      public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
         if (!world.isRemote && entity instanceof EntityLivingBase && !this.anyOtherActivated((EntityLivingBase)entity, stack)) {
            if (this.isActivated((EntityLivingBase)entity, stack)) {
               ProcedureSync.EntityNBTTag.setAndSync(entity, "SenninkaStartTime", entity.getEntityData().getInteger("SenninkaStartTime") + 1);
            } else if (entity.getEntityData().hasKey("SenninkaStartTime")) {
               ProcedureSync.EntityNBTTag.removeAndSync(entity, "SenninkaStartTime");
            }
         }

      }

      public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
         return entity.getHeldItemMainhand().getItem() == ItemSenninkaBroadaxe.block;
      }

      public void deactivate(EntityLivingBase entity) {
         if (!entity.world.isRemote && entity instanceof EntityPlayer) {
            ((EntityPlayer)entity).inventory.clearMatchingItems(ItemSenninkaBroadaxe.block, -1, -1, (NBTTagCompound)null);
         }

      }

      @SideOnly(Side.CLIENT)
      public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
         if (living.getHeldItemMainhand().getItem() == ItemSenninkaBroadaxe.block && !ItemSenninka.PISTONFIST.jutsu.isActivated(stack) && !ItemSenninka.STAGE2.jutsu.isActivated(stack)) {
            model.setVisible(false);
            model.bipedHead.showModel = true;
            model.headStage1.showModel = true;
            model.bipedRightArm.showModel = true;
            model.rightArmSpikes.rotateAngleX = 3.1416F;
            model.bipedBody.showModel = true;
            model.bodyStage1.showModel = true;
            model.isSneak = living.isSneaking();
            model.isRiding = living.isRiding();
            model.isChild = living.isChild();
            return true;
         } else {
            return false;
         }
      }
   }

   public abstract static class SenninkaJutsu implements ItemJutsu.IJutsuCallback {
      private static final List<SenninkaJutsu> list = Lists.newArrayList();

      public SenninkaJutsu() {
         list.add(this);
      }

      public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
      }

      public void onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
      }

      @SideOnly(Side.CLIENT)
      public abstract boolean setModelVisibility(EntityLivingBase var1, ItemStack var2, Renderer.ModelJugo var3);

      public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
         return this.isActivated(stack);
      }

      public boolean anyOtherActivated(EntityLivingBase entity, ItemStack stack) {
         Iterator var3 = list.iterator();

         SenninkaJutsu jutsu;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            jutsu = (SenninkaJutsu)var3.next();
         } while(jutsu == this || !jutsu.isActivated(entity, stack));

         return true;
      }

      public static void deactivateAll(EntityLivingBase entity) {
         ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, ItemSenninka.block);
         if (stack != null) {
            Iterator var2 = list.iterator();

            while(var2.hasNext()) {
               SenninkaJutsu jutsu = (SenninkaJutsu)var2.next();
               if (jutsu.isActivated(entity, stack)) {
                  jutsu.deactivate(entity);
               }
            }
         }

      }
   }

   public static class RangedItem extends ItemJutsu.Base implements ItemOnBody.Interface {
      @SideOnly(Side.CLIENT)
      private ModelBiped armorModel;

      public RangedItem(ItemJutsu.JutsuEnum... list) {
         super(ItemJutsu.JutsuEnum.Type.SENNINKA, list);
         this.setUnlocalizedName("senninka");
         this.setRegistryName("senninka");
         this.setCreativeTab(TabModTab.tab);
         this.defaultCooldownMap[ItemSenninka.BROADAXE.index] = 0L;
         this.defaultCooldownMap[ItemSenninka.PISTONFIST.index] = 0L;
         this.defaultCooldownMap[ItemSenninka.STAGE2.index] = 0L;
         this.defaultCooldownMap[ItemSenninka.ABSORB.index] = 0L;
      }

      public boolean executeJutsu(ItemStack stack, EntityLivingBase entity, float power) {
         if (ItemSenjutsu.isSageModeActivated(entity)) {
            ItemSenjutsu.deactivateSageMode(entity);
         }

         return super.executeJutsu(stack, entity, power);
      }

      public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
         super.onUpdate(itemstack, world, entity, par4, par5);
         if (entity instanceof EntityLivingBase) {
            Iterator var6 = Lists.newArrayList(this.getCompatibleJutsus()).iterator();

            while(var6.hasNext()) {
               ItemJutsu.JutsuEnum jutsuEnum = (ItemJutsu.JutsuEnum)var6.next();
               if (this.canUseJutsu(itemstack, jutsuEnum, (EntityLivingBase)entity) && jutsuEnum.jutsu instanceof SenninkaJutsu) {
                  ((SenninkaJutsu)jutsuEnum.jutsu).onUpdate(itemstack, world, entity, par4, par5);
               }
            }
         }

      }

      public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
         Iterator var4 = this.getActivatedJutsus(itemstack).iterator();

         while(var4.hasNext()) {
            ItemJutsu.JutsuEnum jutsuEnum = (ItemJutsu.JutsuEnum)var4.next();
            if (jutsuEnum.jutsu instanceof SenninkaJutsu) {
               ((SenninkaJutsu)jutsuEnum.jutsu).onLeftClickEntity(itemstack, attacker, target);
            }
         }

         return super.onLeftClickEntity(itemstack, attacker, target);
      }

      @SideOnly(Side.CLIENT)
      public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
         if (this.armorModel == null) {
            this.armorModel = new Renderer.ModelJugo();
         }

         Iterator var5 = Lists.newArrayList(this.getCompatibleJutsus()).iterator();

         ItemJutsu.JutsuEnum jutsuEnum;
         do {
            if (!var5.hasNext()) {
               return null;
            }

            jutsuEnum = (ItemJutsu.JutsuEnum)var5.next();
         } while(!(jutsuEnum.jutsu instanceof SenninkaJutsu) || !((SenninkaJutsu)jutsuEnum.jutsu).setModelVisibility(living, stack, (Renderer.ModelJugo)this.armorModel));

         return this.armorModel;
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
         return "narutomod:textures/jugo.png";
      }

      public boolean showSkinLayer() {
         return true;
      }

      public ItemOnBody.BodyPart showOnBody() {
         return ItemOnBody.BodyPart.NONE;
      }
   }
}
