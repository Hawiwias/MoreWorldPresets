package hawiwias.worldpresets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class PresetCardButton extends Button {
    private final Identifier thumbnail;
    private final Component gradientLabel;
    private final int imgHeight;
    private final int color;

        public PresetCardButton(int x, int y, int w, Identifier thumbnail, Component gradientLabel, OnPress onPress, int color) {
            super(x, y, w, calcHeight(w), Component.empty(), onPress, DEFAULT_NARRATION);
            this.thumbnail = thumbnail;
            this.color = color;
            this.gradientLabel = gradientLabel;
            this.imgHeight = (int) ((w - 8) / (1920f / 1080f));
        }

        private static int calcHeight(int w) {
            int imgH = (int) ((w - 8) / (1920f / 1080f));
            return imgH + 8 + 24;
        }

        @Override
        public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {			int imgX = getX() + 4;
            int imgY = getY() + 4;
            int imgW = getWidth() - 8;
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), isHovered() ? color + 0x040404 : color);
            graphics.outline(getX(), getY(), getWidth(), getHeight(), color + 0x101010);
            graphics.blit(thumbnail, imgX, imgY, imgX + imgW, imgY + imgHeight, 0f, 1f, 0f, 1f);
            graphics.outline(getX() + 4, getY() + 4, getWidth() - 8, imgHeight, color + 0x101010);
            Font font = Minecraft.getInstance().font;
            int labelY = getY() + 4 + imgHeight + (getHeight() - (4 + imgHeight) - font.lineHeight) / 2;
            int totalWidth = font.width(gradientLabel);
            int textX = getX() + (getWidth() - totalWidth) / 2;

            if (gradientLabel.getSiblings().isEmpty()) {
                graphics.text(font, gradientLabel.getString(), textX, labelY, 0xFFFFFF, true);
            } else {
                for (Component sibling : gradientLabel.getSiblings()) {
                    String text = sibling.getString();
                    int rgb = sibling.getStyle().getColor() != null ? sibling.getStyle().getColor().getValue() : 0xFFFFFF;
                    int textColor = 0xFF000000 | rgb;
                    graphics.text(font, text, textX, labelY, textColor, true);
                    textX += font.width(text);
                }
            }
        }
    }
