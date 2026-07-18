package hawiwias.worldpresets.mixin;

import com.google.common.collect.Lists;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import hawiwias.worldpresets.PhaseManager;
import hawiwias.worldpresets.accessor.TemperatureAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
    @Inject(method = "respawn", at = @At("TAIL"))
    private void replaceSpawn(ServerPlayer serverPlayer, boolean bl, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer respawnedPlayer = cir.getReturnValue();
        if (MWP_FIELDS.isWinterWorld  && respawnedPlayer != null) {
            ((TemperatureAccessor) respawnedPlayer).setTemperature(0.0f);
            respawnedPlayer.setTicksFrozen(0);
        }
        if (MWP_FIELDS.challengeWorld == 3 && respawnedPlayer != null) {
            AttributeInstance maxHealthAttr = respawnedPlayer.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                maxHealthAttr.setBaseValue(10.0);
            }
            respawnedPlayer.setHealth((float) respawnedPlayer.getMaxHealth());
        }
    }
}