package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = Item_interactions_mod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ResourcePackRegistrar {

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
        ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(Item_interactions_mod.MOD_ID, "resourcepacks/example_gui_particles");

        event.addPackFinders(
                packLocation,
                PackType.CLIENT_RESOURCES,
                Component.literal("Example gui particle pack"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );

    }

}
