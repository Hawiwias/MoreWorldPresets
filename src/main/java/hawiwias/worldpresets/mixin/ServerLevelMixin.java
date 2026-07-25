package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import hawiwias.worldpresets.Phase;
import hawiwias.worldpresets.PhaseManager;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

import static hawiwias.worldpresets.PhaseManager.*;


@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @Shadow
    public void tickPrecipitation(BlockPos pos) {
    }

    @Shadow
    public abstract ServerLevel getLevel();

    @Shadow
    public abstract GameRules getGameRules();

    @Shadow
    public abstract int getSeaLevel();

    /**
     * @author hawiwias
     * @reason winter world snow and ice accumulation
     */
    @Overwrite
    public void tickChunk(final LevelChunk chunk, final int tickSpeed) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        ProfilerFiller profiler = Profiler.get();
        profiler.push("iceandsnow");

        for(int i = 0; i < tickSpeed; ++i) {
            if (this.random.nextInt(48) == 0) {
                this.tickPrecipitation(this.getBlockRandomPos(minX, 0, minZ, 15));
            }
        }

        profiler.popPush("tickBlocks");
        if (tickSpeed > 0) {
            LevelChunkSection[] sections = chunk.getSections();

            for(int sectionIndex = 0; sectionIndex < sections.length; ++sectionIndex) {
                LevelChunkSection section = sections[sectionIndex];
                if (section.isRandomlyTicking()) {
                    int sectionY = chunk.getSectionYFromSectionIndex(sectionIndex);
                    int minYInSection = SectionPos.sectionToBlockCoord(sectionY);

                    for(int i = 0; i < tickSpeed; ++i) {
                        BlockPos pos = this.getBlockRandomPos(minX, minYInSection, minZ, 15);
                        profiler.push("randomTick");
                        BlockState blockState = section.getBlockState(pos.getX() - minX, pos.getY() - minYInSection, pos.getZ() - minZ);
                        if (blockState.isRandomlyTicking()) {
                            blockState.randomTick(this.getLevel(), pos, this.random);
                        }

                        FluidState fluidState = blockState.getFluidState();
                        if (fluidState.isRandomlyTicking()) {
                            fluidState.randomTick(this.getLevel(), pos, this.random);
                        }

                        profiler.pop();
                    }
                }
            }
        }

        profiler.pop();
        profiler.popPush("iceandsnow");
        int amount = 16;
        if (MWP_FIELDS.isWinterWorld) {
            amount = 2;
        }
        ChunkPos chunkpos = chunk.getPos();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();
        if (this.random.nextInt(amount) == 0) {
            BlockPos blockpos1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(i, 0, j, 15));
            BlockPos blockpos2 = blockpos1.below();
            Biome biome = this.getBiome(blockpos1).value();
            if (biome.shouldFreeze(this, blockpos2) || MWP_FIELDS.isWinterWorld) {
                BlockState state = getBlockState(blockpos2);
                if (state.is(Blocks.WATER) && state.getFluidState().isSource()) {
                    this.setBlockAndUpdate(blockpos2, Blocks.ICE.defaultBlockState());
                }
            }

            if (this.isRaining()) {
                int i1 = this.getGameRules().get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
                if (i1 > 0 && biome.shouldSnow(this, blockpos1) || MWP_FIELDS.isWinterWorld) {
                    BlockState blockstate = this.getBlockState(blockpos1);
                    BlockState below = this.getBlockState(blockpos1.below());
                    if (!below.is(Blocks.WATER) && !below.is(Blocks.LAVA) && !below.is(Blocks.ICE) && (below.isFaceSturdy(this, blockpos1.below(), Direction.UP) || below.is(BlockTags.LEAVES))) {
                        if (blockstate.is(Blocks.SNOW)) {
                            int k = blockstate.getValue(SnowLayerBlock.LAYERS);
                            if (k < Math.min(i1, 8) && !MWP_FIELDS.isWinterWorld) {
                                BlockState blockstate1 = blockstate.setValue(SnowLayerBlock.LAYERS, k + 1);
                                Block.pushEntitiesUp(blockstate, blockstate1, this, blockpos1);
                                this.setBlockAndUpdate(blockpos1, blockstate1);
                            }
                            if (k < 7 && MWP_FIELDS.isWinterWorld) {
                                BlockState blockstate1 = blockstate.setValue(SnowLayerBlock.LAYERS, k + 1);
                                Block.pushEntitiesUp(blockstate, blockstate1, this, blockpos1);
                                this.setBlockAndUpdate(blockpos1, blockstate1);
                            } else if (MWP_FIELDS.isWinterWorld) {
                                this.setBlockAndUpdate(blockpos1, Blocks.SNOW_BLOCK.defaultBlockState());
                            }
                        } else if (blockstate.is(Blocks.SNOW_BLOCK) && MWP_FIELDS.isWinterWorld) {
                            BlockPos above = blockpos1.above();
                            if (this.getBlockState(above).isAir()) {
                                this.setBlockAndUpdate(above, Blocks.SNOW.defaultBlockState());
                            }
                        } else {
                            this.setBlockAndUpdate(blockpos1, Blocks.SNOW.defaultBlockState());
                        }
                    }
                }

                Biome.Precipitation biome$precipitation = biome.getPrecipitationAt(blockpos2, getSeaLevel());
                if (biome$precipitation != Biome.Precipitation.NONE) {
                    BlockState blockstate3 = this.getBlockState(blockpos2);
                    blockstate3.getBlock().handlePrecipitation(blockstate3, this, blockpos2, biome$precipitation);
                }
            }
        }

        profiler.popPush("tickBlocks");
        if (tickSpeed > 0) {
            LevelChunkSection[] alevelchunksection = chunk.getSections();

            for (int l = 0; l < alevelchunksection.length; ++l) {
                LevelChunkSection levelchunksection = alevelchunksection[l];
                if (levelchunksection.isRandomlyTicking()) {
                    int j1 = chunk.getSectionYFromSectionIndex(l);
                    int k1 = SectionPos.sectionToBlockCoord(j1);

                    for (int l1 = 0; l1 < tickSpeed; ++l1) {
                        BlockPos blockpos3 = this.getBlockRandomPos(i, k1, j, 15);
                        profiler.push("randomTick");
                        BlockState blockstate2 = levelchunksection.getBlockState(blockpos3.getX() - i, blockpos3.getY() - k1, blockpos3.getZ() - j);
                        if (blockstate2.isRandomlyTicking()) {
                            blockstate2.randomTick((ServerLevel)(Object)this, blockpos3, this.random);
                        }

                        FluidState fluidstate = blockstate2.getFluidState();
                        if (fluidstate.isRandomlyTicking()) {
                            fluidstate.randomTick(this.getLevel(), blockpos3, this.random);
                        }

                        profiler.pop();
                    }
                }
            }
        }

        profiler.pop();
    }


    @Inject(method = "tick", at = @At("TAIL"))
    public void Oneblock(CallbackInfo ci) {
        if (MWP_FIELDS.isOneblockWorld) {

            //VARIABLES + PHASE INFO
            ServerLevel level = MoreWorldPresets.INSTANCE.getLevel(OVERWORLD);
            Phase currentPhase = PhaseManager.getCurrentPhase();
            //SET BLOCK
            BlockPos pos = new BlockPos(0, 65, 0);
            level.setBlock(new BlockPos(0, 64, 0), Blocks.BEDROCK.defaultBlockState(), 3 | 16);
            List<Block> blockList = new java.util.ArrayList<>(List.of());
            List<EntityType> entityList = new java.util.ArrayList<>(List.of());
            for (Phase phase : PhaseManager.phases) {
                if (phase.unlocked) {
                    blockList.addAll(phase.blocks);
                    entityList.addAll(phase.entities);
                }
            }
            if (!blockList.isEmpty() && level.getBlockState(pos).is(Blocks.AIR)) {
                if (random.nextInt(55) == 0) {
                    Entity entity = entityList.get(random.nextInt(entityList.size())).create(level, EntitySpawnReason.NATURAL);
                    //LOWER CHANCES FOR A WARDEN
                    if (entity.getType() == EntityType.WARDEN && random.nextInt(4) == 0)
                    {
                        entity.setPos(0.5, 66, 0.5);
                        level.addFreshEntity(entity);
                    }
                    else {
                        entity.setPos(0.5, 66, 0.5);
                        level.addFreshEntity(entity);
                    }
                }

                level.getServer().execute(() -> {
                    level.setBlock(pos, blockList.get(level.getRandom().nextInt(blockList.size())).defaultBlockState(), 3 | 16);
                });
                if (random.nextInt(55) == 0) {
                    List<Identifier> availableLootTables = new java.util.ArrayList<>(List.of());
                    ;
                    for (Phase phase : PhaseManager.phases) {
                        if (phase.unlocked && phase.lootTables != null) {
                            availableLootTables.addAll(phase.lootTables);
                        }
                    }

                    if (!availableLootTables.isEmpty()) {
                        level.getServer().execute(() -> {
                            level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3 | 16);
                        });
                        ChestBlockEntity chest = (ChestBlockEntity) level.getBlockEntity(pos);
                        ResourceKey<LootTable> resourceKey = ResourceKey.create(
                                Registries.LOOT_TABLE,
                                availableLootTables.get(random.nextInt(availableLootTables.size()))
                        );
                        chest.setLootTable(resourceKey, random.nextLong());
                    }
                }
            }
            //SET PHASE INFO
            if (phaseProgressBar != null) {
                phaseProgressBar.setMax(currentPhase.maxBlocks);
                phaseProgressBar.setColor(currentPhase.color);
                phaseProgressBar.setName(Component.literal( currentPhase.name +" - " + (currentPhase.maxBlocks - currentPhaseProgress) + " blocks remaining").withStyle(currentPhase.textColor));
                phaseProgressBar.setValue(currentPhaseProgress);
            }
        }
    }
}