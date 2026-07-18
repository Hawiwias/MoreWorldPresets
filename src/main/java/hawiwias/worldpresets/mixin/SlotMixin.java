package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {
    private static final int FIRST_RESTRICTED_SLOT = 22;
    private static final int LAST_RESTRICTED_SLOT = 35;

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void restrictPlacement(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (MWP_FIELDS.challengeWorld != 3) {
            return;
        }
        Slot self = (Slot) (Object) this;
        if (self.container instanceof net.minecraft.world.entity.player.Inventory
                && self.getContainerSlot() >= FIRST_RESTRICTED_SLOT
                && self.getContainerSlot() <= LAST_RESTRICTED_SLOT) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "isHighlightable", at = @At("RETURN"), cancellable = true)
    private void disableHighlight(CallbackInfoReturnable<Boolean> cir) {
        if (MWP_FIELDS.challengeWorld != 3) {
            return;
        }
        Slot self = (Slot) (Object) this;
        if (self.container instanceof Inventory
                && self.getContainerSlot() >= FIRST_RESTRICTED_SLOT
                && self.getContainerSlot() <= LAST_RESTRICTED_SLOT) {
            cir.setReturnValue(false);
        }
    }
}