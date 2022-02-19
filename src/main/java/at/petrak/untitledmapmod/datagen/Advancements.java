package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.UntitledMapMod;
import at.petrak.untitledmapmod.common.advancement.UseMapUnlockerTrigger;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Consumer;

public class Advancements extends AdvancementProvider {
    public static final UseMapUnlockerTrigger USE_MAP_UNLOCKER_TRIGGER = new UseMapUnlockerTrigger();

    public Advancements(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        Advancement.Builder.advancement()
            .display(simple(ModItems.WORLD_MAP.get(), "world_map", FrameType.TASK))
            .addCriterion("on_use", new UseMapUnlockerTrigger.Instance(EntityPredicate.Composite.ANY, false))
            .save(consumer, prefix("world_map"));

        Advancement.Builder.advancement()
            .display(simple(ModItems.MINIMAP.get(), "minimap", FrameType.TASK))
            .addCriterion("on_use", new UseMapUnlockerTrigger.Instance(EntityPredicate.Composite.ANY, true))
            .save(consumer, prefix("minimap"));
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent evt) {
        evt.enqueueWork(Advancements::registerTriggers);
    }

    public static void registerTriggers() {
        CriteriaTriggers.register(USE_MAP_UNLOCKER_TRIGGER);
    }

    protected static DisplayInfo simple(ItemLike icon, String name, FrameType frameType) {
        var expandedName = "advancement.untitledmapmod." + name;
        return new DisplayInfo(new ItemStack(icon.asItem()),
            new TranslatableComponent(expandedName),
            new TranslatableComponent(expandedName + ".desc"),
            null, frameType, true, true, false);
    }

    private static String prefix(String name) {
        return UntitledMapMod.MOD_ID + ":" + name;
    }
}
