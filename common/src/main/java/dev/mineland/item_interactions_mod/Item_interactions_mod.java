package dev.mineland.item_interactions_mod;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Item_interactions_mod {
    public static final String MOD_ID = "item_interactions_mod";
    final static Logger logger = LoggerFactory.getLogger(MOD_ID);
    //    public static List<Particle> UIParticles;




    public static void init() {
        logger.info("Initializing item interactions mod!");

        ItemInteractionsConfig.init();
        ItemInteractionsConfig.refreshConfig();
//        ItemInteractionsResources.onReload();

        ReloadListenerHelper.registerReloadListener(new GuiParticlesReloadListener());



//        animationSetting = ItemInteractionsConfig.animationConfig;

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


}
