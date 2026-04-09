package hawiwias.worldpresets.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowLayerBlock.class)
public class SnowLayerBlockMixin {
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void onRandomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        boolean nearHeatSource = false;
        for (BlockPos nearby : BlockPos.betweenClosed(
                blockPos.offset(-2, -2, -2),
                blockPos.offset(2, 2, 2))) {
            BlockState nearState = serverLevel.getBlockState(nearby);
            if (nearState.is(Blocks.FIRE)
                    || nearState.is(Blocks.CAMPFIRE) && nearState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT)
                    || nearState.is(Blocks.SOUL_CAMPFIRE) && nearState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT)
                    || nearState.is(Blocks.SOUL_FIRE)
                    || nearState.is(Blocks.LAVA)
                    || nearState.is(Blocks.MAGMA_BLOCK)
                    || nearState.is(Blocks.TORCH)
                    || nearState.is(Blocks.WALL_TORCH)
                    || nearState.is(Blocks.LANTERN)
                    || nearState.is(Blocks.SOUL_LANTERN)
                    || nearState.is(Blocks.SMOKER) && nearState.getValue(net.minecraft.world.level.block.SmokerBlock.LIT)
                    || nearState.is(Blocks.GLOWSTONE)
                    || nearState.is(Blocks.FURNACE) && nearState.getValue(net.minecraft.world.level.block.FurnaceBlock.LIT)
                    || nearState.is(Blocks.BLAST_FURNACE) && nearState.getValue(net.minecraft.world.level.block.BlastFurnaceBlock.LIT)) {
                nearHeatSource = true;
                break;
            }
        }
        if (!nearHeatSource) {
            ci.cancel();
        }
    }
}

