package hawiwias.worldpresets.mixin.client;

import hawiwias.worldpresets.GradientTextUtil;
import hawiwias.worldpresets.PresetLabelAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListEntryMixin extends WorldSelectionList.Entry {

    @Shadow net.minecraft.client.Minecraft minecraft;
    @Unique
    private StringWidget moreworldpresets$presetLineWidget;
    @Shadow net.minecraft.world.level.storage.LevelSummary summary;
    @Inject(method = "extractContent", at = @At("TAIL"))
    private void addPresetLine(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a, CallbackInfo ci) {
        String presetId = ((PresetLabelAccessor) this.summary).moreworldpresets$getPresetLabel();
        if (presetId == null) return;

        MutableComponent labelComponent;
        if (presetId.startsWith("challenge:")) {
            int variant = Integer.parseInt(presetId.substring("challenge:".length()));
            labelComponent = GradientTextUtil.gradientText("Challenge World " + variant, 0xF7971E, 0xFFD200);
        } else {
            labelComponent = GradientTextUtil.forPresetId(presetId);
        }
        if (labelComponent == null) return;

        if (moreworldpresets$presetLineWidget == null) {
            moreworldpresets$presetLineWidget = new StringWidget(labelComponent, minecraft.font);
        } else {
            moreworldpresets$presetLineWidget.setMessage(labelComponent);
        }
        moreworldpresets$presetLineWidget.setPosition(this.getContentX() + 36, this.getContentY() + 30);
        moreworldpresets$presetLineWidget.extractRenderState(graphics, mouseX, mouseY, a);
    }
}