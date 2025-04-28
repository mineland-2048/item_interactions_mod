package dev.mineland.item_interactions_mod;

import dev.architectury.injectables.annotations.ExpectPlatform;
//import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ReloadListenerHelper {
    @ExpectPlatform
    public static void registerReloadListener(ResourceManagerReloadListener listener) {
        throw new AssertionError();
    }
}
