package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
    private static final int FIRST_RESTRICTED_SLOT = 22;
    private static final int LAST_RESTRICTED_SLOT = 35;

    @Inject(method = "getFreeSlot", at = @At("RETURN"), cancellable = true)
    private void preventRestrictedFreeSlot(CallbackInfoReturnable<Integer> cir) {
        if (MWP_FIELDS.challengeWorld != 3) {
            return;
        }

        int slot = cir.getReturnValue();

        if (slot >= FIRST_RESTRICTED_SLOT && slot <= LAST_RESTRICTED_SLOT) {
            cir.setReturnValue(-1);
        }
    }

    @Inject(method = "getSlotWithRemainingSpace", at = @At("RETURN"), cancellable = true)
    private void preventRestrictedStackingSlot(CallbackInfoReturnable<Integer> cir) {
        if (MWP_FIELDS.challengeWorld != 3) {
            return;
        }

        int slot = cir.getReturnValue();

        if (slot >= FIRST_RESTRICTED_SLOT && slot <= LAST_RESTRICTED_SLOT) {
            cir.setReturnValue(-1);
        }
    }
}
