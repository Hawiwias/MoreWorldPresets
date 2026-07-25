package hawiwias.worldpresets.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WeatherEffectRenderer.class)
public class WeatherEffectRendererMixin {

    @WrapOperation(
            method = "getPrecipitationAt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;I)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation forceSnowInWinterWorld(Biome biome, BlockPos pos, int seaLevel, Operation<Biome.Precipitation> original, @Local(argsOnly = true, name = "level") Level level) {
        if (MWP_FIELDS.isWinterWorld && level.dimension().equals(Level.OVERWORLD)) {
            return Biome.Precipitation.SNOW;
        }
        return original.call(biome, pos, seaLevel);
    }

    @ModifyConstant(method = "createSnowColumnInstance", constant = @Constant(floatValue = 512.0F))
    private float speedUpSnowFall(float original) {
        if (MWP_FIELDS.isWinterWorld) {
            return original / 6.0F;
        }
        return original;
    }
}