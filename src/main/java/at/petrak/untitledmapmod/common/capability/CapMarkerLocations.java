package at.petrak.untitledmapmod.common.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Attaches to a level and holds all the lodestone locations in it.
 */
public class CapMarkerLocations implements ICapabilitySerializable<ListTag> {
    public static final String CAP_NAME = "marker_locations";

    List<BlockPos> locations;

    public CapMarkerLocations() {
        this.locations = new ArrayList<>();
    }

    public List<BlockPos> getLocations() {
        return this.locations;
    }

    public void addLocation(BlockPos pos) {
        this.locations.add(pos);
    }

    /**
     * @return whether the position actually existed
     */
    public boolean removeLocation(BlockPos pos) {
        return this.locations.remove(pos);
    }
    
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.MARKER_LOCATIONS.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public ListTag serializeNBT() {
        var listTag = new ListTag();
        for (var pos : this.locations) {
            listTag.add(NbtUtils.writeBlockPos(pos));
        }
        return listTag;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        this.locations = new ArrayList<>(nbt.size());
        for (var inner : nbt) {
            locations.add(NbtUtils.readBlockPos((CompoundTag) inner));
        }
    }
}
