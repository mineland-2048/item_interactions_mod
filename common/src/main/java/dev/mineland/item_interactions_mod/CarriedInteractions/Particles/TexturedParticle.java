package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStreamReader;
import java.io.Reader;

public class TexturedParticle extends BaseParticle {

    int tint;
    TextureType textureType;

    JsonObject textureMcMeta;
    ResourceLocation textureLocation;

    int width, height;
    double frametime;
    int textureIndex;
    boolean interpolate;
    int length;

    int xOffset = 0;
    int yOffset = 0;
    public enum TextureType {
        STATIC(0),
        FRAMETIME(1),
        LIFETIME(2);

        final int typeId;
        TextureType(int i) {
            typeId = i;
        }
    }

    public TexturedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double lifeTime, ResourceLocation textureLocation,  int tint) {
        this(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, lifeTime, textureLocation, TextureType.STATIC, tint);
    }

    public TexturedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double lifeTime, ResourceLocation textureLocation, TextureType textureType,  int tint) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, lifeTime);

        this.tint = tint;
        this.textureLocation = textureLocation;
        this.textureType = textureType;


        try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(textureLocation.withSuffix(".mcmeta" )).orElse(null);

            if (resource != null) {
                try (Reader reader = new InputStreamReader(resource.open())) {
                    this.textureMcMeta = JsonParser.parseReader(reader).getAsJsonObject().get("animation").getAsJsonObject();

                    this.interpolate = textureMcMeta.get("interpolate") != null && textureMcMeta.get("interpolate").getAsBoolean();

                    this.width = textureMcMeta.get("width") != null ?
                            textureMcMeta.get("width").getAsInt() :
                            Minecraft.getInstance().getTextureManager().getTexture(textureLocation).getTexture().getWidth(0);



                    this.height = textureMcMeta.get("height") != null ?
                            textureMcMeta.get("height").getAsInt() :
                            this.width;


                    this.xOffset = -this.width / 2;
                    this.yOffset = -this.height / 2;

                    this.length =Minecraft.getInstance().getTextureManager().getTexture(textureLocation).getTexture().getHeight(0) /  Minecraft.getInstance().getTextureManager().getTexture(textureLocation).getTexture().getWidth(0);

                }
            }
        } catch (Exception e) {
//            Item_interactions_mod.warnMessage("Couldnt load particle texture '" + textureLocation + "' \n" + e);
            this.textureMcMeta = null;
            this.width = Minecraft.getInstance().getTextureManager().getTexture(textureLocation).getTexture().getWidth(0);
            this.height = Minecraft.getInstance().getTextureManager().getTexture(textureLocation).getTexture().getHeight(0);
            this.interpolate = false;
            this.length = 0;
            this.textureType = TextureType.STATIC;

        }
    }




    public void render() {
        super.render();


        int totalTextureHeight = Minecraft.getInstance().getTextureManager().getTexture(this.textureLocation).getTexture().getHeight(0);
        int totalTextureWidth = Minecraft.getInstance().getTextureManager().getTexture(this.textureLocation).getTexture().getWidth(0);


        int yStart = 0;

        int uvHeight = totalTextureHeight;

        switch(this.textureType) {
            case STATIC -> {

            }

            case LIFETIME -> {
                int index = (int) Math.floor((lifeTime / maxTick) * length);
                textureIndex = index % length;

                uvHeight = height;

                yStart = textureIndex * height;

            }

            case FRAMETIME -> {
                if (lifeTime % frametime == 0) {
                    textureIndex = (textureIndex + 1) % length;
                }

                uvHeight = height;
                yStart = textureIndex * height;


            }
        }


        this.guiGraphics.blit(RenderType::guiTextured, this.textureLocation,
                (int) this.x + xOffset, (int) this.y + yOffset,
                0f, yStart,
                totalTextureWidth, uvHeight,
                totalTextureWidth, totalTextureHeight,
                this.tint);



//        this.guiGraphics.blit();
    }
}
