package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.client.gui.GuiGraphics;

import java.util.*;


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
        ParticleInstance empty = new ParticleInstance();
        int eventCount = this.attributes.orElse(empty).count.orElse(1);
        int rand = this.attributes_variance.orElse(empty).count.orElse(0);
        for (ParticleInstance p : particles.orElse(new ArrayList<>())) {
            for (int i = 0; i < (int) MiscUtils.randomVariance(p.count.orElse(eventCount), rand); i++) {
                p.spawn(guiGraphics, x, y, speedX, speedY, attributes.orElse(empty), attributes_variance.orElse(empty));
            }
        }
    };

    public void inheritFromParent(Map<String, Either<ParticleEvent, String>> eventMap, Either<ParticleEvent, String> parent) {
        if (parent == null) return;
        if (parent.right().isPresent()) {
            inheritFromParent(eventMap, parent);
            return;
        }
        if (parent.left().isEmpty()) return;

        ParticleInstance localAttributes = new ParticleInstance();
        ParticleInstance localAttributesVariance = new ParticleInstance();


        if (parent.left().get().attributes.isPresent()) {
            localAttributes = parent.left().get().attributes.orElse(localAttributes);
        }

        if (this.attributes.isPresent()) {
                ParticleInstance newAttributes = this.attributes.get();

                localAttributes.x.ifPresent(attr -> newAttributes.x = Optional.of(attr));
                localAttributes.y.ifPresent(attr -> newAttributes.y = Optional.of(attr));
                localAttributes.speedX.ifPresent(attr -> newAttributes.speedX = Optional.of(attr));
                localAttributes.speedY.ifPresent(attr -> newAttributes.speedY = Optional.of(attr));
                localAttributes.accelerationX.ifPresent(attr -> newAttributes.accelerationX = Optional.of(attr));
                localAttributes.accelerationY.ifPresent(attr -> newAttributes.accelerationY = Optional.of(attr));
                localAttributes.frictionX.ifPresent(attr -> newAttributes.frictionX = Optional.of(attr));
                localAttributes.frictionY.ifPresent(attr -> newAttributes.frictionY = Optional.of(attr));
                localAttributes.colorStart.ifPresent(attr -> newAttributes.colorStart = Optional.of(attr));
                localAttributes.colorEnd.ifPresent(attr -> newAttributes.colorEnd = Optional.of(attr));
                localAttributes.duration.ifPresent(attr -> newAttributes.duration = Optional.of(attr));
                localAttributes.count.ifPresent(attr -> newAttributes.count = Optional.of(attr));

                this.attributes = Optional.of(newAttributes);
            } else this.attributes = Optional.of(localAttributes);

        if (parent.left().get().attributes_variance.isPresent()) {
            localAttributesVariance = parent.left().get().attributes_variance.orElse(localAttributesVariance);
        }

        if (this.attributes_variance.isPresent()) {
                ParticleInstance newAttributes = this.attributes_variance.get();

                localAttributesVariance.x.ifPresent(attr -> newAttributes.x = Optional.of(attr));
                localAttributesVariance.y.ifPresent(attr -> newAttributes.y = Optional.of(attr));
                localAttributesVariance.speedX.ifPresent(attr -> newAttributes.speedX = Optional.of(attr));
                localAttributesVariance.speedY.ifPresent(attr -> newAttributes.speedY = Optional.of(attr));
                localAttributesVariance.accelerationX.ifPresent(attr -> newAttributes.accelerationX = Optional.of(attr));
                localAttributesVariance.accelerationY.ifPresent(attr -> newAttributes.accelerationY = Optional.of(attr));
                localAttributesVariance.frictionX.ifPresent(attr -> newAttributes.frictionX = Optional.of(attr));
                localAttributesVariance.frictionY.ifPresent(attr -> newAttributes.frictionY = Optional.of(attr));
                localAttributesVariance.colorStart.ifPresent(attr -> newAttributes.colorStart = Optional.of(attr));
                localAttributesVariance.colorEnd.ifPresent(attr -> newAttributes.colorEnd = Optional.of(attr));
                localAttributesVariance.duration.ifPresent(attr -> newAttributes.duration = Optional.of(attr));
                localAttributesVariance.count.ifPresent(attr -> newAttributes.count = Optional.of(attr));

                this.attributes_variance = Optional.of(newAttributes);
            } else this.attributes_variance = Optional.of(localAttributesVariance);


        if (this.particles.isEmpty()) this.particles = parent.left().get().particles;

        this.use = Optional.empty();

    }


//    String getName() {return this.name;}




}


