package dev.mineland.item_interactions_mod;


import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimPhysics;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimScale;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Item_interactions_mod {
    public static final String MOD_ID = "item_interactions_mod";
    final static Logger logger = LoggerFactory.getLogger(MOD_ID);

    public enum LOADER_ENUM {
        FABRIC,
        NEOFORGE,
        UNKNOWN
    }

    public static LOADER_ENUM LOADER;



    public static void init() {
        logger.info("Initializing item interactions mod!");
        LOADER = LOADER_ENUM.UNKNOWN;

        registerAnimations();
        ItemInteractionsConfig.init();
        ItemInteractionsConfig.refreshConfig();
        ReloadListenerHelper.registerReloadListener(new GuiParticlesReloadListener());
    }

    static void registerAnimations() {
        ItemInteractionsConfig.addAnimation(new AnimSpeed());
        ItemInteractionsConfig.addAnimation(new AnimScale());
        ItemInteractionsConfig.addAnimation(new AnimPhysics());

    }

    public static void infoMessage(String message) {
        logger.info(message);
    }

    public static void warnMessage(String message) {
        logger.warn(message);
        if (GlobalDirt.isReloadingResources) GlobalDirt.spawnerErrorCount++;
    }

    public static void errorMessage(String message) {
        logger.error(message);
        if (GlobalDirt.isReloadingResources) GlobalDirt.spawnerErrorCount++;
    }


    public static void refreshConfig() {
        logger.info("Refreshing config");
        ItemInteractionsConfig.refreshConfig();
    }


    public static boolean isFabric() {
        return LOADER == LOADER_ENUM.FABRIC;
    }

    public static boolean isNeo() {
        return LOADER == LOADER_ENUM.NEOFORGE;
    }
}
