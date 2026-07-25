package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.level.LevelHeightAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelHeightAccessor.class)
public interface LevelHeightAccessorMixin {

    @Inject(method = "getMaxY", at = @At("RETURN"), cancellable = true)
    default void modifyMaxHeight(CallbackInfoReturnable<Integer> cir) {
        if (MWP_FIELDS.challengeWorld == 1)
        {
            cir.setReturnValue(512);
        }
    }
}