package com.poggers;

import com.poggers.config.inventorysearch.ModConfig;
import com.poggers.mixin.ScreenAccessor;

import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class InventorySearch implements ClientModInitializer, ModMenuApi {
	public static EditBox searchBox;
	private static ConfigHolder<ModConfig> configHolder;
	private static String savedSearchText;
	public ModConfig config;
	

	public static ModConfig getConfig() {
		return configHolder.getConfig();
	}

	public static void saveConfig(){
		configHolder.save();
	}

	@Override
	public void onInitializeClient() {
		configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = getConfig();
		
		ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
			if(screen instanceof ContainerScreen || screen instanceof InventoryScreen || screen instanceof ShulkerBoxScreen){
				searchBox = new EditBox(
						client.font,
						w - 120,
						h - 40,
						100,
						20,
						Component.literal("Search...")
				);

				searchBox.setHint(Component.literal("Search..."));
				if(savedSearchText != null && config.iSSettings.getEnabledState()){ 
					searchBox.setValue(savedSearchText);
				}
				

				Button clearSearchButton = Button.builder(Component.literal("Clear Search"), button -> {
					searchBox.setValue(""); 
					savedSearchText = "";
				})
					.pos(w - 120, h - 70)
					.size(100, 20)
					.build();

				((ScreenAccessor) screen).invokeAddRenderableWidget(searchBox);

				((ScreenAccessor) screen).invokeAddRenderableWidget(clearSearchButton);

				ScreenEvents.remove(screen).register((screenArg) -> {
					if(searchBox != null) {
						savedSearchText = searchBox.getValue();
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