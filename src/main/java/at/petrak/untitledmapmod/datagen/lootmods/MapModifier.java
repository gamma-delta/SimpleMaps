package at.petrak.untitledmapmod.datagen.lootmods;

import at.petrak.untitledmapmod.common.items.ModItems;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapModifier extends LootModifier {
    private final float chance;

    public MapModifier(LootItemCondition[] conditions, float chance) {
        super(conditions);
        this.chance = chance;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        var rand = context.getRandom();
        if (rand.nextFloat() < chance) {
            var minimap = rand.nextBoolean();
            generatedLoot.add(new ItemStack((minimap ? ModItems.MINIMAP : ModItems.WORLD_MAP).get()));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<MapModifier> {
        @Override
        public MapModifier read(ResourceLocation location, JsonObject json, LootItemCondition[] conditions) {
            var chance = GsonHelper.getAsFloat(json, "chance");
            return new MapModifier(conditions, chance);
        }

        @Override
        public JsonObject write(MapModifier instance) {
            var json = this.makeConditions(instance.conditions);
            json.addProperty("chance", instance.chance);
            return json;
        }
    }
}
