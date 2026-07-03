package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Random;

public class WorldPresetsScreen extends Screen {
    protected final CreateWorldScreen parent;
    public WorldPresetsScreen(CreateWorldScreen sr5tx) {
        super(Component.translatable("selectWorld.presetsScreen"));
        parent = sr5tx;
    }
    private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");

    // 0 = Disabled, 1-4 = variant, 5 = Random
    private int challengeWorldVariant = 0;

    protected void init() {
        boolean isChallengeWorldActive = parent.getUiState().getWorldType().preset()
                .is(new ResourceLocation("moreworldpresets", "challenge_world"));
        challengeWorldVariant = isChallengeWorldActive ? MWP_FIELDS.challengeWorld : 0;
        GridLayout gridlayout = new GridLayout();
        gridlayout.setPosition(this.width / 2 - 210, 50);
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(2);
        List<WorldCreationUiState.WorldTypeEntry> presetList = parent.getUiState().getAltPresetList();
        presetList.forEach(entry -> {

            // REPLACE the challenge_world entry's button with a cycle button instead of a normal Button
            if (entry.preset().is(new ResourceLocation("moreworldpresets", "challenge_world"))) {
                CycleButton<Integer> cycleButton = CycleButton.<Integer>builder(this::challengeWorldLabel)
                        .withValues(0, 1, 2, 3, 4, 5)
                        .withInitialValue(challengeWorldVariant)
                        .displayOnlyValue()
                        .create(0, 0, 200, 20, CommonComponents.EMPTY, (button, value) -> {
                            challengeWorldVariant = value;

                            if (value == 0) {
                                return;
                            }

                            parent.getUiState().setWorldType(entry);
                            MWP_FIELDS.challengeWorld = (value == 5)
                                    ? 1 + new Random().nextInt(4)
                                    : value;
                            parent.getUiState().onChanged();
                        });
                gridlayout$rowhelper.addChild(cycleButton);
                return;
            }

            Button.Builder builder = Button.builder(entry.describePreset(), (button) -> {
                parent.getUiState().setWorldType(entry);
                if (entry.preset().is(new ResourceLocation("minecraft", "flat")))
                {
                    parent.getUiState().onChanged();
                    PresetEditor presetEditor = parent.getUiState().getPresetEditor();
                    this.minecraft.setScreen(presetEditor.createEditScreen(parent, parent.getUiState().getSettings()));
                    return;
                }
                if (entry.preset().is(new ResourceLocation("minecraft", "single_biome_surface")))
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

    private Component challengeWorldLabel(Integer value) {
        return switch (value) {
            case 0 -> Component.literal("Challenge World: Disabled");
            case 5 -> Component.literal("Challenge World: Random");
            default -> Component.literal("Challenge World: " + value);
        };
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