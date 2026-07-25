package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SnowAndFreezeFeature.class)
public class SnowAndFreezeFeatureMixin {

/**
 * @author Hawiwias
 * @reason snow and freeze generation
 */
@Overwrite  public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> p_160368_) {
    WorldGenLevel worldgenlevel = p_160368_.level();
    BlockPos blockpos = p_160368_.origin();
    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
    BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

    for(int i = 0; i < 16; ++i) {
        for(int j = 0; j < 16; ++j) {
            int k = blockpos.getX() + i;
            int l = blockpos.getZ() + j;
            int i1 = worldgenlevel.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l);
            if (MWP_FIELDS.isWinterWorld)
            {
                BlockPos leavesPos = new BlockPos(k, worldgenlevel.getHeight(Heightmap.Types.MOTION_BLOCKING, k, l) - 1, l);
                BlockState leavesState = worldgenlevel.getBlockState(leavesPos);
                if (leavesState.is(BlockTags.LEAVES) && MWP_FIELDS.isWinterWorld) {
                    BlockPos aboveLeaves = leavesPos.above();
                    if (worldgenlevel.getBlockState(aboveLeaves).isAir()) {
                        worldgenlevel.setBlock(aboveLeaves, Blocks.SNOW.defaultBlockState(), 2);
                    }
                }
                i1 = worldgenlevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, k, l);
            }
            blockpos$mutableblockpos.set(k, i1, l);
            blockpos$mutableblockpos1.set(blockpos$mutableblockpos).move(Direction.DOWN, 1);
            Biome biome = worldgenlevel.getBiome(blockpos$mutableblockpos).value();
            BlockState below = worldgenlevel.getBlockState(blockpos$mutableblockpos1);
            if (biome.shouldFreeze(worldgenlevel, blockpos$mutableblockpos1, false) || (MWP_FIELDS.isWinterWorld && worldgenlevel.getBlockState(blockpos$mutableblockpos1).is(Blocks.WATER) && worldgenlevel.getBlockState(blockpos$mutableblockpos1).getFluidState().isSource())) {
                worldgenlevel.setBlock(blockpos$mutableblockpos1, Blocks.ICE.defaultBlockState(), 2);
            }

            if (biome.shouldSnow(worldgenlevel, blockpos$mutableblockpos) || (MWP_FIELDS.isWinterWorld && !below.is(Blocks.WATER) && !below.is(Blocks.LAVA) && !below.is(Blocks.ICE) && below.isFaceSturdy(worldgenlevel, blockpos$mutableblockpos1, Direction.UP))) {
                worldgenlevel.setBlock(blockpos$mutableblockpos, Blocks.SNOW.defaultBlockState(), 2);
                BlockState above = worldgenlevel.getBlockState(blockpos$mutableblockpos.above());
                if (above.is(Blocks.TALL_GRASS) || above.is(Blocks.LARGE_FERN)
                        || above.is(BlockTags.FLOWERS)) {
                    worldgenlevel.setBlock(blockpos$mutableblockpos.above(), Blocks.AIR.defaultBlockState(), 3);
                }
                BlockState blockstate = worldgenlevel.getBlockState(blockpos$mutableblockpos1);
                if (blockstate.hasProperty(SnowyBlock.SNOWY)) {
                    worldgenlevel.setBlock(blockpos$mutableblockpos1, blockstate.setValue(SnowyBlock.SNOWY, Boolean.TRUE), 2);
                }
            }
        }
    }

    return true;
}
}
