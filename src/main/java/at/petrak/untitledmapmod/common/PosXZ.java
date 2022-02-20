package at.petrak.untitledmapmod.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

/**
 * Position of a *chunk*. Each block pos belongs to the -X, -Z corner of the chunk its in.
 */
public record PosXZ(int x, int z) {
    public static PosXZ fromBlockPos(BlockPos pos) {
        int x = pos.getX() < 0 ? (pos.getX() - 16) / 16 : pos.getX() / 16;
        int z = pos.getZ() < 0 ? (pos.getZ() - 16) / 16 : pos.getZ() / 16;
        return new PosXZ(x, z);
    }

    /**
     * Return the block pos of the corner.
     */
    public Pair<Integer, Integer> toBlockPos() {
        int x = this.x * 16 + this.x < 0 ? 1 : 0;
        int z = this.z * 16 + this.z < 0 ? 1 : 0;
        return new Pair<>(x, z);
    }
}
