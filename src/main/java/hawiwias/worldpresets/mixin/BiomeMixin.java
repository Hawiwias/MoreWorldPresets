package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "getPrecipitationAt", at = @At("HEAD"), cancellable = true)
    private void ongetPrecipitationAt(BlockPos pos, int seaLevel, CallbackInfoReturnable<Biome.Precipitation> cir)
    {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        }
    }
    @Inject(method = "getFoliageColor", at = @At("RETURN"), cancellable = true)
    private void onGetFoliageColor(CallbackInfoReturnable<Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
    @Inject(method = "getGrassColor", at = @At("RETURN"), cancellable = true)
    private void onGetGrassColor(double x, double z, CallbackInfoReturnable<Integer> cir) {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFFFF);
        }
    }
}
