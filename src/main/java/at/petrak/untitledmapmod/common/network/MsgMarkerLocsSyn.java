package at.petrak.untitledmapmod.common.network;

import at.petrak.untitledmapmod.common.capability.ModCapabilities;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record MsgMarkerLocsSyn() {
    public static MsgMarkerLocsSyn deserialize(ByteBuf buffer) {
        return new MsgMarkerLocsSyn();
    }

    public void serialize(ByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender != null) {
                // Todo: some kind of not sending everyone kb of data
                var maybeCap = sender.level.getCapability(ModCapabilities.MARKER_LOCATIONS).resolve();
                maybeCap.ifPresent(locations -> {
                    ModMessages.getNetwork()
                        .send(PacketDistributor.PLAYER.with(() -> sender),
                            new MsgMarkerLocsAck(locations.getLocations()));
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}