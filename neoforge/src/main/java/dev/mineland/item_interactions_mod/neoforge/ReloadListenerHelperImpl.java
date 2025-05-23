package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@EventBusSubscriber(modid = Item_interactions_mod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ReloadListenerHelperImpl {

    private static ResourceManagerReloadListener listener;
    @SubscribeEvent
    public static void onReloadListener(AddClientReloadListenersEvent event) {
        event.addListener(ResourceLocation.fromNamespaceAndPath(Item_interactions_mod.MOD_ID, "gui_particles"), listener);
    }

    public static void registerReloadListener(ResourceManagerReloadListener l) {
        listener = l;
    }
}
