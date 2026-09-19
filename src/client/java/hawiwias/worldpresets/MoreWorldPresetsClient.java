package hawiwias.worldpresets;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
public class MoreWorldPresetsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
	}
	public static class ColoredButton extends Button {
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
	public static class PresetCardButton extends Button {
		private final ResourceLocation thumbnail;
		private final Component gradientLabel;
		private final int imgHeight;
		private final int color;

		public PresetCardButton(int x, int y, int w, ResourceLocation thumbnail, Component gradientLabel, OnPress onPress, int color) {
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
		public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
			graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), isHovered() ? color + 0x040404 : color);
			graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), color + 0x101010);
			graphics.blit(thumbnail, getX() + 4, getY() + 4, 0, 0, getWidth() - 8, imgHeight, getWidth() - 8, imgHeight);
			graphics.renderOutline(getX() + 4, getY() + 4, getWidth() - 8, imgHeight, color + 0x101010);
			Font font = Minecraft.getInstance().font;
			int labelY = getY() + 4 + imgHeight + (getHeight() - (4 + imgHeight) - font.lineHeight) / 2;
			int totalWidth = font.width(gradientLabel);
			int textX = getX() + (getWidth() - totalWidth) / 2;

			if (gradientLabel.getSiblings().isEmpty()) {
				graphics.drawString(font, gradientLabel.getString(), textX, labelY, 0xFFFFFF, true);
			} else {
				for (Component sibling : gradientLabel.getSiblings()) {
					String text = sibling.getString();
					int color = sibling.getStyle().getColor() != null ? sibling.getStyle().getColor().getValue() : 0xFFFFFF;
					graphics.drawString(font, text, textX, labelY, color, true);
					textX += font.width(text);
				}
			}
		}
	}
}