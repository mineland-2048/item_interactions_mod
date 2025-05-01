package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.GuiParticlesReloadListener;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class GuiParticleSpawner {
    protected int id = 0;
    protected ResourceLocation name = null;

    private Optional<ParticleInstance> attributes = Optional.empty();
    private Optional<ParticleInstance> attributes_variance = Optional.empty();
    private Optional<Map<String, Either<ParticleEvent, String>>> events = Optional.empty();

    private String state = null;
    private Optional<ResourceLocation> parent = Optional.empty();
    private Optional<List<ResourceLocation>> childrenLocations = Optional.empty();

    private List<GuiParticleSpawner> childGuiParticleSpawners = new ArrayList<>();
    private Optional<List<ItemStack>> appliedItems = Optional.empty();

    private float timer = 0;

    static Codec<Either<ParticleEvent, String>> eventOrStringCodec = Codec.either(ParticleEvent.CODEC, Codec.STRING);
    static Codec<Map<String, Either<ParticleEvent, String>>> eventsCodec = Codec.unboundedMap(Codec.STRING, eventOrStringCodec);



    public static Codec<GuiParticleSpawner> CODEC = RecordCodecBuilder.create(
            spawnerInstance -> spawnerInstance.group(
                    ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(s -> s.parent),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("children").forGetter(s -> s.childrenLocations),
                    ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes").forGetter(s -> s.attributes),
                    ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes_variance").forGetter(s -> s.attributes_variance),

                    eventsCodec.optionalFieldOf("events").forGetter((spawner -> spawner.events)),

                    ItemStack.CODEC.listOf().optionalFieldOf("applies").forGetter(s -> s.appliedItems)

            ).apply(spawnerInstance, GuiParticleSpawner::new)
    );


    public GuiParticleSpawner() {
        this.id = -1;
    }

//    public GuiParticleSpawner(ResourceLocation parent, List<ResourceLocation> childrenLocations, ParticleInstance attributes, ParticleInstance attributes_variance, Map<String, Either<ParticleEvent, String>> events, List<ItemStack> appliedItems) {
//        this.parent = parent;
//
//        if (parent != null) {
//            System.out.println("Parent! parse all stuff into self");
//
//                Optional<Resource> parentResource = Minecraft.getInstance().getResourceManager().getResource(parent);
//
//                if (parentResource.isPresent()) {
//
//                }
//
//
//        }
//
//        for (ResourceLocation child : childrenLocations) {
//            System.out.println("parse " + child + "into children");
//        }
//
//        this.childrenLocations = childrenLocations;
//        this.attributes = attributes;
//        this.attributes_variance = attributes_variance;
//        this.events = events;
//        this.appliedItems = appliedItems;
//    }
//

    public GuiParticleSpawner parseSpawner(ResourceLocation id) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager.getResource(id).isEmpty()) {
            Item_interactions_mod.warnMessage("Spawner '" + id + "' is empty!");
            return null;
        };

        try (InputStream stream = resourceManager.getResource(id).get().open()) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

            GuiParticleSpawner result;
            DataResult<GuiParticleSpawner> dataResult;

            dataResult = GuiParticleSpawner.CODEC.parse(JsonOps.INSTANCE, json);
            result = dataResult.resultOrPartial(Item_interactions_mod::warnMessage).orElseThrow();



            result.setName(id);
            return result;



        } catch (JsonParseException | IOException e) {
            Item_interactions_mod.warnMessage("Couldnt parse parent '" + id + "'!\n" + e);
            return null;
        }

    }

    public void copyFromParent(GuiParticleSpawner parent) {
        this.childGuiParticleSpawners = parent.childGuiParticleSpawners;
        this.attributes = parent.attributes;
        this.attributes_variance = parent.attributes_variance;
        this.events = parent.events;

    }
    public GuiParticleSpawner(
            Optional<ResourceLocation> parent,
            Optional<List<ResourceLocation>> children,
            Optional<ParticleInstance> attributes,
            Optional<ParticleInstance> attributes_variance,
            Optional<Map<String, Either<ParticleEvent, String>>> eventMap,
            Optional<List<ItemStack>> appliedItems) {

        parent.ifPresent(resourceLocation -> this.copyFromParent(parseSpawner(resourceLocation)));

        attributes.ifPresent(this::setAttributes);
        attributes_variance.ifPresent(this::setAttributes_variance);
        eventMap.ifPresent(this::setEvents);
        appliedItems.ifPresent(this::setAppliedItems);



        children.ifPresent(childrenList -> {
            for (ResourceLocation childId : childrenList) {
                GuiParticleSpawner c = parseSpawner(childId);
                if (c != null) this.childGuiParticleSpawners.add(c);
            }
        });
    }


    public void setName(ResourceLocation name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = ResourceLocation.parse(name);
    }

    public ResourceLocation getName() {
        return this.name;
    }



    public Codec<GuiParticleSpawner> getCODEC() { return GuiParticleSpawner.CODEC; }

    public void fireEvent(String eventName, float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {
        if (this.events.isEmpty()) {
            System.out.println("No events registered in " + this.getName());
            this.timer += timeDuration;
            return;
        }
        Either<ParticleEvent, String> eventStringEither = this.getEvents().get(eventName);
        if (eventStringEither.right().isPresent()) {
            fireEvent(eventStringEither.right().get(), timeDuration, guiGraphics, x, y, speedX, speedY);
            return;
        }



        eventStringEither.ifLeft(e -> {
            e.nextInterval -= timeDuration;
            if (e.nextInterval <= 0) {
                e.fire(this, x, y, speedX, speedY);

                e.nextInterval = e.interval;
            }
        });




    }

    public void tick(float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {

        Either<ParticleEvent, String> event = this.getEvents().get(this.state);
        if (event == null) return;

        fireEvent(this.state, timeDuration, guiGraphics, x, y, speedX, speedY);

        timer += timeDuration;
    };

    public boolean matches(ItemStack itemStack) {

//        Do matching logic;
        return true;
    }


    public List<ItemStack> getAppliedItems() {

        return this.appliedItems.orElseGet(ArrayList::new);


    }

    public void setAppliedItems(List<ItemStack> appliedItems) {
        this.appliedItems = Optional.of(appliedItems);
    }

    public ParticleInstance getAttributes() {
        return attributes.orElseGet(ParticleInstance::new);
    }

    public void setAttributes(ParticleInstance attributes) {
        if (this.attributes.isPresent()) {
            ParticleInstance newAttributes = this.attributes.get();

            attributes.x.ifPresent(attr -> newAttributes.x = Optional.of(attr));
            attributes.y.ifPresent(attr -> newAttributes.y = Optional.of(attr));
            attributes.speedX.ifPresent(attr -> attributes.speedX = Optional.of(attr));
            attributes.speedY.ifPresent(attr -> attributes.speedY = Optional.of(attr));
            attributes.accelerationX.ifPresent(attr -> attributes.accelerationX = Optional.of(attr));
            attributes.accelerationY.ifPresent(attr -> attributes.accelerationY = Optional.of(attr));
            attributes.frictionX.ifPresent(attr -> attributes.frictionX = Optional.of(attr));
            attributes.frictionY.ifPresent(attr -> attributes.frictionY = Optional.of(attr));
            attributes.colorStart.ifPresent(attr -> attributes.colorStart = Optional.of(attr));
            attributes.colorEnd.ifPresent(attr -> attributes.colorEnd = Optional.of(attr));
            attributes.duration.ifPresent(attr -> attributes.duration = Optional.of(attr));
            attributes.count.ifPresent(attr -> attributes.count = Optional.of(attr));

            this.attributes = Optional.of(newAttributes);
        } else this.attributes = Optional.of(attributes);
    }

    public ParticleInstance getAttributes_variance() {
        return attributes_variance.orElseGet(ParticleInstance::new);
    }

    public void setAttributes_variance(ParticleInstance attributes) {
        if (this.attributes_variance.isPresent()) {
            ParticleInstance newAttributes = this.attributes_variance.get();

            attributes.x.ifPresent(attr -> newAttributes.x = Optional.of(attr));
            attributes.y.ifPresent(attr -> newAttributes.y = Optional.of(attr));
            attributes.speedX.ifPresent(attr -> attributes.speedX = Optional.of(attr));
            attributes.speedY.ifPresent(attr -> attributes.speedY = Optional.of(attr));
            attributes.accelerationX.ifPresent(attr -> attributes.accelerationX = Optional.of(attr));
            attributes.accelerationY.ifPresent(attr -> attributes.accelerationY = Optional.of(attr));
            attributes.frictionX.ifPresent(attr -> attributes.frictionX = Optional.of(attr));
            attributes.frictionY.ifPresent(attr -> attributes.frictionY = Optional.of(attr));
            attributes.colorStart.ifPresent(attr -> attributes.colorStart = Optional.of(attr));
            attributes.colorEnd.ifPresent(attr -> attributes.colorEnd = Optional.of(attr));
            attributes.duration.ifPresent(attr -> attributes.duration = Optional.of(attr));
            attributes.count.ifPresent(attr -> attributes.count = Optional.of(attr));

            this.attributes_variance = Optional.of(newAttributes);
        } else this.attributes_variance = Optional.of(attributes);
    }

    public List<GuiParticleSpawner> getChildGuiParticleSpawners() {
        return childGuiParticleSpawners;
    }

    public void setChildGuiParticleSpawners(List<GuiParticleSpawner> childGuiParticleSpawners) {
        this.childGuiParticleSpawners = childGuiParticleSpawners;
    }



    public Map<String, Either<ParticleEvent, String>> getEvents() {
        return this.events.orElseGet(HashMap::new);
    }

    public void setEvents(Map<String, Either<ParticleEvent, String>> events) {

        this.events.ifPresent(eventsMap -> {
            Map<String, Either<ParticleEvent, String>> currentEvents = this.events.get();
            currentEvents.putAll(events);
        });
        this.events = Optional.of(events);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResourceLocation getParent() {
        return parent.orElseGet(() -> null);
    }

    public void setParent(ResourceLocation parent) {
        this.parent = Optional.of(parent);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }
}
