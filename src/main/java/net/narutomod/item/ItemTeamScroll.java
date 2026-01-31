package net.narutomod.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;

import net.narutomod.gui.GuiTeamManager;
import net.narutomod.creativetab.TabModTab;
import net.narutomod.PlayerTracker;
import net.narutomod.NarutomodMod;
import net.narutomod.ElementsNarutomodMod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Collection;
import java.util.Locale;

@ElementsNarutomodMod.ModElement.Tag
public class ItemTeamScroll extends ElementsNarutomodMod.ModElement {
    @GameRegistry.ObjectHolder("narutomod:team_scroll")
    public static final Item block = null;

    public ItemTeamScroll(ElementsNarutomodMod instance) {
        super(instance, 555);
    }

    @Override
    public void initElements() {
        elements.items.add(ItemCustom::new);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(block, 0, new ModelResourceLocation("narutomod:team_scroll", "inventory"));
    }

    public static class ItemCustom extends Item {
        public ItemCustom() {
            this.setMaxDamage(0);
            this.maxStackSize = 1;
            this.setUnlocalizedName("team_scroll");
            this.setRegistryName("team_scroll");
            this.setCreativeTab(TabModTab.tab);
        }

        @Override
        public int getItemEnchantability() {
            return 0;
        }

        @Override
        public int getMaxItemUseDuration(ItemStack itemstack) {
            return 0;
        }

        @Override
        public float getDestroySpeed(ItemStack stack, IBlockState state) {
            return 0F;
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
            ItemStack stack = player.getHeldItem(hand);
            if (player.isCreative() || PlayerTracker.isNinja(player)) {
                int x = (int) player.posX;
                int y = (int) player.posY;
                int z = (int) player.posZ;
                player.openGui(NarutomodMod.instance, GuiTeamManager.GUIID, world, x, y, z);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        @Nullable
        private static ScorePlayerTeam getTeamFromItem(World world, ItemStack stack) {
            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("teamName")) {
                return null;
            }

            Scoreboard scoreboard = world.getScoreboard();
            NBTTagCompound compound = stack.getTagCompound();

            String teamName = compound.getString("teamName");
            String teamDisplayName = compound.getString("teamDisplayName");

            ScorePlayerTeam team = scoreboard.getTeam(teamName);
            if (team == null && !world.isRemote) {
                team = scoreboard.createTeam(teamName);
            }
            if (team == null) {
                return null;
            }

            // Server only: keep scoreboard team display name in sync with the scroll
            if (!world.isRemote && teamDisplayName != null && !team.getDisplayName().equals(teamDisplayName)) {
                team.setDisplayName(teamDisplayName);
            }

            // Server only: sync the scroll's UUID list into the scoreboard membership
            if (!world.isRemote && compound.hasKey("teamMembers", 9)) {
                NBTTagList taglist = compound.getTagList("teamMembers", 10);

                int[] shouldRemove = new int[taglist.tagCount()];
                int removeCount = 0;

                for (int j = 0; j < taglist.tagCount(); ++j) {
                    NBTTagCompound memberTag = taglist.getCompoundTagAt(j);
                    if (!memberTag.hasUniqueId("memberUUID")) continue;

                    EntityPlayer member = world.getPlayerEntityByUUID(memberTag.getUniqueId("memberUUID"));
                    if (member == null) continue;

                    ScorePlayerTeam memberTeam = scoreboard.getPlayersTeam(member.getName());

                    // If the player is on no team, add them to this team.
                    if (memberTeam == null && !team.getMembershipCollection().contains(member.getName())) {
                        scoreboard.addPlayerToTeam(member.getName(), team.getName());
                    }
                    // If they're on a different team, prune them from the scroll list.
                    else if (memberTeam != null && !team.isSameTeam(memberTeam)) {
                        shouldRemove[removeCount++] = j;
                    }
                }

                if (removeCount > 0) {
                    for (int j = removeCount - 1; j >= 0; j--) {
                        taglist.removeTag(shouldRemove[j]);
                    }
                    compound.setTag("teamMembers", taglist);
                }
            }

            return team;
        }

        @Nullable
        public static ScorePlayerTeam getOrCreateTeam(World world, ItemStack stack, String leaderName) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            // If the scroll already has a team, load it
            if (stack.getTagCompound().hasKey("teamName")) {
                return getTeamFromItem(world, stack);
            }

            Scoreboard scoreboard = world.getScoreboard();

            // Internal scoreboard team key must be <= 16 chars and unique
            String internalTeamName = makeUniqueTeamName(scoreboard, leaderName);

            ScorePlayerTeam team = scoreboard.getTeam(internalTeamName);
            if (team == null && !world.isRemote) {
                team = scoreboard.createTeam(internalTeamName);
            }
            if (team == null) {
                return null;
            }

            // Display name is what you show on the scroll
            String display = leaderName + "'s Team";

            // Store on scroll (NBT)
            stack.getTagCompound().setString("teamName", internalTeamName);
            stack.getTagCompound().setString("teamDisplayName", display);

            // Server only: apply to scoreboard
            if (!world.isRemote) {
                team.setDisplayName(display);
            }

            return team;
        }

