package dev.mineland.item_interactions_mod.fabric.client;

import dev.mineland.item_interactions_mod.GuiParticlesReloadListener;
import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import dev.mineland.item_interactions_mod.fabric.ReloadListenerHelperImpl;
import dev.mineland.item_interactions_mod.modcompat.ModCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class Item_interactions_modFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ItemInteractionsMod.init();
        ItemInteractionsMod.LOADER = ItemInteractionsMod.LOADER_ENUM.FABRIC;
        ReloadListenerHelperImpl.registerReloadListener(new GuiParticlesReloadListener());

        FabricLoader.getInstance().getModContainer(ItemInteractionsMod.MOD_ID).ifPresent(container -> {
            Identifier packId = Identifier.fromNamespaceAndPath(ItemInteractionsMod.MOD_ID, "example_gui_particles");
            ResourceLoader.registerBuiltinPack(packId, container, Component.literal("Example gui particle pack"), PackActivationType.NORMAL);
        });

        ModCompat.setTinyItemAnimationsLoaded(FabricLoader.getInstance().isModLoaded("tia"));

    }
}
