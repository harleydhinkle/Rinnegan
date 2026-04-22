package net.narutomod;
import net.narutomod.ElementsNarutomodMod;
import net.minecraftforge.common.config.Config;
@Config(modid = NarutomodMod.MODID,name = "ClanConfig")
@ElementsNarutomodMod.ModElement.Tag
public class ClanConfig extends ElementsNarutomodMod.ModElement {
    public ClanConfig(ElementsNarutomodMod instance) {super(instance, 9935);}

    @Config.Comment("Clan Distrubution weights")
    public static int uchiha_weight_config = 2;
    public static int uzumaki_weight_config = 5;
    public static int hyuga_weight_config = 4;
    public static int senju_weight_config = 3;
    public static int nara_weight_config = 6;
    public static int akimichi_weight_config = 6;
    public static int yamanaka_weight_config = 5;
    public static int aburame_weight_config = 6;
    public static int inuzuka_weight_config = 2;
    public static int kaguya_weight_config = 1;
    public static int hozuki_weight_config = 2;
    public static int sarutobi_weight_config = 4;
    public static int kazekage_weight_config = 1;
    public static boolean no_clan_config = true;

}
