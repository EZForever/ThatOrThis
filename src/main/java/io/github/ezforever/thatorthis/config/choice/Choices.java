package io.github.ezforever.thatorthis.config.choice;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

// Root node of `choices.json`
public class Choices {
    @Expose
    public final Map<String, Choice> choices;

    public Choices(Map<String, Choice> choices) {
        this.choices = new HashMap<>(choices);
    }
}
