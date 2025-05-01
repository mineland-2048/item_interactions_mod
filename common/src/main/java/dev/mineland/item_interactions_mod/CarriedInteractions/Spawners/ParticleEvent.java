package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;


public class ParticleEvent {

//    public String name;
    public float interval = 0f;
    public float nextInterval = 0f;

    public ParticleInstance attributes;
    public ParticleInstance attributes_variance;
    public List<ParticleInstance> particles = new ArrayList<>(10);

    public String use = "";



    public static final Codec<ParticleEvent> CODEC = RecordCodecBuilder.create(
            eventInstance -> eventInstance.group(
                Codec.FLOAT.optionalFieldOf("interval", 0f).forGetter(e -> e.interval),
                ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes", new ParticleInstance()).forGetter((ParticleEvent e) -> e.attributes),
                ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes_variance", new ParticleInstance()).forGetter((ParticleEvent e) -> e.attributes_variance),
                ParticleInstance.CODEC.listOf().optionalFieldOf("particles", new ArrayList<>()).forGetter((ParticleEvent e) -> e.particles),
                Codec.STRING.optionalFieldOf("use", "null").forGetter((ParticleEvent e) -> e.use)

            ).apply(eventInstance, ParticleEvent::new));

//    public static final Codec<ParticleEvent> CODEC = RecordCodecBuilder.create(
//            eventInstance -> eventInstance.group(
//                Codec.FLOAT.optionalFieldOf("interval", 0f).forGetter(e -> e.interval),
//                Codec.STRING.optionalFieldOf("use", "null").forGetter(e -> e.use)
//
//            ).apply(eventInstance, ParticleEvent::new));
//
//
//    public static final Codec<ParticleEvent> NEW_CODEC = RecordCodecBuilder.create(particleInstanceInstance -> particleInstanceInstance.group(
//            Codec.FLOAT.optionalFieldOf("interval", 0f).forGetter(p -> p.interval),
//            Codec.STRING.optionalFieldOf("use", "1").forGetter(p -> p.use)
//
//    ).apply(particleInstanceInstance, ParticleEvent::new));

    public ParticleEvent() {

    }

    public ParticleEvent(float a, String use) {
        System.out.println(a);
        System.out.println(use);
    }

    public ParticleEvent(float interval, ParticleInstance attributes, ParticleInstance attributes_variance, List<ParticleInstance> particles, String use) {
//        this.name = name;
        System.out.println("eventing");
        this.interval = interval;
        this.attributes = attributes;
        this.attributes_variance = attributes_variance;
        this.particles = particles;
        this.use = use;
    }

    public ParticleEvent(String use) {

    }

    void fire(GuiParticleSpawner guiParticleSpawner, float x, float y, float speedX, float speedY) {
        System.out.println("ParticleEvent fired");
        for (ParticleInstance p : particles) {
            p.spawn();
        }
    };

//    String getName() {return this.name;}




}


