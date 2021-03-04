package io.github.ezforever.thatorthis.config.choice;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

// Choice for NestedRule
public class NestedRuleChoice extends Choice {
    @Expose // Choices of all nested rules
    public final Map<String, Choice> choices;

    public NestedRuleChoice(Map<String, Choice> choices) {
        this.choices = new HashMap<>(choices);
    }
}
