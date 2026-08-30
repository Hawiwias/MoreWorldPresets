package hawiwias.worldpresets.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.systems.RenderSystem;
import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.util.Mth;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Unique private static float currentFogEnd = 512.0F;
    @Unique private static float currentFogRed = 1.0F;
    @Unique private static float currentFogGreen = 1.0F;
    @Unique private static float currentFogBlue = 1.0F;

    @Inject(method = "computeFogColor", at = @At("TAIL"))
    private static void onComputeFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector4f dest, CallbackInfo ci) {
        if (MWP_FIELDS.isWinterWorld && level.dimension().equals(ClientLevel.OVERWORLD)) {
            float vanillaFogRed = dest.x();
            float vanillaFogGreen = dest.y();
            float vanillaFogBlue = dest.z();

            long dayTimeTicks = level.getOverworldClockTime() % 24000L;
            float timeFraction = dayTimeTicks / 24000.0F;


            float daylight = (Mth.cos((timeFraction - 0.25F) * 2.0F * (float) Math.PI) + 1.0F) / 2.0F;

            boolean winterFog = level.isRaining();
            float targetRed = winterFog ? Mth.lerp(daylight, 0.02F, 1.0F) : vanillaFogRed;
            float targetGreen = winterFog ? Mth.lerp(daylight, 0.03F, 1.0F) : vanillaFogGreen;
            float targetBlue = winterFog ? Mth.lerp(daylight, 0.08F, 1.0F) : vanillaFogBlue;

            currentFogRed = Mth.lerp(0.006F, currentFogRed, targetRed);
            currentFogGreen = Mth.lerp(0.006F, currentFogGreen, targetGreen);
            currentFogBlue = Mth.lerp(0.006F, currentFogBlue, targetBlue);

            dest.set(currentFogRed, currentFogGreen, currentFogBlue, dest.w());
        }
    }
    @ModifyReturnValue(method = "setupFog", at = @At("RETURN"))
    private FogData onSetupFog(FogData fog, Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level) {
        if (MWP_FIELDS.isWinterWorld && level.dimension().equals(ClientLevel.OVERWORLD)) {
            boolean winterFog = level.isRaining();
            float targetFog = winterFog ? 14.0F : fog.renderDistanceEnd;
            currentFogEnd = Mth.lerp(0.006F, currentFogEnd, targetFog);

            fog.renderDistanceStart = 0.0F;
            fog.renderDistanceEnd = currentFogEnd;
        }
        return fog;
    }
}
