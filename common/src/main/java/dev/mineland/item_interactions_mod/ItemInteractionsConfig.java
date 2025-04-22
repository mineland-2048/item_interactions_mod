package dev.mineland.item_interactions_mod;

//import dev.architectury.transformer.shadowed.impl.com.google.gson.GsonBuilder;
import dev.mineland.item_interactions_mod.Item_interactions_mod.animation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

//import static dev.mineland.item_interactions_mod.GlobalDirt.deceleration;

public class ItemInteractionsConfig {
    private static final Path configPath = Path.of("config", "item_interactions.cfg");
    public static animation animationConfig;

    public static double scaleSpeed;
    public static double scaleAmount;

    public static double mouseSpeedMult;
    public static double mouseDeceleration;

    public static boolean debugDraws;


    /*  Default settings:
            animation = speed
            scale_speed = 4
            scale_amount = 0.1
    */
    /*
          TODO: Fix config screen items wrong z plane
    */
    public static void init() {
        animationConfig = DefaultValues.animationConfig;
        scaleSpeed = DefaultValues.scaleSpeed;
        scaleAmount = DefaultValues.scaleAmount;
        mouseDeceleration = DefaultValues.mouseDeceleration;
        mouseSpeedMult = DefaultValues.mouseSpeedMult;
    }

    static class DefaultValues {
        public static final animation animationConfig = animation.ANIM_SPEED;
        public static final double scaleSpeed = 1;
        public static final double scaleAmount = 0.1;
        public static final double mouseDeceleration = 1;
        public static final double mouseSpeedMult = 1;

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

//                Item_interactions_mod.infoMessage("`" + arg + "` = `" + value + "` || '" + line + "'");

                switch (arg) {
                    case "animation":
                        switch (value) {
                            case "scale":
                                animationConfig = animation.ANIM_SCALE;
                                break;

                            case "speed":
                                animationConfig = animation.ANIM_SPEED;
                                break;

                            case "none":
                                animationConfig = animation.NONE;
                                break;

                            default:
                                Item_interactions_mod.warnMessage("Unknown animation setting. Using Default (speed)");
                                animationConfig = DefaultValues.animationConfig;
                                break;
                        }
                        break;

                    case "scale_speed":
                        try {
                            scaleSpeed = Float.parseFloat(value);
                        } catch (Exception e) {
                            Item_interactions_mod.warnMessage("Error parsing scale speed. Using default\n" + e.getMessage());
                            scaleSpeed = DefaultValues.scaleSpeed;
                        }
                        break;

                    case "scale_amount":
                        try {
                            scaleAmount = Float.parseFloat(value);
                        } catch (Exception e) {
                            Item_interactions_mod.warnMessage("Error parsing scale amount. Using default\n" + e.getMessage());
                            scaleAmount = DefaultValues.scaleAmount;
                        }
                        break;

                    case "deceleration":
                        try {
                            mouseDeceleration = Float.parseFloat(value);
                        } catch (Exception e) {
                            Item_interactions_mod.warnMessage("Error parsing deceleration. Using default\n" + e.getMessage());
                            mouseDeceleration = DefaultValues.mouseDeceleration;
                        }
                        break;

                    case "mouse_speed_multiplier":
                        try {
                            mouseSpeedMult = Float.parseFloat(value);
                        } catch (Exception e) {
                            Item_interactions_mod.warnMessage("Error parsing mouse speed multiplier. Using default\n" + e.getMessage());
                            mouseSpeedMult = DefaultValues.mouseSpeedMult;
                        }
                        break;

                    case "debug":
                        if (value.equals("true")) ItemInteractionsConfig.debugDraws = true;
                        if (value.equals("false")) ItemInteractionsConfig.debugDraws = false;
                        break;

                    default:
                        Item_interactions_mod.infoMessage("Ignoring line " + (lineCount+1) + ". Unknown setting `" + arg + "`.");
                        break;
                }
                lineCount++;

//                Item_interactions_mod.infoMessage(arg + ", " + value);
            }


            if (animationConfig == null) {
                animationConfig = DefaultValues.animationConfig;
                Item_interactions_mod.infoMessage("Defaulting to animation: speed");
            }

//            Item_interactions_mod.infoMessage("Loop count: " + lineCount);


            writeConfig(configFile);

//            Item_interactions_mod.infoMessage("Final config file: \n" + configFileString);
//            Item_interactions_mod.infoMessage("Configuration loaded! File has been sanitized");

        } catch (IOException e) {
            Item_interactions_mod.warnMessage("Failed to refresh the config! \n" + e.getMessage());

            Item_interactions_mod.warnMessage("Using the defaults");
            init();
        }


    }

    private static void writeConfig(File configFile) throws IOException {

        FileWriter obj = new FileWriter(configFile);

        String configFileString = String.format("""
                animation = %s
                scale_speed = %f
                scale_amount = %f
                deceleration = %f
                mouse_speed_multiplier = %f
                debug = %s
                """,
                animationConfig.name,
                scaleSpeed,
                scaleAmount,
                mouseDeceleration,
                mouseSpeedMult,
                debugDraws ? "true" : "false"
                );

        obj.write(configFileString);
        obj.flush();
    }

    public static void createConfig() {
        try {
            writeConfig(configPath.toFile());
        } catch (Exception e) {
            Item_interactions_mod.warnMessage("Error writing config file! \n" + e.toString());
        }
    }


}
