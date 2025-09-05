package com.poggers.mixin;

import com.poggers.InventorySearch;
import com.poggers.utils.ColorUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (InventorySearch.searchBox != null && InventorySearch.searchBox.isFocused()) {
            if(InventorySearch.searchBox != null && InventorySearch.searchBox.isFocused()){
                if(InventorySearch.searchBox.keyPressed(keyCode, scanCode, modifiers)){
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
        if (InventorySearch.searchBox != null) {
            InventorySearch.searchBox.setFocused(InventorySearch.searchBox.isMouseOver(mouseX, mouseY));
        }
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void poggers$drawSlotHighlight(DrawContext context, Slot slot, CallbackInfo ci) {
        if (InventorySearch.searchBox != null && !InventorySearch.searchBox.getText().isEmpty()) {
            String searchText = InventorySearch.searchBox.getText().toLowerCase();
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.getName().getString().toLowerCase().contains(searchText)) {
                int x = slot.x;
                int y = slot.y;

                context.fill(x, y, x + 16, y + 16, ColorUtils.parseHexColor(InventorySearch.getConfig().iSSettings.getHighlightColor()));
            }
        }
    }

}
