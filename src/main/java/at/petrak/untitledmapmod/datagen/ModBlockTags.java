package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTags extends BlockTagsProvider {
    public ModBlockTags(DataGenerator pGenerator,
        @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, SimpleMapMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        for (var blockRobj : ModBlocks.MARKERS) {
            this.m_206424_(BlockTags.MINEABLE_WITH_PICKAXE).add(blockRobj.get());
        }
    }
}
