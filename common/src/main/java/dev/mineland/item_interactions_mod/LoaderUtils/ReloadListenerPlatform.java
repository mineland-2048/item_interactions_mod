package dev.mineland.item_interactions_mod.LoaderUtils;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public interface ReloadListenerPlatform {
    void registerReloadListener(ResourceManagerReloadListener listener);
}
