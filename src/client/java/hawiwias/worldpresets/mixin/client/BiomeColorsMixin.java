package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {
    @Inject(method = "getAverageFoliageColor", at = @At("HEAD"), cancellable = true)
    private static void ongetAverageFoliageColor(BlockAndTintGetter p_108805_, BlockPos p_108806_, CallbackInfoReturnable cir)
    {
        if (MWP_FIELDS.isWinterWorld) {
            cir.setReturnValue(0xFFFFFF);
        }
    }
}
