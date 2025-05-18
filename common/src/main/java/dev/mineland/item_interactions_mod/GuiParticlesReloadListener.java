package dev.mineland.item_interactions_mod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.mineland.item_interactions_mod.CarriedInteractions.Spawners.GuiParticleSpawner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static dev.mineland.item_interactions_mod.GlobalDirt.*;

public class GuiParticlesReloadListener implements ResourceManagerReloadListener {





    private GuiParticleSpawner parseSpawner(JsonObject SpawnerJson, ResourceLocation id, ResourceManager resourceManager) {

        GuiParticleSpawner result;
        DataResult<GuiParticleSpawner> dataResult;

        currentParticleSpawner = id.toString();
        ResourceLocation filePath = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "gui_particle_spawners/" + id.getPath());


        dataResult = GuiParticleSpawner.CODEC.parse(JsonOps.INSTANCE, SpawnerJson);

        result = dataResult.resultOrPartial((s) -> {
            if (!spawnerErrorList.containsKey(filePath)) spawnerErrorList.put(filePath, new ArrayList<>());
            spawnerErrorList.get(filePath).add(s);
            Item_interactions_mod.warnMessage("Errors found in '" + filePath + "\n" + s);
            spawnerErrorCount++;
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
        for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources("gui_particle_spawners", resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
            {




                ResourceLocation id = entry.getKey();
                Resource resource = entry.getValue();

                if (ItemInteractionsConfig.debugDraws) {
                    Item_interactions_mod.infoMessage("Loading " + id + ":" + resource);
                }

                try (InputStream stream = resource.open()) {
                    JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();


//                    Item_interactions_mod.infoMessage("Parsing spawner: " + id);
                    GuiParticleSpawner a = parseSpawner(json, id, resourceManager);

                    SpawnerRegistry.register(a, id);

//                    Item_interactions_mod.infoMessage("Parsed");

                } catch (Exception e) {
                    Item_interactions_mod.errorMessage("Couldn't parse '" + id + "': \n" + e.getCause());
                    if (!spawnerErrorList.containsKey(id)) spawnerErrorList.put(id, new ArrayList<>());
                    spawnerErrorList.get(id).add(e.getMessage());
                    spawnerErrorCount++;

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

        String errorCountString = "";
        if (spawnerErrorCount == 1) errorCountString = " (1 error)";
        else if (spawnerErrorCount > 1) errorCountString = " (" + spawnerErrorCount + " errors)";
        Item_interactions_mod.infoMessage(String.format(spawnerString + errorCountString, SpawnerRegistry.SPAWNER_MAP.size()));




    }
    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, Executor executor, Executor executor2) {

        CompletableFuture<Void> a = CompletableFuture.supplyAsync(() -> {

            isReloadingResources = true;
            spawnerErrorList.clear();
            particleErrorList.clear();
            spawnerErrorCount = 0;
            currentParticleSpawner = "";
            this.loadStuff(resourceManager);

            if (spawnerErrorCount > 0) {

                String errorTitle = (spawnerErrorCount == 1) ? "%d Gui particle error" : "%d Gui particle errors";
                SystemToast.add(Minecraft.getInstance().getToastManager(), SystemToast.SystemToastId.PACK_LOAD_FAILURE,
                        Component.literal(String.format(errorTitle, spawnerErrorCount)),
                        Component.literal("Check the logs for more information") );

            }

            isReloadingResources = false;
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
