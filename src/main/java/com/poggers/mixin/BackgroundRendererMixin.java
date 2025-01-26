package com.poggers.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.poggers.config.ModConfig;
import com.poggers.config.ModConfig.FogRemoval;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At(value = "HEAD"), cancellable = true)
    private static void applyCustomFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float density, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;

        if(ModConfig.visualSettings.getAllFogState() == FogRemoval.EVERYWHERE) {
            ci.cancel();
        }
        else if(ModConfig.visualSettings.getAllFogState() == FogRemoval.NETHER_ONLY && world != null && world.getRegistryKey() == World.NETHER){
            ci.cancel();
        }
    }
}
