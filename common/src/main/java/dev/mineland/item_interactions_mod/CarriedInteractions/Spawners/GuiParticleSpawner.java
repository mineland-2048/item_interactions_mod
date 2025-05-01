package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class GuiParticleSpawner {
    protected int id = 0;
    protected ResourceLocation name = null;

    public ParticleInstance attributes = new ParticleInstance();
    public ParticleInstance attributes_variance = ParticleInstance.defaultVariance();
    public Map<String, Either<ParticleEvent, String>> events = new HashMap<>();

    public String state = null;
    public ResourceLocation parent;
    public List<ResourceLocation> childrenLocations = new ArrayList<>();

    public List<GuiParticleSpawner> childGuiParticleSpawners = new ArrayList<>();

    public float timer = 0;

    static Codec<Either<ParticleEvent, String>> eventOrStringCodec = Codec.either(ParticleEvent.CODEC, Codec.STRING);
    static Codec<Map<String, Either<ParticleEvent, String>>> eventsCodec = Codec.unboundedMap(Codec.STRING, eventOrStringCodec);

    public List<ItemStack> appliedItems;


    public static Codec<GuiParticleSpawner> CODEC = RecordCodecBuilder.create(
            spawnerInstance -> spawnerInstance.group(
                    ResourceLocation.CODEC.fieldOf("parent").orElse(Item_interactions_mod::warnMessage, null).forGetter(s -> s.parent),
                    ResourceLocation.CODEC.listOf().fieldOf("children").orElse(Item_interactions_mod::warnMessage, null).forGetter(s -> s.childrenLocations),
                    ParticleInstance.CONFIG_CODEC.fieldOf("attributes").orElse(Item_interactions_mod::warnMessage, null).forGetter(s -> s.attributes),
                    ParticleInstance.CONFIG_CODEC.fieldOf("attributes_variance").orElse(Item_interactions_mod::warnMessage, null).forGetter(s -> s.attributes_variance),

                    eventsCodec.fieldOf("events").orElse(Item_interactions_mod::warnMessage, new HashMap<>()).forGetter((spawner -> spawner.events)),

                    ItemStack.CODEC.listOf().fieldOf("applies").orElse(Item_interactions_mod::warnMessage, null).forGetter(s -> s.appliedItems)

            ).apply(spawnerInstance, GuiParticleSpawner::new)
    );


    public GuiParticleSpawner(ResourceLocation parent, List<ResourceLocation> childrenLocations, ParticleInstance attributes, ParticleInstance attributes_variance, Map<String, Either<ParticleEvent, String>> events, List<ItemStack> appliedItems) {
        this.parent = parent;

        if (parent != null) {
            System.out.println("Parent! parse all stuff into self");
        }

        for (ResourceLocation child : childrenLocations) {
            System.out.println("parse " + child + "into children");
        }

        this.childrenLocations = childrenLocations;
        this.attributes = attributes;
        this.attributes_variance = attributes_variance;
        this.events = events;

        this.appliedItems = appliedItems;
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
        Either<ParticleEvent, String> eventStringEither = events.get(eventName);
        eventStringEither.ifRight((s) -> {
            fireEvent(s, timeDuration, guiGraphics, x, y, speedX, speedY);
        });

        eventStringEither.ifLeft(e -> {
            e.nextInterval -= timeDuration;
            if (e.nextInterval <= 0) {
                e.fire(this, x, y, speedX, speedY);

                e.nextInterval = e.interval;
            }
        });

        this.timer += timeDuration;



    }

    public void tick(float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {
        timer += timeDuration;

        events.forEach( (String k, Either<ParticleEvent, String> v) -> {
            v.ifLeft((e) -> {
                if (e.nextInterval <= 0) e.fire(this, x, y, speedX, speedY);
            });
        });
    };

    public boolean matches(ItemStack itemStack) {

//        Do matching logic;
        return true;
    }
}
