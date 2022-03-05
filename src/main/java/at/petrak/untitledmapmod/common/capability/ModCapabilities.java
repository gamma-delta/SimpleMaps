package at.petrak.untitledmapmod.common.capability;

import at.petrak.untitledmapmod.SimpleMapMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCapabilities {
    public static final Capability<CapMarkerLocations> MARKER_LOCATIONS = CapabilityManager.get(
        new CapabilityToken<>() {
        });

    @SubscribeEvent
    public static void registerCaps(RegisterCapabilitiesEvent evt) {
        evt.register(CapMarkerLocations.class);
    }

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Level> evt) {
        evt.addCapability(new ResourceLocation(SimpleMapMod.MOD_ID, CapMarkerLocations.CAP_NAME),
            new CapMarkerLocations());
    }
}
