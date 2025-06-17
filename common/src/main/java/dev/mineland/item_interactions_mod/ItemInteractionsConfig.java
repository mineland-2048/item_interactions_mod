package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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


    private static AnimTemplate currentAnimationSelected;
    public static Object getSetting(String setting) {
        return settingsMap.getOrDefault(setting, getDefaultSetting(setting));
    }

    public static void setSetting(String setting, Object value) {
        try {
            if (settingsMap.get(setting) == null) {
                ItemInteractionsMod.errorMessage(String.format("Tried setting %s to %s but it doesn't exist", setting, value));
            }
            if (value == null) {
                ItemInteractionsMod.errorMessage("Tried setting '%s' to *null*", setting);
                return;
            }
            if (!value.getClass().equals(settingsMap.get(setting).getClass())) {
                ItemInteractionsMod.errorMessage(String.format("Failed to set %s (%s) to setting %s (%s)", value, value.getClass().getName(), setting, settingsMap.get(setting).getClass().getName()));
                return;
            }

            settingsMap.put(setting, value);

        } catch (Exception e) {
            MiscUtils.displayErrorInUi(String.format("setSetting(%s, %s): %s", setting, value, e));
        }
    }

    public static void setAnimationSetting(String id) {
        setSetting("animation", id);
        currentAnimationSelected = animations.getOrDefault(id, animations.get("speed"));
    }

    public static Object getDefaultSetting(String setting) {
        return defaultSettingsMap.get(setting);
    }

    private static List<AnimTemplate> animIndexes = new ArrayList<>();
    public static void addAnimation(AnimTemplate anim) {
        animationList.add(anim);
        defaultSettingsMap.putAll(anim.getSettingsList());
    }

    public static void refreshAnimList() {
        animations.clear();

        animationList.forEach(t -> {
            animations.put(t.getId(), t);
            settingsMap.putAll(t.getSettingsList());
        });

        animations.put("none", new AnimTemplate("none"));
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

        currentAnimationSelected = animations.get("speed");



//        animationConfig = DefaultValues.animationConfig;
//        scaleSpeed = DefaultValues.scaleSpeed;
//        scaleAmount = DefaultValues.scaleAmount;
//        mouseDeceleration = DefaultValues.mouseDeceleration;
//        mouseSpeedMult = DefaultValues.mouseSpeedMult;
//
//        enableGuiParticles = DefaultValues.enableGuiParticles;
    }

    public static AnimTemplate getAnimationSetting() {
        return currentAnimationSelected;
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
                    ItemInteractionsMod.infoMessage("Skipping line " + (lineCount+1) + ": `" + line + "`. Contains " + equalCount + " `=``");
                    continue;
                }
                line = line.trim();

                int equalIndex = line.indexOf("=");

                String arg = line.substring(0, equalIndex).trim();
                String value = line.substring(equalIndex+1).trim();


                if (settingsMap.containsKey(arg)) {

                    var og = settingsMap.get(arg);


                    if (MiscUtils.isNumber(value)) {
                        double a = Double.parseDouble(value);
                        settingsMap.put(arg, a);
                    }
                    else if (MiscUtils.isBoolean(value))  {
                        boolean a = Boolean.parseBoolean(value);
                        settingsMap.put(arg, a);
                    }

                    else if (MiscUtils.isVector(value)) {
                        Vector3f a = MiscUtils.parseVector3f(value);
                        settingsMap.put(arg, a);
                    }
                    else { settingsMap.put(arg, value); }

                }

                lineCount++;
            }

            if (!settingsMap.containsKey("animation") || settingsMap.get("animation") == null || !animations.containsKey((String) settingsMap.get("animation"))) {
                settingsMap.put("animation", defaultAnimations.get("speed"));
            }


            writeConfig(configFile);
        } catch (IOException e) {
            ItemInteractionsMod.warnMessage("Failed to refresh the config! \n" + e.getMessage());
            ItemInteractionsMod.warnMessage("Using the defaults");
            init();
        }

        setValuesAfterRefresh();
        getAnimationSetting().refreshSettings();



    }



    private static void setValuesAfterRefresh() {
        mouseDeceleration = (double) getSetting("mouse_deceleration");
        mouseSpeedMult = (double) getSetting("mouse_speed_multiplier");
        enableGuiParticles = (boolean) getSetting("gui_particles");
        debugDraws = (boolean) getSetting("debug");
        currentAnimationSelected = animations.getOrDefault((String) getSetting("animation"), animations.get("speed"));

    }
    private static void writeConfig(File configFile) throws IOException {

        FileWriter obj = new FileWriter(configFile);

        final StringBuilder configFileString = new StringBuilder();

        List<String> stringList = new ArrayList<>(20);


        SortedMap<String, Object> h = new TreeMap<>(settingsMap);



        h.forEach((k, v) -> {
            stringList.add(String.format("%s = %s%n", k, v));
        });

        for(String s : stringList) {
            configFileString.append(s);
        }

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

        obj.write(configFileString.toString());
        obj.flush();
    }

    public static void createConfig() {
        try {
            writeConfig(configPath.toFile());
        } catch (Exception e) {
            ItemInteractionsMod.warnMessage("Error writing config file! \n" + e);
        }
    }
}
