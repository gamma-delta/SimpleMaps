package at.petrak.untitledmapmod;

import at.petrak.untitledmapmod.client.ModKeybinds;
import at.petrak.untitledmapmod.client.ModOverlays;
import at.petrak.untitledmapmod.common.items.ModItems;
import at.petrak.untitledmapmod.datagen.Advancements;
import at.petrak.untitledmapmod.datagen.ModDataGenerators;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(UntitledMapMod.MOD_ID)
public class UntitledMapMod {
    public static final String MOD_ID = "untitledmapmod";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public UntitledMapMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        var evBus = MinecraftForge.EVENT_BUS;

        modBus.register(UntitledMapMod.class);
        modBus.register(ModDataGenerators.class);
        modBus.register(Advancements.class); // register triggers
        ModItems.ITEMS.register(modBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            evBus.register(ModOverlays.class);
        });
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            ModOverlays.initTextures();
            ModKeybinds.init();
        });
    }
}
