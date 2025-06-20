package dev.mineland.item_interactions_mod;


import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimRope;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimScale;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpeed;
import dev.mineland.item_interactions_mod.itemcarriedalgs.AnimSpin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ItemInteractionsMod {
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
        ItemInteractionsConfig.addAnimation(new AnimRope());
        ItemInteractionsConfig.addAnimation(new AnimSpin());

    }

    public static void infoMessage(String message) {
        logger.info(message);
    }

    public static void warnMessage(String message, Object... args) {
        logger.warn(String.format(message, args));
        if (GlobalDirt.isReloadingResources) GlobalDirt.spawnerErrorCount++;
    }

    public static void errorMessage(String message, Object... args) {
        logger.error(String.format(message, args));
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
