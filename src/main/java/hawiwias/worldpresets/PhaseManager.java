package hawiwias.worldpresets;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

import static net.minecraft.world.level.Level.OVERWORLD;

public class PhaseManager {
    public static int currentPhaseIndex = 0;
    public static int currentPhaseProgress = 0;
    public static CustomBossEvent phaseProgressBar;

    public static final List<Phase> phases = List.of(
            new Phase("Plains", 50, BossEvent.BossBarColor.GREEN, List.of(Blocks.GRASS_BLOCK, Blocks.DIRT)),
            new Phase("Stone", 300, BossEvent.BossBarColor.WHITE, List.of(Blocks.STONE, Blocks.COBBLESTONE)),
            new Phase("Nether", 400, BossEvent.BossBarColor.RED, List.of(Blocks.NETHERRACK, Blocks.SOUL_SAND))
    );

    public static Phase getCurrentPhase() {
        return phases.get(currentPhaseIndex);
    }

    public static void onBlockBroken() {
        currentPhaseProgress++;
        if (currentPhaseProgress >= getCurrentPhase().maxBlocks) {
            currentPhaseProgress = 0;
            if (currentPhaseIndex < phases.size() - 1) {
                currentPhaseIndex++;
                MoreWorldPresets.INSTANCE.getLevel(OVERWORLD).players().forEach((serverPlayer -> {
                    serverPlayer.level().playSound(null, serverPlayer.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
                    serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Component.literal(getCurrentPhase().name + " Unlocked!").withStyle(getCurrentPhase().color.getFormatting())));
                }));
            }
        }
    }
}
