package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.GlobalDirt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Spawner {
    protected int id;
    protected ResourceLocation name;

    public ParticleInstance attributes;
    public ParticleInstance attributes_variance;
    public Map<String, Either<Event, String>> events = new HashMap<>();

    public String state;
    public ResourceLocation parent;
    public List<ResourceLocation> childrenLocations;

    public List<Spawner> childSpawners;

    public float timer = 0;

    Codec<Either<Event, String>> eventOrStringCodec = Codec.either(Event.CODEC, Codec.STRING);
    Codec<Map<String, Either<Event, String>>> a = Codec.unboundedMap(Codec.STRING, eventOrStringCodec);

    public List<ItemStack> appliedItems = new ArrayList<>();


    public Codec<Spawner> CODEC = RecordCodecBuilder.create(
            spawnerInstance -> spawnerInstance.group(
                    ResourceLocation.CODEC.optionalFieldOf("parent", null).forGetter(s -> s.parent),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("children", null).forGetter(s -> s.childrenLocations),
                    ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes", new ParticleInstance()).forGetter(s -> s.attributes),
                    ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes_variance", ParticleInstance.defaultVariance()).forGetter(s -> s.attributes_variance),

                    a.fieldOf("events").forGetter((spawner -> spawner.events)),

                    ItemStack.CODEC.listOf().optionalFieldOf("applies", null).forGetter(s -> s.appliedItems)

            ).apply(spawnerInstance, Spawner::new)
    );


    public Spawner(ResourceLocation parent, List<ResourceLocation> childrenLocations, ParticleInstance attributes, ParticleInstance attributes_variance, Map<String, Either<Event, String>> events, List<ItemStack> appliedItems) {
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




    public void tick(float timeDuration, GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {
        timer += timeDuration;

        events.forEach( (String k, Either<Event, String> v) -> {
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
