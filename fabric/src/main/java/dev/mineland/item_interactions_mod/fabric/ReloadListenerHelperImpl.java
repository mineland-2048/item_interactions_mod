package dev.mineland.item_interactions_mod.fabric;

import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListenerHelperImpl {

    public static void registerReloadListener(ResourceManagerReloadListener listener) {

        IdentifiableResourceReloadListener idListener = new IdentifiableResourceReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return mainListener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }

            private final ResourceManagerReloadListener mainListener = listener;
            private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Item_interactions_mod.MOD_ID, "gui_particles");

            public ResourceLocation getFabricId() {
                return ID;
            }

//            @Override
//            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
//                return mainListener.reload(preparationBarrier, resourceManager, executor, executor2);
//            }
        };


        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(idListener);
    }
}
