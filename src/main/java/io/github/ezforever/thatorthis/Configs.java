package io.github.ezforever.thatorthis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configs {
    public static class Rules {
        public static class Rule {
            public static class Option {
                public final String id;
                public final String caption;
                public final Set<String> directories;

                public Option(String id, String caption, Set<String> directories) {
                    this.id = id;
                    this.caption = caption;
                    this.directories = Collections.unmodifiableSet(directories);
                }
            }

            // NOTE: Rules and options are ordered, so cannot use Map<String, T>
            public final String id;
            public final String caption;
            public final String tooltip;
            public final List<Option> options;

            public Rule(String id, String caption, String tooltip, List<Option> options) {
                this.id = id;
                this.caption = caption;
                this.tooltip = tooltip;
                this.options = Collections.unmodifiableList(options);
            }
        }

        public final List<Rule> rules;

        public Rules(List<Rule> rules) {
            this.rules = Collections.unmodifiableList(rules);
        }
    }

    public static class Choices {
        public final Map<String, String> choices;

        public Choices(Map<String, String> choices) {
            this.choices = new HashMap<>(choices);
        }

        public Choices(Choices other) {
            this.choices = new HashMap<>(other.choices);
        }

        Choices() {
            this.choices = new HashMap<>();
        }
    }

    // ---

    private static final Logger LOGGER = LogManager.getFormatterLogger("thatorthis/config");
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static final Path rulesJson;
    private static final Path choicesJson;

    private static Configs instance;

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

    public static Configs getInstance() {
        if(instance == null)
            instance = new Configs();
        return instance;
    }

    // ---

    public final Rules rules;
    public final Choices defaultChoices;
    public Choices choices;

    public void save() {
        try {
            Writer writer = Files.newBufferedWriter(choicesJson);
            GSON.toJson(choices, writer);
            writer.close();
        } catch (IOException | JsonIOException e) {
            LOGGER.error("Unable to save choices.json", e);
            //throw new RuntimeException("Unable to save choices.json", e);
        }
    }

    private Configs() {
        try {
            Reader reader = Files.newBufferedReader(rulesJson);
            rules = GSON.fromJson(reader, Rules.class);
            reader.close();
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            throw new RuntimeException("Missing/invalid rules.json", e);
        }

        defaultChoices = new Choices();
        for(Rules.Rule rule : rules.rules) {
            if(!rule.options.isEmpty()) {
                defaultChoices.choices.put(
                        rule.id,
                        rule.options.get(0).id
                );
            }
        }

        if(Files.exists(choicesJson)) {
            try {
                choices = GSON.fromJson(Files.newBufferedReader(choicesJson), Choices.class);
            } catch (IOException | JsonIOException | JsonSyntaxException e) {
                throw new RuntimeException("Invalid choices.json", e);
            }
        } else {
            LOGGER.info("Missing choices.json; loading default choices");
            choices = new Choices(defaultChoices);
            save();
        }
    }
}
