package hawiwias.worldpresets.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;


@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {
    @Shadow
    private final Supplier<Aquifer.FluidPicker> globalFluidPicker;
    @Unique
    Random random = new Random();
    RandomSource mcrandom = RandomSource.create();
    @Unique
    private Holder<NoiseGeneratorSettings> settings;

    public NoiseBasedChunkGeneratorMixin(Supplier<Aquifer.FluidPicker> globalFluidPicker) {
        this.globalFluidPicker = globalFluidPicker;
    }
    @ModifyVariable(method = "doFill", at = @At("STORE"), ordinal = 6)
    private int modifyI1(int originalI1, @Local(ordinal = 4) int k) {
        if (MWP_FIELDS.challengeWorld == 4) {
            return 11 / k;
        }
        return originalI1;
    }
    @ModifyVariable(method = "doFill", at = @At("STORE"), ordinal = 7)
    private int modifyJ1(int originalJ1, @Local(ordinal = 4) int k) {
        if (MWP_FIELDS.challengeWorld == 4) {
            return 11 / k;
        }
        return originalJ1;
    }

    @Inject(method = "generatorSettings", at = @At("HEAD"), cancellable = true)
    private void onNoiseBasedChunkGen(CallbackInfoReturnable<Holder<NoiseGeneratorSettings>> cir) {
        //USE CHALLENGE WORLD 1 NOISE SETTINGS
        if (MWP_FIELDS.challengeWorld == 1) {
            RegistryAccess registryAccess = MoreWorldPresets.INSTANCE.registryAccess();
            Registry<NoiseGeneratorSettings> registry = registryAccess.lookupOrThrow(Registries.NOISE_SETTINGS);
            settings = registry.getOrThrow(ResourceKey.create(Registries.NOISE_SETTINGS, Identifier.fromNamespaceAndPath("moreworldpresets", "challenge_world")));
            cir.setReturnValue(settings);
        }
    }
    @Inject(method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at = @At("TAIL"))
    private void modifyWorld(ChunkAccess chunkAccess, WorldGenerationContext worldGenerationContext, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, Registry<Biome> registry, Blender blender, CallbackInfo ci) {
        //CLEAR BEDROCK LAYER FOR CHALLENGE WORLD 1
        if (MWP_FIELDS.challengeWorld == 1) {
            for (BlockPos pos : BlockPos.betweenClosed(
                    chunkAccess.getPos().getMinBlockX(), chunkAccess.getMinY(), chunkAccess.getPos().getMinBlockZ(),
                    chunkAccess.getPos().getMaxBlockX(), chunkAccess.getMinY() + 5, chunkAccess.getPos().getMaxBlockZ()
            )) {
                chunkAccess.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        //CLEAR WORLD (VOID)
        if (MWP_FIELDS.isSkyblockWorld || MWP_FIELDS.isOneblockWorld || MWP_FIELDS.isSkygridWorld || MWP_FIELDS.challengeWorld == 2) {
            for (BlockPos pos : BlockPos.betweenClosed(
                    chunkAccess.getPos().getMinBlockX(), chunkAccess.getMinY(), chunkAccess.getPos().getMinBlockZ(),
                    chunkAccess.getPos().getMaxBlockX(), chunkAccess.getMaxY(), chunkAccess.getPos().getMaxBlockZ()
            )) {
                chunkAccess.setBlockState(pos, Blocks.AIR.defaultBlockState(), 3);
                if (pos.getX() % 4 == 0 && pos.getZ() % 4 == 0 && pos.getY() % 4 == 0 && MWP_FIELDS.isSkygridWorld) {
                    //DEFAULT LIST TO GENERATE
                    List<TagKey<Block>> tags = List.of(
                            BlockTags.OVERWORLD_NATURAL_LOGS,
                            BlockTags.AZALEA_ROOT_REPLACEABLE
                    );
                    //VEGETATION LOGIC 1 IN A 6
                    if (random.nextInt(6) == 1)
                    {
                       tags = List.of(
                                BlockTags.SWORD_EFFICIENT
                        );
                    }
                    //MINEABLE WITH HOE 1 IN A 50
                    if (random.nextInt(50) == 1)
                    {
                        tags = List.of(
                                BlockTags.MINEABLE_WITH_HOE
                        );
                    }
                    //ORE GENERATION 1 IN A 50
                    if (random.nextInt(50) == 1) {
                        tags =  List.of(BlockTags.COAL_ORES,
                                BlockTags.IRON_ORES,
                                BlockTags.EMERALD_ORES,
                                BlockTags.GOLD_ORES,
                                BlockTags.LAPIS_ORES,
                                BlockTags.COPPER_ORES,
                                BlockTags.REDSTONE_ORES,
                                BlockTags.DIAMOND_ORES);
                    }
                    
                    //DETERMINE A RANDOM BLOCK
                    List<Block> blocks = Stream.concat(tags.stream()
                            .flatMap(tag -> BuiltInRegistries.BLOCK.get(tag)
                                    .map(named -> named.stream().map(Holder::value))
                                    .orElse(Stream.empty())),
                                    Stream.of(Blocks.DRIPSTONE_BLOCK, Blocks.POINTED_DRIPSTONE))
                            .filter(b -> !b.defaultBlockState().is(BlockTags.SMALL_FLOWERS))
                            .filter(b -> !b.defaultBlockState().is(BlockTags.TERRACOTTA))
                            .filter(b -> b != Blocks.ATTACHED_MELON_STEM)
                            .filter(b -> b != Blocks.ATTACHED_PUMPKIN_STEM)
                            .filter(b -> b != Blocks.MELON_STEM)
                            .filter(b -> b != Blocks.PUMPKIN_STEM)
                            .filter(b -> b != Blocks.NETHER_WART)
                            .filter(b -> b != Blocks.WARPED_FUNGUS)
                            .filter(b -> b != Blocks.WARPED_ROOTS)
                            .filter(b -> b != Blocks.NETHER_SPROUTS)
                            .filter(b -> b != Blocks.CRIMSON_FUNGUS)
                            .filter(b -> b != Blocks.CRIMSON_ROOTS)
                            .filter(b -> b != Blocks.CHORUS_FLOWER)
                            .filter(b -> b != Blocks.CHORUS_PLANT)
                            .filter(b -> b != Blocks.WEEPING_VINES)
                            .filter(b -> b != Blocks.WEEPING_VINES_PLANT)
                            .filter(b -> b != Blocks.TWISTING_VINES)
                            .filter(b -> b != Blocks.TWISTING_VINES_PLANT)
                            .filter(b -> b != Blocks.NETHER_GOLD_ORE)
                            .filter(b -> b != Blocks.TARGET)
                            .filter(b -> b != Blocks.DRIED_KELP_BLOCK)
                            .filter(b -> b != Blocks.NETHER_WART_BLOCK)
                            .filter(b -> b != Blocks.SHROOMLIGHT)
                            .filter(b -> b != Blocks.WARPED_WART_BLOCK)
                            .filter(b -> b.defaultBlockState().getFluidState().isEmpty())
                            .toList();
                    BlockState randomBlock = blocks.get(random.nextInt(blocks.size())).defaultBlockState();

                    //1 IN A 2000 CHANCE TO SET A SPAWNER
                    if (random.nextInt(2500) == 1) {
                        randomBlock = Blocks.SPAWNER.defaultBlockState();
                    }

                    //1 IN A 500 CHANCE TO SET WATER
                    if (random.nextInt(500) == 1) {
                        randomBlock = Blocks.WATER.defaultBlockState();
                    }
                    //1 IN A 500 CHANCE TO SET LAVA
                    if (random.nextInt(500) == 1) {
                        randomBlock = Blocks.LAVA.defaultBlockState();
                    }
                    //1 IN A 300 CHANCE TO SET OBSIDIAN
                    if (random.nextInt(300) == 1) {
                        randomBlock = Blocks.OBSIDIAN.defaultBlockState();
                    }


                    //1 IN A 8000 CHANCE TO SET A CHEST
                    if (random.nextInt(8000) == 1) {
                        randomBlock = Blocks.CHEST.defaultBlockState();
                    }

                    //SET RANDOM BLOCK
                    chunkAccess.setBlockState(pos, randomBlock , 2);

                    //SET RANDOM ENTITY IN SPAWNER
                    if (randomBlock.is(Blocks.SPAWNER)) {
                        SpawnerBlockEntity spawnerblockentity = new SpawnerBlockEntity(pos, randomBlock);
                        List<EntityType<?>> entities = List.of(EntityType.SPIDER, EntityType.SKELETON, EntityType.ZOMBIE,EntityType.CAVE_SPIDER, EntityType.SILVERFISH);
                        spawnerblockentity.setEntityId(entities.get(random.nextInt(entities.size())), mcrandom);
                        chunkAccess.setBlockEntity(spawnerblockentity);
                    }
                    //SET RANDOM LOOT TABLE IN CHEST
                    if (randomBlock.is(Blocks.CHEST)) {
                        ChestBlockEntity chestblockentity = new ChestBlockEntity(pos, randomBlock);
                        List<Identifier> chestResourceLocation = List.of(
                                Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/ancient_city"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/ancient_city_ice_box"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/bastion_bridge"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/bastion_hoglin_stable"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/bastion_other"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/bastion_treasure"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/buried_treasure"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/end_city_treasure"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/igloo_chest"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/jungle_temple"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/jungle_temple_dispenser"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/nether_bridge"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/ruined_portal"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/shipwreck_map"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/shipwreck_supply"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/shipwreck_treasure"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/spawn_bonus_chest"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/stronghold_corridor"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/stronghold_crossing"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/stronghold_library"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/underwater_ruin_big"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/underwater_ruin_small"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/woodland_mansion"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/trial_chambers/corridor"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/trial_chambers/entrance"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/trial_chambers/intersection"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/trial_chambers/reward"),
                                Identifier.fromNamespaceAndPath("minecraft", "chests/trial_chambers/supply")
                        );
                        ResourceKey<LootTable> resourceKey = ResourceKey.create(Registries.LOOT_TABLE, chestResourceLocation.get(random.nextInt(chestResourceLocation.size())));
                        chestblockentity.setLootTable(resourceKey, random.nextLong());
                        chunkAccess.setBlockEntity(chestblockentity);
                    }
                }
            }
        }
    }
}
