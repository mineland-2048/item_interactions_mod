package dev.mineland.item_interactions_mod.CarriedInteractions.Particles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mineland.item_interactions_mod.Item_interactions_mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

public class CopiedParticle extends TexturedParticle {
    public CopiedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double frictionX, double frictionY, double lifeTime, ResourceLocation textureLocation, int tintStart, int tintEnd) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeTime, getTextureFromVanillaParticle(textureLocation), tintStart, tintEnd);
    }

    public CopiedParticle(GuiGraphics guiGraphics, double x, double y, double speedX, double speedY, double accelerationX, double accelerationY, double frictionX, double frictionY, double lifeTime, ResourceLocation textureLocation, TextureType textureType, int tintStart, int tintEnd) {
        super(guiGraphics, x, y, speedX, speedY, accelerationX, accelerationY, frictionX, frictionY, lifeTime, getTextureFromVanillaParticle(textureLocation), textureType, tintStart, tintEnd);
    }


    private static ResourceLocation getTextureFromVanillaParticle(ResourceLocation resourceLocation) {
        try {
            Resource a = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).orElseThrow();


            JsonElement json = JsonParser.parseReader(a.openAsReader());
            ResourceLocation s = ResourceLocation.parse(json.getAsJsonObject().get("textures").getAsJsonArray().get(0).getAsString());
            s = ResourceLocation.fromNamespaceAndPath(s.getNamespace(), "textures/particle/" + s.getPath() + ".png");

            return s;

        } catch (Exception e) {
            Item_interactions_mod.warnMessage("died from getting vanilla texture (" + resourceLocation + "): " + e);
            return resourceLocation;
        }
    }
}
