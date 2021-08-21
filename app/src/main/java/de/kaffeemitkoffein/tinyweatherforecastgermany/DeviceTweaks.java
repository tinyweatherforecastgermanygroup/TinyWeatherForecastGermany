package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.os.Build;

public class DeviceTweaks {
    public static boolean isDarkTextOnOverflowMenuDevice(){
        if ((Build.HARDWARE.equals("qcom")) && (Build.PRODUCT.equals("G8441"))){
            return true;
        }
        return false;
    }
}
