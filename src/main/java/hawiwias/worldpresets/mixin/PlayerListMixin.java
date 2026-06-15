package hawiwias.worldpresets.mixin;

import com.google.common.collect.Lists;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.PhaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.List;

import static hawiwias.worldpresets.PhaseManager.phaseProgressBar;


@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow
    private final List<ServerPlayer> players = Lists.newArrayList();
    @Inject(method = "respawn", at = @At("TAIL"))
    private void replaceSpawn(ServerPlayer serverPlayer, boolean bl, CallbackInfoReturnable<ServerPlayer> cir) {
        if (MWP_FIELDS.challengeWorld == 1 && serverPlayer.getRespawnPosition() == null) {
            for(ServerPlayer serverplayer : players) {
                serverplayer.setRespawnPosition(serverPlayer.level().dimension(),new BlockPos((int) 0.5, -63, (int) 0.5), serverplayer.getYRot(), true, false);
            }
        } else if (MWP_FIELDS.isSkyblockWorld && serverPlayer.getRespawnPosition() == null) {

            for(ServerPlayer serverplayer : players) {
                serverplayer.setRespawnPosition(serverPlayer.level().dimension(),new BlockPos((int) 9.5, 67, (int) 7.5), serverplayer.getYRot(), true, false);
            }
        } else if (MWP_FIELDS.isOneblockWorld && serverPlayer.getRespawnPosition() == null) {

            for(ServerPlayer serverplayer : players) {
                serverplayer.setRespawnPosition(serverPlayer.level().dimension(),new BlockPos((int) 0.5, 66, (int) 0.5), serverplayer.getYRot(), true, false);
            }
        }
    }
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void addProgressionBar(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        CustomBossEvent existing = serverPlayer.getServer().getCustomBossEvents().get(new ResourceLocation("moreworldpresets", "phase_progress"));
        if (!MWP_FIELDS.isOneblockWorld) return;
        if (existing != null) {
            phaseProgressBar = existing;
        } else {
            phaseProgressBar = serverPlayer.getServer().getCustomBossEvents().create(new ResourceLocation("moreworldpresets", "phase_progress"), Component.literal("23131"));
        }
        PhaseManager.phaseProgressBar.addPlayer(serverPlayer);
    }
}