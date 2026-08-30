package hawiwias.worldpresets.mixin.client;


import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;



@Mixin(InventoryScreen.class)
public class InventoryScreenMixin { ;
    @ModifyArg(method = "extractBackground", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GuiGraphicsExtractor.blit (Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V"))
    private Identifier replaceInventoryGui(Identifier resourceLocation) {
        if (MWP_FIELDS.challengeWorld == 3) {
            return Identifier.fromNamespaceAndPath("moreworldpresets", "textures/gui/halfinventory.png");
        }
        return resourceLocation;
    }
}
