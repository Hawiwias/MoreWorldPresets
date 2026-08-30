package hawiwias.worldpresets.mixin.client;


import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;
    @Shadow
    private static long biomeChangedTime;
    @Unique private static float currentFogEnd = 512.0F;
    @Unique private static float currentFogRed = 1.0F;
    @Unique private static float currentFogGreen = 1.0F;
    @Unique private static float currentFogBlue = 1.0F;

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void onSetupColor(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfo ci) {
        if (MWP_FIELDS.isWinterWorld && clientLevel.dimension().equals(ClientLevel.OVERWORLD)) {
            float vanillaFogRed = fogRed;
            float vanillaFogGreen = fogGreen;
            float vanillaFogBlue = fogBlue;

            float timeOfDay = clientLevel.getTimeOfDay(f);
            float sunAngle = Mth.cos(timeOfDay * ((float) Math.PI * 2F));
            float dayFog = Mth.clamp(sunAngle + 0.5F, 0.0F, 1.0F);

            boolean winterFog = MWP_FIELDS.isWinterWorld && camera.getEntity().level().isRaining();
            float targetRed = winterFog ? Mth.lerp(dayFog, 0.05F, 1.0F) : vanillaFogRed;
            float targetGreen = winterFog ? Mth.lerp(dayFog, 0.07F, 1.0F) : vanillaFogGreen;
            float targetBlue = winterFog ? Mth.lerp(dayFog, 0.15F, 1.0F) : vanillaFogBlue;

            currentFogRed = Mth.lerp(0.006F, currentFogRed, targetRed);
            currentFogGreen = Mth.lerp(0.006F, currentFogGreen, targetGreen);
            currentFogBlue = Mth.lerp(0.006F, currentFogBlue, targetBlue);


            fogRed = currentFogRed;
            fogGreen = currentFogGreen;
            fogBlue = currentFogBlue;
            biomeChangedTime = -1L;
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
        }
    }
    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float f, boolean bl, float g, CallbackInfo ci) {
        if (MWP_FIELDS.isWinterWorld  && camera.getEntity().level().dimension().equals(ClientLevel.OVERWORLD)) {
            boolean winterFog = camera.getEntity().level().isRaining();
            float targetFog = winterFog ? 14.0F : f;
            currentFogEnd = Mth.lerp(0.006F, currentFogEnd, targetFog);

            RenderSystem.setShaderFogStart(0.0F);
            RenderSystem.setShaderFogEnd(currentFogEnd);
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }
}
