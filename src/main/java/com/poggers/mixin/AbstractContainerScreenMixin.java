package com.poggers.mixin;

import com.poggers.InventorySearch;
import com.poggers.utils.ColorUtils;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if(InventorySearch.searchBox != null && InventorySearch.searchBox.isFocused()){
            if(InventorySearch.searchBox.keyPressed(event)){
                cir.cancel();
            }
            
            if(Minecraft.getInstance().options.keyInventory.matches(event)){
                cir.cancel();
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClick(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir){
        if (InventorySearch.searchBox != null) {
            InventorySearch.searchBox.setFocused(InventorySearch.searchBox.isMouseOver(event.x(), event.y()));
        }
    }

    @Inject(method = "extractSlot", at = @At("HEAD"))
    private void poggers$drawSlotHighlight(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (InventorySearch.searchBox == null) return;
        String searchText = InventorySearch.searchBox.getValue().toLowerCase(Locale.ROOT).trim();
        if (searchText.isEmpty()) return;

        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) return;

        String displayName = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase(Locale.ROOT);

        if (displayName.contains(searchText) || itemId.contains(searchText)) {
            int slotX = slot.x;
            int slotY = slot.y;
            graphics.fill(slotX, slotY, slotX + 16, slotY + 16,
                ColorUtils.parseHexColor(InventorySearch.getConfig().iSSettings.getHighlightColor()));
        }
    }
}