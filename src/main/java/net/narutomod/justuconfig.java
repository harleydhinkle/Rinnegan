package net.narutomod;
import net.minecraftforge.common.config.Config;
@Config(modid = NarutomodMod.MODID,name = "justuconfig")
@ElementsNarutomodMod.ModElement.Tag
public class justuconfig extends ElementsNarutomodMod.ModElement {

    @Config.Comment("how many you want the player to teleport with Flying Thunder God Jutsu.")
    public static double Flying_Thunder_God_distents = 200.0D;

    @Config.Comment("Maximum damage Jinton (beam & cube) can deal per hit. Set to 0 or negative to disable this per-hit cap.")
    public static double JINTON_MAX_DAMAGE = 0.0D;

    @Config.Comment("Maximum TOTAL damage Jinton BEAM can deal across its entire lifetime (overall cap). Set to 0 or negative to disable the total cap.")
    public static double JINTON_MAX_TOTAL_DAMAGE = 10000.0D;

    @Config.Comment("Overall multiplier for Jinton damage (beam & cube). 1.0 = normal, 0.5 = half, 2.0 = double.")
    public static double JINTON_DAMAGE_MULT = 1.0D;

    @Config.Comment("damage for scorch how much damage each scorch balls do ")
    public static float scorch_damage = 6.0F;

    @Config.Comment("how much damage blood chains do ")
    public static float bloodchainsdamage = 6.0F;

    @Config.Comment("how much heal from blood chains it will all ways deviode by 2 inless you want it to hell the same damage as the damage you can all so rase or lower how much it devids by  ")
    public static float bloodchainsheal = bloodchainsdamage/2;

    @Config.Comment("RESISTANCE_strangth what strangth does the RESISTANCE lighting chacra mode give")
    public static int lighting_RESISTANCE_strangth = 2;

    @Config.Comment("SPEED_strangth what strangth does the SPEEDlighting chacra mode give")
    public static int lighting_SPEED_strangth = 10;

    @Config.Comment("JUMP_BOOST what strangth does the JUMP_BOOST lighting chacra mode give")
    public static int lighting_JUMP_BOOST_strangth = 5;

    @Config.Comment("Damage C3 (Base is 200)")
    public static float damage_c3 = 200.0f;

    @Config.Comment("Damage C4 [Damage is base on tick](Base is 2) ")
    public static int damage_c4 = 2;

    public justuconfig(ElementsNarutomodMod instance) {super(instance, 998);}

}
