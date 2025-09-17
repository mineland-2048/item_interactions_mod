package dev.mineland.item_interactions_mod.forge;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Item_interactions_mod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ResourcePackRegistrar {

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;
        ResourceLocation packLocation = ResourceLocation.fromNamespaceAndPath(Item_interactions_mod.MOD_ID, "resourcepacks/example_gui_particles");

        addPackFinders(event,
                packLocation,
                PackType.CLIENT_RESOURCES,
                Component.literal("Example gui particle pack"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.TOP
        );

    }

    public static void addPackFinders(AddPackFindersEvent event, ResourceLocation packLocation, PackType packType, Component packNameDisplay, PackSource packSource, boolean alwaysActive, Pack.Position packPosition) {
        if (event.getPackType() != packType) return;

        IModInfo modInfo = ModList.get().getModContainerById(packLocation.getNamespace()).orElseThrow(() -> new IllegalArgumentException("Mod not found: " + packLocation.getNamespace())).getModInfo();
        var resourcePath = modInfo.getOwningFile().getFile().findResource(packLocation.getPath());

        var version = modInfo.getVersion();

        var packResourcesFactory = new Pack.ResourcesSupplier() {
            @Override
            public @NotNull PackResources open(String name) {
                return new PathPackResources(name, resourcePath, false); // 'false' = not for metadata only
            }
        };

        var pack = Pack.readMetaAndCreate(
                "mod/" + packLocation, // id
                packNameDisplay,       // title
                true,                  // required metadata
                packResourcesFactory,
                packType,
                packPosition,
                alwaysActive ? PackSource.BUILT_IN : packSource
        );

        if (pack != null) {
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }    }

}



