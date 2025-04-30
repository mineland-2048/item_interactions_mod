package dev.mineland.item_interactions_mod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
//import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.Spawner;
import net.minecraft.client.renderer.item.properties.conditional.ComponentMatches;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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


    private Spawner parseSpawner(JsonObject SpawnerJson, ResourceLocation id, ResourceManager resourceManager) {
//        SpawnerJsonObject parsed;
//        String parentId = SpawnerJson.get("parent").getAsString();
//        ResourceLocation parentLocation = ResourceLocation.parse(parentId);
//
//        List<SpawnerJsonObject.SpawnerItem> appliedItems = new ArrayList<>();
//
//        if (GuiSpawnerRegistry.getObject(parentLocation) != null) {
//            parsed = GuiSpawnerRegistry.getObject(parentLocation).copy();
//        }
//        else if (resourceManager.getResource(parentLocation).isPresent()) {
//
//
//            try {
//                Resource resource = resourceManager.getResourceOrThrow(parentLocation);
//                InputStream stream = resource.open();
//
//                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
//
//                parsed = parseSpawner(json, parentLocation, resourceManager);
//
//                Item_interactions_mod.infoMessage("Parsed spawner parent: " + resource.toString());
//
//
//            } catch (IOException | JsonParseException e ) {
//                Item_interactions_mod.warnMessage("Failed to load spawner parent\n" + e);
//
//            }
//
//
//            JsonObject applyto = SpawnerJson.get("applyTo").getAsJsonObject();
//
//
//            if (applyto.isJsonArray()) {
//                for (JsonElement entry : applyto.getAsJsonArray()) {
//                    JsonObject a = entry.getAsJsonObject();
//
//                    SpawnerJsonObject.SpawnerItem newItem;
//                    ResourceLocation itemResourceLocation;
//                    DataComponentMap includeComponents;
//                    DataComponentMap excludeComponent;
//
//
//
//                    if (a.isJsonObject()) {
//
//                        itemResourceLocation = ResourceLocation.parse(a.get("id").toString());
//
//                        JsonElement includeComponentsJson = a.get("hasComponents");
//
//                        if (includeComponentsJson.isJsonObject()) {
//                            JsonObject include = includeComponentsJson.getAsJsonObject();
//
//
//
//                        }
//
//
//                    }
//
//                    ResourceLocation itemString;
//
//                    try {
//                        itemString = ResourceLocation.parse(a.getAsString());
//
//
//
//
//                    } catch (Exception h) {
//                        System.out.println("Error itemString: " + h);
//                    }
//
//
//                };
//            }
//
//            Spawner spawner = new Spawner(id.toString());
//
//            double speedX =         trySet(SpawnerJson, "speedX", 0.0);
//            double speedY =         trySet(SpawnerJson, "speedY", 0.0);
//            double accelerationX =  trySet(SpawnerJson, "accelerationX", 0.0);
//            double accelerationY =  trySet(SpawnerJson, "accelerationY", 0.0);
//            double frictionX =      trySet(SpawnerJson, "frictionX", 0.0);
//            double frictionY =      trySet(SpawnerJson, "frictionY", 0.0);
//            double rX =             trySet(SpawnerJson, "rX", 0.0);
//            double rY =             trySet(SpawnerJson, "rY", 0.0);
//            double rSpeedX =        trySet(SpawnerJson, "rSpeedX", 0.0);
//            double rSpeedY =        trySet(SpawnerJson, "rSpeedY", 0.0);
//            double lifeDuration =   trySet(SpawnerJson, "lifeDuration", 1.0);
//            double rLifeDuration =  trySet(SpawnerJson, "rLifeDuration", 0.0);
//
//
//
//            spawner.setAll(
//                    speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, rX, rY, rSpeedX, rSpeedY, lifeDuration, rLifeDuration
//            );
//            parsed = new SpawnerJsonObject(spawner);
//
//        }

        Spawner result;

        DataResult<Spawner> dataResult;

        dataResult = Spawner.CODEC.parse(JsonOps.INSTANCE, SpawnerJson);

        result = dataResult.resultOrPartial(Item_interactions_mod::warnMessage).orElseThrow();

//        if SpawnerJson.get("parent")

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

            ResourceLocation id = entry.getKey();
            Resource resource = entry.getValue();

            try (InputStream stream = resource.open()) {
                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();


                Spawner a = parseSpawner(json, id, resourceManager);

                SpawnerRegistry.register(a, id);

                Item_interactions_mod.infoMessage("Parsed spawner: " + id);

            } catch (IOException | JsonParseException e) {
                Item_interactions_mod.warnMessage("Failed to load spawner '" + id + "'\n" + e);

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
