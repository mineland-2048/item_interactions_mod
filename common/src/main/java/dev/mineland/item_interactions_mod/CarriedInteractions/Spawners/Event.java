package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.TexturedParticle;
import net.minecraft.util.ColorRGBA;

import java.util.ArrayList;
import java.util.List;


public class Event {
    static class EventAttributes {
        public float x, y;
        public float speedX, speedY;
        public float accelerationX, accelerationY;
        public float frictionX, frictionY;
        public float duration;

        public static final Codec<EventAttributes> CODEC = RecordCodecBuilder.create(positionsInstance -> positionsInstance.group(
                Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(position -> position.x),
                Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(position -> position.y),
                Codec.FLOAT.optionalFieldOf("speedX", 0f).forGetter(p -> p.speedX),
                Codec.FLOAT.optionalFieldOf("speedY", 0f).forGetter(p -> p.speedY),
                Codec.FLOAT.optionalFieldOf("accelerationX", 0f).forGetter(p -> p.accelerationX),
                Codec.FLOAT.optionalFieldOf("accelerationY", 0f).forGetter(p -> p.accelerationY),
                Codec.FLOAT.optionalFieldOf("frictionX", 0f).forGetter(p -> p.frictionX),
                Codec.FLOAT.optionalFieldOf("frictionY", 0f).forGetter(p -> p.frictionY),
                Codec.FLOAT.optionalFieldOf("duration", 1f).forGetter(p -> p.duration)
        ).apply(positionsInstance, EventAttributes::new));

        public EventAttributes() {
            this.x = 0;
            this.y = 0;
            this.speedX = 0;
            this.speedY = 0;
            this.accelerationX = 0;
            this.accelerationY = 0;
            this.frictionX = 0;
            this.frictionY = 0;
            this.duration = 1;

        }
        public EventAttributes(float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, float duration) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.accelerationX = accelerationX;
            this.accelerationY = accelerationY;
            this.frictionX = frictionX;
            this.frictionY = frictionY;
            this.duration = duration;
        }
    }

//    public String name;
    float interval;
    float nextInterval;
    public ParticleInstance attributes;
    public ParticleInstance attributes_variance;
    public List<ParticleInstance> particles;

    public String use;
//
//    public double x, y;
//    public double speedX, speedY;
//    public double accelerationX, accelerationY;
//    public double frictionX, frictionY;



    public static final Codec<Event> CODEC = RecordCodecBuilder.create(
            eventInstance -> eventInstance.group(
//                Codec.STRING.fieldOf("name").forGetter((Event e) -> e.name),
                Codec.FLOAT.optionalFieldOf("interval", 0f).forGetter(e -> e.interval),
                ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes", new ParticleInstance()).forGetter((Event e) -> e.attributes),
                ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes_variance", new ParticleInstance()).forGetter((Event e) -> e.attributes_variance),
                ParticleInstance.CODEC.listOf().fieldOf("particles").forGetter((Event e) -> e.particles),
                Codec.STRING.optionalFieldOf("use", null).forGetter((Event e) -> e.use)

            ).apply(eventInstance, Event::new));


    public Event(float interval, ParticleInstance attributes, ParticleInstance attributes_variance, List<ParticleInstance> particles, String use) {
//        this.name = name;
        this.interval = interval;
        this.attributes = attributes;
        this.attributes_variance = attributes_variance;
        this.particles = particles;
        this.use = use;
    }

    public Event(String use) {
    }

    void fire(Spawner spawner, float x, float y, float speedX, float speedY) {
        System.out.println("Event fired");
        for (ParticleInstance p : particles) {
            p.spawn();
        }
    };

//    String getName() {return this.name;}




}


