package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import java.lang.Double;
import java.lang.Boolean;

public class ItemInteractionsConfig {
    private static final Path configPath = Path.of("config", "item_interactions.cfg");
    public static String animationConfig;

//    public static double scaleSpeed;
//    public static double scaleAmount;

    public static double mouseSpeedMult;
    public static double mouseDeceleration;

    public static boolean enableGuiParticles;
    public static boolean debugDraws;

    public static HashMap<String, Object> settingsMap = new HashMap<>();
    public static HashMap<String, Object> defaultSettingsMap = new HashMap<>();

    public static HashMap<String, AnimTemplate> animations = new HashMap<>();
    public static HashMap<String, AnimTemplate> defaultAnimations = new HashMap<>();

    public static List<AnimTemplate> animationList = new ArrayList<>();


    public static Object getSetting(String setting) {
        return settingsMap.getOrDefault(setting, getDefaultSetting(setting));
    }

    public static void setSetting(String setting, Object value) {
        if (value == null) {
            Item_interactions_mod.errorMessage("Tried setting '%s' to *null*", setting);
            return;
        }
        if (!value.getClass().equals(settingsMap.get(setting).getClass())) {
            Item_interactions_mod.errorMessage("Failed to set %s (%s) to setting %s (%s)", value, value.getClass().getName(), setting, settingsMap.get(setting).getClass().getName());
            return;
        }

        settingsMap.put(setting, value);
    }

    public static Object getDefaultSetting(String setting) {
        return defaultSettingsMap.get(setting);
    }

    public static void addAnimation(AnimTemplate anim) {
        animationList.add(anim);
        defaultSettingsMap.putAll(anim.getSettingsList());
    }

    public static void refreshAnimList() {
        animations.clear();

        animationList.forEach(t -> {
            animations.put(t.id, t);
            settingsMap.putAll(t.getSettingsList());
        });
        animations.put("none", null);
    }

    public static void init() {

        settingsMap.clear();
        refreshAnimList();

        settingsMap.put("gui_particles", true);
        settingsMap.put("debug", false);
        settingsMap.put("animation", "speed");
        defaultSettingsMap.put("gui_particles", true);
        defaultSettingsMap.put("debug", false);
        defaultSettingsMap.put("animation", "speed");




//        animationConfig = DefaultValues.animationConfig;
//        scaleSpeed = DefaultValues.scaleSpeed;
//        scaleAmount = DefaultValues.scaleAmount;
//        mouseDeceleration = DefaultValues.mouseDeceleration;
//        mouseSpeedMult = DefaultValues.mouseSpeedMult;
//
//        enableGuiParticles = DefaultValues.enableGuiParticles;
    }

    public static AnimTemplate getAnimationSetting() {
        return animations.getOrDefault((String) getSetting("animation"), animations.get("speed"));
    }


    public static void refreshConfig() {
        try {
            File configFile = configPath.toFile();
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            Scanner lector = new Scanner(configFile);

            int lineCount = 0;
            while (lector.hasNext() && lineCount < 20) {
                String line = lector.nextLine();
                int equalCount = MiscUtils.count(line, "=");
                if (equalCount != 1) {
                    Item_interactions_mod.infoMessage("Skipping line " + (lineCount+1) + ": `" + line + "`. Contains " + equalCount + " `=``");
                    continue;
                }
                line = line.trim();

                int equalIndex = line.indexOf("=");

                String arg = line.substring(0, equalIndex).trim();
                String value = line.substring(equalIndex+1).trim();


                if (settingsMap.containsKey(arg)) {

                    var og = settingsMap.get(arg);

                    System.out.printf("%s (%s): %s%n", arg, og != null ? og.getClass().getName() : "*null*", value);

                    if (MiscUtils.isNumber(value)) {
                        double a = Double.parseDouble(value);
                        settingsMap.put(arg, a);
                    }
                    else if (MiscUtils.isBoolean(value))  {
                        boolean a = Boolean.parseBoolean(value);
                        settingsMap.put(arg, a);
                    }
//                    else if (arg.equals("animation")) { settingsMap.put("animation", animations.getOrDefault(value, animations.get("speed"))); }
                    else { settingsMap.put(arg, value); }

                }

                lineCount++;
            }

            if (!settingsMap.containsKey("animation") || settingsMap.get("animation") == null || !animations.containsKey((String) settingsMap.get("animation"))) {
                settingsMap.put("animation", defaultAnimations.get("speed"));
            }


            mouseDeceleration = (double) settingsMap.getOrDefault("mouse_deceleration", 1.0);
            mouseSpeedMult = (double) settingsMap.getOrDefault("mouse_speed_multiplier", 1.0);
            enableGuiParticles = (boolean) settingsMap.getOrDefault("gui_particles", true);
            debugDraws = (boolean) settingsMap.getOrDefault("debug", false);

            writeConfig(configFile);

        } catch (IOException e) {
            Item_interactions_mod.warnMessage("Failed to refresh the config! \n" + e.getMessage());

            Item_interactions_mod.warnMessage("Using the defaults");
            init();
        }


    }

    private static void writeConfig(File configFile) throws IOException {

        FileWriter obj = new FileWriter(configFile);

        final String[] configFileString = {""};



        settingsMap.forEach((k, v) -> {
            configFileString[0] += String.format("%s = %s%n", k, v);
        });



//        String configFileString = String.format("""
//                animation = %s
//                scale_speed = %f
//                scale_amount = %f
//                deceleration = %f
//                mouse_speed_multiplier = %f
//                gui_particles = %s
//                debug = %s
//                """,
//                animationConfig,
//                scaleSpeed,
//                scaleAmount,
//                mouseDeceleration,
//                mouseSpeedMult,
//                enableGuiParticles ? "true": "false",
//                debugDraws ? "true" : "false"
//                );

        obj.write(configFileString[0]);
        obj.flush();
    }

    public static void createConfig() {
        try {
            writeConfig(configPath.toFile());
        } catch (Exception e) {
            Item_interactions_mod.warnMessage("Error writing config file! \n" + e);
        }
    }
}
