package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.CopiedParticle;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.TexturedParticle;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
//        this(0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0f, 0);
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

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

    public void spawn(GuiGraphics guiGraphics, float spawnX, float spawnY, float spawnSpeedX, float spawnSpeedY, ParticleInstance attributes, ParticleInstance attributes_variance) {


        double xVar = attributes_variance.x.orElse(0f);
        double yVar = attributes_variance.y.orElse(0f);
        double speedXVar = attributes_variance.speedX.orElse(0f);
        double speedYVar = attributes_variance.speedY.orElse(0f);
        double accelerationXVar = attributes_variance.accelerationX.orElse(0f);
        double accelerationYVar = attributes_variance.accelerationY.orElse(0f);
        double frictionXVar = attributes_variance.frictionX.orElse(0f);
        double frictionYVar = attributes_variance.frictionY.orElse(0f);

        double x = this.x.orElse(attributes.x.orElse(0f));
        double y = this.y.orElse(attributes.y.orElse(0f));
        double speedX = this.speedX.orElse(attributes.speedX.orElse(0f));
        double speedY = this.speedY.orElse(attributes.speedY.orElse(0f));
        double accelerationX = this.accelerationX.orElse(attributes.accelerationX.orElse(0f));
        double accelerationY = this.accelerationY.orElse(attributes.accelerationY.orElse(0f));
        double frictionX = this.frictionX.orElse(attributes.frictionX.orElse(1f));
        double frictionY = this.frictionY.orElse(attributes.frictionY.orElse(1f));

        int[] colorStart = MiscUtils.int2Array(this.colorStart.orElse(attributes.colorStart.orElse(new ColorRGBA(0xffffffff))).rgba());
        int[] colorEnd = MiscUtils.int2Array(this.colorEnd.orElse(attributes.colorEnd.orElse(new ColorRGBA(0xffffffff))).rgba());

        int[] colorStartVar = MiscUtils.int2Array(attributes_variance.colorStart.orElse(new ColorRGBA(0)).rgba());
        int[] colorEndVar = MiscUtils.int2Array(attributes_variance.colorEnd.orElse(new ColorRGBA(0)).rgba());


        double pX = MiscUtils.randomVariance(spawnX + x, xVar) ;
        double pY = MiscUtils.randomVariance(spawnY + y, yVar) ;
        double pSpeedX = MiscUtils.randomVariance(spawnSpeedX + speedX, speedXVar) ;
        double pSpeedY = MiscUtils.randomVariance(spawnSpeedY + speedY, speedYVar) ;
        double pAccX = MiscUtils.randomVariance(accelerationX, accelerationXVar) ;
        double pAccY = MiscUtils.randomVariance(accelerationY, accelerationYVar) ;
        double pFrictX = Math.clamp(MiscUtils.randomVariance(frictionX, frictionXVar), 0, 1) ;
        double pFrictY = Math.clamp(MiscUtils.randomVariance(frictionY, frictionYVar), 0, 1) ;

        int[] pcStart = new int[4];
        int[] pcEnd = new int[4];

        for (int i = 0; i < colorStartVar.length; i++) {
            pcStart[i] = (int) Math.clamp(MiscUtils.randomVariance(colorStart[i], colorStartVar[i]), 0, 255);
            pcEnd[i] = (int) Math.clamp(MiscUtils.randomVariance(colorEnd[i], colorEndVar[i]), 0, 255);
        }


//        int count = count.orElse(1);
        double pLifetime = MiscUtils.randomVariance(duration.orElse(attributes.duration.orElse(20f)), attributes_variance.duration.orElse(0f));

        new TexturedParticle(guiGraphics, pX, pY, pSpeedX, pSpeedY, pAccX, pAccY, pFrictX, pFrictY, pLifetime, this.id, MiscUtils.array2Int(pcStart), MiscUtils.array2Int(pcEnd));


    }

    public void spawn() {


        System.out.println("Spawned " + id);
    }

}
