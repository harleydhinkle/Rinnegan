
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
    
