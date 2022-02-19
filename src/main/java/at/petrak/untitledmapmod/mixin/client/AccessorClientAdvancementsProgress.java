package at.petrak.untitledmapmod.mixin.client;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientAdvancements.class)
public interface AccessorClientAdvancementsProgress {
    @Accessor("progress")
    Map<Advancement, AdvancementProgress> umm$GetProgress();
}
