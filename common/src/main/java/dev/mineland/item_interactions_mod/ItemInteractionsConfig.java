package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimTemplate;
import org.jetbrains.annotations.NotNull;
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

    public static boolean enableGuiParticles;
    public static boolean smoothGuiParticles;

    public static boolean debugDraws;

    public static HashMap<String, Object> settingsMap = new HashMap<>();
    public static HashMap<String, Object> defaultSettingsMap = new HashMap<>();

    public static HashMap<String, AnimTemplate> animations = new HashMap<>();
    public static HashMap<String, AnimTemplate> defaultAnimations = new HashMap<>();

    public static List<AnimTemplate> animationList = new ArrayList<>();


    private static AnimTemplate currentAnimationSelected;
    public static Object getSetting(String setting) {
        if (settingsMap.get(setting) == null) return getDefaultSetting(setting);
        return settingsMap.getOrDefault(setting, getDefaultSetting(setting));
    }

    public static void setSetting(String setting, Object value) {
        try {
            if (defaultSettingsMap.get(setting) == null) {
                ItemInteractionsMod.errorMessage(String.format("Tried setting %s to %s but it doesn't exist", setting, value));
                return;
            }
            if (value == null) {
                ItemInteractionsMod.errorMessage("Tried setting '%s' to *null*", setting);
                return;
            }
            if (!value.getClass().equals(defaultSettingsMap.get(setting).getClass())) {
                ItemInteractionsMod.errorMessage(String.format("Failed to set %s (%s) to setting %s (%s)", value, value.getClass().getName(), setting, settingsMap.get(setting).getClass().getName()));
                return;
            }

            settingsMap.put(setting, value);

        } catch (Exception e) {
            MiscUtils.displayErrorInUi(String.format("setSetting(%s, %s): %s", setting, value, e));
        }
    }

    public static void resetSetting(String setting) {
        if (defaultSettingsMap.containsKey(setting)) {
            settingsMap.put(setting, defaultSettingsMap.get(setting));
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


    private static void putDefault(String setting, Object value) {
        settingsMap.put(setting, value);
        defaultSettingsMap.put(setting, value);

    }
    public static void init() {

        settingsMap.clear();
        refreshAnimList();

        putDefault("gui_particles", true);
        putDefault("debug", false);
        putDefault("animation", "speed");
        putDefault("gui_smooth_particles", false);

        currentAnimationSelected = animations.get("speed");

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
                    Object parsedValue;

                    if (arg.equals("animation")) {
                        if (animations.containsKey(value)) parsedValue = value;
                        else parsedValue = null;
                    }
                    else {
//                        System.out.print(arg + ": ");
                        parsedValue = parseConfigValue(value, og);
                    }

                    if (parsedValue == null) {
                        ItemInteractionsMod.warnMessage("Defaulting invalid setting: %s = %s", arg, value);
                        parsedValue = getDefaultSetting(arg);
                    };
                    settingsMap.put(arg, parsedValue);


                } else {
                    ItemInteractionsMod.warnMessage("Found unknown setting: %s = %s", arg, value);
                }

                lineCount++;
            }

            if (!settingsMap.containsKey("animation") || settingsMap.get("animation") == null || settingsMap.get("animation").equals("null") || !animations.containsKey((String) settingsMap.get("animation"))) {
                settingsMap.put("animation", defaultAnimations.get("speed"));
            }

//            ItemInteractionsMod.infoMessage("Setting config to: \n" + settingsMap.toString());

            writeConfig(configFile);
        } catch (Exception e) {
            ItemInteractionsMod.warnMessage("Failed to refresh the config! \n"
                    + e.getMessage()
                    + "Using the defaults"
            );

            init();
        }

        setValuesAfterRefresh();
        getAnimationSetting().refreshSettings();



    }

    private static Object parseConfigValue(String value, Object obj) {
        Object parsedValue = null;
        switch (obj) {
            case Integer i:
//                System.out.printf("Parsing %s: %s", "int", value);
                if (MiscUtils.isInt(value)) {
                    parsedValue = Integer.parseInt(value);
                }
                break;

            case Float f:
//                System.out.printf("Parsing %s: %s", "float", value);
                if (MiscUtils.isNumber(value)) {
                    parsedValue = Float.parseFloat(value);
//                    System.out.println(" **SUCCESS**");
                }
                break;

            case Double d:
//                System.out.printf("Parsing %s: %s", "double", value);
                if (MiscUtils.isNumber(value)) {
                    parsedValue = Double.parseDouble(value);
//                    System.out.println(" **SUCCESS**");

                }
                break;

            case Boolean b:
//                System.out.printf("Parsing %s: %s", "boolean", value);
                if (MiscUtils.isBoolean(value)) {
                    parsedValue = Boolean.parseBoolean(value);
//                    System.out.println(" **SUCCESS**");

                }
                break;

            case Vector3f vector3f:
//                System.out.printf("Parsing %s: %s", "Vector3f", value);
                if (MiscUtils.isVector(value)) {
                    parsedValue = MiscUtils.parseVector3f(value);
//                    System.out.println(" **SUCCESS**");
                }
                break;
            default:
//                System.out.printf("Found nothing for supposed %s: %s%n", obj.getClass(), value);
                break;
        }

        return parsedValue;
    }

    private static boolean IsCorrectClass(Object og, Object value) {

        boolean ret = og.getClass().equals(value.getClass());
        if (!ret) {
            ItemInteractionsMod.warnMessage(String.format("invalid value! using the default %n" +
                    "Og: %s%n" +
                    "Cf: %s", og.getClass(), value.getClass() ));

        }

        return ret;
    }


    private static void setValuesAfterRefresh() {
        enableGuiParticles = (boolean) getSetting("gui_particles");
        smoothGuiParticles = (boolean) getSetting("gui_smooth_particles");
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
