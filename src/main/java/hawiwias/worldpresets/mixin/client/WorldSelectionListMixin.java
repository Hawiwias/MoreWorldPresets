package hawiwias.worldpresets.mixin.client;

import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldSelectionList.class)
public class WorldSelectionListMixin {

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ObjectSelectionList;<init>(Lnet/minecraft/client/Minecraft;IIII)V"
            ),
            index = 4
    )
    private static int increaseItemHeight(int itemHeight) {
        return itemHeight + 2;
    }
}