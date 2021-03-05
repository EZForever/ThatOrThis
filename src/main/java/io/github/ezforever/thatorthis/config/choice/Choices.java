package io.github.ezforever.thatorthis.config.choice;

import java.util.HashMap;
import java.util.Map;

// Root node of `choices.json`
public class Choices {
    public final Map<String, Choice> choices;

    public Choices(Map<String, Choice> choices) {
        this.choices = new HashMap<>(choices);
    }
}
