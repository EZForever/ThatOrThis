package io.github.ezforever.thatorthis.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.rule.Rule;
import io.github.ezforever.thatorthis.config.rule.Rules;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;

public class SerializationTest {
    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .registerTypeAdapter(Rule.class, new EnumClassTypeAdapter<>(Rule.Types.class))
            .registerTypeAdapter(Choice.class, new EnumClassTypeAdapter<>(Choice.Types.class))
            .create();

    public static void main(String[] args) {
        try {
            Reader reader = Files.newBufferedReader(new File(".\\rules.future.example.json5").toPath());
            Rules rules = GSON.fromJson(reader, Rules.class);
            reader.close();
            Writer writer = Files.newBufferedWriter(new File(".\\run\\rules.future.example.reserialized.json5").toPath());
            GSON.toJson(rules, writer);
            writer.close();
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
