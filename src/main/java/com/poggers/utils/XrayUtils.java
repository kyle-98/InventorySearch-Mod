package com.poggers.utils;

public class XrayUtils {
    private static boolean xrayState = false;

    public static boolean getXrayState() {
        return xrayState;
    }

    public static void setXrayState(boolean state) {
        ReloadWorld.reloadChunks();
        xrayState = state;
    }
}
