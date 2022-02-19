package at.petrak.untitledmapmod.client;

import at.petrak.untitledmapmod.UntitledMapMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;

// https://github.com/VazkiiMods/Quark/blob/master/src/main/java/vazkii/quark/base/client/handler/ModKeybindHandler.java
@OnlyIn(Dist.CLIENT)
public class ModKeybinds {
    public static final String GROUP = UntitledMapMod.MOD_ID + ".gui.keygroup";

    public static KeyMapping OPEN_WORLD_MAP;
    
    public static KeyMapping bind(String name, String key) {
        KeyMapping kb = new KeyMapping(UntitledMapMod.MOD_ID + ".keybind." + name,
            InputConstants.Type.KEYSYM,
            (key == null ? InputConstants.UNKNOWN :
                InputConstants.getKey("key.keyboard." + key)).getValue(),
            GROUP);
        ClientRegistry.registerKeyBinding(kb);
        return kb;
    }

    public static void init() {
        OPEN_WORLD_MAP = bind("open_world_map", "m");
    }
}
