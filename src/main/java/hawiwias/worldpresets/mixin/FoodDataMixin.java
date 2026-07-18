package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @ModifyVariable(method = "setFoodLevel", at = @At("HEAD"), argsOnly = true)
    private int clampFoodLevel(int foodLevel) {
        if (MWP_FIELDS.challengeWorld == 3) {
            return Math.min(foodLevel, 10);
        }
        return foodLevel;
    }
}