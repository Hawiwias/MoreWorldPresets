package hawiwias.worldpresets;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

import java.util.List;

public class MWP_FIELDS extends WorldGenRegion
{
    public static boolean isWinterWorld = false;
    public static boolean isSkyblockWorld = false;
    public static boolean isOneblockWorld = false;
    public static boolean isSkygridWorld = false;
    public static int SkyblockWorld = 1;
    public static int challengeWorld = 0;
    public RegistryAccess registryAccess;
    public MWP_FIELDS(ServerLevel serverLevel, List<ChunkAccess> list, ChunkStatus chunkStatus, int i) {
        super(serverLevel, list, chunkStatus, i);
        registryAccess = serverLevel.registryAccess();
    }
}
