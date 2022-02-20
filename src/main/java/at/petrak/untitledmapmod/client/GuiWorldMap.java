package at.petrak.untitledmapmod.client;

import at.petrak.untitledmapmod.UntitledMapMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GuiWorldMap extends Screen {
    private static final float BLOCKS_TO_PIXELS = 2f;

    private static final int MAP_WIDTH = 192;
    private static final int MAP_HEIGHT = 108;

    private static final int MAP_BLOCK_WIDTH = (int) (MAP_WIDTH * BLOCKS_TO_PIXELS);
    private static final int MAP_BLOCK_HEIGHT = (int) (MAP_HEIGHT * BLOCKS_TO_PIXELS);
    private static DynamicTexture WORLD_MAP;
    private static final ResourceLocation TEX_WORLD_MAP = new ResourceLocation(UntitledMapMod.MOD_ID, "world_map");

    public static void initTextures() {
        WORLD_MAP = new DynamicTexture(MAP_BLOCK_WIDTH, MAP_BLOCK_HEIGHT, true);
        var tm = Minecraft.getInstance().textureManager;
        tm.register(TEX_WORLD_MAP, WORLD_MAP);
    }

    // Block position at the center of the displayed map
    private Vec2 centerPos;
    private LocalPlayer player;

    public GuiWorldMap(LocalPlayer player) {
        super(new TextComponent(""));

        this.centerPos = new Vec2((float) player.getX(), (float) player.getZ());
        this.player = player;
    }

    @Override
    protected void init() {
        this.blitTexture();
    }

    @Override
    public void render(PoseStack ps, int pMouseX, int pMouseY, float pPartialTick) {
        var oldShader = RenderSystem.getShader();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        ps.pushPose();

        ps.pushPose();
        ps.translate(mapCorner().x, mapCorner().y, 0);
        MapHelper.renderQuad(ps, 192f, 144f, 0f, 80f / 256f, 192f / 256f, 144f / 256f, MapHelper.TEX_MAP_ICONS);
        ps.popPose();

        ps.translate(0, 0, 1);
        ps.pushPose();
        ps.translate(mapCorner().x, mapCorner().y, 0);
        MapHelper.renderQuad(ps, MAP_WIDTH, MAP_HEIGHT, 0f, 0, 1, 1, TEX_WORLD_MAP);
        ps.popPose();

        ps.pushPose();

        RenderSystem.setShader(() -> oldShader);
    }

    private void blitTexture() {
        var centerPos = new BlockPos(Math.round(this.centerPos.x), player.getBlockY() + 1,
            Math.round(this.centerPos.y));
        MapHelper.blitMapToTexture(this.player, centerPos, true, WORLD_MAP);
    }

    private Vec2 mapCorner() {
        return new Vec2(width / 2f - MAP_WIDTH / 2f, height / 2f - MAP_HEIGHT / 2f);
    }

    @SubscribeEvent
    public static void keypress(Keyp evt) {
        var mc = Minecraft.getInstance();
        if (ModKeybinds.OPEN_WORLD_MAP.isDown() && mc.level != null && mc.screen == null) {
            mc.setScreen(new GuiWorldMap(mc.player));
        }
    }
}
