package com.poggers.utils;

import net.minecraft.client.MinecraftClient;

public class ReloadWorld {
     public static void reloadChunks() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Ensure the client and the world are valid
        if (client != null && client.worldRenderer != null) {
            client.worldRenderer.reload();
        }
    }
}
