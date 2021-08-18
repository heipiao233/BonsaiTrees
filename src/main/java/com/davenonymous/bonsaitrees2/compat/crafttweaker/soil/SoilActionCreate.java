package com.davenonymous.bonsaitrees2.compat.crafttweaker.soil;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.block.ModObjects;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.libnonymous.utils.RecipeHelper;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;

public class SoilActionCreate implements IRuntimeAction {
    String id;
    IIngredient ingredient;
    BlockState displayState;
    float tickModifier;
    String[] tags;
    SoilInfo soil;

    public SoilActionCreate(String id, IIngredient ingredient, BlockState displayState, float tickModifier, String[] tags) {
        this.id = id;
        this.ingredient = ingredient;
        this.displayState = displayState;
        this.tickModifier = tickModifier;
        this.tags = tags;

        this.soil = new SoilInfo(ResourceLocation.tryCreate(id), ingredient.asVanillaIngredient(), displayState, tickModifier);
        for(String tag : tags) {
            this.soil.addTag(tag);
        }
    }

    @Override
    public void apply() {
        RecipeHelper.registerRecipe(CTCraftingTableManager.recipeManager, ModObjects.soilRecipeType, this.soil);
    }

    @Override
    public String describe() {
        return String.format("[%s] Created new soil '%s'.", BonsaiTrees2.MODID, this.id);
    }
}
