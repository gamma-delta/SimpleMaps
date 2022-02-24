package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.UntitledMapMod;
import at.petrak.untitledmapmod.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Also does block models for some reason
 */
public class BlockModels extends BlockStateProvider {
    public BlockModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, UntitledMapMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var markerRobj : ModBlocks.MARKERS) {
            var marker = markerRobj.get();
            var name = "marker_" + marker.color.getName();
            var model = new ResourceLocation("block/" + marker.color.getName() + "_wool");
            simpleBlock(marker, models().cubeAll(name, model));
            simpleBlockItem(marker, models().cubeAll(name, model));
        }
    }
}
