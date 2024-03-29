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
import java.util.*;

public class Config {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/config");
    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
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
    public Choices choices;

    public void save() {
        try(Writer writer = Files.newBufferedWriter(choicesJson)) {
            GSON.toJson(choices, writer);
        } catch (IOException | JsonIOException e) {
            LOGGER.error("Unable to save choices.json", e);
            //throw new RuntimeException("Unable to save choices.json", e);
        }
    }

    public Map<String, Set<String>> resolve() {
        Map<String, Set<String>> resultMap;
        if(rules.canDisable() && choices.disabled != null && choices.disabled) {
            LOGGER.debug("Mods under all rules are skipped as per user request");
            resultMap = Collections.emptyMap();
        } else {
            resultMap = rules.resolve(choices.choices);

            // We have no way to know if RuleHolder.resolve() has reset any choices
            // XXX: This removes any unrecognized choices from choices.json
            save();
        }

        return resultMap;
    }

    private Config() {
        if(!Files.exists(rulesJson)) {
            // NOTE: This is the only exception where ThatOrThis writes to rules.json
            // And that's why Rules don't have a save() method
            // XXX: Also create ".minecraft/mods/thatorthis" if not exist?
            LOGGER.info("Missing rules.json; loading default rules");

            try(InputStream is = Objects.requireNonNull(getClass().getClassLoader()
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

        if(Files.exists(choicesJson)) {
            try(Reader reader = Files.newBufferedReader(choicesJson)) {
                choices = GSON.fromJson(reader, Choices.class);
            } catch (IOException | JsonIOException | JsonSyntaxException e) {
                throw new RuntimeException("Invalid choices.json", e);
            }
        } else {
            LOGGER.info("Missing choices.json; loading default choices");
            choices = new Choices(rules.getDefaultChoices(), false);
            //save(); // Done later in resolve()
        }
    }
}
