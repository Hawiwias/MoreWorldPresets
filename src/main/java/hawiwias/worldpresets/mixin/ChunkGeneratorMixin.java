package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"), cancellable = true)
    private void onApplyBiomeDecoration(WorldGenLevel worldGenLevel, ChunkAccess chunkAccess, StructureManager structureManager, CallbackInfo ci) {
        if (MWP_FIELDS.isSkyblockWorld) {
            ChunkPos chunkPos = chunkAccess.getPos();
            Optional<StructureTemplate> template = Optional.empty();
            StructureTemplateManager templateManager = worldGenLevel.getLevel().getStructureManager();
            if (chunkPos.x == 0 && chunkPos.z == 0) {
                switch (MWP_FIELDS.SkyblockWorld) {
                    case 1:
                        if (worldGenLevel.getLevel().dimension() == ServerLevel.OVERWORLD) {
                            //the latest version nether + sand island
                            template = templateManager.get(new ResourceLocation("moreworldpresets", "skyblock/skyblock2_1"));
                        }
                        break;
                    case 2:
                        if (worldGenLevel.getLevel().dimension() == ServerLevel.OVERWORLD) {
                            //3x3 version
                            template = templateManager.get(new ResourceLocation("moreworldpresets", "skyblock/skyblock3x3"));
                            break;
                        }
                    case 3:
                        if (worldGenLevel.getLevel().dimension() == ServerLevel.OVERWORLD) {
                            //original skyblock
                            template = templateManager.get(new ResourceLocation("moreworldpresets", "skyblock/skyblock"));
                            break;
                        }
                }
                template.ifPresent(t -> {
                    StructurePlaceSettings settings = new StructurePlaceSettings()
                            .setMirror(Mirror.NONE)
                            .setRotation(Rotation.NONE)
                            .setIgnoreEntities(false)
                            .setKnownShape(true);
                    BlockPos pos = new BlockPos(6, 64, 6);
                    t.placeInWorld(worldGenLevel, pos, pos, settings, worldGenLevel.getRandom(), 18);
                });
            }
            //generate sand island
            if (chunkPos.x == -5 && chunkPos.z == -1 && MWP_FIELDS.SkyblockWorld != 3 && worldGenLevel.getLevel().dimension() == ServerLevel.OVERWORLD) {
                template = templateManager.get(new ResourceLocation("moreworldpresets", "skyblock/sand_island"));
                template.ifPresent(t -> {
                    StructurePlaceSettings settings = new StructurePlaceSettings()
                            .setMirror(Mirror.NONE)
                            .setRotation(Rotation.NONE)
                            .setIgnoreEntities(false)
                            .setKnownShape(true);
                    BlockPos pos = new BlockPos(-65, 64, 6);
                    t.placeInWorld(worldGenLevel, pos, pos, settings, worldGenLevel.getRandom(), 18);
                });
            }
            ci.cancel();
        } else if (MWP_FIELDS.isOneblockWorld || MWP_FIELDS.isSkygridWorld) {
            ci.cancel();
        }
    }
}
