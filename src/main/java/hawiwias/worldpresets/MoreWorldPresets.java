package hawiwias.worldpresets;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MoreWorldPresets implements ModInitializer {
	public static final String MOD_ID = "moreworldpresets";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static MinecraftServer INSTANCE;
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
				}
			}
		});
	}
}