package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.TexturedParticle;
import dev.mineland.item_interactions_mod.GlobalDirt;
import dev.mineland.item_interactions_mod.GuiRendererHelper;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import dev.mineland.item_interactions_mod.MiscUtils;
import dev.mineland.item_interactions_mod.backport.ColorRGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Optional;

public class ParticleInstance {
    ResourceLocation id;
    public Optional<Float>  x, y,
                            speedX, speedY,
                            accelerationX, accelerationY,
                            frictionX, frictionY,
                            duration;

    public Optional<ColorRGBA> colorStart , colorEnd;


    public Optional<Float> brightnessStart, brightnessEnd;

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
            Codec.FLOAT.optionalFieldOf("brightness_start").forGetter(p -> p.brightnessStart),
            Codec.FLOAT.optionalFieldOf("brightness_end").forGetter(p -> p.brightnessEnd),
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
            Codec.FLOAT.optionalFieldOf("brightness_start").forGetter(p -> p.brightnessStart),
            Codec.FLOAT.optionalFieldOf("brightness_end").forGetter(p -> p.brightnessEnd),
            Codec.FLOAT.optionalFieldOf("duration").forGetter(p -> p.duration),
            Codec.INT.optionalFieldOf("count").forGetter(p -> p.count)

            ).apply(particleInstanceInstance, ParticleInstance::new));

    public ParticleInstance() {
//        this(0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0f, 0);
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),Optional.empty(),Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    }

    public ParticleInstance(Optional<Float> x, Optional<Float> y, Optional<Float> speedX, Optional<Float> speedY, Optional<Float> accelerationX, Optional<Float> accelerationY, Optional<Float> frictionX, Optional<Float> frictionY, Optional<ColorRGBA> colorStart, Optional<ColorRGBA> colorEnd, Optional<Float> brightnessStart, Optional<Float> brightnessEnd, Optional<Float> duration, Optional<Integer> count) {
        this(null, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, colorStart, colorEnd, brightnessStart, brightnessEnd, duration, count);
    }

    public static ParticleInstance defaultVariance() {
        return new ParticleInstance(0f,0f,0f,0f,0f,0f,0f,0f,new ColorRGBA(0), new ColorRGBA(0), 0f, 0f, 0f, 0);
    }


    public ParticleInstance(float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, ColorRGBA colorStart, ColorRGBA colorEnd, float brightnessStart, float brightnessEnd, float duration, int count) {
        this(null, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, colorStart, colorEnd, brightnessStart, brightnessEnd, duration, count);
    }

    public ParticleInstance(ResourceLocation id, Optional<Float> x, Optional<Float> y, Optional<Float> speedX, Optional<Float> speedY, Optional<Float> accelerationX, Optional<Float> accelerationY, Optional<Float> frictionX, Optional<Float> frictionY, Optional<ColorRGBA> colorStart, Optional<ColorRGBA> colorEnd, Optional<Float> brightnessStart, Optional<Float> brightnessEnd, Optional<Float> duration, Optional<Integer> count) {

        if (id != null) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            ResourceLocation fixedPath = id.withPath("particles/" + id.getPath() + ".json");

            Optional<Resource> particleResource =  resourceManager.getResource(fixedPath);

            if (particleResource.isEmpty()) {
                if (!GlobalDirt.particleErrorList.containsKey(id)) GlobalDirt.particleErrorList.put(id, new ArrayList<>());
                if (!GlobalDirt.particleErrorList.get(id).contains("m")) {
                    GlobalDirt.particleErrorList.get(id).add("m");

                    String err = "Missing GUI particle: '" + id + "'";
                    if (fixedPath.getPath().endsWith(".json.json")) err = "particle '" + id + " shouldn't end with .json in the spawner, just the location of the particle";
                    if (fixedPath.getPath().startsWith("particles/particles/")) err = "particle '" + id + "' shouldn't start the path with 'particles/'. Remove it";

                    Item_interactions_mod.warnMessage("[" + GlobalDirt.currentParticleSpawner + "] " + err);
                }

            } else {
                try (Reader reader = particleResource.get().openAsReader()){
                    JsonElement particleJson = JsonParser.parseReader(reader);
                    JsonArray textureList = particleJson.getAsJsonObject().get("textures").getAsJsonArray();

                    for (JsonElement textureLocationJson : textureList.asList()) {
                        ResourceLocation textureLocation = ResourceLocation.tryParse(textureLocationJson.getAsString());
                        textureLocation = new ResourceLocation(textureLocation.getNamespace(), "textures/particle/" + textureLocation.getPath() + ".png");
                        if (resourceManager.getResource(textureLocation).isEmpty() || !GuiRendererHelper.setParticleSizeCache(textureLocation)) {
                            if (!GlobalDirt.particleErrorList.containsKey(id)) GlobalDirt.particleErrorList.put(id, new ArrayList<>());

                            GlobalDirt.particleErrorList.get(id).add("Missing texture " + textureLocation);
                            Item_interactions_mod.warnMessage("[" + GlobalDirt.currentParticleSpawner + "]: Missing texture '" + textureLocation + "'");

                        }





                    }


                } catch (Exception e) {
                    Item_interactions_mod.errorMessage("Failed to parse GUI particle '" + fixedPath + "': " + e);
                    return;
                }
            }
        }
        this.id = id;
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

        if (brightnessStart.isEmpty() && brightnessEnd.isPresent()) brightnessStart = brightnessEnd;
        else if (brightnessEnd.isEmpty() && brightnessStart.isPresent()) brightnessEnd = brightnessStart;

        this.brightnessStart = brightnessStart;
        this.brightnessEnd = brightnessEnd;


        this.duration = duration;
        this.count = count;


    }

    public ParticleInstance(ResourceLocation id, float x, float y, float speedX, float speedY, float accelerationX, float accelerationY, float frictionX, float frictionY, ColorRGBA colorStart, ColorRGBA colorEnd, float brightnessStart, float brightnessEnd, float duration, int count) {
        this(id, Optional.of(x), Optional.of(y), Optional.of(speedX), Optional.of(speedY), Optional.of(accelerationX), Optional.of(accelerationY), Optional.of(frictionX), Optional.of(frictionY), Optional.of(colorStart), Optional.of(colorEnd), Optional.of(brightnessStart), Optional.of(brightnessEnd), Optional.of(duration), Optional.of(count));
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
        float brightnessStartVar = attributes_variance.brightnessStart.orElse(0f);
        float brightnessEndVar = attributes_variance.brightnessEnd.orElse(0f);

        double x = this.x.orElse(attributes.x.orElse(0f));
        double y = this.y.orElse(attributes.y.orElse(0f));
        double speedX = this.speedX.orElse(attributes.speedX.orElse(0f));
        double speedY = this.speedY.orElse(attributes.speedY.orElse(0f));
        double accelerationX = this.accelerationX.orElse(attributes.accelerationX.orElse(0f));
        double accelerationY = this.accelerationY.orElse(attributes.accelerationY.orElse(0f));
        double frictionX = this.frictionX.orElse(attributes.frictionX.orElse(1f));
        double frictionY = this.frictionY.orElse(attributes.frictionY.orElse(1f));

        float brightnessStart = this.brightnessStart.orElse(attributes.brightnessStart.orElse(1f));
        float brightnessEnd = this.brightnessEnd.orElse(attributes.brightnessEnd.orElse(1f));


        int[] colorStart = MiscUtils.int2Array(this.colorStart.orElse(attributes.colorStart.orElse(new ColorRGBA(0xffffffff))).rgba());
        int[] colorEnd = MiscUtils.int2Array(this.colorEnd.orElse(attributes.colorEnd.orElse(new ColorRGBA(0xffffffff))).rgba());

        int[] colorStartVar = MiscUtils.int2Array(attributes_variance.colorStart.orElse(new ColorRGBA(0)).rgba());
        int[] colorEndVar = MiscUtils.int2Array(attributes_variance.colorEnd.orElse(new ColorRGBA(0)).rgba());

        colorStart = MiscUtils.applyBrightness(colorStart, MiscUtils.randomVariance(brightnessStart, brightnessStartVar));
        colorEnd = MiscUtils.applyBrightness(colorEnd, MiscUtils.randomVariance(brightnessEnd, brightnessEndVar));

//        Y axis is negated since guiGraphics is from top to bottom instead of bottom to top
        double pX = MiscUtils.randomVariance(spawnX + x, xVar) ;
        double pY = MiscUtils.randomVariance(spawnY - y, yVar) ;
        double pSpeedX = MiscUtils.randomVariance(spawnSpeedX + speedX, speedXVar) ;
        double pSpeedY = MiscUtils.randomVariance(spawnSpeedY - speedY, speedYVar) ;
        double pAccX = MiscUtils.randomVariance(accelerationX, accelerationXVar) ;
        double pAccY = MiscUtils.randomVariance(-accelerationY, accelerationYVar) ;
        double pFrictX = MiscUtils.clamp(MiscUtils.randomVariance(frictionX, frictionXVar), 0, 1) ;
        double pFrictY = MiscUtils.clamp(MiscUtils.randomVariance(frictionY, frictionYVar), 0, 1) ;

        int[] pcStart = new int[4];
        int[] pcEnd = new int[4];

        for (int i = 0; i < colorStartVar.length; i++) {
            pcStart[i] = (int) MiscUtils.clamp(MiscUtils.randomVariance(colorStart[i], colorStartVar[i]), 0, 255);
            pcEnd[i] = (int) MiscUtils.clamp(MiscUtils.randomVariance(colorEnd[i], colorEndVar[i]), 0, 255);
        }


//        int count = count.orElse(1);
        double pLifetime = MiscUtils.randomVariance(duration.orElse(attributes.duration.orElse(20f)), attributes_variance.duration.orElse(0f));

        new TexturedParticle(guiGraphics, pX, pY, pSpeedX, pSpeedY, pAccX, pAccY, pFrictX, pFrictY, pLifetime, this.id, MiscUtils.array2Int(pcStart), MiscUtils.array2Int(pcEnd));


    }


    public ParticleInstance copy() {
        return new ParticleInstance(this.x, this.y, this.speedX, this.speedY, this.accelerationX, this.accelerationY, this.frictionX, this.frictionY, this.colorStart, this.colorEnd, this.brightnessStart, this.brightnessEnd, this.duration, this.count);
    }

}
