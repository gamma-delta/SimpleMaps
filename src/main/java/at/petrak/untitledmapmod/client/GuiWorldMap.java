package at.petrak.untitledmapmod.client;

import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.advancement.AdvancementHelper;
import at.petrak.untitledmapmod.common.blocks.BlockMarker;
import at.petrak.untitledmapmod.common.network.ModMessages;
import at.petrak.untitledmapmod.common.network.MsgMarkerLocsSyn;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class GuiWorldMap extends Screen {
    private static final float BLOCKS_TO_PIXELS = 2f;

    // *Render pixel* size of the map
    private static final int MAP_WIDTH = 192;
    private static final int MAP_HEIGHT = 108;

    // Actual number of pixels on the map
    private static final int MAP_BLOCK_WIDTH = (int) (MAP_WIDTH * BLOCKS_TO_PIXELS);
    private static final int MAP_BLOCK_HEIGHT = (int) (MAP_HEIGHT * BLOCKS_TO_PIXELS);

    private static final int PATCH_SIZE = 64;
    private static final int PATCHES_ACROSS = MAP_BLOCK_WIDTH / PATCH_SIZE + 2;
    private static final int PATCHES_DOWN = MAP_BLOCK_WIDTH / PATCH_SIZE + 2;

    private static DynamicTexture[] PATCHES = new DynamicTexture[PATCHES_ACROSS * PATCHES_DOWN];

    private static final String TEX_WORLD_MAP_STUB = SimpleMapMod.MOD_ID + ":world_map/";
    private static final ResourceLocation TEX_BORDER = new ResourceLocation(SimpleMapMod.MOD_ID,
        "textures/gui/border.png");


    public static void initTextures() {
        var tm = Minecraft.getInstance().textureManager;

        for (int y = 0; y < PATCHES_DOWN; y++) {
            for (int x = 0; x < PATCHES_ACROSS; x++) {
                int idx = y * PATCHES_ACROSS + x;
                var tex = PATCHES[idx] = new DynamicTexture(PATCH_SIZE, PATCH_SIZE, true);
                // Tmp
                tex.getPixels()
                    .fillRect(0, 0, PATCH_SIZE, PATCH_SIZE, FastColor.ARGB32.color(255, 255 - x * 25, 255 - y * 25, 0));
                tex.upload();
                var name = new ResourceLocation(TEX_WORLD_MAP_STUB + idx);
                SimpleMapMod.LOGGER.info("registering texture {}", name);
                tm.register(name, tex);
            }
        }
    }

    // Pixel distance from the upper-left corner of patch (1, 1) to the upper-left corner of the visible rect.
    private float patchOffsetX = 0;
    private float patchOffsetY = 0;
    private final BlockPos playerPos;
    private final LocalPlayer player;
    private List<Pair<BlockPos, DyeColor>> markerLocations;

    private MapWidget mapWidget;

    private Deque<Integer> idxesToRedraw = new ArrayDeque<>();

    public GuiWorldMap(LocalPlayer player) {
        super(new TextComponent(""));

        this.playerPos = player.getOnPos();
        this.player = player;
        this.markerLocations = new ArrayList<>();

        // Start with the identity mapping.
        for (int y = 0; y < PATCHES_DOWN; y++) {
            for (int x = 0; x < PATCHES_ACROSS; x++) {
                int idx = y * PATCHES_ACROSS + x;
                this.idxesToRedraw.add(idx);
            }
        }
    }

    public void loadMarkerLocations(List<BlockPos> markerLocations) {
        for (var pos : markerLocations) {
            var blockThere = player.level.getBlockState(pos);
            if (blockThere.getBlock() instanceof BlockMarker marker) {
                this.markerLocations.add(new Pair<>(pos, marker.color));
            } else {
                SimpleMapMod.LOGGER.warn("did not find a marker at {}", pos);
            }
        }
        SimpleMapMod.LOGGER.info("got: {}", this.markerLocations);
    }

    private ResourceLocation getPatchIdx(int x, int y) {
        var xOff = Mth.positiveModulo(this.patchOffsetX, PATCH_SIZE);
        var yOff = Mth.positiveModulo(this.patchOffsetY, PATCH_SIZE);
        var idx = (y - yOff) * PATCHES_ACROSS * (x - xOff);
        return new ResourceLocation(TEX_WORLD_MAP_STUB + (int) idx);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(this.mapWidget =
            new MapWidget(width / 2f - MAP_WIDTH / 2f, height / 2f - MAP_HEIGHT / 2f));

        ModMessages.getNetwork().sendToServer(new MsgMarkerLocsSyn());
    }

    @Override
    public void tick() {
        if (!this.idxesToRedraw.isEmpty()) {
            var idx = this.idxesToRedraw.removeFirst();
            // SimpleMapMod.LOGGER.info("'redrawing' {}", idx);
        }
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        this.mapWidget.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.mapWidget.mouseAnchor = null;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    private class MapWidget implements Widget, GuiEventListener, NarratableEntry {
        private float x, y;
        public Vec2 mouseAnchor = null;
        public Vec2 posAnchor = null;

        public MapWidget(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean isMouseOver(double pMouseX, double pMouseY) {
            return x < pMouseX && pMouseX < x + MAP_WIDTH && y < pMouseY && pMouseY < y + MAP_HEIGHT;
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            mouseAnchor = new Vec2((float) pMouseX, (float) pMouseY);
            posAnchor = new Vec2(patchOffsetX, patchOffsetY);
            return true;
        }

        @Override
        public void mouseMoved(double pMouseX, double pMouseY) {
            if (this.mouseAnchor != null && this.posAnchor != null) {
                patchOffsetX = posAnchor.x + (float) pMouseX - mouseAnchor.x;
                patchOffsetY = posAnchor.y + (float) pMouseY - mouseAnchor.y;
            }
        }

        @Override
        public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
            this.mouseAnchor = null;
            posAnchor = null;
            return true;
        }

        @Override
        public void render(PoseStack ps, int pMouseX, int pMouseY, float pPartialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            ps.translate(x, y, 0);

            ps.pushPose();
            MapHelper.renderQuad(ps, MAP_WIDTH, MAP_HEIGHT, 0, 80f / 255f, MAP_WIDTH / 255f, MAP_HEIGHT / 255f,
                MapHelper.TEX_MAP_MAIN);
            ps.popPose();

            ps.translate(0, 0, 1);
            ps.pushPose();
            ps.scale(1 / BLOCKS_TO_PIXELS, 1 / BLOCKS_TO_PIXELS, 1);
            for (int x = 0; x < PATCHES_ACROSS; x++) {
                for (int y = 0; y < PATCHES_DOWN; y++) {
                    ps.pushPose();
                    ps.translate((x - 1) * PATCH_SIZE - patchOffsetX, (y - 1) * PATCH_SIZE - patchOffsetY, 0);
                    MapHelper.renderQuad(ps, PATCH_SIZE, PATCH_SIZE, 0, 0, 1, 1, getPatchIdx(x, y));
                    ps.popPose();
                }
            }
            ps.popPose();

            // Markers
            ps.translate(0, 0, 1);
            ps.pushPose();
            ps.translate(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
            RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
            RenderSystem.setShaderTexture(0, MapHelper.TEX_MAP_DECO);

            for (var pair : markerLocations) {
                var pos = pair.getFirst();
                var dx = pos.getX() - patchOffsetX;
                var dy = pos.getZ() - patchOffsetY;
                if (Mth.abs(dx) < MAP_BLOCK_WIDTH / 2f && Mth.abs(dy) < MAP_BLOCK_HEIGHT / 2f) {
                    ps.pushPose();

                    var px = dx / BLOCKS_TO_PIXELS;
                    var py = dy / BLOCKS_TO_PIXELS;
                    ps.translate(px, py, 0);
                    ps.scale(1f / BLOCKS_TO_PIXELS, 1f / BLOCKS_TO_PIXELS, 1);

                    var color = pair.getSecond().getTextColor() | 0xff_000000;


                    var mat = ps.last().pose();
                    var tess = Tesselator.getInstance();
                    var buf = tess.getBuilder();

                    buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
                    buf.vertex(mat, 0, 0, 0)
                        .color(color)
                        .uv(8f / 16f, 0)
                        .endVertex();
                    buf.vertex(mat, 0, 4f, 0)
                        .color(color)
                        .uv(8f / 16f, 4f / 16f)
                        .endVertex();
                    buf.vertex(mat, 4f, 4f, 0)
                        .color(color)
                        .uv(12f / 16f, 4f / 16f)
                        .endVertex();
                    buf.vertex(mat, 4f, 0, 0)
                        .color(color)
                        .uv(12f / 16f, 0)
                        .endVertex();
                    tess.end();

                    ps.popPose();
                }
            }
            ps.popPose();

            // Player icon
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            ps.translate(0, 0, 1);
            ps.pushPose();
            ps.translate(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
            ps.translate(
                (player.getX() - patchOffsetX) / BLOCKS_TO_PIXELS,
                (player.getZ() - patchOffsetY) / BLOCKS_TO_PIXELS,
                0);
            ps.mulPose(Quaternion.fromXYZ(0f, 0f, Mth.PI + player.getYRot() / 180f * 3.14159f));
            ps.translate(-5f / 4f, -7f / 4f, 0f);
            MapHelper.renderQuad(ps, 5f / 2f, 7f / 2f, 2f / 128f, 0f, 5f / 128f, 7f / 128f,
                MapHelper.TEX_VANILLA_MAP_DECO);
            ps.popPose();

            ps.translate(0, 0, 1);
            ps.pushPose();
            ps.translate(-16, -16, 0);
            MapHelper.renderQuad(ps, 224, 140, 0, 0, 224f / 256f, 140f / 256f, TEX_BORDER);
            ps.popPose();
        }


        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.FOCUSED;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        }
    }

    @SubscribeEvent
    public static void checkKeypresses(TickEvent.ClientTickEvent evt) {
        var mc = Minecraft.getInstance();
        if (ModKeybinds.OPEN_WORLD_MAP.isDown() && mc.player != null && mc.screen == null) {
            if (AdvancementHelper.isDone(mc.player, new ResourceLocation(SimpleMapMod.MOD_ID, "world_map"))) {
                mc.setScreen(new GuiWorldMap(mc.player));
            } else {
                mc.player.displayClientMessage(
                    new TranslatableComponent(SimpleMapMod.MOD_ID + ".message.fail_open_map"),
                    true);
            }
        }
    }

    private static int mod(int num, int dem) {
        return (num % dem + dem) % dem;
    }
}
