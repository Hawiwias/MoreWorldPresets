package hawiwias.worldpresets;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class PhaseManager {
    public static int currentPhaseIndex = 0;
    public static int currentPhaseProgress = 0;
    public static CustomBossEvent phaseProgressBar;

    public static final List<Phase> phases = List.of(
            new Phase("Plains", 50, BossEvent.BossBarColor.GREEN, ChatFormatting.GREEN,List.of(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.DIRT_PATH, Blocks.PUMPKIN), List.of(EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.COW, EntityType.HORSE), List.of(new ResourceLocation("moreworldpresets", "chests/plains")), true),
            new Phase("Forest", 80, BossEvent.BossBarColor.GREEN, ChatFormatting.DARK_GREEN, List.of(Blocks.OAK_LOG, Blocks.OAK_LEAVES, Blocks.BEE_NEST, Blocks.BIRCH_LOG), List.of(EntityType.WOLF, EntityType.BEE), List.of(new ResourceLocation("moreworldpresets", "chests/forest")), false),
            new Phase("Beach", 70, BossEvent.BossBarColor.YELLOW, ChatFormatting.YELLOW,List.of(Blocks.SAND, Blocks.SANDSTONE, Blocks.GRAVEL, Blocks.CLAY), List.of(EntityType.TURTLE), List.of(new ResourceLocation("moreworldpresets", "chests/beach")), false),
            new Phase("Badlands", 50, BossEvent.BossBarColor.YELLOW, ChatFormatting.GOLD,List.of(Blocks.RED_SAND, Blocks.RED_SANDSTONE, Blocks.TERRACOTTA, Blocks.CACTUS, Blocks.RED_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.GOLD_ORE), List.of(EntityType.HUSK), List.of(new ResourceLocation("moreworldpresets", "chests/badlands")), false),
            new Phase("Taiga Forest", 50, BossEvent.BossBarColor.RED, ChatFormatting.RED,List.of(Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES), List.of(EntityType.FOX), null, false),
            new Phase("Dark Forest", 80, BossEvent.BossBarColor.RED, ChatFormatting.RED,List.of(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_LEAVES, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK), List.of(), List.of(new ResourceLocation("moreworldpresets", "chests/darkoak")), false),
            new Phase("Caves", 200,BossEvent.BossBarColor.WHITE, ChatFormatting.GRAY,List.of(Blocks.STONE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.REDSTONE_ORE, Blocks.DIAMOND_ORE, Blocks.COPPER_ORE, Blocks.LAPIS_ORE), List.of(EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER), List.of(new ResourceLocation("moreworldpresets", "chests/caves")), false),
            new Phase("Dripstone caves", 110,BossEvent.BossBarColor.YELLOW, ChatFormatting.GOLD,List.of(Blocks.DRIPSTONE_BLOCK), List.of(EntityType.CREEPER), null, false),
            new Phase("Lush caves", 110,BossEvent.BossBarColor.GREEN, ChatFormatting.DARK_GREEN,List.of(Blocks.MOSS_BLOCK, Blocks.ROOTED_DIRT, Blocks.AZALEA_LEAVES, Blocks.FLOWERING_AZALEA_LEAVES, Blocks.AZALEA, Blocks.FLOWERING_AZALEA), List.of(EntityType.GLOW_SQUID), null, false),
            new Phase("Deepslate caves", 200,BossEvent.BossBarColor.WHITE, ChatFormatting.BLACK,List.of(Blocks.DEEPSLATE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.TUFF), List.of(EntityType.CAVE_SPIDER), null, false),
            new Phase("Deep dark", 200,BossEvent.BossBarColor.BLUE, ChatFormatting.DARK_AQUA,List.of(Blocks.SCULK, Blocks.SCULK_SENSOR, Blocks.SCULK_CATALYST, Blocks.SCULK_SHRIEKER), List.of(EntityType.WARDEN), List.of(new ResourceLocation("moreworldpresets", "chests/deepdark")), false),
            new Phase("Nether wastes", 300,BossEvent.BossBarColor.RED, ChatFormatting.DARK_RED,List.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GLOWSTONE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE), List.of(EntityType.PIGLIN, EntityType.STRIDER, EntityType.ZOMBIFIED_PIGLIN), List.of(new ResourceLocation("moreworldpresets", "chests/netherwastes")), false),
            new Phase("Basalt deltas", 40,BossEvent.BossBarColor.WHITE, ChatFormatting.DARK_GRAY,List.of(Blocks.SOUL_SOIL, Blocks.BONE_BLOCK), List.of(EntityType.MAGMA_CUBE), null, false),
            new Phase("Soul sand valley", 90,BossEvent.BossBarColor.RED, ChatFormatting.RED,List.of(Blocks.NETHERRACK, Blocks.SOUL_SAND), List.of(EntityType.WITHER_SKELETON), List.of(new ResourceLocation("moreworldpresets", "chests/soulsandvalley")), false),
            new Phase("Crimson forest", 200,BossEvent.BossBarColor.RED, ChatFormatting.DARK_RED,List.of(Blocks.CRIMSON_NYLIUM, Blocks.CRIMSON_STEM, Blocks.NETHER_WART_BLOCK, Blocks.SHROOMLIGHT), List.of(EntityType.HOGLIN), null, false),
            new Phase("Warped forest", 200,BossEvent.BossBarColor.BLUE, ChatFormatting.AQUA,List.of(Blocks.WARPED_NYLIUM, Blocks.WARPED_STEM, Blocks.WARPED_WART_BLOCK), List.of(), null, false),
            new Phase("The End", 200,BossEvent.BossBarColor.PURPLE, ChatFormatting.LIGHT_PURPLE,List.of(Blocks.END_STONE, Blocks.CHORUS_FLOWER, Blocks.OBSIDIAN), List.of(EntityType.ENDERMAN), List.of(new ResourceLocation("moreworldpresets", "chests/end")), false)

    );

    public static Phase getCurrentPhase() {
        return phases.get(currentPhaseIndex);
    }

    public static void onBlockBroken(Level world, Player player) {
        ServerPlayer serverPlayer = (ServerPlayer)player;
        currentPhaseProgress++;
        if (currentPhaseProgress >= getCurrentPhase().maxBlocks) {
            currentPhaseProgress = 0;
            if (currentPhaseIndex < phases.size() - 1) {
                phases.get(currentPhaseIndex + 1).unlocked = true;
                currentPhaseIndex++;
                world.playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.literal(getCurrentPhase().name + " Unlocked!").withStyle(getCurrentPhase().textColor)));
            }
        }
    }
}
