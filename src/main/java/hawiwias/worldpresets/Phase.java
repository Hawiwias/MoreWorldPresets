package hawiwias.worldpresets;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class Phase {
    public String name;
    public int maxBlocks;
    public BossEvent.BossBarColor color;
    public List<Block> blocks;
    public List<EntityType> entities;
    public List<Identifier> lootTables;
    public boolean unlocked;
    public ChatFormatting textColor;

    public Phase(String name, int maxBlocks, BossEvent.BossBarColor color, ChatFormatting textColor, List<Block> blocks, List<EntityType> entities, List<Identifier> lootTables, boolean unlocked) {
        this.name = name;
        this.maxBlocks = maxBlocks;
        this.color = color;
        this.blocks = blocks;
        this.entities = entities;
        this.lootTables = lootTables;
        this.unlocked = unlocked;
        this.textColor = textColor;
    }
}

