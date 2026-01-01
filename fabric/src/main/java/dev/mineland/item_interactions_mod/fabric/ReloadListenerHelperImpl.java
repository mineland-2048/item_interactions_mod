package dev.mineland.item_interactions_mod.fabric;

import dev.mineland.item_interactions_mod.ItemInteractionsMod;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ReloadListenerHelperImpl {

    public static void registerReloadListener(ResourceManagerReloadListener listener) {

        ResourceManagerReloadListener idListener = new ResourceManagerReloadListener() {
            @Override
            public @NotNull CompletableFuture<Void> reload(@NotNull SharedState sharedState, @NotNull Executor executor, @NotNull PreparationBarrier preparationBarrier, @NotNull Executor executor2) {
                return mainListener.reload(sharedState,executor,preparationBarrier,executor2);
            }

            @Override
            public void prepareSharedState(@NotNull SharedState sharedState) {
                ResourceManagerReloadListener.super.prepareSharedState(sharedState);
            }

            @Override
            public @NotNull String getName() {
                return ResourceManagerReloadListener.super.getName();
            }

            @Override
            public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {

            }

            private final ResourceManagerReloadListener mainListener = listener;


//            @Override
//            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {
//                return mainListener.reload(preparationBarrier, resourceManager, executor, executor2);
//            }
        };


        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(Identifier.fromNamespaceAndPath(ItemInteractionsMod.MOD_ID, "gui_particles"), idListener);
    }
}
