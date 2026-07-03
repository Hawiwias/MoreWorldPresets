package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.WorldPresetsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
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
        Component base = Component.translatable("selectWorld.currentPreset").append(presetName);
        if (presetName.getString().equals("Challenge World")) {
            base = base.copy().append(" " + MWP_FIELDS.challengeWorld);
        }
        return base;
    }

}
