package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.level.FoliageColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (FoliageColor.class)

public class FoliageColorMixin {
    @Inject(method = "getDefaultColor", at = @At("RETURN"), cancellable = true)
    private static void ongetDefaultColor(CallbackInfoReturnable <Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
    @Inject(method = "getEvergreenColor", at = @At("RETURN"), cancellable = true)
    private static void ongetEvergreenColor(CallbackInfoReturnable <Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private static void onget(double p_46108_, double p_46109_, CallbackInfoReturnable <Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
    @Inject(method = "getBirchColor", at = @At("RETURN"), cancellable = true)
    private static void ongetBirchColor(CallbackInfoReturnable <Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
    @Inject(method = "getMangroveColor", at = @At("RETURN"), cancellable = true)
    private static void ongetMangroveColor(CallbackInfoReturnable <Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
}
