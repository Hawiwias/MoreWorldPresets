package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.gui.components.CycleButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.SwitchGrid$LabeledSwitch")
public class SwitchGridDisabler {
    @Shadow
    private CycleButton<Boolean> button;
    @Inject(method = "refreshState", at = @At("TAIL"))
    private void onRefreshState(CallbackInfo ci) {
        if (MWP_FIELDS.isSkyblockWorld || MWP_FIELDS.isOneblockWorld || MWP_FIELDS.isSkygridWorld) {
            this.button.active = false;
        }
    }
}
