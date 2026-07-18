package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import hawiwias.worldpresets.accessor.TemperatureAccessor;
import net.minecraft.SystemReport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntity implements TemperatureAccessor {

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    private static final Set<UUID> hasPlacedNether = new java.util.HashSet<>();
    //SKYBLOCK NETHER ISLAND GENERATION
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
                    templateManager.get(ResourceLocation.fromNamespaceAndPath("moreworldpresets", "skyblock/skyblock_nether"));

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
    //PREVENT FALLING IN SKYBLOCK
    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        if (MWP_FIELDS.isOneblockWorld) {
            if (y >= 64.0 && y <= 65 && x >= -0.2 && x <= 1.2 && z >= -0.2 && z <= 1.2) {
                player.teleportTo(player.serverLevel(), 0.5, 66.6, 0.5, player.getYRot(), player.getXRot());
            }
        }
    }
}