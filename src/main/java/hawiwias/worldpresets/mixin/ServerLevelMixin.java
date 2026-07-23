package hawiwias.worldpresets.mixin;

import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import hawiwias.worldpresets.Phase;
import hawiwias.worldpresets.PhaseManager;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import static hawiwias.worldpresets.PhaseManager.*;


@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }
    @Shadow
    protected abstract BlockPos findLightningTargetAround(BlockPos pos);

    @Shadow
    public abstract ServerLevel getLevel();

    /**
     * @author hawiwias
     * @reason winter world snow and ice accumulation
     */
    @Overwrite
    public void tickChunk(LevelChunk chunk, int randomTickSpeed) {
        ChunkPos chunkpos = chunk.getPos();
        boolean flag = this.isRaining();
        int i = chunkpos.getMinBlockX();
        int j = chunkpos.getMinBlockZ();
        ProfilerFiller profilerfiller = this.getProfiler();
        profilerfiller.push("thunder");
        if (flag && this.isThundering() && this.random.nextInt(100000) == 0) {
            BlockPos blockpos = this.findLightningTargetAround(this.getBlockRandomPos(i, 0, j, 15));
            if (this.isRainingAt(blockpos)) {
                DifficultyInstance difficultyinstance = this.getCurrentDifficultyAt(blockpos);
                boolean flag1 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double) difficultyinstance.getEffectiveDifficulty() * 0.01D && !this.getBlockState(blockpos.below()).is(Blocks.LIGHTNING_ROD);
                if (flag1) {
                    SkeletonHorse skeletonhorse = EntityType.SKELETON_HORSE.create(this);
                    if (skeletonhorse != null) {
                        skeletonhorse.setTrap(true);
                        skeletonhorse.setAge(0);
                        skeletonhorse.setPos((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
                        this.addFreshEntity(skeletonhorse);
                    }
                }

                LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(this);
                if (lightningbolt != null) {
                    lightningbolt.moveTo(Vec3.atBottomCenterOf(blockpos));
                    lightningbolt.setVisualOnly(flag1);
                    this.addFreshEntity(lightningbolt);
                }
            }
        }

        profilerfiller.popPush("iceandsnow");
        int amount = 16;
        if (MWP_FIELDS.isWinterWorld) {
            amount = 2;
        }
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

            if (flag) {
                int i1 = this.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
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

                Biome.Precipitation biome$precipitation = biome.getPrecipitationAt(blockpos2);
                if (biome$precipitation != Biome.Precipitation.NONE) {
                    BlockState blockstate3 = this.getBlockState(blockpos2);
                    blockstate3.getBlock().handlePrecipitation(blockstate3, this, blockpos2, biome$precipitation);
                }
            }
        }

        profilerfiller.popPush("tickBlocks");
        if (randomTickSpeed > 0) {
            LevelChunkSection[] alevelchunksection = chunk.getSections();

            for (int l = 0; l < alevelchunksection.length; ++l) {
                LevelChunkSection levelchunksection = alevelchunksection[l];
                if (levelchunksection.isRandomlyTicking()) {
                    int j1 = chunk.getSectionYFromSectionIndex(l);
                    int k1 = SectionPos.sectionToBlockCoord(j1);

                    for (int l1 = 0; l1 < randomTickSpeed; ++l1) {
                        BlockPos blockpos3 = this.getBlockRandomPos(i, k1, j, 15);
                        profilerfiller.push("randomTick");
                        BlockState blockstate2 = levelchunksection.getBlockState(blockpos3.getX() - i, blockpos3.getY() - k1, blockpos3.getZ() - j);
                        if (blockstate2.isRandomlyTicking()) {
                            blockstate2.randomTick((ServerLevel)(Object)this, blockpos3, this.random);
                        }

                        FluidState fluidstate = blockstate2.getFluidState();
                        if (fluidstate.isRandomlyTicking()) {
                            fluidstate.randomTick(this, blockpos3, this.random);
                        }

                        profilerfiller.pop();
                    }
                }
            }
        }

        profilerfiller.pop();
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
                    Entity entity = entityList.get(random.nextInt(entityList.size())).create(level);
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
                    level.setBlock(pos, blockList.get(level.random.nextInt(blockList.size())).defaultBlockState(), 3 | 16);
                });
                if (random.nextInt(55) == 0) {
                    List<ResourceLocation> availableLootTables = new java.util.ArrayList<>(List.of());
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