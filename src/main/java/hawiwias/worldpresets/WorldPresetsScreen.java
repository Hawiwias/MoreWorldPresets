package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
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

public class WorldPresetsScreen extends Screen {
    protected final CreateWorldScreen parent;
    protected final Screen previousScreen;
    private int type;
    public WorldPresetsScreen(CreateWorldScreen sr5tx, Screen previousScreen ,int type) {
        super(Component.translatable("selectWorld.presetsScreen"));
        parent = sr5tx;
        this.type = type;
        this.previousScreen = previousScreen;
    }
    private static final Component AMPLIFIED_HELP_TEXT = Component.translatable("generator.minecraft.amplified.info");

    private int challengeWorldVariant = 0;
    private Tooltip challengeWorldTooltip(Integer value) {
        return switch (value) {

            default -> Tooltip.create(CommonComponents.EMPTY);
        };
    }
    protected void init() {
        boolean isChallengeWorldActive = parent.getUiState().getWorldType().preset()
                .is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world"));
        challengeWorldVariant = isChallengeWorldActive ? MWP_FIELDS.challengeWorld : 0;
        GridLayout gridlayout = new GridLayout();
        gridlayout.setPosition( 20, 50);
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(20).alignVerticallyMiddle();
        int totalGridWidth = 3 * (150 + 10);
        gridlayout.setPosition((this.width - totalGridWidth) / 2, 50);
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(3);
        List<WorldCreationUiState.WorldTypeEntry> presetList = parent.getUiState().getAltPresetList();
        presetList.forEach(entry -> {
            Component label = entry.describePreset();

            String presetId = null;
            int presetCategory = 0;

            if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skyblock_world"))) { presetId = "skyblock"; presetCategory = 2; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world"))) { presetId = "challenge"; presetCategory = 2; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "oneblock_world"))) { presetId = "oneblock"; presetCategory = 2; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "winter_world"))) { presetId = "winter"; presetCategory = 2; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skygrid_world"))) { presetId = "skygrid"; presetCategory = 2; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "flat"))) { presetId = "flat"; presetCategory = 1; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "normal"))) { presetId = "normal"; presetCategory = 1; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "amplified"))) { presetId = "amplified"; presetCategory = 1; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "large_biomes"))) { presetId = "large_biomes"; presetCategory = 1; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "single_biome_surface"))) { presetId = "single_biome"; presetCategory = 1; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "debug_all_block_states"))) { presetId = "debug"; presetCategory = 1; }
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "floating_islands"))) { presetId = "floating_islands"; presetCategory = 3; }
            if (presetCategory != type) {
                return;
            }

            if (presetId != null) {
                int[] range = switch (type) {
                    case 1 -> new int[]{0xFFF589, 0x00FF4D};
                    case 2 -> new int[]{0x274D99, 0x7566FF};
                    case 3 -> new int[]{0xAD2424, 0xFF4949};
                    default -> new int[]{0xFFFFFF, 0xFFFFFF};
                };
                int color = range[0];
                int color2 = range[1];
                label = GradientTextUtil.gradientText(entry.describePreset().getString(), color, color2);
            }
            int bgColor = switch (type) {
                case 1 -> 0xFF213d21;
                case 2 -> 0xFF292352;
                case 3 -> 0xFF401616;
                default -> 0xFF5A5A5A;
            };
            ResourceLocation thumbnail = ResourceLocation.fromNamespaceAndPath("moreworldpresets", "textures/gui/presets/" + presetId + ".png");
            PresetCardButton card = new PresetCardButton(0, 0, 150, thumbnail, label, (button) -> {
                parent.getUiState().setWorldType(entry);
                parent.getUiState().setGenerateStructures(true);
                parent.getUiState().setBonusChest(false);
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "flat")))
                {
                    parent.getUiState().onChanged();
                    PresetEditor presetEditor = parent.getUiState().getPresetEditor();
                    this.minecraft.setScreen(presetEditor.createEditScreen(parent, parent.getUiState().getSettings()));
                    return;
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "single_biome_surface")))
                {
                    parent.getUiState().onChanged();
                    PresetEditor presetEditor = parent.getUiState().getPresetEditor();
                    this.minecraft.setScreen(presetEditor.createEditScreen(parent, parent.getUiState().getSettings()));
                    return;
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skyblock_world")))
                {
                    MWP_FIELDS.SkyblockWorld = 1;
                    parent.getUiState().onChanged();
                    this.minecraft.setScreen(new WorldSettingsScreen(this.parent,0, this));
                    return;
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "oneblock_world")))
                {
                    parent.getUiState().onChanged();
                    parent.getUiState().setGenerateStructures(false);
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skygrid_world")))
                {
                    parent.getUiState().onChanged();
                    parent.getUiState().setGenerateStructures(false);
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world")))
                {
                    MWP_FIELDS.challengeWorld = 1;
                    this.minecraft.setScreen(new WorldSettingsScreen(this.parent, 1, this));
                    parent.getUiState().onChanged();
                    return;
                }
                this.minecraft.setScreen(parent);
            }, bgColor);
            if (entry.isAmplified()) {
                card.setTooltip(Tooltip.create(AMPLIFIED_HELP_TEXT));
            }
            gridlayout$rowhelper.addChild(card);
        });

        gridlayout.arrangeElements();
        gridlayout.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            this.minecraft.setScreen(previousScreen);
        }).bounds(this.width / 2 - 75, this.height - 28, 150, 20).build());
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int bgColor = switch (type) {
            case 1 -> 0xFF0c1c0c;
            case 2 -> 0xFF0e0c1c;
            case 3 -> 0xFF1c0c0c;
            default -> 0xFF000000;
        };
        graphics.fill(0, 0, this.width, this.height, bgColor);

        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.pose().pushPose();
        graphics.pose().scale(2.2f, 2.2f, 1f);
        switch (type) {
            case 1:
                graphics.drawCenteredString(this.font, Component.literal("VANILLA PRESETS"), (int) ((this.width / 2) / 2.2), (int) (15 / 2.2), 0xFF347A34);
                graphics.drawCenteredString(this.font, Component.literal("VANILLA PRESETS"), (int) ((this.width / 2) / 2.2) - 1, (int) (15 / 2.2), 0xFF419941);
                break;
            case 2:
                graphics.drawCenteredString(this.font, Component.literal("CUSTOM PRESETS"), (int) ((this.width / 2) / 2.2), (int) (15 / 2.2), 0xFF3D347A);
                graphics.drawCenteredString(this.font, Component.literal("CUSTOM PRESETS"), (int) ((this.width / 2) / 2.2) - 1, (int) (15 / 2.2), 0xFF4D4199);
                break;
            case 3:
                graphics.drawCenteredString(this.font, Component.literal("LEGACY PRESETS"), (int) ((this.width / 2) / 2.2), (int) (15 / 2.2), 0xFF7A3434);
                graphics.drawCenteredString(this.font, Component.literal("LEGACY PRESETS"), (int) ((this.width / 2) / 2.2) - 1, (int) (15 / 2.2), 0xFF994141);
                break;
        }
        graphics.pose().popPose();
    }
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}