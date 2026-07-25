package hawiwias.worldpresets.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.accessor.TemperatureAccessor;
import hawiwias.worldpresets.accessor.heatValues;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.Level.*;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements TemperatureAccessor {
    Player player = (Player) (Object) this;
    @Unique
    Block closestBlock;
    @Unique
    private float frozenProgress = 0f;

    @Unique
    private float ambientTemp = 0.0f;

    @Unique
    private float temperature = 0.0f;

    @Unique
    private boolean tempInitialized = false;

    @Override
    public float getTemperature() {
        return this.temperature;
    }

    @Override
    public void setTemperature(float value) {
        this.temperature = value;
    }

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private static heatValues getheatValues(net.minecraft.world.level.block.Block block) {
        if (block == Blocks.LAVA) return new heatValues(30.0f, 3.0f);
        if (block == Blocks.SOUL_FIRE) return new heatValues(29.0f, 2.5f);
        if (block == Blocks.FIRE) return new heatValues(27.0f, 2.0f);
        if (block == Blocks.SOUL_CAMPFIRE) return new heatValues(29.0f, 2.0f);
        if (block == Blocks.CAMPFIRE) return new heatValues(22.0f, 1.8f);
        if (block == Blocks.MAGMA_BLOCK) return new heatValues(26.0f, 1.5f);
        if (block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER)
            return new heatValues(16.0f, 1.0f);
        if (block == Blocks.TORCH || block == Blocks.WALL_TORCH || block == Blocks.LANTERN || block == Blocks.SOUL_LANTERN || block == Blocks.GLOWSTONE) {
            return new heatValues(8.0f, 0.5f);
        }
        return new heatValues(20.0f, 1.0f);
    }

    @Unique
    private float coldMeter = 0f; // 0-100

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
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
        } else {
            setTemperature(currentTemp + (ambientTemp - currentTemp) * 0.01f);
        }

        float temp = getTemperature();
        boolean inWater = level().getBlockState(this.blockPosition()).getBlock() == Blocks.WATER;
        boolean canSeeSky = this.level().canSeeSky(this.blockPosition());
        boolean isBlizzard = canSeeSky && this.level().isRaining() && temp <= -30f;


        boolean shouldFreeze = MWP_FIELDS.isWinterWorld && !nearHeatSource && (canSeeSky || inWater) && temp < -10f;
        boolean shouldWarm = nearHeatSource || temp > -8f;

        if (shouldFreeze) {
            coldMeter = Math.min(100f, coldMeter + 1.6f);
        } else if (shouldWarm) {
            coldMeter = Math.max(0f, coldMeter - 0.5f);
        }

        float frozenTarget;
        if (coldMeter >= 80f) {
            frozenTarget = isBlizzard ? 300f : 200f;
        } else if (coldMeter <= 20f) {
            frozenTarget = 0f;
        } else {
            frozenTarget = frozenProgress;
        }

        frozenProgress += (frozenTarget - frozenProgress) * 0.008f;
        frozenProgress = Math.max(0f, frozenProgress);

        int currentFrozen = this.getTicksFrozen();
        int targetFrozen = frozenProgress > 20f ? (int) frozenProgress : 0;

        if (targetFrozen > currentFrozen) {

            this.setTicksFrozen(Math.min(targetFrozen, currentFrozen + 3));
        } else if (targetFrozen < currentFrozen) {

            this.setTicksFrozen(Math.max(targetFrozen, currentFrozen - 1));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void temperatureHandling(CallbackInfo ci) {
        if (MWP_FIELDS.isWinterWorld && player.level().dimension().equals(OVERWORLD)) {
            long time = player.level().getOverworldClockTime() % 24000;
            float minTemp = -20.0f;
            float maxTemp = 1.0f;
            float peakTime = 6000f;
            float angle = (float) ((time - peakTime) / 24000.0 * 2.0 * Math.PI);
            float heatCurve = (float) Math.cos(angle);
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
            } else {
                targetAmbient = surfaceTarget;
            }
            if (!tempInitialized) {
                ambientTemp = targetAmbient;
                setTemperature(targetAmbient);
                if (targetAmbient < -10f) {
                    coldMeter = 100f;
                    frozenProgress = 200f;
                }
                tempInitialized = true;
            } else {
                if (level().getBlockState(player.blockPosition()).getBlock() != Blocks.WATER) {
                    ambientTemp += (targetAmbient - ambientTemp) * 0.005f;
                } else {
                    ambientTemp += (-23f - ambientTemp) * 0.05f;
                }
            }
        }
        if (MWP_FIELDS.isWinterWorld && player.level().dimension().equals(NETHER)) ambientTemp = 30.0f;
        if (MWP_FIELDS.isWinterWorld && player.level().dimension().equals(END)) ambientTemp = 9.0f;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void nbtSaveTemperature(ValueOutput output, CallbackInfo ci) {
        output.putFloat("temperature", getTemperature());
        output.putFloat("ambientTemp", ambientTemp);
        output.putFloat("frozenProgress", frozenProgress);
        output.putFloat("coldMeter", coldMeter);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void nbtLoadTemperature(ValueInput input, CallbackInfo ci) {
        if (input.contains("temperature")) {
            setTemperature(input.getFloatOr("temperature", 0.0f));
            ambientTemp = input.contains("ambientTemp")
                    ? input.getFloatOr("ambientTemp", 0.0f)
                    : input.getFloatOr("temperature", 0.0f);
            frozenProgress = input.contains("frozenProgress")
                    ? input.getFloatOr("frozenProgress", 0.0f) : 0f;
            coldMeter = input.contains("coldMeter")
                    ? input.getFloatOr("coldMeter", 0.0f) : 0f;
        }
    }
    @WrapOperation(method = "maybeBackOffFromEdge", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/player/Player.isStayingOnGroundSurface ()Z"))
    private boolean restrictCrouchingEdge(Player instance, Operation<Boolean> original) {
        return original.call(instance) && MWP_FIELDS.challengeWorld != 3;
    }

    @WrapOperation(method = "getDesiredPose", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/player/Player.isShiftKeyDown ()Z"))
    private boolean restrictCrouching(Player instance, Operation<Boolean> original) {
        return original.call(instance) && MWP_FIELDS.challengeWorld != 3;
    }
}
