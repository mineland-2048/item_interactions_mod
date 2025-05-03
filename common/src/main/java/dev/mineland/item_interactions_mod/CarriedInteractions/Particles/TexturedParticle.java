package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import dev.mineland.item_interactions_mod.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class TexturedParticle extends BaseParticle {

    int tintStart, tintEnd;
    TextureType textureType;

    JsonObject textureMcMeta;
    ResourceLocation particleLocation;

    List<ResourceLocation> frames;

    int width, height;
    double frametime;
    int textureIndex;
    boolean interpolate;
    int length;

    double totalLifeTime;

    boolean isMCMetaAnimatedTexture;


    public enum TextureType {
        STATIC(0),
        FRAMETIME(1),
        LIFETIME(2);

        final int typeId;
        TextureType(int i) {
            typeId = i;
        }
    }

    public TexturedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double frictionX, double frictionY, double lifeTime, ResourceLocation particleLocation, int tintStart, int tintEnd) {
        this(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeTime, particleLocation, TextureType.LIFETIME, tintStart, tintEnd);
    }

    public TexturedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double frictionX, double frictionY, double lifeTime, ResourceLocation particleLocation, TextureType textureType,  int tintStart, int tintEnd) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeTime);

        this.tintStart = tintStart;
        this.tintEnd = tintEnd;
        this.particleLocation = particleLocation;
        this.textureType = textureType;

        this.totalLifeTime = lifeTime;


//        Legacy code for .mcmeta particles. Id rather not
//        try {
//            Resource resource = Minecraft.getInstance().getResourceManager().getResource(particleLocation.withSuffix(".mcmeta" )).orElse(null);
//
//            if (resource != null) {
//                try (Reader reader = new InputStreamReader(resource.open())) {
//                    this.textureMcMeta = JsonParser.parseReader(reader).getAsJsonObject().get("animation").getAsJsonObject();
//
//                    this.interpolate = textureMcMeta.get("interpolate") != null && textureMcMeta.get("interpolate").getAsBoolean();
//
//                    this.width = textureMcMeta.get("width") != null ?
//                            textureMcMeta.get("width").getAsInt() :
//                            Minecraft.getInstance().getTextureManager().getTexture(particleLocation).getTexture().getWidth(0);
//
//
//
//                    this.height = textureMcMeta.get("height") != null ?
//                            textureMcMeta.get("height").getAsInt() :
//                            this.width;
//
//
//                    this.length = Minecraft.getInstance().getTextureManager().getTexture(particleLocation).getTexture().getHeight(0) /  Minecraft.getInstance().getTextureManager().getTexture(particleLocation).getTexture().getWidth(0);
////                    this.frames.add(particleLocation);
//                    this.isMCMetaAnimatedTexture = true;
//
//                }
//            }
//        } catch (Exception e) {
////            Item_interactions_mod.warnMessage("Couldnt load particle texture '" + textureLocation + "' \n" + e);
//            this.textureMcMeta = null;
//            this.width = Minecraft.getInstance().getTextureManager().getTexture(particleLocation).getTexture().getWidth(0);
//            this.height = Minecraft.getInstance().getTextureManager().getTexture(particleLocation).getTexture().getHeight(0);
//            this.interpolate = false;
//            this.length = 0;
//            this.textureType = TextureType.STATIC;
//
//        }

        this.frames = getTexturesFromArray(particleLocation);


    }


    private List<ResourceLocation> getTexturesFromArray(ResourceLocation particleLocation) {
        try {
            Resource a = Minecraft.getInstance().getResourceManager().getResource(particleLocation).orElseThrow();
            List<ResourceLocation> finalList = new ArrayList<>();


            JsonElement json = JsonParser.parseReader(a.openAsReader());

            for(JsonElement textureJson : json.getAsJsonObject().get("textures").getAsJsonArray()) {
                ResourceLocation raw = ResourceLocation.parse(textureJson.getAsString());

//                If it sets a different texture then use that instead of parsing from just particles
                String path = raw.getPath().startsWith("textures/") ?
                        raw.getPath() : "textures/particle/" + raw.getPath();

//                If it doesnt end in .png then add it
                path = path.endsWith(".png") ? path : path + ".png";
                finalList.add(ResourceLocation.fromNamespaceAndPath(raw.getNamespace(), path));
            }

            this.length = finalList.size();

            this.isMCMetaAnimatedTexture = false;
            return finalList;

        } catch (Exception e) {
//            Item_interactions_mod.warnMessage("died from getting vanilla texture (" + particleLocation + "): " + e);

            List<ResourceLocation> r = new ArrayList<>();
            r.add(ResourceLocation.parse("minecraft:textures/missingno.png"));
            return r;
        }
    }




    public void render() {
        super.render();

        this.particleLocation = this.frames.get(textureIndex);

        int totalTextureHeight = Minecraft.getInstance().getTextureManager().getTexture(this.particleLocation).getTexture().getHeight(0);
        int totalTextureWidth = Minecraft.getInstance().getTextureManager().getTexture(this.particleLocation).getTexture().getWidth(0);


        int yStart = 0;

        int uvHeight = totalTextureHeight;

        switch(this.textureType) {
            case STATIC -> {

            }

            case LIFETIME -> {
                if (length == 0) break;
                int index = (int) Math.floor((lifeTime / maxTick) * length);
                textureIndex = Math.clamp(index, 0, length - 1);


//                if (!isMCMetaAnimatedTexture) break;
//                uvHeight = height;
//
//                yStart = textureIndex * height;

            }

            case FRAMETIME -> {
                if (lifeTime % frametime == 0) {
                    textureIndex = (textureIndex + 1) % (length + 1);
                }

//                uvHeight = height;
//
//                if (!isMCMetaAnimatedTexture) break;
//                yStart = textureIndex * height;


            }
        }

        int finalColor = MiscUtils.colorLerp((float) ((lifeTime / maxTick)), this.tintStart, this.tintEnd);

        this.guiGraphics.blit(RenderType::guiTextured, this.frames.get(textureIndex),
                (int) this.x - (totalTextureWidth/2), (int) this.y - (uvHeight/2),
                0f, yStart,
                totalTextureWidth, uvHeight,
                totalTextureWidth, totalTextureHeight,
                finalColor);



//        this.guiGraphics.blit();
    }

    public void tick() {
        super.tick();
        this.x += speedX / 20;
        this.y += speedY / 20;
        this.speedX = (speedX + accelerationX) * frictionX;
        this.speedY = (speedY + accelerationY) * frictionY;

    }
}
