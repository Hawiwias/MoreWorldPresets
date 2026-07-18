package hawiwias.worldpresets.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.accessor.TemperatureAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    private static final ResourceLocation GUI_TEMPERATURE_BAR = ResourceLocation.fromNamespaceAndPath("moreworldpresets", "textures/gui/tempbar.png");
    private static final ResourceLocation GUI_TEMPERATURE_ARROW = ResourceLocation.fromNamespaceAndPath("moreworldpresets", "textures/gui/temparrow.png");
    private static final ResourceLocation GUI_TEMPERATURE_BORDER = ResourceLocation.fromNamespaceAndPath("moreworldpresets", "textures/gui/tempborder.png");
    @Shadow
    Minecraft minecraft;
    @ModifyConstant(method = "renderFood", constant = @Constant(intValue = 10))
    private int hideExtraHungerIcons(int constant) {
        if (MWP_FIELDS.challengeWorld == 3) {
            return 5;
        }
        return constant;
    }
    @Inject(method = "render", at = @At("TAIL"))
    private void renderModded(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!minecraft.options.hideGui && minecraft.player != null && MWP_FIELDS.isWinterWorld) {
            renderTemperatureBar(guiGraphics);
        }
    }

    private void renderTemperatureBar(GuiGraphics guiGraphics) {
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        int barWidth = (int) (235 * 0.7);
        int barHeight = (int) (28 * 0.7);
        int arrowSize = 8;

        int x = screenWidth - barWidth - 24;
        int y = screenHeight - barHeight - 9;

        int hotbarLeft = screenWidth / 2 - 91;
        int hotbarRight = screenWidth / 2 + 91;
        int hotbarTop = screenHeight - 22;

        boolean overlapsHotbarHorizontally = x < hotbarRight && x + barWidth > hotbarLeft;
        if (overlapsHotbarHorizontally) {
            y = hotbarTop - barHeight - 4;
        }

        guiGraphics.blit(GUI_TEMPERATURE_BAR, x, y, barWidth, barHeight, 0, 0, 235, 28, 235, 28);
        float minTemp = -40;
        float maxTemp = 30;

        float percent = (((TemperatureAccessor) Minecraft.getInstance().player).getTemperature() - minTemp) / (maxTemp - minTemp);
        percent = Math.max(0f, Math.min(1f, percent));
        percent = 1.0f - percent;

        int borderPaddingLeft = 4;
        int borderPaddingRight = 4;
        int usableMinX = x + borderPaddingLeft;
        int usableMaxX = x + barWidth - borderPaddingRight;

        int arrowCenterX = usableMinX + (int) ((usableMaxX - usableMinX) * percent);
        int arrowX = arrowCenterX - arrowSize / 2;

        arrowX = Math.max(usableMinX, Math.min(usableMaxX - arrowSize, arrowX));

        int arrowY = y + 5;

        guiGraphics.blit(GUI_TEMPERATURE_ARROW, arrowX, arrowY, arrowSize, arrowSize, 0, 0, 128, 128, 128, 128);
        guiGraphics.blit(GUI_TEMPERATURE_BORDER, x, y, barWidth, barHeight, 0, 0, 235, 28, 235, 28);
    }
}
