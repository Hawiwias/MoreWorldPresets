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
import net.minecraft.network.chat.MutableComponent;
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
    private Tooltip challengeWorldTooltip(Integer value) {
        return switch (value) {
            case 1 -> Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world1.info"));
            case 2 -> Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world2.info"));
            case 3 -> Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world3.info"));
            case 4 -> Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world4.info"));
            default -> Tooltip.create(CommonComponents.EMPTY);
        };
    }
    private MutableComponent gradientText(String text, int startColor, int endColor) {
        MutableComponent result = Component.empty();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            float ratio = length <= 1 ? 0 : (float) i / (length - 1);
            int color = lerpColor(startColor, endColor, ratio);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style -> style.withColor(color)));
        }
        return result;
    }

    private int lerpColor(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }
    protected void init() {
        boolean isChallengeWorldActive = parent.getUiState().getWorldType().preset()
                .is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world"));
        challengeWorldVariant = isChallengeWorldActive ? MWP_FIELDS.challengeWorld : 0;
        GridLayout gridlayout = new GridLayout();
        gridlayout.setPosition(this.width / 2 - 210, 50);
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(2);
        List<WorldCreationUiState.WorldTypeEntry> presetList = parent.getUiState().getAltPresetList();
        presetList.forEach(entry -> {
            if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world"))) {
                CycleButton<Integer> cycleButton = CycleButton.<Integer>builder(this::challengeWorldLabel)
                        .withValues(0, 1, 2, 3, 4, 5)
                        .withInitialValue(challengeWorldVariant)
                        .withTooltip(this::challengeWorldTooltip)
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
            Component label = entry.describePreset();

            String presetId = null;
            if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skyblock_world"))) presetId = "skyblock";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skygrid_world"))) presetId = "skygrid";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "oneblock_world"))) presetId = "oneblock";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "winter_world"))) presetId = "winter";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "flat"))) presetId = "flat";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "normal"))) presetId = "normal";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "amplified"))) presetId = "amplified";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "large_biomes"))) presetId = "large_biomes";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "single_biome_surface"))) presetId = "single_biome";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("minecraft", "debug_all_block_states"))) presetId = "debug";
            else if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "floating_islands"))) presetId = "floating_islands";
            if (presetId != null) {
                MutableComponent gradient = GradientTextUtil.forPresetId(presetId);
                if (gradient != null) {
                    label = gradient;
                }
            }
            Button.Builder builder = Button.builder(label, (button) -> {
                parent.getUiState().setWorldType(entry);
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
                    parent.getUiState().onChanged();
                    this.minecraft.setScreen(new SkyblockSettingsScreen(this.parent));
                    return;
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "oneblock_world")))
                {
                    parent.getUiState().onChanged();
                    parent.getUiState().setGenerateStructures(false);
                }
                if (entry.preset().is(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world")))
                {
                    parent.getUiState().onChanged();
                    parent.getUiState().setGenerateStructures(true);
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
        String text = switch (value) {
            case 0 -> "Challenge World: Disabled";
            case 5 -> "Challenge World: Random";
            default -> "Challenge World: " + value;
        };
        return GradientTextUtil.gradientText(text, 0xF7971E, 0xFFD200);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}