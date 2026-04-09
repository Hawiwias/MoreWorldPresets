package hawiwias.worldpresets.mixin;


import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.level.levelgen.WorldOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldOptions.class)
public abstract class WorldOptionsMixin {

    @Inject(method = "generateBonusChest", at = @At("HEAD"), cancellable = true)
    private void forceBonusChest(CallbackInfoReturnable<Boolean> cir) {
        if (MWP_FIELDS.challengeWorld == 1) {
            cir.setReturnValue(true);
        }
    }
}
