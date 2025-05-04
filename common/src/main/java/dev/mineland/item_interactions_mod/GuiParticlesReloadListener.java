package dev.mineland.item_interactions_mod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
//import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
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


    private int errorCount;

//
//    private void parseParticle(JsonObject GuiParticleJson) {
//
//    }
//
//    private void loadParticles(ResourceManager resourceManager) {
//        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("particles/gui", resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
//
//            ResourceLocation id = entry.getKey();
//            Resource resource = entry.getValue();
//
//            try (InputStream stream = resource.open()) {
//                JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
//
//                parseParticle(json);
//
//
//
//                Item_interactions_mod.infoMessage("Parsed particle: " + id);
//            } catch (IOException | JsonParseException e) {
//                Item_interactions_mod.errorMessage("Failed to load particle '" + id + "'\n" + e);
//
//            }
//        }
//
//    }


    private GuiParticleSpawner parseSpawner(JsonObject SpawnerJson, ResourceLocation id, ResourceManager resourceManager) {

        GuiParticleSpawner result;
        DataResult<GuiParticleSpawner> dataResult;


        dataResult = GuiParticleSpawner.CODEC.parse(JsonOps.INSTANCE, SpawnerJson);

        result = dataResult.resultOrPartial((s) -> {
            Item_interactions_mod.warnMessage("Error in '" + id + "\n" + s);
            errorCount++;
        }).orElseThrow();
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
        errorCount = 0;
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("particles/gui_spawners", resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
            {


                ResourceLocation id = entry.getKey();
                Resource resource = entry.getValue();

                try (InputStream stream = resource.open()) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();


//                    Item_interactions_mod.infoMessage("Parsing spawner: " + id);
                    GuiParticleSpawner a = parseSpawner(json, id, resourceManager);

                    SpawnerRegistry.register(a, id);

//                    Item_interactions_mod.infoMessage("Parsed");

                } catch (Exception e) {
                    Item_interactions_mod.errorMessage("Couldn't parse '" + id + "': \n" + e.getCause());

                    errorCount++;

                }
            }
        }

    }

    private void loadStuff(ResourceManager resourceManager) {
        Item_interactions_mod.infoMessage("Reloading gui particle spawners");
        SpawnerRegistry.clear();

        loadSpawners(resourceManager);

        String spawnerString = SpawnerRegistry.SPAWNER_MAP.size() == 1 ?
                "Parsed %d Gui particle spawner": "Parsed %d Gui particle spawners";

        Item_interactions_mod.infoMessage(String.format(spawnerString, SpawnerRegistry.SPAWNER_MAP.size()));





    }
    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {

        CompletableFuture<Void> a = CompletableFuture.supplyAsync(() -> {

            this.loadStuff(resourceManager);

            if (errorCount > 0) {

                String errorTitle = (errorCount == 1) ? "%d Gui particle error" : "%d Gui particle errors";
                SystemToast.add(Minecraft.getInstance().getToastManager(), SystemToast.SystemToastId.PACK_LOAD_FAILURE,
                        Component.literal(String.format(errorTitle, errorCount)),
                        Component.literal("Check the logs for more information") );

            }
            return null;


        }, executor);

        return a.thenCompose(preparationBarrier::wait);

//        return ResourceManagerReloadListener.super.reload(preparationBarrier, resourceManager, executor, executor2);
    }

    @Override
    public @NotNull String getName() {
        return ResourceManagerReloadListener.super.getName();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
