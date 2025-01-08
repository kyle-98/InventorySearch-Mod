package com.poggers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.poggers.mixin.HandledScreenAccessor;
import com.poggers.mixin.ScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class InventorySearch implements ClientModInitializer {
	public static TextFieldWidget searchBox;
//	private static final int DARKENING_COLOR = 0x80000000;
private static final int DARKENING_COLOR = 0xFFFF0000;
	@Override
	public void onInitializeClient() {
		ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
			if(screen instanceof GenericContainerScreen || screen instanceof InventoryScreen || screen instanceof ShulkerBoxScreen){
				searchBox = new TextFieldWidget(
						client.textRenderer,
						w - 120,
						h - 40,
						100,
						20,
						Text.literal("Search...")
				);

				searchBox.setPlaceholder(Text.literal("Search..."));

				((ScreenAccessor) screen).invokeAddDrawableChild(searchBox);

				ScreenEvents.afterRender(screen).register((screenArg, context, mouseX, mouseY, delta) -> {
					if (screenArg instanceof GenericContainerScreen handledScreen ) {
						if (searchBox.isFocused() && !searchBox.getText().isEmpty()) {
							String searchText = searchBox.getText().toLowerCase();
							System.out.println(searchText);

							Map<Slot, SlotViewWrapper> views = new HashMap<>();
							for (Slot slot : handledScreen.getScreenHandler().slots) {
								ItemStack stack = slot.getStack();
								if (stack.isEmpty()) continue;

								boolean matches = stack.getName().getString().toLowerCase().contains(searchText);

								if (!matches) {
									views.put(slot, new SlotViewWrapper(false));
								} else {
									views.put(slot, new SlotViewWrapper(true));
								}
							}

							drawSlotOverlay(handledScreen, views, context);
						}
					}
					else if(screenArg instanceof InventoryScreen handledScreen){
						if (searchBox.isFocused() && !searchBox.getText().isEmpty()) {
							String searchText = searchBox.getText().toLowerCase();
							System.out.println(searchText);

							Map<Slot, SlotViewWrapper> views = new HashMap<>();
							for (Slot slot : handledScreen.getScreenHandler().slots) {
								ItemStack stack = slot.getStack();
								if (stack.isEmpty()) continue;

								boolean matches = stack.getName().getString().toLowerCase().contains(searchText);

								if (!matches) {
									views.put(slot, new SlotViewWrapper(false));
								} else {
									views.put(slot, new SlotViewWrapper(true));
								}
							}

							// Call the method to draw overlays on non-matching slots
							drawSlotOverlay(handledScreen, views, context);
						}
					}
					else if(screenArg instanceof ShulkerBoxScreen handledScreen){
						if (searchBox.isFocused() && !searchBox.getText().isEmpty()) {
							String searchText = searchBox.getText().toLowerCase();
							System.out.println(searchText);

							Map<Slot, SlotViewWrapper> views = new HashMap<>();
							for (Slot slot : handledScreen.getScreenHandler().slots) {
								ItemStack stack = slot.getStack();
								if (stack.isEmpty()) continue;

								boolean matches = stack.getName().getString().toLowerCase().contains(searchText);

								if (!matches) {
									views.put(slot, new SlotViewWrapper(false));
								} else {
									views.put(slot, new SlotViewWrapper(true));
								}
							}

							drawSlotOverlay(handledScreen, views, context);
						}
					}
				});
			}
		});
	}

	private void drawSlotOverlay(Object gui, Map<Slot, SlotViewWrapper> views, DrawContext context) {
		if(gui instanceof InventoryScreen || gui instanceof GenericContainerScreen || gui instanceof ShulkerBoxScreen){
			RenderSystem.enableBlend();

			for (Map.Entry<Slot, SlotViewWrapper> entry : views.entrySet()) {
				if (entry.getValue().isEnableOverlay()) {
					Slot slot = entry.getKey();
					ItemStack stack = slot.getStack();
					int x = slot.x + ((HandledScreenAccessor) gui).getX();
					int y = slot.y + ((HandledScreenAccessor) gui).getY();

					context.fill(x, y, x + 16, y + 16, DARKENING_COLOR);
				}
			}

			RenderSystem.disableBlend();
		}
	}

	public static class SlotViewWrapper {
		private final boolean enableOverlay;

		public SlotViewWrapper(boolean enableOverlay) {
			this.enableOverlay = enableOverlay;
		}

		public boolean isEnableOverlay() {
			return enableOverlay;
		}
	}

}