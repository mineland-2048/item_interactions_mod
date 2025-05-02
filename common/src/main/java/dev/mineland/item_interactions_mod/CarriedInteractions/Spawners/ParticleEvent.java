package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ParticleEvent {

//    public String name;
    public Optional<Float> interval = Optional.empty();
    public float nextInterval = 0;

    public Optional<ParticleInstance> attributes = Optional.empty();
    public Optional<ParticleInstance> attributes_variance = Optional.empty();
    public Optional<List<ParticleInstance>> particles = Optional.empty();

    public Optional<String> use = Optional.empty();



    public static final Codec<ParticleEvent> CODEC = RecordCodecBuilder.create(
            eventInstance -> eventInstance.group(
                Codec.FLOAT.optionalFieldOf("interval").forGetter(e -> e.interval),
                ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes").forGetter((ParticleEvent e) -> e.attributes),
                ParticleInstance.CONFIG_CODEC.optionalFieldOf("attributes_variance").forGetter((ParticleEvent e) -> e.attributes_variance),
                ParticleInstance.CODEC.listOf().optionalFieldOf("particles").forGetter((ParticleEvent e) -> e.particles),
                Codec.STRING.optionalFieldOf("use").forGetter((ParticleEvent e) -> e.use)

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
//        attributes = new ParticleInstance();
//        attributes_variance = ParticleInstance.defaultVariance();
    }

    public ParticleEvent(float a, String use) {
        System.out.println(a);
        System.out.println(use);
    }

    public ParticleEvent(float interval, ParticleInstance attributes, ParticleInstance attributes_variance, List<ParticleInstance> particles, String use) {
//        this.name = name;
//        System.out.println("eventing");
        this.interval = Optional.of(interval);
        this.attributes = Optional.of(attributes);
        this.attributes_variance = Optional.of(attributes_variance);
        this.particles = Optional.of(particles);
        this.use = Optional.of(use);
    }

    public ParticleEvent(String use) {

    }

    public ParticleEvent(Optional<Float> interval, Optional<ParticleInstance> attributes, Optional<ParticleInstance> attributes_variance, Optional<List<ParticleInstance>> particles, Optional<String> use) {
        this.interval = interval;
        this.attributes = attributes;
        this.attributes_variance = attributes_variance;
        this.particles = particles;
        this.use = use;
    }

    void fire(GuiGraphics guiGraphics, float x, float y, float speedX, float speedY) {
        for (ParticleInstance p : particles.orElse(new ArrayList<>())) {
            for (int i = 0; i < p.count.orElse(1); i++) {
                p.spawn(guiGraphics, x, y, speedX, speedY, attributes.orElse(new ParticleInstance()), attributes_variance.orElse(new ParticleInstance()));
            }
        }
    };

//    String getName() {return this.name;}




}


