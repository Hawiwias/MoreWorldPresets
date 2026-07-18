package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(InventoryScreen.class)
public class InventoryScreenMixin { ;
    @ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphics.blit (Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    private ResourceLocation replaceInventoryGui(ResourceLocation resourceLocation) {
        if (MWP_FIELDS.challengeWorld == 3) {
            return new ResourceLocation("moreworldpresets", "textures/gui/halfinventory.png");
        }
        return resourceLocation;
    }
}
