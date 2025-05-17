package dev.mineland.item_interactions_mod.renderState;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record GuiFloatingItemRenderState (
        ItemStackRenderState renderState,
        Vector3f translation,
        Quaternionf rotation,
        @Nullable Quaternionf overrideCameraAngle,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds,
        PoseStack poseStack
) implements PictureInPictureRenderState {
    public GuiFloatingItemRenderState(
            ItemStackRenderState itemStackRenderState,
            Vector3f translation,
            Quaternionf rotation,
            @Nullable Quaternionf overrideCameraAngle,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea,
            PoseStack pose
    ) {
        this(itemStackRenderState, translation, rotation, overrideCameraAngle, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea), pose);
    }

    public GuiFloatingItemRenderState(
        ItemStackRenderState renderState, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds, PoseStack poseStack
    )
    {
        this.renderState = renderState;
        this.translation = translation;
        this.rotation = rotation;
        this.overrideCameraAngle = overrideCameraAngle;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.scale = scale;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
        this.poseStack = poseStack;
    }

    public ItemStackRenderState renderState() { return this.renderState; }


}