        private static String makeUniqueTeamName(Scoreboard scoreboard, String leaderName) {
            String base = sanitizeTeamKey(leaderName);

            if (scoreboard.getTeam(base) == null) return base;

            for (int i = 2; i < 1000; i++) {
                String suffix = "_" + i;
                int maxBaseLen = 16 - suffix.length();
                String candidateBase = base.length() > maxBaseLen ? base.substring(0, maxBaseLen) : base;
                String candidate = candidateBase + suffix;
                if (scoreboard.getTeam(candidate) == null) return candidate;
            }

            return base.substring(0, Math.min(16, base.length()));
        }

        private static String sanitizeTeamKey(String s) {
            if (s == null || s.isEmpty()) return "team";
            String cleaned = s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
            if (cleaned.isEmpty()) cleaned = "team";
            if (cleaned.length() > 16) cleaned = cleaned.substring(0, 16);
            return cleaned;
        }

        public static void addTeamMember(ItemStack stack, EntityPlayer player) {
            ScorePlayerTeam team = getTeamFromItem(player.world, stack);
            if (team == null) {
                // First joiner becomes leader and names the team
                team = getOrCreateTeam(player.world, stack, player.getName());
            }
            if (team == null) return;

            // Server only: add to scoreboard team
            if (!player.world.isRemote && !team.getMembershipCollection().contains(player.getName())) {
                player.world.getScoreboard().addPlayerToTeam(player.getName(), team.getName());
            }

            // Always: record UUID in the scroll NBT
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            NBTTagCompound compound = stack.getTagCompound();
            NBTTagList taglist = compound.hasKey("teamMembers", 9) ? compound.getTagList("teamMembers", 10) : new NBTTagList();

            for (int i = 0; i < taglist.tagCount(); i++) {
                NBTTagCompound existing = taglist.getCompoundTagAt(i);
                if (existing.hasUniqueId("memberUUID") && existing.getUniqueId("memberUUID").equals(player.getUniqueID())) {
                    return; // already present
                }
            }

            NBTTagCompound memberTag = new NBTTagCompound();
            memberTag.setUniqueId("memberUUID", player.getUniqueID());
            taglist.appendTag(memberTag);
            compound.setTag("teamMembers", taglist);
        }

        public static void removeTeamMember(ItemStack stack, EntityPlayer player) {
            // Resolve team first
            ScorePlayerTeam team = getTeamFromItem(player.world, stack);

            // Always try to remove UUID from the scroll NBT first
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("teamMembers", 9)) {
                NBTTagList taglist = stack.getTagCompound().getTagList("teamMembers", 10);
                boolean changed = false;

                for (int i = 0; i < taglist.tagCount(); i++) {
                    NBTTagCompound c = taglist.getCompoundTagAt(i);
                    if (c.hasUniqueId("memberUUID") && c.getUniqueId("memberUUID").equals(player.getUniqueID())) {
                        taglist.removeTag(i);
                        changed = true;
                        break;
                    }
                }

                if (changed) {
                    stack.getTagCompound().setTag("teamMembers", taglist);
                }
            }

            // Then remove from scoreboard team if possible (server only)
            if (!player.world.isRemote && team != null) {
                if (team.getMembershipCollection().contains(player.getName())) {
                    player.world.getScoreboard().removePlayerFromTeam(player.getName(), team);
                }
            }
        }

        public static Collection<String> getTeamMembers(World world, ItemStack stack) {
            ScorePlayerTeam team = getTeamFromItem(world, stack);
            return team != null ? team.getMembershipCollection() : Collections.emptyList();
        }

        public static String getTeamDisplayName(World world, ItemStack stack) {
            ScorePlayerTeam team = getTeamFromItem(world, stack);
            return team != null ? team.getDisplayName() : "";
        }

        public static void setTeamDisplayName(World world, ItemStack stack, String newName) {
            ScorePlayerTeam team = getTeamFromItem(world, stack);
            if (team == null) return;

            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            stack.getTagCompound().setString("teamDisplayName", newName);

            // Server only: apply to scoreboard
            if (!world.isRemote) {
                team.setDisplayName(newName);
            }
        }
    }
}

