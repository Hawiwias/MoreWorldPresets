package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.world.level.Level.OVERWORLD;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntity {

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    @Unique private int ticksOutside = 0;
    private static final Set<UUID> hasPlacedNether = new java.util.HashSet<>();
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        int i = this.getTicksFrozen();
        boolean nearHeatSource = false;
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
                break;
            }
        }
        if (MWP_FIELDS.isWinterWorld && this.level().canSeeSky(this.blockPosition()) && this.canFreeze() && this.level().isRaining() && !nearHeatSource) {
            ticksOutside++;
            if (ticksOutside > 110 && i < 240) {
                this.setTicksFrozen(i + 4);
            }
        } else if (!this.isInPowderSnow) {
            this.setTicksFrozen(Math.max(0, i - 2));
            ticksOutside = 0;
        }
    }
    @Inject(method = "triggerDimensionChangeTriggers", at = @At("TAIL"))
    private void onDimensionChange(ServerLevel origin, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerLevel currentLevel = player.serverLevel();

        if (currentLevel.dimension() != ServerLevel.NETHER) return;
        if (!MWP_FIELDS.isSkyblockWorld) return;
        if (MWP_FIELDS.SkyblockWorld == 3) return;
        if (hasPlacedNether.contains(player.getUUID())) return;
        hasPlacedNether.add(player.getUUID());

        BlockPos playerPos = player.blockPosition();
        Direction facing = player.getDirection();

        player.getServer().execute(() -> {
            StructureTemplateManager templateManager = currentLevel.getStructureManager();
            Optional<StructureTemplate> templateNether =
                    templateManager.get(new ResourceLocation("moreworldpresets", "skyblock/skyblock_nether"));

            templateNether.ifPresent(t -> {
                Rotation rotation = switch (facing) {
                    case NORTH -> Rotation.NONE;
                    case EAST  -> Rotation.CLOCKWISE_90;
                    case SOUTH -> Rotation.CLOCKWISE_180;
                    case WEST  -> Rotation.COUNTERCLOCKWISE_90;
                    default    -> Rotation.NONE;
                };

                StructurePlaceSettings settings = new StructurePlaceSettings()
                        .setMirror(Mirror.NONE)
                        .setRotation(rotation)
                        .setIgnoreEntities(false)
                        .setKnownShape(true);


                BlockPos placePos = new BlockPos(
                        playerPos.getX() + facing.getStepX() - 2,
                        playerPos.getY() - 3,
                        playerPos.getZ() + facing.getStepZ()
                );

                t.placeInWorld(currentLevel, placePos, placePos, settings, currentLevel.random, 1026);
            });
        });
    }
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (MWP_FIELDS.isOneblockWorld) {
            if (y >= 64.0 && y < 65 && x >= -0.2 && x <= 1.2 && z >= -0.2 && z <= 1.2) {
                player.teleportTo(player.serverLevel(), player.getX(), 66.6, player.getZ(), player.getYRot(), player.getXRot());
            }
        }
    }
}