package at.petrak.untitledmapmod.common.advancement;

import at.petrak.untitledmapmod.mixin.client.AccessorClientAdvancementsProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class AdvancementHelper {
    /**
     * Side-agnostic code to get a player's advancement status.
     */
    public static boolean isDone(Player player, ResourceLocation id) {
        if (player instanceof ServerPlayer splayer) {
            var advs = splayer.getAdvancements();
            var karen = splayer.getLevel().getServer().getAdvancements();
            var adv = karen.getAdvancement(id);
            if (adv != null) {
                return advs.getOrStartProgress(adv).isDone();
            }
        } else {
            // Problem: these aren't all loaded on world load!
            // We cheat by just always returning "no progress" until they're all loaded
            // If you're removing all advancements for some reason it won't throw an error when you try to
            // get a nonexistent advancement but why the hell are you doing that
            // Also sometimes the connection is null (before we connect to the server right as we log in)
            // so we check that too
            var conn = Minecraft.getInstance().getConnection();
            if (conn != null) {
                var advs = conn.getAdvancements();
                var adv = advs.getAdvancements().get(id);
                if (adv != null) {
                    var progresses = ((AccessorClientAdvancementsProgress) advs).umm$GetProgress();
                    var prog = progresses.get(adv);
                    return prog != null && prog.isDone();
                }
            }
        }
        return false;
    }
}
