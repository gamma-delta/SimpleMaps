package at.petrak.untitledmapmod.common.network;

import at.petrak.untitledmapmod.UntitledMapMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(UntitledMapMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getNetwork() {
        return NETWORK;
    }

    public static void register() {
        int messageIdx = 0;

        NETWORK.registerMessage(messageIdx++, MsgMarkerLocsSyn.class, MsgMarkerLocsSyn::serialize,
            MsgMarkerLocsSyn::deserialize, MsgMarkerLocsSyn::handle);
        NETWORK.registerMessage(messageIdx++, MsgMarkerLocsAck.class, MsgMarkerLocsAck::serialize,
            MsgMarkerLocsAck::deserialize, MsgMarkerLocsAck::handle);
    }
}
