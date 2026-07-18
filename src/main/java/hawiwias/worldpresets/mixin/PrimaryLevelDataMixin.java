package hawiwias.worldpresets.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Lifecycle;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.PhaseManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.serialization.Dynamic;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin {
    @Inject(method = "parse", at = @At("RETURN"))
    private static <T> void onParse(Dynamic<T> dynamic, LevelSettings levelSettings, PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        boolean winter = dynamic.get("winterworld").asBoolean(false);
        int challenge = dynamic.get("challengeworld").asInt(0);
        boolean skyblock = dynamic.get("skyblockworld").asBoolean(false);
        boolean oneblock = dynamic.get("oneblockworld").asBoolean(false);
        boolean skygrid = dynamic.get("skygridworld").asBoolean(false);

        String presetLabel = "Vanilla";
        if (winter) presetLabel = "Winter World";
        else if (challenge > 0) presetLabel = "Challenge World " + challenge;
        else if (skyblock) presetLabel = "Skyblock";
        else if (oneblock) presetLabel = "Oneblock";
        else if (skygrid) presetLabel = "Skygrid";


        MWP_FIELDS.isWinterWorld = winter;
        MWP_FIELDS.challengeWorld = challenge;
        PhaseManager.currentPhaseProgress = dynamic.get("currentPhaseProgress").asInt(0);
        PhaseManager.currentPhaseIndex = dynamic.get("currentPhaseIndex").asInt(0);
        MWP_FIELDS.isSkyblockWorld = skyblock;
        MWP_FIELDS.isOneblockWorld = oneblock;
        MWP_FIELDS.isSkygridWorld = skygrid;
        for (int j = 0; j < PhaseManager.phases.size(); j++) {
            PhaseManager.phases.get(j).unlocked = dynamic.get("phase_unlocked_" + j).asBoolean(j == 0);
        }
    }

    @Inject(method = "setTagData", at = @At("TAIL"))
    private void onSetTagData(RegistryAccess registryAccess, CompoundTag tag, CompoundTag playerTag, CallbackInfo ci) {
        tag.putString("worldPresetKey", MWP_FIELDS.presetKey);
        tag.putBoolean("winterworld", MWP_FIELDS.isWinterWorld);
        tag.putInt("challengeworld", MWP_FIELDS.challengeWorld);
        tag.putInt("currentPhaseProgress", PhaseManager.currentPhaseProgress);
        tag.putInt("currentPhaseIndex", PhaseManager.currentPhaseIndex);
        tag.putBoolean("skyblockworld", MWP_FIELDS.isSkyblockWorld);
        tag.putBoolean("oneblockworld", MWP_FIELDS.isOneblockWorld);
        tag.putBoolean("skygridworld", MWP_FIELDS.isSkygridWorld);
        for (int j = 0; j < PhaseManager.phases.size(); j++) {
            tag.putBoolean("phase_unlocked_" + j, PhaseManager.phases.get(j).unlocked);
        }
    }
}