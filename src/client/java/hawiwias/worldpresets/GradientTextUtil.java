package hawiwias.worldpresets;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class GradientTextUtil {
    public static MutableComponent gradientText(String text, int startColor, int endColor) {
        MutableComponent result = Component.empty();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            float ratio = length <= 1 ? 0 : (float) i / (length - 1);
            int color = lerpColor(startColor, endColor, ratio);
            result.append(Component.literal(String.valueOf(text.charAt(i))).withStyle(style -> style.withColor(color)));
        }
        return result;
    }

    private static int lerpColor(int start, int end, float ratio) {
        int r1 = (start >> 16) & 0xFF, g1 = (start >> 8) & 0xFF, b1 = start & 0xFF;
        int r2 = (end >> 16) & 0xFF, g2 = (end >> 8) & 0xFF, b2 = end & 0xFF;
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        return (r << 16) | (g << 8) | b;
    }
}