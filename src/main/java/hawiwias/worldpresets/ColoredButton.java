package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ColoredButton extends Button {
        private final int bgColor;

        public ColoredButton(int x, int y, int w, int h, Component msg, OnPress onPress, int bgColor) {
            super(x, y, w, h, msg, onPress, DEFAULT_NARRATION);
            this.bgColor = bgColor;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int color = isHovered() ? bgColor + 0x040404 : bgColor;
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), color);
            graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), bgColor + 0x101010);
            int textColor = active ? 0xFFFFFF : 0xA0A0A0;
            graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, textColor);
        }
    }