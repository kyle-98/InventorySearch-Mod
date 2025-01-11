package com.poggers.utils;

public class ColorUtils {
    public static int parseHexColor(String hexColor) {
        try {
            if(hexColor.startsWith("#")){
                hexColor = hexColor.substring(1);
                hexColor = "FF" + hexColor;
            }
            return (int) Long.parseLong(hexColor, 16);
        } catch (NumberFormatException nfe) {
            return 0xFFD98845;
        }
    } 
}
