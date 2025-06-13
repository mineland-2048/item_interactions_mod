package dev.mineland.item_interactions_mod.CustomGuiComponents;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SteppedSliderButton extends AbstractWidget {
    private static final ResourceLocation SLIDER_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider");
    private static final ResourceLocation HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
    private static final ResourceLocation SLIDER_HANDLE_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle");
    private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");
    protected static final int TEXT_MARGIN = 2;
    private static final int HANDLE_WIDTH = 8;
    private static final int HANDLE_HALF_WIDTH = 4;

    public double minValue, maxValue;
    public int steps;

    private double range;
    private int selectedStep;
    private boolean canChangeValue;
    public boolean divideSteps;

    private double handlePosition;
    public double value;

    public SteppedSliderButton(int left, int top, int width, int height, Component message, double initialValue) {
        this(left, top, width, height, message, initialValue, 0, 1, 0, false);

    }
    public SteppedSliderButton(int left, int top, int width, int height, Component message, double initialValue, double minValue, double maxValue) {
        this(left, top, width, height, message, initialValue, minValue, maxValue, 0, false);

    }
    public SteppedSliderButton(int left, int top, int width, int height, Component message, double initialValue, double minValue, double maxValue, int steps) {
        this(left, top, width, height, message, initialValue, minValue, maxValue, steps, false);
    }

//    Main method
    public SteppedSliderButton(int left, int top, int width, int height, Component message, double initialValue, double minValue, double maxValue, int steps, boolean divideSteps) {
        super(left, top, width, height, message);
//        this.value = (double) Math.round(initialValue * 10) / 10;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.steps = steps;
        this.range = maxValue - minValue;
        this.divideSteps= divideSteps;


        if (steps > 0) {
            this.selectedStep = (int) Math.round((initialValue - minValue)/range * steps);
//            this.value = (double) Math.round(   Mth.clamp( ((double) this.selectedStep / steps * range) + minValue, minValue, maxValue) * 10    ) / 10;
            setValueInternal(((double) this.selectedStep / steps * range) + minValue);
        } else setValueInternal(initialValue);


        updateHandlePosition();
    }

    private ResourceLocation getSprite() {
        return this.isActive() && this.isFocused() && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
    }

    private ResourceLocation getHandleSprite() {
        return !this.isActive() || !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
    }

    protected @NotNull MutableComponent createNarrationMessage() {
        return Component.translatable("gui.narrate.slider", new Object[]{this.getMessage()});
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
            }
        }


    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();

        double stepWidth = (double) this.getWidth() / steps;
        if (divideSteps) for (int x = 0; x < steps; x++) {
            guiGraphics.blitSprite(RenderType::guiTextured, this.getSprite(), (int) (this.getX() + (stepWidth*x)) , this.getY(), (int) stepWidth, this.getHeight(), ARGB.white(this.alpha));
        }
        else guiGraphics.blitSprite(RenderType::guiTextured, this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));

        guiGraphics.blitSprite(RenderType::guiTextured, this.getHandleSprite(), this.getX() + (int)(handlePosition * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));
        int k = this.active ? 16777215 : 10526880;
        this.renderScrollingString(guiGraphics, minecraft.font, 2, k | Mth.ceil(this.alpha * 255.0F) << 24);

//        int yoff = 0;
//        guiGraphics.drawString(minecraft.font, "handle: " + handlePosition, getX(), getY() + getHeight() + (10 * yoff++), 0xFFFFFFFF);
//        guiGraphics.drawString(minecraft.font, "steps: " + steps, getX(), getY() + getHeight() + (10 * yoff++), 0xFFFFFFFF);
//        guiGraphics.drawString(minecraft.font, "value: " + ((handlePosition * range) + minValue), getX(), getY() + getHeight() + (10 * yoff++), 0xFFFFFFFF);
//        guiGraphics.drawString(minecraft.font, "step: " + this.selectedStep, getX(), getY() + getHeight() + (10 * yoff++), 0xFFFFFFFF);
//        guiGraphics.drawString(minecraft.font, "range: " + this.range, getX(), getY() + getHeight() + (10 * yoff++), 0xFFFFFFFF);
    }

    public void onClick(double d, double e) {
        this.setValueFromMouse(d);
    }

    public void updateHandlePosition() {
        if (steps > 0) {
            handlePosition = (float) this.selectedStep/steps;
        } else {
            handlePosition = (value - minValue) / range;
        }
    }

    public void setFocused(boolean bl) {
        super.setFocused(bl);
        if (!bl) {
            this.canChangeValue = false;
        } else {
            InputType inputType = Minecraft.getInstance().getLastInputType();
            if (inputType == InputType.MOUSE || inputType == InputType.KEYBOARD_TAB) {
                this.canChangeValue = true;
            }

        }
    }



    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (CommonInputs.selected(i)) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        } else {
            if (this.canChangeValue) {
                boolean leftKeyPressed = i == 263;
                if (leftKeyPressed || i == 262) {
                    int f = leftKeyPressed ? -1 : 1;
                    if (this.steps == 0) {
                      this.setValue(Math.clamp(this.value + f/range, minValue, maxValue));
                    } else this.setValueStep(this.selectedStep + f);
                    return true;
                }
            }

            return false;
        }
    }

    private void setValueFromMouse(double d) {
        handlePosition = Math.clamp((d - (double)(this.getX() + 4)) / (double)(this.width - 8), 0, 1);
        this.setValueInternal((handlePosition * range) + minValue);
    }

    public void setValueStep(int step) {
        setValueInternal(minValue + ((double) step / steps * range));

        updateHandlePosition();
    }

    private void setValueInternal(double d) {
        double currentValue = this.value;
        int targetStep = -1;
        double targetValue = d ;

        if (steps > 0) {
            targetStep = (int) Math.round((d - minValue)/range * steps);
            targetValue = Mth.clamp( ((double) targetStep / steps * range) + minValue, minValue, maxValue);
        }

        if (currentValue != targetValue) {
//            why is
            this.value = (double) Math.round(targetValue * 100) / 100;
            this.selectedStep = targetStep;
            this.applyValue();
        }

        this.updateMessage();
    }

    public void setValue(double d) {
        setValueInternal(d);
        updateHandlePosition();
    }





    @Override
    protected void onDrag(double d, double e, double f, double g) {
        this.setValueFromMouse(d);
        super.onDrag(d, e, f, g);
    }

    public void playDownSound(SoundManager soundManager) {
    }

    public void onRelease(double d, double e) {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
        if (divideSteps) updateHandlePosition();
    }


    protected abstract void updateMessage();
    protected abstract void applyValue();


}
