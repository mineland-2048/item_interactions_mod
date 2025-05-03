package dev.mineland.item_interactions_mod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
//import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
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

                parseParticle(json);



                Item_interactions_mod.infoMessage("Parsed particle: " + id);
            } catch (IOException | JsonParseException e) {
                Item_interactions_mod.warnMessage("Failed to load particle '" + id + "'\n" + e);

            }
        }

    }


    private GuiParticleSpawner parseSpawner(ResourceLocation id,ResourceManager resourceManager) {
        if (resourceManager.getResource(id).isEmpty()) {
            Item_interactions_mod.warnMessage("Parent '" + id + "' is empty!");
            return null;
        };

        try (InputStream stream = resourceManager.getResource(id).get().open()) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            return parseSpawner(json, id, resourceManager);
        } catch (JsonParseException | IOException e) {
            Item_interactions_mod.warnMessage("Couldnt parse parent '" + id + "'!\n" + e);
            return null;
        }
    }

    private GuiParticleSpawner parseSpawner(JsonObject SpawnerJson, ResourceLocation id, ResourceManager resourceManager) {

        GuiParticleSpawner result;
        DataResult<GuiParticleSpawner> dataResult;


        dataResult = GuiParticleSpawner.CODEC.parse(JsonOps.INSTANCE, SpawnerJson);
        result = dataResult.resultOrPartial(Item_interactions_mod::warnMessage).orElseThrow();
        result.setName(id);
        return result;

    }


    private double trySet(JsonObject json, String member, double defaultValue) {
        try {
            return json.get(member).getAsDouble();
        } catch(Exception ignore) {
            return defaultValue;
        }
    }

    private void loadSpawners(ResourceManager resourceManager) {
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("particles/gui_spawners", resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
            {


                ResourceLocation id = entry.getKey();
                Resource resource = entry.getValue();

                try (InputStream stream = resource.open()) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();


                    Item_interactions_mod.infoMessage("Parsing spawner: " + id);
                    GuiParticleSpawner a = parseSpawner(json, id, resourceManager);

                    SpawnerRegistry.register(a, id);

                    Item_interactions_mod.infoMessage("Parsed");

                } catch (Exception e) {
                    Item_interactions_mod.warnMessage("Failed to load spawner '" + id + "'" +
                            "\n" + e);

                }
            }
        }

    }

    private void loadStuff(ResourceManager resourceManager) {
        Item_interactions_mod.infoMessage("Reloading all the things!");
        SpawnerRegistry.clear();


        Item_interactions_mod.infoMessage("Reloading gui particles");
        loadParticles(resourceManager);
        Item_interactions_mod.infoMessage("Reloading gui spawners");
        loadSpawners(resourceManager);

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
