package com.poggers.config;

import java.util.ArrayList;
import java.util.List;

import com.poggers.utils.NotifyPlayer;
import com.poggers.utils.ReloadWorld;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.ColorPicker;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Config(name = "holo-utils")
public class ModConfig implements ConfigData {
    @Category("inventorySearchSettings")
    @TransitiveObject
    public InventorySearchSettings iSSettings = new InventorySearchSettings();

    public static class InventorySearchSettings{
        @ColorPicker
        private String HIGHLIGHT_COLOR = "#D98845";

        private boolean STAY_ENABLED = true;

        public String getHighlightColor() {
            return HIGHLIGHT_COLOR;
        }

        public void setHighlightColor(String hc) {
            this.HIGHLIGHT_COLOR = hc;
        }

        public boolean getEnabledState() {
            return STAY_ENABLED;
        }

        public void setEnabledState(boolean es) {
            this.STAY_ENABLED = es;
        }
    }

    public enum FogRemoval {
        DISABLED,
        EVERYWHERE,
        NETHER_ONLY;
    }

    @Category("visualSettings")
    @TransitiveObject
    public static VisualSettings visualSettings = new VisualSettings();    
    public static class VisualSettings {
        @ConfigEntry.Gui.EnumHandler(option= ConfigEntry.Gui.EnumHandler.EnumDisplayOption.DROPDOWN)
        private FogRemoval removeFogEverywhere = FogRemoval.DISABLED;

        public FogRemoval getAllFogState() {
            return this.removeFogEverywhere;
        }

        public void setAllFogState(FogRemoval state) {
            this.removeFogEverywhere = state;
        }
    }

    @Category("xray")
    @TransitiveObject
    public static XraySettings xraySettings = new XraySettings();
    public static class XraySettings {
        @ConfigEntry.Gui.Tooltip
        private List<String> stringBlocks = new ArrayList<String>() {{
            add("COAL_ORE");
            add("DEEPSLATE_COAL_ORE");
            add("IRON_ORE");
            add("DEEPSLATE_IRON_ORE");
            add("COPPER_ORE");
            add("DEEPSLATE_COPPER_ORE");
            add("GOLD_ORE");
            add("DEEPSLATE_GOLD_ORE");
            add("REDSTONE_ORE");
            add("DEEPSLATE_REDSTONE_ORE");
            add("EMERALD_ORE");
            add("DEEPSLATE_EMERALD_ORE");
            add("LAPIS_ORE");
            add("DEEPSLATE_LAPIS_ORE");
            add("DIAMOND_ORE");
            add("DEEPSLATE_DIAMOND_ORE");
            add("CHEST");
            add("ENDER_CHEST");
            add("SHULKER_BOX");
            add("CRAFTING_TABLE");
            add("FURNACE");
            add("REDSTONE");
            add("HOPPER");
            add("BARREL");
            add("TNT");
            add("PISTON");
            add("STICKY_PISTON");
            add("RAIL");
        }};

        public List<Block> getXrayBlocks() {
            List<Block> blocksToRender = new ArrayList<>();
            for(String block : stringBlocks){
                String blockNameLower = block.toLowerCase();
                try{
                    Identifier blockId = Identifier.of("minecraft", blockNameLower);
                    blocksToRender.add(Registries.BLOCK.get(blockId));
                } catch(Exception ex) {
                    continue;
                }
            }
            return blocksToRender;
        }
    }

    public static void cycleFogOptions(){
        switch(ModConfig.visualSettings.getAllFogState()) {
            case DISABLED:
                NotifyPlayer.displayMessage("Disabling Fog Everywhere", true);
                ModConfig.visualSettings.setAllFogState(FogRemoval.EVERYWHERE);
                break;
            case EVERYWHERE:
                NotifyPlayer.displayMessage("Disabling Fog in Nether Only", true);
                ModConfig.visualSettings.setAllFogState(FogRemoval.NETHER_ONLY);
                break;
            case NETHER_ONLY:
                NotifyPlayer.displayMessage("Enabling Fog", true);
                ModConfig.visualSettings.setAllFogState(FogRemoval.DISABLED);
                break;
        }
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
}
