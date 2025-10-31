package dev.mineland.item_interactions_mod.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xyz.trivaxy.tia.MixinInjects;

@Mixin(MixinInjects.class)
public interface TIAMixinInjectsMixinAccessor {

//    @Inject(method = "applyScaleForMouseItem", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix3x2fStack;scale(FF)Lorg/joml/Matrix3x2f;"), require = 0)
//    public static void updateTIACarriedAnimationProgress(Matrix3x2fStack pose, CallbackInfo ci) {
//    }


    @Accessor(value = "carriedAnimationProgress", remap = false)
    static float item_interactions_mod$getTIACarriedAnimationProgress() {
        throw new AssertionError();
    }


}
