package hawiwias.worldpresets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@Mod(MoreWorldPresets.MOD_ID)
public class MoreWorldPresets {

	public static final String MOD_ID = "moreworldpresets";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer INSTANCE;

	public MoreWorldPresets(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onBlockDrops(BlockDropsEvent event) {
		BlockPos blockPos = event.getPos();
		if (!blockPos.equals(new BlockPos(0, 65, 0)) || !MWP_FIELDS.isOneblockWorld) return;
		if (!(event.getBreaker() instanceof ServerPlayer player)) return;

		ServerLevel serverLevel = (ServerLevel) event.getLevel();
		PhaseManager.onBlockBroken(serverLevel, player);

		event.getDrops().forEach(itemEntity -> {
			player.addItem(itemEntity.getItem());
		});
		event.getDrops().clear();
	}

	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		if (!player.level().dimension().equals(ServerLevel.OVERWORLD)) return;
		if (player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) != 0) return;

		if (MWP_FIELDS.challengeWorld == 1) {
			player.level().getServer().execute(() -> {
				player.teleportTo(
						player.level(),
						0.500,
						-63,
						0.500,
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
			});
			GlobalPos globalPos = GlobalPos.of(player.level().dimension(), new BlockPos((int) 0.5, -63, (int) 0.5));
			ServerPlayer.RespawnConfig respawnConfig = new ServerPlayer.RespawnConfig(
					new LevelData.RespawnData(globalPos, player.getYRot(), 0.0f),
					true
			);
			player.setRespawnPosition(respawnConfig, false);
		}
		if (MWP_FIELDS.isSkyblockWorld) {
			double x;
			double y;
			double z;
			if (MWP_FIELDS.SkyblockWorld == 3) {
				x = 7.5;
				y = 67;
				z = 10.5;
			} else {
				x = 9.5;
				y = 67;
				z = 7.5;
			}
			player.level().getServer().execute(() -> {
				player.teleportTo(
						player.level(),
						x,
						y,
						z,
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
			});
			GlobalPos globalPos = GlobalPos.of(player.level().dimension(), new BlockPos((int) x, (int) y, (int) z));
			ServerPlayer.RespawnConfig respawnConfig = new ServerPlayer.RespawnConfig(
					new LevelData.RespawnData(globalPos, player.getYRot(), 0.0f),
					true
			);
			player.setRespawnPosition(respawnConfig, false);
		}
		if (MWP_FIELDS.isSkygridWorld) {
			player.level().getServer().execute(() -> {
				player.teleportTo(
						player.level(),
						0.500,
						81,
						0.500,
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
			});
		}
		if (MWP_FIELDS.isOneblockWorld) {
			player.level().getServer().execute(() -> {
				player.teleportTo(
						player.level(),
						0.500,
						66,
						0.500,
						Set.of(),
						player.getYRot(),
						player.getXRot(),
						false
				);
			});
			GlobalPos globalPos = GlobalPos.of(player.level().dimension(), new BlockPos((int) 0.5, 66, (int) 0.5));
			ServerPlayer.RespawnConfig respawnConfig = new ServerPlayer.RespawnConfig(
					new LevelData.RespawnData(globalPos, player.getYRot(), 0.0f),
					true
			);
			player.setRespawnPosition(respawnConfig, false);
		}
		if (MWP_FIELDS.challengeWorld == 2) {
			player.level().getServer().execute(() -> {
				ServerLevel serverLevel = player.level();
				BlockPos pathPos = findNearestPathBlock(serverLevel, BlockPos.ZERO, 500);

				if (pathPos != null) {
					BlockPos spawnPos = pathPos.above();
					player.teleportTo(
							serverLevel,
							spawnPos.getX() + 0.5,
							spawnPos.getY(),
							spawnPos.getZ() + 0.5,
							Set.of(),
							player.getYRot(),
							player.getXRot(),
							false
					);
					GlobalPos globalPos = GlobalPos.of(player.level().dimension(), spawnPos);
					ServerPlayer.RespawnConfig respawnConfig = new ServerPlayer.RespawnConfig(
							new LevelData.RespawnData(globalPos, player.getYRot(), 0.0f),
							true
					);
					player.setRespawnPosition(respawnConfig, false);
				}
			});
		}
		if (MWP_FIELDS.challengeWorld == 3) {
			player.level().getServer().execute(() -> {
				AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
				if (maxHealthAttr != null) {
					maxHealthAttr.setBaseValue(10.0);
				}
				player.setHealth((float) player.getMaxHealth());
				player.getFoodData().setFoodLevel(10);
			});
		}
	}

	public static BlockPos findNearestPathBlock(ServerLevel level, BlockPos center, int maxRadius) {
		int centerChunkX = center.getX() >> 4;
		int centerChunkZ = center.getZ() >> 4;
		int maxChunkRadius = (maxRadius >> 4) + 1;

		int minY = 63;
		int maxY = 120;

		for (int chunkRadius = 0; chunkRadius <= maxChunkRadius; chunkRadius++) {
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
									return pos;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}
