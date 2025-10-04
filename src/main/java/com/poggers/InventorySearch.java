package com.poggers;

import com.poggers.config.inventorysearch.ModConfig;
import com.poggers.mixin.ScreenAccessor;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class InventorySearch implements ClientModInitializer, ModMenuApi {
	public static TextFieldWidget searchBox;
	private static ConfigHolder<ModConfig> configHolder;
	private static String savedSearchText;
	

	public static ModConfig getConfig() {
		return configHolder.getConfig();
	}

	public static void saveConfig(){
		configHolder.save();
	}

	@Override
	public void onInitializeClient() {
		configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		
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
				if(savedSearchText != null){ 
					searchBox.setText(savedSearchText);
				}

				ButtonWidget clearSearchButton = ButtonWidget.builder(Text.literal("Clear Search"), button -> {
					searchBox.setText(""); 
					savedSearchText = "";
				})
					.position(w - 120, h - 70)
					.size(100, 20)
					.build();

				((ScreenAccessor) screen).invokeAddDrawableChild(searchBox);

				((ScreenAccessor) screen).invokeAddDrawableChild(clearSearchButton);

				ScreenEvents.remove(screen).register((screenArg) -> {
					if(searchBox != null) {
						savedSearchText = searchBox.getText();
					}
				});
			}
		});
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