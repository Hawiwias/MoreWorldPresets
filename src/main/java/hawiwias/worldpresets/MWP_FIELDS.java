package hawiwias.worldpresets;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStep;

import java.util.List;

public class MWP_FIELDS extends WorldGenRegion
{
    public static boolean skyblockNetherIslandGenerated = false;
    public static String presetKey = "minecraft:normal";
    public static boolean isWinterWorld = false;
    public static boolean isSkyblockWorld = false;
    public static boolean isOneblockWorld = false;
    public static boolean isSkygridWorld = false;
    public static int SkyblockWorld = 1;
    public static int challengeWorld = 0;
    public RegistryAccess registryAccess;
    public MWP_FIELDS(ServerLevel serverLevel, StaticCache2D<GenerationChunkHolder> generationChunkHolderStaticCache2D, ChunkStep chunkStep, ChunkAccess chunkAccess) {
        super(serverLevel, generationChunkHolderStaticCache2D, chunkStep, chunkAccess);
        registryAccess = serverLevel.registryAccess();
    }
}
