package hawiwias.worldpresets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class Phase {
    public String name;
    public int maxBlocks;
    public BossEvent.BossBarColor color;
    public List<Block> blocks;

    public Phase(String name, int maxBlocks, BossEvent.BossBarColor color, List<Block> blocks) {
        this.name = name;
        this.maxBlocks = maxBlocks;
        this.color = color;
        this.blocks = blocks;
    }
}

