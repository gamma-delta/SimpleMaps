package at.petrak.untitledmapmod.datagen.lootmods;

import at.petrak.paucal.api.lootmod.PaucalAddItemModifier;
import at.petrak.paucal.api.lootmod.PaucalLootMods;
import at.petrak.untitledmapmod.SimpleMapMod;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModLootMods extends GlobalLootModifierProvider {

    public ModLootMods(DataGenerator gen) {
        super(gen, SimpleMapMod.MOD_ID);
    }

    @Override
    protected void start() {
        var items = new Item[]{
            ModItems.MINIMAP.get(),
            ModItems.WORLD_MAP.get(),
        };
        var names = new String[]{
            "minmap", "world_map"
        };
        for (int i = 0; i < 2; i++) {
            var item = items[i];
            var name = names[i];
            add(name + "_dungeon", PaucalLootMods.ADD_ITEM.get(),
                new PaucalAddItemModifier(item, 1, new LootItemCondition[]{
                    LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/simple_dungeon")).build(),
                    LootItemRandomChanceCondition.randomChance(0.2f).build()
                }));
            add(name + "_cartographer", PaucalLootMods.ADD_ITEM.get(),
                new PaucalAddItemModifier(item, 1, new LootItemCondition[]{
                    LootTableIdCondition.builder(
                        new ResourceLocation("minecraft:chests/village/village_cartographer")).build(),
                    LootItemRandomChanceCondition.randomChance(0.8f).build()
                }));
            add(name + "_shipwreck", PaucalLootMods.ADD_ITEM.get(),
                new PaucalAddItemModifier(item, 1, new LootItemCondition[]{
                    LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/shipwreck_map")).build(),
                    LootItemRandomChanceCondition.randomChance(0.8f).build()
                }));
            add(name + "_stronghold_library", PaucalLootMods.ADD_ITEM.get(),
                new PaucalAddItemModifier(item, 1, new LootItemCondition[]{
                    LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/stronghold_library")).build(),
                    LootItemRandomChanceCondition.randomChance(0.5f).build()
                }));
        }
    }
}
