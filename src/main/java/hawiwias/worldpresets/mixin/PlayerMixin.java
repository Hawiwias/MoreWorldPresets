package hawiwias.worldpresets.mixin;


import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.accessor.TemperatureAccessor;
import hawiwias.worldpresets.accessor.heatValues;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.Level.*;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements TemperatureAccessor {
    @Unique
    Block closestBlock;
    @Unique
    private float temperature;


    @Unique private float frozenProgress = 0f;

    @Unique
    private float ambientTemp = 0.0f;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getTemperature() {
        return this.temperature;
    }
    @Override
    public void setTemperature(float value) {
        this.temperature = value;
    }

    @Unique
    private static heatValues getheatValues(net.minecraft.world.level.block.Block block) {
        if (block == Blocks.LAVA)                     return new heatValues(30.0f, 3.0f);
        if (block == Blocks.SOUL_FIRE) return new heatValues(29.0f, 2.5f);
        if (block == Blocks.FIRE) return new heatValues(27.0f, 2.0f);
        if (block == Blocks.SOUL_CAMPFIRE) return new heatValues(29.0f, 2.0f);
        if (block == Blocks.CAMPFIRE) return new heatValues(22.0f, 1.8f);
        if (block == Blocks.MAGMA_BLOCK)              return new heatValues(26.0f, 1.5f);
        if (block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER) return new heatValues(16.0f, 1.0f);
        if (block == Blocks.TORCH || block == Blocks.WALL_TORCH || block == Blocks.LANTERN || block == Blocks.SOUL_LANTERN || block == Blocks.GLOWSTONE) {
            return new heatValues(8.0f, 0.5f);
        }
        return new heatValues(20.0f, 1.0f);
    }


    @Unique private int ticksOutside = 0;
    @Unique private float coldMeter = 0f; // 0-100

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        int i = this.getTicksFrozen();
        boolean nearHeatSource = false;
        double closestDistance = 999.0;
        for (BlockPos nearby : BlockPos.betweenClosed(
                blockPosition().offset(-2, -2, -2),
                blockPosition().offset(2, 2, 2))) {
            BlockState nearState = level().getBlockState(nearby);
            if (nearState.is(Blocks.FIRE)
                    || nearState.is(Blocks.CAMPFIRE) && nearState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT)
                    || nearState.is(Blocks.SOUL_CAMPFIRE) && nearState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT)
                    || nearState.is(Blocks.SOUL_FIRE)
                    || nearState.is(Blocks.LAVA)
                    || nearState.is(Blocks.MAGMA_BLOCK)
                    || nearState.is(Blocks.TORCH)
                    || nearState.is(Blocks.WALL_TORCH)
                    || nearState.is(Blocks.LANTERN)
                    || nearState.is(Blocks.SOUL_LANTERN)
                    || nearState.is(Blocks.SMOKER) && nearState.getValue(net.minecraft.world.level.block.SmokerBlock.LIT)
                    || nearState.is(Blocks.GLOWSTONE)
                    || nearState.is(Blocks.FURNACE) && nearState.getValue(net.minecraft.world.level.block.FurnaceBlock.LIT)
                    || nearState.is(Blocks.BLAST_FURNACE) && nearState.getValue(net.minecraft.world.level.block.BlastFurnaceBlock.LIT)) {
                nearHeatSource = true;
                double distance = position().distanceTo(Vec3.atCenterOf(nearby));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestBlock = nearState.getBlock();
                }
            }
        }
        float currentTemp = getTemperature();
        if (nearHeatSource) {
            heatValues heatSettings = getheatValues(closestBlock);
            float newTemp = Math.min(heatSettings.maxTemp, currentTemp + heatSettings.multiplier * 0.01f);
            setTemperature(newTemp);
        }
        else {
            float newTemp = currentTemp + (ambientTemp - currentTemp) * 0.01f;

            setTemperature(newTemp);
        }
        float temp = getTemperature();
        boolean inWater = level().getBlockState(this.blockPosition()).getBlock() == Blocks.WATER;
        boolean canSeeSky = this.level().canSeeSky(this.blockPosition());
        boolean isBlizzard = canSeeSky && this.level().isRaining() && temp <= -30f;
        if (MWP_FIELDS.isWinterWorld && !nearHeatSource && (canSeeSky || inWater) && temp < -11f) {
            coldMeter = Math.min(100f, coldMeter + 5f);
        } else if (temp > -9f && !this.isInPowderSnow) {
            coldMeter = Math.max(0f, coldMeter - 5f);
        }
// dead zone: coldMeter unchanged

        float frozenTarget;
        if (coldMeter >= 60f) {
            frozenTarget = isBlizzard ? 300f : 200f;
        } else if (coldMeter <= 40f) {
            frozenTarget = 0f;
        } else {
            frozenTarget = frozenProgress; // between 40-60, hold lerp target
        }

        frozenProgress += (frozenTarget - frozenProgress) * 0.015f;
        frozenProgress = Math.max(0f, frozenProgress);
        this.setTicksFrozen(frozenProgress > 5f ? (int) frozenProgress : 0);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void temperatureHandling(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (MWP_FIELDS.isWinterWorld && player.level().dimension().equals(OVERWORLD)) {
            long time = player.level().getDayTime() % 24000;
            float minTemp = -20.0f;
            float maxTemp = 1.0f;
            float peakTime = 6000f;
            float angle = (float)((time - peakTime) / 24000.0 * 2.0 * Math.PI);
            float heatCurve = (float)Math.cos(angle);
            float t = (heatCurve + 1.0f) / 2.0f;
            float baseTemp = minTemp + t * (maxTemp - minTemp);
            baseTemp = Math.max(minTemp, Math.min(maxTemp, baseTemp));

            int y = player.blockPosition().getY();
            float depth = Math.max(0f, level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, player.blockPosition().getX(), player.blockPosition().getZ()) - y);

            boolean canSeeSky = player.level().canSeeSky(player.blockPosition());
            boolean isRaining = player.level().isRaining();


            float surfaceTarget = (canSeeSky && isRaining) ? -40.0f : baseTemp;

            float targetAmbient;
            if (depth > 8) {
                float geothermalTemp = Math.min(12.0f, depth * 0.3f);
                targetAmbient = Math.max(surfaceTarget, geothermalTemp);
            }
            else {
                targetAmbient = surfaceTarget;
            }
            if (level().getBlockState(player.blockPosition()).getBlock() != Blocks.WATER) {
                ambientTemp += (targetAmbient - ambientTemp) *  0.005f;
            }
            else {
                ambientTemp += (-23f - ambientTemp) *  0.05f;
            }
        }
        if (MWP_FIELDS.isWinterWorld && player.level().dimension().equals(NETHER)) ambientTemp = 30.0f;
        if (MWP_FIELDS.isWinterWorld && player.level().dimension().equals(END))    ambientTemp = 9.0f;
    }
}
