package com.poggers.mixin;

import com.poggers.HoloUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (HoloUtils.searchBox != null && HoloUtils.searchBox.isFocused()) {
            if(HoloUtils.searchBox != null && HoloUtils.searchBox.isFocused()){
                if(HoloUtils.searchBox.keyPressed(keyCode, scanCode, modifiers)){
                    cir.cancel();
                }

                if(keyCode == MinecraftClient.getInstance().options.inventoryKey.getDefaultKey().getCode()){
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir){
        if (HoloUtils.searchBox != null) {
            HoloUtils.searchBox.setFocused(HoloUtils.searchBox.isMouseOver(mouseX, mouseY));
        }
    }
}
