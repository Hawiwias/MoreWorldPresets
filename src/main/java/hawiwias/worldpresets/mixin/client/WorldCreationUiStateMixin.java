package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.Identifier;
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
                MWP_FIELDS.presetKey = key.identifier().toString(); // NEW — store raw preset id
                MWP_FIELDS.isWinterWorld = key.identifier().equals(Identifier.fromNamespaceAndPath("moreworldpresets", "winter_world"));
                MWP_FIELDS.challengeWorld = key.identifier().equals(Identifier.fromNamespaceAndPath("moreworldpresets", "challenge_world")) ? MWP_FIELDS.challengeWorld : 0;
                MWP_FIELDS.isSkyblockWorld = key.identifier().equals(Identifier.fromNamespaceAndPath("moreworldpresets", "skyblock_world"));
                MWP_FIELDS.isOneblockWorld = key.identifier().equals(Identifier.fromNamespaceAndPath("moreworldpresets", "oneblock_world"));
                MWP_FIELDS.isSkygridWorld = key.identifier().equals(Identifier.fromNamespaceAndPath("moreworldpresets", "skygrid_world"));
            });
        }
        onChanged();
    }
}