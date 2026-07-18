package hawiwias.worldpresets.mixin;


import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(StructurePlacement.class)
public class StructurePlacementMixin {
    Random random = new Random();
    @Inject(method = "isStructureChunk", at = @At(value = "HEAD"), cancellable = true)
    private void ChallengeWorld2(ChunkGeneratorStructureState chunkGeneratorStructureState, int i, int j, CallbackInfoReturnable<Boolean> cir) {
        if (MWP_FIELDS.challengeWorld == 2) {
            if ((Object) this instanceof ConcentricRingsStructurePlacement) {
                return;
            }
            if (random.nextInt(300) == 1) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }
}
