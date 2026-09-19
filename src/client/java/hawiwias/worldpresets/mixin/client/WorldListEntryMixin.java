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

        String displayText;
        int type;

        if (presetId.startsWith("challenge:")) {
            int variant = Integer.parseInt(presetId.substring("challenge:".length()));
            type = 2;
            displayText = "Challenge World " + variant;
        } else if (presetId.startsWith("skyblock:")) {
            int variant = Integer.parseInt(presetId.substring("skyblock:".length()));
            type = 2;
            displayText = switch (variant) {
                case 1 -> "Skyblock V2.1";
                case 2 -> "Skyblock V2.0";
                case 3 -> "Skyblock V1.1 (original)";
                default -> "Skyblock";
            };
        } else {
            type = switch (presetId) {
                case "normal", "flat", "amplified", "large_biomes", "single_biome", "debug" -> 1;
                case "skyblock", "skygrid", "oneblock", "winter" -> 2;
                case "floating_islands" -> 3;
                default -> 0;
            };
            displayText = switch (presetId) {
                case "normal" -> "Default";
                case "flat" -> "Superflat";
                case "amplified" -> "AMPLIFIED";
                case "large_biomes" -> "Large Biomes";
                case "single_biome" -> "Single Biome";
                case "debug" -> "Debug Mode";
                case "floating_islands" -> "Floating Islands";
                case "skyblock" -> "Skyblock";
                case "skygrid" -> "Skygrid";
                case "oneblock" -> "Oneblock";
                case "winter" -> "Winter World";
                default -> presetId;
            };
        }

        int[] range = switch (type) {
            case 1 -> new int[]{0xFFF589, 0x00FF4D};
            case 2 -> new int[]{0x274D99, 0x7566FF};
            case 3 -> new int[]{0xAD2424, 0xFF4949};
            default -> new int[]{0xFFFFFF, 0xFFFFFF};
        };
        int color = range[0];
        int color2 = range[1];
        MutableComponent labelComponent = GradientTextUtil.gradientText(displayText, color, color2);

        if (labelComponent == null) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(left + 32 + 3, top + 30, 0);
        guiGraphics.pose().scale(0.85f, 0.85f, 1.0f);
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, labelComponent, 0, 0, 0xFFFFFF, false);
        guiGraphics.pose().popPose();
    }
}