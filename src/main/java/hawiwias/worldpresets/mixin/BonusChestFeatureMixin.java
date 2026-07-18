package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BonusChestFeature.class)
public class BonusChestFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    public void onPlace(FeaturePlaceContext<NoneFeatureConfiguration> ctx, CallbackInfoReturnable<Boolean> cir) {
        if (MWP_FIELDS.challengeWorld == 1) {
            RandomSource random = ctx.random();
            WorldGenLevel level = ctx.level();
            BlockPos blockpos = new BlockPos(0, -64, 0);

            if (level.isEmptyBlock(blockpos) || level.getBlockState(blockpos).getCollisionShape(level, blockpos).isEmpty()) {
                level.setBlock(blockpos, Blocks.CHEST.defaultBlockState(), 2);
                ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(blockpos);
                chest.setLootTable(BuiltInLootTables.SPAWN_BONUS_CHEST);
                BlockState torchState = Blocks.TORCH.defaultBlockState();
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos torchPos = blockpos.relative(direction);
                    if (torchState.canSurvive(level, torchPos)) {
                        level.setBlock(torchPos, torchState, 2);
                    }
                }
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

}