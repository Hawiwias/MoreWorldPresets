package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin (BlockColors.class)

public class BlockColorsMixin {
    @Inject(method = "createDefault", at = @At("RETURN"))
    private static void onCreateDefault(CallbackInfoReturnable<BlockColors> cir) {
        BlockColors colors = cir.getReturnValue();

        colors.register(List.of(winterOrConstant(-8345771)), Blocks.BIRCH_LEAVES);
        colors.register(List.of(winterOrConstant(-10380959)), Blocks.SPRUCE_LEAVES);
    }
    private static BlockTintSource winterOrConstant(int vanillaColor) {
        return new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return MWP_FIELDS.isWinterWorld ? 0xFFFFFFFF : vanillaColor;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return MWP_FIELDS.isWinterWorld ? 0xFFFFFFFF : vanillaColor;
            }
        };
    }
}
