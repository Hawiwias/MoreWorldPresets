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
    private void ongetPrecipitationAt(BlockPos p_265163_, CallbackInfoReturnable cir)
    {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        }
    }
}
