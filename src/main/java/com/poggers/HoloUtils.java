package com.poggers;

import com.poggers.utils.ColorUtils;
import com.poggers.utils.NotifyPlayer;
import com.poggers.utils.XrayUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.poggers.config.ModConfig;
import com.poggers.config.ModConfig.FogRemoval;
import com.poggers.mixin.HandledScreenAccessor;
import com.poggers.mixin.ScreenAccessor;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

public class HoloUtils implements ClientModInitializer, ModMenuApi {
	public static TextFieldWidget searchBox;
	private static ConfigHolder<ModConfig> configHolder;
	private ModConfig config;
	private static String savedSearchText;

	private static boolean isFullbrightEnabled = false;
	public static boolean isEspEnabled = false;

	//keybindings
	public static KeyBinding CYCLE_FOG_KEYBIND;
	public static KeyBinding FULLBRIGHT_KEYBIND;
	public static KeyBinding XRAY_KEYBIND;
	public static KeyBinding ESP_KEYBIND;

	public static ModConfig getConfig() {
		return configHolder.getConfig();
	}

	public static void saveConfig(){
		configHolder.save();
	}

	@Override
	public void onInitializeClient() {

		CYCLE_FOG_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.holo-utils.cycleFog",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_U,
			"category.holo-utils"
		));

		FULLBRIGHT_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.holo-utils.fullBright",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_N,
			"category.holo-utils"
		));

		XRAY_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.holo-utils.xray",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_X,
			"category.holo-utils"
		));

		ESP_KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.holo-utils.esp",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_Y,
			"category.holo-utils"
		));

		configHolder = AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
		config = getConfig();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(CYCLE_FOG_KEYBIND.wasPressed()) {
				ModConfig.cycleFogOptions(); 
			}

			while(FULLBRIGHT_KEYBIND.wasPressed()){ 
				SimpleOption<Double> gamma = MinecraftClient.getInstance().options.getGamma();
				if(!isFullbrightEnabled) {
					gamma.setValue(15.0);
					NotifyPlayer.displayMessage("Fullbright ON", true);
				}
				else {
					gamma.setValue(1.0);
					NotifyPlayer.displayMessage("Fullbright OFF", true);
				}
				isFullbrightEnabled = !isFullbrightEnabled;
			}

			while(XRAY_KEYBIND.wasPressed()) {
				SimpleOption<Double> gamma = MinecraftClient.getInstance().options.getGamma();
				if(!XrayUtils.getXrayState()) {
					NotifyPlayer.displayMessage("Xray ON", true);
					isFullbrightEnabled = true;
					gamma.setValue(15.0);
					ModConfig.visualSettings.setAllFogState(FogRemoval.EVERYWHERE);
				} else {
					NotifyPlayer.displayMessage("Xray OFF", true);
					isFullbrightEnabled = false;
					gamma.setValue(1.0);
					ModConfig.visualSettings.setAllFogState(FogRemoval.DISABLED);
				}
				XrayUtils.setXrayState(!XrayUtils.getXrayState());
			}

			while(HoloUtils.ESP_KEYBIND.wasPressed()){
				if (isEspEnabled) {
					MinecraftClient ci = MinecraftClient.getInstance();
					List<AbstractClientPlayerEntity> players = ci.world.getPlayers();
					for (AbstractClientPlayerEntity pe : players) {
						System.out.println("running");
						pe.setGlowing(true);
					}
					
					for (PlayerEntity pe : players) {
						System.out.println("fjkhsdjklhfsdjkf" + pe.getName() + ": " + pe.isGlowing());
					}
				} else {
					Iterable<Entity> worldEntities = ((ClientWorld) MinecraftClient.getInstance().world).getEntities();
					for (Entity e : worldEntities) {
						if (e instanceof OtherClientPlayerEntity) {
							e.setGlowing(false);
						}
					}
				}
				isEspEnabled = !isEspEnabled; 
			}

        });

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

				ScreenEvents.afterRender(screen).register((screenArg, context, mouseX, mouseY, delta) -> {

					if(screenArg instanceof GenericContainerScreen || screenArg instanceof InventoryScreen || screenArg instanceof ShulkerBoxScreen){
						if(config.iSSettings.getEnabledState()){
							if (!searchBox.getText().isEmpty()) {
								String searchText = searchBox.getText().toLowerCase();
								System.out.println(searchText);
	
								Map<Slot, SlotViewWrapper> views = new HashMap<>();
								for (Slot slot : ((HandledScreen<?>) screenArg).getScreenHandler().slots) {
									ItemStack stack = slot.getStack();
									if (stack.isEmpty()) continue;
	
									boolean matches = stack.getName().getString().toLowerCase().contains(searchText);
	
									if (!matches) {
										views.put(slot, new SlotViewWrapper(false));
									} else {
										views.put(slot, new SlotViewWrapper(true));
									}
								}
	
								drawSlotOverlay(screenArg, views, context);
							}
						}
						else {
							if (searchBox.isFocused() && !searchBox.getText().isEmpty()) {
								String searchText = searchBox.getText().toLowerCase();
								System.out.println(searchText);
	
								Map<Slot, SlotViewWrapper> views = new HashMap<>();
								for (Slot slot : ((HandledScreen<?>) screenArg).getScreenHandler().slots) {
									ItemStack stack = slot.getStack();
									if (stack.isEmpty()) continue;
	
									boolean matches = stack.getName().getString().toLowerCase().contains(searchText);
	
									if (!matches) {
										views.put(slot, new SlotViewWrapper(false));
									} else {
										views.put(slot, new SlotViewWrapper(true));
									}
								}
	
								drawSlotOverlay(screenArg, views, context);
							}
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
					int x = slot.x + ((HandledScreenAccessor) gui).getX();
					int y = slot.y + ((HandledScreenAccessor) gui).getY();

					
					context.fill(x, y, x + 16, y + 16, ColorUtils.parseHexColor(config.iSSettings.getHighlightColor()));
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