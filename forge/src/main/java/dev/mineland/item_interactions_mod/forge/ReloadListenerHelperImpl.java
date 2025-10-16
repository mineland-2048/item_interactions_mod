package dev.mineland.item_interactions_mod.forge;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Item_interactions_mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ReloadListenerHelperImpl {

    private static ResourceManagerReloadListener listener;
    @SubscribeEvent
    public static void onReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(listener);
    }

    public static void registerReloadListener(ResourceManagerReloadListener l) {
        listener = l;
    }
}
