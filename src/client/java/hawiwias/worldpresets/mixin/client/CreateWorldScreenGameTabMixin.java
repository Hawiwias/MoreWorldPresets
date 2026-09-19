package hawiwias.worldpresets.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import hawiwias.worldpresets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public class CreateWorldScreenGameTabMixin extends GridLayoutTab {
    @Unique
    private StringWidget currentPresetWidget;
    public CreateWorldScreenGameTabMixin(Component component) {
        super(component);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void replacePresetsButton(CreateWorldScreen createWorldScreen, CallbackInfo ci, @Local(ordinal = 0) GridLayout.RowHelper rowHelper) {
        rowHelper.addChild(Button.builder(Component.empty().append(GradientTextUtil.gradientText("World Presets", 0xFFFFFFFF, 0x8097ff)), (button) -> {
            Minecraft.getInstance().setScreen(new CategorySelectionScreen(createWorldScreen, createWorldScreen));
        }).width(210).build());
        currentPresetWidget = new StringWidget(
                buildPresetLabel(createWorldScreen.getUiState().getWorldType().describePreset()),
                Minecraft.getInstance().font
        );
        rowHelper.addChild(currentPresetWidget, rowHelper.newCellSettings().alignVerticallyMiddle().alignHorizontallyCenter());
        createWorldScreen.getUiState().addListener((state) -> {
            currentPresetWidget.setMessage(
                    buildPresetLabel(state.getWorldType().describePreset())
            );
        });
    }

    @Unique
    private Component buildPresetLabel(Component presetName) {
        Component prefix = Component.empty().append(GradientTextUtil.gradientText("Current Preset: ", 0xFFFFFFFF, 0x8097ff));
        String presetId = resolvePresetId(presetName.getString());
        if (presetId != null) {
            int type = switch (presetId) {
                case "normal", "flat", "amplified", "large_biomes", "single_biome", "debug" -> 1;
                case "skyblock", "skygrid", "oneblock", "winter", "challenge_world" -> 2;
                case "floating_islands" -> 3;
                default -> 0;
            };
            int[] range = switch (type) {
                case 1 -> new int[]{0xFFF589, 0x00FF4D};
                case 2 -> new int[]{0x274D99, 0x7566FF};
                case 3 -> new int[]{0xAD2424, 0xFF4949};
                default -> new int[]{0xFFFFFF, 0xFFFFFF};
            };

            int color = range[0];
            int color2 = range[1];

            String displayText;
            if (presetId.equals("challenge_world")) {
                displayText = "Challenge World " + MWP_FIELDS.challengeWorld;
            } else if (presetId.equals("skyblock")) {
                displayText = switch (MWP_FIELDS.SkyblockWorld) {
                    case 1 -> "Skyblock V2.1";
                    case 2 -> "Skyblock V2.0";
                    case 3 -> "Skyblock V1.1 (original)";
                    default -> presetName.getString();
                };
            } else {
                displayText = presetName.getString();
            }

            MutableComponent gradient = GradientTextUtil.gradientText(displayText, color, color2);
            return prefix.copy().append(gradient);
        }

        return prefix.copy().append(presetName);
    }

    @Unique
    private String resolvePresetId(String presetNameString) {
        if (MWP_FIELDS.challengeWorld > 0 && presetNameString.equals("Challenge Worlds")) {
            return "challenge_world";
        }
        if (MWP_FIELDS.isSkyblockWorld) return "skyblock";
        if (MWP_FIELDS.isSkygridWorld) return "skygrid";
        if (MWP_FIELDS.isOneblockWorld) return "oneblock";
        if (MWP_FIELDS.isWinterWorld) return "winter";

        return switch (presetNameString) {
            case "Superflat" -> "flat";
            case "Default" -> "normal";
            case "AMPLIFIED" -> "amplified";
            case "Large Biomes" -> "large_biomes";
            case "Single Biome" -> "single_biome";
            case "Debug Mode" -> "debug";
            case "Floating Islands" -> "floating_islands";
            default -> null;
        };
    }

}