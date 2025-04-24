package dev.mineland.item_interactions_mod.CarriedInteractions.Spawners;

import net.minecraft.world.item.Items;

public class OakLeavesParticleSpawner extends FallingLeaves {
    public OakLeavesParticleSpawner(int id) {
        super(id, Items.OAK_LEAVES.toString(), 0xFF108000);
    }

    public OakLeavesParticleSpawner() {
        super(Items.OAK_LEAVES.toString(), 0xFF108000);
    }
}
