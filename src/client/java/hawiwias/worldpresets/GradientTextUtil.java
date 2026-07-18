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

    public static MutableComponent forPresetId(String id) {
        return switch (id) {
            case "skyblock" -> gradientText("Skyblock", 0x00C9FF, 0x92FE9D);
            case "skygrid" -> gradientText("Skygrid", 0xFF512F, 0xF09819);
            case "oneblock" -> gradientText("Oneblock", 0x11998E, 0x38EF7D);
            case "winter" -> gradientText("Winter World", 0x6EC6FF, 0xB39DDB);
            case "flat" -> gradientText("Superflat", 0x6fef4a, 0xdbcf82);
            case "normal" -> gradientText("Default", 0x0bee16, 0xa4cb4a);
            case "amplified" -> gradientText("Amplified", 0x8E2DE2, 0x4A00E0);
            case "large_biomes" -> gradientText("Large Biomes", 0x56AB2F, 0xA8E063);
            case "single_biome" -> gradientText("Single Biome", 0xBDC3C7, 0x2C3E50);
            case "debug" -> gradientText("Debug Mode", 0xFF0000, 0x8B0000);
            case "floating_islands" -> gradientText("Floating Islands", 0x89F7FE, 0xFFFFFF);
            default -> null;
        };
    }
}