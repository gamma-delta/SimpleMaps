package at.petrak.untitledmapmod.datagen.lootmods;

import at.petrak.untitledmapmod.UntitledMapMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootMods extends GlobalLootModifierProvider {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODS = DeferredRegister.create(
        ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, UntitledMapMod.MOD_ID);

    private static final RegistryObject<MapModifier.Serializer> MAPS = LOOT_MODS.register(
        "maps", MapModifier.Serializer::new);

    public ModLootMods(DataGenerator gen) {
        super(gen, UntitledMapMod.MOD_ID);
    }

    @Override
    protected void start() {
        add("maps_dungeon", MAPS.get(), new MapModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/simple_dungeon")).build()
        }, 0.2f));
        add("maps_cartographer", MAPS.get(), new MapModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/village/village_cartographer")).build()
        }, 0.8f));
        add("maps_shipwreck", MAPS.get(), new MapModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/shipwreck_map")).build()
        }, 0.8f));
        add("maps_stronghold_library", MAPS.get(), new MapModifier(new LootItemCondition[]{
            LootTableIdCondition.builder(new ResourceLocation("minecraft:chests/stronghold_library")).build()
        }, 0.5f));
    }
}
