package dev.mineland.item_interactions_mod.modcompat;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mineland.item_interactions_mod.mixin.compat.TIAMixinInjectsMixinAccessor;
import xyz.trivaxy.tia.ModConfigs;

public class TinyItemAnimationsCompat {

    public static void applyTIAMouseScaling(PoseStack pose) {
        var carriedAnimationProgress = TIAMixinInjectsMixinAccessor.item_interactions_mod$getTIACarriedAnimationProgress();
        float scale = 1f + (ModConfigs.pickupScale - 1f) * (1 - (float)Math.pow(1 - carriedAnimationProgress, 5));

//        pose.translate(-scale/2, -scale/2, 0);
        pose.scale(scale, scale, 1);
    }
}
