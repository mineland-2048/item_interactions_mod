package dev.mineland.item_interactions_mod.modcompat;

import dev.mineland.item_interactions_mod.ItemInteractionsMod;

public class ModCompat {
    private static boolean tinyItemAnimationsLoaded;

    public static boolean isTinyItemAnimationsLoaded() {
        return tinyItemAnimationsLoaded;
    }

    public static void setTinyItemAnimationsLoaded(boolean tinyItemAnimationsLoaded) {
        ModCompat.tinyItemAnimationsLoaded = tinyItemAnimationsLoaded;

        if (tinyItemAnimationsLoaded) {
            ItemInteractionsMod.infoMessage("Applying compat changes for Tiny item interactions");
        }
    }
}
