package at.petrak.untitledmapmod.common.network;

import at.petrak.untitledmapmod.client.GuiWorldMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record MsgMarkerLocsAck(List<BlockPos> locations) {
    public static MsgMarkerLocsAck deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var list = buf.readList(FriendlyByteBuf::readBlockPos);
        return new MsgMarkerLocsAck(list);
    }

    public void serialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        buf.writeCollection(this.locations, FriendlyByteBuf::writeBlockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var mc = Minecraft.getInstance();
                var screen = mc.screen;
                if (screen instanceof GuiWorldMap worldMap) {
                    worldMap.loadMarkerLocations(this.locations);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}