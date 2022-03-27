package at.petrak.untitledmapmod.datagen;

import at.petrak.paucal.api.datagen.PaucalBlockStateAndModelProvider;
import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.blocks.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Also does block models for some reason
 */
public class BlockModels extends PaucalBlockStateAndModelProvider {
    public BlockModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SimpleMapMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        var bottomLoc = new ResourceLocation(SimpleMapMod.MOD_ID, "block/marker_bottom");
        for (var markerRobj : ModBlocks.MARKERS) {
            var marker = markerRobj.get();
            var name = "marker_" + marker.color.getName();
            var sideLoc = new ResourceLocation(SimpleMapMod.MOD_ID,
                "block/marker_" + marker.color.getName() + "_side");
            var topLoc = new ResourceLocation(SimpleMapMod.MOD_ID, "block/marker_" + marker.color.getName() + "_top");
            var model = models().cubeBottomTop(name, sideLoc, bottomLoc, topLoc);
            simpleBlock(marker, model);
            simpleBlockItem(marker, model);
        }
    }
}
