package dev.mineland.item_interactions_mod.fabric.client;

import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class Item_interactions_modFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ItemInteractionsMod.init();
        ItemInteractionsMod.LOADER = ItemInteractionsMod.LOADER_ENUM.FABRIC;

        FabricLoader.getInstance().getModContainer(ItemInteractionsMod.MOD_ID).ifPresent(container -> {
            ResourceLocation packId = ResourceLocation.fromNamespaceAndPath(ItemInteractionsMod.MOD_ID, "example_gui_particles");
            ResourceManagerHelper.registerBuiltinResourcePack(packId, container, Component.literal("Example gui particle pack"), ResourcePackActivationType.NORMAL);
        });
    }
}
