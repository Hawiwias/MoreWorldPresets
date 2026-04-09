package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class WorldPresetsScreen extends Screen {
    protected final CreateWorldScreen parent;
    public WorldPresetsScreen(CreateWorldScreen sr5tx) {
        super(Component.translatable("selectWorld.presetsScreen"));
        parent = sr5tx;
    }
    private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");
    protected void init() {
        GridLayout gridlayout = new GridLayout();
        gridlayout.setPosition(this.width / 2 - 210, 50);
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(2);
        List<WorldCreationUiState.WorldTypeEntry> presetList = parent.getUiState().getAltPresetList();
        presetList.forEach(entry -> {
            Button.Builder builder = Button.builder(entry.describePreset(), (button) -> {
                parent.getUiState().setWorldType(entry);
                if (entry.preset().is(new ResourceLocation("moreworldpresets", "flat")))
                {
                    parent.getUiState().onChanged();
                    PresetEditor presetEditor = parent.getUiState().getPresetEditor();
                    this.minecraft.setScreen(presetEditor.createEditScreen(parent, parent.getUiState().getSettings()));
                    return;
                }
                if (entry.preset().is(new ResourceLocation("moreworldpresets", "single_biome_surface")))
                {
                    parent.getUiState().onChanged();
                    PresetEditor presetEditor = parent.getUiState().getPresetEditor();
                    this.minecraft.setScreen(presetEditor.createEditScreen(parent, parent.getUiState().getSettings()));
                    return;
                }
                if (entry.preset().is(new ResourceLocation("moreworldpresets", "skyblock_world")))
                {
                    parent.getUiState().onChanged();
                    this.minecraft.setScreen(new SkyblockSettingsScreen(this.parent));
                    return;
                }
                if (entry.preset().is(new ResourceLocation("moreworldpresets", "oneblock_world")))
                {
                    parent.getUiState().onChanged();
                    parent.getUiState().setGenerateStructures(false);
                }
                this.minecraft.setScreen(parent);
            }).width(200);
            if (entry.isAmplified()) {
                builder.tooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
            }
            gridlayout$rowhelper.addChild(builder.build());
        });

        gridlayout.arrangeElements();
        gridlayout.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            this.minecraft.setScreen(parent);
        }).bounds(this.width / 2 - 75, this.height - 28, 150, 20).build());
    }
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}