package com.davenonymous.bonsaitrees2.setup;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import com.davenonymous.bonsaitrees2.loot.function.CopyColor;
import com.davenonymous.bonsaitrees2.BonsaiTrees2;

public class LootFunctions {
    public static LootFunctionType COPY_COLOR;
    public static void registerLootFunctions() {
        COPY_COLOR = registerLootFunctions("copy_color", new CopyColor.Serializer());
    }
    private static LootFunctionType registerLootFunctions(final String name, final ILootSerializer<? extends ILootFunction> serializer) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(BonsaiTrees2.MODID, name), new LootFunctionType(serializer));
    }
}
