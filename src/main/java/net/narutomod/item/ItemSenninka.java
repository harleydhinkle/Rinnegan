/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.model.ModelBox
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.client.event.ModelRegistryEvent
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.client.registry.RenderingRegistry
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.registry.EntityEntryBuilder
 *  net.minecraftforge.fml.common.registry.GameRegistry$ObjectHolder
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package net.narutomod.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
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
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.Particles;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.entity.EntityBeamBase;
import net.narutomod.entity.EntityRendererRegister;
import net.narutomod.entity.EntitySenninkaClone;
import net.narutomod.item.ItemSenninkaBroadaxe;
import net.narutomod.procedure.ProcedureAirPunch;
import net.narutomod.procedure.ProcedureOnLeftClickEmpty;
import net.narutomod.procedure.ProcedureSync;
import net.narutomod.procedure.ProcedureUtils;

@ElementsNarutomodMod.ModElement.Tag
public class ItemSenninka
extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder(value="narutomod:senninka")
    public static final Item block = null;
    public static final int ENTITYID = 524;
    private static final String START_TIME = "SenninkaStartTime";
    public static final ItemJutsu.JutsuEnum BROADAXE = new ItemJutsu.JutsuEnum(0, "item.senninka_broadaxe.name", 'S', 50, 50.0, new Broadaxe());
    public static final ItemJutsu.JutsuEnum PISTONFIST = new ItemJutsu.JutsuEnum(1, "item.senninka.pistonfist", 'S', 50, 50.0, new PistonFist());
    public static final ItemJutsu.JutsuEnum STAGE2 = new ItemJutsu.JutsuEnum(2, "item.senninka.stage2", 'S', 100, 50.0, new Stage2());
    public static final ItemJutsu.JutsuEnum CANNON = new ItemJutsu.JutsuEnum(3, "entitysennikacannon", 'S', 50, 100.0, new EntityMultiCannon.Jutsu());
    public static final ItemJutsu.JutsuEnum ABSORB = new ItemJutsu.JutsuEnum(4, "item.senninka.absorb", 'S', 50, 50.0, new Absorption());

    public ItemSenninka(ElementsNarutomodMod instance) {
        super(instance, 939);
    }

    @Override
    public void initElements() {
        this.elements.items.add(() -> new RangedItem(BROADAXE, PISTONFIST, STAGE2, CANNON, ABSORB));
        this.elements.entities.add(() -> EntityEntryBuilder.create().entity(EntityMultiCannon.class).id(new ResourceLocation("narutomod", "entitysennikacannon"), 524).name("entitysennikacannon").tracker(64, 1, true).build());
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation((Item)block, (int)0, (ModelResourceLocation)new ModelResourceLocation("narutomod:senninka", "inventory"));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ProcedureOnLeftClickEmpty.addQualifiedItem(block, EnumHand.MAIN_HAND);
    }

    public static int getActivationTicks(Entity entity) {
        return entity.getEntityData().func_74762_e(START_TIME);
    }

    public static void setActivationTicks(Entity entity, int ticks) {
        ProcedureSync.EntityNBTTag.setAndSync(entity, START_TIME, ticks);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        new Renderer().register();
    }

    public static class Renderer
    extends EntityRendererRegister {
        @Override
        @SideOnly(value=Side.CLIENT)
        public void register() {
            RenderingRegistry.registerEntityRenderingHandler(EntityMultiCannon.class, renderManager -> new CustomRender(renderManager));
        }

        @SideOnly(value=Side.CLIENT)
        public static class ModelJugo
        extends ModelBiped {
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
                this.field_78090_t = 64;
                this.field_78089_u = 96;
                this.field_78116_c = new ModelRenderer((ModelBase)this);
                this.field_78116_c.func_78793_a(0.0f, 0.0f, 0.0f);
                this.headStage0 = new ModelRenderer((ModelBase)this);
                this.headStage0.func_78793_a(0.0f, 0.0f, 0.0f);
                this.field_78116_c.func_78792_a(this.headStage0);
                this.headStage0.field_78804_l.add(new ModelBox(this.headStage0, 32, 0, -4.0f, -8.0f, -4.0f, 8, 8, 8, 0.0f, false));
                this.hair = new ModelRenderer((ModelBase)this);
                this.hair.func_78793_a(0.0f, -1.0f, 0.0f);
                this.headStage0.func_78792_a(this.hair);
                this.bone1 = new ModelRenderer((ModelBase)this);
                this.bone1.func_78793_a(-2.0f, -5.25f, 0.0f);
                this.hair.func_78792_a(this.bone1);
                this.setRotationAngle(this.bone1, -0.1745f, 0.0f, -0.5236f);
                this.bone1.field_78804_l.add(new ModelBox(this.bone1, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone7 = new ModelRenderer((ModelBase)this);
                this.bone7.func_78793_a(2.0f, -5.25f, 0.0f);
                this.hair.func_78792_a(this.bone7);
                this.setRotationAngle(this.bone7, -0.1745f, 0.0f, 0.5236f);
                this.bone7.field_78804_l.add(new ModelBox(this.bone7, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone9 = new ModelRenderer((ModelBase)this);
                this.bone9.func_78793_a(-2.25f, -4.25f, 0.0f);
                this.hair.func_78792_a(this.bone9);
                this.setRotationAngle(this.bone9, -0.5236f, 0.5236f, -1.0472f);
                this.bone9.field_78804_l.add(new ModelBox(this.bone9, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone15 = new ModelRenderer((ModelBase)this);
                this.bone15.func_78793_a(2.25f, -4.25f, 0.0f);
                this.hair.func_78792_a(this.bone15);
                this.setRotationAngle(this.bone15, -0.5236f, -0.5236f, 1.0472f);
                this.bone15.field_78804_l.add(new ModelBox(this.bone15, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone2 = new ModelRenderer((ModelBase)this);
                this.bone2.func_78793_a(-2.0f, -5.0f, -2.0f);
                this.hair.func_78792_a(this.bone2);
                this.setRotationAngle(this.bone2, 0.1745f, 0.0f, -0.3491f);
                this.bone2.field_78804_l.add(new ModelBox(this.bone2, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone6 = new ModelRenderer((ModelBase)this);
                this.bone6.func_78793_a(2.0f, -5.0f, -2.0f);
                this.hair.func_78792_a(this.bone6);
                this.setRotationAngle(this.bone6, 0.1745f, 0.0f, 0.3491f);
                this.bone6.field_78804_l.add(new ModelBox(this.bone6, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone3 = new ModelRenderer((ModelBase)this);
                this.bone3.func_78793_a(-2.0f, -4.5f, 2.0f);
                this.hair.func_78792_a(this.bone3);
                this.setRotationAngle(this.bone3, -0.5236f, 0.0f, -0.3491f);
                this.bone3.field_78804_l.add(new ModelBox(this.bone3, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone8 = new ModelRenderer((ModelBase)this);
                this.bone8.func_78793_a(2.0f, -4.5f, 2.0f);
                this.hair.func_78792_a(this.bone8);
                this.setRotationAngle(this.bone8, -0.5236f, 0.0f, 0.3491f);
                this.bone8.field_78804_l.add(new ModelBox(this.bone8, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone11 = new ModelRenderer((ModelBase)this);
                this.bone11.func_78793_a(-2.0f, -3.5f, 2.0f);
                this.hair.func_78792_a(this.bone11);
                this.setRotationAngle(this.bone11, -1.309f, 0.0f, -0.3491f);
                this.bone11.field_78804_l.add(new ModelBox(this.bone11, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone18 = new ModelRenderer((ModelBase)this);
                this.bone18.func_78793_a(2.0f, -3.5f, 2.0f);
                this.hair.func_78792_a(this.bone18);
                this.setRotationAngle(this.bone18, -1.309f, 0.0f, 0.3491f);
                this.bone18.field_78804_l.add(new ModelBox(this.bone18, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone12 = new ModelRenderer((ModelBase)this);
                this.bone12.func_78793_a(-3.0f, -2.5f, 1.0f);
                this.hair.func_78792_a(this.bone12);
                this.setRotationAngle(this.bone12, -2.0944f, 0.0f, -0.3491f);
                this.bone12.field_78804_l.add(new ModelBox(this.bone12, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone17 = new ModelRenderer((ModelBase)this);
                this.bone17.func_78793_a(3.0f, -2.5f, 1.0f);
                this.hair.func_78792_a(this.bone17);
                this.setRotationAngle(this.bone17, -2.0944f, 0.0f, 0.3491f);
                this.bone17.field_78804_l.add(new ModelBox(this.bone17, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone10 = new ModelRenderer((ModelBase)this);
                this.bone10.func_78793_a(-2.0f, -3.5f, 1.0f);
                this.hair.func_78792_a(this.bone10);
                this.setRotationAngle(this.bone10, -0.9599f, -0.0873f, -1.309f);
                this.bone10.field_78804_l.add(new ModelBox(this.bone10, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone16 = new ModelRenderer((ModelBase)this);
                this.bone16.func_78793_a(2.0f, -3.5f, 1.0f);
                this.hair.func_78792_a(this.bone16);
                this.setRotationAngle(this.bone16, -0.9599f, 0.0873f, 1.309f);
                this.bone16.field_78804_l.add(new ModelBox(this.bone16, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone4 = new ModelRenderer((ModelBase)this);
                this.bone4.func_78793_a(0.0f, -5.0f, -2.0f);
                this.hair.func_78792_a(this.bone4);
                this.setRotationAngle(this.bone4, 0.5236f, 0.0f, 0.0f);
                this.bone4.field_78804_l.add(new ModelBox(this.bone4, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone13 = new ModelRenderer((ModelBase)this);
                this.bone13.func_78793_a(0.0f, -6.0f, -2.0f);
                this.hair.func_78792_a(this.bone13);
                this.setRotationAngle(this.bone13, 0.0873f, 0.0f, 0.0f);
                this.bone13.field_78804_l.add(new ModelBox(this.bone13, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, true));
                this.bone14 = new ModelRenderer((ModelBase)this);
                this.bone14.func_78793_a(0.0f, -6.0f, 0.0f);
                this.hair.func_78792_a(this.bone14);
                this.setRotationAngle(this.bone14, -0.2618f, 0.0f, 0.0f);
                this.bone14.field_78804_l.add(new ModelBox(this.bone14, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.bone5 = new ModelRenderer((ModelBase)this);
                this.bone5.func_78793_a(0.0f, -5.0f, 2.0f);
                this.hair.func_78792_a(this.bone5);
                this.setRotationAngle(this.bone5, -0.7854f, 0.0f, 0.0f);
                this.bone5.field_78804_l.add(new ModelBox(this.bone5, 24, 0, -2.0f, -4.0f, -2.0f, 4, 4, 4, -0.1f, false));
                this.headStage1 = new ModelRenderer((ModelBase)this);
                this.headStage1.func_78793_a(0.0f, 0.0f, 0.0f);
                this.field_78116_c.func_78792_a(this.headStage1);
                this.headStage1.field_78804_l.add(new ModelBox(this.headStage1, 0, 64, -8.0f, -12.0f, -8.0f, 16, 16, 16, -3.95f, false));
                this.headStage2 = new ModelRenderer((ModelBase)this);
                this.headStage2.func_78793_a(0.0f, 0.0f, 0.0f);
                this.field_78116_c.func_78792_a(this.headStage2);
                this.headStage2.field_78804_l.add(new ModelBox(this.headStage2, 0, 0, -4.0f, -8.0f, -4.0f, 8, 8, 8, 0.1f, false));
                this.eyeRight = new ModelRenderer((ModelBase)this);
                this.eyeRight.func_78793_a(0.5f, 0.3f, -2.25f);
                this.headStage2.func_78792_a(this.eyeRight);
                this.eyeRight.field_78804_l.add(new ModelBox(this.eyeRight, 0, 55, -6.0f, -6.95f, -5.05f, 7, 7, 0, -3.15f, false));
                this.eyeLeft = new ModelRenderer((ModelBase)this);
                this.eyeLeft.func_78793_a(-0.5f, 0.3f, -2.25f);
                this.headStage2.func_78792_a(this.eyeLeft);
                this.eyeLeft.field_78804_l.add(new ModelBox(this.eyeLeft, 0, 55, -1.0f, -6.95f, -5.05f, 7, 7, 0, -3.15f, true));
                this.field_178720_f = new ModelRenderer((ModelBase)this);
                this.field_178720_f.func_78793_a(0.0f, 0.0f, 0.0f);
                this.field_178720_f.field_78804_l.add(new ModelBox(this.field_178720_f, 16, 40, -4.0f, -8.0f, -4.0f, 8, 8, 1, 0.4f, false));
                this.field_78115_e = new ModelRenderer((ModelBase)this);
                this.field_78115_e.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bodyStage1 = new ModelRenderer((ModelBase)this);
                this.bodyStage1.func_78793_a(0.0f, 0.0f, 0.0f);
                this.field_78115_e.func_78792_a(this.bodyStage1);
                this.bodyStage1.field_78804_l.add(new ModelBox(this.bodyStage1, 40, 42, -4.0f, 0.0f, -2.0f, 8, 12, 4, 0.05f, false));
                this.bodyStage2 = new ModelRenderer((ModelBase)this);
                this.bodyStage2.func_78793_a(0.0f, 0.0f, 0.0f);
                this.field_78115_e.func_78792_a(this.bodyStage2);
                this.bodyStage2.field_78804_l.add(new ModelBox(this.bodyStage2, 16, 16, -4.0f, 0.0f, -2.0f, 8, 12, 4, 0.11f, false));
                this.bodyStage2.field_78804_l.add(new ModelBox(this.bodyStage2, 16, 32, -4.0f, 0.0f, -2.0f, 8, 4, 4, 0.34f, false));
                this.exhaust1 = new ModelRenderer((ModelBase)this);
                this.exhaust1.func_78793_a(-2.0f, 3.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust1);
                this.setRotationAngle(this.exhaust1, -1.0472f, -0.7854f, 0.0f);
                this.exhaust1.field_78804_l.add(new ModelBox(this.exhaust1, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, true));
                this.bone33 = new ModelRenderer((ModelBase)this);
                this.bone33.func_78793_a(2.0f, -5.9f, 2.1f);
                this.exhaust1.func_78792_a(this.bone33);
                this.setRotationAngle(this.bone33, 0.5236f, 0.0f, 0.0f);
                this.bone33.field_78804_l.add(new ModelBox(this.bone33, 0, 43, -4.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, true));
                this.bone47 = new ModelRenderer((ModelBase)this);
                this.bone47.func_78793_a(0.1f, -3.8f, 0.2f);
                this.bone33.func_78792_a(this.bone47);
                this.setRotationAngle(this.bone47, 0.3491f, 0.0f, 0.0f);
                this.bone47.field_78804_l.add(new ModelBox(this.bone47, 0, 43, -4.0f, -4.0f, -4.0f, 4, 4, 4, -0.2f, true));
                this.exhaustExtension1 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension1.func_78793_a(-2.0f, -3.3f, 0.0f);
                this.bone47.func_78792_a(this.exhaustExtension1);
                this.setRotationAngle(this.exhaustExtension1, 0.3491f, 0.1745f, -0.3491f);
                this.exhaustExtension1.field_78804_l.add(new ModelBox(this.exhaustExtension1, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, true));
                this.bone67 = new ModelRenderer((ModelBase)this);
                this.bone67.func_78793_a(0.0f, -3.25f, 0.0f);
                this.exhaustExtension1.func_78792_a(this.bone67);
                this.setRotationAngle(this.bone67, 0.3491f, 0.0873f, -0.2618f);
                this.bone67.field_78804_l.add(new ModelBox(this.bone67, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, true));
                this.bone69 = new ModelRenderer((ModelBase)this);
                this.bone69.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone67.func_78792_a(this.bone69);
                this.setRotationAngle(this.bone69, 0.3491f, 0.0f, -0.2618f);
                this.bone69.field_78804_l.add(new ModelBox(this.bone69, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, true));
                this.bone70 = new ModelRenderer((ModelBase)this);
                this.bone70.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone69.func_78792_a(this.bone70);
                this.setRotationAngle(this.bone70, 0.3491f, -0.0873f, -0.2618f);
                this.bone70.field_78804_l.add(new ModelBox(this.bone70, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, true));
                this.bone71 = new ModelRenderer((ModelBase)this);
                this.bone71.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone70.func_78792_a(this.bone71);
                this.setRotationAngle(this.bone71, 0.1745f, 0.0f, -0.1745f);
                this.bone71.field_78804_l.add(new ModelBox(this.bone71, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, true));
                this.ball1 = new ModelRenderer((ModelBase)this);
                this.ball1.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone71.func_78792_a(this.ball1);
                this.exhaust2 = new ModelRenderer((ModelBase)this);
                this.exhaust2.func_78793_a(2.0f, 3.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust2);
                this.setRotationAngle(this.exhaust2, -1.0472f, 0.7854f, 0.0f);
                this.exhaust2.field_78804_l.add(new ModelBox(this.exhaust2, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, false));
                this.bone30 = new ModelRenderer((ModelBase)this);
                this.bone30.func_78793_a(-2.0f, -5.9f, 2.1f);
                this.exhaust2.func_78792_a(this.bone30);
                this.setRotationAngle(this.bone30, 0.5236f, 0.0f, 0.0f);
                this.bone30.field_78804_l.add(new ModelBox(this.bone30, 0, 43, 0.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, false));
                this.bone31 = new ModelRenderer((ModelBase)this);
                this.bone31.func_78793_a(-0.1f, -3.8f, 0.2f);
                this.bone30.func_78792_a(this.bone31);
                this.setRotationAngle(this.bone31, 0.3491f, 0.0f, 0.0f);
                this.bone31.field_78804_l.add(new ModelBox(this.bone31, 0, 43, 0.0f, -4.0f, -4.0f, 4, 4, 4, -0.2f, false));
                this.exhaustExtension2 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension2.func_78793_a(2.0f, -3.3f, 0.0f);
                this.bone31.func_78792_a(this.exhaustExtension2);
                this.setRotationAngle(this.exhaustExtension2, 0.3491f, -0.1745f, 0.3491f);
                this.exhaustExtension2.field_78804_l.add(new ModelBox(this.exhaustExtension2, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, false));
                this.bone99 = new ModelRenderer((ModelBase)this);
                this.bone99.func_78793_a(0.0f, -3.25f, 0.0f);
                this.exhaustExtension2.func_78792_a(this.bone99);
                this.setRotationAngle(this.bone99, 0.3491f, -0.0873f, 0.2618f);
                this.bone99.field_78804_l.add(new ModelBox(this.bone99, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, false));
                this.bone100 = new ModelRenderer((ModelBase)this);
                this.bone100.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone99.func_78792_a(this.bone100);
                this.setRotationAngle(this.bone100, 0.3491f, 0.0f, 0.2618f);
                this.bone100.field_78804_l.add(new ModelBox(this.bone100, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, false));
                this.bone101 = new ModelRenderer((ModelBase)this);
                this.bone101.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone100.func_78792_a(this.bone101);
                this.setRotationAngle(this.bone101, 0.3491f, 0.0873f, 0.2618f);
                this.bone101.field_78804_l.add(new ModelBox(this.bone101, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, false));
                this.bone102 = new ModelRenderer((ModelBase)this);
                this.bone102.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone101.func_78792_a(this.bone102);
                this.setRotationAngle(this.bone102, 0.1745f, 0.0f, 0.1745f);
                this.bone102.field_78804_l.add(new ModelBox(this.bone102, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, false));
                this.ball2 = new ModelRenderer((ModelBase)this);
                this.ball2.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone102.func_78792_a(this.ball2);
                this.exhaust3 = new ModelRenderer((ModelBase)this);
                this.exhaust3.func_78793_a(-2.0f, 3.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust3);
                this.setRotationAngle(this.exhaust3, -1.9199f, -0.6109f, 0.0f);
                this.exhaust3.field_78804_l.add(new ModelBox(this.exhaust3, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, true));
                this.bone74 = new ModelRenderer((ModelBase)this);
                this.bone74.func_78793_a(2.0f, -5.9f, 2.1f);
                this.exhaust3.func_78792_a(this.bone74);
                this.setRotationAngle(this.bone74, 0.5236f, 0.0f, -0.2618f);
                this.bone74.field_78804_l.add(new ModelBox(this.bone74, 0, 43, -4.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, true));
                this.bone75 = new ModelRenderer((ModelBase)this);
                this.bone75.func_78793_a(0.1f, -3.8f, 0.2f);
                this.bone74.func_78792_a(this.bone75);
                this.setRotationAngle(this.bone75, 0.3478f, 0.0298f, -0.3438f);
                this.bone75.field_78804_l.add(new ModelBox(this.bone75, 0, 43, -4.0f, -4.0f, -4.0f, 4, 4, 4, -0.2f, true));
                this.exhaustExtension3 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension3.func_78793_a(-2.0f, -3.3f, 0.0f);
                this.bone75.func_78792_a(this.exhaustExtension3);
                this.setRotationAngle(this.exhaustExtension3, 0.1745f, 0.1745f, -0.2618f);
                this.exhaustExtension3.field_78804_l.add(new ModelBox(this.exhaustExtension3, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, true));
                this.bone77 = new ModelRenderer((ModelBase)this);
                this.bone77.func_78793_a(0.0f, -3.25f, 0.0f);
                this.exhaustExtension3.func_78792_a(this.bone77);
                this.setRotationAngle(this.bone77, 0.2618f, 0.0873f, -0.2618f);
                this.bone77.field_78804_l.add(new ModelBox(this.bone77, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, true));
                this.bone78 = new ModelRenderer((ModelBase)this);
                this.bone78.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone77.func_78792_a(this.bone78);
                this.setRotationAngle(this.bone78, 0.2641f, -0.0183f, -0.3979f);
                this.bone78.field_78804_l.add(new ModelBox(this.bone78, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, true));
                this.bone79 = new ModelRenderer((ModelBase)this);
                this.bone79.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone78.func_78792_a(this.bone79);
                this.setRotationAngle(this.bone79, 0.1742f, 0.0076f, -0.6105f);
                this.bone79.field_78804_l.add(new ModelBox(this.bone79, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, true));
                this.bone80 = new ModelRenderer((ModelBase)this);
                this.bone80.func_78793_a(0.0f, -4.5f, 0.0f);
                this.bone79.func_78792_a(this.bone80);
                this.setRotationAngle(this.bone80, 0.1743f, 0.0113f, -0.3039f);
                this.bone80.field_78804_l.add(new ModelBox(this.bone80, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, true));
                this.ball3 = new ModelRenderer((ModelBase)this);
                this.ball3.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone80.func_78792_a(this.ball3);
                this.exhaust4 = new ModelRenderer((ModelBase)this);
                this.exhaust4.func_78793_a(2.0f, 3.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust4);
                this.setRotationAngle(this.exhaust4, -1.9199f, 0.6109f, 0.0f);
                this.exhaust4.field_78804_l.add(new ModelBox(this.exhaust4, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, false));
                this.bone84 = new ModelRenderer((ModelBase)this);
                this.bone84.func_78793_a(-2.0f, -5.9f, 2.1f);
                this.exhaust4.func_78792_a(this.bone84);
                this.setRotationAngle(this.bone84, 0.5236f, 0.0f, 0.2618f);
                this.bone84.field_78804_l.add(new ModelBox(this.bone84, 0, 43, 0.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, false));
                this.bone90 = new ModelRenderer((ModelBase)this);
                this.bone90.func_78793_a(-0.1f, -3.8f, 0.2f);
                this.bone84.func_78792_a(this.bone90);
                this.setRotationAngle(this.bone90, 0.3478f, -0.0298f, 0.3438f);
                this.bone90.field_78804_l.add(new ModelBox(this.bone90, 0, 43, 0.0f, -4.0f, -4.0f, 4, 4, 4, -0.2f, false));
                this.exhaustExtension4 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension4.func_78793_a(2.0f, -3.3f, 0.0f);
                this.bone90.func_78792_a(this.exhaustExtension4);
                this.setRotationAngle(this.exhaustExtension4, 0.1745f, -0.1745f, 0.2618f);
                this.exhaustExtension4.field_78804_l.add(new ModelBox(this.exhaustExtension4, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, false));
                this.bone93 = new ModelRenderer((ModelBase)this);
                this.bone93.func_78793_a(0.0f, -3.25f, 0.0f);
                this.exhaustExtension4.func_78792_a(this.bone93);
                this.setRotationAngle(this.bone93, 0.2618f, -0.0873f, 0.2618f);
                this.bone93.field_78804_l.add(new ModelBox(this.bone93, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, false));
                this.bone98 = new ModelRenderer((ModelBase)this);
                this.bone98.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone93.func_78792_a(this.bone98);
                this.setRotationAngle(this.bone98, 0.2641f, 0.0183f, 0.3979f);
                this.bone98.field_78804_l.add(new ModelBox(this.bone98, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, false));
                this.bone103 = new ModelRenderer((ModelBase)this);
                this.bone103.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone98.func_78792_a(this.bone103);
                this.setRotationAngle(this.bone103, 0.1742f, -0.0076f, 0.6105f);
                this.bone103.field_78804_l.add(new ModelBox(this.bone103, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, false));
                this.bone104 = new ModelRenderer((ModelBase)this);
                this.bone104.func_78793_a(0.0f, -4.5f, 0.0f);
                this.bone103.func_78792_a(this.bone104);
                this.setRotationAngle(this.bone104, 0.1743f, -0.0113f, 0.3039f);
                this.bone104.field_78804_l.add(new ModelBox(this.bone104, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, false));
                this.ball4 = new ModelRenderer((ModelBase)this);
                this.ball4.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone104.func_78792_a(this.ball4);
                this.exhaust5 = new ModelRenderer((ModelBase)this);
                this.exhaust5.func_78793_a(-2.0f, 3.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust5);
                this.setRotationAngle(this.exhaust5, -2.618f, -0.2618f, 0.0f);
                this.exhaust5.field_78804_l.add(new ModelBox(this.exhaust5, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, true));
                this.bone82 = new ModelRenderer((ModelBase)this);
                this.bone82.func_78793_a(2.25f, -5.9f, 2.1f);
                this.exhaust5.func_78792_a(this.bone82);
                this.setRotationAngle(this.bone82, 0.5087f, 0.1298f, -0.228f);
                this.bone82.field_78804_l.add(new ModelBox(this.bone82, 0, 43, -4.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, true));
                this.bone83 = new ModelRenderer((ModelBase)this);
                this.bone83.func_78793_a(-2.0f, -3.55f, -2.0f);
                this.bone82.func_78792_a(this.bone83);
                this.setRotationAngle(this.bone83, 0.3491f, 0.1658f, -0.1658f);
                this.bone83.field_78804_l.add(new ModelBox(this.bone83, 0, 43, -2.0f, -3.25f, -2.0f, 4, 4, 4, -0.2f, true));
                this.exhaustExtension5 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension5.func_78793_a(0.0f, -2.55f, 2.0f);
                this.bone83.func_78792_a(this.exhaustExtension5);
                this.setRotationAngle(this.exhaustExtension5, 0.3245f, 0.2178f, -0.475f);
                this.exhaustExtension5.field_78804_l.add(new ModelBox(this.exhaustExtension5, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, true));
                this.bone85 = new ModelRenderer((ModelBase)this);
                this.bone85.func_78793_a(0.0f, -3.25f, 0.0f);
                this.exhaustExtension5.func_78792_a(this.bone85);
                this.setRotationAngle(this.bone85, 0.2126f, 0.1339f, -0.2903f);
                this.bone85.field_78804_l.add(new ModelBox(this.bone85, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, true));
                this.bone87 = new ModelRenderer((ModelBase)this);
                this.bone87.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone85.func_78792_a(this.bone87);
                this.setRotationAngle(this.bone87, 0.0805f, 0.1428f, -0.3239f);
                this.bone87.field_78804_l.add(new ModelBox(this.bone87, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, true));
                this.bone88 = new ModelRenderer((ModelBase)this);
                this.bone88.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone87.func_78792_a(this.bone88);
                this.setRotationAngle(this.bone88, 0.1815f, 0.0499f, -0.6301f);
                this.bone88.field_78804_l.add(new ModelBox(this.bone88, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, true));
                this.bone89 = new ModelRenderer((ModelBase)this);
                this.bone89.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone88.func_78792_a(this.bone89);
                this.setRotationAngle(this.bone89, 0.2601f, 0.0887f, -0.4182f);
                this.bone89.field_78804_l.add(new ModelBox(this.bone89, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, true));
                this.bone81 = new ModelRenderer((ModelBase)this);
                this.bone81.func_78793_a(0.0f, -5.0f, 0.0f);
                this.bone89.func_78792_a(this.bone81);
                this.setRotationAngle(this.bone81, 0.1478f, -0.0058f, -0.169f);
                this.bone81.field_78804_l.add(new ModelBox(this.bone81, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, true));
                this.ball5 = new ModelRenderer((ModelBase)this);
                this.ball5.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone81.func_78792_a(this.ball5);
                this.exhaust6 = new ModelRenderer((ModelBase)this);
                this.exhaust6.func_78793_a(2.0f, 3.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust6);
                this.setRotationAngle(this.exhaust6, -2.618f, 0.2618f, 0.0f);
                this.exhaust6.field_78804_l.add(new ModelBox(this.exhaust6, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, false));
                this.bone64 = new ModelRenderer((ModelBase)this);
                this.bone64.func_78793_a(-2.25f, -5.9f, 2.1f);
                this.exhaust6.func_78792_a(this.bone64);
                this.setRotationAngle(this.bone64, 0.5087f, -0.1298f, 0.228f);
                this.bone64.field_78804_l.add(new ModelBox(this.bone64, 0, 43, 0.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, false));
                this.bone65 = new ModelRenderer((ModelBase)this);
                this.bone65.func_78793_a(2.0f, -3.55f, -2.0f);
                this.bone64.func_78792_a(this.bone65);
                this.setRotationAngle(this.bone65, 0.3491f, -0.1658f, 0.1658f);
                this.bone65.field_78804_l.add(new ModelBox(this.bone65, 0, 43, -2.0f, -3.25f, -2.0f, 4, 4, 4, -0.2f, false));
                this.exhaustExtension6 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension6.func_78793_a(0.0f, -2.55f, 2.0f);
                this.bone65.func_78792_a(this.exhaustExtension6);
                this.setRotationAngle(this.exhaustExtension6, 0.3245f, -0.2178f, 0.475f);
                this.exhaustExtension6.field_78804_l.add(new ModelBox(this.exhaustExtension6, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, false));
                this.bone66 = new ModelRenderer((ModelBase)this);
                this.bone66.func_78793_a(0.0f, -3.25f, 0.0f);
                this.exhaustExtension6.func_78792_a(this.bone66);
                this.setRotationAngle(this.bone66, 0.2126f, -0.1339f, 0.2903f);
                this.bone66.field_78804_l.add(new ModelBox(this.bone66, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, false));
                this.bone68 = new ModelRenderer((ModelBase)this);
                this.bone68.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone66.func_78792_a(this.bone68);
                this.setRotationAngle(this.bone68, 0.0805f, -0.1428f, 0.3239f);
                this.bone68.field_78804_l.add(new ModelBox(this.bone68, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, false));
                this.bone73 = new ModelRenderer((ModelBase)this);
                this.bone73.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone68.func_78792_a(this.bone73);
                this.setRotationAngle(this.bone73, 0.1815f, -0.0499f, 0.6301f);
                this.bone73.field_78804_l.add(new ModelBox(this.bone73, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, false));
                this.bone76 = new ModelRenderer((ModelBase)this);
                this.bone76.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone73.func_78792_a(this.bone76);
                this.setRotationAngle(this.bone76, 0.2601f, -0.0887f, 0.4182f);
                this.bone76.field_78804_l.add(new ModelBox(this.bone76, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, false));
                this.bone105 = new ModelRenderer((ModelBase)this);
                this.bone105.func_78793_a(0.0f, -5.0f, 0.0f);
                this.bone76.func_78792_a(this.bone105);
                this.setRotationAngle(this.bone105, 0.1478f, 0.0058f, 0.169f);
                this.bone105.field_78804_l.add(new ModelBox(this.bone105, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, false));
                this.ball6 = new ModelRenderer((ModelBase)this);
                this.ball6.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone105.func_78792_a(this.ball6);
                this.exhaust7 = new ModelRenderer((ModelBase)this);
                this.exhaust7.func_78793_a(-2.0f, 7.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust7);
                this.setRotationAngle(this.exhaust7, -2.618f, -0.2618f, 0.0f);
                this.exhaust7.field_78804_l.add(new ModelBox(this.exhaust7, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, true));
                this.bone91 = new ModelRenderer((ModelBase)this);
                this.bone91.func_78793_a(2.25f, -5.9f, 2.1f);
                this.exhaust7.func_78792_a(this.bone91);
                this.setRotationAngle(this.bone91, 0.5087f, 0.1298f, -0.228f);
                this.bone91.field_78804_l.add(new ModelBox(this.bone91, 0, 43, -4.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, true));
                this.bone92 = new ModelRenderer((ModelBase)this);
                this.bone92.func_78793_a(-1.75f, -3.1f, -1.8f);
                this.bone91.func_78792_a(this.bone92);
                this.setRotationAngle(this.bone92, 0.2443f, 0.1134f, -0.3316f);
                this.bone92.field_78804_l.add(new ModelBox(this.bone92, 0, 43, -2.0f, -3.45f, -2.0f, 4, 4, 4, -0.2f, true));
                this.exhaustExtension7 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension7.func_78793_a(0.25f, -2.5f, 2.0f);
                this.bone92.func_78792_a(this.exhaustExtension7);
                this.setRotationAngle(this.exhaustExtension7, -0.0436f, 0.1745f, -0.3491f);
                this.exhaustExtension7.field_78804_l.add(new ModelBox(this.exhaustExtension7, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, true));
                this.bone94 = new ModelRenderer((ModelBase)this);
                this.bone94.func_78793_a(0.25f, -3.0f, 0.0f);
                this.exhaustExtension7.func_78792_a(this.bone94);
                this.setRotationAngle(this.bone94, -0.0322f, 0.0892f, -0.612f);
                this.bone94.field_78804_l.add(new ModelBox(this.bone94, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, true));
                this.bone95 = new ModelRenderer((ModelBase)this);
                this.bone95.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone94.func_78792_a(this.bone95);
                this.setRotationAngle(this.bone95, -0.0027f, 0.0111f, -0.5668f);
                this.bone95.field_78804_l.add(new ModelBox(this.bone95, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, true));
                this.bone96 = new ModelRenderer((ModelBase)this);
                this.bone96.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone95.func_78792_a(this.bone96);
                this.setRotationAngle(this.bone96, 0.0169f, -0.0701f, -0.4798f);
                this.bone96.field_78804_l.add(new ModelBox(this.bone96, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, true));
                this.bone97 = new ModelRenderer((ModelBase)this);
                this.bone97.func_78793_a(0.0f, -4.5f, 0.0f);
                this.bone96.func_78792_a(this.bone97);
                this.setRotationAngle(this.bone97, 0.001f, 0.0227f, -0.3481f);
                this.bone97.field_78804_l.add(new ModelBox(this.bone97, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, true));
                this.ball7 = new ModelRenderer((ModelBase)this);
                this.ball7.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone97.func_78792_a(this.ball7);
                this.exhaust8 = new ModelRenderer((ModelBase)this);
                this.exhaust8.func_78793_a(2.0f, 7.0f, 0.25f);
                this.bodyStage2.func_78792_a(this.exhaust8);
                this.setRotationAngle(this.exhaust8, -2.618f, 0.2618f, 0.0f);
                this.exhaust8.field_78804_l.add(new ModelBox(this.exhaust8, 0, 43, -2.0f, -6.0f, -2.0f, 4, 6, 4, 0.0f, false));
                this.bone58 = new ModelRenderer((ModelBase)this);
                this.bone58.func_78793_a(-2.25f, -5.9f, 2.1f);
                this.exhaust8.func_78792_a(this.bone58);
                this.setRotationAngle(this.bone58, 0.5087f, -0.1298f, 0.228f);
                this.bone58.field_78804_l.add(new ModelBox(this.bone58, 0, 43, 0.0f, -4.0f, -4.0f, 4, 4, 4, -0.1f, false));
                this.bone59 = new ModelRenderer((ModelBase)this);
                this.bone59.func_78793_a(1.75f, -3.1f, -1.8f);
                this.bone58.func_78792_a(this.bone59);
                this.setRotationAngle(this.bone59, 0.2443f, -0.1134f, 0.3316f);
                this.bone59.field_78804_l.add(new ModelBox(this.bone59, 0, 43, -2.0f, -3.45f, -2.0f, 4, 4, 4, -0.2f, false));
                this.exhaustExtension8 = new ModelRenderer((ModelBase)this);
                this.exhaustExtension8.func_78793_a(-0.25f, -2.5f, 2.0f);
                this.bone59.func_78792_a(this.exhaustExtension8);
                this.setRotationAngle(this.exhaustExtension8, -0.0436f, -0.1745f, 0.3491f);
                this.exhaustExtension8.field_78804_l.add(new ModelBox(this.exhaustExtension8, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.0f, false));
                this.bone60 = new ModelRenderer((ModelBase)this);
                this.bone60.func_78793_a(-0.25f, -3.0f, 0.0f);
                this.exhaustExtension8.func_78792_a(this.bone60);
                this.setRotationAngle(this.bone60, -0.0322f, -0.0892f, 0.612f);
                this.bone60.field_78804_l.add(new ModelBox(this.bone60, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.2f, false));
                this.bone61 = new ModelRenderer((ModelBase)this);
                this.bone61.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone60.func_78792_a(this.bone61);
                this.setRotationAngle(this.bone61, -0.0027f, -0.0111f, 0.5668f);
                this.bone61.field_78804_l.add(new ModelBox(this.bone61, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.4f, false));
                this.bone62 = new ModelRenderer((ModelBase)this);
                this.bone62.func_78793_a(0.0f, -3.5f, 0.0f);
                this.bone61.func_78792_a(this.bone62);
                this.setRotationAngle(this.bone62, 0.0169f, 0.0701f, 0.4798f);
                this.bone62.field_78804_l.add(new ModelBox(this.bone62, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.6f, false));
                this.bone63 = new ModelRenderer((ModelBase)this);
                this.bone63.func_78793_a(0.0f, -4.5f, 0.0f);
                this.bone62.func_78792_a(this.bone63);
                this.setRotationAngle(this.bone63, 0.001f, -0.0227f, 0.3481f);
                this.bone63.field_78804_l.add(new ModelBox(this.bone63, 0, 43, -2.0f, -4.0f, -4.0f, 4, 4, 4, 0.8f, false));
                this.ball8 = new ModelRenderer((ModelBase)this);
                this.ball8.func_78793_a(0.0f, -8.0f, -2.0f);
                this.bone63.func_78792_a(this.ball8);
                this.field_178723_h = new ModelRenderer((ModelBase)this);
                this.field_178723_h.func_78793_a(-5.0f, 2.0f, 0.0f);
                this.setRotationAngle(this.field_178723_h, -0.3927f, 0.0f, 0.0f);
                this.field_178723_h.field_78804_l.add(new ModelBox(this.field_178723_h, 40, 16, -3.0f, -2.0f, -2.0f, 4, 12, 4, 0.11f, false));
                this.rightArmSpikes = new ModelRenderer((ModelBase)this);
                this.rightArmSpikes.func_78793_a(-1.0f, 6.0f, 0.0f);
                this.field_178723_h.func_78792_a(this.rightArmSpikes);
                this.bone129 = new ModelRenderer((ModelBase)this);
                this.bone129.func_78793_a(0.25f, 4.5f, 0.0f);
                this.rightArmSpikes.func_78792_a(this.bone129);
                this.setRotationAngle(this.bone129, 0.0f, 0.0f, -0.3491f);
                this.bone72 = new ModelRenderer((ModelBase)this);
                this.bone72.func_78793_a(-2.0f, 1.0f, 0.0f);
                this.bone129.func_78792_a(this.bone72);
                this.setRotationAngle(this.bone72, 0.0f, 0.0f, -0.3491f);
                this.bone25 = new ModelRenderer((ModelBase)this);
                this.bone25.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone72.func_78792_a(this.bone25);
                this.setRotationAngle(this.bone25, 0.0f, 0.7854f, 0.0f);
                this.bone25.field_78804_l.add(new ModelBox(this.bone25, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone125 = new ModelRenderer((ModelBase)this);
                this.bone125.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone129.func_78792_a(this.bone125);
                this.setRotationAngle(this.bone125, 0.3491f, 0.0f, 0.0f);
                this.bone126 = new ModelRenderer((ModelBase)this);
                this.bone126.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone125.func_78792_a(this.bone126);
                this.setRotationAngle(this.bone126, 0.0f, -0.7854f, 0.0f);
                this.bone126.field_78804_l.add(new ModelBox(this.bone126, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone127 = new ModelRenderer((ModelBase)this);
                this.bone127.func_78793_a(0.0f, 1.0f, 2.0f);
                this.bone129.func_78792_a(this.bone127);
                this.setRotationAngle(this.bone127, -0.3491f, 0.0f, 0.0f);
                this.bone128 = new ModelRenderer((ModelBase)this);
                this.bone128.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone127.func_78792_a(this.bone128);
                this.setRotationAngle(this.bone128, 0.0f, 2.3562f, 0.0f);
                this.bone128.field_78804_l.add(new ModelBox(this.bone128, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone124 = new ModelRenderer((ModelBase)this);
                this.bone124.func_78793_a(-1.5f, 1.0f, -1.5f);
                this.bone129.func_78792_a(this.bone124);
                this.setRotationAngle(this.bone124, 0.2618f, 0.0f, -0.2618f);
                this.bone124.field_78804_l.add(new ModelBox(this.bone124, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone86 = new ModelRenderer((ModelBase)this);
                this.bone86.func_78793_a(-1.5f, 1.0f, 1.5f);
                this.bone129.func_78792_a(this.bone86);
                this.setRotationAngle(this.bone86, -1.5708f, 1.309f, -1.8326f);
                this.bone86.field_78804_l.add(new ModelBox(this.bone86, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone131 = new ModelRenderer((ModelBase)this);
                this.bone131.func_78793_a(0.0f, 2.5f, 0.0f);
                this.rightArmSpikes.func_78792_a(this.bone131);
                this.setRotationAngle(this.bone131, 0.0f, -0.3491f, -0.1745f);
                this.bone132 = new ModelRenderer((ModelBase)this);
                this.bone132.func_78793_a(-2.0f, 1.0f, 0.0f);
                this.bone131.func_78792_a(this.bone132);
                this.setRotationAngle(this.bone132, 0.0f, 0.0f, -0.3491f);
                this.bone133 = new ModelRenderer((ModelBase)this);
                this.bone133.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone132.func_78792_a(this.bone133);
                this.setRotationAngle(this.bone133, 0.0f, 0.7854f, 0.0f);
                this.bone133.field_78804_l.add(new ModelBox(this.bone133, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone134 = new ModelRenderer((ModelBase)this);
                this.bone134.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone131.func_78792_a(this.bone134);
                this.setRotationAngle(this.bone134, 0.3491f, 0.0f, 0.0f);
                this.bone135 = new ModelRenderer((ModelBase)this);
                this.bone135.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone134.func_78792_a(this.bone135);
                this.setRotationAngle(this.bone135, 0.0f, -0.7854f, 0.0f);
                this.bone135.field_78804_l.add(new ModelBox(this.bone135, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone136 = new ModelRenderer((ModelBase)this);
                this.bone136.func_78793_a(0.0f, 1.0f, 2.0f);
                this.bone131.func_78792_a(this.bone136);
                this.setRotationAngle(this.bone136, -0.3491f, 0.0f, 0.0f);
                this.bone137 = new ModelRenderer((ModelBase)this);
                this.bone137.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone136.func_78792_a(this.bone137);
                this.setRotationAngle(this.bone137, 0.0f, 2.3562f, 0.0f);
                this.bone137.field_78804_l.add(new ModelBox(this.bone137, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone138 = new ModelRenderer((ModelBase)this);
                this.bone138.func_78793_a(-1.5f, 1.0f, -1.5f);
                this.bone131.func_78792_a(this.bone138);
                this.setRotationAngle(this.bone138, 0.2618f, 0.0f, -0.2618f);
                this.bone138.field_78804_l.add(new ModelBox(this.bone138, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone139 = new ModelRenderer((ModelBase)this);
                this.bone139.func_78793_a(-1.5f, 1.0f, 1.5f);
                this.bone131.func_78792_a(this.bone139);
                this.setRotationAngle(this.bone139, -1.5708f, 1.309f, -1.8326f);
                this.bone139.field_78804_l.add(new ModelBox(this.bone139, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone140 = new ModelRenderer((ModelBase)this);
                this.bone140.func_78793_a(0.0f, 0.5f, 0.0f);
                this.rightArmSpikes.func_78792_a(this.bone140);
                this.setRotationAngle(this.bone140, 0.0f, 0.0f, -0.0873f);
                this.bone141 = new ModelRenderer((ModelBase)this);
                this.bone141.func_78793_a(-2.0f, 1.0f, 0.0f);
                this.bone140.func_78792_a(this.bone141);
                this.setRotationAngle(this.bone141, 0.0f, 0.0f, -0.3491f);
                this.bone142 = new ModelRenderer((ModelBase)this);
                this.bone142.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone141.func_78792_a(this.bone142);
                this.setRotationAngle(this.bone142, 0.0f, 0.7854f, 0.0f);
                this.bone142.field_78804_l.add(new ModelBox(this.bone142, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone143 = new ModelRenderer((ModelBase)this);
                this.bone143.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone140.func_78792_a(this.bone143);
                this.setRotationAngle(this.bone143, 0.3491f, 0.0f, 0.0f);
                this.bone144 = new ModelRenderer((ModelBase)this);
                this.bone144.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone143.func_78792_a(this.bone144);
                this.setRotationAngle(this.bone144, 0.0f, -0.7854f, 0.0f);
                this.bone144.field_78804_l.add(new ModelBox(this.bone144, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone145 = new ModelRenderer((ModelBase)this);
                this.bone145.func_78793_a(0.0f, 1.0f, 2.0f);
                this.bone140.func_78792_a(this.bone145);
                this.setRotationAngle(this.bone145, -0.3491f, 0.0f, 0.0f);
                this.bone146 = new ModelRenderer((ModelBase)this);
                this.bone146.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone145.func_78792_a(this.bone146);
                this.setRotationAngle(this.bone146, 0.0f, 2.3562f, 0.0f);
                this.bone146.field_78804_l.add(new ModelBox(this.bone146, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone147 = new ModelRenderer((ModelBase)this);
                this.bone147.func_78793_a(-1.5f, 1.0f, -1.5f);
                this.bone140.func_78792_a(this.bone147);
                this.setRotationAngle(this.bone147, 0.2618f, 0.0f, -0.2618f);
                this.bone147.field_78804_l.add(new ModelBox(this.bone147, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone148 = new ModelRenderer((ModelBase)this);
                this.bone148.func_78793_a(-1.5f, 1.0f, 1.5f);
                this.bone140.func_78792_a(this.bone148);
                this.setRotationAngle(this.bone148, -1.5708f, 1.309f, -1.8326f);
                this.bone148.field_78804_l.add(new ModelBox(this.bone148, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone150 = new ModelRenderer((ModelBase)this);
                this.bone150.func_78793_a(0.0f, -1.5f, 0.0f);
                this.rightArmSpikes.func_78792_a(this.bone150);
                this.setRotationAngle(this.bone150, 0.0f, 0.3491f, 0.0f);
                this.bone151 = new ModelRenderer((ModelBase)this);
                this.bone151.func_78793_a(-2.0f, 1.0f, 0.0f);
                this.bone150.func_78792_a(this.bone151);
                this.setRotationAngle(this.bone151, 0.0f, 0.0f, -0.3491f);
                this.bone152 = new ModelRenderer((ModelBase)this);
                this.bone152.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone151.func_78792_a(this.bone152);
                this.setRotationAngle(this.bone152, 0.0f, 0.7854f, 0.0f);
                this.bone152.field_78804_l.add(new ModelBox(this.bone152, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone153 = new ModelRenderer((ModelBase)this);
                this.bone153.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone150.func_78792_a(this.bone153);
                this.setRotationAngle(this.bone153, 0.3491f, 0.0f, 0.0f);
                this.bone154 = new ModelRenderer((ModelBase)this);
                this.bone154.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone153.func_78792_a(this.bone154);
                this.setRotationAngle(this.bone154, 0.0f, -0.7854f, 0.0f);
                this.bone154.field_78804_l.add(new ModelBox(this.bone154, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone157 = new ModelRenderer((ModelBase)this);
                this.bone157.func_78793_a(-1.5f, 1.0f, -1.5f);
                this.bone150.func_78792_a(this.bone157);
                this.setRotationAngle(this.bone157, 0.2618f, 0.0f, -0.2618f);
                this.bone157.field_78804_l.add(new ModelBox(this.bone157, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.bone158 = new ModelRenderer((ModelBase)this);
                this.bone158.func_78793_a(-1.5f, 1.0f, 1.5f);
                this.bone150.func_78792_a(this.bone158);
                this.setRotationAngle(this.bone158, -1.5708f, 1.309f, -1.8326f);
                this.bone158.field_78804_l.add(new ModelBox(this.bone158, 40, 32, 0.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, false));
                this.armExhaust = new ModelRenderer((ModelBase)this);
                this.armExhaust.func_78793_a(-3.5f, 2.5f, 0.0f);
                this.field_178723_h.func_78792_a(this.armExhaust);
                this.setRotationAngle(this.armExhaust, 0.0f, 0.0f, -0.1309f);
                this.bone106 = new ModelRenderer((ModelBase)this);
                this.bone106.func_78793_a(0.5f, -0.5f, 0.0f);
                this.armExhaust.func_78792_a(this.bone106);
                this.setRotationAngle(this.bone106, -0.0315f, -0.8124f, -0.501f);
                this.bone106.field_78804_l.add(new ModelBox(this.bone106, 0, 0, -1.0f, -6.0f, -1.0f, 2, 6, 2, 0.2f, false));
                this.bone109 = new ModelRenderer((ModelBase)this);
                this.bone109.func_78793_a(1.0f, -2.5f, -1.0f);
                this.armExhaust.func_78792_a(this.bone109);
                this.setRotationAngle(this.bone109, 0.3182f, -0.7925f, -0.6233f);
                this.bone109.field_78804_l.add(new ModelBox(this.bone109, 0, 0, -1.0f, -6.0f, -1.0f, 2, 6, 2, 0.2f, false));
                this.bone110 = new ModelRenderer((ModelBase)this);
                this.bone110.func_78793_a(1.0f, -2.5f, 1.0f);
                this.armExhaust.func_78792_a(this.bone110);
                this.setRotationAngle(this.bone110, -0.3864f, -0.9815f, -0.0904f);
                this.bone110.field_78804_l.add(new ModelBox(this.bone110, 0, 0, -1.0f, -6.0f, -1.0f, 2, 6, 2, 0.2f, false));
                this.bone107 = new ModelRenderer((ModelBase)this);
                this.bone107.func_78793_a(1.0f, -0.5f, -1.0f);
                this.armExhaust.func_78792_a(this.bone107);
                this.setRotationAngle(this.bone107, 0.6902f, -0.7106f, -1.0887f);
                this.bone107.field_78804_l.add(new ModelBox(this.bone107, 0, 0, -1.0f, -6.0f, -1.0f, 2, 6, 2, 0.2f, false));
                this.bone108 = new ModelRenderer((ModelBase)this);
                this.bone108.func_78793_a(1.0f, -0.5f, 1.0f);
                this.armExhaust.func_78792_a(this.bone108);
                this.setRotationAngle(this.bone108, -0.7646f, -0.8326f, 0.018f);
                this.bone108.field_78804_l.add(new ModelBox(this.bone108, 0, 0, -1.0f, -6.0f, -1.0f, 2, 6, 2, 0.2f, false));
                this.needle = new ModelRenderer((ModelBase)this);
                this.needle.func_78793_a(0.0f, 10.0f, 0.0f);
                this.field_178723_h.func_78792_a(this.needle);
                this.setRotationAngle(this.needle, 0.0f, -0.7854f, 0.0f);
                this.needle.field_78804_l.add(new ModelBox(this.needle, 56, 16, -0.5f, 0.0f, -0.5f, 1, 8, 1, 0.0f, false));
                this.needle.field_78804_l.add(new ModelBox(this.needle, 56, 16, -0.5f, 7.75f, -0.5f, 1, 4, 1, -0.1f, false));
                this.needle.field_78804_l.add(new ModelBox(this.needle, 56, 16, -0.5f, 11.25f, -0.5f, 1, 2, 1, -0.2f, false));
                this.needle.field_78804_l.add(new ModelBox(this.needle, 56, 16, -0.5f, 12.75f, -0.5f, 1, 2, 1, -0.3f, false));
                this.needle.field_78804_l.add(new ModelBox(this.needle, 56, 16, -0.5f, 13.75f, -0.5f, 1, 2, 1, -0.4f, false));
                this.bulge = new ModelRenderer((ModelBase)this);
                this.bulge.func_78793_a(-0.5f, 16.0f, 0.5f);
                this.needle.func_78792_a(this.bulge);
                this.bulge.field_78804_l.add(new ModelBox(this.bulge, 56, 17, 0.0f, -2.0f, -1.0f, 1, 2, 1, 0.4f, false));
                this.bulge.field_78804_l.add(new ModelBox(this.bulge, 56, 17, 0.0f, -3.0f, -1.0f, 1, 4, 1, 0.2f, false));
                this.bulge.field_78804_l.add(new ModelBox(this.bulge, 56, 17, 0.0f, -4.0f, -1.0f, 1, 6, 1, 0.0f, false));
                this.field_178724_i = new ModelRenderer((ModelBase)this);
                this.field_178724_i.func_78793_a(5.0f, 2.0f, 0.0f);
                this.setRotationAngle(this.field_178724_i, 0.3927f, 0.0f, 0.0f);
                this.field_178724_i.field_78804_l.add(new ModelBox(this.field_178724_i, 40, 16, -1.0f, -2.0f, -2.0f, 4, 12, 4, 0.11f, true));
                this.leftArmSpikes = new ModelRenderer((ModelBase)this);
                this.leftArmSpikes.func_78793_a(1.0f, 6.0f, 0.0f);
                this.field_178724_i.func_78792_a(this.leftArmSpikes);
                this.bone19 = new ModelRenderer((ModelBase)this);
                this.bone19.func_78793_a(-0.25f, 4.5f, 0.0f);
                this.leftArmSpikes.func_78792_a(this.bone19);
                this.setRotationAngle(this.bone19, 0.0f, 0.0f, 0.3491f);
                this.bone20 = new ModelRenderer((ModelBase)this);
                this.bone20.func_78793_a(2.0f, 1.0f, 0.0f);
                this.bone19.func_78792_a(this.bone20);
                this.setRotationAngle(this.bone20, 0.0f, 0.0f, 0.3491f);
                this.bone21 = new ModelRenderer((ModelBase)this);
                this.bone21.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone20.func_78792_a(this.bone21);
                this.setRotationAngle(this.bone21, 0.0f, -0.7854f, 0.0f);
                this.bone21.field_78804_l.add(new ModelBox(this.bone21, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone22 = new ModelRenderer((ModelBase)this);
                this.bone22.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone19.func_78792_a(this.bone22);
                this.setRotationAngle(this.bone22, 0.3491f, 0.0f, 0.0f);
                this.bone23 = new ModelRenderer((ModelBase)this);
                this.bone23.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone22.func_78792_a(this.bone23);
                this.setRotationAngle(this.bone23, 0.0f, 0.7854f, 0.0f);
                this.bone23.field_78804_l.add(new ModelBox(this.bone23, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone24 = new ModelRenderer((ModelBase)this);
                this.bone24.func_78793_a(0.0f, 1.0f, 2.0f);
                this.bone19.func_78792_a(this.bone24);
                this.setRotationAngle(this.bone24, -0.3491f, 0.0f, 0.0f);
                this.bone26 = new ModelRenderer((ModelBase)this);
                this.bone26.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone24.func_78792_a(this.bone26);
                this.setRotationAngle(this.bone26, 0.0f, -2.3562f, 0.0f);
                this.bone26.field_78804_l.add(new ModelBox(this.bone26, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone27 = new ModelRenderer((ModelBase)this);
                this.bone27.func_78793_a(1.5f, 1.0f, -1.5f);
                this.bone19.func_78792_a(this.bone27);
                this.setRotationAngle(this.bone27, 0.2618f, 0.0f, 0.2618f);
                this.bone27.field_78804_l.add(new ModelBox(this.bone27, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone28 = new ModelRenderer((ModelBase)this);
                this.bone28.func_78793_a(1.5f, 1.0f, 1.5f);
                this.bone19.func_78792_a(this.bone28);
                this.setRotationAngle(this.bone28, -1.5708f, -1.309f, 1.8326f);
                this.bone28.field_78804_l.add(new ModelBox(this.bone28, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone29 = new ModelRenderer((ModelBase)this);
                this.bone29.func_78793_a(0.0f, 2.5f, 0.0f);
                this.leftArmSpikes.func_78792_a(this.bone29);
                this.setRotationAngle(this.bone29, 0.0f, 0.3491f, 0.1745f);
                this.bone32 = new ModelRenderer((ModelBase)this);
                this.bone32.func_78793_a(2.0f, 1.0f, 0.0f);
                this.bone29.func_78792_a(this.bone32);
                this.setRotationAngle(this.bone32, 0.0f, 0.0f, 0.3491f);
                this.bone34 = new ModelRenderer((ModelBase)this);
                this.bone34.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone32.func_78792_a(this.bone34);
                this.setRotationAngle(this.bone34, 0.0f, -0.7854f, 0.0f);
                this.bone34.field_78804_l.add(new ModelBox(this.bone34, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone35 = new ModelRenderer((ModelBase)this);
                this.bone35.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone29.func_78792_a(this.bone35);
                this.setRotationAngle(this.bone35, 0.3491f, 0.0f, 0.0f);
                this.bone36 = new ModelRenderer((ModelBase)this);
                this.bone36.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone35.func_78792_a(this.bone36);
                this.setRotationAngle(this.bone36, 0.0f, 0.7854f, 0.0f);
                this.bone36.field_78804_l.add(new ModelBox(this.bone36, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone37 = new ModelRenderer((ModelBase)this);
                this.bone37.func_78793_a(0.0f, 1.0f, 2.0f);
                this.bone29.func_78792_a(this.bone37);
                this.setRotationAngle(this.bone37, -0.3491f, 0.0f, 0.0f);
                this.bone38 = new ModelRenderer((ModelBase)this);
                this.bone38.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone37.func_78792_a(this.bone38);
                this.setRotationAngle(this.bone38, 0.0f, -2.3562f, 0.0f);
                this.bone38.field_78804_l.add(new ModelBox(this.bone38, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone39 = new ModelRenderer((ModelBase)this);
                this.bone39.func_78793_a(1.5f, 1.0f, -1.5f);
                this.bone29.func_78792_a(this.bone39);
                this.setRotationAngle(this.bone39, 0.2618f, 0.0f, 0.2618f);
                this.bone39.field_78804_l.add(new ModelBox(this.bone39, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone40 = new ModelRenderer((ModelBase)this);
                this.bone40.func_78793_a(1.5f, 1.0f, 1.5f);
                this.bone29.func_78792_a(this.bone40);
                this.setRotationAngle(this.bone40, -1.5708f, -1.309f, 1.8326f);
                this.bone40.field_78804_l.add(new ModelBox(this.bone40, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone41 = new ModelRenderer((ModelBase)this);
                this.bone41.func_78793_a(0.0f, 0.5f, 0.0f);
                this.leftArmSpikes.func_78792_a(this.bone41);
                this.setRotationAngle(this.bone41, 0.0f, 0.0f, 0.0873f);
                this.bone42 = new ModelRenderer((ModelBase)this);
                this.bone42.func_78793_a(2.0f, 1.0f, 0.0f);
                this.bone41.func_78792_a(this.bone42);
                this.setRotationAngle(this.bone42, 0.0f, 0.0f, 0.3491f);
                this.bone43 = new ModelRenderer((ModelBase)this);
                this.bone43.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone42.func_78792_a(this.bone43);
                this.setRotationAngle(this.bone43, 0.0f, -0.7854f, 0.0f);
                this.bone43.field_78804_l.add(new ModelBox(this.bone43, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone44 = new ModelRenderer((ModelBase)this);
                this.bone44.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone41.func_78792_a(this.bone44);
                this.setRotationAngle(this.bone44, 0.3491f, 0.0f, 0.0f);
                this.bone45 = new ModelRenderer((ModelBase)this);
                this.bone45.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone44.func_78792_a(this.bone45);
                this.setRotationAngle(this.bone45, 0.0f, 0.7854f, 0.0f);
                this.bone45.field_78804_l.add(new ModelBox(this.bone45, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone46 = new ModelRenderer((ModelBase)this);
                this.bone46.func_78793_a(0.0f, 1.0f, 2.0f);
                this.bone41.func_78792_a(this.bone46);
                this.setRotationAngle(this.bone46, -0.3491f, 0.0f, 0.0f);
                this.bone48 = new ModelRenderer((ModelBase)this);
                this.bone48.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone46.func_78792_a(this.bone48);
                this.setRotationAngle(this.bone48, 0.0f, -2.3562f, 0.0f);
                this.bone48.field_78804_l.add(new ModelBox(this.bone48, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone49 = new ModelRenderer((ModelBase)this);
                this.bone49.func_78793_a(1.5f, 1.0f, -1.5f);
                this.bone41.func_78792_a(this.bone49);
                this.setRotationAngle(this.bone49, 0.2618f, 0.0f, 0.2618f);
                this.bone49.field_78804_l.add(new ModelBox(this.bone49, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone50 = new ModelRenderer((ModelBase)this);
                this.bone50.func_78793_a(1.5f, 1.0f, 1.5f);
                this.bone41.func_78792_a(this.bone50);
                this.setRotationAngle(this.bone50, -1.5708f, -1.309f, 1.8326f);
                this.bone50.field_78804_l.add(new ModelBox(this.bone50, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone51 = new ModelRenderer((ModelBase)this);
                this.bone51.func_78793_a(0.0f, -1.5f, 0.0f);
                this.leftArmSpikes.func_78792_a(this.bone51);
                this.setRotationAngle(this.bone51, 0.0f, -0.3491f, 0.0f);
                this.bone52 = new ModelRenderer((ModelBase)this);
                this.bone52.func_78793_a(2.0f, 1.0f, 0.0f);
                this.bone51.func_78792_a(this.bone52);
                this.setRotationAngle(this.bone52, 0.0f, 0.0f, 0.3491f);
                this.bone53 = new ModelRenderer((ModelBase)this);
                this.bone53.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone52.func_78792_a(this.bone53);
                this.setRotationAngle(this.bone53, 0.0f, -0.7854f, 0.0f);
                this.bone53.field_78804_l.add(new ModelBox(this.bone53, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone54 = new ModelRenderer((ModelBase)this);
                this.bone54.func_78793_a(0.0f, 1.0f, -2.0f);
                this.bone51.func_78792_a(this.bone54);
                this.setRotationAngle(this.bone54, 0.3491f, 0.0f, 0.0f);
                this.bone55 = new ModelRenderer((ModelBase)this);
                this.bone55.func_78793_a(0.0f, 0.0f, 0.0f);
                this.bone54.func_78792_a(this.bone55);
                this.setRotationAngle(this.bone55, 0.0f, 0.7854f, 0.0f);
                this.bone55.field_78804_l.add(new ModelBox(this.bone55, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone56 = new ModelRenderer((ModelBase)this);
                this.bone56.func_78793_a(1.5f, 1.0f, -1.5f);
                this.bone51.func_78792_a(this.bone56);
                this.setRotationAngle(this.bone56, 0.2618f, 0.0f, 0.2618f);
                this.bone56.field_78804_l.add(new ModelBox(this.bone56, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.bone57 = new ModelRenderer((ModelBase)this);
                this.bone57.func_78793_a(1.5f, 1.0f, 1.5f);
                this.bone51.func_78792_a(this.bone57);
                this.setRotationAngle(this.bone57, -1.5708f, -1.309f, 1.8326f);
                this.bone57.field_78804_l.add(new ModelBox(this.bone57, 40, 32, -4.0f, -6.0f, 0.0f, 4, 6, 4, 0.2f, true));
                this.field_178721_j = new ModelRenderer((ModelBase)this);
                this.field_178721_j.func_78793_a(-1.9f, 12.0f, 0.0f);
                this.setRotationAngle(this.field_178721_j, 0.3927f, 0.0f, 0.0f);
                this.field_178721_j.field_78804_l.add(new ModelBox(this.field_178721_j, 0, 16, -2.0f, 0.0f, -2.0f, 4, 12, 4, 0.1f, false));
                this.field_178721_j.field_78804_l.add(new ModelBox(this.field_178721_j, 0, 32, -2.0f, 0.0f, -2.0f, 4, 7, 4, 0.35f, false));
                this.field_178722_k = new ModelRenderer((ModelBase)this);
                this.field_178722_k.func_78793_a(1.9f, 12.0f, 0.0f);
                this.setRotationAngle(this.field_178722_k, -0.3927f, 0.0f, 0.0f);
                this.field_178722_k.field_78804_l.add(new ModelBox(this.field_178722_k, 0, 16, -2.0f, 0.0f, -2.0f, 4, 12, 4, 0.1f, true));
                this.field_178722_k.field_78804_l.add(new ModelBox(this.field_178722_k, 0, 32, -2.0f, 0.0f, -2.0f, 4, 7, 4, 0.35f, true));
                this.blasts = new ModelRenderer((ModelBase)this);
                this.blasts.func_78793_a(0.0f, 0.0f, 0.0f);
                this.blast1 = new ModelRenderer((ModelBase)this);
                this.blast1.func_78793_a(-6.6f, -7.6f, -8.45f);
                this.blasts.func_78792_a(this.blast1);
                this.setRotationAngle(this.blast1, -1.2204f, -0.0806f, -0.0335f);
                this.blast1.field_78804_l.add(new ModelBox(this.blast1, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, false));
                this.blast2 = new ModelRenderer((ModelBase)this);
                this.blast2.func_78793_a(6.6f, -7.6f, -8.45f);
                this.blasts.func_78792_a(this.blast2);
                this.setRotationAngle(this.blast2, -1.2204f, 0.0806f, 0.0335f);
                this.blast2.field_78804_l.add(new ModelBox(this.blast2, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, true));
                this.blast3 = new ModelRenderer((ModelBase)this);
                this.blast3.func_78793_a(-12.55f, -2.55f, -5.7f);
                this.blasts.func_78792_a(this.blast3);
                this.setRotationAngle(this.blast3, -1.4785f, -0.2143f, -0.0423f);
                this.blast3.field_78804_l.add(new ModelBox(this.blast3, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, false));
                this.blast4 = new ModelRenderer((ModelBase)this);
                this.blast4.func_78793_a(12.55f, -2.55f, -5.7f);
                this.blasts.func_78792_a(this.blast4);
                this.setRotationAngle(this.blast4, -1.4785f, 0.2143f, 0.0423f);
                this.blast4.field_78804_l.add(new ModelBox(this.blast4, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, true));
                this.blast5 = new ModelRenderer((ModelBase)this);
                this.blast5.func_78793_a(-14.4f, 6.1f, -6.7f);
                this.blasts.func_78792_a(this.blast5);
                this.setRotationAngle(this.blast5, -1.5708f, -0.1745f, 0.0f);
                this.blast5.field_78804_l.add(new ModelBox(this.blast5, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, false));
                this.blast6 = new ModelRenderer((ModelBase)this);
                this.blast6.func_78793_a(14.4f, 6.1f, -6.7f);
                this.blasts.func_78792_a(this.blast6);
                this.setRotationAngle(this.blast6, -1.5708f, 0.1745f, 0.0f);
                this.blast6.field_78804_l.add(new ModelBox(this.blast6, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, true));
                this.blast7 = new ModelRenderer((ModelBase)this);
                this.blast7.func_78793_a(-14.4f, 14.25f, -5.95f);
                this.blasts.func_78792_a(this.blast7);
                this.setRotationAngle(this.blast7, -1.6144f, -0.0873f, 0.0f);
                this.blast7.field_78804_l.add(new ModelBox(this.blast7, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, false));
                this.blast8 = new ModelRenderer((ModelBase)this);
                this.blast8.func_78793_a(14.4f, 14.25f, -5.95f);
                this.blasts.func_78792_a(this.blast8);
                this.setRotationAngle(this.blast8, -1.6144f, 0.0873f, 0.0f);
                this.blast8.field_78804_l.add(new ModelBox(this.blast8, 11, 49, -2.5f, 0.0f, -2.5f, 5, 0, 5, 0.0f, true));
            }

            public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
                modelRenderer.field_78795_f = x;
                modelRenderer.field_78796_g = y;
                modelRenderer.field_78808_h = z;
            }

            public void func_78088_a(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                super.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                if (this.blasts.field_78806_j) {
                    GlStateManager.func_179094_E();
                    if (entityIn.func_70093_af()) {
                        GlStateManager.func_179109_b((float)0.0f, (float)0.2f, (float)0.0f);
                    }
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179140_f();
                    OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                    this.blasts.func_78785_a(scale);
                    GlStateManager.func_179145_e();
                    GlStateManager.func_179084_k();
                    GlStateManager.func_179121_F();
                }
            }

            public void func_178719_a(boolean visible) {
                super.func_178719_a(visible);
                this.headStage0.field_78806_j = visible;
                this.headStage1.field_78806_j = visible;
                this.headStage2.field_78806_j = visible;
                this.armExhaust.field_78806_j = visible;
                this.bodyStage1.field_78806_j = visible;
                this.bodyStage2.field_78806_j = visible;
                this.exhaustExtension1.field_78806_j = visible;
                this.exhaustExtension2.field_78806_j = visible;
                this.exhaustExtension3.field_78806_j = visible;
                this.exhaustExtension4.field_78806_j = visible;
                this.exhaustExtension5.field_78806_j = visible;
                this.exhaustExtension6.field_78806_j = visible;
                this.exhaustExtension7.field_78806_j = visible;
                this.exhaustExtension8.field_78806_j = visible;
                this.blasts.field_78806_j = visible;
                this.needle.field_78806_j = visible;
                this.bulge.field_78806_j = visible;
            }

            public void func_178686_a(ModelBase model) {
                super.func_178686_a(model);
                if (model instanceof ModelBiped) {
                    this.wearerModel = (ModelBiped)model;
                }
            }

            public void func_78087_a(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
                float f6;
                if (entity instanceof AbstractClientPlayer && ((AbstractClientPlayer)entity).func_175154_l().equals("slim")) {
                    this.field_178724_i.func_78793_a(5.0f, 2.5f, 0.0f);
                    this.field_178723_h.func_78793_a(-5.0f, 2.5f, 0.0f);
                }
                super.func_78087_a(f, f1, f2, f3, f4, f5, entity);
                if (this.wearerModel != null) {
                    ModelJugo.func_178685_a((ModelRenderer)this.wearerModel.field_178724_i, (ModelRenderer)this.field_178724_i);
                    ModelJugo.func_178685_a((ModelRenderer)this.wearerModel.field_178723_h, (ModelRenderer)this.field_178723_h);
                    ModelJugo.func_178685_a((ModelRenderer)this.wearerModel.field_178722_k, (ModelRenderer)this.field_178722_k);
                    ModelJugo.func_178685_a((ModelRenderer)this.wearerModel.field_178721_j, (ModelRenderer)this.field_178721_j);
                }
                if ((f6 = (float)entity.getEntityData().func_74762_e(ItemSenninka.START_TIME) + f2 - (float)entity.field_70173_aa) <= 40.0f) {
                    float gb = MathHelper.func_76131_a((float)((f6 - 20.0f) / 20.0f), (float)0.0f, (float)1.0f);
                    float a = MathHelper.func_76131_a((float)(f6 / 20.0f), (float)0.0f, (float)1.0f);
                    GlStateManager.func_179147_l();
                    GlStateManager.func_179092_a((int)516, (float)0.001f);
                    GlStateManager.func_179131_c((float)1.0f, (float)gb, (float)gb, (float)a);
                    GlStateManager.func_187401_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                }
            }
        }

        @SideOnly(value=Side.CLIENT)
        public class CustomRender
        extends Render<EntityMultiCannon> {
            private final ResourceLocation texture;

            public CustomRender(RenderManager renderManagerIn) {
                super(renderManagerIn);
                this.texture = new ResourceLocation("narutomod:textures/beam_gold.png");
            }

            public boolean shouldRender(EntityMultiCannon livingEntity, ICamera camera, double camX, double camY, double camZ) {
                return true;
            }

            public void doRender(EntityMultiCannon bullet, double x, double y, double z, float yaw, float pt) {
                float age = (float)bullet.field_70173_aa + pt;
                float f = age * 0.01f;
                float max_l = bullet.getBeamLength();
                this.func_180548_c(bullet);
                GlStateManager.func_179094_E();
                GlStateManager.func_179137_b((double)x, (double)y, (double)z);
                GlStateManager.func_179114_b((float)ProcedureUtils.interpolateRotation(bullet.field_70126_B, bullet.field_70177_z, pt), (float)0.0f, (float)1.0f, (float)0.0f);
                GlStateManager.func_179114_b((float)(90.0f - bullet.field_70127_C - (bullet.field_70125_A - bullet.field_70127_C) * pt), (float)1.0f, (float)0.0f, (float)0.0f);
                GlStateManager.func_179114_b((float)(age * 90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GlStateManager.func_179147_l();
                GlStateManager.func_179092_a((int)516, (float)0.001f);
                GlStateManager.func_179129_p();
                GlStateManager.func_179103_j((int)7425);
                GlStateManager.func_179140_f();
                OpenGlHelper.func_77475_a((int)OpenGlHelper.field_77476_b, (float)240.0f, (float)240.0f);
                GlStateManager.func_187401_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
                float f5 = 0.0f - f;
                float f6 = max_l / 32.0f - f;
                float f10 = Math.min(age / 100.0f, 1.0f);
                float f13 = f10 - 0.5f;
                float f11 = 1.0f - f13 * f13 * f13 * f13 * 15.0f;
                Tessellator tessellator = Tessellator.func_178181_a();
                BufferBuilder bufferbuilder = tessellator.func_178180_c();
                bufferbuilder.func_181668_a(5, DefaultVertexFormats.field_181709_i);
                for (float f12 = 0.0f; f12 < f11; f12 += 0.05f) {
                    for (int j = 0; j <= 8; ++j) {
                        float f7 = MathHelper.func_76126_a((float)((float)(j % 8) * ((float)Math.PI * 2) / 8.0f)) * 1.0f;
                        float f8 = MathHelper.func_76134_b((float)((float)(j % 8) * ((float)Math.PI * 2) / 8.0f)) * 1.0f;
                        float f9 = (float)(j % 8) / 8.0f;
                        bufferbuilder.func_181662_b((double)f7, 0.0, (double)f8).func_187315_a((double)f9, (double)f5).func_181666_a(1.0f, 1.0f, 1.0f, 0.15f).func_181675_d();
                        bufferbuilder.func_181662_b((double)(f7 * f12 * max_l * 0.5f), (double)(max_l * f11), (double)(f8 * f12 * max_l * 0.5f)).func_187315_a((double)f9, (double)f6).func_181666_a(1.0f, 1.0f, 1.0f, 0.0f).func_181675_d();
                    }
                }
                tessellator.func_78381_a();
                GlStateManager.func_179145_e();
                GlStateManager.func_179089_o();
                GlStateManager.func_179092_a((int)516, (float)0.1f);
                GlStateManager.func_179084_k();
                GlStateManager.func_179103_j((int)7424);
                GlStateManager.func_179121_F();
            }

            protected ResourceLocation getEntityTexture(EntityMultiCannon entity) {
                return this.texture;
            }
        }
    }

    public static class Absorption
    extends SenninkaJutsu {
        private static final String ID_KEY = "SenninkaAbsorbing";

        @Override
        public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            return true;
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
            if (!world.field_72995_K && entity instanceof EntityLivingBase) {
                boolean flag = this.isActivated((EntityLivingBase)entity, stack);
                if (flag) {
                    boolean absorbed = false;
                    Entity entity1 = ProcedureUtils.objectEntityLookingAt((Entity)entity, (double)2.2).field_72308_g;
                    if (entity1 instanceof EntityLivingBase && entity1.func_70089_S()) {
                        entity1.field_70172_ad = 10;
                        entity1.getEntityData().func_74757_a("TempData_disableKnockback", true);
                        if (entity1.func_70097_a(ItemJutsu.causeSenjutsuDamage(entity, null).func_76348_h(), 0.25f)) {
                            ((EntityLivingBase)entity).func_70691_i(0.25f);
                            if (entity.field_70173_aa % 20 == 2) {
                                entity.field_70170_p.func_184148_a(null, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 0.5f, 1.0f - MathHelper.func_76126_a((float)(0.02f * (float)entity.field_70173_aa)) * 0.3f);
                            }
                            if (!entity.getEntityData().func_74767_n(ID_KEY)) {
                                ProcedureSync.EntityNBTTag.setAndSync(entity, ID_KEY, true);
                            }
                            absorbed = true;
                        } else {
                            entity1.getEntityData().func_82580_o("TempData_disableKnockback");
                        }
                    }
                    if (!absorbed && entity.getEntityData().func_74767_n(ID_KEY)) {
                        ProcedureSync.EntityNBTTag.removeAndSync(entity, ID_KEY);
                    }
                }
                if (!this.anyOtherActivated((EntityLivingBase)entity, stack)) {
                    if (flag) {
                        ProcedureSync.EntityNBTTag.setAndSync(entity, ItemSenninka.START_TIME, entity.getEntityData().func_74762_e(ItemSenninka.START_TIME) + 1);
                    } else if (entity.getEntityData().func_74764_b(ItemSenninka.START_TIME)) {
                        ProcedureSync.EntityNBTTag.removeAndSync(entity, ItemSenninka.START_TIME);
                    }
                }
            }
        }

        @Override
        @SideOnly(value=Side.CLIENT)
        public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
            if (this.isActivated(living, stack) && !this.anyOtherActivated(living, stack)) {
                model.func_178719_a(false);
                model.field_78116_c.field_78806_j = true;
                ((Renderer.ModelJugo)model).headStage1.field_78806_j = true;
                model.field_178723_h.field_78806_j = true;
                ((Renderer.ModelJugo)model).rightArmSpikes.field_78795_f = 3.1416f;
                model.field_78115_e.field_78806_j = true;
                ((Renderer.ModelJugo)model).bodyStage1.field_78806_j = true;
                model.field_78117_n = living.func_70093_af();
                model.field_78093_q = living.func_184218_aH();
                model.field_78091_s = living.func_70631_g_();
                this.showNeedle(living, model);
                return true;
            }
            return false;
        }

        @SideOnly(value=Side.CLIENT)
        protected void showNeedle(EntityLivingBase living, Renderer.ModelJugo model) {
            ((Renderer.ModelJugo)model).needle.field_78806_j = true;
            if (living.getEntityData().func_74767_n(ID_KEY)) {
                ((Renderer.ModelJugo)model).bulge.field_78806_j = true;
                ((Renderer.ModelJugo)model).bulge.field_78797_d = 16 - living.getEntityData().func_74762_e(ItemSenninka.START_TIME) % 16;
            }
        }

        @Override
        public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
            return ItemJutsu.getCurrentJutsu(stack) == ABSORB && entity.func_184607_cu().equals(stack);
        }
    }

    public static class EntityMultiCannon
    extends EntityBeamBase.Base
    implements ItemJutsu.IJutsu {
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
            this.shoot(powerIn);
        }

        @Override
        public ItemJutsu.JutsuEnum.Type getJutsuType() {
            return ItemJutsu.JutsuEnum.Type.SENNINKA;
        }

        @Override
        protected void updatePosition() {
            EntityLivingBase shooter = this.getShooter();
            if (shooter != null) {
                Vec3d vec = shooter.func_70040_Z().func_72441_c(shooter.field_70165_t, shooter.field_70163_u + 1.2, shooter.field_70161_v);
                this.func_70107_b(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
            }
        }

        @Override
        public void func_70071_h_() {
            super.func_70071_h_();
            if (!this.field_70170_p.field_72995_K) {
                if (this.shootingEntity == null || this.ticksAlive > this.duration) {
                    this.func_70106_y();
                } else {
                    this.shoot(this.power);
                    if (this.ticksAlive > 5) {
                        this.beam.execute(this.shootingEntity, this.getBeamLength(), this.power * 0.35f);
                    }
                }
            }
        }

        public void func_70106_y() {
            super.func_70106_y();
            if (!this.field_70170_p.field_72995_K && this.getShooter() != null) {
                Jutsu.deactivateCleanup(this.getShooter());
            }
        }

        public static class Jutsu
        implements ItemJutsu.IJutsuCallback {
            private static final String ID_KEY = "MultiCannonActivated";

            @Override
            public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
                if (ItemSenninka.STAGE2.jutsu.isActivated(stack) && power >= this.getBasePower()) {
                    EntityMultiCannon jutsuEntity = new EntityMultiCannon(entity, power);
                    entity.field_70170_p.func_72838_d((Entity)jutsuEntity);
                    stack.func_77978_p().func_74757_a(ID_KEY, true);
                    entity.field_70170_p.func_184148_a(null, entity.field_70165_t, entity.field_70163_u + 2.0, entity.field_70161_v, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:explosion")), SoundCategory.BLOCKS, 5.0f, 1.0f);
                    return true;
                }
                Jutsu.deactivateCleanup(entity);
                return false;
            }

            @Override
            public float getBasePower() {
                return 10.0f;
            }

            @Override
            public float getPowerupDelay() {
                return 20.0f;
            }

            @Override
            public float getMaxPower() {
                return 40.0f;
            }

            @Override
            public boolean isActivated(ItemStack stack) {
                return stack.func_77942_o() ? stack.func_77978_p().func_74767_n(ID_KEY) : false;
            }

            public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
                return this.isActivated(stack) || ItemSenninka.STAGE2.jutsu.isActivated(stack) && ItemJutsu.getCurrentJutsu(stack) == CANNON && entity.func_184607_cu().equals(stack) && ((ItemJutsu.Base)stack.func_77973_b()).getPower(stack, entity, entity.func_184605_cv()) >= this.getBasePower();
            }

            protected static void deactivateCleanup(EntityLivingBase entity) {
                ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, block);
                if (stack != null && stack.func_77942_o()) {
                    stack.func_77978_p().func_82580_o(ID_KEY);
                    ItemJutsu.setJutsuCooldown(stack, entity, CANNON, 400L);
                }
            }
        }

        public class AirPunch
        extends ProcedureAirPunch {
            public AirPunch() {
                this.blockDropChance = -1.0f;
                this.particlesPre = null;
            }

            @Override
            protected void attackEntityFrom(Entity player, Entity target) {
                target.field_70172_ad = 10;
                target.func_70097_a(ItemJutsu.causeJutsuDamage(EntityMultiCannon.this, player), EntityMultiCannon.this.power * 0.25f);
            }

            @Override
            @Nullable
            protected EntityItem processAffectedBlock(Entity player, BlockPos pos, EnumFacing facing) {
                if (EntityMultiCannon.this.field_70146_Z.nextFloat() < 0.005f) {
                    player.field_70170_p.func_184133_a(null, pos, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:explosion")), SoundCategory.BLOCKS, 4.0f, EntityMultiCannon.this.field_70146_Z.nextFloat() * 0.5f + 0.75f);
                }
                return super.processAffectedBlock(player, pos, facing);
            }

            @Override
            protected void breakBlockParticles(World world, BlockPos pos) {
                Particles.spawnParticle(world, Particles.Types.SMOKE, (double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + 0.5, (double)pos.func_177952_p() + 0.5, 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Integer.MIN_VALUE, 40 + world.field_73012_v.nextInt(30));
            }

            @Override
            protected float getBreakChance(BlockPos pos, Entity player, double range) {
                return (1.0f - (float)((double)MathHelper.func_76133_a((double)player.func_174831_c(pos)) / range)) * 0.05f;
            }
        }
    }

    public static class Stage2
    extends SenninkaJutsu {
        private final String idKey = "Stage2StackKey";
        private final Map<IAttribute, AttributeModifier> buffMap = ImmutableMap.builder().put((Object)SharedMonsterAttributes.field_111264_e, (Object)new AttributeModifier(ItemSenjutsu.ATTACK_DAMAGE_MODIFIER, "senninka.damage", 60.0, 0)).put((Object)SharedMonsterAttributes.field_188790_f, (Object)new AttributeModifier(ItemSenjutsu.ATTACK_SPEED_MODIFIER, "senninka.damagespeed", 2.0, 1)).put((Object)SharedMonsterAttributes.field_111263_d, (Object)new AttributeModifier(ItemSenjutsu.MOVEMENT_SPEED_MODIFIER, "senninka.movement", 1.8, 1)).build();

        @Override
        public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            if (!this.isActivated(stack)) {
                entity.field_70170_p.func_184148_a(null, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 1.0f, 0.8f);
                ItemSenninka.PISTONFIST.jutsu.deactivate(entity);
                stack.func_77978_p().func_74757_a(this.idKey, true);
                for (Map.Entry<IAttribute, AttributeModifier> entry : this.buffMap.entrySet()) {
                    IAttributeInstance attr = entity.func_110148_a(entry.getKey());
                    if (attr == null || attr.func_180374_a(entry.getValue())) continue;
                    attr.func_111121_a(entry.getValue());
                }
                return true;
            }
            this.deactivate(entity);
            return false;
        }

        @Override
        public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
            if (entity instanceof EntityLivingBase && !world.field_72995_K) {
                if (this.isActivated(itemstack)) {
                    int weartime;
                    EntityLivingBase living = (EntityLivingBase)entity;
                    if (entity.field_70173_aa % 20 == 3) {
                        living.func_70690_d(new PotionEffect(MobEffects.field_76430_j, 22, 8, false, false));
                        living.func_70690_d(new PotionEffect(MobEffects.field_76429_m, 22, 2, false, false));
                    }
                    if (!((weartime = entity.getEntityData().func_74762_e(ItemSenninka.START_TIME)) <= 40 || entity instanceof EntityPlayer && ((EntityPlayer)entity).func_175149_v())) {
                        Stage2.renderExhaust(living);
                    }
                    if (entity instanceof EntityPlayer && weartime > (int)(((RangedItem)itemstack.func_77973_b()).getXpRatio(itemstack, STAGE2) * 300.0f)) {
                        this.spawnClone((EntityPlayer)entity, itemstack);
                    }
                    if (ItemSenninka.CANNON.jutsu.isActivated(itemstack) && living.func_184612_cw() % 20 == 1) {
                        entity.field_70170_p.func_184148_a(null, entity.field_70165_t, entity.field_70163_u + 2.0, entity.field_70161_v, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:blast_charge")), SoundCategory.PLAYERS, 2.0f, 1.0f + (float)living.func_184612_cw() / 20.0f * 0.1f);
                    }
                    if (!((RangedItem)itemstack.func_77973_b()).isJutsuEnabled(itemstack, CANNON)) {
                        ((RangedItem)itemstack.func_77973_b()).enableJutsu(itemstack, CANNON, true);
                    }
                    ProcedureSync.EntityNBTTag.setAndSync(entity, ItemSenninka.START_TIME, weartime + 1);
                } else if (((RangedItem)itemstack.func_77973_b()).isJutsuEnabled(itemstack, CANNON)) {
                    ((RangedItem)itemstack.func_77973_b()).enableJutsu(itemstack, CANNON, false);
                }
            }
        }

        public static void renderExhaust(EntityLivingBase living) {
            Vec3d vec = new Vec3d(-0.365625, 0.884375, -0.440625).func_178785_b(-living.field_70761_aq * (float)Math.PI / 180.0f).func_178787_e(living.func_174791_d());
            Vec3d vec1 = new Vec3d(-0.25, -0.0625, -0.25).func_186678_a(1.4).func_178785_b(-living.field_70761_aq * (float)Math.PI / 180.0f);
            Particles.spawnParticle(living.field_70170_p, Particles.Types.SMOKE, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c, 20, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0x20FFFFFF, 10, 3, 240);
            vec = new Vec3d(-0.365625, 0.61875, -0.409375).func_178785_b(-living.field_70761_aq * (float)Math.PI / 180.0f).func_178787_e(living.func_174791_d());
            Particles.spawnParticle(living.field_70170_p, Particles.Types.SMOKE, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c, 20, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0x20FFFFFF, 10, 3, 240);
            vec = new Vec3d(0.365625, 0.884375, -0.440625).func_178785_b(-living.field_70761_aq * (float)Math.PI / 180.0f).func_178787_e(living.func_174791_d());
            vec1 = new Vec3d(0.25, -0.0625, -0.25).func_186678_a(1.4).func_178785_b(-living.field_70761_aq * (float)Math.PI / 180.0f);
            Particles.spawnParticle(living.field_70170_p, Particles.Types.SMOKE, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c, 20, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0x20FFFFFF, 10, 3, 240);
            vec = new Vec3d(0.365625, 0.61875, -0.409375).func_178785_b(-living.field_70761_aq * (float)Math.PI / 180.0f).func_178787_e(living.func_174791_d());
            Particles.spawnParticle(living.field_70170_p, Particles.Types.SMOKE, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c, 20, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0x20FFFFFF, 10, 3, 240);
        }

        private void setClone(ItemStack itemstack, EntitySenninkaClone.EntityCustom clone) {
            itemstack.func_77978_p().func_74768_a("CloneID", clone.func_145782_y());
        }

        @Nullable
        private static EntitySenninkaClone.EntityCustom getClone(World world, ItemStack itemstack) {
            if (Stage2.hasClone(itemstack)) {
                Entity entity = world.func_73045_a(itemstack.func_77978_p().func_74762_e("CloneID"));
                return entity instanceof EntitySenninkaClone.EntityCustom ? (EntitySenninkaClone.EntityCustom)entity : null;
            }
            return null;
        }

        private static int getCloneId(ItemStack stack) {
            return stack.func_77942_o() && stack.func_77978_p().func_74764_b("CloneID") ? stack.func_77978_p().func_74762_e("CloneID") : -1;
        }

        private static boolean hasClone(ItemStack stack) {
            return Stage2.getCloneId(stack) > 0;
        }

        private void spawnClone(EntityPlayer original, ItemStack stack) {
            if (!original.field_70170_p.field_72995_K && !Stage2.hasClone(stack)) {
                EntitySenninkaClone.EntityCustom entity = new EntitySenninkaClone.EntityCustom((EntityLivingBase)original);
                original.field_70170_p.func_72838_d((Entity)entity);
                this.setClone(stack, entity);
            }
        }

        public static void revertOriginal(EntityPlayer player, ItemStack stack) {
            EntitySenninkaClone.EntityCustom clone = Stage2.getClone(player.field_70170_p, stack);
            if (clone != null) {
                clone.func_70106_y();
                stack.func_77978_p().func_82580_o("CloneID");
            }
        }

        @Override
        public void onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
            if (attacker.equals((Object)target)) {
                target = ProcedureUtils.objectEntityLookingAt((Entity)attacker, (double)16.0, (double)3.0).field_72308_g;
                if (target instanceof EntityLivingBase) {
                    attacker.func_71059_n(target);
                }
            } else if (target instanceof EntityLivingBase) {
                target.field_70170_p.func_184148_a(null, attacker.field_70165_t, attacker.field_70163_u, attacker.field_70161_v, SoundEvents.field_187539_bB, SoundCategory.NEUTRAL, 1.0f, attacker.func_70681_au().nextFloat() * 0.5f + 0.5f);
                Vec3d vec = target.func_174791_d().func_178788_d(attacker.func_174791_d()).func_72432_b();
                Particles.Renderer particles = new Particles.Renderer(attacker.field_70170_p);
                int j = 25;
                for (int i = 1; i <= j; ++i) {
                    Vec3d vec1 = vec.func_186678_a(-0.06 * (double)i);
                    particles.spawnParticles(Particles.Types.SONIC_BOOM, attacker.field_70165_t, attacker.field_70163_u + 1.4, attacker.field_70161_v, 1, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0xFFFFFF | (int)((1.0f - (float)i / (float)j) * 64.0f) << 24, i * 2, (int)(5.0f * (1.0f + (float)i / (float)j * 0.5f)));
                }
                particles.send();
                attacker.field_70177_z = ProcedureUtils.getYawFromVec(vec);
                attacker.field_70125_A = ProcedureUtils.getPitchFromVec(vec);
                attacker.func_70634_a(target.field_70165_t - vec.field_72450_a, target.field_70163_u - vec.field_72448_b + 0.5, target.field_70161_v - vec.field_72449_c);
            }
        }

        @Override
        @SideOnly(value=Side.CLIENT)
        public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
            if (this.isActivated(stack)) {
                model.func_178719_a(false);
                model.field_78116_c.field_78806_j = true;
                model.field_178720_f.field_78806_j = true;
                ((Renderer.ModelJugo)model).headStage0.field_78806_j = true;
                ((Renderer.ModelJugo)model).headStage2.field_78806_j = true;
                model.field_178723_h.field_78806_j = true;
                model.field_178724_i.field_78806_j = true;
                ((Renderer.ModelJugo)model).armExhaust.field_78806_j = true;
                ((Renderer.ModelJugo)model).rightArmSpikes.field_78795_f = 0.0f;
                model.field_78115_e.field_78806_j = true;
                ((Renderer.ModelJugo)model).bodyStage2.field_78806_j = true;
                if (((EntityMultiCannon.Jutsu)ItemSenninka.CANNON.jutsu).isActivated(living, stack)) {
                    ((Renderer.ModelJugo)model).exhaustExtension1.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension2.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension3.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension4.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension5.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension6.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension7.field_78806_j = true;
                    ((Renderer.ModelJugo)model).exhaustExtension8.field_78806_j = true;
                    ((Renderer.ModelJugo)model).blasts.field_78806_j = true;
                }
                if (((Absorption)ItemSenninka.ABSORB.jutsu).isActivated(living, stack)) {
                    ((Absorption)ItemSenninka.ABSORB.jutsu).showNeedle(living, model);
                }
                model.field_78117_n = living.func_70093_af();
                model.field_78093_q = living.func_184218_aH();
                model.field_78091_s = living.func_70631_g_();
                return true;
            }
            return false;
        }

        @Override
        public boolean isActivated(ItemStack stack) {
            return stack.func_77942_o() && stack.func_77978_p().func_74767_n(this.idKey);
        }

        @Override
        public void deactivate(EntityLivingBase entity) {
            for (Map.Entry<IAttribute, AttributeModifier> entry : this.buffMap.entrySet()) {
                IAttributeInstance attr = entity.func_110148_a(entry.getKey());
                if (attr == null || !attr.func_180374_a(entry.getValue())) continue;
                attr.func_111124_b(entry.getValue());
            }
            ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, block);
            if (stack != null) {
                if (entity instanceof EntityPlayer) {
                    Stage2.revertOriginal((EntityPlayer)entity, stack);
                }
                stack.func_77978_p().func_82580_o(this.idKey);
                ItemJutsu.setJutsuCooldown(stack, entity, STAGE2, 400L);
            }
            ProcedureSync.EntityNBTTag.removeAndSync((Entity)entity, ItemSenninka.START_TIME);
        }
    }

    public static class PistonFist
    extends SenninkaJutsu {
        private final String idKey = "PistonFistStackKey";
        private final Map<IAttribute, AttributeModifier> buffMap = ImmutableMap.builder().put((Object)SharedMonsterAttributes.field_111264_e, (Object)new AttributeModifier(ItemSenjutsu.ATTACK_DAMAGE_MODIFIER, "senninka.damage", 50.0, 0)).put((Object)SharedMonsterAttributes.field_111263_d, (Object)new AttributeModifier(ItemSenjutsu.MOVEMENT_SPEED_MODIFIER, "senninka.movement", 1.5, 1)).build();

        @Override
        public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            if (!this.isActivated(stack)) {
                entity.field_70170_p.func_184148_a(null, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 1.0f, 0.8f);
                ItemSenninka.STAGE2.jutsu.deactivate(entity);
                stack.func_77978_p().func_74757_a(this.idKey, true);
                for (Map.Entry<IAttribute, AttributeModifier> entry : this.buffMap.entrySet()) {
                    IAttributeInstance attr = entity.func_110148_a(entry.getKey());
                    if (attr == null || attr.func_180374_a(entry.getValue())) continue;
                    attr.func_111121_a(entry.getValue());
                }
                return true;
            }
            this.deactivate(entity);
            return false;
        }

        @Override
        public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
            if (!world.field_72995_K && this.isActivated(itemstack)) {
                int ticks = entity.getEntityData().func_74762_e(ItemSenninka.START_TIME);
                ProcedureSync.EntityNBTTag.setAndSync(entity, ItemSenninka.START_TIME, ticks + 1);
                if (entity instanceof EntityLivingBase && ticks > (int)(((RangedItem)itemstack.func_77973_b()).getXpRatio(itemstack, PISTONFIST) * 300.0f)) {
                    this.deactivate((EntityLivingBase)entity);
                }
            }
        }

        @Override
        public void onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
            if (attacker.equals((Object)target)) {
                target = ProcedureUtils.objectEntityLookingAt((Entity)attacker, (double)16.0, (double)3.0).field_72308_g;
                if (target instanceof EntityLivingBase) {
                    attacker.func_71059_n(target);
                }
            } else if (target instanceof EntityLivingBase) {
                target.field_70170_p.func_184148_a(null, attacker.field_70165_t, attacker.field_70163_u, attacker.field_70161_v, SoundEvents.field_187539_bB, SoundCategory.NEUTRAL, 1.0f, attacker.func_70681_au().nextFloat() * 0.5f + 0.5f);
                Vec3d vec = target.func_174791_d().func_178788_d(attacker.func_174791_d()).func_72432_b();
                Particles.Renderer particles = new Particles.Renderer(attacker.field_70170_p);
                int j = 25;
                for (int i = 1; i <= j; ++i) {
                    Vec3d vec1 = vec.func_186678_a(-0.06 * (double)i);
                    particles.spawnParticles(Particles.Types.SONIC_BOOM, attacker.field_70165_t, attacker.field_70163_u + 1.4, attacker.field_70161_v, 1, 0.0, 0.0, 0.0, vec1.field_72450_a, vec1.field_72448_b, vec1.field_72449_c, 0xFFFFFF | (int)((1.0f - (float)i / (float)j) * 64.0f) << 24, i * 2, (int)(5.0f * (1.0f + (float)i / (float)j * 0.5f)));
                }
                particles.send();
                attacker.field_70177_z = ProcedureUtils.getYawFromVec(vec);
                attacker.field_70125_A = ProcedureUtils.getPitchFromVec(vec);
                attacker.func_70634_a(target.field_70165_t - vec.field_72450_a, target.field_70163_u - vec.field_72448_b + 0.5, target.field_70161_v - vec.field_72449_c);
            }
        }

        @Override
        @SideOnly(value=Side.CLIENT)
        public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
            if (this.isActivated(stack)) {
                model.func_178719_a(false);
                model.field_78116_c.field_78806_j = true;
                ((Renderer.ModelJugo)model).headStage1.field_78806_j = true;
                model.field_178723_h.field_78806_j = true;
                ((Renderer.ModelJugo)model).armExhaust.field_78806_j = true;
                ((Renderer.ModelJugo)model).rightArmSpikes.field_78795_f = 0.0f;
                model.field_78115_e.field_78806_j = true;
                ((Renderer.ModelJugo)model).bodyStage1.field_78806_j = true;
                model.field_78117_n = living.func_70093_af();
                model.field_78093_q = living.func_184218_aH();
                model.field_78091_s = living.func_70631_g_();
                if (ItemJutsu.getCurrentJutsu(stack) == ABSORB && living.func_184587_cr()) {
                    ((Absorption)ItemSenninka.ABSORB.jutsu).showNeedle(living, model);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean isActivated(ItemStack stack) {
            return stack.func_77942_o() && stack.func_77978_p().func_74767_n(this.idKey);
        }

        @Override
        public void deactivate(EntityLivingBase entity) {
            for (Map.Entry<IAttribute, AttributeModifier> entry : this.buffMap.entrySet()) {
                IAttributeInstance attr = entity.func_110148_a(entry.getKey());
                if (attr == null || !attr.func_180374_a(entry.getValue())) continue;
                attr.func_111124_b(entry.getValue());
            }
            ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, block);
            if (stack != null) {
                stack.func_77978_p().func_82580_o(this.idKey);
                ItemJutsu.setJutsuCooldown(stack, entity, PISTONFIST, 300L);
            }
            ProcedureSync.EntityNBTTag.removeAndSync((Entity)entity, ItemSenninka.START_TIME);
        }
    }

    public static class Broadaxe
    extends SenninkaJutsu {
        @Override
        public boolean createJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            if (entity instanceof EntityPlayer && !ProcedureUtils.hasItemInInventory((EntityPlayer)entity, ItemSenninkaBroadaxe.block)) {
                entity.field_70170_p.func_184148_a(null, entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, (SoundEvent)SoundEvent.field_187505_a.func_82594_a((Object)new ResourceLocation("narutomod:woodgrow")), SoundCategory.PLAYERS, 1.0f, 0.8f);
                ItemStack itemstack = new ItemStack(ItemSenninkaBroadaxe.block);
                ProcedureUtils.swapItemToSlot((EntityPlayer)entity, EntityEquipmentSlot.MAINHAND, itemstack);
                return true;
            }
            return false;
        }

        @Override
        public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
            if (!world.field_72995_K && entity instanceof EntityLivingBase && !this.anyOtherActivated((EntityLivingBase)entity, stack)) {
                if (this.isActivated((EntityLivingBase)entity, stack)) {
                    ProcedureSync.EntityNBTTag.setAndSync(entity, ItemSenninka.START_TIME, entity.getEntityData().func_74762_e(ItemSenninka.START_TIME) + 1);
                } else if (entity.getEntityData().func_74764_b(ItemSenninka.START_TIME)) {
                    ProcedureSync.EntityNBTTag.removeAndSync(entity, ItemSenninka.START_TIME);
                }
            }
        }

        @Override
        public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
            return entity.func_184614_ca().func_77973_b() == ItemSenninkaBroadaxe.block;
        }

        @Override
        public void deactivate(EntityLivingBase entity) {
            if (!entity.field_70170_p.field_72995_K && entity instanceof EntityPlayer) {
                ((EntityPlayer)entity).field_71071_by.func_174925_a(ItemSenninkaBroadaxe.block, -1, -1, null);
            }
        }

        @Override
        @SideOnly(value=Side.CLIENT)
        public boolean setModelVisibility(EntityLivingBase living, ItemStack stack, Renderer.ModelJugo model) {
            if (living.func_184614_ca().func_77973_b() == ItemSenninkaBroadaxe.block && !ItemSenninka.PISTONFIST.jutsu.isActivated(stack) && !ItemSenninka.STAGE2.jutsu.isActivated(stack)) {
                model.func_178719_a(false);
                model.field_78116_c.field_78806_j = true;
                ((Renderer.ModelJugo)model).headStage1.field_78806_j = true;
                model.field_178723_h.field_78806_j = true;
                ((Renderer.ModelJugo)model).rightArmSpikes.field_78795_f = 3.1416f;
                model.field_78115_e.field_78806_j = true;
                ((Renderer.ModelJugo)model).bodyStage1.field_78806_j = true;
                model.field_78117_n = living.func_70093_af();
                model.field_78093_q = living.func_184218_aH();
                model.field_78091_s = living.func_70631_g_();
                return true;
            }
            return false;
        }
    }

    public static abstract class SenninkaJutsu
    implements ItemJutsu.IJutsuCallback {
        private static final List<SenninkaJutsu> list = Lists.newArrayList();

        public SenninkaJutsu() {
            list.add(this);
        }

        public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
        }

        public void onLeftClickEntity(ItemStack stack, EntityPlayer attacker, Entity target) {
        }

        @SideOnly(value=Side.CLIENT)
        public abstract boolean setModelVisibility(EntityLivingBase var1, ItemStack var2, Renderer.ModelJugo var3);

        public boolean isActivated(EntityLivingBase entity, ItemStack stack) {
            return this.isActivated(stack);
        }

        public boolean anyOtherActivated(EntityLivingBase entity, ItemStack stack) {
            for (SenninkaJutsu jutsu : list) {
                if (jutsu == this || !jutsu.isActivated(entity, stack)) continue;
                return true;
            }
            return false;
        }

        public static void deactivateAll(EntityLivingBase entity) {
            ItemStack stack = ProcedureUtils.getMatchingItemStack(entity, block);
            if (stack != null) {
                for (SenninkaJutsu jutsu : list) {
                    if (!jutsu.isActivated(entity, stack)) continue;
                    jutsu.deactivate(entity);
                }
            }
        }
    }

    public static class RangedItem
    extends ItemJutsu.Base
    implements ItemOnBody.Interface {
        @SideOnly(value=Side.CLIENT)
        private ModelBiped armorModel;

        public RangedItem(ItemJutsu.JutsuEnum ... list) {
            super(ItemJutsu.JutsuEnum.Type.SENNINKA, list);
            this.func_77655_b("senninka");
            this.setRegistryName("senninka");
            this.func_77637_a(TabModTab.tab);
            this.defaultCooldownMap[ItemSenninka.BROADAXE.index] = 0L;
            this.defaultCooldownMap[ItemSenninka.PISTONFIST.index] = 0L;
            this.defaultCooldownMap[ItemSenninka.STAGE2.index] = 0L;
            this.defaultCooldownMap[ItemSenninka.ABSORB.index] = 0L;
        }

        @Override
        public boolean executeJutsu(ItemStack stack, EntityLivingBase entity, float power) {
            if (ItemSenjutsu.isSageModeActivated(entity)) {
                ItemSenjutsu.deactivateSageMode(entity);
            }
            return super.executeJutsu(stack, entity, power);
        }

        @Override
        public void func_77663_a(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
            super.func_77663_a(itemstack, world, entity, par4, par5);
            if (entity instanceof EntityLivingBase) {
                for (ItemJutsu.JutsuEnum jutsuEnum : this.getAllJutsus(itemstack)) {
                    if (!(jutsuEnum.jutsu instanceof SenninkaJutsu) || !((RangedItem)itemstack.func_77973_b()).canUseJutsu(itemstack, jutsuEnum, (EntityLivingBase)entity)) continue;
                    ((SenninkaJutsu)jutsuEnum.jutsu).onUpdate(itemstack, world, entity, par4, par5);
                }
            }
        }

        public boolean onLeftClickEntity(ItemStack itemstack, EntityPlayer attacker, Entity target) {
            for (ItemJutsu.JutsuEnum jutsuEnum : this.getAllJutsus(itemstack)) {
                if (!(jutsuEnum.jutsu instanceof SenninkaJutsu) || !((RangedItem)itemstack.func_77973_b()).canUseJutsu(itemstack, jutsuEnum, (EntityLivingBase)attacker) || !jutsuEnum.jutsu.isActivated(itemstack)) continue;
                ((SenninkaJutsu)jutsuEnum.jutsu).onLeftClickEntity(itemstack, attacker, target);
            }
            return super.onLeftClickEntity(itemstack, attacker, target);
        }

        @SideOnly(value=Side.CLIENT)
        public ModelBiped getArmorModel(EntityLivingBase living, ItemStack stack, EntityEquipmentSlot slot, ModelBiped defaultModel) {
            if (this.armorModel == null) {
                this.armorModel = new Renderer.ModelJugo();
            }
            for (ItemJutsu.JutsuEnum jutsuEnum : this.getAllJutsus(stack)) {
                if (!(jutsuEnum.jutsu instanceof SenninkaJutsu) || !((SenninkaJutsu)jutsuEnum.jutsu).setModelVisibility(living, stack, (Renderer.ModelJugo)this.armorModel)) continue;
                return this.armorModel;
            }
            return null;
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
            return "narutomod:textures/jugo.png";
        }

        @Override
        public boolean showSkinLayer() {
            return true;
        }

        @Override
        public ItemOnBody.BodyPart showOnBody() {
            return ItemOnBody.BodyPart.NONE;
        }
    }
}

