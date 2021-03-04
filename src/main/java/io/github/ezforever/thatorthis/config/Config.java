package io.github.ezforever.thatorthis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.Choices;
import io.github.ezforever.thatorthis.config.rule.Rule;
import io.github.ezforever.thatorthis.config.rule.Rules;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Config {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/config");
    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Rule.class, new EnumClassTypeAdapter<>(Rule.Types.class))
            .registerTypeAdapter(Choice.class, new EnumClassTypeAdapter<>(Choice.Types.class))
            .create();

    private static final Path rulesJson;
    private static final Path choicesJson;

    private static Config instance;

    static {
        Path configsDir = FabricLoader.getInstance().getConfigDir().resolve("thatorthis");
        if (!Files.exists(configsDir)) {
            try {
                Files.createDirectory(configsDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create directory: " + configsDir, e);
            }
        }

        if (!Files.isDirectory(configsDir)) {
            throw new RuntimeException(configsDir + " is not a directory!");
        }

        rulesJson = configsDir.resolve("rules.json");
        choicesJson = configsDir.resolve("choices.json");
    }

    public static Config getInstance() {
        if(instance == null)
            instance = new Config();
        return instance;
    }

    // ---

    public final Rules rules;
    public final Choices defaultChoices;
    public Choices choices;

    public void save() {
        try(Writer writer = Files.newBufferedWriter(choicesJson)) {
            GSON.toJson(choices, writer);
        } catch (IOException | JsonIOException e) {
            LOGGER.error("Unable to save choices.json", e);
            //throw new RuntimeException("Unable to save choices.json", e);
        }
    }

    private Config() {
        if(!Files.exists(rulesJson)) {
            // NOTE: This is the only exception where ThatOrThis writes to rules.json
            // Any that's why Rules don't have a save() method
            LOGGER.info("Missing rules.json; loading default rules");

            try(InputStream is = Objects.requireNonNull(
                    getClass().getClassLoader()
                    .getResourceAsStream("assets/thatorthis/rules.default.json5"))) {
                Files.copy(is, rulesJson);
            } catch (NullPointerException | IOException e) {
                throw new RuntimeException("Could not load default rules", e);
            }
        }

        try(Reader reader = Files.newBufferedReader(rulesJson)) {
            rules = GSON.fromJson(reader, Rules.class);
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            throw new RuntimeException("Invalid rules.json", e);
        }

        Map<String, Choice> defaultChoicesMap = new HashMap<>();
        for(Rule rule : rules.rules)
            rule.getDefaultChoice().ifPresent((Choice choice) -> defaultChoicesMap.put(rule.id, choice));
        defaultChoices = new Choices(defaultChoicesMap);

        if(Files.exists(choicesJson)) {
            try(Reader reader = Files.newBufferedReader(choicesJson)) {
                choices = GSON.fromJson(reader, Choices.class);
            } catch (IOException | JsonIOException | JsonSyntaxException e) {
                throw new RuntimeException("Invalid choices.json", e);
            }
        } else {
            LOGGER.info("Missing choices.json; loading default choices");
            choices = new Choices(defaultChoices.choices);
            save();
        }
    }
}
