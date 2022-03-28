package at.petrak.untitledmapmod.client;

import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.advancement.AdvancementHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MinimapOverlay {
    private static final int MAP_WIDTH = 128;
    private static final int MAP_HEIGHT = 128;
    private static DynamicTexture MINIMAP;
    private static final ResourceLocation TEX_MINIMAP = new ResourceLocation(SimpleMapMod.MOD_ID, "minimap");

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
        if (!AdvancementHelper.isDone(player, new ResourceLocation(SimpleMapMod.MOD_ID, "minimap"))) {
            return;
        }

        var renderWidth = 64f;
        var renderHeight = 64f;

        var ps = evt.getMatrixStack();
        var windowWidth = mc.getWindow().getGuiScaledWidth();

        ps.pushPose();
        ps.translate(windowWidth - renderWidth - 10, 10, 0);


        var oldShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        // Draw snek bg
        ps.pushPose();
        MapHelper.renderQuad(ps, 63, 63, 68f / 256f, 0, 60f / 256f, 60f / 256f, MapHelper.TEX_MAP_MAIN);
        ps.popPose();

        // Draw the map itself
        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.scale(renderWidth / MAP_WIDTH, renderHeight / MAP_HEIGHT, 1);
        var leftoverX = Mth.frac(player.getX());
        var leftoverZ = Mth.frac(player.getZ());
        ps.translate(-leftoverX, -leftoverZ, 0);
        MapHelper.renderQuad(ps, MAP_WIDTH, MAP_HEIGHT, 0, 0, 1, 1, TEX_MINIMAP);
        ps.popPose();

        // Draw other mobs around
        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.translate(renderWidth / 2f, renderHeight / 2f, 0);
        // 4x4 icons: center them
        ps.translate(-2f, -2f, 0);
        var xscale = renderWidth / MAP_WIDTH;
        var yscale = renderHeight / MAP_HEIGHT;
        ps.scale(xscale, yscale, 1);

        var aabbDelta = new Vec3(MAP_WIDTH / 2f, 16, MAP_HEIGHT / 2f);
        var aabb = new AABB(player.position().subtract(aabbDelta), player.position().add(aabbDelta));
        var entities = player.level.getEntities(player, aabb, e -> e instanceof LivingEntity);

        for (var entity : entities) {
            var delta = entity.position().subtract(player.position());
            var isHostile = entity instanceof Enemy;
            var sx = isHostile ? 4f / 16f : 0f;

            ps.pushPose();
            ps.translate(delta.x, delta.z, 0);
            MapHelper.renderQuad(ps, 4f, 4f, sx, 0, 4f / 16f, 4f / 16f, MapHelper.TEX_MAP_DECO);
            ps.popPose();
        }
        ps.popPose();

        // Draw the player icon
        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.translate(renderWidth / 2f, renderHeight / 2f, 0);
        ps.mulPose(Quaternion.fromXYZ(0f, 0f, Mth.PI + player.getViewYRot(evt.getPartialTicks()) / 180f * 3.14159f));
        ps.translate(-5f / 4f, -7f / 4f, 0f);
        MapHelper.renderQuad(ps, 5f / 2f, 7f / 2f, 2f / 128f, 0f, 5f / 128f, 7f / 128f, MapHelper.TEX_VANILLA_MAP_DECO);
        ps.popPose();

        // Draw the window frame overlay
        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.translate(-2, -2, 0);
        MapHelper.renderQuad(ps, 68, 80, 0, 0, 68f / 256f, 80f / 256f,
            MapHelper.TEX_MAP_MAIN);
        ps.popPose();

        // do the text last because it sets shaders
        ps.translate(0, 0, 1);
        ps.pushPose();
        var coordsText = String.format("%d, %d, %d", player.getBlockX(), player.getBlockY(), player.getBlockZ());
        var msgWidth = mc.font.width(coordsText);
        ps.translate(renderWidth / 2f, renderHeight + 7, 0);
        ps.scale(0.5f, 0.5f, 1f);
        mc.font.drawShadow(ps, coordsText, -msgWidth / 2f, 0, -1);
        ps.popPose();

        RenderSystem.setShader(() -> oldShader);

        ps.popPose();
    }

    @SubscribeEvent
    public static void updateMap(TickEvent.ClientTickEvent evt) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (!AdvancementHelper.isDone(player, new ResourceLocation(SimpleMapMod.MOD_ID, "minimap"))) {
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
            MapHelper.blitMapToTexture(player, player.getOnPos().above(), false, MINIMAP);
        }
    }
}
