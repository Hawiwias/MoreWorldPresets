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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MoreWorldPresets implements ModInitializer {
	public static final String MOD_ID = "moreworldpresets";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer INSTANCE;

	public static BlockPos findNearestPathBlock(ServerLevel level, BlockPos center, int maxRadius) {
		int centerChunkX = center.getX() >> 4;
		int centerChunkZ = center.getZ() >> 4;
		int maxChunkRadius = (maxRadius >> 4) + 1;

		int minY = 50;
		int maxY = level.getMaxBuildHeight() - 1;

		for (int chunkRadius = 0; chunkRadius <= maxChunkRadius; chunkRadius++) {
			System.out.println("[MWP] scanning chunk radius " + chunkRadius);
			for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
				for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != chunkRadius) continue;

					int chunkX = centerChunkX + dx;
					int chunkZ = centerChunkZ + dz;
					level.getChunk(chunkX, chunkZ, ChunkStatus.FEATURES, true);

					int baseX = chunkX << 4;
					int baseZ = chunkZ << 4;
					for (int x = 0; x < 16; x++) {
						for (int z = 0; z < 16; z++) {
							for (int y = maxY; y > minY; y--) {
								BlockPos pos = new BlockPos(baseX + x, y, baseZ + z);
								if (level.getBlockState(pos).is(BlockTags.PLANKS)) {
									System.out.println("[MWP] found plank block at " + pos);
									return pos;
								}
							}
						}
					}
				}
			}
		}
		System.out.println("[MWP] no plank block found within radius");
		return null;
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
						BlockPos pathPos = findNearestPathBlock(serverLevel, BlockPos.ZERO, 300);

						if (pathPos != null) {
							BlockPos spawnPos = pathPos.above();
							player.teleportTo(
									serverLevel,
									spawnPos.getX() + 0.5,
									spawnPos.getY(),
									spawnPos.getZ() + 0.5,
									player.getYRot(),
									player.getXRot()
							);
							player.setRespawnPosition(serverLevel.dimension(), spawnPos, player.getYRot(), true, false);
						}
					});
				}
				if (MWP_FIELDS.challengeWorld == 3) {
					player.getServer().execute(() -> {
						AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
						if (maxHealthAttr != null) {
							maxHealthAttr.setBaseValue(10.0);
						}
						player.setHealth((float) player.getMaxHealth());
						player.getFoodData().setFoodLevel(10);
					});
				}
			}
		});
	}
}