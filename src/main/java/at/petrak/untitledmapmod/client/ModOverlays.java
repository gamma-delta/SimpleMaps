package at.petrak.untitledmapmod.client;

import at.petrak.untitledmapmod.UntitledMapMod;
import at.petrak.untitledmapmod.common.advancement.AdvancementHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModOverlays {
    private static final int MAP_WIDTH = 128;
    private static final int MAP_HEIGHT = 128;
    private static DynamicTexture MINIMAP;
    private static final ResourceLocation TEX_MINIMAP = new ResourceLocation(UntitledMapMod.MOD_ID, "minimap");

    private static BlockPos cachedPos = null;

    public static void initTextures() {
        MINIMAP = new DynamicTexture(MAP_WIDTH, MAP_HEIGHT, true);
        var tm = Minecraft.getInstance().textureManager;
        tm.register(TEX_MINIMAP, MINIMAP);
    }

    @SubscribeEvent
    public static void renderMapOverlay(RenderGameOverlayEvent.Post evt) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.getConnection() == null || evt.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        var player = mc.player;
        if (!AdvancementHelper.isDone(player, new ResourceLocation(UntitledMapMod.MOD_ID, "minimap"))) {
            return;
        }

        var renderWidth = 64f;
        var renderHeight = 64f;

        var ps = evt.getMatrixStack();
        var windowWidth = mc.getWindow().getGuiScaledWidth();

        ps.pushPose();
        ps.translate(windowWidth - renderWidth - 10, 10, 0);


        var oldShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // Draw the map itself
        ps.pushPose();
        ps.scale(renderWidth / MAP_WIDTH, renderHeight / MAP_HEIGHT, 1);
        var leftoverX = Mth.frac(player.getX());
        var leftoverZ = Mth.frac(player.getZ());
        ps.translate(-leftoverX, -leftoverZ, 0);
        MapHelper.renderQuad(ps, MAP_WIDTH, MAP_HEIGHT, 0, 0, 1, 1, TEX_MINIMAP);
        ps.popPose();

        // Draw the window overlay
        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.translate(-2, -2, 0);
        MapHelper.renderQuad(ps, 68, 80, 0, 0, 68f / 256f, 80f / 256f,
            MapHelper.TEX_MAP_ICONS);
        ps.popPose();

        ps.translate(0, 0, 1);
        ps.pushPose();
        var coordsText = String.format("%d, %d, %d", player.getBlockX(), player.getBlockY(), player.getBlockZ());
        var msgWidth = mc.font.width(coordsText);
        ps.translate(renderWidth / 2f, renderHeight + 7, 0);
        ps.scale(0.5f, 0.5f, 1f);
        mc.font.drawShadow(ps, coordsText, -msgWidth / 2f, 0, -1);
        ps.popPose();

        // Draw the player icon
        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.translate(renderWidth / 2f, renderHeight / 2f, 0);
        ps.mulPose(Quaternion.fromXYZ(0f, 0f, Mth.PI + player.getViewYRot(evt.getPartialTicks()) / 180f * 3.14159f));
        ps.translate(-2.5f, -3.5f, 0f);
        MapHelper.renderQuad(ps, 5f, 7f, 2f / 128f, 0f, 5f / 128f, 7f / 128f,
            MapHelper.TEX_VANILLA_MAP_ICONS);
        ps.popPose();

        RenderSystem.setShader(() -> oldShader);

        ps.popPose();
    }

    @SubscribeEvent
    public static void updateMap(TickEvent.ClientTickEvent evt) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (!AdvancementHelper.isDone(player, new ResourceLocation(UntitledMapMod.MOD_ID, "minimap"))) {
            return;
        }

        var doRedraw = false;
        if (cachedPos == null) {
            cachedPos = player.getOnPos();
            doRedraw = true;
        } else {
            var herePos = player.getOnPos();
            if (!herePos.closerThan(cachedPos, 1d)) {
                cachedPos = herePos;
                doRedraw = true;
            }
        }

        if (doRedraw) {
            MapHelper.blitMapToTexture(player, player.getOnPos().above(), MINIMAP);
        }
    }
}
