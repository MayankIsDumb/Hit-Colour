package com.example.hitcolour.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.slider.ISliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.slider.SliderControllerElement;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

/**
 * Integer slider that works on Minecraft 26.3.
 *
 * <p>26.3 replaced GLFW with SDL3, which renumbered mouse buttons: left is now
 * {@code 1} ({@link InputConstants#MOUSE_BUTTON_LEFT}) instead of {@code 0}.
 * YACL 3.9.6's {@code SliderControllerElement} still hardcodes {@code button != 0},
 * so left-clicks never grab the thumb and the slider cannot be dragged
 * (tick-boxes and color fields are unaffected — they don't check the button).
 * This wrapper delegates everything to YACL's controller but serves a slider
 * element that translates SDL left-clicks back to what YACL expects.</p>
 */
public final class SdlSafeIntegerSlider implements ISliderController<Integer> {
    private final IntegerSliderController delegate;

    public SdlSafeIntegerSlider(Option<Integer> option, int min, int max, int interval, Function<Integer, Component> formatter) {
        this.delegate = new IntegerSliderController(option, min, max, interval, formatter);
    }

    @Override
    public Option<Integer> option() {
        return delegate.option();
    }

    @Override
    public Component formatValue() {
        return delegate.formatValue();
    }

    @Override
    public double min() {
        return delegate.min();
    }

    @Override
    public double max() {
        return delegate.max();
    }

    @Override
    public double interval() {
        return delegate.interval();
    }

    @Override
    public void setPendingValue(double value) {
        delegate.setPendingValue(value);
    }

    @Override
    public double pendingValue() {
        return delegate.pendingValue();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new Element(this, screen, widgetDimension, min(), max(), interval());
    }

    /** Slider element identical to YACL's, except left-clicks are recognised on 26.3's SDL backend. */
    public static final class Element extends SliderControllerElement {
        public Element(ISliderController<?> option, YACLScreen screen, Dimension<Integer> dim, double min, double max, double interval) {
            super(option, screen, dim, min, max, interval);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return super.mouseClicked(withLegacyLeftButton(event), doubleClick);
        }

        @Override
        public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
            return super.mouseDragged(withLegacyLeftButton(event), dx, dy);
        }

        @Override
        public boolean mouseReleased(MouseButtonEvent event) {
            return super.mouseReleased(withLegacyLeftButton(event));
        }

        private static MouseButtonEvent withLegacyLeftButton(MouseButtonEvent event) {
            if (event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
                return new MouseButtonEvent(event.x(), event.y(), new MouseButtonInfo(0, event.modifiers()));
            }
            return event;
        }
    }
}
