package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CategorySelectionScreen extends Screen {
    protected final CreateWorldScreen parent;
    private Button backButton;
    public CategorySelectionScreen(CreateWorldScreen rf6tg7yhu, Screen previousScreen) {
        super(Component.empty());
        parent = rf6tg7yhu;
    }
    protected void init() {
        int third = this.width / 3;

        backButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            this.minecraft.setScreen(parent);
        }).bounds(this.width / 2 - 75, this.height - 28, 150, 20).build());

        this.addRenderableWidget(new MoreWorldPresetsClient.ColoredButton(0, 0, third, this.height, Component.empty(), (button) -> this.minecraft.setScreen(new WorldPresetsScreen(this.parent, this, 1)), 0xFF0c1c0c));
        this.addRenderableWidget(new MoreWorldPresetsClient.ColoredButton(third, 0, third, this.height, Component.empty(), (button) -> this.minecraft.setScreen(new WorldPresetsScreen(this.parent, this, 2)), 0xFF0e0c1c));
        this.addRenderableWidget(new MoreWorldPresetsClient.ColoredButton(third * 2, 0, this.width - third * 2, this.height, Component.empty(), (button) -> this.minecraft.setScreen(new WorldPresetsScreen(this.parent, this, 3)), 0xFF1c0c0c));
    }
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int third = this.width / 3;
        //TITLES
        graphics.pose().pushPose();
        graphics.pose().scale(1.8f, 1.8f, 1f);
        graphics.drawCenteredString(this.font, Component.literal("VANILLA PRESETS"), (int)((third / 2) / 1.8), (int) (90 / 1.8), 0xFF347A34);
        graphics.drawCenteredString(this.font, Component.literal("VANILLA PRESETS"), (int)((third / 2) / 1.8) - 1, (int) ((90) / 1.8), 0xFF419941);
        graphics.drawCenteredString(this.font, Component.literal("CUSTOM PRESETS"), (int)(((third / 2) + third) / 1.8), (int) (90 / 1.8), 0xFF3D347A);
        graphics.drawCenteredString(this.font, Component.literal("CUSTOM PRESETS"), (int)(((third / 2) + third) / 1.8) - 1, (int) ((90) / 1.8), 0xFF4D4199);
        graphics.drawCenteredString(this.font, Component.literal("LEGACY PRESETS"), (int)(((third / 2) + third * 2) / 1.8), (int) (90 / 1.8), 0xFF7A3434);
        graphics.drawCenteredString(this.font, Component.literal("LEGACY PRESETS"), (int)((((third / 2) + third * 2) / 1.8) - 1), (int) ((90) / 1.8), 0xFF994141);
        graphics.pose().popPose();
        //ICONS
        float itemScale = 4.5f;
        float itemSize = 16f * itemScale;
        int itemY = (int) (10f / itemScale);
        float thirdF = this.width / 3f;
        float centerX = thirdF / 2f;
        float[] centers = { centerX, centerX + thirdF, centerX + thirdF * 2 };
        Item[] icons = { Items.OAK_LOG, Items.COMMAND_BLOCK, Items.COBBLESTONE };

        for (int i = 0; i < 3; i++) {
            int x = (int) ((centers[i] - itemSize / 2f) / itemScale);
            graphics.pose().pushPose();
            graphics.pose().scale(itemScale, itemScale, 1f);
            graphics.renderFakeItem(icons[i].getDefaultInstance(), x, itemY);
            graphics.pose().popPose();
        }
        //BULLET LISTS
        graphics.drawString(this.font, Component.literal("• Floating Islands"), (int) centers[2] - font.width("LEGACY PRESETS"), 130, 0xFF7A3434);
        //TODO: IMPLEMENT OLD GENERATION
//        graphics.drawString(this.font, Component.literal("• Classic World"), (int) centers[2] - font.width("LEGACY PRESETS"), 150, 0xFF7A3434);
//        graphics.drawString(this.font, Component.literal("• Indev World"), (int) centers[2] - font.width("LEGACY PRESETS"), 170, 0xFF7A3434);
//        graphics.drawString(this.font, Component.literal("• Infdev World"), (int) centers[2] - font.width("LEGACY PRESETS"), 190, 0xFF7A3434);
        graphics.drawString(this.font, Component.literal("• Winter World"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 130, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Oneblock"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 150, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Skyblock"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 170, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Skygrid"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 190, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Challenge World 1"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 210, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Challenge World 2"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 230, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Challenge World 3"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 250, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Challenge World 4"), (int) (centers[1] - font.width("CUSTOM PRESETS")), 270, 0xFF3D347A);
        graphics.drawString(this.font, Component.literal("• Default"), (int) (centers[0] - font.width("VANILLA PRESETS")), 130, 0xFF347A34);
        graphics.drawString(this.font, Component.literal("• Large Biomes"), (int) (centers[0] - font.width("VANILLA PRESETS")), 150, 0xFF347A34);
        graphics.drawString(this.font, Component.literal("• Single Biome"), (int) (centers[0] - font.width("VANILLA PRESETS")), 170, 0xFF347A34);
        graphics.drawString(this.font, Component.literal("• Superflat"), (int) (centers[0] - font.width("VANILLA PRESETS")), 190, 0xFF347A34);
        graphics.drawString(this.font, Component.literal("• Debug Mode"), (int) (centers[0] - font.width("VANILLA PRESETS")), 210, 0xFF347A34);
        graphics.drawString(this.font, Component.literal("• Amplified"), (int) (centers[0] - font.width("VANILLA PRESETS")), 230, 0xFF347A34);
        backButton.render(graphics, mouseX, mouseY, partialTick);
    }
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

}
