package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.PresetLabelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.File;
import java.nio.file.Path;

@Mixin(LevelSummary.class)
public class LevelSummaryMixin implements PresetLabelAccessor {

    @Shadow @Final private Path icon;
    @Unique
    private boolean moreworldpresets$computed = false;

    @Unique
    private String moreworldpresets$presetId;

    @Override
    public String moreworldpresets$getPresetLabel() {
        if (!moreworldpresets$computed) {
            moreworldpresets$computed = true;
            try {
                File levelDatFile = this.icon.getParent().resolve("level.dat").toFile();
                if (levelDatFile.exists()) {
                    CompoundTag root = NbtIo.readCompressed(levelDatFile.toPath(), NbtAccounter.unlimitedHeap());
                    CompoundTag data = root.getCompound("Data").orElseThrow();
                    if (data.getBoolean("winterworld").orElse(false)) moreworldpresets$presetId = "winter";
                    else if (data.getInt("challengeworld").orElse(0) > 0) moreworldpresets$presetId = "challenge:" + data.getInt("challengeworld").orElse(0);
                    else if (data.getBoolean("skyblockworld").orElse(false)) moreworldpresets$presetId = "skyblock";
                    else if (data.getBoolean("oneblockworld").orElse(false)) moreworldpresets$presetId = "oneblock";
                    else if (data.getBoolean("skygridworld").orElse(false)) moreworldpresets$presetId = "skygrid";
                    else {
                        String rawKey = data.getString("worldPresetKey").orElse("");
                        moreworldpresets$presetId = switch (rawKey) {
                            case "minecraft:normal" -> "normal";
                            case "minecraft:flat" -> "flat";
                            case "minecraft:amplified" -> "amplified";
                            case "minecraft:large_biomes" -> "large_biomes";
                            case "minecraft:single_biome_surface" -> "single_biome";
                            case "minecraft:debug_all_block_states" -> "debug";
                            case "moreworldpresets:floating_islands" -> "floating_islands";
                            default -> null;
                        };
                    }
                }
            } catch (Exception ignored) {}
        }
        return moreworldpresets$presetId;
    }
}