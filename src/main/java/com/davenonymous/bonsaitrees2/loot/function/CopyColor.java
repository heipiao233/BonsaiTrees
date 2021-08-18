package com.davenonymous.bonsaitrees2.loot.function;

import com.davenonymous.bonsaitrees2.setup.LootFunctions;
import com.davenonymous.libnonymous.misc.ColorProperty;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class CopyColor extends LootFunction {
    protected CopyColor(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        BlockState state = context.get(LootParameters.BLOCK_STATE);
        if(state.hasProperty(ColorProperty.COLOR)) {
            int color = state.get(ColorProperty.COLOR);
            stack.getOrCreateTag().putInt("bonsaitrees2:color", color);
        }
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<CopyColor> {

        @Override
        public CopyColor deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new CopyColor(conditionsIn);
        }
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootFunctions.COPY_COLOR;
    }
}
