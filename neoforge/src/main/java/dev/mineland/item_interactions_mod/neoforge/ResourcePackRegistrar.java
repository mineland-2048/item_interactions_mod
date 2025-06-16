package dev.mineland.item_interactions_mod.neoforge;

import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = ItemInteractionsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ResourcePackRegistrar {

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(ItemInteractionsMod.MOD_ID, "resourcepacks/example_gui_particles");

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
