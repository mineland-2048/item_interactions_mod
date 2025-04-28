package dev.mineland.item_interactions_mod;

import dev.mineland.item_interactions_mod.CarriedInteractions.Particles.BaseParticle;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GuiParticleRegistry {
    private static final Map<ResourceLocation, BaseParticle> PARTICLES = new HashMap<>();


    public static void register(ResourceLocation id, BaseParticle particle) {
        PARTICLES.put(id, particle);
    }

    public static BaseParticle get(ResourceLocation id) {
        return PARTICLES.get(id);
    }

    public static Collection<BaseParticle> getAll() {
        return PARTICLES.values();
    }

    static void clear() {
        PARTICLES.clear();
    }
}
