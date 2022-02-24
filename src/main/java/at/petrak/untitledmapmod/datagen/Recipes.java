package at.petrak.untitledmapmod.datagen;

import at.petrak.untitledmapmod.common.blocks.ModBlocks;
import at.petrak.untitledmapmod.common.items.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipes) {
        for (int i = 0; i < DyeColor.values().length; i++) {
            var color = DyeColor.values()[i];
            var marker = ModBlocks.MARKERS[i].get();

            ShapedRecipeBuilder.shaped(marker)
                .define('D', DyeItem.byColor(color))
                .define('C', Items.COMPASS)
                .define('B', Items.CHISELED_STONE_BRICKS)
                .pattern(" C ")
                .pattern("BDB")
                .pattern("BBB")
                .unlockedBy("has_item", has(ModItems.WORLD_MAP.get()))
                .save(recipes);
        }
    }
}
