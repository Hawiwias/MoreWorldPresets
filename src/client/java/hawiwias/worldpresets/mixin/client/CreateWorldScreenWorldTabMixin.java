package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.GradientTextUtil;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.WorldPresetsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$WorldTab")
public abstract class CreateWorldScreenWorldTabMixin extends GridLayoutTab {

    public CreateWorldScreenWorldTabMixin(Component component) {
        super(component);
    }
    @Unique
    private StringWidget currentPresetWidget;
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 0))
    private LayoutElement removeCycleButton(GridLayout.RowHelper instance, LayoutElement element) {
        ((AbstractWidget) element).visible = false;
        return instance.addChild(element);
    }
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 1))
    private LayoutElement removeCustomizeButton(GridLayout.RowHelper instance, LayoutElement element) {
        ((AbstractWidget) element).visible = false;
        return instance.addChild(element);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void replacePresetsButton(CreateWorldScreen createWorldScreen, CallbackInfo ci) {
        GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(8).createRowHelper(2);
        gridlayout$rowhelper.addChild(Button.builder(Component.translatable("selectWorld.presetsScreen"), (button) -> {
            Minecraft.getInstance().setScreen(new WorldPresetsScreen(createWorldScreen));
        }).build());
        currentPresetWidget = new StringWidget(
                buildPresetLabel(createWorldScreen.getUiState().getWorldType().describePreset()),
                Minecraft.getInstance().font
        ).alignLeft();
        gridlayout$rowhelper.addChild(currentPresetWidget, gridlayout$rowhelper.newCellSettings().alignVerticallyMiddle());
        createWorldScreen.getUiState().addListener((state) -> {
            currentPresetWidget.setMessage(
                    buildPresetLabel(state.getWorldType().describePreset())
            );
        });
    }

    @Unique
    private Component buildPresetLabel(Component presetName) {
        Component prefix = Component.translatable("selectWorld.currentPreset");

        if (MWP_FIELDS.challengeWorld > 0 && presetName.getString().equals("Challenge World")) {
            MutableComponent gradient = GradientTextUtil.gradientText("Challenge World " + MWP_FIELDS.challengeWorld, 0xF7971E, 0xFFD200);
            return prefix.copy().append(gradient);
        }

        String presetId = resolvePresetId(presetName.getString());
        if (presetId != null) {
            MutableComponent gradient = GradientTextUtil.forPresetId(presetId);
            if (gradient != null) {
                return prefix.copy().append(gradient);
            }
        }

        return prefix.copy().append(presetName);
    }

    @Unique
    private String resolvePresetId(String presetNameString) {
        if (MWP_FIELDS.challengeWorld > 0 && presetNameString.equals("Challenge World")) {
            return null;
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
