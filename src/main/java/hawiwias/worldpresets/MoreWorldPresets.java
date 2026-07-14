package hawiwias.worldpresets;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MoreWorldPresets implements ModInitializer {
	public static final String MOD_ID = "moreworldpresets";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer INSTANCE;

	public static BlockPos findSafeSpawnNear(ServerLevel level, BlockPos center) {
		int x = center.getX();
		int z = center.getZ();

		for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
			BlockPos ground = new BlockPos(x, y, z);
			BlockPos above1 = ground.above();
			BlockPos above2 = ground.above(2);

			boolean groundSolid = !level.getBlockState(ground).isAir() && level.getBlockState(ground).getFluidState().isEmpty();
			boolean spaceClear = level.getBlockState(above1).isAir() && level.getBlockState(above2).isAir();

			if (groundSolid && spaceClear) {
				return above1;
			}
		}

		int fallbackY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, x, z);
		return new BlockPos(x, fallbackY, z);
	}
	@Override
	public void onInitialize() {
		PlayerBlockBreakEvents.AFTER.register((world, player, blockPos, state, blockEntity) -> {
			if (!blockPos.equals(new BlockPos(0, 65, 0)) || !MWP_FIELDS.isOneblockWorld) return;

			PhaseManager.onBlockBroken(world, player);

			ServerLevel serverLevel = (ServerLevel) world;
			serverLevel.getServer().execute(() -> {
				List<ItemEntity> drops = serverLevel.getEntitiesOfClass(
						ItemEntity.class,
						new AABB(blockPos).inflate(-0.2)
				);
				for (ItemEntity item : drops) {
					player.addItem(item.getItem());
					item.kill();
				}
			});
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayer player = handler.getPlayer();
			if (player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) == 0) {
				if (MWP_FIELDS.challengeWorld == 1) {
					player.getServer().execute(() -> {
						player.teleportTo(
								player.serverLevel(),
								0.500,
								-63,
								0.500,
								player.getYRot(),
								player.getXRot()
						);
					});
					player.setRespawnPosition(player.level().dimension(),new BlockPos((int) 0.5, -63, (int) 0.5), player.getYRot(), true, false);
				}
				if (MWP_FIELDS.isSkyblockWorld) {
					player.getServer().execute(() -> {
						player.teleportTo(
								player.serverLevel(),
								9.500,
								67,
								7.500,
								player.getYRot(),
								player.getXRot()
						);
					});
					player.setRespawnPosition(player.level().dimension(),new BlockPos((int) 9.5, 67, (int) 7.5), player.getYRot(), true, false);
				}
				if (MWP_FIELDS.isSkygridWorld) {
					player.getServer().execute(() -> {
						player.teleportTo(
								player.serverLevel(),
								0.500,
								81,
								0.500,
								player.getYRot(),
								player.getXRot()
						);
					});
				}
				if (MWP_FIELDS.isOneblockWorld) {
					player.getServer().execute(() -> {
						player.teleportTo(
								player.serverLevel(),
								0.500,
								66,
								0.500,
								player.getYRot(),
								player.getXRot()
						);
					});
					player.setRespawnPosition(player.level().dimension(),new BlockPos((int) 0.5, 66, (int) 0.5), player.getYRot(), true, false);
				}
				if (MWP_FIELDS.challengeWorld == 2) {
					player.getServer().execute(() -> {
						ServerLevel serverLevel = player.serverLevel();
						Registry<Structure> structureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
						HolderSet<Structure> villageStructures = structureRegistry.getOrCreateTag(StructureTags.VILLAGE);

						Pair<BlockPos, Holder<Structure>> result = serverLevel.getChunkSource().getGenerator()
								.findNearestMapStructure(serverLevel, villageStructures, BlockPos.ZERO, 1000, false);
						if (result != null) {
							BlockPos villagePos = result.getFirst();
							BlockPos safePos = findSafeSpawnNear(serverLevel, villagePos);
							player.teleportTo(
									serverLevel,
									safePos.getX() + 0.5,
									safePos.getY(),
									safePos.getZ() + 0.5,
									player.getYRot(),
									player.getXRot()
							);
							player.setRespawnPosition(serverLevel.dimension(), safePos, player.getYRot(), true, false);
						}
					});
				}
			}
		});
	}
}