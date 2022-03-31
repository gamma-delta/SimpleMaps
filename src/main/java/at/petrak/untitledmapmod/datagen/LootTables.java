package at.petrak.untitledmapmod.datagen;

import at.petrak.paucal.api.datagen.PaucalLootTableProvider;
import at.petrak.untitledmapmod.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Map;

public class LootTables extends PaucalLootTableProvider {
    public LootTables(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void makeLootTables(Map<Block, LootTable.Builder> map) {
        dropSelf(map, ModBlocks.MARKERS);
    }
}
