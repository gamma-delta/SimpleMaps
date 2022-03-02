package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.UntitledMapMod;
import at.petrak.untitledmapmod.common.advancement.UseMapUnlockerTrigger;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        var root = Advancement.Builder.advancement()
            .display(Items.COMPASS, new TranslatableComponent("advancement." + UntitledMapMod.MOD_ID + ":root"),
                new TranslatableComponent("advancement." + UntitledMapMod.MOD_ID + ":root.desc"),
                new ResourceLocation("minecraft:textures/block/cartography_table_side3.png"),
                FrameType.TASK, true, true, false)
            .addCriterion("i_cant_think_of_anything", new TickTrigger.TriggerInstance(EntityPredicate.Composite.ANY))
            .save(consumer, prefix("root"));

        Advancement.Builder.advancement()
            .display(simple(ModItems.WORLD_MAP.get(), "world_map", FrameType.TASK))
            .parent(root)
            .addCriterion("on_use", new UseMapUnlockerTrigger.Instance(EntityPredicate.Composite.ANY, false))
            .save(consumer, prefix("world_map"));

        Advancement.Builder.advancement()
            .display(simple(ModItems.MINIMAP.get(), "minimap", FrameType.TASK))
            .parent(root)
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
        var expandedName = "advancement." + UntitledMapMod.MOD_ID + ":" + name;
        return new DisplayInfo(new ItemStack(icon.asItem()),
            new TranslatableComponent(expandedName),
            new TranslatableComponent(expandedName + ".desc"),
            null, frameType, true, true, false);
    }

    private static String prefix(String name) {
        return UntitledMapMod.MOD_ID + ":" + name;
    }
}
