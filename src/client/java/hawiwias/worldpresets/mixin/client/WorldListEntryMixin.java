package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.GradientTextUtil;
import hawiwias.worldpresets.PresetLabelAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListEntryMixin {

    @Shadow net.minecraft.world.level.storage.LevelSummary summary;

    @Inject(method = "render", at = @At("TAIL"))
    private void addPresetLine(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick, CallbackInfo ci) {
        String presetId = ((PresetLabelAccessor) (Object) this.summary).moreworldpresets$getPresetLabel();
        if (presetId == null) return;

        MutableComponent labelComponent;
        if (presetId.startsWith("challenge:")) {
            int variant = Integer.parseInt(presetId.substring("challenge:".length()));
            labelComponent = GradientTextUtil.gradientText("Challenge World " + variant, 0xF7971E, 0xFFD200);
        } else {
            labelComponent = GradientTextUtil.forPresetId(presetId);
        }
        if (labelComponent == null) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(left + 32 + 3, top + 30, 0);
        guiGraphics.pose().scale(0.85f, 0.85f, 1.0f);
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, labelComponent, 0, 0, 0xFFFFFF, false);
        guiGraphics.pose().popPose();
    }
}