package io.github.ezforever.thatorthis;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.ezforever.thatorthis.config.Config;
import io.github.ezforever.thatorthis.config.choice.ChoiceHolder;
import io.github.ezforever.thatorthis.config.choice.Choices;
import io.github.ezforever.thatorthis.gui.Texts;
import io.github.ezforever.thatorthis.gui.future.ChoiceScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/gui");

    // ---

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) -> {
            Config config;
            try {
                config = Config.getInstance();
            } catch(RuntimeException e) {
                LOGGER.error("Choice screen disabled due to config errors", e);
                return new ConfirmScreen(
                        (boolean result) -> {
                            if(result) {
                                File logsDir = FabricLoader.getInstance()
                                        .getGameDir().resolve("logs")
                                        .toFile();
                                Util.getOperatingSystem().open(logsDir);
                            }
                            MinecraftClient.getInstance().openScreen(parent);
                        },
                        LiteralText.EMPTY, // TODO: Text
                        LiteralText.EMPTY // TODO: Text
                );
            }

            return new ChoiceScreen(parent, config.rules, config.choices.choices, (ChoiceHolder choices, Screen parentScreen) -> {
                config.choices = new Choices(choices);
                config.save();

                return new ConfirmScreen(
                        (boolean result) -> {
                            MinecraftClient minecraftClient = MinecraftClient.getInstance();
                            if(result)
                                minecraftClient.scheduleStop();
                            else
                                minecraftClient.openScreen(parentScreen);
                        },
                        Texts.CONFIRM_TITLE,
                        Texts.CONFIRM_MESSAGE
                );
            });
        };
    }
}
