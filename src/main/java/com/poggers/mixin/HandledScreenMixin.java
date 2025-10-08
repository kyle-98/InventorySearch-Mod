package com.poggers.mixin;

import com.poggers.InventorySearch;
import com.poggers.utils.ColorUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if(InventorySearch.searchBox != null && InventorySearch.searchBox.isFocused()){
            if(InventorySearch.searchBox.keyPressed(keyInput)){
                cir.cancel();
            }

            if(keyInput.key() == MinecraftClient.getInstance().options.inventoryKey.getDefaultKey().getCode()){
                cir.cancel();
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void onMouseClick(Click click, boolean doubled,  CallbackInfoReturnable<Boolean> cir){
        if (InventorySearch.searchBox != null) {
            InventorySearch.searchBox.setFocused(InventorySearch.searchBox.isMouseOver(click.x(), click.y()));
        }
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void poggers$drawSlotHighlight(DrawContext context, Slot slot, CallbackInfo ci) {
        if (InventorySearch.searchBox == null) return;
        String searchText = InventorySearch.searchBox.getText().toLowerCase(Locale.ROOT).trim();
        if (searchText.isEmpty()) return;

        ItemStack stack = slot.getStack();
        if (stack.isEmpty()) return;

        String displayName = stack.getName().getString().toLowerCase(Locale.ROOT);
        String itemId = Registries.ITEM.getId(stack.getItem()).getPath().toLowerCase(Locale.ROOT);

        if (displayName.contains(searchText) || itemId.contains(searchText)) {
            int x = slot.x;
            int y = slot.y;
            context.fill(x, y, x + 16, y + 16,
                ColorUtils.parseHexColor(InventorySearch.getConfig().iSSettings.getHighlightColor()));
        }
    }

}
