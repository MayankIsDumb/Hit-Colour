package com.example.hitcolour.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.ColorController;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class HCScreen {
    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(HConfig.CONFIG, (defaults, config, builder) -> builder
                .title(Component.translatable("hitcolour.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("hitcolour.title"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("hitcolour.title"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(Component.translatable("hitcolour.enable"))
                                        .description(OptionDescription.of(Component.translatable("hitcolour.enable.description")))
                                        .binding(defaults.enable, () -> config.enable, newVal -> config.enable = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("hitcolour.color"))
                                        .description(OptionDescription.of(Component.translatable("hitcolour.color.description")))
                                        .binding(defaults.color, () -> config.color, value -> config.color = value)
                                        .customController(opt -> new ColorController(opt, true))
                                        .build())
                                .option(Option.createBuilder(int.class)
                                        .name(Component.translatable("hitcolour.opacity"))
                                        .description(OptionDescription.of(Component.translatable("hitcolour.opacity.description")))
                                        .binding(defaults.opacity, () -> config.opacity, newVal -> config.opacity = newVal)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                                .range(0, 100)
                                                .step(5)
                                                .valueFormatter(val -> Component.literal(val + "%")))
                                        .build())
                                .build())
                        .build())
        ).generateScreen(parent);
    }
}