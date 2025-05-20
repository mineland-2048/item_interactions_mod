package dev.mineland.item_interactions_mod.fabric;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class Item_interactions_modFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

//        Item_interactions_mod.init();
//
//        FabricLoader.getInstance().getModContainer(Item_interactions_mod.MOD_ID).ifPresent(container -> {
//            ResourceLocation packId = ResourceLocation.fromNamespaceAndPath(Item_interactions_mod.MOD_ID, "example_gui_particles");
//            ResourceManagerHelper.registerBuiltinResourcePack(packId, container, Component.literal("Example gui particle pack"), ResourcePackActivationType.NORMAL);
//        });
        // Run our common setup.
    }
}
