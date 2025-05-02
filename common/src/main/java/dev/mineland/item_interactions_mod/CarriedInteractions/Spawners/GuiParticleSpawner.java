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

import javax.swing.text.html.Option;
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
                e.nextInterval = e.interval;
                e.fire(guiGraphics, x, y, speedX, speedY);
            }

//            this.getEvents().put(eventName, Either.left(e));

        });




    }

    public void tick(float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {

        for (GuiParticleSpawner child : childGuiParticleSpawners) child.tick(timeDuration, guiGraphics, x, y, speedX, speedY);
        Either<ParticleEvent, String> event = this.getEvents().get(this.state);
        if (event == null) return;

        fireEvent(this.state, timeDuration, guiGraphics, x, y, speedX, speedY);

        timer += timeDuration;
    };

    public boolean matches(ItemStack itemStack) {

//        TODO: Do matching logic;
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
            attributes.speedX.ifPresent(attr -> newAttributes.speedX = Optional.of(attr));
            attributes.speedY.ifPresent(attr -> newAttributes.speedY = Optional.of(attr));
            attributes.accelerationX.ifPresent(attr -> newAttributes.accelerationX = Optional.of(attr));
            attributes.accelerationY.ifPresent(attr -> newAttributes.accelerationY = Optional.of(attr));
            attributes.frictionX.ifPresent(attr -> newAttributes.frictionX = Optional.of(attr));
            attributes.frictionY.ifPresent(attr -> newAttributes.frictionY = Optional.of(attr));
            attributes.colorStart.ifPresent(attr -> newAttributes.colorStart = Optional.of(attr));
            attributes.colorEnd.ifPresent(attr -> newAttributes.colorEnd = Optional.of(attr));
            attributes.duration.ifPresent(attr -> newAttributes.duration = Optional.of(attr));
            attributes.count.ifPresent(attr -> newAttributes.count = Optional.of(attr));

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
            attributes.speedX.ifPresent(attr -> newAttributes.speedX = Optional.of(attr));
            attributes.speedY.ifPresent(attr -> newAttributes.speedY = Optional.of(attr));
            attributes.accelerationX.ifPresent(attr -> newAttributes.accelerationX = Optional.of(attr));
            attributes.accelerationY.ifPresent(attr -> newAttributes.accelerationY = Optional.of(attr));
            attributes.frictionX.ifPresent(attr -> newAttributes.frictionX = Optional.of(attr));
            attributes.frictionY.ifPresent(attr -> newAttributes.frictionY = Optional.of(attr));
            attributes.colorStart.ifPresent(attr -> newAttributes.colorStart = Optional.of(attr));
            attributes.colorEnd.ifPresent(attr -> newAttributes.colorEnd = Optional.of(attr));
            attributes.duration.ifPresent(attr -> newAttributes.duration = Optional.of(attr));
            attributes.count.ifPresent(attr -> newAttributes.count = Optional.of(attr));

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

    public void setEvents(Map<String, Either<ParticleEvent, String>> newEvents) {

//        this.events.ifPresent(eventsMap -> {
//            Map<String, Either<ParticleEvent, String>> eventsMap = this.events.orElse(new HashMap<>())
            Map<String, Either<ParticleEvent, String>> newEventsMap = this.events.orElse(new HashMap<>());

            System.out.println("Parsing events: " + newEvents);


            newEvents.forEach((String k, Either<ParticleEvent, String> newEvent) -> {
                Either<ParticleEvent, String> eitherCurrentEvent = newEventsMap.getOrDefault(k, newEvent);
                ParticleEvent finalEvent = new ParticleEvent();

                if (eitherCurrentEvent.left().isPresent()) finalEvent = eitherCurrentEvent.left().get();
                else if (eitherCurrentEvent.right().isPresent()) finalEvent.use = eitherCurrentEvent.right().get();


                if (this.attributes.isPresent()) {
                    finalEvent.attributes.x = finalEvent.attributes.x.isEmpty() ? this.attributes.get().x : finalEvent.attributes.x;
                    finalEvent.attributes.y = finalEvent.attributes.y.isEmpty() ? this.attributes.get().y : finalEvent.attributes.y;

                    finalEvent.attributes.speedX = finalEvent.attributes.speedX.isEmpty() ? this.attributes.get().speedX : finalEvent.attributes.speedX;
                    finalEvent.attributes.speedY = finalEvent.attributes.speedY.isEmpty() ? this.attributes.get().speedY : finalEvent.attributes.speedY;

                    finalEvent.attributes.accelerationX = finalEvent.attributes.accelerationX.isEmpty() ? this.attributes.get().accelerationX : finalEvent.attributes.accelerationX;
                    finalEvent.attributes.accelerationY = finalEvent.attributes.accelerationY.isEmpty() ? this.attributes.get().accelerationY : finalEvent.attributes.accelerationY;

                    finalEvent.attributes.frictionX = finalEvent.attributes.frictionX.isEmpty() ? this.attributes.get().frictionX : finalEvent.attributes.frictionX;
                    finalEvent.attributes.frictionY = finalEvent.attributes.frictionY.isEmpty() ? this.attributes.get().frictionY : finalEvent.attributes.frictionY;

                    finalEvent.attributes.colorStart = finalEvent.attributes.colorStart.isEmpty() ? this.attributes.get().colorStart : finalEvent.attributes.colorStart;
                    finalEvent.attributes.colorEnd = finalEvent.attributes.colorEnd.isEmpty() ? this.attributes.get().colorEnd : finalEvent.attributes.colorEnd;

                    finalEvent.attributes.duration = finalEvent.attributes.duration.isEmpty() ? this.attributes.get().duration : finalEvent.attributes.duration;
                    finalEvent.attributes.count = finalEvent.attributes.count.isEmpty() ? this.attributes.get().count : finalEvent.attributes.count;

                }
                if (attributes_variance.isPresent()) {
                    finalEvent.attributes_variance.x = finalEvent.attributes_variance.x.isEmpty() ? this.attributes_variance.get().x : finalEvent.attributes_variance.x;
                    finalEvent.attributes_variance.y = finalEvent.attributes_variance.y.isEmpty() ? this.attributes_variance.get().y : finalEvent.attributes_variance.y;

                    finalEvent.attributes_variance.speedX = finalEvent.attributes_variance.speedX.isEmpty() ? this.attributes_variance.get().speedX : finalEvent.attributes_variance.speedX;
                    finalEvent.attributes_variance.speedY = finalEvent.attributes_variance.speedY.isEmpty() ? this.attributes_variance.get().speedY : finalEvent.attributes_variance.speedY;

                    finalEvent.attributes_variance.accelerationX = finalEvent.attributes_variance.accelerationX.isEmpty() ? this.attributes_variance.get().accelerationX : finalEvent.attributes_variance.accelerationX;
                    finalEvent.attributes_variance.accelerationY = finalEvent.attributes_variance.accelerationY.isEmpty() ? this.attributes_variance.get().accelerationY : finalEvent.attributes_variance.accelerationY;

                    finalEvent.attributes_variance.frictionX = finalEvent.attributes_variance.frictionX.isEmpty() ? this.attributes_variance.get().frictionX : finalEvent.attributes_variance.frictionX;
                    finalEvent.attributes_variance.frictionY = finalEvent.attributes_variance.frictionY.isEmpty() ? this.attributes_variance.get().frictionY : finalEvent.attributes_variance.frictionY;

                    finalEvent.attributes_variance.colorStart = finalEvent.attributes_variance.colorStart.isEmpty() ? this.attributes_variance.get().colorStart : finalEvent.attributes_variance.colorStart;
                    finalEvent.attributes_variance.colorEnd = finalEvent.attributes_variance.colorEnd.isEmpty() ? this.attributes_variance.get().colorEnd : finalEvent.attributes_variance.colorEnd;

                    finalEvent.attributes_variance.duration = finalEvent.attributes_variance.duration.isEmpty() ? this.attributes_variance.get().duration : finalEvent.attributes_variance.duration;
                    finalEvent.attributes_variance.count = finalEvent.attributes_variance.count.isEmpty() ? this.attributes_variance.get().count : finalEvent.attributes_variance.count;
                }


                newEventsMap.put(k, Either.left(finalEvent));

            });


            this.events = Optional.of(newEventsMap);
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
