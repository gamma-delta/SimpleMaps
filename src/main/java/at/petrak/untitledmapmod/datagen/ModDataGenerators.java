package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.datagen.lootmods.ModLootMods;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModDataGenerators {
    @SubscribeEvent
    public static void generateData(GatherDataEvent ev) {
        var gen = ev.getGenerator();
        var efh = ev.getExistingFileHelper();
        if (ev.includeClient()) {
            gen.addProvider(new ItemModels(gen, efh));
            gen.addProvider(new BlockModels(gen, efh));
            gen.addProvider(new ModLootMods(gen));
        }
        if (ev.includeServer()) {
            gen.addProvider(new Advancements(gen, efh));
            gen.addProvider(new Recipes(gen));
            gen.addProvider(new LootTables(gen));
            gen.addProvider(new ModBlockTags(gen, efh));
        }
    }
}
