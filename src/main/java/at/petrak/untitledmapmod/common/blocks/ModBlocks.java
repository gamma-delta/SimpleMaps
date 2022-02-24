package at.petrak.untitledmapmod.common.blocks;

import at.petrak.untitledmapmod.UntitledMapMod;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, UntitledMapMod.MOD_ID);

    public static final RegistryObject<BlockMarker> MARKERS[] = new RegistryObject[16];

    static {
        var markerProps = BlockBehaviour.Properties.of(Material.STONE);
        for (int i = 0; i < DyeColor.values().length; i++) {
            var color = DyeColor.values()[i];
            var block = new BlockMarker(color, markerProps);
            var name = "marker_" + color.getName();
            
            ModItems.ITEMS.register(name, () -> new BlockItem(block, ModItems.props()));
            RegistryObject<BlockMarker> marker = BLOCKS.register(name, () -> block);
            MARKERS[i] = marker;
        }
    }

}
