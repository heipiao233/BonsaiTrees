package com.davenonymous.bonsaitrees2.setup;

import com.davenonymous.bonsaitrees2.network.Networking;
import net.minecraftforge.common.MinecraftForge;

public class ModSetup {
    public void init() {
        LootFunctions.registerLootFunctions();
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
        Networking.registerMessages();
    }
}
