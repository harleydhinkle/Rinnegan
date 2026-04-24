package net.narutomod.command;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.narutomod.ClanConfig;
import net.narutomod.ElementsNarutomodMod;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.Entity;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommand;
import net.minecraft.command.CommandHandler;
import net.narutomod.procedure.ProcedureUtils;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@ElementsNarutomodMod.ModElement.Tag
public class CommandClan extends ElementsNarutomodMod.ModElement {
    public CommandClan(ElementsNarutomodMod instance) {super(instance,3248);}

    @Override
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHandler());
    }
    public static class CommandHandler implements ICommand {
        @Override
        public int compareTo(ICommand c) {return getName().compareTo(c.getName());}

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender.canUseCommand(4, this.getName());
        }

        @Override
        public List getAliases() {return new ArrayList<>();}


        @Override
        public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1){
                return Options.getAllCommands();
            }
            else if (args.length == 2){
                return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            }
            else if (args.length == 3 && args[0].equalsIgnoreCase(Options.SET.toString())){
                return Clans.getAllCommands();
            }
            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "rinneganclan";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/rinneganclan [set/reroll/clear/check] [name]";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
            Entity entity = sender.getCommandSenderEntity();
            String username = "";
            username = (String) (new Object() {
                public String getText() {
                    String param = (String) cmd[1];
                    if (param != null) {
                        return param;
                    }
                    return "";
                }
            }.getText());

            EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);
            if (cmd.length < 1) {
                ProcedureUtils.sendChat((EntityPlayer)entity, this.getUsage(sender));
            } else if (cmd[0].equalsIgnoreCase("set")) {

                if (cmd[2].equalsIgnoreCase("uchiha")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","uchiha");
                }
                else if (cmd[2].equalsIgnoreCase("uzumaki")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","uzumaki");
                }
                else if (cmd[2].equalsIgnoreCase("hyuga")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","hyuga");
                }
                else if (cmd[2].equalsIgnoreCase("senju")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","senju");
                }
                else if (cmd[2].equalsIgnoreCase("nara")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","nara");
                }
                else if (cmd[2].equalsIgnoreCase("akimichi")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","akimichi");
                }
                else if (cmd[2].equalsIgnoreCase("yamanaka")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","yamanaka");
                }
                else if (cmd[2].equalsIgnoreCase("aburame")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","aburame");
                }
                else if (cmd[2].equalsIgnoreCase("inuzuka")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","inuzuka");
                }
                else if (cmd[2].equalsIgnoreCase("kaguya")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","kaguya");
                }
                else if (cmd[2].equalsIgnoreCase("hozuki")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","hozuki");
                }
                else if (cmd[2].equalsIgnoreCase("sarutobi")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","sarutobi");
                }
                else if (cmd[2].equalsIgnoreCase("kazekage")) {
                    player.getEntityData().setString("clan","");
                    player.getEntityData().setString("clan","kazekage");
                }
                ProcedureUtils.sendChat((EntityPlayer)entity, String.format("%s's clan set to %s", player.getName(), player.getEntityData().getString("clan")));
            } else if (cmd[0].equalsIgnoreCase("clear")) {
                player.getEntityData().setString("clan","");
            } else if (cmd[0].equalsIgnoreCase("reroll")) {
                int uchiha_weight = 0;
                int uzumaki_weight = 0;
                int hyuga_weight = 0;
                int senju_weight = 0;
                int nara_weight = 0;
                int akimichi_weight = 0;
                int yamanaka_weight = 0;
                int aburame_weight = 0;
                int inuzuka_weight = 0;
                int kaguya_weight = 0;
                int hozuki_weight = 0;
                int sarutobi_weight = 0;
                int kazekage_weight = 0;
                int rng = 0;

                uchiha_weight = ClanConfig.uchiha_weight_config;
                uzumaki_weight = ClanConfig.uzumaki_weight_config;
                hyuga_weight = ClanConfig.hyuga_weight_config;
                senju_weight = ClanConfig.senju_weight_config;
                nara_weight = ClanConfig.nara_weight_config;
                akimichi_weight = ClanConfig.akimichi_weight_config;
                yamanaka_weight = ClanConfig.yamanaka_weight_config;
                aburame_weight = ClanConfig.aburame_weight_config;
                inuzuka_weight = ClanConfig.inuzuka_weight_config;
                kaguya_weight = ClanConfig.kaguya_weight_config;
                hozuki_weight = ClanConfig.hozuki_weight_config;
                sarutobi_weight = ClanConfig.sarutobi_weight_config;
                kazekage_weight = ClanConfig.kazekage_weight_config;
                rng = ThreadLocalRandom.current().nextInt(1,(uchiha_weight + uzumaki_weight + hyuga_weight + senju_weight + nara_weight + akimichi_weight + yamanaka_weight + aburame_weight + inuzuka_weight + kaguya_weight + hozuki_weight + sarutobi_weight + kazekage_weight));
                player.getEntityData().setString("clan","");
                if (rng <= rng - (rng - uchiha_weight) && !(uchiha_weight == 0)) {
                    // Uchiha
                    entity.getEntityData().setString("clan","uchiha");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight) && !(uzumaki_weight == 0)) {
                    // Uzumaki
                    entity.getEntityData().setString("clan","uzumaki");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight) && !(hyuga_weight == 0)) {
                    // Hyuga
                    entity.getEntityData().setString("clan","hyuga");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight) && !(senju_weight == 0)) {
                    // Senju
                    entity.getEntityData().setString("clan","senju");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight) && !(nara_weight == 0)) {
                    // Nara
                    entity.getEntityData().setString("clan","nara");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight) && !(akimichi_weight == 0)) {
                    // Akimichi
                    entity.getEntityData().setString("clan","akimichi");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight) && !(yamanaka_weight == 0)) {
                    // Yamanaka
                    entity.getEntityData().setString("clan","yamanaka");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight) && !(aburame_weight == 0)) {
                    // Aburame
                    entity.getEntityData().setString("clan","aburame");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight) && !(inuzuka_weight == 0)) {
                    // Inuzuka
                    entity.getEntityData().setString("clan","inuzuka");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight) && !(kaguya_weight == 0)) {
                    // Kaguya
                    entity.getEntityData().setString("clan","kaguya");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight) && !(hozuki_weight == 0)) {
                    // Hozuki
                    entity.getEntityData().setString("clan","hozuki");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight - sarutobi_weight) && !(sarutobi_weight == 0)) {
                    // Sarutobi
                    entity.getEntityData().setString("clan","sarutobi");
                    entity.getEntityData().setBoolean("firstclan", false);

                } else if (rng > rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight - sarutobi_weight) && rng <= rng - (rng - uchiha_weight - uzumaki_weight - hyuga_weight - senju_weight - nara_weight - akimichi_weight - yamanaka_weight - aburame_weight - inuzuka_weight - kaguya_weight - hozuki_weight - sarutobi_weight - kazekage_weight) && !(kazekage_weight == 0)) {
                    // Kazekage
                    entity.getEntityData().setString("clan","kazekage");
                    entity.getEntityData().setBoolean("firstclan", false);
                }
                ProcedureUtils.sendChat(player, String.format("Your clan is %s", player.getEntityData().getString("clan")));
                ProcedureUtils.sendChat((EntityPlayer)entity, String.format("%s's clan rolled to %s", player.getName(), player.getEntityData().getString("clan")));
                } else if (cmd[0].equalsIgnoreCase("check")) {
                ProcedureUtils.sendChat((EntityPlayer)entity, String.format("%s's clan is %s", player.getName(), player.getEntityData().getString("clan")));
            }
        }
        public enum Clans {
            UCHIHA("uchiha"),
            UZUMAKI("uzumaki"),
            HYUGA("hyuga"),
            SENJU("senju"),
            NARA("nara"),
            AKIMICHI("akimichi"),
            YAMANAKA("yamanaka"),
            ABURAME("aburame"),
            INUZUKA("inuzuka"),
            KAGUYA("kaguya"),
            HOZUKI("hozuki"),
            SARUTOBI("sarutobi"),
            KAZEKAGE("kazekage"),
            UNKNOWN;

            private final String argString;
            private static final Map<String, Clans> COMMANDS = new HashMap<>();

            static {
                for (Clans cmd : values()) {
                    if (cmd.argString != null) {
                        COMMANDS.put(cmd.argString, cmd);
                    }
                }
            }

            Clans() {
                this.argString = null;
            }

            Clans(String str) {
                this.argString = str;
            }

            public String toString() {
                return this.argString;
            }

            public static Clans getTypeFromString(String str) {
                return COMMANDS.get(str);
            }

            public static List<String> getAllCommands() {
                List<String> list = Lists.<String>newArrayList();
                list.addAll(COMMANDS.keySet());
                return list;
            }

            public static String getAllCommandsFormatted() {
                return String.join(" | ", COMMANDS.keySet());
            }
        }

        public enum Options {
            REROLL("reroll"),
            SET("set"),
            CLEAR("clear"),
            CHECK("check"),
            UNKOWN;

            private final String argString;
            private static final Map<String, Options> COMMANDS = new HashMap<>();

            static {
                for (Options cmd : values()) {
                    if (cmd.argString != null) {
                        COMMANDS.put(cmd.argString, cmd);
                    }
                }
            }

            Options() {
                this.argString = null;
            }

            Options(String str) {
                this.argString = str;
            }

            public String toString() {
                return this.argString;
            }

            public static Options getTypeFromString(String str) {
                return COMMANDS.get(str);
            }

            public static List<String> getAllCommands() {
                List<String> list = Lists.<String>newArrayList();
                list.addAll(COMMANDS.keySet());
                return list;
            }

            public static String getAllCommandsFormatted() {
                return String.join(" | ", COMMANDS.keySet());
            }
        }
    }
}
