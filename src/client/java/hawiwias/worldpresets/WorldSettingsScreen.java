package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WorldSettingsScreen extends Screen {
    protected final Screen previousScreen;
    protected final CreateWorldScreen parent;
    protected int type;
    public WorldSettingsScreen(CreateWorldScreen sr5tx , int type, Screen previousScreen) {
        super(Component.literal("Version Selector"));
        parent = sr5tx;
        this.type = type;
        this.previousScreen = previousScreen;
    }
    protected void init() {
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(20).alignVerticallyMiddle();
        int totalGridWidth = 3 * (150 + 10);
        gridlayout.setPosition((this.width - totalGridWidth) / 2, 50);
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(3);
        List<String> thumbnails = List.of();
        List<String> versions = List.of();
        int[] values = new int[0];
        Tooltip[] tooltips = new Tooltip[0];
        if (this.type == 0) {
            gridlayout.setPosition((this.width - totalGridWidth) / 2, (this.height / 2) - 55);
            thumbnails = Arrays.asList("skyblock_latest", "skyblock_3x3", "skyblock_original");
            versions = Arrays.asList("Skyblock V2.1", "Skyblock V2.0", "Skyblock V1.1 (original)");
            values = new int[]{1, 2, 3};
            tooltips = new Tooltip[]{
                    Tooltip.create(Component.translatable("generator.moreworldpresets.skyblock_2_1.info")),
                    Tooltip.create(Component.translatable("generator.moreworldpresets.skyblock_2_0.info")),
                    Tooltip.create(Component.translatable("generator.moreworldpresets.skyblock_1_1.info"))
            };
        } else {
            thumbnails = Arrays.asList("random_challenge_world", "challenge_world1", "challenge_world2", "challenge_world3", "challenge_world4");
            versions = Arrays.asList("Random Challenge World", "Challenge World 1", "Challenge World 2", "Challenge World 3", "Challenge World 4");
            values = new int[]{5, 1, 2, 3, 4};
            tooltips = new Tooltip[]{
                    Tooltip.create(Component.empty()),
                    Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world1.info")),
                    Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world2.info")),
                    Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world3.info")),
                    Tooltip.create(Component.translatable("generator.moreworldpresets.challenge_world4.info"))
            };
        }
        for (int i = 0; i < versions.size(); i++) {
            int version = values[i];
            Component label = GradientTextUtil.gradientText(versions.get(i), 0x274D99, 0x7566FF);

            Button.OnPress onPress = (this.type == 0)
                    ? (button) -> {
                MWP_FIELDS.SkyblockWorld = version;
                parent.getUiState().setGenerateStructures(false);
                parent.getUiState().setBonusChest(false);
                this.minecraft.setScreen(parent);
            }
                    : (button) -> {
                MWP_FIELDS.challengeWorld = (version == 5) ? 1 + new Random().nextInt(4) : version;
                parent.getUiState().setGenerateStructures(true);
                parent.getUiState().onChanged();
                this.minecraft.setScreen(parent);
            };

            MoreWorldPresetsClient.PresetCardButton card = new MoreWorldPresetsClient.PresetCardButton(
                    0, 0, 150, ResourceLocation.fromNamespaceAndPath("moreworldpresets", "textures/gui/presets/" + thumbnails.get(i) + ".png"), label, onPress, 0xFF292352);
            card.setTooltip(tooltips[i]);
            gridlayout$rowhelper.addChild(card);
        }
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            this.minecraft.setScreen(previousScreen);
        }).bounds(this.width / 2 - 75, this.height - 28, 150, 20).build());
            gridlayout.arrangeElements();
            gridlayout.visitWidgets(this::addRenderableWidget);
    }
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0 , 0, this.width, this.height, 0xFF0e0c1c);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}