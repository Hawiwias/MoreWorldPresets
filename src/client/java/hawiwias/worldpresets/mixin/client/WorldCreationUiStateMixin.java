package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {
    @Shadow public abstract void onChanged();
    @Inject(method = "setWorldType", at = @At("TAIL"))
    private void onSetWorldType(WorldCreationUiState.WorldTypeEntry entry, CallbackInfo ci) {
        if (entry.preset() != null) {
            entry.preset().unwrapKey().ifPresent(key -> {
                MWP_FIELDS.presetKey = key.location().toString(); // NEW — store raw preset id
                MWP_FIELDS.isWinterWorld = key.location().equals(new ResourceLocation("moreworldpresets", "winter_world"));
                MWP_FIELDS.challengeWorld = key.location().equals(new ResourceLocation("moreworldpresets", "challenge_world")) ? MWP_FIELDS.challengeWorld : 0;
                MWP_FIELDS.isSkyblockWorld = key.location().equals(new ResourceLocation("moreworldpresets", "skyblock_world"));
                MWP_FIELDS.isOneblockWorld = key.location().equals(new ResourceLocation("moreworldpresets", "oneblock_world"));
                MWP_FIELDS.isSkygridWorld = key.location().equals(new ResourceLocation("moreworldpresets", "skygrid_world"));
            });
        }
        onChanged();
    }
}