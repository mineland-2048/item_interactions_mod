package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.ColorRGBA;

import java.awt.*;

public class ParticleInstance {
    ResourceLocation id;
    public float x = 0f, y = 0f;
    public float speedX = 0f, speedY = 0f;
    public float accelerationX = 0f, accelerationY = 0f;
    public float frictionX = 0f, frictionY = 0f;
    public float duration = 1f;

    public ColorRGBA colorStart = new ColorRGBA(0xFFFFFFFF), colorEnd = new ColorRGBA(0xFFFFFFFF);

    public int count = 1    ;


    public static final Codec<ParticleInstance> CODEC = RecordCodecBuilder.create(particleInstanceInstance -> particleInstanceInstance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(p -> p.id),
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(p -> p.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(p -> p.y),
            Codec.FLOAT.optionalFieldOf("speedX", 0f).forGetter(p -> p.speedX),
            Codec.FLOAT.optionalFieldOf("speedY", 0f).forGetter(p -> p.speedY),
            Codec.FLOAT.optionalFieldOf("accelerationX", 0f).forGetter(p -> p.accelerationX),
            Codec.FLOAT.optionalFieldOf("accelerationY", 0f).forGetter(p -> p.accelerationY),
            Codec.FLOAT.optionalFieldOf("frictionX", 0f).forGetter(p -> p.frictionX),
            Codec.FLOAT.optionalFieldOf("frictionY", 0f).forGetter(p -> p.frictionY),
            Codec.FLOAT.optionalFieldOf("duration", 1f).forGetter(p -> p.duration),
            ColorRGBA.CODEC.optionalFieldOf("color_start", new ColorRGBA(0xFFFFFFFF)).forGetter(p -> p.colorStart),
            ColorRGBA.CODEC.optionalFieldOf("color_end", new ColorRGBA(0xFFFFFFFF)).forGetter(p -> p.colorEnd),
            Codec.INT.optionalFieldOf("count", 1).forGetter(p -> p.count)

            ).apply(particleInstanceInstance, ParticleInstance::new));

    public static final Codec<ParticleInstance> CONFIG_CODEC = RecordCodecBuilder.create(particleInstanceInstance -> particleInstanceInstance.group(
            Codec.FLOAT.optionalFieldOf("x", 0f).forGetter(p -> p.x),
            Codec.FLOAT.optionalFieldOf("y", 0f).forGetter(p -> p.y),
            Codec.FLOAT.optionalFieldOf("speedX", 0f).forGetter(p -> p.speedX),
            Codec.FLOAT.optionalFieldOf("speedY", 0f).forGetter(p -> p.speedY),
            Codec.FLOAT.optionalFieldOf("accelerationX", 0f).forGetter(p -> p.accelerationX),
            Codec.FLOAT.optionalFieldOf("accelerationY", 0f).forGetter(p -> p.accelerationY),
            Codec.FLOAT.optionalFieldOf("frictionX", 0f).forGetter(p -> p.frictionX),
            Codec.FLOAT.optionalFieldOf("frictionY", 0f).forGetter(p -> p.frictionY),
            Codec.FLOAT.optionalFieldOf("duration", 1f).forGetter(p -> p.duration),
            ColorRGBA.CODEC.optionalFieldOf("color_start", new ColorRGBA(0xFFFFFFFF)).forGetter(p -> p.colorStart),
            ColorRGBA.CODEC.optionalFieldOf("color_end", new ColorRGBA(0xFFFFFFFF)).forGetter(p -> p.colorEnd),
            Codec.INT.optionalFieldOf("count", 1).forGetter(p -> p.count)

            ).apply(particleInstanceInstance, ParticleInstance::new));

    public ParticleInstance() {
        this(0f,0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0);

    }

    public static ParticleInstance defaultVariance() {
        return new ParticleInstance(0f,0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0);
    }


    public ParticleInstance(float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, float duration, ColorRGBA colorStart, ColorRGBA colorEnd, int count) {
        this(null, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, duration, colorStart, colorEnd, count);
    }

    public ParticleInstance(ResourceLocation id, float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, float duration, ColorRGBA colorStart, ColorRGBA colorEnd, int count) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.frictionX = frictionX;
        this.frictionY = frictionY;
        this.duration = duration;

        if (colorStart == null && colorEnd != null) colorStart = colorEnd;
        else if (colorEnd == null && colorStart != null) colorEnd = colorStart;

        if (colorStart != null) {
            this.colorStart = colorStart;
            this.colorEnd = colorEnd;
        }
        this.count = count;
    }

    public void spawn(float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, float duration, ColorRGBA colorStart, ColorRGBA colorEnd, int count) {

    }

    public void spawn() {


        System.out.println("Spawned " + id);
    }

}
