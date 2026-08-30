package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.Phase;
import hawiwias.worldpresets.PhaseManager;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen .class)
public class CreateWorldScreenMixin {
    @Inject(method = "popScreen", at = @At("HEAD"))
    private void onpopScreen(CallbackInfo ci) {
        MWP_FIELDS.isWinterWorld = false;
        MWP_FIELDS.isSkyblockWorld = false;
        MWP_FIELDS.isOneblockWorld = false;
        MWP_FIELDS.isSkygridWorld = false;
        MWP_FIELDS.challengeWorld = 0;
        MWP_FIELDS.SkyblockWorld = 1;
        for (int i = 0; i < PhaseManager.phases.size(); i++) {
            PhaseManager.phases.get(i).unlocked = (i == 0);
        }
    }
    @Inject(method = "openFresh", at = @At("HEAD"))
    private static void onopenFresh(CallbackInfo ci) {
        MWP_FIELDS.isWinterWorld = false;
        MWP_FIELDS.isSkyblockWorld = false;
        MWP_FIELDS.isOneblockWorld = false;
        MWP_FIELDS.isSkygridWorld = false;
        MWP_FIELDS.challengeWorld = 0;
        MWP_FIELDS.SkyblockWorld = 1;
        PhaseManager.currentPhaseIndex = 0;
        PhaseManager.currentPhaseProgress = 0;
        for (int i = 0; i < PhaseManager.phases.size(); i++) {
            PhaseManager.phases.get(i).unlocked = (i == 0);
        }
    }

}
