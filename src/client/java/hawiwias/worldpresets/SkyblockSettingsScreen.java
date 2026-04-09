package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;

public class SkyblockSettingsScreen extends Screen {
    protected final CreateWorldScreen parent;
    public SkyblockSettingsScreen(CreateWorldScreen sr5tx) {
        super(Component.literal("Version Selector"));
        parent = sr5tx;
    }
    Component SKYBLOCK_2_1_INFO = Component.translatable("generator.moreworldpresets.skyblock_2_1.info");
    Component SKYBLOCK_2_0_INFO = Component.translatable("generator.moreworldpresets.skyblock_2_0.info");
    Component SKYBLOCK_1_1_INFO = Component.translatable("generator.moreworldpresets.skyblock_1_1.info");
    protected void init() {
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(2);
        gridlayout$rowhelper.addChild(Button.builder(Component.literal("Skyblock V2.1"), (button) -> {
            MWP_FIELDS.SkyblockWorld = 1;
            parent.getUiState().setGenerateStructures(false);
            parent.getUiState().setBonusChest(false);
            this.minecraft.setScreen(parent);
        }).width(200).build(), 2, gridlayout$rowhelper.newCellSettings().paddingTop(42)).setTooltip(Tooltip.create(SKYBLOCK_2_1_INFO));
        gridlayout$rowhelper.addChild(Button.builder(Component.literal("Skyblock V2.0"), (button) -> {
            MWP_FIELDS.SkyblockWorld = 2;
            parent.getUiState().setGenerateStructures(false);
            parent.getUiState().setBonusChest(false);
            this.minecraft.setScreen(parent);
        }).width(200).build(), 2, gridlayout$rowhelper.newCellSettings().paddingTop(42)).setTooltip(Tooltip.create(SKYBLOCK_2_0_INFO));
        gridlayout$rowhelper.addChild(Button.builder(Component.literal("Skyblock V1.1 (original)"), (button) -> {
            MWP_FIELDS.SkyblockWorld = 3;
            parent.getUiState().setGenerateStructures(false);
            parent.getUiState().setBonusChest(false);
            this.minecraft.setScreen(parent);
        }).width(200).build(), 2, gridlayout$rowhelper.newCellSettings().paddingTop(42)).setTooltip(Tooltip.create(SKYBLOCK_1_1_INFO));;
        gridlayout.arrangeElements();
        FrameLayout.alignInRectangle(gridlayout, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridlayout.visitWidgets(this::addRenderableWidget);
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