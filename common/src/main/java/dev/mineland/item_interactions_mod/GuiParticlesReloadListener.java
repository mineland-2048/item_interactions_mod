package dev.mineland.item_interactions_mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
//import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GuiParticlesReloadListener implements ResourceManagerReloadListener {


    private void parseParticle(JsonObject GuiParticleJson) {

    }

    private void loadParticles(ResourceManager resourceManager) {
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("particles/gui", resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {

            ResourceLocation id = entry.getKey();
            Resource resource = entry.getValue();

            try (InputStream stream = resource.open()) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

//                TODO: add logic
                parseParticle(json);



                Item_interactions_mod.infoMessage("Parsed particle: " + id);
            } catch (IOException | JsonParseException e) {
                Item_interactions_mod.warnMessage("Failed to load particle '" + id + "'\n" + e);

            }
        }

    }

    private void loadStuff(ResourceManager resourceManager) {
        Item_interactions_mod.infoMessage("Reloading all the things!");
        GuiParticleRegistry.clear();
        GuiSpawnerRegistry.clear();


        Item_interactions_mod.infoMessage("Reloading gui particles");
        loadParticles(resourceManager);
        Item_interactions_mod.infoMessage("Reloading gui spawners");
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("particles/gui_spawners", resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {

            ResourceLocation id = entry.getKey();
            Resource resource = entry.getValue();

            try (InputStream stream = resource.open()) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

//                TODO: add logic

                Item_interactions_mod.infoMessage("Parsed spawner: " + id);

            } catch (IOException | JsonParseException e) {
                Item_interactions_mod.warnMessage("Failed to load spawner '" + id + "'\n" + e);

            }
        }


    }
    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {

        CompletableFuture<Void> a = CompletableFuture.supplyAsync(() -> {

            this.loadStuff(resourceManager);
            return null;


        }, executor);

        return a.thenCompose(preparationBarrier::wait);

//        return ResourceManagerReloadListener.super.reload(preparationBarrier, resourceManager, executor, executor2);
    }

    @Override
    public String getName() {
        return ResourceManagerReloadListener.super.getName();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
