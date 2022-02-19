package at.petrak.untitledmapmod.datagen;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModDataGenerators {
    @SubscribeEvent
    public static void generateData(GatherDataEvent ev) {
        var gen = ev.getGenerator();
        var efh = ev.getExistingFileHelper();
        if (ev.includeClient()) {
            gen.addProvider(new ItemModels(gen, efh));
        }
        if (ev.includeServer()) {
            gen.addProvider(new Advancements(gen, efh));
        }
    }
}
