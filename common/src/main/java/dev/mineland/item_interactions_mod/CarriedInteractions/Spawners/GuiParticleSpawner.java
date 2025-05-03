package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiParticlesReloadListener;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicates;
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

    private Optional<ResourceLocation> parent = Optional.empty();
    private Optional<List<ResourceLocation>> childrenLocations = Optional.empty();
    private Optional<ParticleInstance> attributes = Optional.empty();
    private Optional<ParticleInstance> attributes_variance = Optional.empty();
    private Optional<Map<String, Either<ParticleEvent, String>>> events = Optional.empty();
    private Optional<List<ItemStack>> appliedItems = Optional.empty();

    private String state = null;

    private List<GuiParticleSpawner> childGuiParticleSpawners = new ArrayList<>();

    private float timer = 0;

    static Codec<Either<ParticleEvent, String>> eventOrStringCodec = Codec.either(ParticleEvent.CODEC, Codec.STRING);
    static Codec<Map<String, Either<ParticleEvent, String>>> eventsCodec = Codec.unboundedMap(Codec.STRING, eventOrStringCodec);



    public GuiParticleSpawner duplicate() {
        return new GuiParticleSpawner(this.parent, this.childrenLocations, attributes, attributes_variance, events, appliedItems);



    }
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


    private JsonObject tempParentJson;

    public GuiParticleSpawner parseSpawner(ResourceLocation id) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager.getResource(id).isEmpty()) {
            Item_interactions_mod.warnMessage("Spawner '" + id + "' is empty!");
            return null;
        };

        try (InputStream stream = resourceManager.getResource(id).get().open()) {
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            this.tempParentJson = json;
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

    }
    public GuiParticleSpawner(
            Optional<ResourceLocation> parent,
            Optional<List<ResourceLocation>> children,
            Optional<ParticleInstance> attributes,
            Optional<ParticleInstance> attributes_variance,
            Optional<Map<String, Either<ParticleEvent, String>>> eventMap,
            Optional<List<ItemStack>> appliedItems) {

        GuiParticleSpawner parentSpawner;
        Optional<Map<String, Either<ParticleEvent, String>>> parentEvents = Optional.empty();
        if (parent.isPresent()) {
            ResourceLocation resourceLocation = parent.get();
            parentSpawner = parseSpawner(resourceLocation);
            this.copyFromParent(parentSpawner);

        }
        attributes.ifPresent(this::setAttributes);
        attributes_variance.ifPresent(this::setAttributes_variance);


        if (parent.isPresent()) {
            DataResult<Map<String, Either<ParticleEvent, String>>> p = eventsCodec.parse(JsonOps.INSTANCE, tempParentJson.get("events"));
            if (p.resultOrPartial().isPresent()) {
                this.setEvents(p.resultOrPartial().get());
            }

        }

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

    public void fireEvent(int id, int childCount, String eventName, float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {
        if (this.events.isEmpty()) {
            System.out.println("No events registered in " + this.getName());
//            this.timer += timeDuration;
            return;
        }
        Either<ParticleEvent, String> eventStringEither = this.getEvents().get(eventName);
        if (eventStringEither == null) return;

        if (eventStringEither.right().isPresent()) {
            fireEvent(id, childCount, eventStringEither.right().get(), timeDuration, guiGraphics, x, y, speedX, speedY);
            return;
        }



        eventStringEither.ifLeft(e -> {
            GlobalDirt.slotSpawners.modifySpawnTimer(id, childCount, -timeDuration);
            float interval = GlobalDirt.slotSpawners.getSpawnerTimer(id, childCount);

            e.use.ifPresent(s -> e.inheritFromParent(this.getEvents(), this.getEvents().get(s)));

            if (interval <= 0) {
                float nextTime = (float) MiscUtils.randomVariance(e.interval.orElse(0f), e.interval_variance.orElse(0f));
                GlobalDirt.slotSpawners.setSpawnerTimer(id, childCount, nextTime);
                e.fire(guiGraphics, x, y, speedX, speedY);
            }

//            this.getEvents().put(eventName, Either.left(e));

        });
    }



    public void tick(float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY, int slotId, int childCount) {

        for (GuiParticleSpawner child : childGuiParticleSpawners) child.tick(timeDuration, guiGraphics, x, y, speedX, speedY, slotId, childCount + 1);
        Either<ParticleEvent, String> event = this.getEvents().get(this.state);
        if (event == null) return;

        fireEvent(slotId, childCount, this.state, timeDuration, guiGraphics, x, y, speedX, speedY);

//        timer += timeDuration;
    };


    public boolean matches(ItemStack itemStack) {
        DataComponentMap input = itemStack.getComponents();

        for (ItemStack conditionItem : this.appliedItems.orElse(new ArrayList<>())) {
            if (itemStack.getItem() != conditionItem.getItem()) continue;

            DataComponentMap conditionMap = conditionItem.getComponents();
            if (conditionMap.isEmpty()) return true;

            if (areDeepSubset(conditionMap, input)) {
                return true;
            }
        }
        return false;
    }

    private boolean areDeepSubset(DataComponentMap source, DataComponentMap target) {
        for (DataComponentType<?> type : source.keySet()) {
            if (!target.has(type)) {
                return false;
            }

            Object sourceValue = source.get(type);
            Object targetValue = target.get(type);

            if (!compareValuesAsSubset(sourceValue, targetValue)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static boolean compareValuesAsSubset(Object source, Object target) {
        if (source == null || target == null) return source == target;

        if (source instanceof Map && target instanceof Map) {
            Map<Object, Object> sourceMap = (Map<Object, Object>) source;
            Map<Object, Object> targetMap = (Map<Object, Object>) target;

            for (Map.Entry<Object, Object> entry : sourceMap.entrySet()) {
                Object key = entry.getKey();
                if (!targetMap.containsKey(key)) return false;
                if (!compareValuesAsSubset(entry.getValue(), targetMap.get(key))) return false;
            }
            return true;
        }

        if (source instanceof Iterable && target instanceof Iterable) {
            // Require all source elements to be in target
            for (Object srcItem : (Iterable<?>) source) {
                boolean matched = false;
                for (Object tgtItem : (Iterable<?>) target) {
                    if (compareValuesAsSubset(srcItem, tgtItem)) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) return false;
            }
            return true;
        }

        if (source.getClass().isRecord() && target.getClass().isRecord() && source.getClass() == target.getClass()) {
            // Reflectively compare fields in the record
            try {
                for (var field : source.getClass().getRecordComponents()) {
                    var getter = field.getAccessor();
                    Object srcField = getter.invoke(source);
                    Object tgtField = getter.invoke(target);
                    if (!compareValuesAsSubset(srcField, tgtField)) {
                        return false;
                    }
                }
                return true;
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to compare record fields", e);
            }
        }

        // Fallback to simple equality
        return source.equals(target);
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

    public void setAttributes_variance(ParticleInstance attributes_variance) {
        if (this.attributes_variance.isPresent()) {
            ParticleInstance newAttributes = this.attributes_variance.get();

            attributes_variance.x.ifPresent(attr -> newAttributes.x = Optional.of(attr));
            attributes_variance.y.ifPresent(attr -> newAttributes.y = Optional.of(attr));
            attributes_variance.speedX.ifPresent(attr -> newAttributes.speedX = Optional.of(attr));
            attributes_variance.speedY.ifPresent(attr -> newAttributes.speedY = Optional.of(attr));
            attributes_variance.accelerationX.ifPresent(attr -> newAttributes.accelerationX = Optional.of(attr));
            attributes_variance.accelerationY.ifPresent(attr -> newAttributes.accelerationY = Optional.of(attr));
            attributes_variance.frictionX.ifPresent(attr -> newAttributes.frictionX = Optional.of(attr));
            attributes_variance.frictionY.ifPresent(attr -> newAttributes.frictionY = Optional.of(attr));
            attributes_variance.colorStart.ifPresent(attr -> newAttributes.colorStart = Optional.of(attr));
            attributes_variance.colorEnd.ifPresent(attr -> newAttributes.colorEnd = Optional.of(attr));
            attributes_variance.duration.ifPresent(attr -> newAttributes.duration = Optional.of(attr));
            attributes_variance.count.ifPresent(attr -> newAttributes.count = Optional.of(attr));

            this.attributes_variance = Optional.of(newAttributes);
        } else this.attributes_variance = Optional.of(attributes_variance);
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
                else if (eitherCurrentEvent.right().isPresent()) finalEvent.use = Optional.of(eitherCurrentEvent.right().get());


                if (finalEvent.attributes.isEmpty())  finalEvent.attributes = Optional.of(new ParticleInstance());
                if (finalEvent.attributes_variance.isEmpty())  finalEvent.attributes_variance = Optional.of(new ParticleInstance());

                if (this.attributes.isPresent()) {
                    finalEvent.attributes.get().x = finalEvent.attributes.get().x.isEmpty() ? this.attributes.get().x : finalEvent.attributes.get().x;
                    finalEvent.attributes.get().y = finalEvent.attributes.get().y.isEmpty() ? this.attributes.get().y : finalEvent.attributes.get().y;

                    finalEvent.attributes.get().speedX = finalEvent.attributes.get().speedX.isEmpty() ? this.attributes.get().speedX : finalEvent.attributes.get().speedX;
                    finalEvent.attributes.get().speedY = finalEvent.attributes.get().speedY.isEmpty() ? this.attributes.get().speedY : finalEvent.attributes.get().speedY;

                    finalEvent.attributes.get().accelerationX = finalEvent.attributes.get().accelerationX.isEmpty() ? this.attributes.get().accelerationX : finalEvent.attributes.get().accelerationX;
                    finalEvent.attributes.get().accelerationY = finalEvent.attributes.get().accelerationY.isEmpty() ? this.attributes.get().accelerationY : finalEvent.attributes.get().accelerationY;

                    finalEvent.attributes.get().frictionX = finalEvent.attributes.get().frictionX.isEmpty() ? this.attributes.get().frictionX : finalEvent.attributes.get().frictionX;
                    finalEvent.attributes.get().frictionY = finalEvent.attributes.get().frictionY.isEmpty() ? this.attributes.get().frictionY : finalEvent.attributes.get().frictionY;

                    finalEvent.attributes.get().colorStart = finalEvent.attributes.get().colorStart.isEmpty() ? this.attributes.get().colorStart : finalEvent.attributes.get().colorStart;
                    finalEvent.attributes.get().colorEnd = finalEvent.attributes.get().colorEnd.isEmpty() ? this.attributes.get().colorEnd : finalEvent.attributes.get().colorEnd;

                    finalEvent.attributes.get().duration = finalEvent.attributes.get().duration.isEmpty() ? this.attributes.get().duration : finalEvent.attributes.get().duration;
                    finalEvent.attributes.get().count = finalEvent.attributes.get().count.isEmpty() ? this.attributes.get().count : finalEvent.attributes.get().count;

                }

                if (this.attributes_variance.isPresent()) {
                    finalEvent.attributes_variance.get().x = finalEvent.attributes_variance.get().x.isEmpty() ? this.attributes_variance.get().x : finalEvent.attributes_variance.get().x;
                    finalEvent.attributes_variance.get().y = finalEvent.attributes_variance.get().y.isEmpty() ? this.attributes_variance.get().y : finalEvent.attributes_variance.get().y;

                    finalEvent.attributes_variance.get().speedX = finalEvent.attributes_variance.get().speedX.isEmpty() ? this.attributes_variance.get().speedX : finalEvent.attributes_variance.get().speedX;
                    finalEvent.attributes_variance.get().speedY = finalEvent.attributes_variance.get().speedY.isEmpty() ? this.attributes_variance.get().speedY : finalEvent.attributes_variance.get().speedY;

                    finalEvent.attributes_variance.get().accelerationX = finalEvent.attributes_variance.get().accelerationX.isEmpty() ? this.attributes_variance.get().accelerationX : finalEvent.attributes_variance.get().accelerationX;
                    finalEvent.attributes_variance.get().accelerationY = finalEvent.attributes_variance.get().accelerationY.isEmpty() ? this.attributes_variance.get().accelerationY : finalEvent.attributes_variance.get().accelerationY;

                    finalEvent.attributes_variance.get().frictionX = finalEvent.attributes_variance.get().frictionX.isEmpty() ? this.attributes_variance.get().frictionX : finalEvent.attributes_variance.get().frictionX;
                    finalEvent.attributes_variance.get().frictionY = finalEvent.attributes_variance.get().frictionY.isEmpty() ? this.attributes_variance.get().frictionY : finalEvent.attributes_variance.get().frictionY;

                    finalEvent.attributes_variance.get().colorStart = finalEvent.attributes_variance.get().colorStart.isEmpty() ? this.attributes_variance.get().colorStart : finalEvent.attributes_variance.get().colorStart;
                    finalEvent.attributes_variance.get().colorEnd = finalEvent.attributes_variance.get().colorEnd.isEmpty() ? this.attributes_variance.get().colorEnd : finalEvent.attributes_variance.get().colorEnd;

                    finalEvent.attributes_variance.get().duration = finalEvent.attributes_variance.get().duration.isEmpty() ? this.attributes_variance.get().duration : finalEvent.attributes_variance.get().duration;
                    finalEvent.attributes_variance.get().count = finalEvent.attributes_variance.get().count.isEmpty() ? this.attributes_variance.get().count : finalEvent.attributes_variance.get().count;
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
