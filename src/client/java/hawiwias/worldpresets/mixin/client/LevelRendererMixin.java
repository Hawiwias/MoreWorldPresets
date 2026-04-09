package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyVariable(method = "renderSnowAndRain", at = @At("STORE"), ordinal = 4)
    private float modifySnowSpeed(float f5) {
        if (MWP_FIELDS.isWinterWorld) {
            return f5 * 6F;
        }
        return f5;
    }

    @Shadow
    @Nullable
    private ClientLevel level;

    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;hasPrecipitation()Z"))
    private boolean redirectHasPrecipitation(Biome biome) {
        if (MWP_FIELDS.isWinterWorld) {
            assert this.level != null;
            if (this.level.dimension().equals(Level.OVERWORLD)) {
                return true;
            }
        }
        return biome.hasPrecipitation();
    }
    @Redirect(method = "renderSnowAndRain", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"))
    private Biome.Precipitation redirectGetPrecipitationAt(Biome biome, BlockPos pos) {
        if (MWP_FIELDS.isWinterWorld) {
            return Biome.Precipitation.SNOW;
        }
        return biome.getPrecipitationAt(pos);
    }

}