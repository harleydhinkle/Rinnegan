package net.narutomod.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ICommand;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@ElementsNarutomodMod.ModElement.Tag
public class CommandGrantKg extends ElementsNarutomodMod.ModElement {
    public CommandGrantKg(ElementsNarutomodMod instance) {
        super(instance, 0);
    }
    @Override
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandHandler());
    }
    public static class CommandHandler implements ICommand {
        @Override
        public int compareTo(ICommand c) {
            return getName().compareTo(c.getName());
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender var1) {
            return var1.canUseCommand(4,this.getName());
        }

        @Override
        public List getAliases() {return new ArrayList<>();}

        @Override
        public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
            if (args.length == 1) {
                return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            }
            else if (args.length == 2){
                return Kgs.getAllCommands();
            }
            return new ArrayList();
        }

        @Override
        public boolean isUsernameIndex(String[] string, int index) {
            return true;
        }

        @Override
        public String getName() {
            return "grantkg";
        }

        @Override
        public String getUsage(ICommandSender var1) {
            return "/grantkg [<arguments>]";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] cmd){
            String username = "";
            username = (String) (new Object(){
                public String getText() {
                    String param = (String) cmd[0];
                    if (param != null) {
                        return param;
                    }
                    return "";
                }

            }.getText());
            EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);

            String kg = "";
            kg = (String) (new Object(){
                public String getText() {
                    String param = (String) cmd[1];
                    if (param != null) {
                        return param;
                    }
                    return "";
                }

            }.getText());
            ItemStack stack = ItemStack.EMPTY;
            if (kg.equals("sharingan")) {
                //Sharingan
                stack = new ItemStack(ItemSharingan.helmet, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemOcularJutsu.Base) stack.getItem()).setOwner(stack, player);
                Advancement adv = ((MinecraftServer) player.mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:sharinganopened"));
                AdvancementProgress ap = player.getAdvancements().getProgress(adv);
                for (String crit : ap.getRemaningCriteria()) {
                    player.getAdvancements().grantCriterion(adv, crit);
                }
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("byakugan")) {
                //Byakugan
                stack = new ItemStack(ItemByakugan.helmet, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemOcularJutsu.Base) stack.getItem()).setOwner(stack, player);
                Advancement adv = ((MinecraftServer) player.mcServer).getAdvancementManager().getAdvancement(new ResourceLocation("narutomod:byakuganopened"));
                AdvancementProgress ap = player.getAdvancements().getProgress(adv);
                for (String crit : ap.getRemaningCriteria()) {
                    player.getAdvancements().grantCriterion(adv, crit);
                }
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("bone")) {
                //bone
                stack = new ItemStack(ItemShikotsumyaku.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("lava")) {
                //lava
                stack = new ItemStack(ItemLavaRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("scorch")) {
                //scorch
                stack = new ItemStack(ItemScorchRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("ice")) {
                //ice
                stack = new ItemStack(ItemIceRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("magnet")) {
                //magnet
                stack = new ItemStack(ItemMagnetRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("explosion")) {
                //explosion
                stack = new ItemStack(ItemExplosionRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("storm")) {
                //storm
                stack = new ItemStack(ItemStormRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("boil")) {
                //boil
                stack = new ItemStack(ItemWindRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("wood")) {
                //wood
                stack = new ItemStack(ItemWoodRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("dust")) {
                //dust
                stack = new ItemStack(ItemDustRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("crystal")) {
                stack = new ItemStack(ItemCrystalRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("eightgates")) {
                stack = new ItemStack(ItemEightGates.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);

            } else if (kg.equals("blood")) {
                stack = new ItemStack(ItemBloodRelease.block, (int) (1));
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ((ItemJutsu.Base) stack.getItem()).setOwner(stack, player);
                ((ItemJutsu.Base) stack.getItem()).setIsAffinity(stack, true);
                player.getEntityData().setBoolean("firstkg", false);
            }

            }

        public enum Kgs {
            SHARINGAN("sharingan"),
            BYAKUGAN("byakugan"),
            BONE("bone"),
            LAVA("lava"),
            SCORCH("scorch"),
            ICE("ice"),
            MAGNET("magnet"),
            EXPLOSION("explosion"),
            STORM("storm"),
            BOIL("boil"),
            WOOD("wood"),
            DUST("dust"),
            CRYSTAL("crystal"),
            EIGHTGATES("eightgates"),
            BLOOD("blood"),
            UNKNOWN;

            private final String argString;
            private static final Map<String, Kgs> COMMANDS = Maps.newHashMap();

            static {
                for (Kgs cmd : values()) {
                    if (cmd.argString != null) {
                        COMMANDS.put(cmd.argString, cmd);
                    }
                }
            }

            Kgs() {
                this.argString = null;
            }

            Kgs(String str) {
                this.argString = str;
            }

            public String toString() {
                return this.argString;
            }

            public static Kgs getTypeFromString(String str) {
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
