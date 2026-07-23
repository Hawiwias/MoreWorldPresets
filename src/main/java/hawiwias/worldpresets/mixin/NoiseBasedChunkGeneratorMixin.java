package hawiwias.worldpresets.mixin;


import com.llamalad7.mixinextras.sugar.Local;
import hawiwias.worldpresets.MWP_FIELDS;
import hawiwias.worldpresets.MoreWorldPresets;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
            Registry<NoiseGeneratorSettings> registry = registryAccess.registryOrThrow(Registries.NOISE_SETTINGS);
            settings = registry.getHolderOrThrow(ResourceKey.create(Registries.NOISE_SETTINGS, ResourceLocation.fromNamespaceAndPath("moreworldpresets", "challenge_world")));
            cir.setReturnValue(settings);
        }
    }
    @Inject(method = "buildSurface(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/world/level/levelgen/WorldGenerationContext;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/biome/BiomeManager;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/blending/Blender;)V", at = @At("TAIL"))
    private void modifyWorld(ChunkAccess chunkAccess, WorldGenerationContext worldGenerationContext, RandomState randomState, StructureManager structureManager, BiomeManager biomeManager, Registry<Biome> registry, Blender blender, CallbackInfo ci) {
        //CLEAR BEDROCK LAYER FOR CHALLENGE WORLD 1
        if (MWP_FIELDS.challengeWorld == 1) {
            for (BlockPos pos : BlockPos.betweenClosed(
                    chunkAccess.getPos().getMinBlockX(), chunkAccess.getMinBuildHeight(), chunkAccess.getPos().getMinBlockZ(),
                    chunkAccess.getPos().getMaxBlockX(), chunkAccess.getMinBuildHeight() + 5, chunkAccess.getPos().getMaxBlockZ()
            )) {
                chunkAccess.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
            }
        }
        //CLEAR WORLD (VOID)
        if (MWP_FIELDS.isSkyblockWorld || MWP_FIELDS.isOneblockWorld || MWP_FIELDS.isSkygridWorld || MWP_FIELDS.challengeWorld == 2) {
            for (BlockPos pos : BlockPos.betweenClosed(
                    chunkAccess.getPos().getMinBlockX(), chunkAccess.getMinBuildHeight(), chunkAccess.getPos().getMinBlockZ(),
                    chunkAccess.getPos().getMaxBlockX(), chunkAccess.getMaxBuildHeight(), chunkAccess.getPos().getMaxBlockZ()
            )) {
                chunkAccess.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
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
                            .flatMap(tag -> BuiltInRegistries.BLOCK.getTag(tag)
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
                    chunkAccess.setBlockState(pos, randomBlock , false);

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
                        List<ResourceLocation> chestResourceLocation = List.of(
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ancient_city"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ancient_city_ice_box"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_bridge"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_hoglin_stable"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_other"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/bastion_treasure"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/buried_treasure"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/desert_pyramid"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/igloo_chest"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/jungle_temple_dispenser"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/nether_bridge"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ruined_portal"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_map"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_supply"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_treasure"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/spawn_bonus_chest"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_corridor"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_crossing"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/underwater_ruin_big"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/underwater_ruin_small"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/woodland_mansion"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/corridor"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/entrance"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/intersection"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/reward"),
                                ResourceLocation.fromNamespaceAndPath("minecraft", "chests/trial_chambers/supply")
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
