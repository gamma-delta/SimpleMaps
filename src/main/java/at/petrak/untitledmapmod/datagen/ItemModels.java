package at.petrak.untitledmapmod.datagen;

import at.petrak.paucal.api.datagen.PaucalItemModelProvider;
import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends PaucalItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SimpleMapMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.MINIMAP.get());
        simpleItem(ModItems.WORLD_MAP.get());
    }
}
