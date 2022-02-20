package at.petrak.untitledmapmod.client;

import at.petrak.untitledmapmod.UntitledMapMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MaterialColor;

public class MapHelper {
    public static final ResourceLocation TEX_VANILLA_MAP_ICONS = new ResourceLocation("minecraft",
        "textures/map/map_icons.png");
    public static final ResourceLocation TEX_MAP_ICONS = new ResourceLocation(UntitledMapMod.MOD_ID,
        "textures/gui/map.png");

    /**
     * Draw the region around the player to the texture and upload it.
     */
    public static void blitMapToTexture(LocalPlayer player, BlockPos targetPos, boolean onlyShowTop,
        DynamicTexture texture) {
        var world = player.getLevel();
        var canPlayerSeeSky = onlyShowTop
            || (world.canSeeSky(player.getOnPos().above()) && !world.dimensionType().hasCeiling());

        var mapWidth = texture.getPixels().getWidth();
        var mapHeight = texture.getPixels().getHeight();

        for (int px = 0; px < mapWidth; px++) {
            for (int py = 0; py < mapHeight; py++) {
                int x = targetPos.getX() + px - mapWidth / 2;
                int z = targetPos.getZ() + py - mapHeight / 2;
                int y = targetPos.getY() + 1;
                var pos = new BlockPos(x, y, z); // check at the eye position

                var color = MaterialColor.COLOR_BLACK;
                var brightness = 220;
                var bs = world.getBlockState(pos);
                if (canPlayerSeeSky) {
                    // Get the top layer of the world
                    var height = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                    var topPos = new BlockPos(x, height - 1, z);
                    var topBs = world.getBlockState(topPos);
                    color = topBs.getMapColor(world, topPos);
                    // A block's color is darker if placed at a lower elevation than the block north of it,
                    // or brighter if placed at a higher elevation than the block north of it.
                    // (This is a case of the mapping convention of top lighting.)
                    // -- https://minecraft.fandom.com/wiki/Map_item_format#Map_Pixel_Art
                    var northHeight = world.getHeight(Heightmap.Types.WORLD_SURFACE, x, z - 1);
                    var delta = height - northHeight;
                    brightness = 220 + delta * 4;
                } else if (bs.isAir() || !bs.getFluidState().isEmpty()) {
                    // As per popular map mods, display "solid" things as black/dark and the floor as bright
                    // Scan down
                    var maxDepth = 16;
                    for (int dy = 0; dy < maxDepth; dy++) {
                        var herePos = new BlockPos(x, y - dy, z);
                        var hereBs = world.getBlockState(herePos);
                        if (!hereBs.isAir()) {
                            // we found our limit
                            color = hereBs.getMapColor(world, herePos);
                            var leftoverY = Mth.frac(player.getX());
                            var prop = (float) (dy + leftoverY) / (float) (maxDepth - 1);
                            brightness -= (int) (100f * prop);

                            var northUpPos = herePos.offset(0, 1, -1);
                            var northUpBs = world.getBlockState(northUpPos);
                            var northPos = herePos.offset(0, 0, -1);
                            var northBs = world.getBlockState(northPos);
                            if (!northUpBs.isAir()) {
                                brightness -= 40;
                            } else if (northBs.isAir()) {
                                brightness += 40;
                            }

                            break;
                        }
                    }
                } // else keep it as black

                brightness = Mth.clamp(brightness, 0, 255);
                int r = (color.col >> 16 & 255) * brightness / 255;
                int g = (color.col >> 8 & 255) * brightness / 255;
                int b = (color.col & 255) * brightness / 255;
                int colorInt = -16777216 | b << 16 | g << 8 | r;
                texture.getPixels().setPixelRGBA(px, py, colorInt | 0xFF000000);
            }
        }

        texture.upload();
    }

    /**
     * Make sure you have the `PositionTexShader` set
     */
    public static void renderQuad(PoseStack ps, float width, float height, float u, float v, float uw, float vh,
        ResourceLocation tex) {
        var mat = ps.last().pose();
        var tess = Tesselator.getInstance();
        var buf = tess.getBuilder();

        RenderSystem.setShaderTexture(0, tex);

        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buf.vertex(mat, 0, 0, 0)
            .uv(u, v)
            .endVertex();
        buf.vertex(mat, 0, height, 0)
            .uv(u, v + vh)
            .endVertex();
        buf.vertex(mat, width, height, 0)
            .uv(u + uw, v + vh)
            .endVertex();
        buf.vertex(mat, width, 0, 0)
            .uv(u + uw, v)
            .endVertex();
        tess.end();
    }
}
