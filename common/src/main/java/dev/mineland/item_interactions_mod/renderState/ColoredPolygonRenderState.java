package dev.mineland.item_interactions_mod.renderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
//~ if >= 26.1 'net.minecraft.client.gui.render.state.GuiElementRenderState' -> 'net.minecraft.client.renderer.state.gui.GuiElementRenderState'
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

//@Environment(EnvType.CLIENT)
public record ColoredPolygonRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        Vector2f tl,
        Vector2f tr,
        Vector2f bl,
        Vector2f br,
        int col1,
        int col2,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    public ColoredPolygonRenderState(
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            Matrix3x2f pose,
            Vector2f tl,
            Vector2f tr,
            Vector2f bl,
            Vector2f br,
            int col1,
            int col2,
            @Nullable ScreenRectangle screenRectangle
    ) {
        this(pipeline, textureSetup, pose, tl, tr, bl, br, col1, col2, screenRectangle, getBounds(tl, tr, bl, br, pose, screenRectangle));
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer) {
        vertexConsumer.addVertexWith2DPose(this.pose(), this.br.x(), this.br.y()).setColor(this.col1());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.tr.x(), this.tr.y()).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.tl.x(), this.tl.y()).setColor(this.col2());
        vertexConsumer.addVertexWith2DPose(this.pose(), this.bl.x(), this.bl.y()).setColor(this.col1());
//        vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1()).setColor(this.col2());
//        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1()).setColor(this.col2());
//        vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0()).setColor(this.col1());
    }

    @Nullable
    private static ScreenRectangle getBounds(Vector2f tl, Vector2f tr, Vector2f bl, Vector2f br, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {


        int left = (int) Math.min(tl.x, bl.x);
        int right = (int) Math.max(tr.x, br.x);

        int top = (int) Math.min(tl.y, tr.y);
        int bottom = (int) Math.min(bl.y, br.y);




        ScreenRectangle screenRectangle2 = new ScreenRectangle(left, top, right, bottom).transformMaxBounds(matrix3x2f);
        return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
    }
}
