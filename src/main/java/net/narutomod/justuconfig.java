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

    @Config.Comment("how much heal from blood chains  ")
    public static float bloodchainsheal = 3.0F;
    
    @Config.Comment("KG Distrubution weights")
    public static int sharingan_weight_config = 3;
    public static int byakugan_weight_config = 3;
    public static int shikotsumyaku_weight_config = 3;
    public static int yooton_weight_config = 4;
    public static int shakuton_weight_config = 4;
    public static int hyoton_weight_config = 4;
    public static int jiton_weight_config = 5;
    public static int bakuton_weight_config = 4;
    public static int ranton_weight_config = 4;
    public static int futton_weight_config = 4;
    public static int kekkeitota_weight_config = 3;
    public static int jinchuriki_weight_config = 1;

    public justuconfig(ElementsNarutomodMod instance) {super(instance, 998);}

}
