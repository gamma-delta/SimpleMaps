package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {
    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SimpleMapMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.MINIMAP.get());
        simpleItem(ModItems.WORLD_MAP.get());
    }

    public void simpleItem(Item item) {
        simpleItem(item.getRegistryName());
    }

    public void simpleItem(ResourceLocation path) {
        singleTexture(path.getPath(), new ResourceLocation("item/generated"),
            "layer0", new ResourceLocation(SimpleMapMod.MOD_ID, "item/" + path.getPath()));
    }
}
