package hawiwias.worldpresets.mixin.client;

import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$GameTab")
public class CreateWorldScreenGameTabMixin extends GridLayoutTab {

    public CreateWorldScreenGameTabMixin(Component component) {
        super(component);
    }
}