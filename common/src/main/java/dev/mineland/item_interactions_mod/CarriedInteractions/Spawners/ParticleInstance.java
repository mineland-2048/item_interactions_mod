package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.ColorRGBA;

import java.awt.*;
import java.util.Optional;

public class ParticleInstance {
    ResourceLocation id;
    public Optional<Float>  x, y,
                            speedX, speedY,
                            accelerationX, accelerationY,
                            frictionX, frictionY,
                            duration;

    public Optional<ColorRGBA> colorStart , colorEnd;

    public Optional<Integer> count;


    public static final Codec<ParticleInstance> CODEC = RecordCodecBuilder.create(particleInstanceInstance -> particleInstanceInstance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(p -> p.id),
            Codec.FLOAT.optionalFieldOf("x").forGetter(p -> p.x),
            Codec.FLOAT.optionalFieldOf("y").forGetter(p -> p.y),
            Codec.FLOAT.optionalFieldOf("speedX").forGetter(p -> p.speedX),
            Codec.FLOAT.optionalFieldOf("speedY").forGetter(p -> p.speedY),
            Codec.FLOAT.optionalFieldOf("accelerationX").forGetter(p -> p.accelerationX),
            Codec.FLOAT.optionalFieldOf("accelerationY").forGetter(p -> p.accelerationY),
            Codec.FLOAT.optionalFieldOf("frictionX").forGetter(p -> p.frictionX),
            Codec.FLOAT.optionalFieldOf("frictionY").forGetter(p -> p.frictionY),
            ColorRGBA.CODEC.optionalFieldOf("color_start").forGetter(p -> p.colorStart),
            ColorRGBA.CODEC.optionalFieldOf("color_end").forGetter(p -> p.colorEnd),
            Codec.FLOAT.optionalFieldOf("duration").forGetter(p -> p.duration),
            Codec.INT.optionalFieldOf("count").forGetter(p -> p.count)

            ).apply(particleInstanceInstance, ParticleInstance::new));

    public static final Codec<ParticleInstance> CONFIG_CODEC = RecordCodecBuilder.create(particleInstanceInstance -> particleInstanceInstance.group(
            Codec.FLOAT.optionalFieldOf("x").forGetter(p -> p.x),
            Codec.FLOAT.optionalFieldOf("y").forGetter(p -> p.y),
            Codec.FLOAT.optionalFieldOf("speedX").forGetter(p -> p.speedX),
            Codec.FLOAT.optionalFieldOf("speedY").forGetter(p -> p.speedY),
            Codec.FLOAT.optionalFieldOf("accelerationX").forGetter(p -> p.accelerationX),
            Codec.FLOAT.optionalFieldOf("accelerationY").forGetter(p -> p.accelerationY),
            Codec.FLOAT.optionalFieldOf("frictionX").forGetter(p -> p.frictionX),
            Codec.FLOAT.optionalFieldOf("frictionY").forGetter(p -> p.frictionY),
            ColorRGBA.CODEC.optionalFieldOf("color_start").forGetter(p -> p.colorStart),
            ColorRGBA.CODEC.optionalFieldOf("color_end").forGetter(p -> p.colorEnd),
            Codec.FLOAT.optionalFieldOf("duration").forGetter(p -> p.duration),
            Codec.INT.optionalFieldOf("count").forGetter(p -> p.count)

            ).apply(particleInstanceInstance, ParticleInstance::new));

    public ParticleInstance() {
        this(0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0f, 0);

    }

    public ParticleInstance(Optional<Float> x, Optional<Float> y, Optional<Float> speedX, Optional<Float> speedY, Optional<Float> accelerationX, Optional<Float> accelerationY, Optional<Float> frictionX, Optional<Float> frictionY, Optional<ColorRGBA> colorStart, Optional<ColorRGBA> colorEnd, Optional<Float> duration, Optional<Integer> count) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.frictionX = frictionX;
        this.frictionY = frictionY;

        if (colorStart.isEmpty() && colorEnd.isPresent()) colorStart = colorEnd;
        else if (colorEnd.isEmpty() && colorStart.isPresent()) colorEnd = colorStart;

        this.colorStart = colorStart;
        this.colorEnd = colorEnd;


        this.duration = duration;
        this.count = count;
    }

    public static ParticleInstance defaultVariance() {
        return new ParticleInstance(0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0f, 0);
    }


    public ParticleInstance(float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, ColorRGBA colorStart, ColorRGBA colorEnd, float duration, int count) {
        this(null, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, colorStart, colorEnd, duration, count);
    }

    public ParticleInstance(ResourceLocation id, Optional<Float> x, Optional<Float> y, Optional<Float> speedX, Optional<Float> speedY, Optional<Float> accelerationX, Optional<Float> accelerationY, Optional<Float> frictionX, Optional<Float> frictionY, Optional<ColorRGBA> colorStart, Optional<ColorRGBA> colorEnd, Optional<Float> duration, Optional<Integer> count) {
        this(x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, colorStart, colorEnd, duration, count);

        this.id = id;

    }

    public ParticleInstance(ResourceLocation id, float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, ColorRGBA colorStart, ColorRGBA colorEnd, float duration, int count) {
        this(id, Optional.of(x), Optional.of(y), Optional.of(speedX), Optional.of(speedY), Optional.of(accelerationX), Optional.of(accelerationY), Optional.of(frictionX), Optional.of(frictionY), Optional.of(colorStart), Optional.of(colorEnd), Optional.of(duration), Optional.of(count));
    }

    public void spawn(float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, float duration, ColorRGBA colorStart, ColorRGBA colorEnd, int count) {

    }

    public void spawn() {


        System.out.println("Spawned " + id);
    }

}
